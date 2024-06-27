plugins {
    java
    kotlin("jvm") version "1.6.10"
}

allprojects {
    repositories {
        mavenCentral()
    }
}

tasks.register("runFolioApp") {
    dependsOn(":folio-app:bootRun")
}

subprojects {
    apply(plugin = "java")
    apply(plugin = "kotlin")

    dependencies {
        implementation(kotlin("stdlib"))
    }
}