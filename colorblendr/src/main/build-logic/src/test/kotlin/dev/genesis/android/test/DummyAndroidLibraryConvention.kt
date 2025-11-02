package dev.genesis.android.test

import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * Dummy replacement for "genesis.android.library" in tests.
 * Registers a fake 'android' LibraryExtension so the compose convention can configure it.
 */
class DummyAndroidLibraryConvention : Plugin<Project> {
    /**
     * Applies the dummy Android library convention to the given project.
     *
     * If the project does not already contain an extension of type [com.android.build.api.dsl.LibraryExtension],
     * this registers a new extension named "android" backed by [FakeLibraryExtension]. If such an extension
     * is already present, the method is a no-op.
     *
     * @param target The Gradle project to which the extension may be added.

     */
    override fun apply(target: Project) {
        if (target.extensions.findByType(LibraryExtension::class.java) == null) {
            target.extensions.add(LibraryExtension::class.java, "android", FakeLibraryExtension())
        }
    }
}