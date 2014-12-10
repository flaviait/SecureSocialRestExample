package controllers

import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.json._
import play.api.mvc._
import play.modules.reactivemongo.MongoController
import play.modules.reactivemongo.json.collection.JSONCollection
import securesocial.core.{RuntimeEnvironment, SecureSocial}

import scala.concurrent.Future

/**
 * Author: Dennis Fricke
 * Date: 23.09.2014
 */
class Product(override implicit val env: RuntimeEnvironment[models.User]) extends Controller with MongoController with SecureSocial[models.User] {
  def collection = db.collection[JSONCollection]("products")

  /**
   * Create a new product
   * @return
   */
  def create = SecuredAction.async(parse.json) { request =>
    request.body.validate[models.Product].map {
      case product => {
        val futureResult = collection.save(product)
        futureResult.map {
          case t => t.inError match {
            case true => InternalServerError("%s".format(t))
            case false => Ok(Json.toJson(product))
          }
        }
      }
    }.recoverTotal {
      e => Future {
        BadRequest(JsError.toFlatJson(e))
      }
    }
  }

  /**
   * List all products
   * @return
   */
  def list() = SecuredAction.async { request =>
    val cursor = collection.find(Json.obj()).cursor[models.Product]
    val futureResults = cursor.collect[List]()
    futureResults.map {
      case t => Ok(Json.toJson(t))
    }
  }

  /**
   * Get a product by id
   * @param id
   * @return
   */
  def find(id: String) = SecuredAction.async(parse.anyContent) { request =>
    val futureResults: Future[Option[models.Product]] = collection.find(Json.obj("_id" -> Json.obj("$oid" -> id))).one[models.Product]
    futureResults.map {
      case t => Ok(Json.toJson(t))
    }
  }

  /**
   * Update a product by id
   * @param id
   * @return
   */
  def update(id: String) = SecuredAction.async(parse.json) { request =>
    request.body.validate[models.Product].map {
      case product => {
        val futureResult = collection.update(Json.obj("_id" -> Json.obj("$oid" -> id)), product)
        futureResult.map {
          case t => t.inError match {
            case true => InternalServerError("%s".format(t))
            case false => Ok(Json.toJson(product))
          }
        }
      }
    }.recoverTotal {
      e => Future {
        BadRequest(JsError.toFlatJson(e))
      }
    }
  }

  /**
   * Delete a product by id
   * @param id
   * @return
   */
  def delete(id: String) = SecuredAction.async(parse.anyContent) { request =>
    val futureResult = collection.remove(Json.obj("_id" -> Json.obj("$oid" -> id)), firstMatchOnly = true)
    futureResult.map {
      case t => t.inError match {
        case true => InternalServerError("%s".format(t))
        case false => Ok("success")
      }
    }
  }

}
