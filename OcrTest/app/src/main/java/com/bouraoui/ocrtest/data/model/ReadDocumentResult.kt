package com.bouraoui.ocrtest.data.model

import com.google.gson.annotations.SerializedName

data class ReadDocumentResult(
    @SerializedName("response")
    val response: Response?
)