import nu.studer.gradle.jooq.JooqGenerate
import org.jooq.meta.jaxb.ForcedType
import org.jooq.meta.jaxb.Logging
import org.jooq.meta.jaxb.Property

plugins {
    java
    application

    id("nu.studer.jooq") version "9.0"
//    id("org.jooq.jooq-codegen-gradle") version "3.19.8"
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
    implementation(libs.hikaricp)

    // mapstruct
    implementation(libs.mapstruct)
    annotationProcessor(libs.mapstruct.processor)

    implementation(libs.jspecify)

    // dagger
    implementation(libs.dagger)
    annotationProcessor(libs.dagger.compiler)

    runtimeOnly(libs.logback.classic)
    runtimeOnly(libs.mysql.connector.j)

    // jOOQ
    implementation(libs.jooq)
    implementation(libs.jooq.meta)
    implementation(libs.jooq.codegen)
    implementation(libs.jakarta.xml.bind.api)
//    jooqCodegen(libs.jooq.meta.extensions)
//    jooqCodegen(libs.mysql.connector.j)
    jooqGenerator(libs.jooq.meta.extensions)
    jooqGenerator(libs.mysql.connector.j)
    jooqGenerator(project(":lib"))  // for custom GeneratorStrategy

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
    url = "jdbc:mysql://localhost:3307/realworld"
    user = "root"
    password = "root"

    // TODO: remove
    cleanDisabled = false
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

//jooq {
//    version = libs.versions.jooq.get()
//
//    configuration {}
//
//    executions {
//        create("main") {
//            configuration {
//                logging = Logging.DEBUG
//                jdbc = null  // ?
//
//                generator {
//                    name = "org.jooq.codegen.JavaGenerator"
//
//                    database {
//                        name = "org.jooq.meta.extensions.ddl.DDLDatabase"
//
//                        properties {
//                            property {
//                                key = "scripts"
//                                value = "src/main/resources/db/migration/*.sql"
//                            }
//                            property {
//                                key = "sort"
//                                value = "flyway"
//                            }
//                            property {
//                                key = "unqualifiedSchema"
//                                value = "none"
//                            }
//                            property {
//                                key = "defaultNameCase"
//                                value = "upper"
//                            }
//                        }
//
//                        forcedTypes {
//                            forcedType {
//                                name = "BOOLEAN"
//                                includeExpression = ".*\\.IS_VALID"
//                            }
//                            forcedType {
//                                userType = "de.huxhorn.sulky.ulid.ULID.Value"
//                                binding = "io.realworld.common.jooq.MysqlUlidBinding"
//                                includeExpression = ".*\\.ULID"
//                                includeTypes = "(?i:BINARY)"
//                            }
//                        }
//                    }
//
//                    generate {
//                        isDeprecated = false
//                        isRecords = true
//                        isImmutablePojos = true
//                        isFluentSetters = true
//                    }
//
//                    target {
//                        packageName = "io.realworld"
//                        directory = "${projectDir}/build/generated-src/jooq/main"
//                    }
//
//                    strategy {
//                        name = "org.jooq.codegen.DefaultGeneratorStrategy"
//                    }
//                }
//            }
//        }
//    }
//}

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
//                jdbc.apply {
//                    driver = "com.mysql.cj.jdbc.Driver"
//                    url = "jdbc:mysql://localhost:3306/realworld"
//                    user = "root"
//                    password = "root"
//                    properties.add(Property().apply {
//                        key = "ssl"
//                        value = "false"
//                    })
//                }
                generator.apply {
                    name = "org.jooq.codegen.JavaGenerator"
                    database.apply {
//                        name = "org.jooq.meta.mysql.MySQLDatabase"
//                        inputSchema = "realworld"
                        name = "org.jooq.meta.extensions.ddl.DDLDatabase"
                        properties.addAll(listOf(
                            Property().apply {
                                key = "scripts"
                                value = "src/main/resources/db/migration/*.sql"
                            },
                            Property().apply {
                                key = "sort"
                                value = "flyway"
                            },
                            Property().apply {
                                key = "unqualifiedSchema"
                                value = "none"
                            },
                            Property().apply {
                                key = "defaultNameCase"
                                value = "upper"
                            }
                        ))
                        forcedTypes.addAll(listOf(
                            // (MYSQL) BINARY(16)(=byte[]) <-> ULID.Value (jOOQ)
                            ForcedType().apply {
                                userType = "de.huxhorn.sulky.ulid.ULID.Value"
                                binding = "io.realworld.common.jooq.MysqlUlidBinding"
                                includeExpression = ".*\\.ID"
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
                        packageName = "io.realworld.jooq"
                        directory = "build/generated-src/jooq/main"
                    }

                    // strategy.name = "org.jooq.codegen.DefaultGeneratorStrategy"
                    strategy.name = "io.realworld.common.jooq.DepluralizedAndPojoNamingGeneratorStrategy"
                }
            }
        }
    }
}

tasks.named<JooqGenerate>("generateJooq") {
//    dependsOn("flywayMigrate")

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
