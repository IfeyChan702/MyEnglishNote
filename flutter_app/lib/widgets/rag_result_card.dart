import 'package:flutter/material.dart';
import '../models/note_model.dart';
import '../utils/helpers.dart';

/// Widget for displaying a RAG search result card
class RagResultCard extends StatelessWidget {
  final NoteModel note;
  final double? similarity;
  final VoidCallback? onTap;

  const RagResultCard({
    super.key,
    required this.note,
    this.similarity,
    this.onTap,
  });

  @override
  Widget build(BuildContext context) {
    final createdAt = Helpers.parseDateTime(note.createdAt);
    final timeAgo =
        createdAt != null ? Helpers.formatRelativeTime(createdAt) : '';

    return Card(
      margin: const EdgeInsets.symmetric(horizontal: 16, vertical: 8),
      elevation: 2,
      child: InkWell(
        onTap: onTap,
        borderRadius: BorderRadius.circular(8),
        child: Padding(
          padding: const EdgeInsets.all(16),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Row(
                children: [
                  if (similarity != null) ...[
                    Container(
                      padding: const EdgeInsets.symmetric(
                        horizontal: 8,
                        vertical: 4,
                      ),
                      decoration: BoxDecoration(
                        color: _getSimilarityColor(similarity!),
                        borderRadius: BorderRadius.circular(4),
                      ),
                      child: Text(
                        '${(similarity! * 100).toStringAsFixed(0)}%',
                        style: const TextStyle(
                          color: Colors.white,
                          fontSize: 12,
                          fontWeight: FontWeight.bold,
                        ),
                      ),
                    ),
                    const SizedBox(width: 8),
                  ],
                  Expanded(
                    child: Text(
                      'Note #${note.id}',
                      style: TextStyle(
                        fontSize: 14,
                        color: Colors.grey[600],
                        fontWeight: FontWeight.w500,
                      ),
                    ),
                  ),
                ],
              ),
              const SizedBox(height: 12),
              Text(
                note.content,
                style: const TextStyle(
                  fontSize: 16,
                ),
                maxLines: 5,
                overflow: TextOverflow.ellipsis,
              ),
              if (note.tags != null && note.tags!.isNotEmpty) ...[
                const SizedBox(height: 8),
                Wrap(
                  spacing: 4,
                  runSpacing: 4,
                  children: note.tags!
                      .split(',')
                      .map((tag) => Chip(
                            label: Text(
                              tag.trim(),
                              style: const TextStyle(fontSize: 12),
                            ),
                            padding: const EdgeInsets.symmetric(horizontal: 4),
                            materialTapTargetSize:
                                MaterialTapTargetSize.shrinkWrap,
                          ))
                      .toList(),
                ),
              ],
              const SizedBox(height: 8),
              Text(
                timeAgo,
                style: TextStyle(
                  fontSize: 12,
                  color: Colors.grey[600],
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }

  Color _getSimilarityColor(double similarity) {
    if (similarity >= 0.9) return Colors.green;
    if (similarity >= 0.8) return Colors.lightGreen;
    if (similarity >= 0.7) return Colors.orange;
    return Colors.grey;
  }
}
