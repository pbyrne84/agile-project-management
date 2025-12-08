package examples.responsibilities

object UserSyncStep2DoneMoreChildProof {

  sealed trait UserId

  case class ManagerId(id: Int)  extends UserId
  case class EngineerId(id: Int) extends UserId

  sealed trait User

  case class EngineerUser(id: EngineerId) extends User

  case class ManagerUser(id: ManagerId) extends User

  trait EngineerUserLookup {
    def getUser(id: EngineerId): Either[Throwable, EngineerUser]
  }

  trait EngineerUserRemoteSync {
    def transmitUser(user: EngineerUser): Either[Throwable, true]
  }

  trait EngineerUserValidation {
    def validateUser(user: EngineerUser): Either[Throwable, true]
  }

  trait ManagerUserLookup {
    def getUser(id: ManagerId): Either[Throwable, ManagerUser]
  }

  trait ManagerUserRemoteSync {
    def transmitUser(user: ManagerUser): Either[Throwable, true]
  }

  trait ManagerUserValidation {
    def validateUser(user: ManagerUser): Either[Throwable, true]
  }

  /** These 2 are now separate and can grow with complexity more cleanly. Importantly, the test for one will never
    * dominate the other. When we have a class with too many responsibilities, and in this case each we have 2 types of
    * users, each with their own call trees and dependencies, and completely different business requirements. We cannot
    * foretell business requirements, we can only leave room for them to be made quite cleanly and easily.
    *
    * People are primarily opportunistic, we can use that to our advantage by leaving good opportunities.
    */
  class EngineerUserSync(
      engineerUserLookup: UserSyncStep2DoneMoreChildProof.EngineerUserLookup,
      engineerUserValidation: UserSyncStep2DoneMoreChildProof.EngineerUserValidation,
      engineerUserRemoteSync: UserSyncStep2DoneMoreChildProof.EngineerUserRemoteSync
  ) {
    def syncEngineer(id: EngineerId): Either[Throwable, true] = {
      for {
        user               <- engineerUserLookup.getUser(id)
        _                  <- engineerUserValidation.validateUser(user)
        transmissionResult <- engineerUserRemoteSync.transmitUser(user)
      } yield transmissionResult
    }
  }

  class ManagerUserSync(
      managerUserLookup: UserSyncStep2DoneMoreChildProof.ManagerUserLookup,
      managerUserValidation: UserSyncStep2DoneMoreChildProof.ManagerUserValidation,
      managerUserRemoteSync: UserSyncStep2DoneMoreChildProof.ManagerUserRemoteSync
  ) {
    def syncManager(id: ManagerId): Either[Throwable, true] = {
      for {
        user               <- managerUserLookup.getUser(id)
        _                  <- managerUserValidation.validateUser(user)
        transmissionResult <- managerUserRemoteSync.transmitUser(user)
      } yield transmissionResult
    }
  }

}

class UserSyncStep2DoneMoreChildProof(
    engineerUserSync: UserSyncStep2DoneMoreChildProof.EngineerUserSync,
    managerUserSync: UserSyncStep2DoneMoreChildProof.ManagerUserSync
) {

  def syncUserById(id: UserSyncStep2DoneMoreChildProof.UserId): Either[Throwable, true] = {
    id match {
      case managerId: UserSyncStep2DoneMoreChildProof.ManagerId   => managerUserSync.syncManager(managerId)
      case engineerId: UserSyncStep2DoneMoreChildProof.EngineerId => engineerUserSync.syncEngineer(engineerId)
    }

  }

}
