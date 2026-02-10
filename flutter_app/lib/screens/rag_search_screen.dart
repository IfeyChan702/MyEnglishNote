import 'package:flutter/material.dart';
import '../models/rag_response_model.dart';
import '../models/note_model.dart';
import '../services/api_service.dart';
import '../widgets/rag_result_card.dart';

/// Screen for RAG search and AI-powered Q&A
class RagSearchScreen extends StatefulWidget {
  const RagSearchScreen({super.key});

  @override
  State<RagSearchScreen> createState() => _RagSearchScreenState();
}

class _RagSearchScreenState extends State<RagSearchScreen> {
  final _questionController = TextEditingController();
  final _apiService = ApiService();
  
  bool _isLoading = false;
  RAGResponseModel? _response;
  List<NoteModel> _searchResults = [];
  String? _errorMessage;

  @override
  void dispose() {
    _questionController.dispose();
    super.dispose();
  }

  Future<void> _performSearch() async {
    if (_questionController.text.trim().isEmpty) {
      _showError('Please enter a question');
      return;
    }

    setState(() {
      _isLoading = true;
      _errorMessage = null;
      _response = null;
      _searchResults.clear();
    });

    try {
      final response = await _apiService.ragAnswer(
        _questionController.text.trim(),
      );

      setState(() {
        _response = response;
        _searchResults = response.relatedNotes ?? [];
      });
    } catch (e) {
      setState(() {
        _errorMessage = e.toString();
      });
    } finally {
      setState(() => _isLoading = false);
    }
  }

  void _showError(String message) {
    ScaffoldMessenger.of(context).showSnackBar(
      SnackBar(content: Text(message), backgroundColor: Colors.red),
    );
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('RAG Search'),
      ),
      body: Column(
        children: [
          Padding(
            padding: const EdgeInsets.all(16),
            child: Row(
              children: [
                Expanded(
                  child: TextField(
                    controller: _questionController,
                    decoration: const InputDecoration(
                      hintText: 'Ask a question about your notes...',
                      border: OutlineInputBorder(),
                      prefixIcon: Icon(Icons.search),
                    ),
                    onSubmitted: (_) => _performSearch(),
                  ),
                ),
                const SizedBox(width: 8),
                IconButton(
                  icon: const Icon(Icons.send),
                  onPressed: _isLoading ? null : _performSearch,
                  style: IconButton.styleFrom(
                    backgroundColor: Colors.blue,
                    foregroundColor: Colors.white,
                    padding: const EdgeInsets.all(16),
                  ),
                ),
              ],
            ),
          ),
          if (_isLoading)
            const Expanded(
              child: Center(
                child: CircularProgressIndicator(),
              ),
            )
          else if (_errorMessage != null)
            Expanded(
              child: Center(
                child: Column(
                  mainAxisAlignment: MainAxisAlignment.center,
                  children: [
                    const Icon(Icons.error_outline,
                        size: 60, color: Colors.red),
                    const SizedBox(height: 16),
                    Text(
                      'Error',
                      style: Theme.of(context).textTheme.titleLarge,
                    ),
                    const SizedBox(height: 8),
                    Padding(
                      padding: const EdgeInsets.symmetric(horizontal: 32),
                      child: Text(
                        _errorMessage!,
                        textAlign: TextAlign.center,
                      ),
                    ),
                  ],
                ),
              ),
            )
          else if (_response != null)
            Expanded(
              child: ListView(
                padding: const EdgeInsets.all(16),
                children: [
                  if (_response!.answer != null) ...[
                    Card(
                      color: Colors.blue[50],
                      child: Padding(
                        padding: const EdgeInsets.all(16),
                        child: Column(
                          crossAxisAlignment: CrossAxisAlignment.start,
                          children: [
                            Row(
                              children: [
                                const Icon(Icons.psychology,
                                    color: Colors.blue),
                                const SizedBox(width: 8),
                                Text(
                                  'AI Answer',
                                  style: Theme.of(context)
                                      .textTheme
                                      .titleLarge
                                      ?.copyWith(color: Colors.blue[900]),
                                ),
                              ],
                            ),
                            const SizedBox(height: 12),
                            Text(
                              _response!.answer!,
                              style: const TextStyle(
                                fontSize: 16,
                                height: 1.5,
                              ),
                            ),
                            if (_response!.processingTime != null) ...[
                              const SizedBox(height: 8),
                              Text(
                                'Processing time: ${_response!.processingTime}ms',
                                style: TextStyle(
                                  fontSize: 12,
                                  color: Colors.grey[600],
                                ),
                              ),
                            ],
                          ],
                        ),
                      ),
                    ),
                    const SizedBox(height: 16),
                  ],
                  if (_searchResults.isNotEmpty) ...[
                    Padding(
                      padding: const EdgeInsets.symmetric(vertical: 8),
                      child: Text(
                        'Related Notes (${_searchResults.length})',
                        style: Theme.of(context).textTheme.titleMedium,
                      ),
                    ),
                    ..._searchResults.map(
                      (note) => RagResultCard(note: note),
                    ),
                  ] else ...[
                    const Center(
                      child: Padding(
                        padding: EdgeInsets.all(32),
                        child: Text(
                          'No related notes found',
                          style: TextStyle(color: Colors.grey),
                        ),
                      ),
                    ),
                  ],
                ],
              ),
            )
          else
            Expanded(
              child: Center(
                child: Column(
                  mainAxisAlignment: MainAxisAlignment.center,
                  children: [
                    Icon(Icons.search, size: 80, color: Colors.grey[300]),
                    const SizedBox(height: 16),
                    Text(
                      'Search your notes with AI',
                      style: TextStyle(
                        fontSize: 18,
                        color: Colors.grey[600],
                      ),
                    ),
                    const SizedBox(height: 8),
                    Padding(
                      padding: const EdgeInsets.symmetric(horizontal: 48),
                      child: Text(
                        'Ask questions about your notes and get AI-powered answers',
                        textAlign: TextAlign.center,
                        style: TextStyle(color: Colors.grey[500]),
                      ),
                    ),
                  ],
                ),
              ),
            ),
        ],
      ),
    );
  }
}
