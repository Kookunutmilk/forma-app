-keepattributes *Annotation*, InnerClasses, Signature

# kotlinx.serialization
-keepclassmembers class kotlinx.serialization.json.** {
    *** Companion;
}
-keepclasseswithmembers class kotlinx.serialization.json.** {
    kotlinx.serialization.KSerializer serializer(...);
}
-keep,includedescriptorclasses class com.forma.app.**$$serializer { *; }
-keepclassmembers class com.forma.app.** {
    *** Companion;
}
