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
                Toast.makeText(requireContext(), "Изображение не выбрано", Toast.LENGTH_LONG).show()
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
        titleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }
            override fun afterTextChanged(s: Editable?) { }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                playlistTitle = s?.toString() ?: ""
                if (playlistTitle.isNotEmpty()) {
                    binding.btnCreatePlaylist.setEnabled(true)
                } else {
                    binding.btnCreatePlaylist.setEnabled(false)
                }
            }
        }
        titleTextWatcher?.let { binding.playlistTitleField.addTextChangedListener(it) }

        binding.playlistDescriptionField.setText("")
        descriptionTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }
            override fun afterTextChanged(s: Editable?) { }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                playlistDescription = s?.toString() ?: ""
            }
        }
        descriptionTextWatcher?.let { binding.playlistDescriptionField.addTextChangedListener(it) }

        binding.btnCreatePlaylist.setOnClickListener {
            savePlaylist()
            showSnackBar()
            findNavController().navigateUp()
        }
    }

    private fun handleBackPressed() {
        if (binding.playlistTitleField.text.isNotEmpty() ||
            binding.playlistDescriptionField.text.isNotEmpty()
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

    private fun showSnackBar() {
        val snackbar = Snackbar.make(binding.root, "Плейлист $playlistTitle создан", Snackbar.LENGTH_LONG)
        val textView = snackbar.view.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
        val typeface = ResourcesCompat.getFont(requireContext(), R.font.ys_display_regular)
        textView.setTextSize(14f)
        textView.setTypeface(typeface)
        snackbar.show()
    }

    private fun dpToPx(dp: Float, context: Context): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            context.resources.displayMetrics).toInt()
    }
}
