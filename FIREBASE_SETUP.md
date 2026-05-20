# Firebase setup for PetPulse

PetPulse now uses Firebase Authentication, Cloud Firestore and Cloud Storage.

## 1. Android app

The Android package must match:

```text
co.edu.unab.spfbayterangarita.petpulse
```

Place the downloaded Firebase config file here:

```text
app/google-services.json
```

## 2. Authentication

In Firebase Console:

1. Open **Authentication**.
2. Go to **Sign-in method**.
3. Enable **Email/Password**.

The app starts at the login/register screen. After login, data is stored under the signed-in user's Firebase Auth `uid`.

Password recovery uses Firebase Auth password reset emails. Make sure the Firebase Authentication email templates are enabled in Console.

## 3. Firestore structure

PetPulse writes user data like this:

```text
users/{uid}/pets/{petId}
users/{uid}/appointments/{appointmentId}
```

Suggested development rules:

```text
rules_version = '2';

service cloud.firestore {
  match /databases/{database}/documents {
    match /users/{userId}/{document=**} {
      allow read, write: if request.auth != null && request.auth.uid == userId;
    }
  }
}
```

## 4. Storage structure

Pet profile photos are uploaded here:

```text
users/{uid}/pets/{petId}/profile.jpg
```

Suggested development rules:

```text
rules_version = '2';

service firebase.storage {
  match /b/{bucket}/o {
    match /users/{userId}/{allPaths=**} {
      allow read, write: if request.auth != null && request.auth.uid == userId;
    }
  }
}
```

## 5. Verify

Run:

```powershell
.\gradlew.bat :app:assembleDebug
```

Then create a Firebase account in the app, add a pet, take a photo, and verify:

```text
Firestore: users/{uid}/pets
Storage: users/{uid}/pets/{petId}/profile.jpg
```

## 6. Notifications

Agenda reminders are local Android notifications scheduled with WorkManager.

The app asks for the `POST_NOTIFICATIONS` permission on Android 13+. Firestore stores the event timestamp in:

```text
scheduledAtMillis
```

The local reminder is scheduled before the event:

- 24 hours before, when possible.
- 1 hour before, if the event is less than 24 hours away.
- 15 minutes before, if the event is less than 1 hour away.

## 7. AI chatbot

Do not put an OpenAI API key directly in the Android app.

Recommended architecture:

```text
Android app -> Firebase Cloud Function / backend -> OpenAI API
```

The backend should read the user's pet logs from Firestore, build a safe prompt, call OpenAI, and return the answer to the app. This keeps the API key private and lets you add abuse limits, logging and medical disclaimers.
