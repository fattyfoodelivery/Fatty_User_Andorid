package com.orikino.fatty.ui.views.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.orikino.fatty.domain.viewstates.InboxViewState
import com.google.android.material.card.MaterialCardView
import com.orikino.fatty.R
import com.orikino.fatty.adapters.NotiAdapter
import com.orikino.fatty.adapters.SysNotiAdapter
import com.orikino.fatty.app.FattyApp
import com.orikino.fatty.databinding.FragmentNotiBinding
import com.orikino.fatty.domain.view_model.NotiViewModel
import com.orikino.fatty.ui.views.activities.auth.login.LoginActivity
import com.orikino.fatty.ui.views.activities.noti_detail.NotificationDetailActivity
import com.orikino.fatty.ui.views.activities.order.OrderDetailActivity
import com.orikino.fatty.ui.views.activities.parcel.ParcelDetailActivity
import com.orikino.fatty.utils.Constants
import com.orikino.fatty.utils.delegate.ItemIdDelegate
import com.orikino.fatty.utils.EqualSpacingItemDecoration
import com.orikino.fatty.utils.LoadingProgressDialog
import com.orikino.fatty.utils.PreferenceUtils
import com.orikino.fatty.utils.WarningDialog
import com.orikino.fatty.utils.helper.showDatePickerDialog
import com.orikino.fatty.utils.helper.showSnackBar
import dagger.hilt.android.AndroidEntryPoint
import java.text.ParseException


@AndroidEntryPoint
class NotiFragment : Fragment(), ItemIdDelegate {

    private var notiBinding : FragmentNotiBinding? = null
    private var notiAdapter: NotiAdapter? = null
    private var systemNotiAdapter : SysNotiAdapter? = null

    private val viewModel : NotiViewModel by viewModels()

    var isCheckOrder = true
    private var filterDate : String ? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        notiBinding = FragmentNotiBinding.inflate(inflater,container,false)
        return (notiBinding?.root)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        notiBinding?.systemEmptyView?.emptyMessage?.text = getString(R.string.no_data_available)
        notiBinding?.systemEmptyView?.emptyMessageDes?.text = ""
        notiBinding?.orderEmptyView?.emptyMessage?.text = getString(R.string.no_data_available)
        notiBinding?.orderEmptyView?.emptyMessageDes?.text = ""
        setUpOrderNotiList()
        setUpSystemNotiList()

        setUpObserver()
        filterNotiByDate()

