package com.bouraoui.ocrtest.repository

import com.bouraoui.ocrtest.data.model.ReadDocumentResult
import com.bouraoui.ocrtest.data.network.ApiService
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import java.io.File


class Repository(private val apiService: ApiService) {

    suspend fun readDocument(photo: File): Response<ReadDocumentResult> {

        val data = photo.asRequestBody("image/*".toMediaTypeOrNull())
//        val requestBody: RequestBody = MultipartBody.Builder()
//            .setType(MultipartBody.FORM)
//            .addFormDataPart("photo", photo.name, data)
//            .build()
        // MultipartBody.Part is used to send also the actual file name
        val body: MultipartBody.Part =
            MultipartBody.Part.createFormData("photo", photo.name, data)

        return apiService.readDocument(body)

    }


}