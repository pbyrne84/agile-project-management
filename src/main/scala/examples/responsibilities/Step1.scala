package examples.responsibilities

object Step1Dependencies {
  case class User(id: Int)

  trait UserLookup {
    def getUser(id: Int): Either[Throwable, User]
  }

  trait UserRemoteSync {
    def transmitUser(user: User): Either[Throwable, true]
  }

  trait UserValidation {
    def validateUser(user: User): Either[Throwable, true]
  }

}

class UserSyncStep1(
    userLookup: Step1Dependencies.UserLookup,
    userValidation: Step1Dependencies.UserValidation,
    userRemoteSync: Step1Dependencies.UserRemoteSync
) {

  def syncUserById(id: Int): Either[Throwable, true] = {
    for {
      user               <- userLookup.getUser(id)
      _                  <- userValidation.validateUser(user)
      transmissionResult <- userRemoteSync.transmitUser(user)
    } yield transmissionResult
  }

}
