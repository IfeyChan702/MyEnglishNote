import 'package:flutter_secure_storage/flutter_secure_storage.dart';
import 'package:shared_preferences/shared_preferences.dart';

/// Service for handling local data storage
class StorageService {
  static final StorageService _instance = StorageService._internal();
  factory StorageService() => _instance;
  StorageService._internal();

  final FlutterSecureStorage _secureStorage = const FlutterSecureStorage();
  SharedPreferences? _prefs;

  /// Initialize the storage service
  Future<void> init() async {
    _prefs = await SharedPreferences.getInstance();
  }

  // Secure Storage Methods (for sensitive data like tokens)

  /// Save token securely
  Future<void> saveToken(String token) async {
    await _secureStorage.write(key: 'auth_token', value: token);
  }

  /// Get saved token
  Future<String?> getToken() async {
    return await _secureStorage.read(key: 'auth_token');
  }

  /// Delete token
  Future<void> deleteToken() async {
    await _secureStorage.delete(key: 'auth_token');
  }

  /// Save user ID securely
  Future<void> saveUserId(int userId) async {
    await _secureStorage.write(key: 'user_id', value: userId.toString());
  }

  /// Get user ID
  Future<int?> getUserId() async {
    final userIdStr = await _secureStorage.read(key: 'user_id');
    return userIdStr != null ? int.tryParse(userIdStr) : null;
  }

  /// Delete user ID
  Future<void> deleteUserId() async {
    await _secureStorage.delete(key: 'user_id');
  }

  // SharedPreferences Methods (for non-sensitive data)

  /// Save username
  Future<bool> saveUsername(String username) async {
    return await _prefs?.setString('username', username) ?? false;
  }

  /// Get username
  String? getUsername() {
    return _prefs?.getString('username');
  }

  /// Delete username
  Future<bool> deleteUsername() async {
    return await _prefs?.remove('username') ?? false;
  }

  /// Save last sync time
  Future<bool> saveLastSyncTime(DateTime dateTime) async {
    return await _prefs?.setString(
          'last_sync_time',
          dateTime.toIso8601String(),
        ) ??
        false;
  }

  /// Get last sync time
  DateTime? getLastSyncTime() {
    final timeStr = _prefs?.getString('last_sync_time');
    return timeStr != null ? DateTime.tryParse(timeStr) : null;
  }

  /// Save API base URL
  Future<bool> saveBaseUrl(String url) async {
    return await _prefs?.setString('base_url', url) ?? false;
  }

  /// Get API base URL
  String? getBaseUrl() {
    return _prefs?.getString('base_url');
  }

  /// Save theme mode
  Future<bool> saveThemeMode(String mode) async {
    return await _prefs?.setString('theme_mode', mode) ?? false;
  }

  /// Get theme mode
  String? getThemeMode() {
    return _prefs?.getString('theme_mode');
  }

  /// Clear all data
  Future<void> clearAll() async {
    await _secureStorage.deleteAll();
    await _prefs?.clear();
  }

  /// Check if user is logged in
  Future<bool> isLoggedIn() async {
    final token = await getToken();
    return token != null && token.isNotEmpty;
  }
}
