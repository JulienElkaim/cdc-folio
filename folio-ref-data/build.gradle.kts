plugins {
    id("buildlogic.java-conventions")
}

dependencies {
    api(project(":folio-commons"))
    compileOnly(libs.jakarta.persistence.jakarta.persistence.api)
}

description = "folio-ref-data"
