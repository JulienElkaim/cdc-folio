plugins {
    id("buildlogic.java-conventions")
}

dependencies {
    api(project(":folio-commons"))
    api(project(":folio-ref-data"))
    api(project(":folio-market-data"))
    api(libs.org.apache.commons.commons.math3)
}

description = "folio-price-engine"
