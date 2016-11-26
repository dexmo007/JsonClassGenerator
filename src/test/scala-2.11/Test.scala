import de.hd.jcg.generators.JavaGenerator

import scala.collection.mutable.ListBuffer

/**
  * Created by henri on 10/27/2016.
  */
object Test {

  val json = "{\"first_name\":\"John\", \"last_name\":\"Doe\"}"

  val json2 = s"""{"name":"Peeda","age":42,"is_single":false,"girlfriends":["Tessa","Maria"],"height":1.86,"job":null,"nums":[1,2,3,4]}"""

  val complex =
    """{
      |"name":"dexter",
      |"friends":["Harry","Floid"],
      |"age":23,
      |"single":true,
      |"car":{"brand":"vw","year":2014}
      |}""".stripMargin

  def main(args: Array[String]): Unit = {
    println(new JavaGenerator().generateClass(complex))
  }
}

