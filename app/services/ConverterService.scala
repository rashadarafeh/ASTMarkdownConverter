package services

import javax.inject._
import models.ast.{MarkdownParser, Node}
import models.json.HTMLResponse
import play.api.Logger

/**
 * ConverterService:
 *
 * This is the service layer of the application. This layer
 * generally houses integrations with data sources, repository classes
 * or other resources required for the business logic.
 *
 * It may have many integrations and sources, and the business logic to combine
 * all sources into the desired results
 *
 * In this simple service layer, the MarkdownParser acts as the repository class,
 * or integration. The MarkdownParser bubbles up its results to the service layer,
 * who may have more business logic to perform and combine with other data sources in the service layer...
 *
 * ...but in this example, we neednt do much with the AST results other than packaging it and turning into a printable string.
 *
 * This service layer is injected into the applications main controller as a dependency, HomeController, and the
 * parseMarkdownAndPrintHTML method is called from the controller
 */
class ConverterService @Inject()() {
  val astConverter  = MarkdownParser
  val logger = Logger(getClass)

  def parseMarkdownAndPrintHTML(markdown: String): HTMLResponse = prettyPrintHTMLOutput(parseMarkdown(markdown))

  private def parseMarkdown(markdown: String): Seq[Node] = {
    logger.info("Parsing Markdown into the AST")
    astConverter.parse(markdown)
  }

  private def prettyPrintHTMLOutput(sections: Seq[Node]): HTMLResponse = {
    val rawHtml = sections.foldLeft("") { (s, node) => s + node.toHtml }
    logger.info(s"rawHtml  Produced: $rawHtml")
    HTMLResponse(rawHtml)
  }
}
