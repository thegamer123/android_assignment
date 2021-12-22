package com.bouraoui.ocrtest.data.model

import com.google.gson.annotations.SerializedName

data class ParagraphModel(
    @SerializedName("paragraph") val paragraph: String?,
    @SerializedName("language") val language: String?
)