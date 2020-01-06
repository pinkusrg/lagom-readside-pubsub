import com.lightbend.lagom.sbt.LagomImport.lagomForkedTestSettings

organization in ThisBuild := "com.grapes"
version in ThisBuild := "1.0-SNAPSHOT"

// the Scala version that will be used for cross-compiled libraries
scalaVersion in ThisBuild := "2.13.0"

val macwire = "com.softwaremill.macwire" %% "macros" % "2.3.3" % "provided"
val scalaTest = "org.scalatest" %% "scalatest" % "3.0.8" % Test

lazy val `pub-sub` = (project in file("."))
  .aggregate(`pub-api`, `pub-impl`,`sub-api`, `sub-impl`)

lazy val `pub-api` = (project in file("pub-api"))
  .settings(
    libraryDependencies ++= Seq(
      lagomScaladslApi
    )
  )
lazy val `pub-impl` = (project in file("pub-impl"))
  .enablePlugins(LagomScala)
  .settings(
    libraryDependencies ++= Seq(
      lagomScaladslPersistenceCassandra,
      lagomScaladslKafkaBroker,
      lagomScaladslKafkaClient,
      lagomScaladslTestKit,
      lagomScaladslBroker,
      lagomScaladslApi,
      macwire
    )
  ).settings(lagomForkedTestSettings: _*)
  .dependsOn(`pub-api`)

lazy val `sub-api` = (project in file("sub-api"))
  .settings(
    libraryDependencies ++= Seq(
      lagomScaladslApi
    )
  )
lazy val `sub-impl` = (project in file("sub-impl"))
  .enablePlugins(LagomScala)
  .settings(
    libraryDependencies ++= Seq(
      lagomScaladslPersistenceCassandra,
      lagomScaladslKafkaBroker,
      lagomScaladslKafkaClient,
      lagomScaladslBroker,
      lagomScaladslTestKit,
      macwire,
      scalaTest
    )
  ).settings(lagomForkedTestSettings: _*)
  .dependsOn(`sub-api`, `pub-api`)

lagomCassandraEnabled in ThisBuild := false
lagomUnmanagedServices in ThisBuild := Map("cas_native" -> "http://localhost:9042")
lagomCassandraPort in ThisBuild := 9042

lagomKafkaEnabled in ThisBuild := false
lagomKafkaAddress in ThisBuild := "localhost:9092"
