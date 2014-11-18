package models

import reactivemongo.bson.BSONObjectID
import securesocial.core._

/**
 * Copyright © 2014 dreamr.io. All Rights reserved
 * Author: Dennis Fricke
 * Date: 02.10.2014
 */
case class User(_id: Option[BSONObjectID],
								main: BasicProfile,
							  identities: List[BasicProfile])

object User {
	import play.api.libs.json._
	import play.modules.reactivemongo.json.BSONFormats._

	implicit val AuthenticationMethodFormat = Json.format[AuthenticationMethod]
	implicit val OAuth1InfoFormat = Json.format[OAuth1Info]
	implicit val OAuth2InfoFormat = Json.format[OAuth2Info]
	implicit val PasswordInfoFormat = Json.format[PasswordInfo]

	implicit val basicProfileReads = Json.reads[BasicProfile]
	implicit val basicProfileWrites = Json.writes[BasicProfile]

	implicit val reads = Json.reads[User]
	implicit val writes = Json.writes[User]
}