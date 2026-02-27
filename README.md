# Maya-Martin-Avery (Android)

Android app built with Kotlin + Jetpack Compose.

## Requirements

- **Android Studio**: a recent stable version (compatible with **Android Gradle Plugin 8.9.1**)
- **JDK**: **11** (this project targets JVM 11)
- **Android SDK**:
  - **compileSdk / targetSdk**: 35
  - **minSdk**: 24

## Run the app (Android Studio)

1. Open the project folder in Android Studio.
2. If prompted, let Android Studio import/sync the Gradle project.
3. Select an emulator or a connected device.
4. Click **Run** (the green play button).

## Run the app (Gradle CLI)

### macOS / Linux

```bash
./gradlew :app:assembleDebug
./gradlew :app:installDebug
```

### Windows (PowerShell)

```powershell
.\gradlew.bat :app:assembleDebug
.\gradlew.bat :app:installDebug
```

## Default credentials

The app seeds a default user on first run:

- **Username**: `admin`
- **Password**: `admin`

## Run local unit tests

Local unit tests live under `app/src/test/` and run on the JVM (no emulator/device required).

### Android Studio

- Open a test file in `app/src/test/`, then click the **Run** gutter icon next to a test/class, or
- Use **Gradle** tool window → `:app` → `Tasks` → `verification` → `testDebugUnitTest`.

### Gradle CLI

#### macOS / Linux

```bash
./gradlew :app:testDebugUnitTest
```

#### Windows (PowerShell)

```powershell
.\gradlew.bat :app:testDebugUnitTest
```

### Test reports

- **HTML report**: `app/build/reports/tests/testDebugUnitTest/index.html`
- **XML results**: `app/build/test-results/testDebugUnitTest/`

## Troubleshooting

- **Gradle sync fails / wrong Java version**: make sure Android Studio / your terminal is using **JDK 11**.
- **Clean build**:
  - macOS / Linux: `./gradlew clean`
  - Windows: `.\gradlew.bat clean`

## Design documentation

The app follows a simple layered architecture:

- **Presentation**: Jetpack Compose screens + `ViewModel`s
- **Domain**: use cases + repository interfaces + domain models
- **Data**: Room (local persistence) + Retrofit (remote sample API) + repository implementations

### Class diagram

