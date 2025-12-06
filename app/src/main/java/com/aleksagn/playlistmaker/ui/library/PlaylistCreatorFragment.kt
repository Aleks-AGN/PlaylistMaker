package com.aleksagn.playlistmaker.ui.library

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.text.TextWatcher
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.os.bundleOf
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.aleksagn.playlistmaker.R
import com.aleksagn.playlistmaker.databinding.FragmentPlaylistCreatorBinding
import com.aleksagn.playlistmaker.presentation.library.PlaylistCreatorViewModel
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistCreatorFragment : Fragment() {

    companion object {
        private const val ARGS_PLAYLIST_ID = "playlist_id"

        fun createArgs(playlistId: Int): Bundle =
            bundleOf(ARGS_PLAYLIST_ID to playlistId)
    }

    private val playlistCreatorViewModel: PlaylistCreatorViewModel by viewModel()

    private var titleTextWatcher: TextWatcher? = null
    private var descriptionTextWatcher: TextWatcher? = null

    private var _binding: FragmentPlaylistCreatorBinding? = null
    private val binding get() = _binding!!

    private val pickImage =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri: Uri? ->
            setImage(uri)
        }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        _binding = FragmentPlaylistCreatorBinding.inflate(inflater, container, false)
        val playlistId = requireArguments().getInt(PlaylistCreatorFragment.ARGS_PLAYLIST_ID)
        playlistCreatorViewModel.setPlaylistData(playlistId)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        titleTextWatcher?.let { binding.playlistTitleField.removeTextChangedListener(it) }
        descriptionTextWatcher?.let { binding.playlistDescriptionField.removeTextChangedListener(it) }
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)

        if (playlistCreatorViewModel.getEditableStatus()) {
            binding.playlistCreatorToolbar.setTitle(playlistCreatorViewModel.getFragmentTitle())
            binding.btnCreatePlaylist.setText(playlistCreatorViewModel.getButtonText())

            val uri = playlistCreatorViewModel.getImageUri()
            if (uri != null && uri.toString().isNotEmpty()) {
                setImage(uri)
            } else {
                binding.playlistCover.setImageDrawable(requireContext().getDrawable(R.drawable.ic_playlist_creator_placeholder))
            }
        }

        binding.playlistCover.setOnClickListener {
            pickImage.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        binding.playlistCreatorToolbar.setNavigationOnClickListener {
            if (playlistCreatorViewModel.getEditableStatus()) {
                findNavController().navigateUp()
            } else {
                handleBackPressed()
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (playlistCreatorViewModel.getEditableStatus()) {
                    findNavController().navigateUp()
                } else {
                    handleBackPressed()
                }
            }
        })

        binding.playlistTitleField.setText(playlistCreatorViewModel.getPlaylistTitle())
        if (playlistCreatorViewModel.getPlaylistTitle().isNotEmpty()) {
            binding.btnCreatePlaylist.setEnabled(true)
        } else {
            binding.btnCreatePlaylist.setEnabled(false)
        }
        binding.playlistTitleField.doOnTextChanged { text, _, _, _ ->
            val playlistTitle = text?.toString()?.trim() ?: ""
            playlistCreatorViewModel.setPlaylistTitle(playlistTitle)
                if (playlistTitle.isNotEmpty()) {
                    binding.btnCreatePlaylist.setEnabled(true)
                } else {
                    binding.btnCreatePlaylist.setEnabled(false)
                }
        }

        binding.playlistDescriptionField.setText(playlistCreatorViewModel.getPlaylistDescription())
        binding.playlistDescriptionField.doOnTextChanged { text, _, _, _ ->
            val playlistDescription = text?.toString() ?: ""
            playlistCreatorViewModel.setPlaylistDescription(playlistDescription)
        }

        binding.btnCreatePlaylist.setOnClickListener {
            if (playlistCreatorViewModel.getPlaylistTitle().isNotEmpty()) {
                savePlaylist()
                val message = playlistCreatorViewModel.getConfirmPlaylistProcessMessage()
                Snackbar.make(it, message, Snackbar.LENGTH_LONG).show()
                findNavController().navigateUp()
            }
        }
    }

    private fun setImage(uri: Uri?) {
        if (uri != null) {
            playlistCreatorViewModel.setImageUri(uri)
            Glide.with(this@PlaylistCreatorFragment)
                .load(uri)
                .fitCenter()
                .transform(
                    CenterCrop(),
                    RoundedCorners(dpToPx(8f, requireContext()))
                )
                .into(binding.playlistCover)
        }
    }

    private fun handleBackPressed() {
        if (playlistCreatorViewModel.isPlaylistHasData()) {
            confirmDialog()
        } else {
            findNavController().navigateUp()
        }
    }

    private fun savePlaylist() {
        playlistCreatorViewModel.savePlaylist()
    }

    private fun confirmDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(requireContext().getString(R.string.playlist_creator_dialog_title))
            .setMessage(requireContext().getString(R.string.playlist_creator_dialog_message))
            .setNeutralButton(requireContext().getString(R.string.playlist_creator_dialog_cancel)) { dialog, which ->
            }
            .setPositiveButton(requireContext().getString(R.string.playlist_creator_dialog_finish)) { dialog, which ->
                findNavController().navigateUp()
            }
            .show()
    }

    private fun dpToPx(dp: Float, context: Context): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            context.resources.displayMetrics).toInt()
    }
}
