package com.orikino.fatty.ui.views.activities.base

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import androidx.viewpager2.widget.ViewPager2
import com.orikino.fatty.R
import com.orikino.fatty.adapters.ViewPagerAdapter
import com.orikino.fatty.databinding.ActivityMainBinding
import com.orikino.fatty.service.FattyPushyService
import com.orikino.fatty.service.RegisterForPushNotificationsAsync
import com.orikino.fatty.domain.view_model.MainViewModel
import com.orikino.fatty.ui.views.activities.auth.login.LoginActivity
import com.orikino.fatty.ui.views.activities.checkout.CheckOutActivity
import com.orikino.fatty.ui.views.activities.rest_detail.RestaurantDetailViewActivity
import com.orikino.fatty.ui.views.fragments.AccountFragment
import com.orikino.fatty.ui.views.fragments.HomeFragment
import com.orikino.fatty.ui.views.fragments.NotiFragment
import com.orikino.fatty.ui.views.fragments.OrderFragment
import com.orikino.fatty.ui.views.activities.splash.SplashActivity
import com.orikino.fatty.utils.PreferenceUtils
import com.orikino.fatty.utils.SuccessDialog
import com.orikino.fatty.utils.helper.correctLocale
import com.orikino.fatty.utils.helper.gone
import com.orikino.fatty.utils.helper.show
import com.orikino.fatty.utils.helper.showSnackBar
import dagger.hilt.android.AndroidEntryPoint
import me.pushy.sdk.Pushy
import me.pushy.sdk.util.exceptions.PushyException

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    lateinit var baseBinding: ActivityMainBinding
    private val viewModel : MainViewModel by viewModels()

    private var prevMenuItem: MenuItem? = null


    companion object {
        var isAccount = false
        var isOrderHistory = false
        var isNotification = false
        var needToShowLoading = false
        var isParcel = false
        var isHome: Boolean = true
        var isFirstTime: Boolean = true
        var isHomeKeyPressed = false
        var isCurrencyUpdate = MutableLiveData<Boolean>(false)
        var adsRestaurantID : Int? = null
        fun getIntent(context: Context): Intent {
            return Intent(context, MainActivity::class.java)
        }

        fun getIntentWithFlag(context: Context, adsRestaurantID : Int): Intent {
            this.adsRestaurantID = adsRestaurantID
            return Intent(context, MainActivity::class.java)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        correctLocale()
        super.onCreate(savedInstanceState)

        setUpPushy()
        //Pushy.toggleForegroundService(true,this)
        //Pushy.register(this)
         /*if (!Pushy.isPermissionGranted(this)) {
             Pushy.requestNotificationPermission(this)
         }*/
        Pushy.listen(this)
        baseBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(baseBinding.root)
        isOrderHistory = false
        isNotification = false
        baseBinding.bottomNavigationView.background = null
        baseBinding.bottomNavigationView.menu.getItem(2).isEnabled = false
        setUpBottomNavigationView()
        navigateToCartView()
        stopService()
        checkPostNoti()
        if (adsRestaurantID != null){
            PreferenceUtils.needToShow = false
            PreferenceUtils.isBackground = false
            val intent = Intent(this,RestaurantDetailViewActivity::class.java)
            intent.putExtra(RestaurantDetailViewActivity.RESTAURANT_ID,adsRestaurantID)
            startActivity(intent)
        }
    }

    @SuppressLint("SuspiciousIndentation")
    private fun checkPostNoti() {
        var permissionState = 0
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionState = ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
        }
        // If the permission is not granted, request it.
        if (permissionState == PackageManager.PERMISSION_DENIED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    1
                )
            }
        }
    }

    private fun setUpPushy() {
        try {
            Pushy.subscribe("fatty/news", applicationContext)
        } catch (e: PushyException) {
            e.printStackTrace()
        }

        if (!Pushy.isRegistered(this)) {
            RegisterForPushNotificationsAsync(this).execute()
        }
    }
    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("ResourceType")
    private fun setUpBottomNavigationView() {
        // bottomNavigationView.selectedItemId = R.id.home
        baseBinding.bottomNavigationView.setOnNavigationItemSelectedListener { item ->

            when (item.itemId) {

                R.id.action_order -> {
                    isAccount = false
                    PreferenceUtils.needToShow = false
                    isHomeKeyPressed = false
                    PreferenceUtils.isHome.postValue(false)
                    isHome = false
                    needToShowLoading = true
                    if (PreferenceUtils.readUserVO().customer_id == 0) {
                        baseBinding.bottomNavigationView.menu.findItem(R.id.action_order).isCheckable = false
                        SuccessDialog.Builder(
                            this,
                            resources.getString(R.string.login_message),
                            callback = {
                                PreferenceUtils.clearCache()
                                finishAffinity()
                                //startActivity<SplashActivity>()
                                val intent = Intent(this, SplashActivity::class.java)
                                startActivity(intent)
                            })
                            .show(
                                supportFragmentManager,
                                MainActivity::class.simpleName
                            )

                    } else {
                        baseBinding.viewPager.currentItem = 1
                        baseBinding.bottomNavigationView.menu.findItem(R.id.action_order).isCheckable = true
                        isOrderHistory = false
                        isNotification = false
                    }
                }

                R.id.action_noti -> {
                    isAccount = false
                    PreferenceUtils.needToShow = false
                    isHomeKeyPressed = PreferenceUtils.needToShow
                    PreferenceUtils.isHome.postValue(false)
                    isHome = false
                    //bottomNavigationView.selectedItemId = R.id.inbox
                    baseBinding.viewPager.currentItem = 2
                    isOrderHistory = false
                    isNotification = false
                    needToShowLoading = false
                }

                R.id.action_account -> {
                    isAccount = true
                    PreferenceUtils.needToShow = false
                    isHomeKeyPressed = PreferenceUtils.needToShow
                    PreferenceUtils.isHome.postValue(false)
                    isHome = false
                    needToShowLoading = false
                    if (PreferenceUtils.readUserVO().customer_id == 0) {
                        baseBinding.bottomNavigationView.menu.findItem(R.id.action_account).isCheckable = false
                        SuccessDialog.Builder(
                            this,
                            resources.getString(R.string.login_message),
                            callback = {
                                //startActivity<LoginActivity>(LoginActivity.FROM to "base")
                                val intent = Intent(this, LoginActivity::class.java)
                                intent.putExtra("from", "base")
                                startActivity(intent)
                                finishAffinity()
                            })
                            .show(
                                supportFragmentManager,
                                MainActivity::class.simpleName
                            )
                    } else {
                        baseBinding.viewPager.currentItem = 3
                        baseBinding.bottomNavigationView.menu.findItem(R.id.action_account).isCheckable = true
                        isOrderHistory = false
                        isNotification = false
                    }


                }

                else -> {
                    isAccount = false
                    PreferenceUtils.isHome.postValue(true)
                    isHome = true
                    //bottomNavigationView.selectedItemId = R.id.home
                    baseBinding.viewPager.currentItem = 0
                    isOrderHistory = false
                    isNotification = false
                    needToShowLoading = false

                }
            }
            true
        }


        baseBinding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                try {
                    if (prevMenuItem != null) {
                        prevMenuItem?.setChecked(false)
                    } else {
                        baseBinding.bottomNavigationView.menu.getItem(0).isChecked = false
                    }

                    when (position) {
                        0 -> {
                            baseBinding.viewPager.currentItem = 0
                        }
                        1 -> {
                            baseBinding.viewPager.currentItem = 1
                        }
                        2 -> {
                            baseBinding.viewPager.currentItem = 2;
                        }
                        3 -> {
                            baseBinding.viewPager.currentItem = 3;
                        }
                    }

                    baseBinding.bottomNavigationView.menu.getItem(position).isChecked = true
                    prevMenuItem = baseBinding.bottomNavigationView.menu.getItem(position)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        })

        baseBinding.viewPager.isUserInputEnabled = false // Disable swipe
        setupViewPager(baseBinding.viewPager)

        /*baseBinding.viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                try {
                    if (prevMenuItem != null) {
                        prevMenuItem.setChecked(false)
                    } else {
                        baseBinding.bottomNavigationView.menu.getItem(0).isChecked = false
                    }

                    when (position) {
                        0 -> {
                            baseBinding.viewPager.currentItem = 0
                        }
                        1 -> {
                            baseBinding.viewPager.currentItem = 1
                        }
                        2 -> {
                            baseBinding.viewPager.currentItem = 2;
                        }
                        3 -> {
                            baseBinding.viewPager.currentItem = 3;
                        }
                    }

                    baseBinding.bottomNavigationView.menu.getItem(position).isChecked = true
                    prevMenuItem = baseBinding.bottomNavigationView.menu.getItem(position)
                } catch (e: Exception) {
                }

            }

            override fun onPageScrollStateChanged(state: Int) {}
        })
        baseBinding.viewPager.disableScroll(true)
        setupViewPager( baseBinding.viewPager)*/
    }

    /*private fun setupViewPager(viewPager: ViewPager2) {
        val adapter = ViewPagerAdapter(supportFragmentManager)
        val homeFragment = HomeFragment()
        val orderHistoryFragment = OrderFragment()
        val inboxFragment = NotiFragment()
        val accountFragment = AccountFragment()
        adapter.addFragment(homeFragment)
        adapter.addFragment(orderHistoryFragment)
        adapter.addFragment(inboxFragment)
        adapter.addFragment(accountFragment)
        viewPager.offscreenPageLimit = 5
        viewPager.adapter = adapter
    }*/

    private fun setupViewPager(viewPager: ViewPager2) {
        val fragments = listOf(
            HomeFragment(),
            OrderFragment(),
            NotiFragment(),
            AccountFragment()
        )

        val adapter = ViewPagerAdapter(this, fragments)
        viewPager.adapter = adapter
        viewPager.offscreenPageLimit = fragments.size - 1 // Set it correctly
    }
    private fun navigateToCartView() {
        baseBinding.btnFab.setOnClickListener {
            PreferenceUtils.needToShow = false
            PreferenceUtils.isBackground = false
            when {
                PreferenceUtils.readUserVO().customer_id == 0 -> {
                    SuccessDialog.Builder(
                        this,
                        resources.getString(R.string.login_message),
                        callback = {
                            PreferenceUtils.clearCache()
                            finishAffinity()
                            //startActivity<SplashActivity>()
                            val intent = Intent(this, SplashActivity::class.java)
                            startActivity(intent)
                        })
                        .show(
                            supportFragmentManager,
                            MainActivity::class.simpleName
                        )
                }
                PreferenceUtils.readFoodOrderList()!!
                    .isNotEmpty() && PreferenceUtils.readUserVO().customer_id != 0 -> {
                    /*startActivity<CheckOutActivity>(
                        CheckOutActivity.LAT to PreferenceUtils.readUserVO().latitude,
                        CheckOutActivity.LNG to PreferenceUtils.readUserVO().longitude,
                        CheckOutActivity.ADDRESS_TYPE to ""
                    )*/
                    val intent = Intent(this, CheckOutActivity::class.java)
                    intent.putExtra(CheckOutActivity.LAT, PreferenceUtils.readUserVO().latitude)
                    intent.putExtra(CheckOutActivity.LNG, PreferenceUtils.readUserVO().longitude)
                    intent.putExtra(CheckOutActivity.ADDRESS_TYPE, "")
                    startActivity(intent)

                }
                else -> {
                    showSnackBar(resources.getString(R.string.empty_cart))
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        baseBinding.bottomNavigationView.menu.getItem(0).title = resources.getString(R.string.home)
        baseBinding.bottomNavigationView.menu.getItem(1).title = resources.getString(R.string.order)
        baseBinding.bottomNavigationView.menu.getItem(3).title = resources.getString(R.string.notification)
        baseBinding.bottomNavigationView.menu.getItem(4).title = resources.getString(R.string.account)
        isHomeKeyPressed = false
        needToShowLoading = false
        PreferenceUtils.cartCount.postValue(PreferenceUtils.readFoodOrderList()?.size)
        PreferenceUtils.cartCount.observe(this) {
            if (it.equals(0)) {
                baseBinding.tvCartCount.gone()
            } else {
                baseBinding.tvCartCount.show()
                baseBinding.tvCartCount.text = "$it"
            }

        }
        if (isOrderHistory) {
            baseBinding.bottomNavigationView.selectedItemId = R.id.action_order
            baseBinding.viewPager.currentItem = 1
            PreferenceUtils.needToShow = false
            isHome = false
            isHomeKeyPressed = false
        }
        if (isNotification){
            baseBinding.bottomNavigationView.selectedItemId = R.id.action_noti
            baseBinding.viewPager.currentItem = 2
            PreferenceUtils.needToShow = false
            isHome = false
            isHomeKeyPressed = false
        }
    }


    private fun stopService() {
        val intent = Intent(this, FattyPushyService::class.java)
        stopService(intent)
    }

    override fun onDestroy() {
        stopService()
        super.onDestroy()
    }
}
