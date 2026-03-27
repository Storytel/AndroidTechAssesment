# Storytel Android Tech Assessment

Welcome, and thanks for taking the time to do this!

## About this project

This is a small podcast app that fetches RSS feeds, stores episodes in a local database, and displays them in a list. It is intentionally simple — the purpose is to give us a shared codebase to work from during the interview, not to showcase a production-quality application.

**Please note:** The architecture, patterns, and code style in this project do not reflect how we build things at Storytel. Our production app is significantly larger and follows different conventions. You will not be judged on whether you recognise our internal patterns — only on how you reason about and navigate the code in front of you.

## Tech stack

- **Language:** Kotlin
- **UI:** Jetpack Compose
- **Database:** Room
- **Networking:** OkHttp
- **Image loading:** Coil
- **Dependency management:** Gradle version catalogs (`gradle/libs.versions.toml`)

## How to run

1. Clone the repository
2. Open in Android Studio (Hedgehog or later recommended)
3. Run on an emulator or physical device (min SDK 21)

No API keys or special setup required — the app fetches publicly available RSS feeds.

## During the interview

You will be given a set of tasks to work through together with your interviewer. The tasks range from simple navigation exercises to more involved code changes. Feel free to use Android Studio's built-in tools, search, documentation, and anything else you would normally have available — we want to see how you actually work, not how well you memorise APIs.

There are no trick questions. If something is unclear, just ask.
