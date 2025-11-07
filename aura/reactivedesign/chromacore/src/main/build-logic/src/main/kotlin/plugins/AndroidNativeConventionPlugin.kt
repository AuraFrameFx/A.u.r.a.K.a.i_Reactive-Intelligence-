//package plugins
//
//import com.android.build.api.dsl.CommonExtension
//import org.gradle.api.Plugin
//import org.gradle.api.Project
//import org.gradle.kotlin.dsl.configure
//
///**
// * Android Native Convention Plugin
// * Configures native C/C++ support for Android modules
// */
//class AndroidNativeConventionPlugin : Plugin<Project> {
//    override fun apply(target: Project) {
//        with(target) {
//            extensions.configure<CommonExtension<*, *, *, *, *, *>> {
//                defaultConfig {
//                    externalNativeBuild {
//                        cmake {
//                            cppFlags.add("-std=c++17")
//                        }
//                    }
//                }
//
//                externalNativeBuild {
//                    cmake {
//                        path = file("src/main/cpp/CMakeLists.txt")
//                        version = "3.22.1"
//                    }
//                }
//            }
//        }
//    }
//}
