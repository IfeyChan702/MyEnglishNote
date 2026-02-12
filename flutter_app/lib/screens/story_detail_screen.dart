import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import '../models/story_model.dart';
import '../services/api_service.dart';
import '../utils/constants.dart';

class StoryDetailScreen extends StatefulWidget {
  final int storyId;

  const StoryDetailScreen({super.key, required this.storyId});

  @override
  State<StoryDetailScreen> createState() => _StoryDetailScreenState();
}

class _StoryDetailScreenState extends State<StoryDetailScreen> {
  final ApiService _apiService = ApiService();
  StoryModel? _story;
  bool _isLoading = true;
  String? _errorMessage;
  bool _isEditing = false;
  final TextEditingController _titleController = TextEditingController();
  final TextEditingController _contentController = TextEditingController();

  @override
  void initState() {
    super.initState();
    _loadStory();
  }

  @override
  void dispose() {
    _titleController.dispose();
    _contentController.dispose();
    super.dispose();
  }

  Future<void> _loadStory() async {
    setState(() {
      _isLoading = true;
      _errorMessage = null;
    });

    try {
      final story = await _apiService.getStoryById(widget.storyId);
      setState(() {
        _story = story;
        _titleController.text = story.title ?? '';
        _contentController.text = story.content;
        _isLoading = false;
      });
    } catch (e) {
      setState(() {
        _errorMessage = 'Failed to load story: $e';
        _isLoading = false;
      });
    }
  }

