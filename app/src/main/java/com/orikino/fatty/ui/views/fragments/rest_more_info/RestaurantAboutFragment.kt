package com.orikino.fatty.ui.views.fragments.rest_more_info

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.card.MaterialCardView
import com.orikino.fatty.R
import com.orikino.fatty.databinding.FragmentRestaurantAboutBinding

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class RestaurantAboutFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null

    private var _binding : FragmentRestaurantAboutBinding? = null

    var isCheckAbout = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentRestaurantAboutBinding.inflate(inflater, container, false)
        return (_binding?.root)
    }

    companion object {

        fun newInstance(/*param1: String, param2: String*/) =
            RestaurantAboutFragment().apply {
                /*arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }*/
            }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /*_binding?.cvOrder?.setOnClickListener {
            isCheckAbout = true
            _binding?.flAbout?.show()
            _binding?.flReviews?.gone()
            checkUpdate()
        }

        _binding?.cvMessage?.setOnClickListener {
            isCheckAbout = false
            _binding?.flAbout?.gone()
            _binding?.flReviews?.show()
            checkUpdate()
        }*/
    }


    private fun checkUpdate() {
        /*if (isCheckAbout) {
            //setUpOrderNotiList()
            _binding?.cvOrder?.let { viewChecked(it,_binding!!.tvOrder) }
            _binding?.cvMessage?.let { viewUnChecked(it,_binding!!.tvMessage) }

        }else {
            //setUpMessageNotiList()
            _binding?.cvMessage?.let { viewChecked(it,_binding!!.tvMessage) }
            _binding?.cvOrder?.let { viewUnChecked(it,_binding!!.tvOrder) }
        }*/
    }

    private fun viewChecked(view: MaterialCardView, title : TextView) {
        view.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.check_card_bg))
        title.setTextColor(ContextCompat.getColor(requireContext(), R.color.fattyPrimary))

    }

    private fun viewUnChecked(view : MaterialCardView, title : TextView) {
        view.setCardBackgroundColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.surfacePrimary02
            )
        )
        title.setTextColor(ContextCompat.getColor(requireContext(), R.color.textPrimary02))

    }
}