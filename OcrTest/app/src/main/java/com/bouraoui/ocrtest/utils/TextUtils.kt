package com.bouraoui.ocrtest.utils

object TextUtils {


    fun String.formatTitle(): String {
        val title = this.split(".")
        val titleWithoutExt = title[0].split("_")
        val date = titleWithoutExt[0].replace("-", "/")
        val time = titleWithoutExt[1].split(":")
        return "$date ${time[0]}:${time[1]}"
    }
}