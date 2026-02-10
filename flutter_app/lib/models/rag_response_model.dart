import 'package:json_annotation/json_annotation.dart';
import 'note_model.dart';

part 'rag_response_model.g.dart';

@JsonSerializable()
class RAGResponseModel {
  final String? answer;
  final String question;
  @JsonKey(name: 'related_notes')
  final List<NoteModel>? relatedNotes;
  final bool success;
  @JsonKey(name: 'error_message')
  final String? errorMessage;
  @JsonKey(name: 'processing_time')
  final int? processingTime;
  @JsonKey(name: 'similarity_threshold')
  final double? similarityThreshold;
  @JsonKey(name: 'max_results')
  final int? maxResults;

  RAGResponseModel({
    this.answer,
    required this.question,
    this.relatedNotes,
    required this.success,
    this.errorMessage,
    this.processingTime,
    this.similarityThreshold,
    this.maxResults,
  });

  factory RAGResponseModel.fromJson(Map<String, dynamic> json) =>
      _$RAGResponseModelFromJson(json);

  Map<String, dynamic> toJson() => _$RAGResponseModelToJson(this);

  RAGResponseModel copyWith({
    String? answer,
    String? question,
    List<NoteModel>? relatedNotes,
    bool? success,
    String? errorMessage,
    int? processingTime,
    double? similarityThreshold,
    int? maxResults,
  }) {
    return RAGResponseModel(
      answer: answer ?? this.answer,
      question: question ?? this.question,
      relatedNotes: relatedNotes ?? this.relatedNotes,
      success: success ?? this.success,
      errorMessage: errorMessage ?? this.errorMessage,
      processingTime: processingTime ?? this.processingTime,
      similarityThreshold: similarityThreshold ?? this.similarityThreshold,
      maxResults: maxResults ?? this.maxResults,
    );
  }

  @override
  String toString() {
    return 'RAGResponseModel(question: $question, success: $success, answer: $answer)';
  }
}
