package com.example.parkingqr.di

import android.content.Context
import com.example.parkingqr.data.local.auth.AuthLocalData
import com.example.parkingqr.data.local.auth.AuthLocalDataSource
import com.example.parkingqr.data.local.invoice.InvoiceLocalData
import com.example.parkingqr.data.local.invoice.InvoiceLocalDataSource
import com.example.parkingqr.data.local.user.UserLocalData
import com.example.parkingqr.data.local.user.UserLocalDataSource
import com.example.parkingqr.data.local.vehicle.VehicleLocalData
import com.example.parkingqr.data.local.vehicle.VehicleLocalDataSource
import com.example.parkingqr.data.remote.auth.AuthRemoteData
import com.example.parkingqr.data.remote.auth.AuthRemoteDataSource
import com.example.parkingqr.data.remote.invoice.InvoiceRemoteData
import com.example.parkingqr.data.remote.invoice.InvoiceRemoteDataSource
import com.example.parkingqr.data.remote.parkinglot.ParkingLotRemoteData
import com.example.parkingqr.data.remote.parkinglot.ParkingLotRemoteDataSource
import com.example.parkingqr.data.remote.user.UserRemoteData
import com.example.parkingqr.data.remote.user.UserRemoteDataSource
import com.example.parkingqr.data.remote.vehicle.VehicleRemoteData
import com.example.parkingqr.data.remote.vehicle.VehicleRemoteDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
class AppModule {
    @Provides
    @Singleton
    fun provideAuthRemoteRepository(@ApplicationContext context: Context): AuthRemoteData{
        return AuthRemoteDataSource(context)
    }

    @Provides
    @Singleton
    fun provideAuthLocalRepository(@ApplicationContext context: Context): AuthLocalData{
        return AuthLocalDataSource(context)
    }

    @Provides
    @Singleton
    fun provideInvoiceRemoteRepository(@ApplicationContext context: Context): InvoiceRemoteData{
        return InvoiceRemoteDataSource(context)
    }

    @Provides
    @Singleton
    fun provideInvoiceLocalRepository(@ApplicationContext context: Context): InvoiceLocalData{
        return InvoiceLocalDataSource(context)
    }

    @Provides
    @Singleton
    fun provideUserRemoteRepository(@ApplicationContext context: Context): UserRemoteData{
        return UserRemoteDataSource(context)
    }

    @Provides
    @Singleton
    fun provideUserLocalRepository(@ApplicationContext context: Context): UserLocalData{
        return UserLocalDataSource(context)
    }

    @Provides
    @Singleton
    fun provideVehicleRemoteRepository(@ApplicationContext context: Context): VehicleRemoteData{
        return VehicleRemoteDataSource(context)
    }

    @Provides
    @Singleton
    fun provideVehicleLocalRepository(@ApplicationContext context: Context): VehicleLocalData{
        return VehicleLocalDataSource(context)
    }

    @Provides
    @Singleton
    fun provideParkingLotRemoteRepository(@ApplicationContext context: Context): ParkingLotRemoteData{
        return ParkingLotRemoteDataSource(context)
    }
}