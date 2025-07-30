package com.orikino.fatty.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.orikino.fatty.databinding.LayoutItemReviewBinding
import com.orikino.fatty.domain.responses.RestDetailReviewListResponse
import com.orikino.fatty.viewholder.BaseViewHolder

class RestDetailReviewListAdapter(
    val context: Context
) : BaseAdapter<RestDetailReviewListAdapter.RestDetailReviewListViewHolder,RestDetailReviewListResponse.Data.Reviews.Data>(context){

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BaseViewHolder<RestDetailReviewListResponse.Data.Reviews.Data> {
        return RestDetailReviewListViewHolder(LayoutItemReviewBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    inner class RestDetailReviewListViewHolder(val binding: LayoutItemReviewBinding) : BaseViewHolder<RestDetailReviewListResponse.Data.Reviews.Data>(binding.root) {

        override fun setData(item: RestDetailReviewListResponse.Data.Reviews.Data, position: Int) {

            val originalString = item.star
            val ratingValue = originalString?.let { calculateRating(it) }
            binding.tvCusName.text = item.customer_name
            binding.tvDate.text = item.review_date

            when(item.options?.size) {
                1 -> {
                    binding.tvSrvOne.visibility = View.VISIBLE
                    binding.tvSrvTwo.visibility = View.GONE
                    binding.tvSrvThree.visibility = View.GONE
                    binding.tvSrvOne.text = item.options?.get(0) ?: ""
                }
                2 -> {
                    binding.tvSrvOne.visibility = View.VISIBLE
                    binding.tvSrvTwo.visibility = View.VISIBLE
                    binding.tvSrvThree.visibility = View.GONE
                    binding.tvSrvTwo.text = item.options?.get(1) ?: ""
                }
                3 -> {
                    binding.tvSrvOne.visibility = View.VISIBLE
                    binding.tvSrvTwo.visibility = View.VISIBLE
                    binding.tvSrvThree.visibility = View.VISIBLE

                    binding.tvSrvThree.text = item.options?.get(2) ?: ""
                }
            }

            /*if(item.options?.get(0) != null) {
                binding.tvSrvOne.visibility = View.VISIBLE
                binding.tvSrvTwo.visibility = View.GONE
                binding.tvSrvThree.visibility = View.GONE
                binding.tvSrvOne.text = item.options?.get(0) ?: ""
            } else if (item.options?.get(1) != null) {
                binding.tvSrvOne.visibility = View.VISIBLE
                binding.tvSrvTwo.visibility = View.VISIBLE
                binding.tvSrvThree.visibility = View.GONE
                binding.tvSrvTwo.text = item.options?.get(1) ?: ""

            } else if (item.options?.get(2) != null) {
                binding.tvSrvOne.visibility = View.VISIBLE
                binding.tvSrvTwo.visibility = View.VISIBLE
                binding.tvSrvThree.visibility = View.VISIBLE
                binding.tvSrvThree.text = item.options?.get(2) ?: ""
            }*/

            binding.ratingBarRestaurant.rating = ratingValue!!


        }

        override fun onClick(p0: View?) {

        }

    }

    private fun calculateRating(str: String): Float {

        val length = str.length
        val rating = (length / 3.0) * 5.0
        return rating.toFloat()

    }


}