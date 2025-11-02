# Gson rules
-keep class com.google.gson.** { *; }
-keep class com.google.gson.reflect.TypeToken { *; }
-keep class * extends com.google.gson.reflect.TypeToken
-keep class com.google.gson.TypeAdapter { *; }
-keep class com.google.gson.TypeAdapter$1 { *; }

# Preserve all annotated classes
-keep @androidx.room.Entity class *
-keep @androidx.room.TypeConverters class *
-keep class * extends androidx.room.TypeConverter

# Preserve all model classes
-keep class com.example.recipemanager.data.model.** { *; }

# Keep parameter names for Gson
-keepattributes Signature
-keepattributes *Annotation*
-keepattributes EnclosingMethod
-keep class sun.misc.Unsafe { *; }
-keep class com.google.gson.stream.** { *; }