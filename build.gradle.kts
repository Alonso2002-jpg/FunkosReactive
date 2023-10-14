plugins {
    id("java")
}

group = "org.develop"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
     // Project Reactor
    implementation("io.projectreactor:reactor-core:3.5.10")
    // Para test: https://www.baeldung.com/reactive-streams-step-verifier-test-publisher
    // NO lo vamos a usar, pero lo dejo por si acaso
    // testImplementation("io.projectreactor:reactor-test:3.5.10")

    // R2DBC
    implementation("io.r2dbc:r2dbc-h2:1.0.0.RELEASE")
    implementation("io.r2dbc:r2dbc-pool:1.0.0.RELEASE")

    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")

    testImplementation("org.mockito:mockito-junit-jupiter:5.5.0")
    testImplementation("org.mockito:mockito-core:5.5.0")

    implementation("org.projectlombok:lombok:1.18.28")
    testImplementation("org.projectlombok:lombok:1.18.28")
    annotationProcessor("org.projectlombok:lombok:1.18.28")

    implementation("ch.qos.logback:logback-classic:1.4.11")

}

tasks.test {
    useJUnitPlatform()
}