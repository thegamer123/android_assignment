package com.bouraoui.ocrtest.ui.fragment.library

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bouraoui.ocrtest.R
import com.bouraoui.ocrtest.databinding.FragmentLibraryBinding
import com.bouraoui.ocrtest.ui.MainActivity
import com.bouraoui.ocrtest.ui.fragment.capture.CaptureFragment
import com.bouraoui.ocrtest.ui.fragment.library.adapter.FilesAdapter
import com.bouraoui.ocrtest.ui.fragment.library.bottomSheet.FileBottomSheet
import dagger.hilt.android.AndroidEntryPoint
import java.io.File


@AndroidEntryPoint
class LibraryFragment : Fragment() {

    private lateinit var textOutputDirectory: File
    lateinit var binding: FragmentLibraryBinding

    val adapter = FilesAdapter(emptyList<File>().toMutableList()) {
        handleFileClick(it)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
        populateAdapter()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentLibraryBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        textOutputDirectory = MainActivity.getTextFileOutputDirectory(requireContext())
        binding.fileRV.adapter = adapter
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun populateAdapter() {
        adapter.filesList =
            textOutputDirectory.listFiles()?.toMutableList() ?: emptyList<File>().toMutableList()

        adapter.notifyDataSetChanged()
    }

    private fun handleFileClick(file: File?) {
        if (file != null)
            FileBottomSheet.newInstance(requireActivity(), file).showBottomSheet()
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            LibraryFragment()
    }
}