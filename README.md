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

## Generating an APK / AAB for the Google Play Store

When you are ready to publish:
1. In Android Studio, go to the top menu bar.
2. Click **Build** -> **Generate Signed Bundle / APK...**
3. Select **Android App Bundle (.aab)**. This is the required format for new apps on the Google Play Store.
4. Click **Next**.
5. Under **Key store path**, click `Create new...` and fill out the keystore details (password, alias, and certificate details). Keep this `.jks` file extremely safe, as losing it means you can never update the app again.
6. Select the **release** build variant and click **Finish**.
7. Once Gradle signs the app, click **locate** in the pop-up notification. The resulting `.aab` file can be directly uploaded to the Google Play Console under "Production" or "Internal Testing".

## Optional Firebase Integration

To add cloud sync:
1. Go to Tools -> Firebase in Android Studio.
2. Select "Authentication" -> "Connect to Firebase".
3. Add the `google-services.json` to the `app/` directory and apply the `com.google.gms.google-services` plugin.