```mermaid
classDiagram
direction LR

class MainActivity
class AppNavGraph

class StartViewModel
class LoginViewModel
class WalletViewModel
class SendMoneyViewModel
class TransactionsViewModel

class GetCurrentUserIdUseCase
class SaveCurrentUserIdUseCase
class ClearPreferencesUseCase
class SeedDefaultUserUseCase
class LoginUseCase
class GetWalletByUserIdUseCase
class SendMoneyUseCase
class ObserveLocalTransactionsUseCase
class SaveLocalTransactionUseCase
class GetRemoteTransactionsUseCase
class PostTransactionUseCase

class CurrentUserPreferences
class CurrentUserPreferencesImpl
class SharedPreferences

class UserRepository
class WalletRepository
class TransactionRepository

class UserRepositoryImpl
class WalletRepositoryImpl
class TransactionRepositoryImpl

class AppDatabase
class UserDao
class WalletDao
class TransactionDao

class UserEntity
class WalletEntity
class TransactionEntity

class UserMapper
class WalletMapper

class TransactionsApi
class RemoteTransactionDto
class PostRemoteTransactionBodyDto
class Retrofit
class OkHttpClient

class RepositoryModule
class DatabaseModule
class NetworkModule
class PreferencesModule

MainActivity --> AppNavGraph

StartViewModel --> GetCurrentUserIdUseCase
LoginViewModel --> LoginUseCase
LoginViewModel --> SaveCurrentUserIdUseCase
WalletViewModel --> GetWalletByUserIdUseCase
WalletViewModel --> GetCurrentUserIdUseCase
WalletViewModel --> ClearPreferencesUseCase
SendMoneyViewModel --> GetCurrentUserIdUseCase
SendMoneyViewModel --> GetWalletByUserIdUseCase
SendMoneyViewModel --> SendMoneyUseCase
SendMoneyViewModel --> SaveLocalTransactionUseCase
SendMoneyViewModel --> PostTransactionUseCase
TransactionsViewModel --> GetCurrentUserIdUseCase
TransactionsViewModel --> ObserveLocalTransactionsUseCase
TransactionsViewModel --> GetRemoteTransactionsUseCase

GetCurrentUserIdUseCase --> CurrentUserPreferences
SaveCurrentUserIdUseCase --> CurrentUserPreferences
ClearPreferencesUseCase --> CurrentUserPreferences

SeedDefaultUserUseCase --> UserRepository
LoginUseCase --> UserRepository
GetWalletByUserIdUseCase --> WalletRepository
SendMoneyUseCase --> WalletRepository
ObserveLocalTransactionsUseCase --> TransactionRepository
SaveLocalTransactionUseCase --> TransactionRepository
GetRemoteTransactionsUseCase --> TransactionRepository
PostTransactionUseCase --> TransactionRepository

CurrentUserPreferences <|.. CurrentUserPreferencesImpl
CurrentUserPreferencesImpl --> SharedPreferences

UserRepository <|.. UserRepositoryImpl
WalletRepository <|.. WalletRepositoryImpl
TransactionRepository <|.. TransactionRepositoryImpl

UserRepositoryImpl --> UserDao
UserRepositoryImpl --> WalletDao
UserRepositoryImpl --> UserMapper
WalletRepositoryImpl --> WalletDao
WalletRepositoryImpl --> WalletMapper
TransactionRepositoryImpl --> TransactionDao
TransactionRepositoryImpl --> TransactionsApi

AppDatabase --> UserDao
AppDatabase --> WalletDao
AppDatabase --> TransactionDao

TransactionDao --> TransactionEntity
WalletDao --> WalletEntity
UserDao --> UserEntity

TransactionsApi --> RemoteTransactionDto
TransactionsApi --> PostRemoteTransactionBodyDto
NetworkModule --> Retrofit
NetworkModule --> OkHttpClient
NetworkModule --> TransactionsApi

DatabaseModule --> AppDatabase
DatabaseModule --> UserDao
DatabaseModule --> WalletDao
DatabaseModule --> TransactionDao

PreferencesModule --> SharedPreferences

RepositoryModule ..> CurrentUserPreferencesImpl : binds
RepositoryModule ..> UserRepositoryImpl : binds
RepositoryModule ..> WalletRepositoryImpl : binds
RepositoryModule ..> TransactionRepositoryImpl : binds
```

### Sequence diagrams

#### Send money flow

```mermaid
sequenceDiagram
autonumber

actor User
participant SendMoneyScreen
participant SendMoneyViewModel
participant GetCurrentUserIdUseCase
participant SendMoneyUseCase
participant WalletRepository as "WalletRepository (WalletRepositoryImpl)"
participant WalletDao
participant SaveLocalTransactionUseCase
participant TransactionRepository as "TransactionRepository (TransactionRepositoryImpl)"
participant TransactionDao
participant PostTransactionUseCase
participant TransactionsApi

User ->> SendMoneyScreen: Tap "Submit"
SendMoneyScreen ->> SendMoneyViewModel: onSubmit()
SendMoneyViewModel ->> GetCurrentUserIdUseCase: invoke()
GetCurrentUserIdUseCase -->> SendMoneyViewModel: userId?

alt Invalid_input_or_no_user
  SendMoneyViewModel -->> SendMoneyScreen: uiState.sheet = Failure
else Valid_input_and_userId_present
  SendMoneyViewModel ->> SendMoneyUseCase: invoke(userId, amount)
  SendMoneyUseCase ->> WalletRepository: getWalletByUserId(userId)
  WalletRepository ->> WalletDao: getWalletByUserId(userId)
  WalletDao -->> WalletRepository: WalletEntity?
  WalletRepository -->> SendMoneyUseCase: Result(WalletDomain)
  SendMoneyUseCase ->> WalletRepository: updateWalletBalance(userId, newBalance)
  WalletRepository ->> WalletDao: updateWalletBalance(userId, newBalance)
  WalletDao -->> WalletRepository: rowsUpdated
  WalletRepository -->> SendMoneyUseCase: Result(Unit)
  SendMoneyUseCase -->> SendMoneyViewModel: Result(Double) newBalance

  opt Local_history_persisted_on_success
    SendMoneyViewModel ->> SaveLocalTransactionUseCase: invoke(userId, amount, description, createdAtEpochMs)
    SaveLocalTransactionUseCase ->> TransactionRepository: saveLocalSentTransaction(...)
    TransactionRepository ->> TransactionDao: insert(TransactionEntity)
    TransactionDao -->> TransactionRepository: rowId
    TransactionRepository -->> SaveLocalTransactionUseCase: Result(Unit)
  end

  opt Best_effort_remote_POST
    SendMoneyViewModel ->> PostTransactionUseCase: invoke(userId, amount, description)
    PostTransactionUseCase ->> TransactionRepository: postRemoteSampleTransaction(...)
    TransactionRepository ->> TransactionsApi: postTransaction(PostRemoteTransactionBodyDto)
    TransactionsApi -->> TransactionRepository: RemoteTransactionDto
    TransactionRepository -->> PostTransactionUseCase: Result(Unit)
  end

  SendMoneyViewModel -->> SendMoneyScreen: uiState.sheet = Success
end
```

