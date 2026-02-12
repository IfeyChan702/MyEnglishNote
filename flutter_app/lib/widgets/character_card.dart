import 'package:flutter/material.dart';
import '../models/story_character_model.dart';
import '../utils/constants.dart';

class CharacterCard extends StatelessWidget {
  final StoryCharacterModel character;
  final VoidCallback? onTap;
  final VoidCallback? onEdit;
  final VoidCallback? onDelete;

  const CharacterCard({
    super.key,
    required this.character,
    this.onTap,
    this.onEdit,
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
          child: Row(
            children: [
              // Avatar
              CircleAvatar(
                radius: 30,
                backgroundColor: AppConstants.primaryColor.withOpacity(0.1),
                backgroundImage: character.avatar != null
                    ? NetworkImage(character.avatar!)
                    : null,
                child: character.avatar == null
                    ? Text(
                        character.name.isNotEmpty
                            ? character.name[0].toUpperCase()
                            : '?',
                        style: const TextStyle(
                          fontSize: 24,
                          fontWeight: FontWeight.bold,
                          color: AppConstants.primaryColor,
                        ),
                      )
                    : null,
              ),
              
              const SizedBox(width: AppConstants.mediumPadding),
              
              // Character info
              Expanded(
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Text(
                      character.name,
                      style: AppConstants.subheadingStyle,
                      maxLines: 1,
                      overflow: TextOverflow.ellipsis,
                    ),
                    if (character.description != null &&
                        character.description!.isNotEmpty)
                      Padding(
                        padding: const EdgeInsets.only(
                            top: AppConstants.smallPadding),
                        child: Text(
                          character.description!,
                          style: const TextStyle(
                            fontSize: 14,
                            color: Colors.grey,
                          ),
                          maxLines: 2,
                          overflow: TextOverflow.ellipsis,
                        ),
                      ),
                    if (character.storyCount != null)
                      Padding(
                        padding: const EdgeInsets.only(
                            top: AppConstants.smallPadding),
                        child: Text(
                          '${character.storyCount} stories',
                          style: const TextStyle(
                            fontSize: 12,
                            color: Colors.grey,
                          ),
                        ),
                      ),
                  ],
                ),
              ),
              
              // Action buttons
              if (onEdit != null || onDelete != null)
                Column(
                  mainAxisSize: MainAxisSize.min,
                  children: [
                    if (onEdit != null)
                      IconButton(
                        icon: const Icon(Icons.edit_outlined, size: 20),
                        onPressed: onEdit,
                        color: AppConstants.primaryColor,
                      ),
                    if (onDelete != null)
                      IconButton(
                        icon: const Icon(Icons.delete_outline, size: 20),
                        onPressed: onDelete,
                        color: Colors.grey,
                      ),
                  ],
                ),
            ],
          ),
        ),
      ),
    );
  }
}
