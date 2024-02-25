plugins {
    id("java")
    application

}

group = "dev.exhq"
version = "1.0-SNAPSHOT"
application {
    mainClass = "dev.exhq.Main"
}
repositories {
    mavenCentral()
}

dependencies {
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("org.slf4j:slf4j-simple:2.0.10")
    implementation("io.javalin:javalin:6.1.0")
    implementation("org.xerial:sqlite-jdbc:3.30.1")
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}
