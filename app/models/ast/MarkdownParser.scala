package models.ast

import scala.util.parsing.combinator.RegexParsers

// These define the nodes of the
// AST
sealed trait Node {
  def toHtml: String
}

//Represents a Header
case class Heading(level: Int, text: String) extends Node {
  def toHtml: String = s"<h$level>${text.trim}</h$level>"
}

//Repreents a paragraph
case class Paragraph(
  before: String,
  linkText: Option[String],
  linkUrl: Option[String],
  after: Option[String]
) extends Node {
  def toHtml: String = (linkText, linkUrl, after) match {
    case (Some(link), Some(url), Some(aft)) => s"""<p>$before<a href=$url>$link</a>$aft</p>"""
    case _ => s"""<p>$before</p>"""
  }
}

// Represents a Link
// Links are found in the bodies of
// headers and paragraph
case class LinkInPlainText(
  before: String,
  linkText: Option[String],
  linkUrl: Option[String],
  after: Option[String]
) extends Node {
  def toHtml: String = (linkText, linkUrl, after) match {
    case (Some(link), Some(url), Some(aft)) => s"""$before<a href=$url>$link</a>$aft"""
    case _ => before
  }
}

/**
 *  This is an Abstract Syntax Tree implementation
 *  of a Markdown -> HTML converter.
 *
 *  The node types (Header, Paragraph, LinkInPlainText) encapsulate
 *  the conversion of their data to html in the toHtml methods.
 */

object MarkdownParser extends RegexParsers {

  // Helper function:
  // If the string contains values in and out of [] and ()
  // i.e. detecting a link, extract the components of the
  // link if present
  private def breakoutLinkComponenets(input: String): LinkInPlainText = {
      val regex1 = """^(.*?)\[(.*?)\]\((.*?)\)(.*)$""".r
      regex1.findFirstMatchIn(input) match {
        case Some(matched) =>

          if (matched.group(2).nonEmpty) {
            LinkInPlainText(matched.group(1), Some(matched.group(2)), Some(matched.group(3)), Some(matched.group(4)))
          } else {
            LinkInPlainText(matched.group(1), None, None, None)
          }
        case None =>
          val regex2 = """^(.*)$""".r
          regex2.findFirstMatchIn(input) match {
            case Some(matched) =>
              val before = matched.group(1)
              LinkInPlainText(before, None, None, None)
            case None =>
              LinkInPlainText("", None, None, None)
          }
      }
  }

  // Detecting and extracting a paragraph.
  // applies to plain text without formatting
  def paragraph: Parser[Paragraph] = {
    textWithoutHeader.flatMap { text: String =>
      val linkInPlainText = breakoutLinkComponenets(text)
      success(Paragraph(linkInPlainText.before, linkInPlainText.linkText, linkInPlainText.linkUrl, linkInPlainText.after))
    }
  }

  // Detecting and extracting the header
  // this function also reads the number
  // of #'s at the beginning of a header line
  // and passes that on as the heading level
  // level must be between 1 and 6
  def heading: Parser[Heading] = {
    "#" ~ rep1("#") ~ " ".? ~ textWithoutLink ^^ { case first ~ rest ~ _ ~ text =>
      val level = first.length + rest.length
      if (level < 1 || level > 6) {
        throw new IllegalArgumentException("Invalid input: header level must be between 1 and 6")
      } else {
        val linkInPlainText = breakoutLinkComponenets(text)
        Heading(level, linkInPlainText.toHtml)
      }
    } |
      literal("#") ~ not("#" ~ ">") ~ " ".? ~ textWithoutLink ^^ { case first ~ _ ~ _ ~ text =>
        val linkInPlainText = breakoutLinkComponenets(text)
        Heading(1, linkInPlainText.toHtml)
      }
  }

  def end: Parser[String] = """\z""".r
  def all: Parser[String] = ".+".r

  // Detect plain text, that is not a link
  def textWithoutLink: Parser[String] = {
    (not(heading) ~> not("\n") ~> not(">") ~> ".+".r <~ "\n".?) ^^ (_.trim) |
      (not(heading) ~> not(">") ~> ".+".r <~ "\n".?) ^^ (_.trim)
  }
  // Detect plain text, that is not a header
  def textWithoutHeader: Parser[String] = {
    (not(heading) ~> not("\n") ~> not(">") ~> ".+".r <~ "\n".?) ^^ (_.trim) |
      (not(heading) ~> not(">") ~> ".+".r <~ "\n".?) ^^ (_.trim) |
      ("[" ~> not("]") ~> all <~ "]") ^^ (_.trim)
  }

  def nodes: Parser[Seq[Node]] = {
    rep(node) <~ end
  }

  def node: Parser[Node] = {
    heading | paragraph
  }

  def parse(input: String): Seq[Node] = parseAll(nodes, input) match {
    case Success(result, _) => result
    case NoSuccess(msg, _) => throw new IllegalArgumentException(s"Error parsing input: $msg")
  }
}
