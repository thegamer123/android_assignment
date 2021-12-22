package com.bouraoui.ocrtest.data.model

import com.google.gson.annotations.SerializedName

data class Response(
    @SerializedName("paragraphs")
    val paragraphs: MutableList<ParagraphModel>
)