  Future<void> _toggleFavorite() async {
    if (_story == null) return;

    try {
      final newFavoriteStatus = !(_story!.isFavorite ?? false);
      await _apiService.toggleStoryFavorite(_story!.id!, newFavoriteStatus);
      
      setState(() {
        _story = _story!.copyWith(isFavorite: newFavoriteStatus);
      });

      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(
            content: Text(newFavoriteStatus
                ? 'Added to favorites'
                : 'Removed from favorites'),
            duration: const Duration(seconds: 2),
          ),
        );
      }
    } catch (e) {
      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(
            content: Text('Failed to update favorite: $e'),
            backgroundColor: Colors.red,
          ),
        );
      }
    }
  }

  Future<void> _saveChanges() async {
    if (_story == null) return;

    try {
      final updatedStory = await _apiService.updateStory(
        _story!.id!,
        title: _titleController.text,
        content: _contentController.text,
      );

      setState(() {
        _story = updatedStory;
        _isEditing = false;
      });

      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          const SnackBar(content: Text('Story updated')),
        );
      }
    } catch (e) {
      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(
            content: Text('Failed to update story: $e'),
            backgroundColor: Colors.red,
          ),
        );
      }
    }
  }

  Future<void> _shareStory() async {
    if (_story == null) return;

    try {
      final shareToken = await _apiService.shareStory(_story!.id!);
      final shareUrl = '${AppConstants.baseUrl}/story/shared/$shareToken';
      
      await Clipboard.setData(ClipboardData(text: shareUrl));
      
      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          const SnackBar(
            content: Text('Share link copied to clipboard'),
            duration: Duration(seconds: 3),
          ),
        );
      }
    } catch (e) {
      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(
            content: Text('Failed to share story: $e'),
            backgroundColor: Colors.red,
          ),
        );
      }
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Story Details'),
        actions: [
          if (_story != null && !_isEditing) ...[
            IconButton(
              icon: Icon(
                _story!.isFavorite == true
                    ? Icons.favorite
                    : Icons.favorite_border,
              ),
              onPressed: _toggleFavorite,
            ),
            IconButton(
              icon: const Icon(Icons.share),
              onPressed: _shareStory,
            ),
            IconButton(
              icon: const Icon(Icons.edit),
              onPressed: () {
                setState(() {
                  _isEditing = true;
                });
              },
            ),
          ],
          if (_isEditing) ...[
            IconButton(
              icon: const Icon(Icons.check),
              onPressed: _saveChanges,
            ),
            IconButton(
              icon: const Icon(Icons.close),
              onPressed: () {
                setState(() {
                  _isEditing = false;
                  _titleController.text = _story?.title ?? '';
                  _contentController.text = _story?.content ?? '';
                });
              },
            ),
          ],
        ],
      ),
      body: _buildBody(),
    );
  }

  Widget _buildBody() {
    if (_isLoading) {
      return const Center(child: CircularProgressIndicator());
    }

    if (_errorMessage != null) {
      return Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Text(_errorMessage!, style: const TextStyle(color: Colors.red)),
            const SizedBox(height: AppConstants.mediumPadding),
            ElevatedButton(
              onPressed: _loadStory,
              child: const Text('Retry'),
            ),
          ],
        ),
      );
    }

    if (_story == null) {
      return const Center(child: Text('Story not found'));
    }

    return SingleChildScrollView(
      padding: const EdgeInsets.all(AppConstants.mediumPadding),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          // Image
          if (_story!.imageUrl != null)
            ClipRRect(
              borderRadius: BorderRadius.circular(12),
              child: Image.network(
                _story!.imageUrl!,
                width: double.infinity,
                height: 200,
                fit: BoxFit.cover,
                errorBuilder: (context, error, stackTrace) {
                  return Container(
                    height: 200,
                    color: Colors.grey[300],
                    child: const Center(
                      child: Icon(Icons.broken_image, size: 64),
                    ),
                  );
                },
              ),
            ),
          
          const SizedBox(height: AppConstants.mediumPadding),
          
          // Character info
          if (_story!.characterName != null)
            Card(
              child: Padding(
                padding: const EdgeInsets.all(AppConstants.mediumPadding),
                child: Row(
                  children: [
                    if (_story!.characterAvatar != null)
                      CircleAvatar(
                        backgroundImage: NetworkImage(_story!.characterAvatar!),
                        radius: 20,
                      ),
                    const SizedBox(width: AppConstants.mediumPadding),
                    Expanded(
                      child: Column(
                        crossAxisAlignment: CrossAxisAlignment.start,
                        children: [
                          const Text(
                            'Character',
                            style: TextStyle(
                              fontSize: 12,
                              color: Colors.grey,
                            ),
                          ),
                          Text(
                            _story!.characterName!,
                            style: const TextStyle(
                              fontSize: 16,
                              fontWeight: FontWeight.w600,
                            ),
                          ),
                        ],
                      ),
                    ),
                  ],
                ),
              ),
            ),
          
          const SizedBox(height: AppConstants.mediumPadding),
          
          // Title
          if (_isEditing)
            TextField(
              controller: _titleController,
              decoration: const InputDecoration(
                labelText: 'Title',
                border: OutlineInputBorder(),
              ),
              style: AppConstants.headingStyle,
            )
          else
            Text(
              _story!.title ?? 'Untitled Story',
              style: AppConstants.headingStyle,
            ),
          
          const SizedBox(height: AppConstants.mediumPadding),
          
          // Content
          if (_isEditing)
            TextField(
              controller: _contentController,
              decoration: const InputDecoration(
                labelText: 'Content',
                border: OutlineInputBorder(),
              ),
              maxLines: 10,
              style: AppConstants.bodyStyle,
            )
          else
            Text(
              _story!.content,
              style: AppConstants.bodyStyle,
            ),
          
          const SizedBox(height: AppConstants.mediumPadding),
          
          // Metadata
          Card(
            child: Padding(
              padding: const EdgeInsets.all(AppConstants.mediumPadding),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  _buildMetadataRow(
                    icon: Icons.calendar_today,
                    label: 'Created',
                    value: _formatDate(_story!.createdAt),
                  ),
                  if (_story!.viewCount != null) ...[
                    const Divider(),
                    _buildMetadataRow(
                      icon: Icons.visibility,
                      label: 'Views',
                      value: '${_story!.viewCount}',
                    ),
                  ],
                ],
              ),
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildMetadataRow({
    required IconData icon,
    required String label,
    required String value,
  }) {
    return Row(
      children: [
        Icon(icon, size: 16, color: Colors.grey),
        const SizedBox(width: AppConstants.smallPadding),
        Text(
          '$label: ',
          style: const TextStyle(
            fontSize: 14,
            color: Colors.grey,
            fontWeight: FontWeight.w500,
          ),
        ),
        Text(
          value,
          style: const TextStyle(fontSize: 14),
        ),
      ],
    );
  }

  String _formatDate(String? dateStr) {
    if (dateStr == null) return '';
    
    try {
      final date = DateTime.parse(dateStr);
      return '${date.month}/${date.day}/${date.year} ${date.hour}:${date.minute.toString().padLeft(2, '0')}';
    } catch (e) {
      return dateStr;
    }
  }
}
