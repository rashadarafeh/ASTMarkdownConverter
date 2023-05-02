package controllers

import models.json.MarkdownRequest
import javax.inject._
import play.api.libs.json.{JsValue, Json}
import play.api.mvc._
import services.ConverterService

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class HomeController @Inject()(val controllerComponents: ControllerComponents, val converterService: ConverterService) extends BaseController {

  /** This is a healthcheck endpoint provided by Play:
   * Create an Action to render an HTML page.
   *
   * The configuration in the `routes` file means that this method
   * will be called when the application receives a `GET` request with
   * a path of `/`.
   */
  def index() = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.index())
  }

  /** The end point serving this functionality
   * This is a POST method that accepts a payload in
   * the form of:
   *  {"markdown": "## This is a header [with a link](http://yahoo.com)"}'
   *
   * it returns a json in the form of:
   *
   * {"rawHtml": "<h2>This is a header <a href=http://yahoo.com>with a link</a></h2>"}
   * */

  def postMethod(): Action[JsValue] = Action(parse.json) { implicit request: Request[JsValue] =>
    // Validate the request body
    val requestBodyResult = request.body.validate[MarkdownRequest]

    requestBodyResult.fold(
      errors => {
        // Return an error response if the JSON is invalid
        BadRequest(Json.obj("message" -> "Invalid JSON payload"))
      },
      payload => {
        Ok(
          Json.toJson(
            converterService.parseMarkdownAndPrintHTML(payload.markdown)
          )
        )
      }
    )
  }
}
