plugins {
    id("buildlogic.java-conventions")
}

dependencies {
    api(project(":folio-market-data"))
    api(project(":folio-commons"))
    api(project(":folio-ref-data"))
    api(project(":folio-price-engine"))
}

description = "folio-core"
