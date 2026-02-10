import 'storage_service.dart';

/// Service for handling user authentication
class AuthService {
  static final AuthService _instance = AuthService._internal();
  factory AuthService() => _instance;
  AuthService._internal();

  final StorageService _storage = StorageService();

  /// Login with username and password
  Future<Map<String, dynamic>> login(String username, String password) async {
    // This will be called through API service
    // This method is a placeholder for auth-related logic
    return {
      'success': false,
      'message': 'Use ApiService.login() instead',
    };
  }

  /// Logout
  Future<void> logout() async {
    await _storage.deleteToken();
    await _storage.deleteUserId();
    await _storage.deleteUsername();
  }

  /// Check if user is authenticated
  Future<bool> isAuthenticated() async {
    final token = await _storage.getToken();
    return token != null && token.isNotEmpty;
  }

  /// Get current user token
  Future<String?> getToken() async {
    return await _storage.getToken();
  }

  /// Save authentication token
  Future<void> saveToken(String token) async {
    await _storage.saveToken(token);
  }

  /// Get current user ID
  Future<int?> getUserId() async {
    return await _storage.getUserId();
  }

  /// Save user ID
  Future<void> saveUserId(int userId) async {
    await _storage.saveUserId(userId);
  }

  /// Get username
  String? getUsername() {
    return _storage.getUsername();
  }

  /// Save username
  Future<void> saveUsername(String username) async {
    await _storage.saveUsername(username);
  }

  /// Save user credentials after successful login
  Future<void> saveUserCredentials({
    required String token,
    required int userId,
    required String username,
  }) async {
    await _storage.saveToken(token);
    await _storage.saveUserId(userId);
    await _storage.saveUsername(username);
  }

  /// Clear all authentication data
  Future<void> clearAuthData() async {
    await logout();
  }
}
