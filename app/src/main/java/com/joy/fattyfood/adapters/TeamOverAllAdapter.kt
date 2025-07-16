package com.joy.fattyfood.adapters

/*
class TeamOverAllAdapter(var context: Context,var dataList :ArrayList<RankListData>, var delegate: ItemIdDelegate)
    :BaseAdapter<TeamLargeViewHolder,RankListData>(context) {

    companion object {
        const val VIEW_TYPE_ONE = 1
        const val VIEW_TYPE_TWO = 2
        const val VIEW_TYPE_THREE = 3
    }

    private lateinit var overAllLargeViewBinding: OverAllLargeViewBinding
    private lateinit var overAllSmallViewBinding: OverAllSmallViewBinding
    private lateinit var smallAdsViewBinding: SmallAdsViewBinding

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BaseViewHolder<RankListData> {
        return when (viewType) {
            VIEW_TYPE_ONE -> {
                overAllLargeViewBinding = OverAllLargeViewBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                TeamLargeViewHolder(overAllLargeViewBinding, delegate)
            }
            VIEW_TYPE_TWO -> {
                overAllSmallViewBinding = OverAllSmallViewBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                TeamSmallViewHolder(overAllSmallViewBinding, delegate)
            }
            else -> {
                smallAdsViewBinding = SmallAdsViewBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                TeamSmallAdsViewHolder(smallAdsViewBinding, delegate)
            }
        }

    }

    override fun getItemViewType(position: Int): Int {
        return dataList[position].viewType
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

}*/
