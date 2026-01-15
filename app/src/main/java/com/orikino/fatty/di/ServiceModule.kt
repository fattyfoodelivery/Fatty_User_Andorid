package com.orikino.fatty.di

import com.orikino.fatty.data.apiService.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import retrofit2.Retrofit
import javax.inject.Named


@Module
@InstallIn(ViewModelComponent::class)
object ServiceModule {

    @Provides
    fun provideSplashService(@Named("MVVMClient") retrofit: Retrofit) : SplashService{
        return retrofit.create(SplashService::class.java)
    }

    @Provides
    fun provideMainService(@Named("MVVMClient") retrofit: Retrofit) : MainService{
        return retrofit.create(MainService::class.java)
    }


    @Provides
    fun provideAuthService(@Named("MVVMClient") retrofit: Retrofit) : AuthService{
        return retrofit.create(AuthService::class.java)
    }

    @Provides
    fun provideUserService(@Named("MVVMClient") retrofit: Retrofit) : UserService{
        return retrofit.create(UserService::class.java)
    }

    @Provides
    fun provideAddressService(@Named("MVVMClient") retrofit: Retrofit) : AddressService{
        return retrofit.create(AddressService::class.java)
    }

    @Provides
    fun provideHomeService(@Named("MVVMClient") retrofit: Retrofit) : HomeService{
        return retrofit.create(HomeService::class.java)
    }

    @Provides
    fun provideServiceItemService(@Named("MVVMClient") retrofit: Retrofit) : ServiceItemService{
        return retrofit.create(ServiceItemService::class.java)
    }
    @Provides
    fun provideCategoryService(@Named("MVVMClient") retrofit: Retrofit) : CategoryService{
        return retrofit.create(CategoryService::class.java)
    }

    @Provides
    fun provideRestaurantService(@Named("MVVMClient") retrofit: Retrofit) : RestaurantService{
        return retrofit.create(RestaurantService::class.java)
    }

    @Provides
    fun provideRestaurantDetailService(@Named("MVVMClient") retrofit: Retrofit) : RestaurantDetailService{
        return retrofit.create(RestaurantDetailService::class.java)
    }

    @Provides
    fun provideOrderService(@Named("MVVMClient") retrofit: Retrofit) : OrderService{
        return retrofit.create(OrderService::class.java)
    }

    @Provides
    fun provideTrackOrderService(@Named("MVVMClient") retrofit: Retrofit) : TrackOrderService{
        return retrofit.create(TrackOrderService::class.java)
    }
    @Provides
    fun provideParcelService(@Named("MVVMClient") retrofit: Retrofit) : ParcelService{
        return retrofit.create(ParcelService::class.java)
    }

    @Provides
    fun provideTrackOrderApi(@Named("MVVMClient") retrofit: Retrofit) : TrackOrderApi{
        return retrofit.create(TrackOrderApi::class.java)
    }

    @Provides
    fun provideTrackParcelApi(@Named("MVVMClient") retrofit: Retrofit) : TrackParcelApi{
        return retrofit.create(TrackParcelApi::class.java)
    }

    @Provides
    fun provideNotiService(@Named("MVVMClient") retrofit: Retrofit) : NotiService{
        return retrofit.create(NotiService::class.java)
    }

    @Provides
    fun provideSearchService(@Named("MVVMClient") retrofit: Retrofit) : SearchService{
        return retrofit.create(SearchService::class.java)
    }
    @Provides
    fun provideWishListService(@Named("MVVMClient") retrofit: Retrofit) : WishListService{
        return retrofit.create(WishListService::class.java)
    }

    @Provides
    fun provideAboutService(@Named("MVVMClient") retrofit: Retrofit) : AboutService{
        return retrofit.create(AboutService::class.java)
    }

    @Provides
    fun provideDeliverRatingService(@Named("MVVMClient") retrofit: Retrofit) : DeliveryRatingService{
        return retrofit.create(DeliveryRatingService::class.java)
    }
}