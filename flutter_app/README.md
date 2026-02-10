# MyEnglishNote Flutter App

A comprehensive English learning note-taking mobile application with RAG (Retrieval-Augmented Generation) search and SRS (Spaced Repetition System) review capabilities.

## Features

- **User Authentication**: Secure login with JWT token management
- **Note Management**: Create, read, update, and delete English learning notes
- **RAG Search**: AI-powered semantic search across your notes using vector embeddings
- **SRS Review**: Intelligent spaced repetition system for effective learning
- **Cross-platform**: Supports Android, iOS, Web, Windows, macOS, and Linux

## Prerequisites

- Flutter SDK 3.0.0 or higher
- Dart SDK
- Android Studio / Xcode (for mobile development)
- Backend API running (see main README.md)

## Getting Started

### Installation

1. Install Flutter dependencies:
```bash
cd flutter_app
flutter pub get
```

2. Generate JSON serialization code:
```bash
flutter pub run build_runner build --delete-conflicting-outputs
```

### Configuration

Update the API base URL in `lib/utils/constants.dart`:

```dart
static const String baseUrl = 'http://your-backend-url:9501';
```

For local development:
- Android Emulator: `http://10.0.2.2:9501`
- iOS Simulator: `http://localhost:9501`
- Physical Device: `http://YOUR_COMPUTER_IP:9501`

### Running the App

#### Mobile (Android/iOS)
```bash
flutter run
```

#### Web
```bash
flutter run -d chrome
```

#### Desktop
```bash
# Windows
flutter run -d windows

# macOS
flutter run -d macos

# Linux
flutter run -d linux
```

## Project Structure

```
lib/
├── main.dart                       # App entry point
├── models/                         # Data models
│   ├── note_model.dart
│   ├── review_record_model.dart
│   └── rag_response_model.dart
├── services/                       # Business logic services
│   ├── api_service.dart           # Backend API integration
│   ├── storage_service.dart       # Local data storage
│   └── auth_service.dart          # Authentication management
├── screens/                        # UI screens
│   ├── login_screen.dart
│   ├── home_screen.dart
│   ├── notes_list_screen.dart
│   ├── note_detail_screen.dart
│   ├── add_note_screen.dart
│   ├── rag_search_screen.dart
│   ├── review_screen.dart
│   └── profile_screen.dart
├── widgets/                        # Reusable UI components
│   ├── note_card.dart
│   ├── rag_result_card.dart
│   └── review_item_card.dart
└── utils/                          # Utility functions
    ├── constants.dart
    ├── validators.dart
    └── helpers.dart
```

## Key Dependencies

- **dio**: HTTP client for API requests
- **flutter_secure_storage**: Secure token storage
- **shared_preferences**: Local data persistence
- **provider/riverpod**: State management
- **json_serializable**: JSON serialization
- **intl**: Internationalization and date formatting
- **logger**: Logging utility

## Building for Production

### Android APK
```bash
flutter build apk --release
```

### Android App Bundle (for Play Store)
```bash
flutter build appbundle --release
```

### iOS
```bash
flutter build ios --release
```

### Web
```bash
flutter build web --release
```

## Testing

Run tests:
```bash
flutter test
```

## Troubleshooting

### Common Issues

1. **Connection Refused**: Ensure backend is running and base URL is correct
2. **Build Errors**: Run `flutter clean && flutter pub get`
3. **Plugin Issues**: Run `flutter pub upgrade`

## License

This project is part of MyEnglishNote system.
