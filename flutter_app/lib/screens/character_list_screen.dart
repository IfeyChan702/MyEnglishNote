import 'package:flutter/material.dart';
import '../models/story_character_model.dart';
import '../services/api_service.dart';
import '../widgets/character_card.dart';
import '../utils/constants.dart';
import 'character_edit_screen.dart';
import 'story_list_screen.dart';

class CharacterListScreen extends StatefulWidget {
  const CharacterListScreen({super.key});

  @override
  State<CharacterListScreen> createState() => _CharacterListScreenState();
}

class _CharacterListScreenState extends State<CharacterListScreen> {
  final ApiService _apiService = ApiService();
  List<StoryCharacterModel> _characters = [];
  bool _isLoading = false;
  String? _errorMessage;
  int _currentPage = 1;
  bool _hasMore = true;
  final ScrollController _scrollController = ScrollController();

  @override
  void initState() {
    super.initState();
    _loadCharacters();
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

  Future<void> _loadCharacters({bool refresh = false}) async {
    if (_isLoading) return;

    setState(() {
      _isLoading = true;
      _errorMessage = null;
      if (refresh) {
        _characters = [];
        _currentPage = 1;
        _hasMore = true;
      }
    });

    try {
      final characters = await _apiService.getCharacters(
        pageNum: _currentPage,
        pageSize: AppConstants.defaultPageSize,
      );

      setState(() {
        if (refresh) {
          _characters = characters;
        } else {
          _characters.addAll(characters);
        }
        _hasMore = characters.length >= AppConstants.defaultPageSize;
        _isLoading = false;
      });
    } catch (e) {
      setState(() {
        _errorMessage = 'Failed to load characters: $e';
        _isLoading = false;
      });
    }
  }

  Future<void> _loadMore() async {
    _currentPage++;
    await _loadCharacters();
  }

  Future<void> _deleteCharacter(StoryCharacterModel character) async {
    final confirmed = await showDialog<bool>(
      context: context,
      builder: (context) => AlertDialog(
        title: const Text('Delete Character'),
        content: Text(
            'Are you sure you want to delete "${character.name}"? This will not delete associated stories.'),
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
        await _apiService.deleteCharacter(character.id!);
        setState(() {
          _characters.removeWhere((c) => c.id == character.id);
        });

        if (mounted) {
          ScaffoldMessenger.of(context).showSnackBar(
            const SnackBar(content: Text('Character deleted')),
          );
        }
      } catch (e) {
        if (mounted) {
          ScaffoldMessenger.of(context).showSnackBar(
            SnackBar(
              content: Text('Failed to delete character: $e'),
              backgroundColor: Colors.red,
            ),
          );
        }
      }
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Characters'),
      ),
      body: _buildBody(),
      floatingActionButton: FloatingActionButton(
        onPressed: () async {
          final result = await Navigator.push(
            context,
            MaterialPageRoute(
              builder: (context) => const CharacterEditScreen(),
            ),
          );
          
          if (result == true) {
            _loadCharacters(refresh: true);
          }
        },
        child: const Icon(Icons.add),
      ),
    );
  }

  Widget _buildBody() {
    if (_isLoading && _characters.isEmpty) {
      return const Center(child: CircularProgressIndicator());
    }

    if (_errorMessage != null && _characters.isEmpty) {
      return Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Text(_errorMessage!, style: const TextStyle(color: Colors.red)),
            const SizedBox(height: AppConstants.mediumPadding),
            ElevatedButton(
              onPressed: () => _loadCharacters(refresh: true),
              child: const Text('Retry'),
            ),
          ],
        ),
      );
    }

    if (_characters.isEmpty) {
      return Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Icon(Icons.person_outline, size: 64, color: Colors.grey[400]),
            const SizedBox(height: AppConstants.mediumPadding),
            const Text(
              'No characters yet',
              style: TextStyle(fontSize: 18, color: Colors.grey),
            ),
            const SizedBox(height: AppConstants.smallPadding),
            const Text(
              'Tap + to create your first character',
              style: TextStyle(color: Colors.grey),
            ),
          ],
        ),
      );
    }

    return RefreshIndicator(
      onRefresh: () => _loadCharacters(refresh: true),
      child: ListView.builder(
        controller: _scrollController,
        itemCount: _characters.length + (_hasMore ? 1 : 0),
        itemBuilder: (context, index) {
          if (index == _characters.length) {
            return const Center(
              child: Padding(
                padding: EdgeInsets.all(AppConstants.mediumPadding),
                child: CircularProgressIndicator(),
              ),
            );
          }

          final character = _characters[index];
          return CharacterCard(
            character: character,
            onTap: () {
              // Navigate to stories filtered by this character
              // For now, just show a message
              ScaffoldMessenger.of(context).showSnackBar(
                SnackBar(
                  content: Text('${character.storyCount ?? 0} stories with ${character.name}'),
                ),
              );
            },
            onEdit: () async {
              final result = await Navigator.push(
                context,
                MaterialPageRoute(
                  builder: (context) => CharacterEditScreen(character: character),
                ),
              );
              
              if (result == true) {
                _loadCharacters(refresh: true);
              }
            },
            onDelete: () => _deleteCharacter(character),
          );
        },
      ),
    );
  }
}
