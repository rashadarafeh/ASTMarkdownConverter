package models.ast

package test.models.ast

import org.specs2.mutable.Specification
import models.ast.{Heading, Paragraph, MarkdownParser}

class MarkdownParserSpec extends Specification {
  "MarkdownParser" should {
    "parse headings correctly" in {
      MarkdownParser.parse("# Heading 1") must beEqualTo(Seq(Heading(1, "Heading 1")))
      MarkdownParser.parse("## Heading 2") must beEqualTo(Seq(Heading(2, "Heading 2")))
      MarkdownParser.parse("### Heading 3") must beEqualTo(Seq(Heading(3, "Heading 3")))
      MarkdownParser.parse("#### Heading 4") must beEqualTo(Seq(Heading(4, "Heading 4")))
      MarkdownParser.parse("##### Heading 5") must beEqualTo(Seq(Heading(5, "Heading 5")))
      MarkdownParser.parse("###### Heading 6") must beEqualTo(Seq(Heading(6, "Heading 6")))
    }

    "parse paragraphs without links correctly" in {
      MarkdownParser.parse("This is a paragraph.") must beEqualTo(Seq(Paragraph("This is a paragraph.", None, None, None)))
      MarkdownParser.parse("This is a paragraph.\nThis is another paragraph.") must beEqualTo(Seq(Paragraph("This is a paragraph.", None, None, None), Paragraph("This is another paragraph.", None, None, None)))
    }

    "parse paragraphs with links correctly" in {
      MarkdownParser.parse("[link](https://example.com) at the front of a paragraph.") must beEqualTo(Seq(Paragraph("", Some("link"), Some("https://example.com"), Some(" at the front of a paragraph."))))
      MarkdownParser.parse("This is a paragraph with a [link](https://example.com) in the middle.") must beEqualTo(Seq(Paragraph("This is a paragraph with a ", Some("link"), Some("https://example.com"), Some(" in the middle."))))
      MarkdownParser.parse("This is a paragraph where at the end is the [link](https://example.com)") must beEqualTo(Seq(Paragraph("This is a paragraph where at the end is the ", Some("link"), Some("https://example.com"), Some(""))))
    }
  }
}


