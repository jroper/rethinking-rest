//import ByteConversions._

organization in ThisBuild := "sample.chirper"

// the Scala version that will be used for cross-compiled libraries
scalaVersion in ThisBuild := "2.11.7"

lazy val apiCommon = project("api-common")
  .settings(
    version := "1.0-SNAPSHOT",
    libraryDependencies += lagomJavadslApi
  )

lazy val serverCommon = project("server-common")
  .settings(
    version := "1.0-SNAPSHOT",
    libraryDependencies += lagomJavadslServer
  ).dependsOn(apiCommon)

lazy val friendApi = project("friend-api")
  .settings(
    version := "1.0-SNAPSHOT",
    libraryDependencies += lagomJavadslApi
  ).dependsOn(apiCommon)

lazy val friendImpl = project("friend-impl")
  .enablePlugins(LagomJava)
  .settings(
    version := "1.0-SNAPSHOT",
    libraryDependencies ++= Seq(
      lagomJavadslPersistence,
      lagomJavadslTestKit
    )
  )
  .settings(lagomForkedTestSettings: _*)
  .dependsOn(friendApi, serverCommon)

lazy val chirpApi = project("chirp-api")
  .settings(
    version := "1.0-SNAPSHOT",
    libraryDependencies ++= Seq(
      lagomJavadslApi,
      lagomJavadslJackson
    )
  ).dependsOn(apiCommon, likeApi)

lazy val chirpImpl = project("chirp-impl")
  .enablePlugins(LagomJava)
  .settings(
    version := "1.0-SNAPSHOT",
    libraryDependencies ++= Seq(
      lagomJavadslPersistence,
      lagomJavadslPubSub,
      lagomJavadslTestKit
    )
  )
  .settings(lagomForkedTestSettings: _*)
  .dependsOn(chirpApi, serverCommon)

lazy val activityStreamApi = project("activity-stream-api")
  .settings(
    version := "1.0-SNAPSHOT",
    libraryDependencies += lagomJavadslApi
  )
  .dependsOn(chirpApi, apiCommon)

lazy val activityStreamImpl = project("activity-stream-impl")
  .enablePlugins(LagomJava)
  .settings(
    version := "1.0-SNAPSHOT",
    libraryDependencies += lagomJavadslTestKit
  )
  .dependsOn(activityStreamApi, chirpApi, friendApi, serverCommon)

lazy val likeApi = project("like-api")
  .settings(
    version := "1.0-SNAPSHOT",
    libraryDependencies ++= Seq(
      lagomJavadslApi,
      lagomJavadslJackson
    )
  ).dependsOn(apiCommon)

lazy val likeImpl = project("like-impl")
  .enablePlugins(LagomJava)
  .settings(
    version := "1.0-SNAPSHOT",
    libraryDependencies ++= Seq(
      lagomJavadslPersistence,
      lagomJavadslPubSub,
      lagomJavadslTestKit
    )
  )
  .settings(lagomForkedTestSettings: _*)
  .dependsOn(likeApi, serverCommon)

lazy val frontEnd = project("front-end")
  .enablePlugins(PlayJava, LagomPlay)
  .settings(
    version := "1.0-SNAPSHOT",
    routesGenerator := InjectedRoutesGenerator,
    libraryDependencies ++= Seq(
      "org.webjars" % "react" % "0.14.3",
      "org.webjars" % "react-router" % "1.0.3",
      "org.webjars" % "jquery" % "2.2.0",
      "org.webjars" % "foundation" % "5.3.0"
//      "com.typesafe.conductr" %% "lagom10-conductr-bundle-lib" % "1.4.1"
    ),
    // needed to resolve lagom10-conductr-bundle-lib
    resolvers += Resolver.bintrayRepo("typesafe", "maven-releases"),
    ReactJsKeys.sourceMapInline := true
    // ConductR settings
/*    BundleKeys.nrOfCpus := 1.0,
    BundleKeys.memory := 64.MiB,
    BundleKeys.diskSpace := 35.MB,
    BundleKeys.endpoints := Map("web" -> Endpoint("http", services = Set(URI("http://:9000")))),
    javaOptions in Bundle ++= Seq("-Dhttp.address=$WEB_BIND_IP", "-Dhttp.port=$WEB_BIND_PORT")
*/  )

lazy val loadTestApi = project("load-test-api")
  .settings(
    version := "1.0-SNAPSHOT",
    libraryDependencies += lagomJavadslApi
  ).dependsOn(apiCommon)

lazy val loadTestImpl = project("load-test-impl")
  .enablePlugins(LagomJava)
  .settings(version := "1.0-SNAPSHOT")
  .dependsOn(loadTestApi, friendApi, activityStreamApi, chirpApi, serverCommon)

def project(id: String) = Project(id, base = file(id))
  .settings(eclipseSettings: _*)
  .settings(javacOptions in compile ++= Seq("-encoding", "UTF-8", "-source", "1.8", "-target", "1.8", "-Xlint:unchecked", "-Xlint:deprecation"))
  .settings(jacksonParameterNamesJavacSettings: _*) // applying it to every project even if not strictly needed.


// See https://github.com/FasterXML/jackson-module-parameter-names
lazy val jacksonParameterNamesJavacSettings = Seq(
  javacOptions in compile += "-parameters"
)

// configuration of sbteclipse
lazy val eclipseSettings = Seq(
  EclipseKeys.projectFlavor := EclipseProjectFlavor.Java,
  EclipseKeys.withBundledScalaContainers := false,
  EclipseKeys.createSrc := EclipseCreateSrc.Default + EclipseCreateSrc.Resource,
  EclipseKeys.eclipseOutput := Some(".target"),
  EclipseKeys.withSource := true,
  EclipseKeys.withJavadoc := true
)

// do not delete database files on start
lagomCassandraCleanOnStart in ThisBuild := false