        navigator()



    }

    private fun navigator() {
        notiBinding?.cvOrder?.setOnClickListener {
            notiBinding?.rlOrder?.visibility = View.VISIBLE
            notiBinding?.rlSystem?.visibility = View.GONE
            notiBinding?.systemEmptyView?.rootView?.visibility = View.GONE
            notiBinding?.tvDateResult?.text = ""
            viewModel.fetchUserNotiList(1,"")
            checkUpdate(true)
        }

        notiBinding?.cvSystem?.setOnClickListener {
            notiBinding?.rlSystem?.visibility  = View.VISIBLE
            notiBinding?.rlOrder?.visibility = View.GONE
            notiBinding?.orderEmptyView?.rootView?.visibility = View.GONE
            notiBinding?.tvDateResult?.text = ""
            viewModel.fetchSystemNot(1,"")
            checkUpdate(false)
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.fetchUserNotiList(1,"")
        viewModel.fetchSystemNot(1,"")
    }

    private fun filterNotiByDate() {
        notiBinding?.imvCalendar?.setOnClickListener {
            context?.showDatePickerDialog { date ->

                viewModel.currentPage = 0
                viewModel.currentPage = 1

                viewModel.isLoading = true
                LoadingProgressDialog.showLoadingProgress(requireContext())
                changeDateFormat(date)
            }
        }
    }

    private fun changeDateFormat(date: String) {

        try {
            filterDate = date

            notiBinding?.tvDateResult?.text = filterDate

            if (isCheckOrder) {
                PreferenceUtils.readUserVO().customer_id.let { cusId ->
                    filterDate?.let { fDate ->
                        viewModel.fetchUserNotiList( 1, fDate)
                    }
                }
            } else {
                PreferenceUtils.readUserVO().customer_id.let { cusId ->
                    filterDate?.let { fDate ->
                        viewModel.fetchSystemNot( 1, fDate)
                    }
                }
            }
        } catch (e: ParseException) {
            e.printStackTrace()
            // Handle parsing error if needed
        }
    }



    private fun setUpObserver() {
        viewModel.viewState.observe(viewLifecycleOwner) { render(it) }
    }

    private fun render(state : InboxViewState) {
        when(state) {
            is InboxViewState.OnLoadingUserNotiList -> renderOnLoadingUserNotiList()
            is InboxViewState.OnSuccesUserNotiList -> renderOnSuccesUserNotiList(state)
            is InboxViewState.OnFailUserNotiList -> renderOnFailUserNotiList(state)

            is InboxViewState.OnLoadingSystemNotiList -> renderOnLoadingSystemNotiList()
            is InboxViewState.OnSuccessSystemNotiList -> renderOnSuccessSystemNotiList(state)
            is InboxViewState.OnFailSystemNotiList -> renderOnFailSystemNotiList(state)

            is InboxViewState.OnLoadingReadNoti -> renderOnLoadingReadNoti()
            is InboxViewState.OnSuccessReadNoti -> renderOnSuccessReadNoti(state)
            is InboxViewState.OnFailReadNoti -> renderOnFailReadNoti(state)

            else -> {}
        }
    }

    private fun renderOnLoadingSystemNotiList() {
        viewModel.isLoading = true
        LoadingProgressDialog.showLoadingProgress(requireContext())
    }

    private fun renderOnSuccessSystemNotiList(state: InboxViewState.OnSuccessSystemNotiList) {
        LoadingProgressDialog.hideLoadingProgress()
        viewModel.isLoading = false
        if (state.data.success) {
            if (state.data.data.notification.data.size > 1) {
                notiBinding?.cvMessageBadge?.visibility = View.VISIBLE
                notiBinding?.tvMessageCount?.text = state.data.data.unread_count.toString()
            } else {
                notiBinding?.cvMessageBadge?.visibility = View.GONE
            }
            notiBinding?.systemEmptyView?.root?.visibility = if (state.data.data.notification.data.isNotEmpty()){
                View.GONE
            } else {
                View.VISIBLE
            }
            systemNotiAdapter?.setNewData(state.data.data.notification.data)
        }

    }

    private fun renderOnFailSystemNotiList(state: InboxViewState.OnFailSystemNotiList) {
        LoadingProgressDialog.hideLoadingProgress()
        viewModel.isLoading = false
        when(state.message){
            Constants.CONNECTION_ISSUE -> {
                showSnackBar(resources.getString(R.string.no_internet_title))
            }

            Constants.ANOTHER_LOGIN -> startActivity(LoginActivity.getIntent("noti"))


            Constants.DENIED -> WarningDialog.Builder(requireContext(),
                resources.getString(R.string.maintain_title),
                resources.getString(R.string.maintain_msg),
                "OK",
                callback = {
                    requireActivity().finishAffinity()

                })
                .show(childFragmentManager, NotiFragment::class.simpleName)

            Constants.FAILED -> {
                showSnackBar(state.message)
            }
            else -> {
                showSnackBar(state.message)
            }
        }
    }
    private fun renderOnLoadingUserNotiList() {
        LoadingProgressDialog.showLoadingProgress(requireContext())
    }

    private fun renderOnSuccesUserNotiList(state: InboxViewState.OnSuccesUserNotiList) {
        LoadingProgressDialog.hideLoadingProgress()
        if (state.data.success){
            if (state.data.data.notification.data.size > 1) {
                notiBinding?.cvOrderBadge?.visibility = View.VISIBLE
                notiBinding?.tvOrderCount?.text = state.data.data.unread_count.toString()
            } else {
                notiBinding?.cvOrderBadge?.visibility = View.GONE
            }
            notiBinding?.orderEmptyView?.root?.visibility = if (state.data.data.notification.data.isNotEmpty()){
                View.GONE
            } else {
                View.VISIBLE
            }

            notiAdapter?.setNewData(state.data.data.notification.data)
        }
    }
    private fun renderOnFailUserNotiList(state : InboxViewState.OnFailUserNotiList) {
        LoadingProgressDialog.hideLoadingProgress()
        when(state.message){
            Constants.CONNECTION_ISSUE -> {
                showSnackBar(resources.getString(R.string.no_internet_title))
            }

            Constants.ANOTHER_LOGIN -> startActivity(LoginActivity.getIntent("noti"))


            Constants.DENIED -> WarningDialog.Builder(requireContext(),
                resources.getString(R.string.maintain_title),
                resources.getString(R.string.maintain_msg),
                "OK",
                callback = {
                    requireActivity().finishAffinity()

                })
                .show(childFragmentManager, NotiFragment::class.simpleName)

            Constants.FAILED -> {
                showSnackBar(state.message)
            }
            else -> {
                showSnackBar(state.message)
            }
        }
    }

    private fun checkUpdate(check : Boolean) {
        //updateObserver()
        if (check) {
            isCheckOrder = true
            notiBinding?.cvOrder?.let { viewChecked(it,notiBinding!!.tvOrder,notiBinding!!.cvOrderBadge) }
            notiBinding?.cvSystem?.let { viewUnChecked(it,notiBinding!!.tvMessage,notiBinding!!.cvMessageBadge) }

        } else {
            isCheckOrder = false
            notiBinding?.cvSystem?.let { viewChecked(it,notiBinding!!.tvMessage,notiBinding!!.cvMessageBadge) }
            notiBinding?.cvOrder?.let { viewUnChecked(it,notiBinding!!.tvOrder,notiBinding!!.cvOrderBadge) }
        }
    }

    private fun viewChecked(view: MaterialCardView,title : TextView,badge : MaterialCardView) {
        view.setCardBackgroundColor(ContextCompat.getColor(requireContext(),R.color.check_card_bg))
        title.setTextColor(ContextCompat.getColor(requireContext(),R.color.fattyPrimary))
        badge.setCardBackgroundColor(ContextCompat.getColor(requireContext(),R.color.textError))
    }

    private fun viewUnChecked(view : MaterialCardView,title : TextView,badge : MaterialCardView) {
        view.setCardBackgroundColor(ContextCompat.getColor(requireContext(),R.color.surfacePrimary02))
        title.setTextColor(ContextCompat.getColor(requireContext(),R.color.textPrimary02))
        badge.setCardBackgroundColor(ContextCompat.getColor(requireContext(),R.color.iconSecondary))
    }


    private fun renderOnLoadingReadNoti() {}

    private fun renderOnSuccessReadNoti(state: InboxViewState.OnSuccessReadNoti) {
        if (state.data.success == true) {
            viewModel.fetchUserNotiList(1,"")
            viewModel.fetchSystemNot(1,"")
        }
    }


    private fun renderOnFailReadNoti(state: InboxViewState.OnFailReadNoti) {}

    private fun setUpOrderNotiList() {

        val linearLayoutManager =
            LinearLayoutManager(FattyApp.getInstance(),LinearLayoutManager.VERTICAL,false)
        notiBinding?.rvOrder?.layoutManager = linearLayoutManager
        notiBinding?.rvOrder?.addItemDecoration(
            EqualSpacingItemDecoration(
                24,
                EqualSpacingItemDecoration.HORIZONTAL
            )
        )
        notiBinding?.rvOrder?.setHasFixedSize(true)
        notiBinding?.rvOrder?.isNestedScrollingEnabled = true
        notiAdapter = NotiAdapter(requireContext()) { data,str,pos ->
            PreferenceUtils.readUserVO().customer_id.let {
                viewModel.readNoti(data.id,"user",PreferenceUtils.readUserVO().customer_id?:0)
            }
            val order_id = data.order_id.toInt()
            when(str) {
                "food" -> {
                    /*requireActivity().startActivity<OrderDetailActivity>(
                        OrderDetailActivity.ORDER_ID to data.order_id,
                        OrderDetailActivity.INTENT_FROM to false
                    )*/
                    val intent = Intent(requireContext(), OrderDetailActivity::class.java)
                    intent.putExtra("order_id", order_id)
                    startActivity(intent)
                }
                "parcel" ->  {
                    /*requireContext().startActivity<ParcelDetailActivity>(
                        ParcelDetailActivity.ORDER_ID to order_id
                    )*/
                    val intent = Intent(requireContext(), ParcelDetailActivity::class.java)
                    intent.putExtra("order_id", order_id)
                    startActivity(intent)
                }

            }
        }
        notiBinding?.rvOrder?.adapter = notiAdapter
    }

    private fun setUpSystemNotiList() {
        val linearLayoutManager =
            LinearLayoutManager(FattyApp.getInstance(),LinearLayoutManager.VERTICAL,false)
        notiBinding?.rvSystem?.layoutManager = linearLayoutManager
        notiBinding?.rvSystem?.addItemDecoration(
            EqualSpacingItemDecoration(
                24,
                EqualSpacingItemDecoration.HORIZONTAL
            )
        )
        notiBinding?.rvSystem?.setHasFixedSize(true)
        notiBinding?.rvSystem?.isNestedScrollingEnabled = true
        systemNotiAdapter = SysNotiAdapter(requireContext()) {data,str,pos ->
            when(str) {
                "view_read" -> {
                    PreferenceUtils.readUserVO().customer_id?.let {
                        viewModel.readNoti(data.id,"system",it)
                    }
                    /*requireActivity().startActivity<NotificationDetailActivity>(
                        NotificationDetailActivity.NOTI_ID to data.id,
                        NotificationDetailActivity.NOTI_BODY to data.body
                    )*/
                    val intent = Intent(requireContext(), NotificationDetailActivity::class.java)
                    intent.putExtra("noti_id", data.id)
                    intent.putExtra("noti_body", data.body)
                    startActivity(intent)
                }
            }

        }
        notiBinding?.rvSystem?.adapter = systemNotiAdapter
    }

    companion object {

        fun newInstance(/*param1: String, param2: String*/) =
            NotiFragment().apply {

            }
    }

    override fun onTapItemID(itemId: Int) {
    }
}