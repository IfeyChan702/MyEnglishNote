import 'package:flutter/material.dart';
import '../models/story_model.dart';
import '../utils/constants.dart';

class StoryCard extends StatelessWidget {
  final StoryModel story;
  final VoidCallback? onTap;
  final VoidCallback? onFavorite;
  final VoidCallback? onDelete;

  const StoryCard({
    super.key,
    required this.story,
    this.onTap,
    this.onFavorite,
    this.onDelete,
  });

  @override
  Widget build(BuildContext context) {
    return Card(
      margin: const EdgeInsets.symmetric(
        horizontal: AppConstants.mediumPadding,
        vertical: AppConstants.smallPadding,
      ),
      elevation: 2,
      shape: RoundedRectangleBorder(
        borderRadius: BorderRadius.circular(12),
      ),
      child: InkWell(
        onTap: onTap,
        borderRadius: BorderRadius.circular(12),
        child: Padding(
          padding: const EdgeInsets.all(AppConstants.mediumPadding),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              // Header with title and favorite icon
              Row(
                children: [
                  Expanded(
                    child: Text(
                      story.title ?? 'Untitled Story',
                      style: AppConstants.subheadingStyle,
                      maxLines: 1,
                      overflow: TextOverflow.ellipsis,
                    ),
                  ),
                  if (onFavorite != null)
                    IconButton(
                      icon: Icon(
                        story.isFavorite == true
                            ? Icons.favorite
                            : Icons.favorite_border,
                        color: story.isFavorite == true
                            ? Colors.red
                            : Colors.grey,
                      ),
                      onPressed: onFavorite,
                    ),
                  if (onDelete != null)
                    IconButton(
                      icon: const Icon(Icons.delete_outline, color: Colors.grey),
                      onPressed: onDelete,
                    ),
                ],
              ),
              const SizedBox(height: AppConstants.smallPadding),
              
              // Character info
              if (story.characterName != null)
                Row(
                  children: [
                    if (story.characterAvatar != null)
                      CircleAvatar(
                        radius: 12,
                        backgroundImage: NetworkImage(story.characterAvatar!),
                      ),
                    const SizedBox(width: AppConstants.smallPadding),
                    Text(
                      story.characterName!,
                      style: const TextStyle(
                        fontSize: 14,
                        color: Colors.grey,
                        fontWeight: FontWeight.w500,
                      ),
                    ),
                  ],
                ),
              
              const SizedBox(height: AppConstants.smallPadding),
              
              // Content preview
              Text(
                story.content,
                style: AppConstants.bodyStyle,
                maxLines: 3,
                overflow: TextOverflow.ellipsis,
              ),
              
              const SizedBox(height: AppConstants.smallPadding),
              
              // Footer with date and view count
              Row(
                mainAxisAlignment: MainAxisAlignment.spaceBetween,
                children: [
                  Text(
                    _formatDate(story.createdAt),
                    style: const TextStyle(
                      fontSize: 12,
                      color: Colors.grey,
                    ),
                  ),
                  if (story.viewCount != null)
                    Row(
                      children: [
                        const Icon(Icons.visibility, size: 16, color: Colors.grey),
                        const SizedBox(width: 4),
                        Text(
                          '${story.viewCount}',
                          style: const TextStyle(
                            fontSize: 12,
                            color: Colors.grey,
                          ),
                        ),
                      ],
                    ),
                ],
              ),
            ],
          ),
        ),
      ),
    );
  }

  String _formatDate(String? dateStr) {
    if (dateStr == null) return '';
    
    try {
      final date = DateTime.parse(dateStr);
      final now = DateTime.now();
      final difference = now.difference(date);
      
      if (difference.inDays == 0) {
        if (difference.inHours == 0) {
          return '${difference.inMinutes}m ago';
        }
        return '${difference.inHours}h ago';
      } else if (difference.inDays < 7) {
        return '${difference.inDays}d ago';
      } else {
        return '${date.month}/${date.day}/${date.year}';
      }
    } catch (e) {
      return dateStr;
    }
  }
}
