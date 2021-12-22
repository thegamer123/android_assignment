package com.bouraoui.ocrtest.ui.fragment.home

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.bouraoui.ocrtest.R
import com.bouraoui.ocrtest.databinding.FragmentHomeBinding
import com.bouraoui.ocrtest.ui.fragment.capture.CaptureFragmentViewModel
import com.bouraoui.ocrtest.ui.fragment.home.adapter.HomeFragmentsAdapter
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class HomeFragment : Fragment() {

    private lateinit var adapter: HomeFragmentsAdapter
    lateinit var binding: FragmentHomeBinding

    private val captureFragmentViewModel: CaptureFragmentViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initContent()
    }

    private fun initContent() {
        adapter = HomeFragmentsAdapter(this@HomeFragment)
        binding.pager.adapter = adapter

        TabLayoutMediator(binding.tabLayout, binding.pager) { tab, position ->
            tab.text = when (position) {
                0 -> {
                    getString(R.string.capture_label)
                }
                else -> {
                    getString(R.string.library_label)
                }
            }
        }.attach()

        captureFragmentViewModel.navigateLiveData.observe(viewLifecycleOwner, {
            if (it)
                showSnackBar()
        })

        binding.pager.currentItem = 1
    }

    private fun showSnackBar() {
        val snackbar = Snackbar.make(
            binding.root,
            R.string.text_saved_label,
            Snackbar.LENGTH_INDEFINITE
        )
        snackbar.view.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
            .setTextColor(Color.BLACK)
        snackbar.view.setBackgroundColor(Color.WHITE)
        snackbar.setAction(getString(R.string.go_to_library_label)) {
            binding.tabLayout.getTabAt(1)?.select()
        }
        snackbar.show()
    }

}