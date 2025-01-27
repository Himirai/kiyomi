import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import xyz.jpenilla.resourcefactory.bukkit.BukkitPluginYaml

plugins {
	kotlin("jvm") version "1.9.21"
	id("maven-publish")
	id("com.github.johnrengelman.shadow") version "8.1.1"
	id("io.papermc.paperweight.userdev").version("1.5.11")
	id("io.github.patrick.remapper") version "1.4.1"
	id("xyz.jpenilla.run-paper") version "2.2.3"
	id("xyz.jpenilla.resource-factory-bukkit-convention") version "1.1.1"
}

val mainClassName = "kiyomi"
group = "dev.himirai.${mainClassName.lowercase()}"
version = "1.0.0"
val internal = "$group.internal"

repositories {
	mavenCentral()
	maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
	paperweight.paperDevBundle("1.20.4-R0.1-SNAPSHOT")
	implementation("org.jetbrains.kotlin:kotlin-reflect:1.9.21")
}

java {
	sourceCompatibility = JavaVersion.VERSION_17
	targetCompatibility = JavaVersion.VERSION_17
	toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}

bukkitPluginYaml {
	main = "$group.${mainClassName.replaceFirstChar { it.uppercaseChar() }}"
	apiVersion = "1.20"
	authors.add("Himirai")
	version = project.version.toString()
	load = BukkitPluginYaml.PluginLoadOrder.POSTWORLD
}

val sourcesJar by tasks.creating(Jar::class) {
	from(sourceSets.main.get().allSource)
	archiveFileName.set("kiyomi-${project.version}.jar")
}

tasks {
	runServer {
		version("1.20.4")
	}

	shadowJar {
		val relocations = listOf("org.intellij", "org.jetbrains", "kotlin")
		relocations.forEach { relocate(it, "$internal.$it") }
		archiveClassifier.set("")
		archiveFileName.set("kiyomi-unmapped.jar")
	}

	jar {
		enabled = false
	}

	build {
		dependsOn(shadowJar)
		dependsOn(sourcesJar)
	}

	reobfJar {
		dependsOn(shadowJar)
		inputJar.set(shadowJar.get().archiveFile)
		outputJar.set(layout.buildDirectory.file("libs/kiyomi-remapped.jar"))
	}

	assemble {
		dependsOn(reobfJar)
	}

	withType<KotlinCompile> {
		kotlinOptions.jvmTarget = "17"
	}
}

publishing {
	publications {
		create<MavenPublication>("maven") {
			from(components["java"])
			artifact(sourcesJar)
		}
	}
}
