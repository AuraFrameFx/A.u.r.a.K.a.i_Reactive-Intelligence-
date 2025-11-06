plugins {
    `kotlin-dsl`
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(25))
    }
}

dependencies {
    // Plugin dependencies for convention plugins
    // These allow the convention plugins to apply Android, Kotlin, Hilt, and KSP plugins
    implementation(libs.gradle.plugin)
    implementation(libs.kotlin.gradle.plugin)
    implementation(libs.hilt.gradle.plugin)
    implementation(libs.ksp.gradle.plugin)
}

gradlePlugin {
    plugins {
        register("genesisApplication") {
            id = "genesis.application"
            implementationClass = "plugins.GenesisApplicationPlugin"
        }
        register("genesisLibrary") {
            id = "genesis.library"
            implementationClass = "plugins.GenesisLibraryPlugin"
        }
        register("genesisBase") {
            id = "genesis.base"
            implementationClass = "plugins.GenesisBasePlugin"
        }
    }
}
