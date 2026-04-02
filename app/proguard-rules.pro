# ProGuard / R8 Rules for Adaptive Gym Tracker Security

# 1. Protect Domain Logic (Aggressive Obfuscation)
# Do not allow the core algorithmic classes to be easily decompiled
-keepclassmembers class * {
    @dagger.Provides *;
}
-repackageclasses ''
-flattenpackagehierarchy ''
-keepattributes SourceFile,LineNumberTable

# 2. Room Database and SQLCipher
-keep class com.mattia.adaptivegymtracker.data.entities.** { *; }
-keepclassmembers class com.mattia.adaptivegymtracker.data.entities.** {
    *;
}
-keep class net.sqlcipher.** { *; }
-keep class net.sqlcipher.database.** { *; }

# 3. Firebase & Coroutines Stability
-keepattributes *Annotation*
-keepattributes Signature
-keepattributes Exceptions
-keepattributes InnerClasses

-keepclassmembers class ** {
    @androidx.room.Query *;
    @androidx.room.Insert *;
    @androidx.room.Update *;
    @androidx.room.Delete *;
}

# Assume no side effects so R8 can strip uncalled methods heavily
-assumenosideeffects class android.util.Log {
    public static boolean isLoggable(java.lang.String, int);
    public static int d(...);
    public static int w(...);
    public static int v(...);
    public static int i(...);
}
