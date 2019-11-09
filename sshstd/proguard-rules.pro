-optimizationpasses 3
#-overloadaggressively
-assumenosideeffects class kotlin.jvm.internal.Intrinsics {
    public static *** checkReturnedValueIsNotNull(...);
    public static *** checkParameterIsNotNull(...);
    public static *** checkExpressionValueIsNotNull(...);
    public static *** throwNpe(...);
}
#-repackageclasses ''
#-allowaccessmodification
-keep class ru.ostrovskal.sshstd.Common { *; }
-keep class ru.ostrovskal.sshstd.***
#-keep class com.dropbox.core.*** { *; }
#-keep class com.dropbox.core.v2.***
-dontobfuscate
#-dontshrink
#-keepattributes *Annotation*,SourceFile,LineNumberTable
