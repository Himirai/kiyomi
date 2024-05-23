import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import xyz.jpenilla.resourcefactory.bukkit.BukkitPluginYaml

plugins {
	kotlin("jvm") version "1.9.21"
	id("com.github.johnrengelman.shadow") version "8.1.1"
	id("io.papermc.paperweight.userdev").version("1.5.11")
	id("io.github.patrick.remapper") version "1.4.1"
	id("xyz.jpenilla.run-paper") version "2.2.3"
	id("xyz.jpenilla.resource-factory-bukkit-convention") version "1.1.1"
}

// TODO: change this to your needs
val mainClassName = "SamplePlugin"
group = "dev.himirai.${mainClassName.lowercase()}"
version = "1.0.0"
val internal = "$group.internal"

repositories {
	mavenCentral()
	maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
	paperweight.paperDevBundle("1.20.4-R0.1-SNAPSHOT")
}

java {
	toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}

bukkitPluginYaml {
	main = "$group.$mainClassName"
	apiVersion = "1.20"
	authors.add("Himirai")
	version = project.version.toString()
	load = BukkitPluginYaml.PluginLoadOrder.POSTWORLD
//    depend.add("WorldEdit")
//    softDepend.add("Vault")
}

tasks {
	runServer {
		version("1.20.4")
	}

	shadowJar {
		val relocations = listOf("org.intellij", "org.jetbrains", "kotlin")
		relocations.forEach { relocate(it, "$internal.$it") }
		archiveClassifier.set("")
		archiveFileName.set("v${project.version}/$mainClassName.jar")
	}

	build {
		dependsOn(shadowJar)
	}

	reobfJar {
		outputJar.set(layout.buildDirectory.file("libs/v${project.version}/${project.name}-remapped.jar"))
		doFirst {
			val versionDir = file("${layout.buildDirectory}/libs/v${project.version}")
			if (!versionDir.exists()) versionDir.mkdirs()
		}
	}

	assemble {
		dependsOn(reobfJar)
	}

	withType<KotlinCompile> {
		kotlinOptions.jvmTarget = "17"
	}
}
