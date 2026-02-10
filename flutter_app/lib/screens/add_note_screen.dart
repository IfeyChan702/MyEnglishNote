import 'package:flutter/material.dart';
import '../models/note_model.dart';
import '../services/api_service.dart';
import '../utils/validators.dart';

/// Screen for adding or editing a note
class AddNoteScreen extends StatefulWidget {
  final NoteModel? noteToEdit;

  const AddNoteScreen({super.key, this.noteToEdit});

  @override
  State<AddNoteScreen> createState() => _AddNoteScreenState();
}

class _AddNoteScreenState extends State<AddNoteScreen> {
  final _formKey = GlobalKey<FormState>();
  final _contentController = TextEditingController();
  final _tagsController = TextEditingController();
  final _apiService = ApiService();
  bool _isLoading = false;

  @override
  void initState() {
    super.initState();
    if (widget.noteToEdit != null) {
      _contentController.text = widget.noteToEdit!.content;
      _tagsController.text = widget.noteToEdit!.tags ?? '';
    }
  }

  @override
  void dispose() {
    _contentController.dispose();
    _tagsController.dispose();
    super.dispose();
  }

  Future<void> _saveNote() async {
    if (!_formKey.currentState!.validate()) {
      return;
    }

    setState(() => _isLoading = true);

    try {
      if (widget.noteToEdit != null) {
        // Update existing note
        await _apiService.updateNote(
          widget.noteToEdit!.id!,
          _contentController.text.trim(),
          tags: _tagsController.text.trim().isNotEmpty
              ? _tagsController.text.trim()
              : null,
        );
        _showSuccess('Note updated successfully');
      } else {
        // Create new note
        await _apiService.createNote(
          _contentController.text.trim(),
          tags: _tagsController.text.trim().isNotEmpty
              ? _tagsController.text.trim()
              : null,
        );
        _showSuccess('Note created successfully');
      }

      if (mounted) {
        Navigator.pop(context, true);
      }
    } catch (e) {
      _showError('Failed to save note: ${e.toString()}');
    } finally {
      if (mounted) {
        setState(() => _isLoading = false);
      }
    }
  }

  void _showError(String message) {
    ScaffoldMessenger.of(context).showSnackBar(
      SnackBar(content: Text(message), backgroundColor: Colors.red),
    );
  }

  void _showSuccess(String message) {
    ScaffoldMessenger.of(context).showSnackBar(
      SnackBar(content: Text(message), backgroundColor: Colors.green),
    );
  }

  @override
  Widget build(BuildContext context) {
    final isEditing = widget.noteToEdit != null;

    return Scaffold(
      appBar: AppBar(
        title: Text(isEditing ? 'Edit Note' : 'Add Note'),
        actions: [
          IconButton(
            icon: const Icon(Icons.check),
            onPressed: _isLoading ? null : _saveNote,
          ),
        ],
      ),
      body: Form(
        key: _formKey,
        child: ListView(
          padding: const EdgeInsets.all(16),
          children: [
            TextFormField(
              controller: _contentController,
              decoration: const InputDecoration(
                labelText: 'Note Content',
                hintText: 'Enter your English learning note...',
                border: OutlineInputBorder(),
                alignLabelWithHint: true,
              ),
              maxLines: 10,
              validator: Validators.validateNoteContent,
              enabled: !_isLoading,
            ),
            const SizedBox(height: 16),
            TextFormField(
              controller: _tagsController,
              decoration: const InputDecoration(
                labelText: 'Tags (optional)',
                hintText: 'vocabulary, grammar, phrases',
                border: OutlineInputBorder(),
                helperText: 'Separate tags with commas',
              ),
              enabled: !_isLoading,
            ),
            const SizedBox(height: 24),
            if (_isLoading)
              const Center(child: CircularProgressIndicator())
            else
              ElevatedButton(
                onPressed: _saveNote,
                style: ElevatedButton.styleFrom(
                  padding: const EdgeInsets.symmetric(vertical: 16),
                  textStyle: const TextStyle(fontSize: 16),
                ),
                child: Text(isEditing ? 'Update Note' : 'Create Note'),
              ),
            if (isEditing) ...[
              const SizedBox(height: 16),
              const Divider(),
              const SizedBox(height: 8),
              Text(
                'Note Info',
                style: Theme.of(context).textTheme.titleMedium,
              ),
              const SizedBox(height: 8),
              Text('ID: ${widget.noteToEdit!.id}'),
              if (widget.noteToEdit!.createdAt != null)
                Text('Created: ${widget.noteToEdit!.createdAt}'),
              if (widget.noteToEdit!.updatedAt != null)
                Text('Updated: ${widget.noteToEdit!.updatedAt}'),
              if (widget.noteToEdit!.embedding != null)
                const Row(
                  children: [
                    Icon(Icons.check_circle, color: Colors.green, size: 16),
                    SizedBox(width: 4),
                    Text('Vector embedding generated'),
                  ],
                ),
            ],
          ],
        ),
      ),
    );
  }
}
