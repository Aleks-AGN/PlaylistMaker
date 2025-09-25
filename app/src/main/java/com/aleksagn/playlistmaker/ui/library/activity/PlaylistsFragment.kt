package com.aleksagn.playlistmaker.ui.library.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.aleksagn.playlistmaker.databinding.FragmentPlaylistsBinding
import com.aleksagn.playlistmaker.ui.library.view_model.PlaylistsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class PlaylistsFragment : Fragment() {

    companion object {
        private const val KEY = "key"

        fun newInstance(key: String) = PlaylistsFragment().apply {
            arguments = Bundle().apply {
                putString(KEY, key)
            }
        }
    }

    private val playlistsViewModel: PlaylistsViewModel by viewModel {
        parametersOf(requireArguments().getString(KEY))
    }

    private lateinit var binding: FragmentPlaylistsBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = FragmentPlaylistsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}
