package computerdatabase

import scala.concurrent.duration._
import java.util.concurrent.ThreadLocalRandom

import io.gatling.core.Predef._
import io.gatling.core.structure.ChainBuilder
import io.gatling.http.Predef._

class ComputerWorld extends Simulation {
  val httpProtocol = http
      .baseUrl("https://apis-staging.hellobetter.de")

  val headers_0 = Map(
    "Content-Type" -> "application/json")

  object login{
    val feeder = csv("sample.csv").circular
    val login = feed(feeder)
      .exec(http("Service")
        .post("/api/v1/login")
        .headers(headers_0)
        .body(StringBody(
        """{"email":"${eml}","password":"${pwd}"}""")
        )
        .check(bodyString.saveAs("BODY"))
        .check(status is 200)
      ).exec(session => {
      val response = session("BODY").as[String]
      println(response)
      session
    })
  }

  val scn=scenario("login")
    .exec(login.login)

  setUp(scn.inject(
    constantUsersPerSec(1) during(1 second)
  )).protocols(httpProtocol)
}
