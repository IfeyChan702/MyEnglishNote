// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'note_model.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

NoteModel _$NoteModelFromJson(Map<String, dynamic> json) => NoteModel(
      id: (json['id'] as num?)?.toInt(),
      userId: (json['user_id'] as num?)?.toInt(),
      content: json['content'] as String,
      embedding: (json['embedding'] as List<dynamic>?)
          ?.map((e) => (e as num).toDouble())
          .toList(),
      embeddingModel: json['embedding_model'] as String?,
      tags: json['tags'] as String?,
      createdAt: json['created_at'] as String?,
      updatedAt: json['updated_at'] as String?,
      delFlag: json['del_flag'] as String?,
    );

Map<String, dynamic> _$NoteModelToJson(NoteModel instance) => <String, dynamic>{
      'id': instance.id,
      'user_id': instance.userId,
      'content': instance.content,
      'embedding': instance.embedding,
      'embedding_model': instance.embeddingModel,
      'tags': instance.tags,
      'created_at': instance.createdAt,
      'updated_at': instance.updatedAt,
      'del_flag': instance.delFlag,
    };
