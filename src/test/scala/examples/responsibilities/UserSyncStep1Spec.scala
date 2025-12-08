package examples.responsibilities

import org.scalamock.scalatest.MockFactory
import org.scalatest.freespec.AnyFreeSpecLike
import org.scalatest.matchers.should.Matchers._

class UserSyncStep1Spec extends AnyFreeSpecLike with MockFactory {

  private val userLookup: Step1Dependencies.UserLookup         = mock[Step1Dependencies.UserLookup]
  private val userValidation: Step1Dependencies.UserValidation = mock[Step1Dependencies.UserValidation]
  private val userRemoteSync: Step1Dependencies.UserRemoteSync = mock[Step1Dependencies.UserRemoteSync]

  private val userSyncStep1 = new UserSyncStep1(userLookup, userValidation, userRemoteSync)

  "syncUserById" - {
    "should sync a user" in {
      val userid    = 22
      val foundUser = Step1Dependencies.User(userid)

      userLookup.getUser
        .expects(userid)
        .returning(Right(foundUser))

      userValidation.validateUser
        .expects(foundUser)
        .returning(Right(true))

      userRemoteSync.transmitUser
        .expects(foundUser)
        .returning(Right(true))

      userSyncStep1.syncUserById(userid) shouldBe Right(true)
    }
  }
}
