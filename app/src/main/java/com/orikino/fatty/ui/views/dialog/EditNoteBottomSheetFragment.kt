package com.orikino.fatty.ui.views.dialog

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.orikino.fatty.R
import com.orikino.fatty.databinding.FragmentEditNoteBottomSheetBinding
import com.orikino.fatty.domain.model.CreateFoodVO
import com.orikino.fatty.domain.view_model.OrderViewModel
import com.orikino.fatty.ui.views.base.BaseBottomSheet
import com.orikino.fatty.ui.views.delegate.EditNoteDelegate
import com.orikino.fatty.utils.CustomToast

class EditNoteBottomSheetFragment: BaseBottomSheet<OrderViewModel>(){
    override fun getTheme(): Int {
        return R.style.FattyBottomSheetStyle
    }

    private lateinit var binding : FragmentEditNoteBottomSheetBinding

    companion object {
        const val TAG = "FilterBottomSheet"
        var delegate: EditNoteDelegate? = null
        var foodVO = CreateFoodVO()
        var position : Int = 0

        fun newInstance(delegate: EditNoteDelegate, foodVO: CreateFoodVO, position : Int): EditNoteBottomSheetFragment {
            this.delegate = delegate
            this.foodVO = foodVO
            this.position = position
            return EditNoteBottomSheetFragment()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEditNoteBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.let {
            val sheet = it as BottomSheetDialog
            sheet.behavior.state = BottomSheetBehavior.STATE_EXPANDED
        }
        initUi()
    }

    @SuppressLint("SetTextI18n", "StringFormatMatches")
    private fun initUi() {
        if (!foodVO.food_note.isNullOrEmpty()) {
            binding.edtNote.setText(foodVO.food_note)
            binding.tvNoteCount.text = "${foodVO.food_note!!.length}/100"
            //enableButton()
        }
        enableButton()
        binding.ivClose.setOnClickListener {
            dialog?.dismiss()
        }

        binding.edtNote.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val trimmedNote = s?.toString()?.trim() ?: ""
                binding.tvNoteCount.text = "${trimmedNote.length}/100"
                if (trimmedNote.length > 100) {
                    binding.edtNote.clearFocus()
                    CustomToast(requireContext(),
                        "Note exceeds 100 characters!",false).createCustomToast()
                }
                if (trimmedNote.isNotEmpty()) {
                    //enableButton()
                } else {
                    //disableButton()
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })

        binding.btnUpdate.setOnClickListener {
            val note = binding.edtNote.text.toString().trim()
            foodVO.food_note = note
            delegate?.onEdit(foodVO, position)
            dialog?.dismiss()
        }
    }

    private fun enableButton(){
        binding.btnUpdate.isEnabled = true
        binding.btnUpdate.alpha = 1.0f
    }

    private fun disableButton(){
        binding.btnUpdate.isEnabled = false
        binding.btnUpdate.alpha = 0.5f
    }
}