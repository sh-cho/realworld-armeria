plugins {
    java
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.jooq.meta)
    implementation(libs.jooq.codegen)
}
