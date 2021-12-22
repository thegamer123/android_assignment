package com.bouraoui.ocrtest.ui.fragment.capture

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bouraoui.ocrtest.data.model.ReadDocumentResult
import com.bouraoui.ocrtest.repository.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import java.io.File
import javax.inject.Inject

@HiltViewModel
class CaptureFragmentViewModel @Inject constructor(private val repository: Repository) :
    ViewModel() {

    val navigateLiveData = MutableLiveData<Boolean>()

    fun readDocument(photoFile: File, callback: (ReadDocumentResult?, Int) -> Unit) {
        viewModelScope.launch {
            try {
                val data = repository.readDocument(photoFile)
                when (data.isSuccessful) {
                    true -> {
                        val result = data.body()
                        if (result?.response != null) {
                            callback.invoke(result, data.code())
                        } else {
                            callback.invoke(null, 507)
                        }
                    }
                    else -> {
                        callback.invoke(null, data.code())
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                callback.invoke(null, 405)
            }

        }
    }

}