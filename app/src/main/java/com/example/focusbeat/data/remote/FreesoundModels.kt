package com.example.focusbeat.data.remote

import com.google.gson.annotations.SerializedName

data class FreesoundResponse(
    val results: List<FreesoundSound> = emptyList()
)

data class FreesoundSound(
    val id: Int?,
    val name: String?,
    val username: String?,
    val previews: FreesoundPreviews?,
    val duration: Double?
)

data class FreesoundPreviews(
    @SerializedName("preview-hq-mp3")
    val previewHqMp3: String?,

    @SerializedName("preview-lq-mp3")
    val previewLqMp3: String?
)