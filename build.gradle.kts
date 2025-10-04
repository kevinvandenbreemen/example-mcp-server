plugins {
    kotlin("jvm") version "2.2.20"
    application
}

group = "com.vandenbreemen"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {

    //  MCP stuff
    val mcpVersion = "0.7.2" // check the repo badge for the latest
    implementation("io.modelcontextprotocol:kotlin-sdk:$mcpVersion")
    // Pick a Ktor engine if you’ll use HTTP/SSE transports (optional for stdio)
    implementation("io.ktor:ktor-server-netty:2.3.12")

    // SLF4J logging
    implementation("org.slf4j:slf4j-api:2.0.9")
    implementation("ch.qos.logback:logback-classic:1.4.11")

    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}

application {
    mainClass.set("com.vandenbreemen.MainKt")
}