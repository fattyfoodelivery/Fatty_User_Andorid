package com.joy.fattyfood.ui.views.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.joy.fattyfood.R
import com.joy.fattyfood.adapters.OrderHistoryAdapter
import com.joy.fattyfood.app.FattyApp
import com.joy.fattyfood.databinding.FragmentOrderBinding
import com.joy.fattyfood.databinding.ItemShowRatingViewDialogBinding
import com.joy.fattyfood.databinding.LayoutDialogRemoveCartBinding
import com.joy.fattyfood.domain.model.OrderDetailVO
import com.joy.fattyfood.domain.responses.MyOrderHistoryResponse
import com.joy.fattyfood.domain.view_model.OrderViewModel
import com.joy.fattyfood.domain.viewstates.OrderViewState
import com.joy.fattyfood.ui.views.activities.order.OrderDetailActivity
import com.joy.fattyfood.ui.views.activities.parcel.ParcelDetailActivity
import com.joy.fattyfood.ui.views.activities.splash.SplashActivity
import com.joy.fattyfood.ui.views.activities.track.TrackOrderParcelActivity
import com.joy.fattyfood.utils.EqualSpacingItemDecoration
import com.joy.fattyfood.utils.LoadingProgressDialog
import com.joy.fattyfood.utils.PreferenceUtils
import com.joy.fattyfood.utils.WarningDialog
import com.joy.fattyfood.utils.helper.gone
import com.joy.fattyfood.utils.helper.show
import com.joy.fattyfood.utils.helper.showDatePickerDialog
import com.joy.fattyfood.utils.helper.showSnackBar
import com.joy.fattyfood.utils.viewpod.EmptyViewPodDelegate
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OrderFragment : Fragment() , EmptyViewPodDelegate{


    private var orderBinding : FragmentOrderBinding? = null
    private var dialogBinding : ItemShowRatingViewDialogBinding? = null

    private var orderHistoryAdapter : OrderHistoryAdapter? = null

    private val viewModel : OrderViewModel by viewModels()

    companion object {
        fun newInstance(/*param1: String, param2: String*/) =
            OrderFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        orderBinding = FragmentOrderBinding.inflate(inflater,container,false)
        return (orderBinding?.root)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        setUpOrderHistories()
        clearList()
        setUpObserver()
        filterOrderByDate()
        clickFoodAndParcelBtn()

        orderBinding?.swipeRefresh?.setOnRefreshListener {
            orderBinding?.swipeRefresh?.isRefreshing = true
            PreferenceUtils.readUserVO().customer_id?.let {
                //viewModel.fetchOrderHistory(it)
                viewModel.fetchFoodParcelOrderHistory(it)
            }
            setUpObserver()
        }

        orderBinding?.rvOrder?.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                println("scrollling is working")
                if (!recyclerView.canScrollVertically(1)) {
                    if (viewModel.currentPage < viewModel.lastPage) {
                        viewModel.currentPage += 1
                        PreferenceUtils.readUserVO().customer_id?.let { viewModel.fetchOrderDetailForReviews(it) }
                    }
                }
            }
        })

        PreferenceUtils.readUserVO().customer_id?.let {
            //viewModel.fetchOrderHistory(it)
            viewModel.fetchFoodParcelOrderHistory(it)
        }


    }

    private fun clearList() {
        viewModel.currentPage = 1
        viewModel.foodOrderList.clear()
    }



    override fun onResume() {
        super.onResume()
        setUpObserver()
    }

    private fun setUpObserver() {
        viewModel.viewState.observe(viewLifecycleOwner, Observer {
            render(it)
        })
    }

    private fun filterOrderByDate() {
        orderBinding?.ibtnPickDate?.setOnClickListener {
            context?.showDatePickerDialog {
                viewModel.date = it
                viewModel.currentPage = 0
                viewModel.currentPage = 1

                viewModel.isLoading = true
                LoadingProgressDialog.showLoadingProgress(requireContext())
                PreferenceUtils.readUserVO().customer_id?.let {
                    //viewModel.fetchOrderHistory(it)
                    viewModel.fetchFoodParcelOrderHistory(it)
                }
            }
            viewModel.date = ""
        }
    }

    private fun clickFoodAndParcelBtn() {
        viewModel.currentPage = 1
    }

    private fun render(state : OrderViewState) {
        when(state) {
            is OrderViewState.OnLoadingOrderDetailForReview -> renderOnLoadingOrderDetailForReview()
            is OrderViewState.OnSuccessOrderDetailForReview -> renderOnSuccessOrderDetailForReview(state)
            is OrderViewState.OnFailOrderDetailForReview -> renderOnFailOrderDetailForReview(state)

            is OrderViewState.OnLoadingMyOrder -> renderOnLoadingMyOrder()
            is OrderViewState.OnSuccessMyOrder -> renderOnSuccessMyOrder(state)
            is OrderViewState.OnFailMyOrder -> { renderFailedOrder(state) }//{renderOnFailMyOrder(state)

            is OrderViewState.OnSuccessOrderCancel -> renderOnSuccessOrderCancel(state)
            is OrderViewState.OnFailOrderCancel -> renderOnFailOrderCancel(state)

            else -> {}
        }
    }

    private fun renderOnLoadingOrderDetailForReview() {
    }

    private fun renderOnFailOrderDetailForReview(state: OrderViewState.OnFailOrderDetailForReview) {
        showSnackBar(state.message!!)
    }

    private fun renderOnSuccessOrderDetailForReview(state: OrderViewState.OnSuccessOrderDetailForReview){
        if (true) {
            showRatingViewDialog(state.data.data)
        }

    }

    private fun setUpRatingReviewUI(data: OrderDetailVO) {
        dialogBinding?.layoutRider?.show()
        dialogBinding?.tvRateLevel?.text = "Rate Level"

        if (data.rider_review_details.options?.isEmpty() == true) {
            dialogBinding?.riderHsvView?.gone()
        } else {
            dialogBinding?.riderHsvView?.show()
            dialogBinding?.tvRiderSrvOne?.text = data.rider_review_details.options!![0]
            dialogBinding?.tvRiderSrvOne?.text = data.rider_review_details.options!![1]
            dialogBinding?.tvRiderSrvOne?.text = data.rider_review_details.options!![3]
        }
        dialogBinding?.txtRiderComment?.text = data.rider_review_details.comment
        dialogBinding?.ratingBarRider?.rating = 3f

        dialogBinding?.layoutRestaurant?.show()
        dialogBinding?.tvLevelRestaurant?.text = "Rate level Restaurants"
        if (data.restaurant_review_details.options?.isEmpty() == true) {
            dialogBinding?.restHsvView?.gone()
        } else {
            dialogBinding?.tvSrvOne?.text = data.restaurant_review_details.options!![0]
            dialogBinding?.tvSrvTwo?.text = data.restaurant_review_details.options!![1]
            dialogBinding?.tvSrvThree?.text = data.restaurant_review_details.options!![2]
        }
        dialogBinding?.txtRestaurantComment?.text = data.restaurant_review_details.comment
        dialogBinding?.ratingBarRestaurant?.rating = 5f

    }


    private fun renderOnLoadingMyOrder(){
        viewModel.isLoading = true
        orderBinding?.swipeRefresh?.isRefreshing = true
        LoadingProgressDialog.showLoadingProgress(requireContext())
    }

    private fun renderFailedOrder(state: OrderViewState.OnFailMyOrder) {
        viewModel.isLoading = false
        orderBinding?.swipeRefresh?.isRefreshing = false
        LoadingProgressDialog.hideLoadingProgress()
    }
    private fun renderOnFailMyOrder(state: OrderViewState.OnFailMyOrder){
        viewModel.isLoading = false
        orderBinding?.swipeRefresh?.isRefreshing = false
        LoadingProgressDialog.hideLoadingProgress()
        when(state.message){
            "Server Issue" -> {
                showSnackBar(state.message)
            }

            "Another Login" -> WarningDialog.Builder(requireContext(),
                resources.getString(R.string.already_login_title),
                resources.getString(R.string.already_login_msg),
                resources.getString(R.string.force_login),
                callback = {
                    PreferenceUtils.clearCache()
                    requireActivity().finish()
                    //requireContext().startActivity<SplashActivity>()
                    val intent = Intent(requireContext(),SplashActivity::class.java)
                    startActivity(intent)
                }).show(requireFragmentManager(), OrderFragment::class.simpleName)


            "Denied" -> WarningDialog.Builder(requireContext(),
                resources.getString(R.string.maintain_title),
                resources.getString(R.string.maintain_msg),
                "OK",
                callback = {
                    requireActivity().finishAffinity()

                })
                .show(childFragmentManager, OrderFragment::class.simpleName)

            "Failed" -> { showSnackBar(state.message) }
            else -> {
                showSnackBar(state.message!!)
            }
        }
    }

    private fun renderOnSuccessMyOrder(state: OrderViewState.OnSuccessMyOrder){
        viewModel.isLoading = true
        orderBinding?.swipeRefresh?.isRefreshing = false
        LoadingProgressDialog.hideLoadingProgress()
        if (state.data?.success == true){
            if (state.data.data?.data?.isEmpty() == true){
                emptyViewSetUp()
            } else {
                hideEmptyView()
                state.data.data?.data?.let { orderHistoryAdapter?.setNewData(it) }
            }
        }
    }


    private fun renderOnFailOrderCancel(state: OrderViewState.OnFailOrderCancel) {}
    private fun renderOnSuccessOrderCancel(state: OrderViewState.OnSuccessOrderCancel) {
        if (state.data.success) {
            PreferenceUtils.readUserVO().customer_id?.let {
                //viewModel.fetchOrderHistory(it)
                viewModel.fetchFoodParcelOrderHistory(it)
            }
        }
    }


    private fun emptyViewSetUp() {
        orderBinding?.emptyVew?.root?.show()
        orderBinding?.emptyVew?.root?.setDelegate(this)
        orderBinding?.emptyVew?.root?.setEmptyData(
            "No Orders Yet",
            "No Orders Yet",
            resources.getDrawable(R.drawable.ic_food_empty_144dp)
        )
    }

    private fun hideEmptyView() {
        orderBinding?.emptyVew?.root?.gone()
    }

    private fun setUpOrderHistories() {
        val linearLayoutManager =
            LinearLayoutManager(FattyApp.getInstance(), LinearLayoutManager.VERTICAL,false)
        orderBinding?.rvOrder?.layoutManager = linearLayoutManager
        orderBinding?.rvOrder?.addItemDecoration(
            EqualSpacingItemDecoration(
                24,
                EqualSpacingItemDecoration.VERTICAL
            )
        )
        orderBinding?.rvOrder?.setHasFixedSize(true)
        orderBinding?.rvOrder?.isNestedScrollingEnabled = true
        orderHistoryAdapter = OrderHistoryAdapter(FattyApp.getInstance()) { data ,str , pos ->
            when(str) {
                "cancel" -> itemActionCancelClick(data)
                "track" -> itemActionTrackClick(data)
                "track_food_finish" -> data.order_id?.let { trackFoodFinishDetail(it) }//data.let { itemRootClick(it) }
                "view_rating" -> data.order_id?.let { fetchRatingReview(it) }
                "track_parcel_finish" -> data.order_id?.let { trackFinishedDetail(it) }
            }
        }
        orderBinding?.rvOrder?.adapter = orderHistoryAdapter
    }

    private fun trackFoodFinishDetail(orderId: Int) {
        /*requireActivity().startActivity<OrderDetailActivity>(
            OrderDetailActivity.ORDER_ID to orderId,
            OrderDetailActivity.INTENT_FROM to true
        )*/
        val intent = Intent(requireContext(),OrderDetailActivity::class.java)
        intent.putExtra(OrderDetailActivity.ORDER_ID,orderId)
        intent.putExtra(OrderDetailActivity.INTENT_FROM,true)
        startActivity(intent)
    }

    private fun trackFinishedDetail(orderId: Int) {
        /*requireActivity().startActivity<ParcelDetailActivity>(
            ParcelDetailActivity.ORDER_ID to orderId,
            ParcelDetailActivity.PARCEL_HISTORY to true
        )*/
        val intent = Intent(requireContext(),ParcelDetailActivity::class.java)
        intent.putExtra(ParcelDetailActivity.ORDER_ID,orderId)
        intent.putExtra(ParcelDetailActivity.PARCEL_HISTORY,true)
        startActivity(intent)

    }

    private fun itemActionTrackClick(data: MyOrderHistoryResponse.Data.Data) {
        /*requireActivity().startActivity<TrackOrderParcelActivity>(
            TrackOrderParcelActivity.ORDER_ID to data.order_id,
            TrackOrderParcelActivity.VIEW_TYPE to "his"
        )*/
        val intent = Intent(requireContext(),TrackOrderParcelActivity::class.java)
        intent.putExtra(TrackOrderParcelActivity.ORDER_ID,data.order_id)
        intent.putExtra(TrackOrderParcelActivity.VIEW_TYPE,"his")
        startActivity(intent)
    }

    private fun fetchRatingReview(orderId: Int){
        viewModel.fetchOrderDetailForReviews(orderId)
    }
    private fun showRatingViewDialog(data: OrderDetailVO) {
        dialogBinding = ItemShowRatingViewDialogBinding.inflate(LayoutInflater.from(requireContext()))//layoutInflater.inflate(R.layout.layout_dialog_remove_cart, null)
        val builder = AlertDialog.Builder(requireContext())
        builder.setView(dialogBinding?.root)
        builder.create().apply {
            window?.setBackgroundDrawableResource(android.R.color.transparent)
            setCancelable(false)
            dialogBinding?.ivClose?.setOnClickListener { dismiss() }
            setUpRatingReviewUI(data)

            /*dialogView.tvTitle.text = title
            dialogView.tvTitleDesc.text = message

            dialogView.btnClose.setOnClickListener {
                dismiss()
            }
            dialogView.btnRemove.text = resources.getString(R.string.confirm)*/
            dialogBinding?.btnOk?.setOnClickListener {
                dismiss()
                //orderHistoryAdapter?.setNewData(mutableListOf())

                //viewModel.orderHistoriesList.clear()
                //viewModel.pastOrderHistoriesList.clear()
                //viewModel.foodOrderList.clear()
                //viewModel.parcelOrderHistoriesList.clear()
                //viewModel.parcelPastOrderHistoriesList.clear()
                //viewModel.parcelOrderList.clear()
                //viewModel.fetchOrderCancel(order.order_id)
            }

            show()
        }
    }

    private fun itemActionCancelClick(data : MyOrderHistoryResponse.Data.Data) {
        PreferenceUtils.isBackground = false
        if (data.order_status_id == 1 || data.order_status_id == 19) showConfirmDialog(
            "Are you sure you want to cancel  this order?",//resources.getString(R.string.are_you_sure_to_delete_this_order),
            "By doing so, you won’t be able to bring it back.",
            //resources.getString(R.string.sure_to_cancel),
            data
        )
        else {
            PreferenceUtils.needToShow = false
            /*requireActivity().startActivity<OrderDetailActivity>(
                OrderDetailActivity.ORDER_ID to data.order_id,
                OrderDetailActivity.INTENT_FROM to true
            )*/
            val intent = Intent(requireContext(),OrderDetailActivity::class.java)
            intent.putExtra(OrderDetailActivity.ORDER_ID,data.order_id)
            intent.putExtra(OrderDetailActivity.INTENT_FROM,true)
            startActivity(intent)

        }
    }

    private fun itemRootClick(data : MyOrderHistoryResponse.Data.Data) {
        PreferenceUtils.needToShow = false
        PreferenceUtils.isBackground = false

        if (data.order_type == "food") {
            /*requireActivity().startActivity<OrderDetailActivity>(
                OrderDetailActivity.ORDER_ID to data.order_id,
                OrderDetailActivity.INTENT_FROM to true
            )*/
            val intent = Intent(requireContext(),OrderDetailActivity::class.java)
            intent.putExtra(OrderDetailActivity.ORDER_ID,data.order_id)
            intent.putExtra(OrderDetailActivity.INTENT_FROM,true)
            startActivity(intent)
        } else {
            /*requireActivity().startActivity<ParcelDetailActivity>(
                ParcelDetailActivity.ORDER_ID to data.order_id
            )*/
            val intent = Intent(requireContext(),ParcelDetailActivity::class.java)
            intent.putExtra(ParcelDetailActivity.ORDER_ID,data.order_id)
            startActivity(intent)
        }


    }


    private fun showConfirmDialog(title: String, message: String, order: MyOrderHistoryResponse.Data.Data) {
        val dialogView = LayoutDialogRemoveCartBinding.inflate(LayoutInflater.from(requireContext()))//layoutInflater.inflate(R.layout.layout_dialog_remove_cart, null)
        val builder = AlertDialog.Builder(requireContext())
        builder.setView(dialogView.root)
        builder.create().apply {
            window?.setBackgroundDrawableResource(android.R.color.transparent)
            setCancelable(false)
            dialogView.tvTitle.text = title
            dialogView.tvTitleDesc.text = message

            dialogView.btnClose.setOnClickListener {
                dismiss()
            }
            dialogView.btnRemove.text = resources.getString(R.string.confirm)
            dialogView.btnRemove.setOnClickListener {
                dismiss()
                orderHistoryAdapter?.setNewData(mutableListOf())

                //viewModel.orderHistoriesList.clear()
                //viewModel.pastOrderHistoriesList.clear()
                //viewModel.foodOrderList.clear()
                //viewModel.parcelOrderHistoriesList.clear()
                //viewModel.parcelPastOrderHistoriesList.clear()
                //viewModel.parcelOrderList.clear()
                order.order_id?.let { viewModel.fetchOrderCancel(it) }
            }

            show()
        }
    }

    override fun onTapTryAgain() {
        println("ontap")
        Toast.makeText(requireContext(), "clicl explore", Toast.LENGTH_SHORT).show()
    }


}