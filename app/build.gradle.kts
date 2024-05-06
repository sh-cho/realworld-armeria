import org.jooq.meta.jaxb.ForcedType
import org.jooq.meta.jaxb.Logging
import org.jooq.meta.jaxb.Property

plugins {
    java
    application

    id("nu.studer.jooq") version "9.0"
    id("org.flywaydb.flyway") version "10.12.0"
}

repositories {
    mavenCentral()
}

buildscript {
    dependencies {
        classpath("org.flywaydb:flyway-mysql:10.12.0")
    }
}

val flywayMigration = configurations.create("flywayMigration")

dependencies {
    implementation(platform(libs.armeria.bom))
    implementation("com.linecorp.armeria:armeria")
    implementation("com.linecorp.armeria:armeria-logback")
    implementation(libs.guava)
    implementation(libs.sulky.ulid)
    implementation(libs.jetbrains.annotations)

    runtimeOnly(libs.logback.classic)
    runtimeOnly(libs.mysql.connector.j)

    // jOOQ
    implementation(libs.jooq)
    implementation(libs.jooq.meta)
    implementation(libs.jooq.codegen)
    jooqGenerator(libs.mysql.connector.j)

    // flyway
    flywayMigration(libs.mysql.connector.j)

    // Use JUnit Jupiter for testing.
    testImplementation(libs.junit.jupiter)
    testImplementation("com.linecorp.armeria:armeria-junit5")
    testImplementation(libs.assertj.core)

    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

flyway {
    configurations = arrayOf("flywayMigration")
    url = "jdbc:mysql://localhost:3306/realworld"
    user = "root"
    password = "root"

    // TODO: remove
    cleanDisabled = false
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

// TODO: separation pojo and table names
//  (like POJO: Article, Table: TArticle or something)
//  Maybe `T-` prefix could be misunderstood with Thrift?
jooq {
    version.set(libs.versions.jooq.get())
    edition.set(nu.studer.gradle.jooq.JooqEdition.OSS)

    configurations {
        create("main") {
            generateSchemaSourceOnCompilation.set(true)

            jooqConfiguration.apply {
                logging = Logging.WARN
                jdbc.apply {
                    driver = "com.mysql.cj.jdbc.Driver"
                    url = "jdbc:mysql://localhost:3306/realworld"
                    user = "root"
                    password = "root"
                    properties.add(Property().apply {
                        key = "ssl"
                        value = "false"
                    })
                }
                generator.apply {
                    name = "org.jooq.codegen.JavaGenerator"
                    database.apply {
                        name = "org.jooq.meta.mysql.MySQLDatabase"
                        inputSchema = "realworld"
                        forcedTypes.addAll(listOf(
                            ForcedType().apply {
                                name = "varchar"
                                includeExpression = ".*"
                                includeTypes = "JSONB?"
                            },
                            ForcedType().apply {
                                name = "varchar"
                                includeExpression = ".*"
                                includeTypes = "INET"
                            },

                            ForcedType().apply {
//                                name = org.jooq.impl.SQLDataType.BOOLEAN.name
                                name = "BOOLEAN"
                                includeExpression = ".*\\.IS_VALID"
                            },

                            // (MYSQL) BINARY(16)(=byte[]) <-> ULID.Value (jOOQ)
                            ForcedType().apply {
                                userType = "de.huxhorn.sulky.ulid.ULID.Value"
                                binding = "io.realworld.jooq.MysqlUlidBinding"
                                includeExpression = ".*\\.ULID"
                                includeTypes = "(?i:BINARY)"
                            }
                        ))
                    }
                    generate.apply {
                        isDeprecated = false
                        isRecords = true
                        isImmutablePojos = true
                        isFluentSetters = true
                    }
                    target.apply {
                        packageName = "io.realworld"
                        directory = "build/generated-src/jooq/main"
                    }
                    strategy.name = "org.jooq.codegen.DefaultGeneratorStrategy"
                }
            }
        }
    }
}

tasks.named<nu.studer.gradle.jooq.JooqGenerate>("generateJooq") {
    dependsOn("flywayMigrate")

    // declare Flyway migration scripts as inputs on the jOOQ task
    inputs.files(fileTree("src/main/resources/db/migration"))
        .withPropertyName("migrations")
        .withPathSensitivity(PathSensitivity.RELATIVE)

    // make jOOQ task participate in incremental builds (which is also a prerequisite for build caching)
    allInputsDeclared.set(true)
}

application {
    mainClass = "io.realworld.RealworldApplication"
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}
