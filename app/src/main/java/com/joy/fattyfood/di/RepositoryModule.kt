package com.joy.fattyfood.di

import com.joy.fattyfood.data.repository.*
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun bindSplashRepository(repo: SplashRepositoryImpl): SplashRepository

    @Binds
    abstract fun bindMainRepository(repo : MainRepositoryImpl) : MainRepository
    @Binds
    abstract fun bindAuthRepository(repo: AuthRepositoryImpl): AuthRepository

    @Binds
    abstract fun bindUserRepository(repo: UserRepositoryImpl): UserRepository

    @Binds
    abstract fun bindAddressRepository(repo: AddressRepositoryImpl): AddressRepository

    @Binds
    abstract fun bindHomeRepository(repo : HomeRepositoryImpl) : HomeRepository

    @Binds
    abstract fun bindCategoryRepository(repo : CategoryRepositoryImpl) : CategoryRepository

    @Binds
    abstract fun bindRestaurantRepository(repo : RestaurantRepositoryImpl) : RestaurantRepository


    @Binds
    abstract fun bindRestaurantDetailRepository(repo : RestaurantDetailRepositoryImpl) : RestaurantDetailRepository

    @Binds
    abstract fun bindSearchRepository(repo : SearchRepositoryImpl) : SearchRepository

    @Binds
    abstract fun bindWishListRepository(repo : WishListRepositoryImpl) : WishListRepository


    @Binds
    abstract fun bindAboutRepository(repo : AboutRepositoryImpl) : AboutRepository

    @Binds
    abstract fun bindTrackOrderRepository(repo : TrackOrderRepositoryImpl) : TrackOrderRepository
    @Binds
    abstract fun bindTrackFoodOrderRepository(repo : TrackFoodOrderRepositoryImpl) : TrackFoodOrderRepository

    @Binds
    abstract fun bindTrackParcelRepository(repo : TrackParcelRepositoryImpl) : TrackParcelRepository

    @Binds
    abstract fun bindOrderRepository(repo : OrderRepositoryImpl) : OrderRepository

    @Binds
    abstract fun bindParcelRepository(repo : ParcelOrderRepositoryImpl) : ParcelRepository

    @Binds
    abstract fun bindNotiRepository(repo : NotiRepositoryImpl) : NotiRepository

    @Binds
    abstract fun bindDeliverRatingRepository(repo : DeliveryRatingRepositoryImpl) : DeliveryRatingRepository
}
