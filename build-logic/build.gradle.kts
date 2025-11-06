plugins {
    `kotlin-dsl`
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(25))
    }
}

dependencies {
    // Avoid leaking plugins to consumers
    compileOnly(libs.android.application)
    compileOnly(libs.kotlin.android)
    compileOnly(libs.hilt)
    compileOnly(libs.ksp)
    compileOnly(libs.google.services)
    compileOnly(libs.plugins.compose.compiler) // for type references
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
