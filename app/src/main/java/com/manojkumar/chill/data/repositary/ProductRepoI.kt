package com.manojkumar.chill.data.repositary

import com.manojkumar.chill.room.AppDatabase
import com.manojkumar.chill.data.model.Products
import io.reactivex.Single

interface ProductRepoI {
    fun getProductList(): Single<Products>
    fun getLocalProductList(database: AppDatabase): Single<List<Products.Product>>
}
