package examples.responsibilities

import examples.responsibilities.Step2Dependencies.UserType

/** We have a feature request come in, we now have different types of user requiring a different sync process. We are
  * lazy in the worst way, setting up pain later. If we don't like splitting things up when it is easy, we definitely
  * don't like doing it when hard. It is really easy to do now, just need to know how to use your tooling.
  *
  * We were just syncing Engineers, now managers feel left out so we need to sync them.
  *
  * The following code is poorly done procedural. We just look for the easiest place to bung the code and care little
  * about design and how it can grow. Experienced devs need to leave room for the less experienced devs to not cause
  * massive quality drops easily. This is all maths/probabilities and hedging bets.
  */

object Step2Dependencies {
  sealed trait User

  case class EngineerUser(id: Int) extends User
  case class ManagerUser(id: Int)  extends User

  trait EngineerUserLookup {
    def getUser(id: Int): Either[Throwable, EngineerUser]
  }

  trait EngineerUserRemoteSync {
    def transmitUser(user: EngineerUser): Either[Throwable, true]
  }

  trait EngineerUserValidation {
    def validateUser(user: EngineerUser): Either[Throwable, true]
  }

  trait ManagerUserLookup {
    def getUser(id: Int): Either[Throwable, ManagerUser]
  }

  trait ManagerUserRemoteSync {
    def transmitUser(user: ManagerUser): Either[Throwable, true]
  }

  trait ManagerUserValidation {
    def validateUser(user: ManagerUser): Either[Throwable, true]
  }

  sealed trait UserType

  case object Management extends UserType
  case object Engineer   extends UserType

}

class UserSyncStep2DoneBadly(
    engineerUserLookup: Step2Dependencies.EngineerUserLookup,
    engineerUserValidation: Step2Dependencies.EngineerUserValidation,
    engineerUserRemoteSync: Step2Dependencies.EngineerUserRemoteSync,
    managerUserLookup: Step2Dependencies.ManagerUserLookup,
    managerUserValidation: Step2Dependencies.ManagerUserValidation,
    managerUserRemoteSync: Step2Dependencies.ManagerUserRemoteSync
) {

  def syncUserById(id: Int, userType: UserType): Either[Throwable, true] = {
    userType match {
      case Step2Dependencies.Management => syncManager(id)
      case Step2Dependencies.Engineer   => syncEngineer(id)
    }
  }

  /** At this point these methods look similar, but as new requests come in, they will deviate. Plus when a third type
    * comes in, as life is Murphy's law "What can go wrong will go wrong", things will get worse. it becomes a game of
    * who can hold their nose the longest.
    */
  private def syncManager(id: Int): Either[Throwable, true] = {
    for {
      user               <- managerUserLookup.getUser(id)
      _                  <- managerUserValidation.validateUser(user)
      transmissionResult <- managerUserRemoteSync.transmitUser(user)
    } yield transmissionResult
  }

  private def syncEngineer(id: Int): Either[Throwable, true] = {
    for {
      user               <- engineerUserLookup.getUser(id)
      _                  <- engineerUserValidation.validateUser(user)
      transmissionResult <- engineerUserRemoteSync.transmitUser(user)
    } yield transmissionResult
  }

}
