package de.hd.jcg.generators

/**
  * Created by henri on 10/28/2016.
  */
class ScalaGenerator extends Generator {
  /**
    * type names for {0: string, 1: boolean, 2:floating point, 3:integer, 4:list of type {0}, 5:object}
    */
  override protected val typeNames: Array[String] = Array("String", "Boolean", "Double", "Int", "List[{0}]", "Any")
  override val languageName: String = "Scala"
  override val highlightClass: String = "scala"
  override val fileEnding: String = ".scala"

  override def generateClassInternal(className: String, json: String): String = {
    val clazz =
      s"""/**
          | * ${classDoc(className)}
          | */
          |case class $className(
          |${parseFields(json).map(generateProperty).mkString(",\n")}
          |\t) {
          |}
     """.stripMargin
    if (!annotate) return clazz
    s"""import com.google.gson.annotations.SerializedName
        |
         |$clazz
       """.stripMargin
  }

  private def generateProperty(raw: Array[String]): String = {
    val typeName = generateTypeName(raw(1), raw(0))
    if (annotate) {
      s"""\t@SerializedName(${raw(0)}) ${generateName(raw(0))}: $typeName"""
    } else {
      s"""\t${raw(0)}: $typeName"""
    }
  }

  override def generateName(jsonName: String): String = jsonName.toCamelCase
}
