package com.orikino.fatty.ui.views.fragments.rest_more_info

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.orikino.fatty.databinding.FragmentRestaurantReviewBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [RestaurantReviewFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class RestaurantReviewFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null


    private var _binding : FragmentRestaurantReviewBinding? = null

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
        _binding = FragmentRestaurantReviewBinding.inflate(inflater,container, false)
        return _binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        createRating()

    }

    private fun createRating() {
        /*val img = mutableListOf<Int>(R.drawable.ic_full_star_20dp,R.drawable.ic_full_star_20dp,R.drawable.ic_full_star_20dp,R.drawable.ic_full_star_20dp,R.drawable.ic_full_star_20dp,R.drawable.ic_empty_star_20dp)

        val imv = ImageView(requireContext())

        imv.layoutParams.width = 20
        imv.layoutParams.height = 20

        for (im in img.indices){
            imv.setImageResource(img[im])
        }

        _binding?.llReview?.addView(imv)*/
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment RestaurantReviewFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            RestaurantReviewFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}