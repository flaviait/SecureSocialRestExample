import filters.CORSFilter
import models.User
import play.api.GlobalSettings
import play.api.mvc.WithFilters
import securesocial.controllers.routes
import securesocial.core.RuntimeEnvironment
import securesocial.core.providers.{TwitterProvider, FacebookProvider}
import services.InMemoryUserService

import scala.collection.immutable.ListMap

/**
 * Author: Dennis Fricke
 * Date: 22.10.2014
 */
object Global extends WithFilters(CORSFilter()) with GlobalSettings {
	object ServiceRuntimeEnvironment extends RuntimeEnvironment.Default[User] {
		override lazy val userService: InMemoryUserService = new InMemoryUserService
		override lazy val providers = ListMap(
			include(new FacebookProvider(routes, cacheService, oauth2ClientFor(FacebookProvider.Facebook))),
			include(new TwitterProvider(routes, cacheService, oauth1ClientFor(TwitterProvider.Twitter)))
		)
	}
}