package com.aleksagn.playlistmaker.ui.library

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.aleksagn.playlistmaker.databinding.FragmentFavoritesBinding
import com.aleksagn.playlistmaker.presentation.library.FavoritesViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class FavoritesFragment : Fragment() {

    companion object {
        private const val KEY = "key"

        fun newInstance(key: String) = FavoritesFragment().apply {
            arguments = Bundle().apply {
                putString(KEY, key)
            }
        }
    }

    private val favoritesViewModel: FavoritesViewModel by viewModel {
        parametersOf(requireArguments().getString(KEY))
    }

    private var _binding: FragmentFavoritesBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        _binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}
