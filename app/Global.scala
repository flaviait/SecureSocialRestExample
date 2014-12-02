import java.lang.reflect.Constructor

import filters.CORSFilter
import models.User
import play.api.GlobalSettings
import play.api.mvc.WithFilters
import securesocial.core.RuntimeEnvironment
import securesocial.core.providers.{FacebookProvider, TwitterProvider}
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

  override def getControllerInstance[A](controllerClass: Class[A]): A = {
    val instance = controllerClass.getConstructors.find { c =>
      val params = c.getParameterTypes
      params.length == 1 && params(0) == classOf[RuntimeEnvironment[User]]
    }.map {
      _.asInstanceOf[Constructor[A]].newInstance(ServiceRuntimeEnvironment)
    }
    instance.getOrElse(super.getControllerInstance(controllerClass))
  }
}