package com.manojkumar.chill.data

import com.manojkumar.chill.utils.PRODUCT_LIST_URL
import com.manojkumar.chill.data.model.Products
import retrofit2.Call
import retrofit2.http.GET

interface ApiInterface {

    @GET(PRODUCT_LIST_URL)
    fun getProductList(): Call<Products>
}