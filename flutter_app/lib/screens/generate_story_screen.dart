import 'dart:convert';
import 'dart:io';
import 'package:flutter/material.dart';
import 'package:image_picker/image_picker.dart';
import '../models/story_character_model.dart';
import '../models/story_model.dart';
import '../services/api_service.dart';
import '../utils/constants.dart';
import 'character_edit_screen.dart';

class GenerateStoryScreen extends StatefulWidget {
  const GenerateStoryScreen({super.key});

  @override
  State<GenerateStoryScreen> createState() => _GenerateStoryScreenState();
}

class _GenerateStoryScreenState extends State<GenerateStoryScreen> {
  final ApiService _apiService = ApiService();
  final ImagePicker _imagePicker = ImagePicker();
  final TextEditingController _titleController = TextEditingController();
  
  File? _selectedImage;
  StoryCharacterModel? _selectedCharacter;
  List<StoryCharacterModel> _characters = [];
  bool _isLoadingCharacters = false;
  bool _isGenerating = false;
  String? _errorMessage;

  @override
  void initState() {
    super.initState();
    _loadCharacters();
  }

  @override
  void dispose() {
    _titleController.dispose();
    super.dispose();
  }

  Future<void> _loadCharacters() async {
    setState(() {
      _isLoadingCharacters = true;
      _errorMessage = null;
    });

    try {
      final characters = await _apiService.getCharacters();
      setState(() {
        _characters = characters;
        _isLoadingCharacters = false;
      });
    } catch (e) {
      setState(() {
        _errorMessage = 'Failed to load characters: $e';
        _isLoadingCharacters = false;
      });
    }
  }

