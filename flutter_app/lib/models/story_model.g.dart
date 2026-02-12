// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'story_model.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

StoryModel _$StoryModelFromJson(Map<String, dynamic> json) => StoryModel(
      id: json['id'] as int?,
      userId: json['user_id'] as int?,
      characterId: json['character_id'] as int?,
      title: json['title'] as String?,
      content: json['content'] as String,
      imageUrl: json['image_url'] as String?,
      isFavorite: json['is_favorite'] as bool?,
      shareToken: json['share_token'] as String?,
      viewCount: json['view_count'] as int?,
      createdAt: json['created_at'] as String?,
      updatedAt: json['updated_at'] as String?,
      delFlag: json['del_flag'] as String?,
      characterName: json['character_name'] as String?,
      characterAvatar: json['character_avatar'] as String?,
    );

Map<String, dynamic> _$StoryModelToJson(StoryModel instance) =>
    <String, dynamic>{
      'id': instance.id,
      'user_id': instance.userId,
      'character_id': instance.characterId,
      'title': instance.title,
      'content': instance.content,
      'image_url': instance.imageUrl,
      'is_favorite': instance.isFavorite,
      'share_token': instance.shareToken,
      'view_count': instance.viewCount,
      'created_at': instance.createdAt,
      'updated_at': instance.updatedAt,
      'del_flag': instance.delFlag,
      'character_name': instance.characterName,
      'character_avatar': instance.characterAvatar,
    };
