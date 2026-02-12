import 'package:json_annotation/json_annotation.dart';

part 'story_character_model.g.dart';

@JsonSerializable()
class StoryCharacterModel {
  final int? id;
  @JsonKey(name: 'user_id')
  final int? userId;
  final String name;
  final String? description;
  final String? avatar;
  @JsonKey(name: 'personality')
  final String? personality;
  @JsonKey(name: 'background')
  final String? background;
  @JsonKey(name: 'story_count')
  final int? storyCount;
  @JsonKey(name: 'created_at')
  final String? createdAt;
  @JsonKey(name: 'updated_at')
  final String? updatedAt;
  @JsonKey(name: 'del_flag')
  final String? delFlag;

  StoryCharacterModel({
    this.id,
    this.userId,
    required this.name,
    this.description,
    this.avatar,
    this.personality,
    this.background,
    this.storyCount,
    this.createdAt,
    this.updatedAt,
    this.delFlag,
  });

  factory StoryCharacterModel.fromJson(Map<String, dynamic> json) =>
      _$StoryCharacterModelFromJson(json);

  Map<String, dynamic> toJson() => _$StoryCharacterModelToJson(this);

  StoryCharacterModel copyWith({
    int? id,
    int? userId,
    String? name,
    String? description,
    String? avatar,
    String? personality,
    String? background,
    int? storyCount,
    String? createdAt,
    String? updatedAt,
    String? delFlag,
  }) {
    return StoryCharacterModel(
      id: id ?? this.id,
      userId: userId ?? this.userId,
      name: name ?? this.name,
      description: description ?? this.description,
      avatar: avatar ?? this.avatar,
      personality: personality ?? this.personality,
      background: background ?? this.background,
      storyCount: storyCount ?? this.storyCount,
      createdAt: createdAt ?? this.createdAt,
      updatedAt: updatedAt ?? this.updatedAt,
      delFlag: delFlag ?? this.delFlag,
    );
  }

  @override
  String toString() {
    return 'StoryCharacterModel(id: $id, name: $name, description: $description)';
  }
}
