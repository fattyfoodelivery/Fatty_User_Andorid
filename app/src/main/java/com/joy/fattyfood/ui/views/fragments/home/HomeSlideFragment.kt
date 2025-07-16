package com.joy.fattyfood.ui.views.fragments.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.joy.fattyfood.R
import com.joy.fattyfood.databinding.FragmentHomeSlideBinding
import com.joy.fattyfood.domain.model.UpAndDownVO
import com.joy.fattyfood.utils.helper.loadPhoto
import java.util.ArrayList


class HomeSlideFragment(val onClick : () ->  Unit) : Fragment() {

    private var position: Int? = null
    private var coverList : List<String> = listOf()

    private var binding : FragmentHomeSlideBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            position = it.getInt(COVER_POS)
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeSlideBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val cover = arguments?.getString(COVER_URL)
        val dataList = arguments?.getParcelableArrayList<UpAndDownVO>(DATA_LIST) ?: mutableListOf()

        dataList[this.position ?: 0]?.image?.let { binding?.imvCoverSlider?.loadPhoto(it) }

        binding?.imvCoverSlider?.setOnClickListener {
            onClick.invoke()
        }

        //coverList = formatCoverList(cover.toString())

        println("cover Listr ${coverList.size}")
        setUpCoverSlider()
    }

    private fun setUpCoverSlider() {

        /*binding?.imvCoverSlider?.loadPhoto(da[this.position ?: 0])

        binding?.imvCoverSlider?.setOnClickListener {
            onClick.invoke()
        }*/


    }


    companion object {
        @JvmStatic
        fun newInstance(
            position: Int,
            dataList : MutableList<UpAndDownVO>,
            onClick: () -> Unit,
        ) =
            HomeSlideFragment(onClick).apply {
                arguments = Bundle().apply {
                    putInt(COVER_POS, position)
                    //putString(COVER_URL,coverUrl)
                    //putStringArrayList(DATA_LIST, ArrayList(dataList))
                    putParcelableArrayList(DATA_LIST, ArrayList(dataList))
                }
            }

        const val DATA_LIST = "data_list"
        const val COVER_URL = "cover_url"
        private const val COVER_POS = "coverPos"
    }
}