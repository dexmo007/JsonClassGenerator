package de.hd.jcg.view

import java.io.{File, PrintWriter}
import java.net.URI

import scala.io.Source

/**
  * Created by henri on 10/28/2016.
  */
object CodeFormat {

  def generateHTML(lang: String, code: String, style: String): URI = {
    val modified = Source.fromFile(getClass.getResource("/highlight/code.html").toURI).getLines.mkString
      .replace("{LANGUAGE}", lang)
      .replace("{CODE}", code)
      .replace("{STYLE}", style)

    val output = getClass.getResource("/highlight/formatted.html").toURI
    new PrintWriter(new File(output)) {
      write(modified)
      close()
    }
    output
  }

}
