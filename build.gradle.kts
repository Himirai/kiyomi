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

val mainClassName = "Kiyomi"
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
	main = "$group.$mainClassName"
	apiVersion = "1.20"
	authors.add("Himirai")
	version = project.version.toString()
	load = BukkitPluginYaml.PluginLoadOrder.POSTWORLD
}

tasks {
	runServer {
		version("1.20.4")
	}

	shadowJar {
		val relocations = listOf("org.intellij", "org.jetbrains", "kotlin")
		relocations.forEach { relocate(it, "$internal.$it") }
		archiveClassifier.set("")
		archiveFileName.set("$mainClassName.jar")
	}

	jar {
		enabled = false
	}

	val sourcesJar by creating(Jar::class) {
		from(sourceSets.main.get().allSource)
		archiveClassifier.set("sources")
		archiveFileName.set("$mainClassName-sources.jar")
	}

	build {
		dependsOn(reobfJar)
	}

	reobfJar {
		dependsOn(shadowJar)
		dependsOn(sourcesJar)
		inputJar.set(shadowJar.get().archiveFile)
		outputJar.set(layout.buildDirectory.file("libs/${project.name}-remapped.jar"))
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
			artifact(tasks.named("sourcesJar")) {
				classifier = "sources"
			}
			artifact(tasks.reobfJar) {
				classifier = "sources"
			}
			groupId = project.group.toString()
			artifactId = project.name
			version = project.version.toString()
		}
	}
	repositories {
		maven {
			url = uri("https://jitpack.io")
		}
	}
}
