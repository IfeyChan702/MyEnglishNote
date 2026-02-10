import 'package:json_annotation/json_annotation.dart';

part 'review_record_model.g.dart';

@JsonSerializable()
class ReviewRecordModel {
  final int? id;
  @JsonKey(name: 'note_id')
  final int noteId;
  @JsonKey(name: 'user_id')
  final int? userId;
  final int quality;
  @JsonKey(name: 'easiness_factor')
  final double? easinessFactor;
  @JsonKey(name: 'interval_days')
  final int? intervalDays;
  final int? repetitions;
  @JsonKey(name: 'next_review_date')
  final String nextReviewDate;
  @JsonKey(name: 'reviewed_at')
  final String? reviewedAt;

  ReviewRecordModel({
    this.id,
    required this.noteId,
    this.userId,
    required this.quality,
    this.easinessFactor,
    this.intervalDays,
    this.repetitions,
    required this.nextReviewDate,
    this.reviewedAt,
  });

  factory ReviewRecordModel.fromJson(Map<String, dynamic> json) =>
      _$ReviewRecordModelFromJson(json);

  Map<String, dynamic> toJson() => _$ReviewRecordModelToJson(this);

  ReviewRecordModel copyWith({
    int? id,
    int? noteId,
    int? userId,
    int? quality,
    double? easinessFactor,
    int? intervalDays,
    int? repetitions,
    String? nextReviewDate,
    String? reviewedAt,
  }) {
    return ReviewRecordModel(
      id: id ?? this.id,
      noteId: noteId ?? this.noteId,
      userId: userId ?? this.userId,
      quality: quality ?? this.quality,
      easinessFactor: easinessFactor ?? this.easinessFactor,
      intervalDays: intervalDays ?? this.intervalDays,
      repetitions: repetitions ?? this.repetitions,
      nextReviewDate: nextReviewDate ?? this.nextReviewDate,
      reviewedAt: reviewedAt ?? this.reviewedAt,
    );
  }

  @override
  String toString() {
    return 'ReviewRecordModel(id: $id, noteId: $noteId, quality: $quality, nextReviewDate: $nextReviewDate)';
  }
}
