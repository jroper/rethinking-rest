package sample.chirper.common.server

import javax.inject.Inject

import com.lightbend.lagom.internal.server.ServiceRouter
import play.api.Configuration
import play.api.mvc.{Results, Action}
import play.api.routing.Router
import play.api.routing.Router.Routes

class KillingRouter(router: Router, kill: Boolean) extends Router {
  @Inject
  def this(router: ServiceRouter, configuration: Configuration) = {
    this(router, configuration.underlying.getBoolean("kill"))
  }

  override def routes: Routes = if (kill) {
    case _ => Action(Results.InternalServerError("KILLED"))
  } else router.routes

  override def withPrefix(prefix: String): Router = new KillingRouter(router.withPrefix(prefix), kill)

  override def documentation: Seq[(String, String, String)] = router.documentation
}