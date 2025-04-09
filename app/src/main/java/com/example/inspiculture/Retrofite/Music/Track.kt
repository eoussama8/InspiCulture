package com.example.inspiculture.Retrofite.Music

// Category Response
data class CategoryResponse(
    val categories: Categories
)

data class Categories(
    val items: List<Category>
)
data class Album(
    val name: String,
    val images: List<Image> // Add this
)


data class Image( // New data class
    val url: String,
    val height: Int?,
    val width: Int?
)

data class Category(
    val id: String,
    val name: String
)

// Track Response (already defined, refined here)
data class TrackResponse(
    val tracks: Tracks
)

data class Tracks(
    val items: List<Track>
)

data class Track(
    val id: String,
    val name: String,
    val artists: List<Artist>,
    val album: Album
) {
    fun getArtistsString() = artists.joinToString(", ") { it.name }
}

data class TokenResponse(
    val access_token: String,
    val token_type: String,
    val expires_in: Int
)

data class Artist(
    val name: String
)



data class PlaylistResponse(
    val playlists: Playlists
)

data class Playlists(
    val items: List<Playlist>
)

data class Playlist(
    val id: String,
    val name: String,
    val tracks: PlaylistTracks
)

data class PlaylistTracks(
    val href: String, // URL to fetch full track list
    val items: List<Track> // May be empty, depending on API response
)
