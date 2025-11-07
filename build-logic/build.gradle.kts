plugins { `kotlin-dsl` }

java {
    toolchain { languageVersion.set(JavaLanguageVersion.of(25)) }
}

dependencies {
    // Avoid leaking plugins to consumers
    compileOnly(libs.plugins.android.application)
    compileOnly(libs.plugins.kotlin.android)
    compileOnly(libs.plugins.dagger.hilt)
    compileOnly(libs.plugins.devtools.ksp)
    compileOnly(libs.plugins.google.services)
    compileOnly(libs.plugins.compose.compiler) // for type references
}

gradlePlugin {
    plugins {
        create("genesisApplication") {
            id = "genesis.application"
            implementationClass = "plugins.GenesisApplicationPlugin"
        }
        create("genesisLibrary") {
            id = "genesis.library"
            implementationClass = "plugins.GenesisLibraryPlugin"
        }
        create("genesisBase") {
            id = "genesis.base"
            implementationClass = "plugins.GenesisBasePlugin"
        }
    }
}
