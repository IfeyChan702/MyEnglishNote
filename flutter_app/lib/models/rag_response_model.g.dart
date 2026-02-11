// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'rag_response_model.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

RAGResponseModel _$RAGResponseModelFromJson(Map<String, dynamic> json) =>
    RAGResponseModel(
      answer: json['answer'] as String?,
      question: json['question'] as String,
      relatedNotes: (json['related_notes'] as List<dynamic>?)
          ?.map((e) => NoteModel.fromJson(e as Map<String, dynamic>))
          .toList(),
      success: json['success'] as bool,
      errorMessage: json['error_message'] as String?,
      processingTime: (json['processing_time'] as num?)?.toInt(),
      similarityThreshold: (json['similarity_threshold'] as num?)?.toDouble(),
      maxResults: (json['max_results'] as num?)?.toInt(),
    );

Map<String, dynamic> _$RAGResponseModelToJson(RAGResponseModel instance) =>
    <String, dynamic>{
      'answer': instance.answer,
      'question': instance.question,
      'related_notes': instance.relatedNotes,
      'success': instance.success,
      'error_message': instance.errorMessage,
      'processing_time': instance.processingTime,
      'similarity_threshold': instance.similarityThreshold,
      'max_results': instance.maxResults,
    };
