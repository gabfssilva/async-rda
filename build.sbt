name := "r2da"
organization in ThisBuild := "io.github.gabfssilva"
version in ThisBuild := "0.0.1"
scalaVersion in ThisBuild := "2.13.0"

lazy val compilerOptions = Seq(
  "-unchecked",
  "-feature",
  "-language:existentials",
  "-language:higherKinds",
  "-language:implicitConversions",
  "-language:postfixOps",
  "-deprecation",
  "-encoding",
  "utf8"
)

lazy val commonSettings = Seq(
  scalacOptions ++= compilerOptions,
  resolvers ++= Seq(
    "Local Maven Repository" at "file://" + Path.userHome.absolutePath + "/.m2/repository",
    Resolver.jcenterRepo
  )
)

lazy val common = project
  .settings(
    name := "common",
    commonSettings,
    libraryDependencies ++= commonDependencies
  )

lazy val api = project
  .settings(
    name := "api",
    commonSettings,
    libraryDependencies ++= commonDependencies
  )
  .dependsOn(common)

lazy val postgressql = project
  .settings(
    name := "postgressql",
    commonSettings,
    libraryDependencies ++= commonDependencies
  )
  .dependsOn(common, api)

lazy val client = project
  .settings(
    name := "client",
    commonSettings,
    libraryDependencies ++= commonDependencies
  )
  .dependsOn(common, api)

lazy val global = project
  .in(file("."))
  .settings(commonSettings)
  .aggregate(client, common, api, postgressql)

val dependencies = new {
  val akkaVersion = "2.5.23"

  val akka = "com.typesafe.akka" %% "akka-actor" % akkaVersion
  val scalatest = "org.scalatest" %% "scalatest" % "3.0.8" % "test"
  val pegdown = "org.pegdown" % "pegdown" % "1.6.0" % "test"
}

lazy val commonDependencies = Seq(
  dependencies.akka,
  dependencies.scalatest,
  dependencies.pegdown
)