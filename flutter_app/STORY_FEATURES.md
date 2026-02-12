# Story and Character Features Guide

## Overview

The MyEnglishNote Flutter app now includes AI-powered story generation features that allow users to create stories from photos with custom characters.

## Features

### 1. Character Management

Create and manage characters that will appear in your stories.

#### Creating a Character
1. Go to Profile tab
2. Tap "Manage Characters"
3. Tap the + button
4. Fill in character details:
   - **Name** (required): The character's name
   - **Description**: Brief description of the character
   - **Avatar URL**: Link to character's avatar image
   - **Personality**: Description of character's personality traits
   - **Background**: Character's background story

#### Editing a Character
1. In Character List, tap the edit icon on any character
2. Update the fields you want to change
3. Tap "Update Character"

#### Deleting a Character
1. In Character List, tap the delete icon on any character
2. Confirm deletion
3. Note: Deleting a character won't delete associated stories

### 2. Story Generation

Generate AI stories from photos with your custom characters.

#### How to Generate a Story
1. Go to Stories tab (second tab in bottom navigation)
2. Tap the + button
3. Select an image:
   - Take a photo with camera
   - Choose from gallery
4. Select a character from your list
5. Optionally enter a title
6. Tap "Generate Story"
7. Wait for AI to generate the story

#### Story Generation API
The app sends:
- Image in base64 format
- Selected character ID
- Optional title

The backend AI processes the image and generates a story featuring the selected character.

### 3. Story Management

#### Viewing Stories
- Stories tab shows all your generated stories
- Each story card displays:
  - Title
  - Character info (name and avatar)
  - Content preview (first 3 lines)
  - Creation date
  - View count
  - Favorite status

#### Story Details
Tap any story to see:
- Full image
- Character information
- Complete story content
- Metadata (creation date, view count)

#### Editing a Story
1. Open story details
2. Tap the edit icon
3. Modify title or content
4. Tap the checkmark to save

#### Favoriting Stories
- Tap the heart icon on a story card or in story details
- Favorite stories can be filtered in the story list

#### Sharing Stories
1. Open story details
2. Tap the share icon
3. Share link is copied to clipboard
4. Share the link with others

#### Deleting a Story
1. In Story List, tap the delete icon on a story
2. Confirm deletion

### 4. Filtering Stories

In the Story List screen:
1. Tap the filter icon in the top right
2. Options:
   - Show only favorites
   - Filter by character (future enhancement)
3. Tap "Clear filters" to reset

## API Endpoints Used

### Story APIs
- `POST /api/story/generate` - Generate story from image
- `GET /api/story/list` - Get story list
- `GET /api/story/{id}` - Get story details
- `PUT /api/story/{id}` - Update story
- `DELETE /api/story/{id}` - Delete story
- `POST /api/story/{id}/favorite` - Add to favorites
- `DELETE /api/story/{id}/favorite` - Remove from favorites
- `POST /api/story/{id}/share` - Generate share link

### Character APIs
- `GET /api/character/list` - Get character list
- `POST /api/character/add` - Create character
- `GET /api/character/{id}` - Get character details
- `PUT /api/character/{id}` - Update character
- `DELETE /api/character/{id}` - Delete character

### Search APIs
- `POST /api/search/stories` - Search stories
- `POST /api/search/characters` - Search characters
- `POST /api/search/all` - Global search

## Permissions Required

### Android
- `INTERNET` - Network access
- `CAMERA` - Taking photos
- `READ_EXTERNAL_STORAGE` - Gallery access
- `READ_MEDIA_IMAGES` - Media access (Android 13+)

### iOS
- `NSCameraUsageDescription` - Camera access
- `NSPhotoLibraryUsageDescription` - Photo library access

## Troubleshooting

### Camera/Gallery Issues
1. Check app permissions in device settings
2. Ensure image_picker plugin is properly installed
3. For Android emulator, use AVD with camera support

### Story Generation Issues
1. Verify backend is running and accessible
2. Check network connection
3. Ensure character exists before generating
4. Image size should be reasonable (<5MB recommended)

### Display Issues
1. Check that base URL is correctly configured
2. Ensure avatar URLs are accessible
3. Try refreshing the list (pull down)

## Best Practices

1. **Character Creation**
   - Create characters before generating stories
   - Use descriptive names and personalities
   - Add avatar URLs for better visual experience

2. **Story Generation**
   - Use clear, well-lit photos for better results
   - Choose appropriate characters for your photos
   - Add meaningful titles to organize your stories

3. **Performance**
   - Story generation may take 10-30 seconds
   - Keep images under 5MB for faster upload
   - Use pagination for large story collections

## Future Enhancements

Potential improvements:
- Search functionality for stories
- Filter stories by character
- Story categories/tags
- Export stories to PDF
- Offline story viewing
- Story templates
- Multiple character selection per story
