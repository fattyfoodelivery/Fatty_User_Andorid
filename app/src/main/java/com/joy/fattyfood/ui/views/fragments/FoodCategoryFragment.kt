package com.joy.fattyfood.ui.views.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.tabs.TabLayout
import com.joy.fattyfood.adapters.NearByRestaurantAdapter
import com.joy.fattyfood.app.FattyApp
import com.joy.fattyfood.databinding.FragmentFoodCategoryBinding
import com.joy.fattyfood.domain.model.MenuVO
import com.joy.fattyfood.utils.delegate.ItemIdDelegate
import com.joy.fattyfood.utils.EqualSpacingItemDecoration

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class FoodCategoryFragment : Fragment() , ItemIdDelegate {

    private var param1: String? = null
    private var param2: String? = null

    private var _binding : FragmentFoodCategoryBinding? = null

    private var nearbyRestaurantAdapter : NearByRestaurantAdapter? = null

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

        _binding = FragmentFoodCategoryBinding.inflate(inflater,container,false)
        return (_binding?.root)
    }

    companion object {
        fun newInstance(/*param1: String, param2: String*/) =
            FoodCategoryFragment().apply {
                /*arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }*/
            }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        //initTabLayout(menus)
        //initMediator(menus)

        setUpFoodList()
    }


    private fun initTabLayout(menus: MutableList<MenuVO>) {
        _binding?.tabFoodMenu?.removeAllTabs()
        for (menu in menus) {
            _binding?.tabFoodMenu?.addTab( _binding?.tabFoodMenu?.newTab()!!.setText(menu.food_menu_name_mm))
        }
    }

    private fun initMediator(menus: MutableList<MenuVO>) {
        try {
            menus.indices.toMutableList().let {
                //TabbedListMediator(restaurantDetailBinding.rvFoodMenu, restaurantDetailBinding.tabFoodMenu, it, false).attach()
            }
            _binding?.tabFoodMenu?.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab?) {
                    /*if (menus.indices.toMutableList()
                            .last() == restaurantDetailBinding.tabFoodMenu.selectedTabPosition
                    ) appbarLayout.setExpanded(false, true)*/
                    if (menus.indices.toMutableList().last() ==  _binding?.tabFoodMenu?.selectedTabPosition ) {

                    }
                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {
                    // TODO("Not yet implemented")
                }

                override fun onTabReselected(tab: TabLayout.Tab?) {
                    //TODO("Not yet implemented")
                }
            })
        } catch (e: Exception) {
        }

    }

    private fun setUpFoodList() {
        val linearLayoutManager =
            LinearLayoutManager(FattyApp.getInstance(), LinearLayoutManager.VERTICAL,false)
        _binding?.rvFoodMenu?.layoutManager = linearLayoutManager
        _binding?.rvFoodMenu?.addItemDecoration(
            EqualSpacingItemDecoration(
                24,
                EqualSpacingItemDecoration.VERTICAL
            )
        )
        _binding?.rvFoodMenu?.setHasFixedSize(true)
        _binding?.rvFoodMenu?.isNestedScrollingEnabled = true
        nearbyRestaurantAdapter = NearByRestaurantAdapter(FattyApp.getInstance()){ data,str,pos ->

        }
        //nearbyRestaurantAdapter?.setNewData(dummyList)
        _binding?.rvFoodMenu?.adapter = nearbyRestaurantAdapter
    }

    override fun onTapItemID(itemId: Int) {

    }
}