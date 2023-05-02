package models.json

import play.api.libs.json.{Json, OFormat}


// Define a case class for the response body
case class HTMLResponse(rawHtml: String)
object HTMLResponse {
  implicit val myResponseFmt: OFormat[HTMLResponse] = Json.format[HTMLResponse]
}

