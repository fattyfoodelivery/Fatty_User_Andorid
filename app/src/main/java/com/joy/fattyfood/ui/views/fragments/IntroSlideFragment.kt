package com.joy.fattyfood.ui.views.fragments

import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.joy.fattyfood.R
import com.joy.fattyfood.databinding.FragmentIntroSlideBinding
import com.joy.fattyfood.domain.model.FattyIntroVO

private const val ARG_PARAM1 = "param1"

class IntroSlideFragment : Fragment() {

    private var position: Int? = null
    private lateinit var introList: MutableList<FattyIntroVO>

    private var introSlideBinding : FragmentIntroSlideBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            position = it.getInt(ARG_PARAM1)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        introSlideBinding = FragmentIntroSlideBinding.inflate(inflater,container,false)
        return (introSlideBinding?.root)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        introList = mutableListOf(
            FattyIntroVO(
                requireContext().resources.getString(R.string.intro_title_one),
                requireContext().resources.getString(R.string.intro_dec_one)
            ),
            FattyIntroVO(
                requireContext().resources.getString(R.string.intro_title_two),
                requireContext().resources.getString(R.string.intro_dec_one)
            ),
            FattyIntroVO(
                requireContext().resources.getString(R.string.intro_title_three),
                requireContext().resources.getString(R.string.intro_dec_one)
            )
        )

        val texts = introList
        val images = arrayListOf(R.drawable.intro_one, R.drawable.intro_two, R.drawable.intro_three)

        //introSlideBinding?.tvTitle?.text = texts[this.position ?: 0].introTile
        //introSlideBinding?.tvDesc?.text = texts[this.position ?: 0].introDes
        introSlideBinding?.imgIntroOne?.setImageResource(images[position ?: 0])


        //val introOne = introSlideBinding?.tvTitle?.text.toString()//texts[this.position ?: 0].introTile//requireContext().resources.getString(R.string.intro_title_one)
        //val introTwo = texts[this.position ?: 1].introTile//requireContext().resources.getString(R.string.intro_title_two)
        //val  introThree = texts[this.position ?: 2].introTile//requireContext().resources.getString(R.string.intro_title_three)
        //titleOne(introOne)
        //titleTwo(introTwo)
        //titleThree(introThree)




    }

    private fun titleOne(title : String) {
        val spannableString = SpannableString(title)
        val startIndex = title.indexOf("near you")
        val endIndex = startIndex + "near you".length
        spannableString.setSpan(
            ForegroundColorSpan(Color.parseColor("#FF6704")),
            startIndex,
            endIndex,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
    }

    private fun titleTwo(title : String) {
        /*val spannableString = SpannableString(title)
        val startIndex = title.indexOf("track")
        val endIndex = startIndex + "track".length
        spannableString.setSpan(
            ForegroundColorSpan(Color.parseColor("#FF6704")),
            startIndex,
            endIndex,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )*/
    }

    private fun titleThree(title : String) {
        /*val spannableString = SpannableString(title)
        val startIndex = title.indexOf("in 1 hour")
        val endIndex = startIndex + "in 1 hour".length
        spannableString.setSpan(
            ForegroundColorSpan(Color.parseColor("#FF6704")),
            startIndex,
            endIndex,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )*/
    }

    companion object {
        @JvmStatic
        fun newInstance(position: Int) =
            IntroSlideFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_PARAM1, position)
                }
            }
    }
}