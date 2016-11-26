package de.hd.jcg.view

import java.awt.Toolkit
import java.awt.datatransfer.StringSelection
import java.net.URL
import java.util.{ResourceBundle, ServiceLoader}
import javafx.fxml.{FXML, Initializable}
import javafx.scene.control._
import javafx.scene.layout.FlowPane
import javafx.scene.web.WebView

import de.hd.jcg.generators.Generator

/**
  * Created by henri on 10/28/2016.
  */
class MainController extends Initializable {

  @FXML
  var flowPane: FlowPane = _
  @FXML
  var jsonArea: TextArea = _
  @FXML
  var webView: WebView = _
  @FXML
  var annotate: CheckBox = _

  val toggleGroup = new ToggleGroup
  var generators = Map.empty[Toggle, Generator]

  override def initialize(location: URL, resources: ResourceBundle): Unit = {
    val iterator = ServiceLoader.load(classOf[Generator]).iterator()
    while (iterator.hasNext) {
      val gen = iterator.next()
      val button = new RadioButton(gen.languageName)
      generators += button -> gen
      button.setToggleGroup(toggleGroup)
      flowPane.getChildren.add(button)
    }
    jsonArea.setText(s"""{"name":"Peeda","age":42,"is_single":false,"girlfriends":["Tessa","Maria"],"height":1.86,"job":null,"nums":[1,2,3,4]}""")
  }

  @FXML
  def generate(): Unit = {
    val generator = generators(toggleGroup.getSelectedToggle).annotate(annotate.isSelected)
    val code = generator.generateClass(jsonArea.getText)
    val html = CodeFormat.generateHTML("java", code, "androidstudio").toURL.toExternalForm
    webView.getEngine.load(html)
  }

  @FXML
  def copy(): Unit = {
//    if (codeArea.getText.isEmpty) return
//    val sel = new StringSelection(codeArea.getText)
//    Toolkit.getDefaultToolkit.getSystemClipboard.setContents(sel, sel)
  }
}
