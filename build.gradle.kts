@file:Suppress("PropertyName")

import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

plugins {
    java
    kotlin("jvm") version "2.1.+"
    eclipse
    idea
    `maven-publish`
    id("net.minecraftforge.gradle") version "[6.0,6.2)"
    id("org.parchmentmc.librarian.forgegradle") version "1.+"
    id("org.spongepowered.mixin") version "0.7.+"
}

object ModConfig {
    const val MOD_ID = "examplemod"
    const val MOD_NAME = "Example Mod"
    const val MOD_LICENSE = "MIT"
    const val MOD_VERSION = "0.1.0"
    const val MOD_GROUP_ID = "dev.toapuro.examplemod"
    const val MOD_AUTHORS = "toapuro"
    const val MOD_DESCRIPTION = ""
    const val MOD_DISPLAY_URL = ""
    const val MOD_CREDITS = ""
}

version = "v${ModConfig.MOD_VERSION}"
group = ModConfig.MOD_GROUP_ID

base {
    archivesName.set(provider { "${ModConfig.MOD_ID}-${libs.versions.minecraft.get()}-forge" })
}

java.toolchain.languageVersion.set(JavaLanguageVersion.of(17))

println("Java: ${System.getProperty("java.version")}, JVM: ${System.getProperty("java.vm.version")} (${System.getProperty("java.vendor")}), Arch: ${System.getProperty("os.arch")}")

minecraft {
    mappings("parchment", libs.versions.parchment)
    copyIdeResources.set(true)
//    accessTransformer(file("src/main/resources/META-INF/accesstransformer.cfg"))

    runs.configureEach {
        workingDirectory(project.file("run"))
        property("forge.logging.markers", "REGISTRIES")
        property("forge.logging.console.level", "debug")

        mods.create(ModConfig.MOD_ID) {
            source(sourceSets.main.get())
        }

        property("mixin.env.remapRefMap", "true")
        property("mixin.env.refMapRemappingFile", "${project.projectDir}/build/createSrgToMcp/output.srg")
    }

    runs {
        create("client") {
            property("forge.enabledGameTestNamespaces", ModConfig.MOD_ID)
            jvmArgs("-XX:+AllowEnhancedClassRedefinition")
        }

        create("server") {
            workingDirectory(project.file("run-server"))
            property("forge.enabledGameTestNamespaces", ModConfig.MOD_ID)
            args("--nogui")
            jvmArgs("-XX:+AllowEnhancedClassRedefinition")
        }

        create("gameTestServer") {
            workingDirectory(project.file("run-server"))
            property("forge.enabledGameTestNamespaces", ModConfig.MOD_ID)
        }

        create("data") {
            workingDirectory(project.file("run-data"))
            args("--mod", ModConfig.MOD_ID, "--all", "--output", file("src/generated/resources/"), "--existing", file("src/main/resources/"))
        }
    }
}

sourceSets {
    test {
    }
}

sourceSets.main.get().resources {
    srcDir("src/generated/resources")
}

repositories {
//    flatDir {
//        dir("libs")
//    }

//    maven {
//        name = "ModMaven"
//        url = uri("https://modmaven.dev/")
//    }

//    exclusiveContent {
//        forRepository {
//            maven {
//                name = "Modrinth"
//                url = uri("https://api.modrinth.com/maven")
//            }
//        }
//        forRepositories(fg.repository)
//        filter {
//            includeGroup("maven.modrinth")
//        }
//    }

    maven {
        name = "Kotlin for Forge"
        url = uri("https://thedarkcolour.github.io/KotlinForForge/")
        content { includeGroup("thedarkcolour") }
    }

    exclusiveContent {
        forRepository {
            maven {
                url = uri("https://cursemaven.com")
            }
        }
        forRepositories(fg.repository)
        filter {
            includeGroup("curse.maven")
        }
    }
}

dependencies {
    @Suppress("VulnerableLibrariesLocal", "RedundantSuppression")
    minecraft(libs.forge)
    annotationProcessor("org.spongepowered:mixin:0.8.5:processor")
    testImplementation(libs.junit)

//    // Mixin Extras
//    compileOnly(annotationProcessor(libs.mixinExtrasCommon.get())!!)
//    implementation(jarJar(libs.mixinExtrasForge.get())) {
//        jarJar.ranged(this, libs.versions.mixinExtrasRange)
//    }

    // Default Dependencies
    runtimeOnly(fg.deobf(deps.catalogue))
    runtimeOnly(fg.deobf(deps.configured))
    runtimeOnly(fg.deobf(deps.jade))
    runtimeOnly(fg.deobf(deps.jei))
    runtimeOnly(fg.deobf(deps.jeiIntegration))

    // Mod Dependencies
    implementation(deps.kotlinforforge)
}

tasks.test {
    useJUnitPlatform()
}

mixin {
    add(sourceSets.main.get(), "${ModConfig.MOD_ID}.refmap.json")
    config("${ModConfig.MOD_ID}.mixins.json")
}

tasks.named<ProcessResources>("processResources") {
    val replaceProperties = mapOf(
        "minecraft_version" to libs.versions.minecraft.get(),
        "minecraft_version_range" to libs.versions.minecraftRange.get(),
        "forge_version" to libs.versions.forge.get(),
        "forge_version_range" to libs.versions.forgeRange.get(),
        "loader_version_range" to libs.versions.loaderRange.get(),
        "kff_version_range" to deps.versions.kffRange.get(),

        "mod_id" to ModConfig.MOD_ID,
        "mod_name" to ModConfig.MOD_NAME,
        "mod_license" to ModConfig.MOD_LICENSE,
        "mod_version" to ModConfig.MOD_VERSION,
        "mod_authors" to ModConfig.MOD_AUTHORS,
        "mod_description" to ModConfig.MOD_DESCRIPTION,
        "mod_display_url" to ModConfig.MOD_DISPLAY_URL,
        "mod_credits" to ModConfig.MOD_CREDITS
    )

    inputs.properties(replaceProperties)

    filesMatching(listOf("META-INF/mods.toml", "pack.mcmeta")) {
        expand(replaceProperties + ("project" to project))
    }
}

tasks.named<Jar>("jar") {
    manifest.attributes(
        "Specification-Title" to ModConfig.MOD_ID,
        "Specification-Vendor" to ModConfig.MOD_AUTHORS,
        "Specification-Version" to "1",
        "Implementation-Title" to project.name,
        "Implementation-Version" to archiveVersion,
        "Implementation-Vendor" to ModConfig.MOD_AUTHORS,
        "Implementation-Timestamp" to ZonedDateTime.now()
            .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssZ"))
    )
    finalizedBy("reobfJar")
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks.withType<JavaExec> {
    standardInput = System.`in`
}

tasks.register("setupServer") {
    group = "custom"
    description = "Sets up the server run directory with eula.txt and server.properties"

    doLast {
        project.run {
            file("run-server").mkdirs()
            file("run-server/eula.txt").writeText("eula=true")
            file("run-server/server.properties").writeText("""
                allow-flight=true
                enable-command-block=true
                gamemode=creative
                online-mode=false
                """.trimIndent()
            )
        }
    }
}
