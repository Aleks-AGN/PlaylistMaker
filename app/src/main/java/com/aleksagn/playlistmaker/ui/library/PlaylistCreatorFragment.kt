package com.aleksagn.playlistmaker.ui.library

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.res.ResourcesCompat
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

    private val playlistCreatorViewModel: PlaylistCreatorViewModel by viewModel()

    private var titleTextWatcher: TextWatcher? = null
    private var descriptionTextWatcher: TextWatcher? = null

    private var _binding: FragmentPlaylistCreatorBinding? = null
    private val binding get() = _binding!!

    private var playlistTitle = ""
    private var playlistDescription = ""
    private var imageUri: Uri? = null

    private val pickImage =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri: Uri? ->
            if (uri != null) {
                imageUri = uri
                Glide.with(this@PlaylistCreatorFragment)
                    .load(uri)
                    .fitCenter()
                    .transform(
                        CenterCrop(),
                        RoundedCorners(dpToPx(8f, requireContext()))
                    )
                    .into(binding.playlistCover)
            } else {
                Toast.makeText(requireContext(), requireContext().getString(R.string.empty_image), Toast.LENGTH_LONG).show()
            }
        }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        _binding = FragmentPlaylistCreatorBinding.inflate(inflater, container, false)
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

        binding.playlistCover.setOnClickListener {
            pickImage.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        binding.playlistCreatorToolbar.setNavigationOnClickListener {
            handleBackPressed()
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                handleBackPressed()
            }
        })

        binding.playlistTitleField.setText("")
        binding.playlistTitleField.doOnTextChanged { text, _, _, _ ->
            playlistTitle = text?.toString()?.trim() ?: ""
                if (playlistTitle.isNotEmpty()) {
                    binding.btnCreatePlaylist.setEnabled(true)
                } else {
                    binding.btnCreatePlaylist.setEnabled(false)
                }
        }

        binding.playlistDescriptionField.setText("")
        binding.playlistDescriptionField.doOnTextChanged { text, _, _, _ ->
            playlistDescription = text?.toString() ?: ""
        }

        binding.btnCreatePlaylist.setOnClickListener {
            savePlaylist()
            val message = requireContext().getString(R.string.playlist) + " " +
                    playlistTitle + " " + requireContext().getString(R.string.create)
            Snackbar.make(it, message, Snackbar.LENGTH_LONG).show()
            findNavController().navigateUp()
        }
    }

    private fun handleBackPressed() {
        if (!binding.playlistTitleField.text.isNullOrEmpty() ||
            !binding.playlistDescriptionField.text.isNullOrEmpty()
            || imageUri != null) {
            confirmDialog()
        } else {
            findNavController().navigateUp()
        }
    }

    private fun savePlaylist() {
        playlistCreatorViewModel.savePlaylist(playlistTitle, playlistDescription, imageUri)

        if (imageUri != null){
            playlistCreatorViewModel.saveImage(imageUri!!, playlistTitle)
        }
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
