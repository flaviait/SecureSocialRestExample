package models

/**
 * Author: Dennis Fricke
 * Date: 31.10.2014
 */
case class Point(latitude: Double, longitude: Double);

object Point {
	import play.api.libs.json._
	import play.modules.reactivemongo.json.BSONFormats._

	implicit val pointReads = Json.reads[Point]
	implicit val pointWrites = Json.writes[Point]
}
