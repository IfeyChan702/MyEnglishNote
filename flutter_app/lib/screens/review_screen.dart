import 'package:flutter/material.dart';
import '../models/review_record_model.dart';
import '../models/note_model.dart';
import '../services/api_service.dart';
import '../widgets/review_item_card.dart';

/// Screen for SRS review
class ReviewScreen extends StatefulWidget {
  const ReviewScreen({super.key});

  @override
  State<ReviewScreen> createState() => _ReviewScreenState();
}

class _ReviewScreenState extends State<ReviewScreen> {
  final _apiService = ApiService();
  
  ReviewRecordModel? _currentReview;
  NoteModel? _currentNote;
  bool _isLoading = false;
  Map<String, dynamic>? _stats;

  @override
  void initState() {
    super.initState();
    _loadNextReview();
    _loadStats();
  }

  Future<void> _loadNextReview() async {
    setState(() => _isLoading = true);

    try {
      final reviews = await _apiService.getNextReviewItems(limit: 1);
      
      if (reviews.isNotEmpty) {
        final review = reviews.first;
        setState(() => _currentReview = review);
        
        // Load the note content
        try {
          final note = await _apiService.getNoteById(review.noteId);
          setState(() => _currentNote = note);
        } catch (e) {
          _showError('Failed to load note: ${e.toString()}');
        }
      } else {
        setState(() {
          _currentReview = null;
          _currentNote = null;
        });
      }
    } catch (e) {
      _showError('Failed to load review: ${e.toString()}');
    } finally {
      setState(() => _isLoading = false);
    }
  }

  Future<void> _loadStats() async {
    try {
      final stats = await _apiService.getReviewStats();
      setState(() => _stats = stats);
    } catch (e) {
      // Silently fail stats loading
    }
  }

  Future<void> _recordReview(int quality) async {
    if (_currentReview == null) return;

    setState(() => _isLoading = true);

    try {
      await _apiService.recordReview(_currentReview!.noteId, quality);
      _showSuccess('Review recorded!');
      
      // Load next review
      await _loadNextReview();
      await _loadStats();
    } catch (e) {
      _showError('Failed to record review: ${e.toString()}');
      setState(() => _isLoading = false);
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
    return Scaffold(
      appBar: AppBar(
        title: const Text('Review'),
        actions: [
          IconButton(
            icon: const Icon(Icons.refresh),
            onPressed: _loadNextReview,
          ),
          if (_stats != null)
            IconButton(
              icon: const Icon(Icons.bar_chart),
              onPressed: _showStatsDialog,
            ),
        ],
      ),
      body: _isLoading
          ? const Center(child: CircularProgressIndicator())
          : _currentReview == null || _currentNote == null
              ? Center(
                  child: Column(
                    mainAxisAlignment: MainAxisAlignment.center,
                    children: [
                      Icon(Icons.check_circle,
                          size: 80, color: Colors.green[300]),
                      const SizedBox(height: 16),
                      const Text(
                        'All caught up!',
                        style: TextStyle(
                          fontSize: 24,
                          fontWeight: FontWeight.bold,
                        ),
                      ),
                      const SizedBox(height: 8),
                      Text(
                        'No reviews due right now',
                        style: TextStyle(
                          fontSize: 16,
                          color: Colors.grey[600],
                        ),
                      ),
                      const SizedBox(height: 24),
                      if (_stats != null) ...[
                        _buildStatCard(
                          'Total Reviews',
                          _stats!['totalReviews']?.toString() ?? '0',
                        ),
                        const SizedBox(height: 8),
                        _buildStatCard(
                          'Due Today',
                          _stats!['dueToday']?.toString() ?? '0',
                        ),
                      ],
                    ],
                  ),
                )
              : SingleChildScrollView(
                  child: ReviewItemCard(
                    review: _currentReview!,
                    noteContent: _currentNote!.content,
                    onQualitySelected: _recordReview,
                  ),
                ),
    );
  }

  Widget _buildStatCard(String label, String value) {
    return Card(
      child: Padding(
        padding: const EdgeInsets.symmetric(horizontal: 32, vertical: 16),
        child: Column(
          children: [
            Text(
              value,
              style: const TextStyle(
                fontSize: 32,
                fontWeight: FontWeight.bold,
                color: Colors.blue,
              ),
            ),
            Text(
              label,
              style: TextStyle(
                fontSize: 14,
                color: Colors.grey[600],
              ),
            ),
          ],
        ),
      ),
    );
  }

  void _showStatsDialog() {
    if (_stats == null) return;

    showDialog(
      context: context,
      builder: (context) => AlertDialog(
        title: const Text('Review Statistics'),
        content: Column(
          mainAxisSize: MainAxisSize.min,
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            _buildStatRow('Total Reviews',
                _stats!['totalReviews']?.toString() ?? '0'),
            _buildStatRow(
                'Due Today', _stats!['dueToday']?.toString() ?? '0'),
            _buildStatRow('This Week',
                _stats!['dueThisWeek']?.toString() ?? '0'),
            _buildStatRow(
                'This Month', _stats!['dueThisMonth']?.toString() ?? '0'),
            _buildStatRow('Average Quality',
                _stats!['averageQuality']?.toStringAsFixed(2) ?? '0.00'),
          ],
        ),
        actions: [
          TextButton(
            onPressed: () => Navigator.pop(context),
            child: const Text('Close'),
          ),
        ],
      ),
    );
  }

  Widget _buildStatRow(String label, String value) {
    return Padding(
      padding: const EdgeInsets.symmetric(vertical: 4),
      child: Row(
        mainAxisAlignment: MainAxisAlignment.spaceBetween,
        children: [
          Text(label),
          Text(
            value,
            style: const TextStyle(fontWeight: FontWeight.bold),
          ),
        ],
      ),
    );
  }
}
