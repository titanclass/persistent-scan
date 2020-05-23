import de.heikoseeberger.sbtheader.License
// *****************************************************************************
// Projects
// *****************************************************************************

lazy val `persistent-scan` =
  project
    .in(file("."))
    .enablePlugins(AutomateHeaderPlugin)
    .settings(commonSettings)
    .settings(
      libraryDependencies ++= Seq(
        library.akkaStreams,
        library.munit           % Test,
        library.munitScalaCheck % Test,
      ),
    )

// *****************************************************************************
// Library dependencies
// *****************************************************************************

lazy val library =
  new {
    object Version {
      val akka  = "2.6.5"
      val munit = "0.7.7"
    }
    val akkaStreams     = "com.typesafe.akka" %% "akka-stream"      % Version.akka
    val munit           = "org.scalameta"     %% "munit"            % Version.munit
    val munitScalaCheck = "org.scalameta"     %% "munit-scalacheck" % Version.munit
  }

// *****************************************************************************
// Settings
// *****************************************************************************

lazy val commonSettings =
  Seq(
    scalaVersion := "2.13.2",
    organization := "au.com.titanclass",
    organizationName := "Titan Class Pty Ltd ",
    startYear := Some(2020),
    licenses += ("Apache-2.0", new URL("https://www.apache.org/licenses/LICENSE-2.0.txt")),
    scalacOptions ++= Seq(
      "-unchecked",
      "-deprecation",
      "-language:_",
      "-encoding", "UTF-8",
      "-Ywarn-unused:imports",
    ),
    testFrameworks += new TestFramework("munit.Framework"),
    scalafmtOnCompile := true,
    Compile / compile / wartremoverWarnings ++= Warts.unsafe,
)
