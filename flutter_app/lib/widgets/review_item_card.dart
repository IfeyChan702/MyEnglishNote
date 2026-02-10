import 'package:flutter/material.dart';
import '../models/review_record_model.dart';
import '../utils/helpers.dart';

/// Widget for displaying a review item card
class ReviewItemCard extends StatelessWidget {
  final ReviewRecordModel review;
  final String noteContent;
  final Function(int quality)? onQualitySelected;

  const ReviewItemCard({
    super.key,
    required this.review,
    required this.noteContent,
    this.onQualitySelected,
  });

  @override
  Widget build(BuildContext context) {
    final nextReviewDate = Helpers.parseDateTime(review.nextReviewDate);
    final daysUntil = nextReviewDate != null
        ? Helpers.daysUntilReview(nextReviewDate)
        : 0;
    final isDue = nextReviewDate != null
        ? Helpers.isReviewDue(nextReviewDate)
        : false;

    return Card(
      margin: const EdgeInsets.all(16),
      elevation: 4,
      child: Padding(
        padding: const EdgeInsets.all(20),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.stretch,
          children: [
            Row(
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: [
                Text(
                  'Review #${review.noteId}',
                  style: const TextStyle(
                    fontSize: 18,
                    fontWeight: FontWeight.bold,
                  ),
                ),
                Container(
                  padding: const EdgeInsets.symmetric(
                    horizontal: 12,
                    vertical: 6,
                  ),
                  decoration: BoxDecoration(
                    color: isDue ? Colors.red[100] : Colors.green[100],
                    borderRadius: BorderRadius.circular(20),
                  ),
                  child: Text(
                    isDue ? 'Due Now' : 'In $daysUntil days',
                    style: TextStyle(
                      color: isDue ? Colors.red[800] : Colors.green[800],
                      fontWeight: FontWeight.bold,
                      fontSize: 12,
                    ),
                  ),
                ),
              ],
            ),
            const SizedBox(height: 16),
            Container(
              padding: const EdgeInsets.all(16),
              decoration: BoxDecoration(
                color: Colors.grey[100],
                borderRadius: BorderRadius.circular(8),
              ),
              child: Text(
                noteContent,
                style: const TextStyle(
                  fontSize: 16,
                  height: 1.5,
                ),
              ),
            ),
            const SizedBox(height: 16),
            Row(
              mainAxisAlignment: MainAxisAlignment.spaceAround,
              children: [
                _buildStatItem(
                  'Repetitions',
                  review.repetitions?.toString() ?? '0',
                  Icons.repeat,
                ),
                _buildStatItem(
                  'Interval',
                  '${review.intervalDays ?? 0} days',
                  Icons.calendar_today,
                ),
                _buildStatItem(
                  'Easiness',
                  review.easinessFactor?.toStringAsFixed(2) ?? '2.50',
                  Icons.trending_up,
                ),
              ],
            ),
            if (onQualitySelected != null) ...[
              const SizedBox(height: 24),
              const Text(
                'How well did you remember?',
                style: TextStyle(
                  fontSize: 16,
                  fontWeight: FontWeight.w600,
                ),
                textAlign: TextAlign.center,
              ),
              const SizedBox(height: 16),
              Row(
                mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                children: List.generate(
                  6,
                  (index) => _buildQualityButton(context, index),
                ),
              ),
            ],
          ],
        ),
      ),
    );
  }

  Widget _buildStatItem(String label, String value, IconData icon) {
    return Column(
      children: [
        Icon(icon, size: 20, color: Colors.blue[700]),
        const SizedBox(height: 4),
        Text(
          value,
          style: const TextStyle(
            fontSize: 16,
            fontWeight: FontWeight.bold,
          ),
        ),
        Text(
          label,
          style: TextStyle(
            fontSize: 12,
            color: Colors.grey[600],
          ),
        ),
      ],
    );
  }

  Widget _buildQualityButton(BuildContext context, int quality) {
    final color = _getQualityButtonColor(quality);
    
    return Expanded(
      child: Padding(
        padding: const EdgeInsets.symmetric(horizontal: 2),
        child: ElevatedButton(
          onPressed: () => onQualitySelected?.call(quality),
          style: ElevatedButton.styleFrom(
            backgroundColor: color,
            padding: const EdgeInsets.symmetric(vertical: 12),
            shape: RoundedRectangleBorder(
              borderRadius: BorderRadius.circular(8),
            ),
          ),
          child: Text(
            quality.toString(),
            style: const TextStyle(
              color: Colors.white,
              fontWeight: FontWeight.bold,
              fontSize: 16,
            ),
          ),
        ),
      ),
    );
  }

  Color _getQualityButtonColor(int quality) {
    switch (quality) {
      case 0:
        return Colors.red[900]!;
      case 1:
        return Colors.red[700]!;
      case 2:
        return Colors.orange[700]!;
      case 3:
        return Colors.yellow[700]!;
      case 4:
        return Colors.lightGreen[700]!;
      case 5:
        return Colors.green[700]!;
      default:
        return Colors.grey;
    }
  }
}
