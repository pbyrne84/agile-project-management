# How code organisation affects tests and sustained pace

In agile we do incremental changes on existing work, often bringing in new concepts to coincide with the old. Often, this is done in an opportunistic fashion. People take what they believe is the easiest path, but often is not.
Sustained pace really means sustained energy levels. We have limited energy, so we need to monitor how we
individually are spending it.


## A made up example which follows possibly a common experience
I have come across this multiple times. Not dealt with early when easy, left until it is hard and is causing everyone
problems working in that area.

### Step1 - We have a system that is designed around 1 type.

All the users in the system are actually engineers. We want to sync them across to something. The validation just checks whether they fit some made-up rules. Instead of Throwable as the left concept we would have something more specific.

We just care about the happy path in this example.


#### Code

```scala
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

```

#### Test

We could do an integration test instead of mocking. But it requires more skill for that not to become another effort sink. We should aim to be able to write tests using muscle memory, and poor test organisation kills that. 

Right now it is nice and simple.

```scala
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
```


### Step 2 - We now have a request to sync another type of user, a manager.


#### How most people will do it opportunistically, branch the original code on something like UserType


##### Code

```scala
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

```

##### Test

```scala
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

```

At this point you think no probs, nice and simple. If it gets complicated, we can refactor later. 

Unfortunately, in my experience

1. People don't know what refactoring is, the most common ones are about making the system more humanly communicative,
   and we can just generally be bad at communication. This is a skill barrier, a skill that a lot of people never
   learn unless proactively taught. This knowledge comes from massive amounts of other people's discussed joint 
   experience. It is arrogant to assume we can guess it, but a lot of us assume and make less than ideal guesses.
2. People seem to hate/fear making any changes beyond extracting a method within classes, they will just follow
   existing patterns. Possibly from a fear of blame. 
3. Trying to clean something up when other people may be working in the code is a major headache. Conflict/rebase 
   nightmare. Small refactors can be a massive pull request headache. So this will put people off.


##### Problems we will likely face branching code like this.

1. We have 2 clear responsibilities in this class. This means there are more reasons for other people to be working
   in the same files as us. This increases the chance that we will end up having needless conflict parties as we try
   and push things through PR.
2. We are inviting all future shared logic to be pushed into private methods. As I said, people really seem to be
   against creating new classes and tests. Intellij makes this very easy, in the end it can be much quicker to do it. Definitely quicker to do it early, then try and clean up later. This leads to complicated call chains.

#### How we can organise it

##### Code

```scala
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
```

##### Tests

```scala
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

```
##### How doing this affects how people will do future work

1. 2 People can now work on parallel tickets with diminished tripping over each other. One person can focus on
   Managers, the other Engineers. Fewer needless PR conflicts/rebasing. Getting something approved then having to get
   it reapproved diminishes. Unfortunately, how we organise things can really inhibit the PR process. It is also
   common enough that other branches are added. This sets a pattern that enables this more easily. YAGGNI is not an
   excuse for poor design.
2. Shared logic between the two is more likely to be injected and made separately testable. Small injectable things
   also aid in being able to work with each other with fewer conflicts. It is a game of odds. Having a lot of private
   methods, some shared, some not, makes it far harder to understand/follow the call chain. What is the same between
   the 2 types of processes and what is different should be made clear. It helps us make good design decisions.

##### How to do this split easily
It should take minutes to do the changes. Someone claiming it is too much energy lacks experience in doing this.

Instructions for Intellij

###### Specialise the original UserSyncStep1 to being engineering-based.
1. Navigate to **UserSyncStep1** and press F5 to bring up the copy class prompt. 
2. Give it the specialised name **EngineerUserSync**
3. Navigate to the **UserSyncStep1Spec** and repeat the previous process calling it **EngineerUserSyncSpec**
4. Inject the **EngineerUserSync** into the **UserSync** calling it, delete the unneeded code.
5. Modify the original test. It should be a lot more simple now. We want to try and avoid excessive boundary testing
   across levels of abstraction, it just gets heavier and heavier to do, and it also gets very error-prone leading to
   tests passing for the wrong reasons.
