import 'package:flutter/material.dart';
import '../models/note_model.dart';
import '../utils/helpers.dart';

/// Screen for displaying note details
class NoteDetailScreen extends StatelessWidget {
  final NoteModel note;

  const NoteDetailScreen({super.key, required this.note});

  @override
  Widget build(BuildContext context) {
    final createdAt = Helpers.parseDateTime(note.createdAt);
    final updatedAt = Helpers.parseDateTime(note.updatedAt);

    return Scaffold(
      appBar: AppBar(
        title: const Text('Note Details'),
      ),
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.stretch,
          children: [
            Card(
              child: Padding(
                padding: const EdgeInsets.all(16),
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Text(
                      'Content',
                      style: Theme.of(context).textTheme.titleLarge,
                    ),
                    const SizedBox(height: 12),
                    Text(
                      note.content,
                      style: const TextStyle(fontSize: 16, height: 1.5),
                    ),
                  ],
                ),
              ),
            ),
            if (note.tags != null && note.tags!.isNotEmpty) ...[
              const SizedBox(height: 16),
              Card(
                child: Padding(
                  padding: const EdgeInsets.all(16),
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      Text(
                        'Tags',
                        style: Theme.of(context).textTheme.titleMedium,
                      ),
                      const SizedBox(height: 8),
                      Wrap(
                        spacing: 8,
                        runSpacing: 8,
                        children: note.tags!
                            .split(',')
                            .map((tag) => Chip(
                                  label: Text(tag.trim()),
                                ))
                            .toList(),
                      ),
                    ],
                  ),
                ),
              ),
            ],
            const SizedBox(height: 16),
            Card(
              child: Padding(
                padding: const EdgeInsets.all(16),
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Text(
                      'Metadata',
                      style: Theme.of(context).textTheme.titleMedium,
                    ),
                    const SizedBox(height: 12),
                    _buildInfoRow('ID', note.id?.toString() ?? 'N/A'),
                    _buildInfoRow('User ID', note.userId?.toString() ?? 'N/A'),
                    if (createdAt != null)
                      _buildInfoRow('Created', Helpers.formatDateTime(createdAt)),
                    if (updatedAt != null)
                      _buildInfoRow('Updated', Helpers.formatDateTime(updatedAt)),
                    _buildInfoRow(
                      'Embedding Model',
                      note.embeddingModel ?? 'Not set',
                    ),
                    Row(
                      children: [
                        const Text(
                          'Vector Status: ',
                          style: TextStyle(fontWeight: FontWeight.w500),
                        ),
                        Icon(
                          note.embedding != null
                              ? Icons.check_circle
                              : Icons.cancel,
                          color: note.embedding != null
                              ? Colors.green
                              : Colors.red,
                          size: 20,
                        ),
                        const SizedBox(width: 4),
                        Text(
                          note.embedding != null
                              ? 'Generated'
                              : 'Not generated',
                        ),
                      ],
                    ),
                  ],
                ),
              ),
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildInfoRow(String label, String value) {
    return Padding(
      padding: const EdgeInsets.only(bottom: 8),
      child: Row(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          SizedBox(
            width: 120,
            child: Text(
              '$label:',
              style: const TextStyle(fontWeight: FontWeight.w500),
            ),
          ),
          Expanded(
            child: Text(value),
          ),
        ],
      ),
    );
  }
}
