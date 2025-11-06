# Genesis-OS Feature Module Consumer ProGuard Rules
# Generated consumer rules for featuremodule

# Keep all public classes and methods for Genesis AI framework
-keep public class dev.aurakai.auraframefx.feature.** { *; }

# Standard Android consumer rules
-dontwarn java.lang.invoke.StringConcatFactory
