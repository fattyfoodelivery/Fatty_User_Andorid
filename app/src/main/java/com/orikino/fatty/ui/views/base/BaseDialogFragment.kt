package com.orikino.fatty.ui.views.base

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.annotation.LayoutRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.DialogFragment
import androidx.viewbinding.ViewBinding
import com.orikino.fatty.R
import com.orikino.fatty.utils.helper.getScreenWidth
import androidx.core.graphics.drawable.toDrawable

typealias Inflate<T> = (LayoutInflater, ViewGroup?, Boolean) -> T

abstract class BaseDialogFragment<VB : ViewBinding>(
    private val inflate: Inflate<VB>
) : DialogFragment() {
    private var loadingDialog: AlertDialog? = null

    private var _binding: VB? = null
    val binding get() = _binding!!

    protected val loginDialogWidth by lazy { getScreenWidth(requireActivity(), 0.4) }


    override fun onCreate(savedInstanceState: Bundle?) {
        setStyle(STYLE_NORMAL, R.style.Base_App_Dialog_EdgeToEdge)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = inflate.invoke(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    fun showLoadingDialog() {
        if (loadingDialog == null) {
            loadingDialog =
                createCustomDialog(
                    requireContext(),
                    createView(R.layout.layout_loading_dialog),
                    Gravity.CENTER,
                    false
                )
        }
        modifyWindowsParamsAndShow(
            loadingDialog!!,
            loginDialogWidth,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    abstract fun initView()

    fun modifyWindowsParamsAndShow(dialog: AlertDialog, width: Int, height: Int) {
        dialog.window!!.setBackgroundDrawableResource(R.color.transparent)
        dialog.show()
//        dialog.window?.attributes = lWindowParams
        dialog.window!!.setLayout(width, height)
    }

    @SuppressLint("UseGetLayoutInflater")
    fun createView(@LayoutRes layout: Int) =
        LayoutInflater.from(requireContext()).inflate(layout, null)!!

    fun createCustomDialog(
        context: Context?,
        view: View?,
        gravity: Int,
        cancelable: Boolean
    ): AlertDialog {
        val dialog = AlertDialog.Builder(context).create()
        dialog.setView(view)
        val window = dialog.window
        window?.setGravity(gravity)
        window!!.setBackgroundDrawableResource(R.color.transparent)
        val layoutParams = window.attributes
        layoutParams.y = 40 // bottom margin
        window.attributes = layoutParams
        dialog.setCancelable(cancelable)
        return dialog
    }

    abstract fun logOut()

    abstract fun errorResponseFromAPI(error: String)

    fun hideLoadingDialog() {
        if (loadingDialog != null && loadingDialog!!.isShowing)
            loadingDialog?.dismiss()
        loadingDialog = null
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

    override fun onStart() {
        super.onStart()
        dialog?.setCanceledOnTouchOutside(true)
        dialog?.window?.setBackgroundDrawable(
            requireContext().getColor(R.color.transparent).toDrawable())
        dialog?.window?.setLayout(
            ConstraintLayout.LayoutParams.MATCH_PARENT,
            ConstraintLayout.LayoutParams.MATCH_PARENT
        )
        dialog?.window?.let { window ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                // Android 11 (API 30+) - Use WindowInsetsController
                WindowCompat.setDecorFitsSystemWindows(window, false)
                val controller = WindowInsetsControllerCompat(window, window.decorView)
                controller.isAppearanceLightStatusBars = true
                controller.isAppearanceLightNavigationBars = true
                ViewCompat.setOnApplyWindowInsetsListener(window.decorView) { v, insets ->
                    val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                    v.setPadding(
                        systemBars.left,
                        systemBars.top,
                        systemBars.right,
                        systemBars.bottom
                    )
                    insets
                }
            }
        }
    }
}