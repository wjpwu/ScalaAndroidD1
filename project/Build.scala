import sbt._

import Keys._
import AndroidKeys._

object General {

  val resolutionRepos = Seq(
    "typesafe releases" at "http://repo.typesafe.com/typesafe/releases",
    "typesafe snapshots" at "http://repo.typesafe.com/typesafe/snapshots"
  )

  val settings = Defaults.defaultSettings ++ Seq (
    name := "PactDD",
    version := "0.3",
    versionCode := 3,
    scalaVersion := "2.10.1",
    platformName in Android := "android-8",
    javacOptions ++= Seq("-encoding", "UTF-8", "-source", "1.6", "-target", "1.6"),
    githubRepo in Android := "https://github.com/wjpwu/ScalaAndroidD1.git",
    resolvers             ++= resolutionRepos
  )

  val proguardSettings = Seq (
    useProguard in Android := true,
    proguardOption in Android :=
      """-dontobfuscate
        |-dontoptimize
        |-dontpreverify
        |-dontskipnonpubliclibraryclasses
        |-dontskipnonpubliclibraryclassmembers
        |-keepattributes Exceptions,InnerClasses,Signature,Deprecated,
        |                SourceFile,LineNumberTable,*Annotation*,EnclosingMethod
        |-keep public class com.markupartist.**
        |-dontwarn com.markupartist.**
        |-dontwarn com.google.zxing.**
        |-keep public class com.google.zxing.**
        |-dontwarn com.loopj.**
        |-keep public class com.loopj.**
        |-dontwarn org.dom4j.**
        |-keep public class org.dom4j.**
        |-dontwarn com.thoughtworks.**
        |-keep public class com.thoughtworks.**
        |
        |-keep public class org.jaxen.**
        |-dontwarn org.jaxen.**
        |
        |-keep public class org.w3c.**
        |-dontwarn org.w3c.**
        |
        |-dontwarn com.google.common.collect.MinMaxPriorityQueue
        |-dontwarn com.google.common.primitives.UnsignedBytes$*
        |
        |-dontnote android.app.backup.BackupAgentHelper
        |-dontnote com.android.vending.licensing.ILicensingService
        |
        |-keep public class * extends android.app.Activity
        |-keep public class * extends android.app.Application
        |-keep public class * extends android.app.Service
        |-keep public class * extends android.content.BroadcastReceiver
        |-keep public class * extends android.content.ContentProvider
        |-keep public class * extends android.app.backup.BackupAgentHelper
        |-keep public class * extends android.preference.Preference
        |-keep public class com.android.vending.licensing.ILicensingService
        |
        |-keepclasseswithmembernames class * {
        |    native <methods>;
        |}
        |
        |-keepclasseswithmembers class * {
        |    public <init>(android.content.Context, android.util.AttributeSet);
        |}
        |
        |-keepclasseswithmembers class * {
        |    public <init>(android.content.Context, android.util.AttributeSet, int);
        |}
        |
        |-keepclassmembers class * extends android.app.Activity {
        |   public void *(android.view.View);
        |}
        |
        |-keepclassmembers enum * {
        |    public static **[] values();
        |    public static ** valueOf(java.lang.String);
        |}
        |
        |-keep class * implements android.os.Parcelable {
        |  public static final android.os.Parcelable$Creator *;
        |}
        |
        |-dontwarn **$$anonfun$*
        |-dontwarn scala.android.**
        |-dontwarn scala.beans.ScalaBeanInfo
        |-dontwarn scala.collection.generic.GenTraversableFactory
        |-dontwarn scala.collection.immutable.RedBlack$Empty
        |-dontwarn scala.concurrent.forkjoin.**
        |-dontwarn scala.reflect.**
        |-dontwarn scala.sys.process.**
        |-dontwarn scala.tools.**,plugintemplate.**
        |
        |-dontnote org.xml.sax.EntityResolver
        |
        |-dontnote org.apache.james.mime4j.storage.DefaultStorageProvider
        |
        |-dontnote scala.android.app.Activity
        |
        |-keep class scala.android.package**
        |-keep class * extends scala.android.app.Activity
        |-keep class * extends android.app.Activity
        |-keep class * extends scala.runtime.MethodCache {
        |    public <methods>;
        |}
        |
        |-keepclassmembers class * {
        |    ** MODULE$;
        |}
        |
        |-keepclassmembernames class scala.concurrent.forkjoin.ForkJoinPool {
        |    long eventCount;
        |    int  workerCounts;
        |    int  runControl;
        |}
        |
        |-keepclassmembernames class scala.concurrent.forkjoin.ForkJoinWorkerThread {
        |    int base;
        |    int sp;
        |    int runState;
        |}
        |-keepclassmembernames class scala.concurrent.forkjoin.ForkJoinTask {
        |    int status;
        |}
        |-dontwarn scala.concurrent.duration.*
        |-keep class scala.concurrent.duration.*
        |-dontwarn scala.concurrent.impl.*
        |-keep class scala.concurrent.impl.*
        |-dontwarn scala.concurrent.util.Unsafe
        |-keep class scala.concurrent.util.Unsafe { *;}
        |-dontwarn scala.*
        |-keep class scala.*
      """.stripMargin
  )

  lazy val fullAndroidSettings =
    General.settings ++
    AndroidProject.androidSettings ++
    TypedResources.settings ++
    proguardSettings ++
    AndroidManifestGenerator.settings ++
    AndroidMarketPublish.settings ++ Seq (
      keyalias in Android := "password",
      libraryDependencies += "org.scalatest" %% "scalatest" % "1.9" % "test",
      libraryDependencies += "org.scaloid" %% "scaloid" % "2.1-8"
    )
}

object AndroidBuild extends Build {
  lazy val main = Project (
    "PactDD",
    file("."),
    settings = General.fullAndroidSettings
  )

  lazy val tests = Project (
    "tests",
    file("tests"),
    settings = General.settings ++
               AndroidTest.androidSettings ++
               General.proguardSettings ++ Seq (
      name := "PactDDTests"
    )
  ) dependsOn main
}
