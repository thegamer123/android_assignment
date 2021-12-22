package com.bouraoui.ocrtest.ui.fragment.library.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bouraoui.ocrtest.databinding.FileRowBinding
import com.bouraoui.ocrtest.ui.fragment.capture.CaptureFragment
import com.bouraoui.ocrtest.utils.TextUtils.formatTitle
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class FilesAdapter(
    var filesList: MutableList<File>,
    var callback: (File?) -> Unit
) :
    RecyclerView.Adapter<FilesAdapter.ViewHolder>() {

    lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilesAdapter.ViewHolder {
        val v = FileRowBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        context = parent.context
        return ViewHolder(v, callback)
    }

    override fun onBindViewHolder(holder: FilesAdapter.ViewHolder, position: Int) {
        holder.bind(filesList[position])
    }

    override fun getItemCount(): Int {
        return filesList.size
    }

    inner class ViewHolder(
        var itemBinding: FileRowBinding,
        var callback: (File?) -> Unit
    ) : RecyclerView.ViewHolder(itemBinding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(file: File) {
            itemBinding.titleTV.text = file.name.formatTitle()

            itemBinding.root.setOnClickListener {
                callback(file)
            }
        }
    }

}