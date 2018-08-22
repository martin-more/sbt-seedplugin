import sbt.ScriptedPlugin._
// import _root_.bintray.BintrayPlugin.bintrayPublishSettings

crossSbtVersions := Seq("1.0.2", "0.13.16")	// !IMPORTANT

val baseVersion = "1.0.0-SNAPSHOT"

scriptedLaunchOpts := { scriptedLaunchOpts.value ++
  Seq("-Xmx1024M", "-Dplugin.version=" + version.value)
}
scriptedBufferLog := false

val maxMetaspaceSize = if (scala.util.Properties.isJavaAtLeast("1.8")) {
	"-XX:MaxMetaspaceSize=384m"
} else {
	"-XX:MaxPermSize=384m"
}

// SBT 0.13
// https://www.scala-sbt.org/0.13.15/api/index.html#sbt.Project$
/*
**	lazy val root = Project(
**	"seed-plugin",
**	file("."),
**	settings = commonSettings ++ Seq(
**		// ++ libraryDependencies ++ Seq(...)
**	)
**)
*/

// SBT 1.x
// https://www.scala-sbt.org/1.x/api/sbt/Project.html
lazy val root = Project(
	"sbt-seedplugin", 
	file(".")
).settings(
	commonSettings,
	// other settings
)

def commonSettings = {
	versionWithGit ++
	// scriptedSettings ++	// SBT 0.13
	sbtrelease.ReleasePlugin.projectSettings ++
	Seq(
		organization    := "com.martinmore.scala.plugin",
		sbtPlugin       := true,
		git.baseVersion := baseVersion,
		sbtVersion in GlobalScope := {
			System.getProperty("sbt.build.version", (sbtVersion in GlobalScope).value)
		},
		scalacOptions ++= Seq("-unchecked", "-deprecation",
			CrossVersion.partialVersion(scalaVersion.value) match {
				case Some((2, 12)) => "-target:jvm-1.8"
				case _             => "-target:jvm-1.7"
			}
		),
		scalaVersion  := {
			(sbtVersion in GlobalScope).value match {
				case sbt10  if sbt10.startsWith("1.0")    => "2.12.4"
				case sbt013 if sbt013.startsWith("0.13.") => "2.10.6"
				case _  => "2.12.4"
			}
		},
		//	SBT 0.13
		/*
		sbtDependency in GlobalScope := {
			(sbtDependency in GlobalScope).value.copy(revision = (sbtVersion in GlobalScope).value)
		},
		*/
		sbtDependency in GlobalScope := {
			(sbtDependency in GlobalScope).value.withRevision(revision = (sbtVersion in GlobalScope).value)
		},
		// Another settings like bintray tools
		licenses += ("Apache-2.0", url("https://www.apache.org/licenses/LICENSE-2.0.html")),
		scriptedBufferLog := false,
		scriptedLaunchOpts ++= Seq(maxMetaspaceSize, "-Dplugin.version=" + version.value)
	)
}

