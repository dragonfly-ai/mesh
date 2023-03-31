ThisBuild / scalaVersion := "3.2.1"
ThisBuild / publishTo := Some( Resolver.file( "file",  new File("/var/www/maven" ) ) )
ThisBuild / resolvers += "ai.dragonfly.code" at "https://code.dragonfly.ai/"
ThisBuild / organization := "ai.dragonfly.code"
ThisBuild / scalacOptions ++= Seq("-feature", "-deprecation")
ThisBuild / version := "0.03.41.5401"

lazy val mesh = crossProject(JSPlatform, JVMPlatform, NativePlatform)
  .crossType(CrossType.Full)
  .settings(
    name := "mesh",
    libraryDependencies ++= Seq(
      "ai.dragonfly.code" %%% "matrix" % "0.41.5401"
    ),
  )
  .jsSettings()
  .jvmSettings().nativeSettings(
    libraryDependencies += "org.scala-js" %% "scalajs-stubs" % "1.1.0"
  )


lazy val demo = crossProject(JSPlatform, JVMPlatform, NativePlatform)
  .crossType(CrossType.Full)
  .dependsOn(mesh)
  .settings(
    name := "demo",
    Compile / mainClass := Some("Demo"),
    libraryDependencies ++= Seq(
      "ai.dragonfly.code" %%% "democrossy" % "0.02"
    ),
    Compile / mainClass := Some("Demo")
  )
  .jsSettings(
    Compile / fastOptJS / artifactPath := file("./demo/public_html/js/main.js"),
    Compile / fullOptJS / artifactPath := file("./demo/public_html/js/main.js"),
    scalaJSUseMainModuleInitializer := true
  )
  .jvmSettings()
