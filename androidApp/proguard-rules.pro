# Ktor
-keep class io.ktor.** { *; }
-keepclassmembers class io.ktor.** { *; }
-dontwarn io.ktor.**

# Kotlinx serialization
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt
-keepclassmembers class kotlinx.serialization.json.** { *; }
-keep,includedescriptorclasses class com.altears.**$$serializer { *; }
-keepclassmembers class com.altears.** {
    *** Companion;
}
-keepclasseswithmembers class com.altears.** {
    kotlinx.serialization.KSerializer serializer(...);
}

# Coil
-dontwarn coil3.**
-keep class coil3.** { *; }