#### Wallet load / refresh flow

```mermaid
sequenceDiagram
autonumber

actor User
participant WalletScreen
participant WalletViewModel
participant GetCurrentUserIdUseCase
participant GetWalletByUserIdUseCase
participant WalletRepository as "WalletRepository (WalletRepositoryImpl)"
participant WalletDao

User ->> WalletScreen: Open screen / pull-to-refresh
WalletScreen ->> WalletViewModel: init / refreshWallet()
WalletViewModel ->> GetCurrentUserIdUseCase: invoke()
GetCurrentUserIdUseCase -->> WalletViewModel: userId?

alt No_current_user
  WalletViewModel -->> WalletScreen: uiState.errorMessage set
else Has_current_user
  WalletViewModel ->> GetWalletByUserIdUseCase: invoke(userId)
  GetWalletByUserIdUseCase ->> WalletRepository: getWalletByUserId(userId)
  WalletRepository ->> WalletDao: getWalletByUserId(userId)
  WalletDao -->> WalletRepository: WalletEntity?
  WalletRepository -->> GetWalletByUserIdUseCase: Result(WalletDomain)
  GetWalletByUserIdUseCase -->> WalletViewModel: Result(WalletDomain)
  WalletViewModel -->> WalletScreen: uiState.balance updated
end
```

#### Transactions screen (local + remote)

```mermaid
sequenceDiagram
autonumber

actor User
participant TransactionsScreen
participant TransactionsViewModel
participant GetCurrentUserIdUseCase
participant ObserveLocalTransactionsUseCase
participant GetRemoteTransactionsUseCase
participant TransactionRepository as "TransactionRepository (TransactionRepositoryImpl)"
participant TransactionDao
participant TransactionsApi

User ->> TransactionsScreen: Open screen / tap refresh
TransactionsScreen ->> TransactionsViewModel: init / refreshRemote()

par Subscribe_local_transactions
  TransactionsViewModel ->> GetCurrentUserIdUseCase: invoke()
  GetCurrentUserIdUseCase -->> TransactionsViewModel: userId?
  alt No_current_user
    TransactionsViewModel -->> TransactionsScreen: uiState.screenErrorMessage set
  else Has_current_user
    TransactionsViewModel ->> ObserveLocalTransactionsUseCase: invoke(userId)
    ObserveLocalTransactionsUseCase ->> TransactionRepository: observeLocalTransactions(userId)
    TransactionRepository ->> TransactionDao: observeByUserId(userId)
    loop As_database_emits_updates
      TransactionDao -->> TransactionsViewModel: Flow(List(TransactionEntity))
      TransactionsViewModel -->> TransactionsScreen: uiState.localTransactions updated
    end
  end
and Refresh_remote_sample_transactions
  TransactionsViewModel ->> GetRemoteTransactionsUseCase: invoke()
  GetRemoteTransactionsUseCase ->> TransactionRepository: getRemoteSampleTransactions()
  TransactionRepository ->> TransactionsApi: getTransactions()
  TransactionsApi -->> TransactionRepository: List(RemoteTransactionDto)
  TransactionRepository -->> GetRemoteTransactionsUseCase: Result(List(RemoteSample))
  GetRemoteTransactionsUseCase -->> TransactionsViewModel: Result(List(RemoteSample))
  TransactionsViewModel -->> TransactionsScreen: uiState.remoteTransactions updated
end
```

