package de.hd.jcg.generators

import java.util.ServiceLoader

import scala.collection.immutable.IndexedSeq
import scala.collection.mutable.ListBuffer

/**
  * Created by henri on 10/26/2016.
  */
trait Generator {
  /**
    * flag that indicates if the class will be generated with annotations
    */
  protected final var annotate: Boolean = true
  /**
    * type names for {0: string, 1: boolean, 2:floating point, 3:integer, 4:list of type {0}, 5:object}
    */
  protected val typeNames: Array[String]
  val languageName: String
  val highlightClass: String
  val fileEnding: String
  protected var innerClasses: List[String] = Nil

  protected def generateClassInternal(className: String, json: String): String

  def generateClass(json: String, className: String = "RootElement"): String = {
    innerClasses = Nil
    s"""${generateClassInternal(className, json)}
       |${innerClasses.mkString("\n")}
     """.stripMargin
  }

  def generateClasses(json: String, className: String = "RootElement"): List[String] = {
    innerClasses = Nil
    generateClassInternal(className, json) :: innerClasses
  }

  protected final def classDoc(className: String): String = s"""Auto-generated JSON model class for $className"""

  def generateTypeName(attributeValue: String, name: String): String = {
    val value = attributeValue.trim
    if (value.startsWith("\"")) {
      // String
      typeNames(0)
    } else if (value == "true" || value == "false") {
      // Boolean
      typeNames(1)
    } else if (value == "null" || value == "") {
      // unknown -> Object / Any
      typeNames(5)
    } else if (value.startsWith("[")) {
      // List
      typeNames(4).replace("{0}", generateTypeName(value.splitFields.head.replace("[", ""), "Element"))
    } else if (value.startsWith("{")) {
      // Class
      val pascalCase = name.toPascalCase
      innerClasses = generateClass(value.splitFields.head, pascalCase) :: innerClasses
      pascalCase
    } else if (value.contains(".")) {
      // double
      typeNames(2)
    } else if (value.forall(Character.isDigit)) {
      // integer
      typeNames(3)
    } else {
      throw new RuntimeException("unknown type")
    }
  }

  def generateName(jsonName: String): String

  final def annotate(yesNo: Boolean): Generator = {
    annotate = yesNo
    this
  }

  protected final def parseFields(json: String): List[Array[String]] = {
    // cut curly braces
    var pureJson = json.trim
    pureJson = json.substring(1, json.length - 1).trim
    val list = new ListBuffer[Array[String]]
    for (field <- pureJson.splitFields) {
      list += field.split(":", 2).map(_.trim)
    }
    list.toList
  }

  protected final implicit class StringUtil(string: String) {
    def toCamelCase: String = {
      var trimmed = string.trim
      trimmed = string.substring(1, string.length - 1).trim
      if (trimmed.contains("_")) {
        trimmed = trimmed.split("_").map(_.capitalize).mkString
      }
      trimmed.decapitalize
    }

    def toPascalCase: String = {
      var trimmed = string.trim
      trimmed = string.substring(1, string.length - 1).trim
      if (trimmed.contains("_")) {
        return trimmed.split("_").map(_.capitalize).mkString
      }
      trimmed.capitalize
    }

    def decapitalize: String = {
      val chars = string.toCharArray
      if (string.length > 0) {
        chars(0) = chars(0).toLower
      }
      new String(chars)
    }

    def splitFields: List[String] = {
      val ch = ','
      var off = 0
      var next = string.indexOf(ch, off)
      val list = new ListBuffer[String]
      while (next != -1) {
        val substring = string.substring(off, next)
        // make sure commas in lists and inner objects are skipped
        if ((substring.contains("{") && !substring.contains("}"))
          || (substring.contains("[") && !substring.contains("]"))) {
          next = string.indexOf(ch, next + 1)
        } else {
          list += substring
          off = next + 1
          next = string.indexOf(ch, off)
        }
      }
      // If no match was found, return this
      if (off == 0) return List(string)

      // add remaining segment
      list += string.substring(off, string.length())
      list.toList
    }
  }

}

object Generator {

  import scala.collection.JavaConverters._
  import scala.collection.breakOut

  def load(): Iterable[Generator] = ServiceLoader.load(classOf[Generator]).asScala

  def loadMap(): Map[String, Generator] = ServiceLoader.load(classOf[Generator]).asScala.map(gen => (gen.highlightClass, gen))(breakOut)
}
