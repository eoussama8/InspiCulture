package com.example.inspiculture.Retrofite.Shows

import kotlinx.serialization.Serializable

@Serializable
data class Show(
    val id: Int,
    val title: String,
    val release_date: String,
    val poster_path: String? = null,
    val overview: String? = null,
    val isFavoris: Boolean,
    val genre_ids: List<Int> = emptyList(),
    @Transient
    val genreNames: List<String> = emptyList()  // Mark as non-serializable
) {
    val genreIds: List<Int>? get() = genre_ids
}


@Serializable
data class ProductionCompany(
    val id: Int,
    val name: String
)

@Serializable
data class WatchProviders(
    val results: Map<String, CountryProviders>? = null
)

@Serializable
data class CountryProviders(
    val link: String? = null,
    val flatrate: List<Provider>? = null,
    val buy: List<Provider>? = null,
    val rent: List<Provider>? = null
)

@Serializable
data class Provider(
    val provider_name: String,
    val provider_id: Int,
    val logo_path: String? = null
)

@Serializable
data class Credits(
    val cast: List<CastMember>
)

@Serializable
data class CastMember(
    val name: String,
    val profile_path: String? = null
)
@Serializable
data class ShowDetails(
    val id: Int,
    val title: String,
    val poster_path: String?,
    val overview: String?,
    val release_date: String?,
    val runtime: Int? = null,
    val genres: List<Genre>? = null,
    val production_companies: List<ProductionCompany>? = null,
    val budget: Long? = null,
    val homepage: String? = null,
    val credits: Credits? = null,
    val watch_providers: WatchProviders? = null
)

@Serializable
data class GenreResponse(
    val genres: List<Genre>
)
@Serializable

data class Genre(
    val id: Int,
    val name: String
)


