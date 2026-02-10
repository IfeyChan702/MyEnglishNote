# MyEnglishNote - Flutter Development Documentation

Complete guide for developing and maintaining the Flutter mobile application.

## Table of Contents

- [Overview](#overview)
- [Getting Started](#getting-started)
- [Project Architecture](#project-architecture)
- [State Management](#state-management)
- [API Integration](#api-integration)
- [Local Storage](#local-storage)
- [UI Components](#ui-components)
- [Platform-Specific Configuration](#platform-specific-configuration)
- [Build and Release](#build-and-release)
- [Testing](#testing)
- [Troubleshooting](#troubleshooting)

## Overview

MyEnglishNote Flutter app is a cross-platform mobile application that provides:
- User authentication with JWT
- Note management (CRUD operations)
- RAG-powered semantic search
- SRS-based review system
- Offline-first architecture with local caching

### Supported Platforms

- ✅ Android (5.0+)
- ✅ iOS (11.0+)
- ✅ Web (Chrome, Firefox, Safari, Edge)
- ✅ Windows (10+)
- ✅ macOS (10.14+)
- ✅ Linux (Ubuntu 18.04+)

## Getting Started

### Prerequisites

```bash
# Install Flutter SDK
flutter doctor

# Verify Flutter version
flutter --version
# Should be 3.0.0 or higher
```

### Installation

```bash
cd flutter_app

# Get dependencies
flutter pub get

# Generate JSON serialization code
flutter pub run build_runner build --delete-conflicting-outputs

# Run the app
flutter run
```

### IDE Setup

**VS Code**:
1. Install "Flutter" extension
2. Install "Dart" extension
3. Open `flutter_app/` folder
4. Press F5 to run

**Android Studio**:
1. Install Flutter plugin
2. Install Dart plugin
3. Open `flutter_app/` folder
4. Click Run button

## Project Architecture

### Directory Structure

```
lib/
├── main.dart                      # Application entry point
├── models/                        # Data models (JSON serializable)
│   ├── note_model.dart
│   ├── review_record_model.dart
│   └── rag_response_model.dart
├── services/                      # Business logic layer
│   ├── api_service.dart          # Backend API client
│   ├── storage_service.dart      # Local storage (secure & shared prefs)
│   └── auth_service.dart         # Authentication management
├── screens/                       # UI screens
│   ├── login_screen.dart
│   ├── home_screen.dart
│   ├── notes_list_screen.dart
│   ├── note_detail_screen.dart
│   ├── add_note_screen.dart
│   ├── rag_search_screen.dart
│   ├── review_screen.dart
│   └── profile_screen.dart
├── widgets/                       # Reusable UI components
│   ├── note_card.dart
│   ├── rag_result_card.dart
│   └── review_item_card.dart
└── utils/                         # Utilities and helpers
    ├── constants.dart            # App constants
    ├── validators.dart           # Input validators
    └── helpers.dart              # Helper functions
```

### Architectural Patterns

**Service Layer Pattern**:
- Services encapsulate business logic
- Screens call services, not directly API
- Easy to test and maintain

**Repository Pattern** (implicit):
- API service acts as repository
- Abstracts data source (could add local DB later)

**MVVM-like** (Model-View-ViewModel):
- Models: Data classes
- Views: Screens and widgets
- ViewModels: Service classes

## State Management

The app uses simple `setState()` for local state. For future enhancements, consider:

### Provider (included in dependencies)

```dart
// Create a provider
class NotesProvider extends ChangeNotifier {
  List<NoteModel> _notes = [];
  
  List<NoteModel> get notes => _notes;
  
  Future<void> loadNotes() async {
    _notes = await ApiService().getNotes();
    notifyListeners();
  }
}

// Use in widget
Consumer<NotesProvider>(
  builder: (context, provider, child) {
    return ListView.builder(
      itemCount: provider.notes.length,
      itemBuilder: (context, index) {
        return NoteCard(note: provider.notes[index]);
      },
    );
  },
)
```

### Riverpod (included in dependencies)

```dart
// Define provider
final notesProvider = FutureProvider<List<NoteModel>>((ref) async {
  return await ApiService().getNotes();
});

// Use in widget
Consumer(
  builder: (context, ref, child) {
    final notesAsync = ref.watch(notesProvider);
    return notesAsync.when(
      data: (notes) => ListView(...),
      loading: () => CircularProgressIndicator(),
      error: (error, stack) => Text('Error: $error'),
    );
  },
)
```

## API Integration

### Configuration

Update base URL in `lib/utils/constants.dart`:

```dart
class AppConstants {
  static const String baseUrl = 'http://your-backend-url:9501';
  // ...
}
```

**Platform-specific URLs**:
- **Android Emulator**: `http://10.0.2.2:9501`
- **iOS Simulator**: `http://localhost:9501`
- **Physical Device**: `http://YOUR_COMPUTER_IP:9501`
- **Production**: `https://your-domain.com`

### API Service Usage

```dart
// Initialize (done in main.dart)
final apiService = ApiService();
apiService.init(baseUrl: 'http://your-backend:9501');

// Login
final response = await apiService.login('username', 'password');

// Get notes
final notes = await apiService.getNotes(pageNum: 1, pageSize: 10);

// Create note
final note = await apiService.createNote(
  'apple - 苹果',
  tags: 'vocabulary,fruit',
);

// RAG search
final response = await apiService.ragAnswer('What is apple?');

// Record review
await apiService.recordReview(noteId: 1, quality: 4);
```

### Error Handling

```dart
try {
  final notes = await apiService.getNotes();
  // Handle success
} catch (e) {
  if (e is DioError) {
    if (e.response?.statusCode == 401) {
      // Handle unauthorized
      Navigator.pushReplacement(
        context,
        MaterialPageRoute(builder: (_) => LoginScreen()),
      );
    } else {
      // Handle other errors
      showErrorSnackBar(context, e.message);
    }
  }
}
```

## Local Storage

### Secure Storage (for tokens)

```dart
final storage = StorageService();
await storage.init();

// Save token
await storage.saveToken('jwt-token-here');

// Get token
final token = await storage.getToken();

// Delete token
await storage.deleteToken();
```

### Shared Preferences (for non-sensitive data)

```dart
// Save username
await storage.saveUsername('john_doe');

// Get username
final username = storage.getUsername();

// Save last sync time
await storage.saveLastSyncTime(DateTime.now());
```

## UI Components

### Material Design 3

The app uses Material Design 3 with custom theme:

```dart
MaterialApp(
  theme: ThemeData(
    colorScheme: ColorScheme.fromSeed(seedColor: Colors.blue),
    useMaterial3: true,
  ),
)
```

### Custom Widgets

**NoteCard**:
```dart
NoteCard(
  note: noteModel,
  onTap: () => navigateToDetail(),
  onEdit: () => navigateToEdit(),
  onDelete: () => deleteNote(),
)
```

**RagResultCard**:
```dart
RagResultCard(
  note: noteModel,
  similarity: 0.85,
  onTap: () => showDetails(),
)
```

**ReviewItemCard**:
```dart
ReviewItemCard(
  review: reviewRecord,
  noteContent: 'apple - 苹果',
  onQualitySelected: (quality) => recordReview(quality),
)
```

### Navigation

```dart
// Push new screen
Navigator.push(
  context,
  MaterialPageRoute(
    builder: (context) => NoteDetailScreen(note: note),
  ),
);

// Replace current screen
Navigator.pushReplacement(
  context,
  MaterialPageRoute(builder: (context) => HomeScreen()),
);

// Pop screen
Navigator.pop(context);

// Pop with result
Navigator.pop(context, true);
```

### Dialogs and SnackBars

```dart
// Show dialog
showDialog(
  context: context,
  builder: (context) => AlertDialog(
    title: Text('Confirm'),
    content: Text('Are you sure?'),
    actions: [
      TextButton(
        onPressed: () => Navigator.pop(context, false),
        child: Text('Cancel'),
      ),
      TextButton(
        onPressed: () => Navigator.pop(context, true),
        child: Text('OK'),
      ),
    ],
  ),
);

// Show snackbar
ScaffoldMessenger.of(context).showSnackBar(
  SnackBar(
    content: Text('Note saved successfully'),
    backgroundColor: Colors.green,
  ),
);
```

## Platform-Specific Configuration

### Android

**Minimum SDK**: API 21 (Android 5.0)

`android/app/build.gradle`:
```gradle
android {
    compileSdkVersion 34
    
    defaultConfig {
        minSdkVersion 21
        targetSdkVersion 34
    }
}
```

**Permissions** (`android/app/src/main/AndroidManifest.xml`):
```xml
<uses-permission android:name="android.permission.INTERNET" />
```

**Network Security** (for HTTP in dev):
`android/app/src/main/res/xml/network_security_config.xml`:
```xml
<?xml version="1.0" encoding="utf-8"?>
<network-security-config>
    <base-config cleartextTrafficPermitted="true" />
</network-security-config>
```

### iOS

**Minimum iOS**: 11.0

`ios/Podfile`:
```ruby
platform :ios, '11.0'
```

**Permissions** (`ios/Runner/Info.plist`):
```xml
<key>NSAppTransportSecurity</key>
<dict>
    <key>NSAllowsArbitraryLoads</key>
    <true/>
</dict>
```

### Web

**Index.html** configuration is in `web/index.html`

**CORS**: Backend must allow web origin:
```java
@CrossOrigin(origins = "http://localhost:8080")
```

## Build and Release

### Android Release

```bash
# Build APK
flutter build apk --release

# Build App Bundle (for Play Store)
flutter build appbundle --release

# Output location:
# build/app/outputs/flutter-apk/app-release.apk
# build/app/outputs/bundle/release/app-release.aab
```

**Signing**:
1. Create keystore:
   ```bash
   keytool -genkey -v -keystore ~/key.jks -keyalg RSA -keysize 2048 -validity 10000 -alias key
   ```

2. Create `android/key.properties`:
   ```properties
   storePassword=your-password
   keyPassword=your-password
   keyAlias=key
   storeFile=/path/to/key.jks
   ```

3. Update `android/app/build.gradle`

### iOS Release

```bash
# Build for release
flutter build ios --release

# Open Xcode
open ios/Runner.xcworkspace
```

Then archive and upload via Xcode.

### Web Release

```bash
# Build for web
flutter build web --release

# Output in build/web/
# Deploy to any static hosting (Netlify, Vercel, Firebase)
```

### Desktop Release

```bash
# Windows
flutter build windows --release

# macOS
flutter build macos --release

# Linux
flutter build linux --release
```

## Testing

### Unit Tests

```dart
// test/services/api_service_test.dart
import 'package:flutter_test/flutter_test.dart';

void main() {
  test('ApiService initialization', () {
    final apiService = ApiService();
    apiService.init();
    expect(apiService, isNotNull);
  });
}
```

### Widget Tests

```dart
// test/widgets/note_card_test.dart
import 'package:flutter_test/flutter_test.dart';

void main() {
  testWidgets('NoteCard displays content', (tester) async {
    final note = NoteModel(content: 'Test content');
    
    await tester.pumpWidget(
      MaterialApp(
        home: Scaffold(
          body: NoteCard(note: note),
        ),
      ),
    );
    
    expect(find.text('Test content'), findsOneWidget);
  });
}
```

### Integration Tests

```dart
// integration_test/app_test.dart
import 'package:flutter_test/flutter_test.dart';
import 'package:integration_test/integration_test.dart';

void main() {
  IntegrationTestWidgetsFlutterBinding.ensureInitialized();
  
  testWidgets('Complete login flow', (tester) async {
    app.main();
    await tester.pumpAndSettle();
    
    // Find and tap login button
    await tester.enterText(find.byType(TextField).first, 'username');
    await tester.enterText(find.byType(TextField).last, 'password');
    await tester.tap(find.text('Login'));
    await tester.pumpAndSettle();
    
    // Verify navigation
    expect(find.text('My Notes'), findsOneWidget);
  });
}
```

Run tests:
```bash
flutter test
flutter test integration_test
```

## Troubleshooting

### Build Errors

```bash
# Clean and rebuild
flutter clean
flutter pub get
flutter pub run build_runner build --delete-conflicting-outputs
flutter run
```

### Network Issues

1. **Android Emulator can't connect**:
   - Use `10.0.2.2` instead of `localhost`
   - Check network security config

2. **iOS Simulator can't connect**:
   - Use `localhost` or actual IP
   - Check Info.plist for NSAppTransportSecurity

3. **CORS errors on web**:
   - Backend must allow web origin
   - Check browser console for details

### Platform-Specific Issues

**Android**:
- Gradle sync failed: Update Android Studio
- Build tools version: Match with your SDK

**iOS**:
- Pod install failed: `cd ios && pod install`
- Code signing: Configure in Xcode

**Web**:
- Can't access camera: HTTPS required
- Storage issues: Check browser settings

### Common Dart/Flutter Issues

**Hot reload not working**:
```bash
# Stop and restart
flutter run
```

**Version conflicts**:
```bash
flutter pub upgrade
```

**Generated files not updating**:
```bash
flutter pub run build_runner build --delete-conflicting-outputs
```

## Performance Optimization

1. **Use const constructors**: Reduces widget rebuilds
2. **Lazy loading**: Load images and data on demand
3. **Pagination**: Load data in chunks
4. **Caching**: Cache API responses locally
5. **Image optimization**: Use `cached_network_image`

## Best Practices

1. **Code Organization**
   - Keep screens focused and small
   - Extract reusable widgets
   - Use services for business logic

2. **Error Handling**
   - Always handle exceptions
   - Show user-friendly messages
   - Log errors for debugging

3. **Security**
   - Store tokens securely
   - Validate user input
   - Use HTTPS in production

4. **UI/UX**
   - Show loading indicators
   - Handle empty states
   - Provide feedback for actions

5. **Testing**
   - Write unit tests for services
   - Widget tests for UI components
   - Integration tests for flows

## Resources

- [Flutter Documentation](https://flutter.dev/docs)
- [Dart Documentation](https://dart.dev/guides)
- [Material Design 3](https://m3.material.io/)
- [Pub.dev Packages](https://pub.dev/)

## Support

For Flutter-specific issues:
- Check Flutter Doctor: `flutter doctor`
- Review logs: `flutter logs`
- Search existing issues
- Flutter Community: [discord.gg/flutter](https://discord.gg/flutter)
