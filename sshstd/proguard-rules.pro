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
-keep class ru.ostrovskal.sshstd.***
-dontobfuscate
#-dontshrink
#-keepattributes *Annotation*,SourceFile,LineNumberTable
