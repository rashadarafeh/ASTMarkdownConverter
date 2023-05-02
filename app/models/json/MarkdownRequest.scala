package models.json

import play.api.libs.json.{Json, OFormat}

// Define the case class for the JSON payload
case class MarkdownRequest(markdown: String)

object MarkdownRequest {
  implicit val myPayloadFmt: OFormat[MarkdownRequest] = Json.format[MarkdownRequest]
}
