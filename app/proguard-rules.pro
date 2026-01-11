# Keep `Companion` object fields of serializable classes.
# This avoids serializer lookup through `getDeclaredClasses` as done for named companion objects.
-if @kotlinx.serialization.Serializable class **
-keepclassmembers class <1> {
    static <1>$Companion Companion;
}

# Keep `serializer()` on companion objects (both default and named) of serializable classes.
-if @kotlinx.serialization.Serializable class ** {
    static **$* *;
}
-keepclassmembers class <2>$<3> {
    kotlinx.serialization.KSerializer serializer(...);
}

# Keep `INSTANCE.serializer()` of serializable objects.
-if @kotlinx.serialization.Serializable class ** {
    public static ** INSTANCE;
}
-keepclassmembers class <1> {
    public static <1> INSTANCE;
    kotlinx.serialization.KSerializer serializer(...);
}

# @Serializable and @Polymorphic are used at runtime for polymorphic serialization.
-keepattributes RuntimeVisibleAnnotations,AnnotationDefault

-keepattributes *Annotation*
-keepclassmembers enum androidx.lifecycle.Lifecycle$Event {
    <fields>;
}
-keep !interface * implements androidx.lifecycle.LifecycleObserver {
}
-keep class * implements androidx.lifecycle.GeneratedAdapter {
    <init>(...);
}
-keepclassmembers class ** {
    @androidx.lifecycle.OnLifecycleEvent *;
}
# this rule is need to work properly when app is compiled with api 28, see b/142778206
# Also this rule prevents registerIn from being inlined.
-keepclassmembers class androidx.lifecycle.ReportFragment$LifecycleCallbacks { *; }

-keep enum * { *; }

# Please add these rules to your existing keep rules in order to suppress warnings.
# This is generated automatically by the Android Gradle plugin.
-dontwarn org.jetbrains.kotlin.compiler.plugin.CliOption
-dontwarn org.jetbrains.kotlin.compiler.plugin.CommandLineProcessor
-dontwarn org.jetbrains.kotlin.compiler.plugin.ComponentRegistrar
-dontwarn org.jetbrains.kotlin.diagnostics.DiagnosticFactory0
-dontwarn org.jetbrains.kotlin.diagnostics.DiagnosticFactory1
-dontwarn org.jetbrains.kotlin.diagnostics.DiagnosticFactory2
-dontwarn org.jetbrains.kotlin.diagnostics.DiagnosticFactory3
-dontwarn org.jetbrains.kotlin.diagnostics.Errors$Initializer
-dontwarn org.jetbrains.kotlin.diagnostics.PositioningStrategies
-dontwarn org.jetbrains.kotlin.diagnostics.PositioningStrategy
-dontwarn org.jetbrains.kotlin.diagnostics.Severity
-dontwarn org.jetbrains.kotlin.diagnostics.rendering.CommonRenderers
-dontwarn org.jetbrains.kotlin.diagnostics.rendering.ContextIndependentParameterRenderer
-dontwarn org.jetbrains.kotlin.diagnostics.rendering.DefaultErrorMessages$Extension
-dontwarn org.jetbrains.kotlin.diagnostics.rendering.DiagnosticFactoryToRendererMap
-dontwarn org.jetbrains.kotlin.diagnostics.rendering.DiagnosticParameterRenderer
-dontwarn org.jetbrains.kotlin.diagnostics.rendering.Renderers
-dontwarn org.jetbrains.kotlin.diagnostics.rendering.SmartDescriptorRenderer
-dontwarn org.jetbrains.kotlin.diagnostics.rendering.SmartTypeRenderer
-dontwarn org.jetbrains.kotlin.fir.extensions.FirExtensionRegistrar
-dontwarn androidx.window.extensions.area.ExtensionWindowAreaPresentation
-dontwarn androidx.window.extensions.core.util.function.Consumer
-dontwarn androidx.window.extensions.core.util.function.Function
-dontwarn androidx.window.extensions.core.util.function.Predicate