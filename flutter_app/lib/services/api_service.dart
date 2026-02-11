import 'dart:convert';
import 'package:dio/dio.dart';
import 'package:logger/logger.dart';
import '../models/note_model.dart';
import '../models/review_record_model.dart';
import '../models/rag_response_model.dart';
import '../utils/constants.dart';
import 'auth_service.dart';
import 'package:uuid/uuid.dart';
/// Service for handling all API requests
class ApiService {
  static final ApiService _instance = ApiService._internal();
  factory ApiService() => _instance;
  ApiService._internal();

  late Dio _dio;
  final Logger _logger = Logger();
  final AuthService _authService = AuthService();

  /// Initialize the API service
  void init({String? baseUrl}) {
    _dio = Dio(BaseOptions(
      baseUrl: baseUrl ?? AppConstants.baseUrl,
      connectTimeout: AppConstants.connectionTimeout,
      receiveTimeout: AppConstants.receiveTimeout,
      headers: {
        'Content-Type': 'application/json',
        'Accept': 'application/json',
      },
    ));

    // Add interceptors for logging and auth
    _dio.interceptors.add(InterceptorsWrapper(
      onRequest: (options, handler) async {
        // Add auth token to headers
        final token = await _authService.getToken();
        if (token != null && token.isNotEmpty) {
          options.headers['Authorization'] = 'Bearer $token';
        }

        _logger.d('REQUEST[${options.method}] => PATH: ${options.path}');
        return handler.next(options);
      },
      onResponse: (response, handler) {
        _logger.d(
            'RESPONSE[${response.statusCode}] => PATH: ${response.requestOptions.path}');
        return handler.next(response);
      },
      onError: (error, handler) {
        _logger.e(
            'ERROR[${error.response?.statusCode}] => PATH: ${error.requestOptions.path}');
        return handler.next(error);
      },
    ));
  }

  /// Handle API response
  Map<String, dynamic> _handleResponse(Response response) {
    if (response.statusCode == 200 || response.statusCode == 201) {
      return response.data;
    } else {
      throw Exception('Request failed with status: ${response.statusCode}');
    }
  }

  // ==================== Authentication APIs ====================

  /// User login
  Future<Map<String, dynamic>> login(String username, String password) async {
    try {
      // 生成 UUID
      const uuid = Uuid();
      String generatedUuid = uuid.v4();

      final response = await _dio.post(
        AppConstants.loginEndpoint,
        data: {
          'username': username,
          'password': password,
          'uuid': generatedUuid,  // 添加 uuid
          'isSysLogin': true,      // 添加 isSysLogin
        },
      );
      return _handleResponse(response);
    } catch (e) {
      _logger.e('Login error: $e');
      rethrow;
    }
  }

  /// User registration
  Future<Map<String, dynamic>> register(
      String username, String password, String email) async {
    try {
      final response = await _dio.post(
        AppConstants.registerEndpoint,
        data: {
          'username': username,
          'password': password,
          'email': email,
        },
      );
      return _handleResponse(response);
    } catch (e) {
      _logger.e('Register error: $e');
      rethrow;
    }
  }

  // ==================== Note APIs ====================

  /// Get list of notes
  Future<List<NoteModel>> getNotes({int pageNum = 1, int pageSize = 10}) async {
    try {
      final response = await _dio.get(
        AppConstants.noteListEndpoint,
        queryParameters: {
          'pageNum': pageNum,
          'pageSize': pageSize,
        },
      );

      final result = _handleResponse(response);
      
      // Handle RuoYi response format
      if (result['code'] == 200) {
        final rows = result['rows'] as List<dynamic>? ?? [];
        return rows.map((json) => NoteModel.fromJson(json)).toList();
      } else {
        throw Exception(result['msg'] ?? 'Failed to fetch notes');
      }
    } catch (e) {
      _logger.e('Get notes error: $e');
      rethrow;
    }
  }

  /// Get note by ID
  Future<NoteModel> getNoteById(int id) async {
    try {
      final response = await _dio.get('${AppConstants.noteGetEndpoint}/$id');
      final result = _handleResponse(response);
      
      if (result['code'] == 200) {
        return NoteModel.fromJson(result['data']);
      } else {
        throw Exception(result['msg'] ?? 'Failed to fetch note');
      }
    } catch (e) {
      _logger.e('Get note by ID error: $e');
      rethrow;
    }
  }

  /// Create a new note
  Future<NoteModel> createNote(String content, {String? tags}) async {
    try {
      final response = await _dio.post(
        AppConstants.noteAddEndpoint,
        data: {
          'content': content,
          if (tags != null) 'tags': tags,
        },
      );

      final result = _handleResponse(response);
      
      if (result['code'] == 200) {
        return NoteModel.fromJson(result['data']);
      } else {
        throw Exception(result['msg'] ?? 'Failed to create note');
      }
    } catch (e) {
      _logger.e('Create note error: $e');
      rethrow;
    }
  }

  /// Update a note
  Future<NoteModel> updateNote(int id, String content, {String? tags}) async {
    try {
      final response = await _dio.put(
        '${AppConstants.noteUpdateEndpoint}/$id',
        data: {
          'content': content,
          if (tags != null) 'tags': tags,
        },
      );

      final result = _handleResponse(response);
      
      if (result['code'] == 200) {
        return NoteModel.fromJson(result['data']);
      } else {
        throw Exception(result['msg'] ?? 'Failed to update note');
      }
    } catch (e) {
      _logger.e('Update note error: $e');
      rethrow;
    }
  }

