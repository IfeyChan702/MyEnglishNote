// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'review_record_model.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

ReviewRecordModel _$ReviewRecordModelFromJson(Map<String, dynamic> json) =>
    ReviewRecordModel(
      id: (json['id'] as num?)?.toInt(),
      noteId: (json['note_id'] as num).toInt(),
      userId: (json['user_id'] as num?)?.toInt(),
      quality: (json['quality'] as num).toInt(),
      easinessFactor: (json['easiness_factor'] as num?)?.toDouble(),
      intervalDays: (json['interval_days'] as num?)?.toInt(),
      repetitions: (json['repetitions'] as num?)?.toInt(),
      nextReviewDate: json['next_review_date'] as String,
      reviewedAt: json['reviewed_at'] as String?,
    );

Map<String, dynamic> _$ReviewRecordModelToJson(ReviewRecordModel instance) =>
    <String, dynamic>{
      'id': instance.id,
      'note_id': instance.noteId,
      'user_id': instance.userId,
      'quality': instance.quality,
      'easiness_factor': instance.easinessFactor,
      'interval_days': instance.intervalDays,
      'repetitions': instance.repetitions,
      'next_review_date': instance.nextReviewDate,
      'reviewed_at': instance.reviewedAt,
    };
