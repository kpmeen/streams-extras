resolvers ++= Seq(
  Resolver.bintrayIvyRepo("sbt", "sbt-plugin-releases"),
  Resolver.typesafeRepo("releases")
)

addSbtPlugin("io.get-coursier"   %% "sbt-coursier"          % "1.0.0-RC1")
addSbtPlugin("com.geirsson"      % "sbt-scalafmt"           % "0.6.3")
addSbtPlugin("org.scalastyle"    %% "scalastyle-sbt-plugin" % "0.8.0")
addSbtPlugin("org.scoverage"     % "sbt-scoverage"          % "1.5.0")
addSbtPlugin("com.timushev.sbt"  % "sbt-updates"            % "0.3.0")
addSbtPlugin("com.github.gseitz" % "sbt-release"            % "1.0.4")
addSbtPlugin("me.lessis"         % "bintray-sbt"            % "0.3.0")
//addSbtPlugin("org.foundweekends" % "sbt-bintray"            % "0.4.0")
