package examples.responsibilities

import examples.responsibilities.UserSyncStep2DoneMoreChildProof._
import org.scalamock.scalatest.MockFactory
import org.scalatest.freespec.AnyFreeSpecLike
import org.scalatest.matchers.should.Matchers.*

class EngineerUserSyncSpec extends AnyFreeSpecLike with MockFactory {

  private val mockEngineerUserLookup: EngineerUserLookup         = mock[EngineerUserLookup]
  private val mockEngineerUserValidation: EngineerUserValidation = mock[EngineerUserValidation]
  private val mockEngineerUserRemoteSync: EngineerUserRemoteSync = mock[EngineerUserRemoteSync]
  private val engineerUserSync = new EngineerUserSync(
    mockEngineerUserLookup,
    mockEngineerUserValidation,
    mockEngineerUserRemoteSync
  )

  "syncEngineer" - {
    "should sync an engineer" in {
      val engineerId = EngineerId(22)
      val foundUser  = EngineerUser(engineerId)

      mockEngineerUserLookup.getUser
        .expects(engineerId)
        .returning(Right(foundUser))

      mockEngineerUserValidation.validateUser
        .expects(foundUser)
        .returning(Right(true))

      mockEngineerUserRemoteSync.transmitUser
        .expects(foundUser)
        .returning(Right(true))

      engineerUserSync.syncEngineer(engineerId) shouldBe Right(true)
    }
  }
}

class ManagerUserSyncSpec extends AnyFreeSpecLike with MockFactory {

  private val mockManagerUserLookup: ManagerUserLookup         = mock[ManagerUserLookup]
  private val mockManagerUserValidation: ManagerUserValidation = mock[ManagerUserValidation]
  private val mockManagerUserRemoteSync: ManagerUserRemoteSync = mock[ManagerUserRemoteSync]

  private val managerUserSync = new ManagerUserSync(
    mockManagerUserLookup,
    mockManagerUserValidation,
    mockManagerUserRemoteSync
  )

  "syncManager" - {
    "should sync a manager" in {
      val managerId = ManagerId(22)
      val foundUser = ManagerUser(managerId)

      mockManagerUserLookup.getUser
        .expects(managerId)
        .returning(Right(foundUser))

      mockManagerUserValidation.validateUser
        .expects(foundUser)
        .returning(Right(true))

      mockManagerUserRemoteSync.transmitUser
        .expects(foundUser)
        .returning(Right(true))

      managerUserSync.syncManager(managerId) shouldBe Right(true)
    }
  }
}

class UserSyncStep2DoneMoreChildProofSpec extends AnyFreeSpecLike with MockFactory {

  private val mockEngineerUserSync: EngineerUserSync = mock[EngineerUserSync]
  private val mockManagerUserSync: ManagerUserSync   = mock[ManagerUserSync]

  private val userSyncStep2DoneMoreChildProof =
    new UserSyncStep2DoneMoreChildProof(mockEngineerUserSync, mockManagerUserSync)

  "syncUserById" - {

    "should sync a manager if the id is a manager id" in {
      val managerId = ManagerId(33)

      mockManagerUserSync.syncManager
        .expects(managerId)
        .returning(Right(true))

      userSyncStep2DoneMoreChildProof.syncUserById(managerId) shouldBe Right(true)
    }

    "should sync an engineer if the id is an engineer id" in {
      val engineerId = EngineerId(33)

      mockEngineerUserSync.syncEngineer
        .expects(engineerId)
        .returning(Right(true))

      userSyncStep2DoneMoreChildProof.syncUserById(engineerId) shouldBe Right(true)
    }

  }

}
