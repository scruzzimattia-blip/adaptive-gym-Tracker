# Adaptive Gym Tracker

A production-ready Android application built with Kotlin, MVVM, Room Database, and Jetpack Compose. The app tracks workouts and implements an intelligent **Adaptive Workout Engine** to auto-adjust weight progression based on user performance.

## Setup Instructions

1. Ensure **Android Studio (Giraffe | 2023.1.1 or newer)** is installed on your computer.
2. Open Android Studio and select **Open**.
3. Navigate to the `Adaptive-Gym-Tracker` folder and select it.
4. Android Studio will automatically recognize the `settings.gradle.kts` file and begin Gradle Sync. It will also download the required Gradle Wrapper.
5. Once the sync is complete, press **Run (Shift + F10)** to install the app on an Android Emulator or a physical device.

## Application Architecture

- **UI Layer:** Built exclusively with Material 3 Jetpack Compose for modern, reactive interfaces. Components reside in `ui/screens`.
- **Presentation Layer:** Managed by Android `ViewModel` utilizing Kotlin `StateFlow` (`ui/viewmodels`).
- **Domain Layer:** Contains core business logic without Android dependencies. `AdaptiveEngine.kt` resides here.
- **Data Layer:** Handled by Room Database mapped via `AppDatabase`, `WorkoutDao`, and wrapped by the `WorkoutRepository`.

## Explanation of the Adaptive Algorithm

The `AdaptiveEngine` (located in `domain/AdaptiveEngine.kt`) takes the user's recent workout sets and overarching goals to calculate progression or regression steps.
- **Progression:** If a user completes all sets of an exercise seamlessly (reaching target reps without reaching failure), the algorithm increases the upcoming weight by +2.5kg (or +1.25kg for intermediate/advanced lifters).
- **Deload / Regression:** If the user logs multiple failures on a specific set, the algorithm slightly drops the weight to encourage form recovery.
- **Fatigue Tracking:** Based on max Rating of Perceived Exertion (RPE) > 8, if there's less than 48 hours of rest, the engine suggests a "Rest Day" instead of rotating to the next body part (Push/Pull/Legs).

## Generating an APK / AAB for the Google Play Store (Manual)

When you are ready to publish manually:
1. In Android Studio, go to the top menu bar.
2. Click **Build** -> **Generate Signed Bundle / APK...**
3. Select **Android App Bundle (.aab)**.
4. Click **Next**, create a keystore, and build the release variant.

## 🤖 Continuous Integration / Continuous Deployment (GitHub Actions)

The project includes a fully robust CI/CD pipeline located in `.github/workflows/ci.yml`. It ensures the code is always in a releasable state and automates versioning and builds.

### Pipeline Triggers
- **Push & Pull Requests** on `main` and `develop` branches.

### Automated Steps
1. **Quality Checks:** Runs Android Lint against your code to detect structural issues and verifies logic via unit tests.
2. **Build Generation:** Compiles a **Debug APK** automatically so testers can download it straight from the GitHub Actions dashboard without compiling it themselves.
3. **Production Publishing:** Compiles a production-ready **Release AAB** (Android App Bundle).
4. **Versioning Automation:** The app's `versionCode` and `versionName` automatically increment using the `GITHUB_RUN_NUMBER`.

### 🔑 Setting up Secrets for Release
If the pipeline does not detect Keystore secrets, it will generate an *unsigned* Release AAB. To fully automate Google Play Store deployments, define the following variables in your **GitHub Repository Settings -> Secrets and Variables -> Actions**:
- `SIGNING_KEYSTORE_BASE64`: A Base64-encoded string of your `release.keystore` file. *(Command to yield string on your local machine: `base64 release.keystore > base64.txt`)*
- `KEYSTORE_PASSWORD`: The password for your keystore.
- `KEY_ALIAS`: The alias for your signing key.
- `KEY_PASSWORD`: The password for the specific signing key.

### Downloading Artifacts
1. Go to your repository on GitHub and click the **Actions** tab.
2. Click on the latest successful workflow run.
3. Scroll down to the **Artifacts** section at the bottom.
4. Click on `debug-apk` or `release-aab` to download the zip file containing the compiled application.

## Optional Firebase Integration

To add cloud sync:
1. Go to Tools -> Firebase in Android Studio.
2. Select "Authentication" -> "Connect to Firebase".
3. Add the `google-services.json` to the `app/` directory and apply the `com.google.gms.google-services` plugin.
