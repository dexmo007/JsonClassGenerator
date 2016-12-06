package de.hd.jcg.generators

/**
  * Created by henri on 10/26/2016.
  */
class CsGenerator extends Generator {

  override val typeNames = Array("string", "bool", "double", "int", "List<{0}>", "object")
  override val languageName = "C#"
  override val highlightClass: String = "cs"
  override val fileEnding = ".cs"

  override def generateClassInternal(className: String, json: String): String = {
    val fields = parseFields(json)
    fields.map(arr => generateProperty(arr(0), arr(1)))
    s"""public class $className {
        |${fields.map(raw => generateProperty(raw(0), raw(1))).mkString("\n")}
        |}
       """.stripMargin
  }

  def generateProperty(rawName: String, value: String): String = {
    val typeName = generateTypeName(value, rawName)
    if (annotate) {
      val name = generateName(rawName)
      s"""\t[JsonProperty($rawName)]
          |\tpublic $typeName $name { get; set; }
     """.stripMargin
    } else {
      s"""\tpublic $typeName ${rawName.replaceAll("\"", "")} { get; set; }"""
    }
  }

  override def generateName(jsonName: String): String = jsonName.toPascalCase
}
