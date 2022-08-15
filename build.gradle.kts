plugins {
    `java-library`
    id("net.minecrell.plugin-yml.bukkit") version "0.5.2" // Generates plugin.yml
    id("xyz.jpenilla.run-paper") version "1.0.6" // Adds runServer and runMojangMappedServer tasks for testing
}

group = "xyz.holocons.mc"
version = "1.0-SNAPSHOT"

repositories {
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://maven.enginehub.org/repo/")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.19.1-R0.1-SNAPSHOT")
    compileOnly("com.sk89q.worldguard:worldguard-bukkit:7.0.8-SNAPSHOT")
}

tasks {
    compileJava {
        options.encoding = Charsets.UTF_8.name() // We want UTF-8 for everything

        // Set the release flag. This configures what version bytecode the compiler will emit, as well as what JDK APIs are usable.
        // See https://openjdk.java.net/jeps/247 for more information.
        options.release.set(17)
    }
    javadoc {
        options.encoding = Charsets.UTF_8.name() // We want UTF-8 for everything
    }
    processResources {
        filteringCharset = Charsets.UTF_8.name() // We want UTF-8 for everything
    }

    // Configure the Minecraft version for runServer task
    // https://github.com/jpenilla/run-paper
    runServer {
        minecraftVersion("1.19.1")
    }
}

// Configure plugin.yml generation
// https://github.com/Minecrell/plugin-yml
bukkit {
    main = "xyz.holocons.mc.resourceguard.ResourceGuard"
    version = project.version.toString()
    apiVersion = "1.19"
    authors = listOf("TraceL")
    website = "holocons.xyz"
    depend = listOf("WorldGuard")
    prefix = "ResourceGuard"

    commands {
        register("res-guard") {
            usage = "/res-guard <info/reset>"
        }
    }
}