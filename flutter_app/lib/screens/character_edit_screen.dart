import 'package:flutter/material.dart';
import '../models/story_character_model.dart';
import '../services/api_service.dart';
import '../utils/constants.dart';

class CharacterEditScreen extends StatefulWidget {
  final StoryCharacterModel? character;

  const CharacterEditScreen({super.key, this.character});

  @override
  State<CharacterEditScreen> createState() => _CharacterEditScreenState();
}

class _CharacterEditScreenState extends State<CharacterEditScreen> {
  final ApiService _apiService = ApiService();
  final _formKey = GlobalKey<FormState>();
  
  late TextEditingController _nameController;
  late TextEditingController _descriptionController;
  late TextEditingController _avatarController;
  late TextEditingController _personalityController;
  late TextEditingController _backgroundController;
  
  bool _isSaving = false;
  String? _errorMessage;

  @override
  void initState() {
    super.initState();
    _nameController = TextEditingController(text: widget.character?.name ?? '');
    _descriptionController = TextEditingController(text: widget.character?.description ?? '');
    _avatarController = TextEditingController(text: widget.character?.avatar ?? '');
    _personalityController = TextEditingController(text: widget.character?.personality ?? '');
    _backgroundController = TextEditingController(text: widget.character?.background ?? '');
  }

  @override
  void dispose() {
    _nameController.dispose();
    _descriptionController.dispose();
    _avatarController.dispose();
    _personalityController.dispose();
    _backgroundController.dispose();
    super.dispose();
  }

  Future<void> _saveCharacter() async {
    if (!_formKey.currentState!.validate()) {
      return;
    }

    setState(() {
      _isSaving = true;
      _errorMessage = null;
    });

    try {
      if (widget.character == null) {
        // Create new character
        await _apiService.createCharacter(
          name: _nameController.text,
          description: _descriptionController.text.isNotEmpty
              ? _descriptionController.text
              : null,
          avatar: _avatarController.text.isNotEmpty
              ? _avatarController.text
              : null,
          personality: _personalityController.text.isNotEmpty
              ? _personalityController.text
              : null,
          background: _backgroundController.text.isNotEmpty
              ? _backgroundController.text
              : null,
        );
      } else {
        // Update existing character
        await _apiService.updateCharacter(
          widget.character!.id!,
          name: _nameController.text,
          description: _descriptionController.text.isNotEmpty
              ? _descriptionController.text
              : null,
          avatar: _avatarController.text.isNotEmpty
              ? _avatarController.text
              : null,
          personality: _personalityController.text.isNotEmpty
              ? _personalityController.text
              : null,
          background: _backgroundController.text.isNotEmpty
              ? _backgroundController.text
              : null,
        );
      }

      setState(() {
        _isSaving = false;
      });

      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(
            content: Text(widget.character == null
                ? 'Character created'
                : 'Character updated'),
          ),
        );
        Navigator.pop(context, true);
      }
    } catch (e) {
      setState(() {
        _errorMessage = 'Failed to save character: $e';
        _isSaving = false;
      });

      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(
            content: Text('Failed to save character: $e'),
            backgroundColor: Colors.red,
          ),
        );
      }
    }
  }

  @override
  Widget build(BuildContext context) {
    final isEditing = widget.character != null;

    return Scaffold(
      appBar: AppBar(
        title: Text(isEditing ? 'Edit Character' : 'Create Character'),
      ),
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(AppConstants.mediumPadding),
        child: Form(
          key: _formKey,
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.stretch,
            children: [
              // Name field (required)
              TextFormField(
                controller: _nameController,
                decoration: const InputDecoration(
                  labelText: 'Name *',
                  border: OutlineInputBorder(),
                  hintText: 'Enter character name',
                ),
                validator: (value) {
                  if (value == null || value.trim().isEmpty) {
                    return 'Name is required';
                  }
                  return null;
                },
                enabled: !_isSaving,
              ),

              const SizedBox(height: AppConstants.mediumPadding),

              // Description field
              TextFormField(
                controller: _descriptionController,
                decoration: const InputDecoration(
                  labelText: 'Description',
                  border: OutlineInputBorder(),
                  hintText: 'Brief description of the character',
                ),
                maxLines: 2,
                enabled: !_isSaving,
              ),

              const SizedBox(height: AppConstants.mediumPadding),

              // Avatar URL field
              TextFormField(
                controller: _avatarController,
                decoration: const InputDecoration(
                  labelText: 'Avatar URL',
                  border: OutlineInputBorder(),
                  hintText: 'https://example.com/avatar.jpg',
                ),
                enabled: !_isSaving,
              ),

              const SizedBox(height: AppConstants.mediumPadding),

              // Personality field
              TextFormField(
                controller: _personalityController,
                decoration: const InputDecoration(
                  labelText: 'Personality',
                  border: OutlineInputBorder(),
                  hintText: 'Describe the character\'s personality',
                ),
                maxLines: 3,
                enabled: !_isSaving,
              ),

              const SizedBox(height: AppConstants.mediumPadding),

              // Background field
              TextFormField(
                controller: _backgroundController,
                decoration: const InputDecoration(
                  labelText: 'Background',
                  border: OutlineInputBorder(),
                  hintText: 'Character\'s background story',
                ),
                maxLines: 3,
                enabled: !_isSaving,
              ),

              const SizedBox(height: AppConstants.largePadding),

              // Save button
              ElevatedButton(
                onPressed: _isSaving ? null : _saveCharacter,
                style: ElevatedButton.styleFrom(
                  padding: const EdgeInsets.symmetric(vertical: 16),
                  backgroundColor: AppConstants.primaryColor,
                ),
                child: _isSaving
                    ? const SizedBox(
                        height: 20,
                        width: 20,
                        child: CircularProgressIndicator(
                          strokeWidth: 2,
                          valueColor:
                              AlwaysStoppedAnimation<Color>(Colors.white),
                        ),
                      )
                    : Text(
                        isEditing ? 'Update Character' : 'Create Character',
                        style: const TextStyle(fontSize: 16, color: Colors.white),
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
                          'Create characters that will appear in your AI-generated stories',
                          style: TextStyle(color: Colors.blue[700]),
                        ),
                      ),
                    ],
                  ),
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }
}
