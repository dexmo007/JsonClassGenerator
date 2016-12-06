name := "JsonClassGenerator"

version := "1.0"

scalaVersion := "2.11.8"

// https://mvnrepository.com/artifact/com.google.code.gson/gson
libraryDependencies += "com.google.code.gson" % "gson" % "2.7"

dependencyOverrides += "org.scala-lang" % "scala-compiler" % scalaVersion.value
    