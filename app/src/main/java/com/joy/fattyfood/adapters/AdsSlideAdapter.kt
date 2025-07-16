package com.joy.fattyfood.adapters

//import com.smarteist.autoimageslider.SliderViewAdapter

/*
class AdsSlideAdapter(val context: Context, private val listener : (UpAndDownVO) -> Unit) :
    SliderViewAdapter<AdsSlideAdapter.SlidingViewHolder>() {
    var mData: MutableList<UpAndDownVO> = mutableListOf()

    lateinit var binding : LayoutItemImagesBinding

    fun addImageUrl(data: MutableList<UpAndDownVO>) {
        mData = data
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup?): SlidingViewHolder {

        binding = LayoutItemImagesBinding.inflate(LayoutInflater.from(context),parent,false)
        //val view = LayoutInflater.from(context).inflate(R.layout.layout_item_images, parent, false)
        return SlidingViewHolder(binding, context)

    }

    override fun getCount(): Int {
        return mData.count()
    }

    override fun onBindViewHolder(viewHolder: SlidingViewHolder?, position: Int) {
        viewHolder?.bindData(mData[position],listener)
    }

    class SlidingViewHolder(private val binding: LayoutItemImagesBinding, private val context: Context) :
        ViewHolder(binding.root) {

        private lateinit var mData: UpAndDownVO
        var image: AppCompatImageView? = null

        fun bindData(upAndDownVO: UpAndDownVO, listener: (UpAndDownVO) -> Unit) {
            mData = upAndDownVO
            binding.ivAutoImageSlider.load(mData.image) {
                error(R.drawable.updown_slide_dummy)
                placeholder(R.drawable.updown_slide_dummy)
            }
            image?.setOnClickListener {
                listener.invoke(mData)

                PreferenceUtils.needToShow = false
                PreferenceUtils.isBackground = false
                if (mData.restaurant_id != 0) {
                    context.startActivity<RestaurantDetailViewActivity>(
                        RestaurantDetailViewActivity.RESTAURANT_ID to mData.restaurant_id
                    ) //.startActivity(RestaurantDetailActivity.getIntent(mData.restaurant_id,"ads_slide"))
                } else {

                    // go to ads detail
                }
            }
        }
    }
}*/
