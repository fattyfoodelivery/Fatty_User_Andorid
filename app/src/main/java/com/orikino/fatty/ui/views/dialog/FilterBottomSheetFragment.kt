package com.orikino.fatty.ui.views.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.orikino.fatty.R
import com.orikino.fatty.databinding.FragmentFilterBottomSheetBinding
import com.orikino.fatty.ui.views.activities.services.ServicesViewModel
import com.orikino.fatty.ui.views.base.BaseBottomSheet
import com.orikino.fatty.ui.views.delegate.FilterDelegate

class FilterBottomSheetFragment : BaseBottomSheet<ServicesViewModel>() {

    override fun getTheme(): Int {
        return R.style.FattyBottomSheetStyle
    }

    private lateinit var binding : FragmentFilterBottomSheetBinding

    private val viewModel : ServicesViewModel by viewModels()



    companion object {
        const val TAG = "FilterBottomSheet"
        var delegate : FilterDelegate? = null
        private var currentFilter : String = "recommended"
        fun newInstance(
            delegate: FilterDelegate,
            currentFilter : String
        ): FilterBottomSheetFragment {
            this.delegate = delegate
            this.currentFilter = currentFilter
            return FilterBottomSheetFragment()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFilterBottomSheetBinding.inflate(inflater, container, false)
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

    private fun initUi(){
        if (currentFilter == "recommended"){
            binding.rdoRecommended.isChecked = true
        }else{
            binding.rdoNearby.isChecked = true
        }
        binding.rgFilter.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.rdo_recommended -> {
                    currentFilter = "recommended"
                }

                R.id.rdo_nearby -> {
                    currentFilter = "nearby"
                }
            }
        }
        binding.btnUpdate.setOnClickListener {
            delegate?.onClickFilterApply(currentFilter)
            dialog?.dismiss()
        }
        binding.ivClose.setOnClickListener {
            dialog?.dismiss()
        }
    }
}