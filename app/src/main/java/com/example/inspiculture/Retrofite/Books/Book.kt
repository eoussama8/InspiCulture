package com.example.inspiculture.Retrofite.Books

data class BookItem(
    val id: String,
    val volumeInfo: VolumeInfo,
    val accessInfo: AccessInfo
)

data class VolumeInfo(
    val categories: List<String>?,
    val imageLinks: ImageLinks?,
    val language: String?,
    val description: String?,
    val country: String?,
    val title: String,
    val authors: List<String>?
)

data class AccessInfo(
    val pdf: Pdf?
)

data class ImageLinks(
    val smallThumbnail: String?,
    val thumbnail: String?
)

data class Pdf(
    val isAvailable: Boolean,
    val downloadLink: String?
)

data class Book(
    val id: String,
    val categories: List<String>?, // ← keep it as List
    val imageLinks: ImageLinks?,
    val language: String?,
    val pdf: Pdf?,
    val description: String?,
    val isFavoris: Boolean,
    val country: String?,
    val title: String,
    val authors: List<String>?
)
