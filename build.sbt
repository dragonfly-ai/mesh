val appVersion:String = "0.12"
val globalScalaVersion = "3.3.1"

ThisBuild / organization := "ai.dragonfly"
ThisBuild / organizationName := "dragonfly.ai"
ThisBuild / startYear := Some(2023)
ThisBuild / licenses := Seq(License.Apache2)
ThisBuild / developers := List( tlGitHubDev("dragonfly-ai", "dragonfly.ai") )
ThisBuild / scalaVersion := globalScalaVersion

ThisBuild / tlBaseVersion := appVersion
ThisBuild / tlCiReleaseBranches := Seq()
ThisBuild / tlSonatypeUseLegacyHost := false

ThisBuild / nativeConfig ~= {
  _.withLTO(scala.scalanative.build.LTO.thin)
    .withMode(scala.scalanative.build.Mode.releaseFast)
    .withGC(scala.scalanative.build.GC.commix)
}

lazy val mesh = crossProject(JSPlatform, JVMPlatform, NativePlatform)
  .crossType(CrossType.Full)
  .settings(
    name := "mesh",
    libraryDependencies ++= Seq(
      "ai.dragonfly" %%% "slash" % "0.2"
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
      "ai.dragonfly" %%% "democrossy" % "0.102"
    ),
    Compile / mainClass := Some("Demo")
  )
  .jsSettings(
    Compile / fastOptJS / artifactPath := file("./docs/js/main.js"),
    Compile / fullOptJS / artifactPath := file("./docs/js/main.js"),
    scalaJSUseMainModuleInitializer := true
  )
  .jvmSettings()


lazy val root = tlCrossRootProject.aggregate(mesh, tests).settings(name := "mesh")

lazy val docs = project.in(file("site")).enablePlugins(TypelevelSitePlugin).settings(
  mdocVariables := Map(
    "VERSION" -> appVersion,
    "SCALA_VERSION" -> globalScalaVersion
  ),
  laikaConfig ~= { _.withRawContent }
)

lazy val unidocs = project
  .in(file("unidocs"))
  .enablePlugins(TypelevelUnidocPlugin) // also enables the ScalaUnidocPlugin
  .settings(
    name := "mesh-docs",
    ScalaUnidoc / unidoc / unidocProjectFilter :=
      inProjects(
        mesh.jvm,
        mesh.js,
        mesh.native
      )
  )

lazy val tests = crossProject(
    JVMPlatform,
    JSPlatform,
    NativePlatform
  )
  .in(file("tests"))
  .enablePlugins(NoPublishPlugin)
  .dependsOn(mesh)
  .settings(
    name := "mesh-tests",
    libraryDependencies += "org.scalameta" %%% "munit" % "1.0.0-M10" % Test
  )