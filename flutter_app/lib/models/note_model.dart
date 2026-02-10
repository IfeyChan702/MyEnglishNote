import 'package:json_annotation/json_annotation.dart';

part 'note_model.g.dart';

@JsonSerializable()
class NoteModel {
  final int? id;
  @JsonKey(name: 'user_id')
  final int? userId;
  final String content;
  final List<double>? embedding;
  @JsonKey(name: 'embedding_model')
  final String? embeddingModel;
  final String? tags;
  @JsonKey(name: 'created_at')
  final String? createdAt;
  @JsonKey(name: 'updated_at')
  final String? updatedAt;
  @JsonKey(name: 'del_flag')
  final String? delFlag;

  NoteModel({
    this.id,
    this.userId,
    required this.content,
    this.embedding,
    this.embeddingModel,
    this.tags,
    this.createdAt,
    this.updatedAt,
    this.delFlag,
  });

  factory NoteModel.fromJson(Map<String, dynamic> json) =>
      _$NoteModelFromJson(json);

  Map<String, dynamic> toJson() => _$NoteModelToJson(this);

  NoteModel copyWith({
    int? id,
    int? userId,
    String? content,
    List<double>? embedding,
    String? embeddingModel,
    String? tags,
    String? createdAt,
    String? updatedAt,
    String? delFlag,
  }) {
    return NoteModel(
      id: id ?? this.id,
      userId: userId ?? this.userId,
      content: content ?? this.content,
      embedding: embedding ?? this.embedding,
      embeddingModel: embeddingModel ?? this.embeddingModel,
      tags: tags ?? this.tags,
      createdAt: createdAt ?? this.createdAt,
      updatedAt: updatedAt ?? this.updatedAt,
      delFlag: delFlag ?? this.delFlag,
    );
  }

  @override
  String toString() {
    return 'NoteModel(id: $id, userId: $userId, content: $content, tags: $tags, createdAt: $createdAt)';
  }
}
