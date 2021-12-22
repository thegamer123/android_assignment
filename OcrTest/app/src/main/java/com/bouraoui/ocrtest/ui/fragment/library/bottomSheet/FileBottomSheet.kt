package com.bouraoui.ocrtest.ui.fragment.library.bottomSheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import com.bouraoui.ocrtest.databinding.BottomSheetLayoutBinding
import com.bouraoui.ocrtest.utils.TextUtils.formatTitle
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.io.File

class FileBottomSheet(var currentActivity: FragmentActivity, var file: File) :
    BottomSheetDialogFragment() {

    companion object {
        fun newInstance(
            currentActivity: FragmentActivity,
            file: File
        ): FileBottomSheet =
            FileBottomSheet(currentActivity, file).apply {
                return this
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = BottomSheetLayoutBinding.inflate(inflater, container, false)
        view.titleTextView.text = file.name.formatTitle()
        var data = ""
        file.readLines().forEach {
            data += it.plus("\n")
        }
        view.fileDataTV.text = data
        return view.root
    }

    fun showBottomSheet() {
        this.show(
            currentActivity.supportFragmentManager,
            FileBottomSheet::class.java.name
        )
    }
}