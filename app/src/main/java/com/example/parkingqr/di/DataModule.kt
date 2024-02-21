package com.example.parkingqr.di

import com.example.parkingqr.data.repo.auth.AuthRepository
import com.example.parkingqr.data.repo.auth.AuthRepositoryImpl
import com.example.parkingqr.data.repo.invoice.InvoiceRepository
import com.example.parkingqr.data.repo.invoice.InvoiceRepositoryImpl
import com.example.parkingqr.data.repo.user.UserRepository
import com.example.parkingqr.data.repo.user.UserRepositoryImpl
import com.example.parkingqr.data.repo.vehicle.VehicleRepository
import com.example.parkingqr.data.repo.vehicle.VehicleRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {
    @Binds
    @Singleton
    abstract fun bindAuthRepository(repository: AuthRepositoryImpl): AuthRepository

    @Binds
    @Singleton
    abstract fun bindUserRepository(repository: UserRepositoryImpl): UserRepository

    @Binds
    @Singleton
    abstract fun bindVehicleRepository(repository: VehicleRepositoryImpl): VehicleRepository

    @Binds
    @Singleton
    abstract fun bindInvoiceRepository(repository: InvoiceRepositoryImpl): InvoiceRepository

}