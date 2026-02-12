import 'package:flutter/material.dart';
import '../models/story_model.dart';
import '../services/api_service.dart';
import '../widgets/story_card.dart';
import '../utils/constants.dart';
import 'story_detail_screen.dart';
import 'generate_story_screen.dart';

class StoryListScreen extends StatefulWidget {
  const StoryListScreen({super.key});

  @override
  State<StoryListScreen> createState() => _StoryListScreenState();
}

class _StoryListScreenState extends State<StoryListScreen> {
  final ApiService _apiService = ApiService();
  List<StoryModel> _stories = [];
  bool _isLoading = false;
  String? _errorMessage;
  int _currentPage = 1;
  bool _hasMore = true;
  final ScrollController _scrollController = ScrollController();

  // Filter states
  int? _filterCharacterId;
  bool? _filterFavorite;

  @override
  void initState() {
    super.initState();
    _loadStories();
    _scrollController.addListener(_onScroll);
  }

  @override
  void dispose() {
    _scrollController.dispose();
    super.dispose();
  }

  void _onScroll() {
    if (_scrollController.position.pixels >=
        _scrollController.position.maxScrollExtent - 200) {
      if (!_isLoading && _hasMore) {
        _loadMore();
      }
    }
  }

  Future<void> _loadStories({bool refresh = false}) async {
    if (_isLoading) return;

    setState(() {
      _isLoading = true;
      _errorMessage = null;
      if (refresh) {
        _stories = [];
        _currentPage = 1;
        _hasMore = true;
      }
    });

    try {
      final stories = await _apiService.getStories(
        pageNum: _currentPage,
        pageSize: AppConstants.defaultPageSize,
        characterId: _filterCharacterId,
        isFavorite: _filterFavorite,
      );

      setState(() {
        if (refresh) {
          _stories = stories;
        } else {
          _stories.addAll(stories);
        }
        _hasMore = stories.length >= AppConstants.defaultPageSize;
        _isLoading = false;
      });
    } catch (e) {
      setState(() {
        _errorMessage = 'Failed to load stories: $e';
        _isLoading = false;
      });
    }
  }

  Future<void> _loadMore() async {
    _currentPage++;
    await _loadStories();
  }

  Future<void> _toggleFavorite(StoryModel story) async {
    try {
      final newFavoriteStatus = !(story.isFavorite ?? false);
      await _apiService.toggleStoryFavorite(story.id!, newFavoriteStatus);
      
      setState(() {
        final index = _stories.indexWhere((s) => s.id == story.id);
        if (index != -1) {
          _stories[index] = story.copyWith(isFavorite: newFavoriteStatus);
        }
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

  Future<void> _deleteStory(StoryModel story) async {
    final confirmed = await showDialog<bool>(
      context: context,
      builder: (context) => AlertDialog(
        title: const Text('Delete Story'),
        content: const Text('Are you sure you want to delete this story?'),
        actions: [
          TextButton(
            onPressed: () => Navigator.pop(context, false),
            child: const Text('Cancel'),
          ),
          TextButton(
            onPressed: () => Navigator.pop(context, true),
            child: const Text('Delete', style: TextStyle(color: Colors.red)),
          ),
        ],
      ),
    );

    if (confirmed == true) {
      try {
        await _apiService.deleteStory(story.id!);
        setState(() {
          _stories.removeWhere((s) => s.id == story.id);
        });

        if (mounted) {
          ScaffoldMessenger.of(context).showSnackBar(
            const SnackBar(content: Text('Story deleted')),
          );
        }
      } catch (e) {
        if (mounted) {
          ScaffoldMessenger.of(context).showSnackBar(
            SnackBar(
              content: Text('Failed to delete story: $e'),
              backgroundColor: Colors.red,
            ),
          );
        }
      }
    }
  }

  void _showFilterOptions() {
    showModalBottomSheet(
      context: context,
      builder: (context) => Padding(
        padding: const EdgeInsets.all(AppConstants.mediumPadding),
        child: Column(
          mainAxisSize: MainAxisSize.min,
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            const Text(
              'Filter Stories',
              style: AppConstants.headingStyle,
            ),
            const SizedBox(height: AppConstants.mediumPadding),
            CheckboxListTile(
              title: const Text('Show only favorites'),
              value: _filterFavorite ?? false,
              onChanged: (value) {
                setState(() {
                  _filterFavorite = value == true ? true : null;
                });
                Navigator.pop(context);
                _loadStories(refresh: true);
              },
            ),
            ListTile(
              title: const Text('Clear filters'),
              leading: const Icon(Icons.clear),
              onTap: () {
                setState(() {
                  _filterFavorite = null;
                  _filterCharacterId = null;
                });
                Navigator.pop(context);
                _loadStories(refresh: true);
              },
            ),
          ],
        ),
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Stories'),
        actions: [
          IconButton(
            icon: const Icon(Icons.filter_list),
            onPressed: _showFilterOptions,
          ),
        ],
      ),
      body: _buildBody(),
      floatingActionButton: FloatingActionButton(
        onPressed: () async {
          final result = await Navigator.push(
            context,
            MaterialPageRoute(
              builder: (context) => const GenerateStoryScreen(),
            ),
          );
          
          if (result == true) {
            _loadStories(refresh: true);
          }
        },
        child: const Icon(Icons.add),
      ),
    );
  }

  Widget _buildBody() {
    if (_isLoading && _stories.isEmpty) {
      return const Center(child: CircularProgressIndicator());
    }

    if (_errorMessage != null && _stories.isEmpty) {
      return Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Text(_errorMessage!, style: const TextStyle(color: Colors.red)),
            const SizedBox(height: AppConstants.mediumPadding),
            ElevatedButton(
              onPressed: () => _loadStories(refresh: true),
              child: const Text('Retry'),
            ),
          ],
        ),
      );
    }

    if (_stories.isEmpty) {
      return Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Icon(Icons.auto_stories, size: 64, color: Colors.grey[400]),
            const SizedBox(height: AppConstants.mediumPadding),
            const Text(
              'No stories yet',
              style: TextStyle(fontSize: 18, color: Colors.grey),
            ),
            const SizedBox(height: AppConstants.smallPadding),
            const Text(
              'Tap + to create your first story',
              style: TextStyle(color: Colors.grey),
            ),
          ],
        ),
      );
    }

    return RefreshIndicator(
      onRefresh: () => _loadStories(refresh: true),
      child: ListView.builder(
        controller: _scrollController,
        itemCount: _stories.length + (_hasMore ? 1 : 0),
        itemBuilder: (context, index) {
          if (index == _stories.length) {
            return const Center(
              child: Padding(
                padding: EdgeInsets.all(AppConstants.mediumPadding),
                child: CircularProgressIndicator(),
              ),
            );
          }

          final story = _stories[index];
          return StoryCard(
            story: story,
            onTap: () async {
              final result = await Navigator.push(
                context,
                MaterialPageRoute(
                  builder: (context) => StoryDetailScreen(storyId: story.id!),
                ),
              );
              
              if (result == true) {
                _loadStories(refresh: true);
              }
            },
            onFavorite: () => _toggleFavorite(story),
            onDelete: () => _deleteStory(story),
          );
        },
      ),
    );
  }
}
