import 'package:json_annotation/json_annotation.dart';

part 'story_model.g.dart';

@JsonSerializable()
class StoryModel {
  final int? id;
  @JsonKey(name: 'user_id')
  final int? userId;
  @JsonKey(name: 'character_id')
  final int? characterId;
  final String? title;
  final String content;
  @JsonKey(name: 'image_url')
  final String? imageUrl;
  @JsonKey(name: 'is_favorite')
  final bool? isFavorite;
  @JsonKey(name: 'share_token')
  final String? shareToken;
  @JsonKey(name: 'view_count')
  final int? viewCount;
  @JsonKey(name: 'created_at')
  final String? createdAt;
  @JsonKey(name: 'updated_at')
  final String? updatedAt;
  @JsonKey(name: 'del_flag')
  final String? delFlag;

  // Optional character info (when joined)
  @JsonKey(name: 'character_name')
  final String? characterName;
  @JsonKey(name: 'character_avatar')
  final String? characterAvatar;

  StoryModel({
    this.id,
    this.userId,
    this.characterId,
    this.title,
    required this.content,
    this.imageUrl,
    this.isFavorite,
    this.shareToken,
    this.viewCount,
    this.createdAt,
    this.updatedAt,
    this.delFlag,
    this.characterName,
    this.characterAvatar,
  });

  factory StoryModel.fromJson(Map<String, dynamic> json) =>
      _$StoryModelFromJson(json);

  Map<String, dynamic> toJson() => _$StoryModelToJson(this);

  StoryModel copyWith({
    int? id,
    int? userId,
    int? characterId,
    String? title,
    String? content,
    String? imageUrl,
    bool? isFavorite,
    String? shareToken,
    int? viewCount,
    String? createdAt,
    String? updatedAt,
    String? delFlag,
    String? characterName,
    String? characterAvatar,
  }) {
    return StoryModel(
      id: id ?? this.id,
      userId: userId ?? this.userId,
      characterId: characterId ?? this.characterId,
      title: title ?? this.title,
      content: content ?? this.content,
      imageUrl: imageUrl ?? this.imageUrl,
      isFavorite: isFavorite ?? this.isFavorite,
      shareToken: shareToken ?? this.shareToken,
      viewCount: viewCount ?? this.viewCount,
      createdAt: createdAt ?? this.createdAt,
      updatedAt: updatedAt ?? this.updatedAt,
      delFlag: delFlag ?? this.delFlag,
      characterName: characterName ?? this.characterName,
      characterAvatar: characterAvatar ?? this.characterAvatar,
    );
  }

  @override
  String toString() {
    return 'StoryModel(id: $id, title: $title, characterId: $characterId, isFavorite: $isFavorite)';
  }
}
