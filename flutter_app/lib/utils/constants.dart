import 'package:flutter/material.dart';

/// Application constants
class AppConstants {
  // API Configuration
  static const String baseUrl = 'http://localhost:9501';
  static const String apiPrefix = '/api';
  
  // API Endpoints
  static const String loginEndpoint = '/loginmobile';
  static const String registerEndpoint = '/register';
  static const String noteListEndpoint = '$apiPrefix/note/list';
  static const String noteAddEndpoint = '$apiPrefix/note/add';
  static const String noteGetEndpoint = '$apiPrefix/note'; // /{id}
  static const String noteUpdateEndpoint = '$apiPrefix/note'; // /{id}
  static const String noteDeleteEndpoint = '$apiPrefix/note'; // /{id}
  static const String noteCountEndpoint = '$apiPrefix/note/count';
  static const String ragSearchEndpoint = '$apiPrefix/rag/search';
  static const String ragAnswerEndpoint = '$apiPrefix/rag/answer';
  static const String ragChatEndpoint = '$apiPrefix/rag/chat';
  static const String reviewNextEndpoint = '$apiPrefix/review/next';
  static const String reviewRecordEndpoint = '$apiPrefix/review/record';
  static const String reviewStatsEndpoint = '$apiPrefix/review/stats';
  static const String reviewInitEndpoint = '$apiPrefix/review/initialize';
  
  // Story Endpoints
  static const String storyGenerateEndpoint = '$apiPrefix/story/generate';
  static const String storyListEndpoint = '$apiPrefix/story/list';
  static const String storyGetEndpoint = '$apiPrefix/story'; // /{id}
  static const String storyUpdateEndpoint = '$apiPrefix/story'; // /{id}
  static const String storyDeleteEndpoint = '$apiPrefix/story'; // /{id}
  static const String storyFavoriteEndpoint = '$apiPrefix/story'; // /{id}/favorite
  static const String storyShareEndpoint = '$apiPrefix/story'; // /{id}/share
  static const String storySharedEndpoint = '$apiPrefix/story/shared'; // /{shareToken}
  
  // Character Endpoints
  static const String characterListEndpoint = '$apiPrefix/character/list';
  static const String characterAddEndpoint = '$apiPrefix/character/add';
  static const String characterGetEndpoint = '$apiPrefix/character'; // /{id}
  static const String characterUpdateEndpoint = '$apiPrefix/character'; // /{id}
  static const String characterDeleteEndpoint = '$apiPrefix/character'; // /{id}
  
  // Search Endpoints
  static const String searchStoriesEndpoint = '$apiPrefix/search/stories';
  static const String searchCharactersEndpoint = '$apiPrefix/search/characters';
  static const String searchAllEndpoint = '$apiPrefix/search/all';
  
  // Storage Keys
  static const String tokenKey = 'auth_token';
  static const String userIdKey = 'user_id';
  static const String usernameKey = 'username';
  
  // Timeouts
  static const Duration connectionTimeout = Duration(seconds: 30);
  static const Duration receiveTimeout = Duration(seconds: 30);
  
  // Pagination
  static const int defaultPageSize = 10;
  static const int defaultPageNum = 1;
  
  // RAG Configuration
  static const double defaultSimilarityThreshold = 0.7;
  static const int defaultMaxResults = 5;
  
  // Review Quality Scale
  static const int minReviewQuality = 0;
  static const int maxReviewQuality = 5;
  
  // App Theme Colors
  static const Color primaryColor = Color(0xFF2196F3);
  static const Color accentColor = Color(0xFF03DAC6);
  static const Color errorColor = Color(0xFFB00020);
  static const Color successColor = Color(0xFF4CAF50);
  
  // Text Styles
  static const TextStyle headingStyle = TextStyle(
    fontSize: 24,
    fontWeight: FontWeight.bold,
  );
  
  static const TextStyle subheadingStyle = TextStyle(
    fontSize: 18,
    fontWeight: FontWeight.w600,
  );
  
  static const TextStyle bodyStyle = TextStyle(
    fontSize: 16,
  );
  
  // Spacing
  static const double smallPadding = 8.0;
  static const double mediumPadding = 16.0;
  static const double largePadding = 24.0;
  
  // Animation Durations
  static const Duration shortAnimation = Duration(milliseconds: 200);
  static const Duration mediumAnimation = Duration(milliseconds: 300);
  static const Duration longAnimation = Duration(milliseconds: 500);
}
