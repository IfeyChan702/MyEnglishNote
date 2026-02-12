import 'package:flutter/material.dart'; // ← 添加这行导入
// API endpoints
class ApiEndpoints {
  static const String baseUrl = 'https://api.example.com';
  static const String login = '/login';
  static const String register = '/register';
  static const String fetchUserData = '/user/data';
}

// Theme configuration
class AppTheme {
  static const Color primaryColor = Color(0xFF6200EE);
  static const Color accentColor = Color(0xFF03DAC6);
  static const TextStyle headerStyle = TextStyle(
    fontSize: 24,
    fontWeight: FontWeight.bold,
  );
  static const TextStyle bodyStyle = TextStyle(
    fontSize: 14,
    fontWeight: FontWeight.normal,
  );
}