package de.hd.jcg.generators

/**
  * Created by henri on 10/26/2016.
  */
class JavaGenerator extends Generator {

  override val typeNames = Array("String", "boolean", "double", "int", "List<{0}>", "Object")
  override val languageName = "Java"
  override val highlightClass = "java"
  override val fileEnding = ".java"

  override def generateTypeName(attributeValue: String, name: String): String = {
    val normal = super.generateTypeName(attributeValue, name)
    if (normal.startsWith("List")) {
      var componentType = normal.substring(normal.indexOf('<') + 1, normal.indexOf('>'))
      componentType = if (componentType == "int") "Integer" else componentType.capitalize
      s"List<$componentType>"
    } else {
      normal
    }
  }

  //noinspection ZeroIndexToHead
  override def generateClassInternal(className: String, json: String): String = {
    // parses and maps to (type name, raw name, camelcase name )
    val name: String => String = if (annotate) generateName else _.replaceAll("\"", "")
    val fields = parseFields(json).map(raw => List(generateTypeName(raw(1), raw(0)), raw(0).replaceAll("\"", ""), name(raw(0))))

    val clazz =
      s"""/**
          | * ${classDoc(className)}
          | */
          |public class $className {
          |
          |${fields.map(field => generateField(field)).mkString("\n")}
          |${fields.map(field => generateGetSet(field(0), field(2))).mkString("\n")}
          |}
     """.stripMargin

    if (!annotate) return clazz
    s"""import com.google.gson.annotations.SerializedName;
        |
         |$clazz
       """.stripMargin
  }

  override def generateName(jsonName: String): String = jsonName.toCamelCase

  private def generateField(field: List[String]): String = {
    val typeName = field.head
    val rawName = field(1)
    val name = field(2)
    if (annotate) {
      s"""\t@SerializedName("$rawName")
          |\tprivate $typeName $name;
          |""".stripMargin
    } else {
      s"""\tprivate $typeName $rawName;
       """.stripMargin
    }
  }

  private def generateGetSet(typeName: String, name: String): String = {
    val capName = name.capitalize
    s"""\tpublic $typeName get$capName(){
        |\t\treturn $name;
        |\t}
        |
       |\tpublic void set$capName($typeName $name){
        |\t\tthis.$name = $name;
        |\t}
     """.stripMargin
  }

}
