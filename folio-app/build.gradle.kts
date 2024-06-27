plugins {
    id("buildlogic.java-conventions")
}

dependencies {
    api(project(":folio-core"))
    runtimeOnly(libs.com.h2database.h2)
    implementation(libs.org.springframework.boot.spring.boot.starter)
    implementation(libs.org.springframework.boot.spring.boot.starter.data.jpa)
}

description = "folio-app"
