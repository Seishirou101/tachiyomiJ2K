package eu.kanade.tachiyomi.data.track.mangaupdates

import eu.kanade.tachiyomi.data.database.models.Track
import eu.kanade.tachiyomi.data.track.mangaupdates.dto.Context
import eu.kanade.tachiyomi.data.track.mangaupdates.dto.ListItem
import eu.kanade.tachiyomi.data.track.mangaupdates.dto.Rating
import eu.kanade.tachiyomi.data.track.mangaupdates.dto.Record
import eu.kanade.tachiyomi.network.DELETE
import eu.kanade.tachiyomi.network.GET
import eu.kanade.tachiyomi.network.POST
import eu.kanade.tachiyomi.network.PUT
import eu.kanade.tachiyomi.network.awaitSuccess
import eu.kanade.tachiyomi.network.parseAs
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.add
import kotlinx.serialization.json.addJsonObject
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonObject
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.RequestBody.Companion.toRequestBody
import timber.log.Timber
import uy.kohesive.injekt.injectLazy

class MangaUpdatesApi(
    interceptor: MangaUpdatesInterceptor,
    private val client: OkHttpClient,
) {
    private val json: Json by injectLazy()

    private val baseUrl = "https://api.mangaupdates.com"
    private val contentType = "application/vnd.api+json".toMediaType()

    private val authClient =
        client
            .newBuilder()
            .addInterceptor(interceptor)
            .build()

    suspend fun getSeriesListItem(track: Track): Pair<ListItem, Rating?> {
        val listItem =
            with(json) {
                authClient
                    .newCall(
                        GET(
                            url = "$baseUrl/v1/lists/series/${track.media_id}",
                        ),
                    ).awaitSuccess()
                    .parseAs<ListItem>()
            }

        val rating = getSeriesRating(track)

        return listItem to rating
    }

    suspend fun addSeriesToList(track: Track) {
        val body = createTrackBody(track)
        authClient
            .newCall(
                POST(
                    url = "$baseUrl/v1/lists/series",
                    body = body.toString().toRequestBody(contentType),
                ),
            ).awaitSuccess()
    }

    suspend fun removeSeriesFromList(track: Track): Boolean {
        val body =
            buildJsonArray {
                add(track.media_id)
            }
        authClient
            .newCall(
                POST(
                    url = "$baseUrl/v1/lists/series/delete",
                    body = body.toString().toRequestBody(contentType),
                ),
            ).awaitSuccess()
            .let { return it.code == 200 }
    }

    suspend fun updateSeriesListItem(track: Track) {
        val body = createTrackBody(track)
        authClient
            .newCall(
                POST(
                    url = "$baseUrl/v1/lists/series/update",
                    body = body.toString().toRequestBody(contentType),
                ),
            ).awaitSuccess()

        updateSeriesRating(track)
    }

    private fun createTrackBody(track: Track) =
        buildJsonArray {
            addJsonObject {
                putJsonObject("series") {
                    put("id", track.media_id)
                }
                put("list_id", track.status)
                putJsonObject("status") {
                    put("chapter", track.last_chapter_read.toInt())
                }
            }
        }

    private suspend fun getSeriesRating(track: Track): Rating? =
        try {
            with(json) {
                authClient
                    .newCall(
                        GET(
                            url = "$baseUrl/v1/series/${track.media_id}/rating",
                        ),
                    ).awaitSuccess()
                    .parseAs<Rating>()
            }
        } catch (e: Exception) {
            null
        }

    private suspend fun updateSeriesRating(track: Track) {
        if (track.score != 0f) {
            val body =
                buildJsonObject {
                    put("rating", track.score)
                }
            authClient
                .newCall(
                    PUT(
                        url = "$baseUrl/v1/series/${track.media_id}/rating",
                        body = body.toString().toRequestBody(contentType),
                    ),
                ).awaitSuccess()
        } else {
            authClient
                .newCall(
                    DELETE(
                        url = "$baseUrl/v1/series/${track.media_id}/rating",
                    ),
                ).awaitSuccess()
        }
    }

    suspend fun search(query: String): List<Record> {
        val body =
            buildJsonObject {
                put("search", query)
                put(
                    "filter_types",
                    buildJsonArray {
                        add("drama cd")
                        add("novel")
                    },
                )
            }
        return with(json) {
            client
                .newCall(
                    POST(
                        url = "$baseUrl/v1/series/search",
                        body = body.toString().toRequestBody(contentType),
                    ),
                ).awaitSuccess()
                .parseAs<JsonObject>()
                .let { obj ->
                    obj["results"]?.jsonArray?.map { element ->
                        json.decodeFromJsonElement<Record>(element.jsonObject["record"]!!)
                    }
                }.orEmpty()
        }
    }

    suspend fun authenticate(
        username: String,
        password: String,
    ): Context? {
        val body =
            buildJsonObject {
                put("username", username)
                put("password", password)
            }
        return with(json) {
            client
                .newCall(
                    PUT(
                        url = "$baseUrl/v1/account/login",
                        body = body.toString().toRequestBody(contentType),
                    ),
                ).awaitSuccess()
                .parseAs<JsonObject>()
                .let { obj ->
                    try {
                        json.decodeFromJsonElement<Context>(obj["context"]!!)
                    } catch (e: Exception) {
                        Timber.e(e)
                        null
                    }
                }
        }
    }
}
