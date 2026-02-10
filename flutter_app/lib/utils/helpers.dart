import 'package:intl/intl.dart';

/// Helper utility functions
class Helpers {
  /// Format DateTime to readable string
  static String formatDateTime(DateTime dateTime) {
    return DateFormat('yyyy-MM-dd HH:mm').format(dateTime);
  }
  
  /// Format DateTime to date only
  static String formatDate(DateTime dateTime) {
    return DateFormat('yyyy-MM-dd').format(dateTime);
  }
  
  /// Format DateTime to time only
  static String formatTime(DateTime dateTime) {
    return DateFormat('HH:mm').format(dateTime);
  }
  
  /// Format DateTime to relative time (e.g., "2 hours ago")
  static String formatRelativeTime(DateTime dateTime) {
    final now = DateTime.now();
    final difference = now.difference(dateTime);
    
    if (difference.inSeconds < 60) {
      return 'just now';
    } else if (difference.inMinutes < 60) {
      return '${difference.inMinutes} minutes ago';
    } else if (difference.inHours < 24) {
      return '${difference.inHours} hours ago';
    } else if (difference.inDays < 7) {
      return '${difference.inDays} days ago';
    } else if (difference.inDays < 30) {
      return '${(difference.inDays / 7).floor()} weeks ago';
    } else if (difference.inDays < 365) {
      return '${(difference.inDays / 30).floor()} months ago';
    } else {
      return '${(difference.inDays / 365).floor()} years ago';
    }
  }
  
  /// Parse DateTime from string
  static DateTime? parseDateTime(String? dateString) {
    if (dateString == null || dateString.isEmpty) {
      return null;
    }
    try {
      return DateTime.parse(dateString);
    } catch (e) {
      return null;
    }
  }
  
  /// Truncate text with ellipsis
  static String truncateText(String text, int maxLength) {
    if (text.length <= maxLength) {
      return text;
    }
    return '${text.substring(0, maxLength)}...';
  }
  
  /// Calculate days until next review
  static int daysUntilReview(DateTime nextReviewDate) {
    final now = DateTime.now();
    final difference = nextReviewDate.difference(now);
    return difference.inDays;
  }
  
  /// Check if review is due
  static bool isReviewDue(DateTime nextReviewDate) {
    return DateTime.now().isAfter(nextReviewDate);
  }
  
  /// Get quality description
  static String getQualityDescription(int quality) {
    switch (quality) {
      case 0:
        return 'Complete blackout';
      case 1:
        return 'Incorrect, but familiar';
      case 2:
        return 'Incorrect, but easy to recall';
      case 3:
        return 'Correct, but difficult';
      case 4:
        return 'Correct, with hesitation';
      case 5:
        return 'Perfect recall';
      default:
        return 'Unknown';
    }
  }
  
  /// Get quality color
  static String getQualityColor(int quality) {
    if (quality <= 1) return '#f44336'; // Red
    if (quality <= 2) return '#ff9800'; // Orange
    if (quality <= 3) return '#ffeb3b'; // Yellow
    if (quality <= 4) return '#8bc34a'; // Light Green
    return '#4caf50'; // Green
  }
  
  /// Sanitize search query
  static String sanitizeQuery(String query) {
    return query.trim().replaceAll(RegExp(r'\s+'), ' ');
  }
  
  /// Check if string is valid JSON
  static bool isValidJson(String str) {
    try {
      // Simple check - could be improved with actual JSON parsing
      return str.startsWith('{') || str.startsWith('[');
    } catch (e) {
      return false;
    }
  }
}