  /// Delete a note
  Future<bool> deleteNote(int id) async {
    try {
      final response =
          await _dio.delete('${AppConstants.noteDeleteEndpoint}/$id');
      final result = _handleResponse(response);
      return result['code'] == 200;
    } catch (e) {
      _logger.e('Delete note error: $e');
      rethrow;
    }
  }

  /// Get note count
  Future<int> getNoteCount() async {
    try {
      final response = await _dio.get(AppConstants.noteCountEndpoint);
      final result = _handleResponse(response);
      
      if (result['code'] == 200) {
        return result['data'] as int? ?? 0;
      } else {
        return 0;
      }
    } catch (e) {
      _logger.e('Get note count error: $e');
      return 0;
    }
  }

  // ==================== RAG APIs ====================

  /// Search notes using RAG
  Future<List<NoteModel>> ragSearch(
    String question, {
    double? similarityThreshold,
    int? maxResults,
  }) async {
    try {
      final response = await _dio.post(
        AppConstants.ragSearchEndpoint,
        data: {
          'question': question,
          'similarityThreshold':
              similarityThreshold ?? AppConstants.defaultSimilarityThreshold,
          'maxResults': maxResults ?? AppConstants.defaultMaxResults,
        },
      );

      final result = _handleResponse(response);
      
      if (result['code'] == 200) {
        final notes = result['data'] as List<dynamic>? ?? [];
        return notes.map((json) => NoteModel.fromJson(json)).toList();
      } else {
        throw Exception(result['msg'] ?? 'Failed to search notes');
      }
    } catch (e) {
      _logger.e('RAG search error: $e');
      rethrow;
    }
  }

  /// Get AI answer using RAG
  Future<RAGResponseModel> ragAnswer(
    String question, {
    double? similarityThreshold,
    int? maxResults,
    bool includeContext = true,
  }) async {
    try {
      final response = await _dio.post(
        AppConstants.ragAnswerEndpoint,
        data: {
          'question': question,
          'similarityThreshold':
              similarityThreshold ?? AppConstants.defaultSimilarityThreshold,
          'maxResults': maxResults ?? AppConstants.defaultMaxResults,
          'includeContext': includeContext,
        },
      );

      final result = _handleResponse(response);
      
      if (result['code'] == 200) {
        return RAGResponseModel.fromJson(result['data']);
      } else {
        throw Exception(result['msg'] ?? 'Failed to get answer');
      }
    } catch (e) {
      _logger.e('RAG answer error: $e');
      rethrow;
    }
  }

  /// Chat using RAG (multi-turn conversation)
  Future<RAGResponseModel> ragChat(
    String question, {
    double? similarityThreshold,
    int? maxResults,
  }) async {
    try {
      final response = await _dio.post(
        AppConstants.ragChatEndpoint,
        data: {
          'question': question,
          'similarityThreshold':
              similarityThreshold ?? AppConstants.defaultSimilarityThreshold,
          'maxResults': maxResults ?? AppConstants.defaultMaxResults,
        },
      );

      final result = _handleResponse(response);
      
      if (result['code'] == 200) {
        return RAGResponseModel.fromJson(result['data']);
      } else {
        throw Exception(result['msg'] ?? 'Failed to chat');
      }
    } catch (e) {
      _logger.e('RAG chat error: $e');
      rethrow;
    }
  }

  // ==================== Review APIs ====================

  /// Get next review items
  Future<List<ReviewRecordModel>> getNextReviewItems({int limit = 1}) async {
    try {
      final response = await _dio.get(
        AppConstants.reviewNextEndpoint,
        queryParameters: {'limit': limit},
      );

      final result = _handleResponse(response);
      
      if (result['code'] == 200) {
        final data = result['data'];
        if (data is List) {
          return data.map((json) => ReviewRecordModel.fromJson(json)).toList();
        } else if (data is Map) {
          // 之后（显式转换为 Map<String, dynamic>）
          return [ReviewRecordModel.fromJson(data as Map<String, dynamic>)];
        } else {
          return [];
        }
      } else {
        return [];
      }
    } catch (e) {
      _logger.e('Get next review items error: $e');
      return [];
    }
  }

  /// Record review result
  Future<ReviewRecordModel> recordReview(int noteId, int quality) async {
    try {
      final response = await _dio.post(
        AppConstants.reviewRecordEndpoint,
        queryParameters: {
          'noteId': noteId,
          'quality': quality,
        },
      );

      final result = _handleResponse(response);
      
      if (result['code'] == 200) {
        return ReviewRecordModel.fromJson(result['data']);
      } else {
        throw Exception(result['msg'] ?? 'Failed to record review');
      }
    } catch (e) {
      _logger.e('Record review error: $e');
      rethrow;
    }
  }

  /// Get review statistics
  Future<Map<String, dynamic>> getReviewStats() async {
    try {
      final response = await _dio.get(AppConstants.reviewStatsEndpoint);
      final result = _handleResponse(response);
      
      if (result['code'] == 200) {
        return result['data'] as Map<String, dynamic>;
      } else {
        throw Exception(result['msg'] ?? 'Failed to get review stats');
      }
    } catch (e) {
      _logger.e('Get review stats error: $e');
      rethrow;
    }
  }

  /// Initialize review for a note
  Future<ReviewRecordModel> initializeReview(int noteId) async {
    try {
      final response = await _dio.post(
        AppConstants.reviewInitEndpoint,
        queryParameters: {'noteId': noteId},
      );

      final result = _handleResponse(response);
      
      if (result['code'] == 200) {
        return ReviewRecordModel.fromJson(result['data']);
      } else {
        throw Exception(result['msg'] ?? 'Failed to initialize review');
      }
    } catch (e) {
      _logger.e('Initialize review error: $e');
      rethrow;
    }
  }
}
