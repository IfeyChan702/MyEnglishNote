// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'story_character_model.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

StoryCharacterModel _$StoryCharacterModelFromJson(Map<String, dynamic> json) =>
    StoryCharacterModel(
      id: json['id'] as int?,
      userId: json['user_id'] as int?,
      name: json['name'] as String,
      description: json['description'] as String?,
      avatar: json['avatar'] as String?,
      personality: json['personality'] as String?,
      background: json['background'] as String?,
      storyCount: json['story_count'] as int?,
      createdAt: json['created_at'] as String?,
      updatedAt: json['updated_at'] as String?,
      delFlag: json['del_flag'] as String?,
    );

Map<String, dynamic> _$StoryCharacterModelToJson(
        StoryCharacterModel instance) =>
    <String, dynamic>{
      'id': instance.id,
      'user_id': instance.userId,
      'name': instance.name,
      'description': instance.description,
      'avatar': instance.avatar,
      'personality': instance.personality,
      'background': instance.background,
      'story_count': instance.storyCount,
      'created_at': instance.createdAt,
      'updated_at': instance.updatedAt,
      'del_flag': instance.delFlag,
    };
