package examples.responsibilities

import examples.responsibilities.Step2Dependencies.{Engineer, EngineerUser, Management, ManagerUser}
import org.scalamock.scalatest.MockFactory
import org.scalatest.freespec.AnyFreeSpecLike
import org.scalatest.matchers.should.Matchers.*

/** This looks nice and light, but only because the complication that will come has not set in. Devs, and often
  * including senior ones, don't take into account that most people tend to not deviate from an existing pattern. This
  * is not just due to knowledge, but people can have accrued a lot of criticism in their lives so going with the flow
  * is less likely to cause them social problems. That is not a good working environment to foster, so we should help
  * people feel comfortable making design changes, else we end up with a large shared headache.z
  */
class UserSyncStep2DoneBadlySpec extends AnyFreeSpecLike with MockFactory {

  private val managerUserLookup     = mock[Step2Dependencies.ManagerUserLookup]
  private val managerUserValidation = mock[Step2Dependencies.ManagerUserValidation]
  private val managerUserRemoteSync = mock[Step2Dependencies.ManagerUserRemoteSync]

  private val engineerUserLookup     = mock[Step2Dependencies.EngineerUserLookup]
  private val engineerUserValidation = mock[Step2Dependencies.EngineerUserValidation]
  private val engineerUserRemoteSync = mock[Step2Dependencies.EngineerUserRemoteSync]

  private val userSyncStep2DoneBadly =
    new UserSyncStep2DoneBadly(
      engineerUserLookup,
      engineerUserValidation,
      engineerUserRemoteSync,
      managerUserLookup,
      managerUserValidation,
      managerUserRemoteSync
    )

  "syncUserById" - {

    "should sync a user when it is an engineer" in {
      val userid    = 22
      val foundUser = EngineerUser(userid)

      engineerUserLookup.getUser
        .expects(userid)
        .returning(Right(foundUser))

      engineerUserValidation.validateUser
        .expects(foundUser)
        .returning(Right(true))

      engineerUserRemoteSync.transmitUser
        .expects(foundUser)
        .returning(Right(true))

      userSyncStep2DoneBadly.syncUserById(userid, Engineer) shouldBe Right(true)
    }

    "should sync a user when it is an manager" in {
      val userid    = 22
      val foundUser = ManagerUser(userid)

      managerUserLookup.getUser
        .expects(userid)
        .returning(Right(foundUser))

      managerUserValidation.validateUser
        .expects(foundUser)
        .returning(Right(true))

      managerUserRemoteSync.transmitUser
        .expects(foundUser)
        .returning(Right(true))

      userSyncStep2DoneBadly.syncUserById(userid, Management) shouldBe Right(true)
    }

  }
}