  Future<void> _pickImage(ImageSource source) async {
    try {
      final XFile? image = await _imagePicker.pickImage(
        source: source,
        maxWidth: 1920,
        maxHeight: 1080,
        imageQuality: 85,
      );

      if (image != null) {
        setState(() {
          _selectedImage = File(image.path);
        });
      }
    } catch (e) {
      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(
            content: Text('Failed to pick image: $e'),
            backgroundColor: Colors.red,
          ),
        );
      }
    }
  }

  void _showImageSourceDialog() {
    showModalBottomSheet(
      context: context,
      builder: (context) => SafeArea(
        child: Wrap(
          children: [
            ListTile(
              leading: const Icon(Icons.photo_camera),
              title: const Text('Take Photo'),
              onTap: () {
                Navigator.pop(context);
                _pickImage(ImageSource.camera);
              },
            ),
            ListTile(
              leading: const Icon(Icons.photo_library),
              title: const Text('Choose from Gallery'),
              onTap: () {
                Navigator.pop(context);
                _pickImage(ImageSource.gallery);
              },
            ),
          ],
        ),
      ),
    );
  }

  Future<void> _generateStory() async {
    if (_selectedImage == null) {
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(
          content: Text('Please select an image'),
          backgroundColor: Colors.orange,
        ),
      );
      return;
    }

    if (_selectedCharacter == null) {
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(
          content: Text('Please select a character'),
          backgroundColor: Colors.orange,
        ),
      );
      return;
    }

    setState(() {
      _isGenerating = true;
      _errorMessage = null;
    });

    try {
      // Convert image to base64
      final bytes = await _selectedImage!.readAsBytes();
      final base64Image = base64Encode(bytes);

      // Generate story
      final story = await _apiService.generateStory(
        image: base64Image,
        imageType: 'base64',
        characterId: _selectedCharacter!.id!,
        title: _titleController.text.isNotEmpty ? _titleController.text : null,
      );

      setState(() {
        _isGenerating = false;
      });

      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          const SnackBar(content: Text('Story generated successfully!')),
        );
        Navigator.pop(context, true);
      }
    } catch (e) {
      setState(() {
        _errorMessage = 'Failed to generate story: $e';
        _isGenerating = false;
      });

      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(
            content: Text('Failed to generate story: $e'),
            backgroundColor: Colors.red,
          ),
        );
      }
    }
  }

  void _showCharacterSelectionDialog() {
    showDialog(
      context: context,
      builder: (context) => AlertDialog(
        title: const Text('Select Character'),
        content: SizedBox(
          width: double.maxFinite,
          child: _characters.isEmpty
              ? const Center(child: Text('No characters available'))
              : ListView.builder(
                  shrinkWrap: true,
                  itemCount: _characters.length,
                  itemBuilder: (context, index) {
                    final character = _characters[index];
                    return ListTile(
                      leading: CircleAvatar(
                        backgroundImage: character.avatar != null
                            ? NetworkImage(character.avatar!)
                            : null,
                        child: character.avatar == null
                            ? Text(character.name[0].toUpperCase())
                            : null,
                      ),
                      title: Text(character.name),
                      subtitle: character.description != null
                          ? Text(
                              character.description!,
                              maxLines: 1,
                              overflow: TextOverflow.ellipsis,
                            )
                          : null,
                      onTap: () {
                        setState(() {
                          _selectedCharacter = character;
                        });
                        Navigator.pop(context);
                      },
                    );
                  },
                ),
        ),
        actions: [
          TextButton(
            onPressed: () => Navigator.pop(context),
            child: const Text('Cancel'),
          ),
          TextButton(
            onPressed: () async {
              Navigator.pop(context);
              final result = await Navigator.push(
                context,
                MaterialPageRoute(
                  builder: (context) => const CharacterEditScreen(),
                ),
              );
              
              if (result == true) {
                _loadCharacters();
              }
            },
            child: const Text('Create New'),
          ),
        ],
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Generate Story'),
      ),
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(AppConstants.mediumPadding),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.stretch,
          children: [
            // Image selection
            Card(
              child: InkWell(
                onTap: _isGenerating ? null : _showImageSourceDialog,
                child: Container(
                  height: 250,
                  decoration: BoxDecoration(
                    borderRadius: BorderRadius.circular(8),
                    color: Colors.grey[200],
                  ),
                  child: _selectedImage != null
                      ? ClipRRect(
                          borderRadius: BorderRadius.circular(8),
                          child: Image.file(
                            _selectedImage!,
                            fit: BoxFit.cover,
                          ),
                        )
                      : Column(
                          mainAxisAlignment: MainAxisAlignment.center,
                          children: [
                            Icon(
                              Icons.add_photo_alternate,
                              size: 64,
                              color: Colors.grey[400],
                            ),
                            const SizedBox(height: AppConstants.smallPadding),
                            Text(
                              'Tap to select image',
                              style: TextStyle(
                                fontSize: 16,
                                color: Colors.grey[600],
                              ),
                            ),
                          ],
                        ),
                ),
              ),
            ),

            const SizedBox(height: AppConstants.mediumPadding),

            // Title input (optional)
            TextField(
              controller: _titleController,
              decoration: const InputDecoration(
                labelText: 'Title (Optional)',
                border: OutlineInputBorder(),
                hintText: 'Enter a title for your story',
              ),
              enabled: !_isGenerating,
            ),

            const SizedBox(height: AppConstants.mediumPadding),

            // Character selection
            Card(
              child: ListTile(
                leading: _selectedCharacter?.avatar != null
                    ? CircleAvatar(
                        backgroundImage: NetworkImage(_selectedCharacter!.avatar!),
                      )
                    : CircleAvatar(
                        child: _selectedCharacter != null
                            ? Text(_selectedCharacter!.name[0].toUpperCase())
                            : const Icon(Icons.person),
                      ),
                title: Text(
                  _selectedCharacter?.name ?? 'Select Character',
                  style: TextStyle(
                    color: _selectedCharacter == null ? Colors.grey : null,
                  ),
                ),
                subtitle: _selectedCharacter?.description != null
                    ? Text(
                        _selectedCharacter!.description!,
                        maxLines: 1,
                        overflow: TextOverflow.ellipsis,
                      )
                    : const Text('Choose who will be in your story'),
                trailing: const Icon(Icons.arrow_forward_ios, size: 16),
                onTap: _isGenerating ? null : _showCharacterSelectionDialog,
              ),
            ),

            const SizedBox(height: AppConstants.mediumPadding),

            // Info card
            Card(
              color: Colors.blue[50],
              child: Padding(
                padding: const EdgeInsets.all(AppConstants.mediumPadding),
                child: Row(
                  children: [
                    Icon(Icons.info_outline, color: Colors.blue[700]),
                    const SizedBox(width: AppConstants.smallPadding),
                    Expanded(
                      child: Text(
                        'Upload a photo and select a character to generate an AI story',
                        style: TextStyle(color: Colors.blue[700]),
                      ),
                    ),
                  ],
                ),
              ),
            ),

            const SizedBox(height: AppConstants.largePadding),

            // Generate button
            ElevatedButton(
              onPressed: _isGenerating ? null : _generateStory,
              style: ElevatedButton.styleFrom(
                padding: const EdgeInsets.symmetric(vertical: 16),
                backgroundColor: AppConstants.primaryColor,
              ),
              child: _isGenerating
                  ? const SizedBox(
                      height: 20,
                      width: 20,
                      child: CircularProgressIndicator(
                        strokeWidth: 2,
                        valueColor: AlwaysStoppedAnimation<Color>(Colors.white),
                      ),
                    )
                  : const Text(
                      'Generate Story',
                      style: TextStyle(fontSize: 16, color: Colors.white),
                    ),
            ),

            if (_errorMessage != null) ...[
              const SizedBox(height: AppConstants.mediumPadding),
              Card(
                color: Colors.red[50],
                child: Padding(
                  padding: const EdgeInsets.all(AppConstants.mediumPadding),
                  child: Text(
                    _errorMessage!,
                    style: const TextStyle(color: Colors.red),
                  ),
                ),
              ),
            ],
          ],
        ),
      ),
    );
  }
}
