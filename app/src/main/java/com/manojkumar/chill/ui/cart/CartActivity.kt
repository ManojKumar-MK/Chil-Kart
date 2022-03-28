package com.manojkumar.chill.ui.cart

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.manojkumar.chill.data.repositary.ProductRepo
import com.manojkumar.chill.helper.createFactory
import com.manojkumar.chill.room.AppDatabase
import com.manojkumar.chill.ui.Result
import com.manojkumar.chill.R
import com.manojkumar.chill.data.model.Products
import com.manojkumar.chill.helper.gone
import com.manojkumar.chill.helper.visible
import com.manojkumar.chill.ui.adapter.ProductAdapter
import kotlinx.android.synthetic.main.activity_cart.*
import java.util.concurrent.Executor
import java.util.concurrent.Executors

class CartActivity : AppCompatActivity() {

    private val productRepo = ProductRepo()

    private lateinit var viewModel: CartViewModel

    private lateinit var mDB: AppDatabase
    private lateinit var executor: Executor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)
        init()
        setObserver()
        loadProducts()
        onClicks()

    }

    private fun init() {
        Log.d(TAG, " >>> Initializing viewModel")

        mDB = AppDatabase.getInstance(this)!!
        executor = Executors.newSingleThreadExecutor()

        val factory = CartViewModel(mDB).createFactory()
        viewModel = ViewModelProvider(this, factory).get(CartViewModel::class.java)
        viewModel.setRepository(productRepo)
    }

    private fun setObserver() {
        viewModel.mutableProductData.observe(this, Observer {
            updateView(it)
        })
    }

    private fun loadProducts() {
        viewModel.getLocalProductList()
    }

    private fun onClicks() {
        btn_cart_retry.setOnClickListener {
            loadProducts()
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun updateView(state: Result?) {
        when {
            state!!.loading -> showLoading()
            state.success -> setProductRecyclerView(state.data as List<Products.Product>)
            state.failure -> showError()
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    @SuppressLint("SetTextI18n")
    private fun setProductRecyclerView(products: List<Products.Product>) {
        hideLoading()
        rv_cart_product.visible()
        ll_total_cart_value.visible()

        val price = CalculatePrice.calculateSpacialPrice(products)
        val savedPrice = CalculatePrice.calculatePrice(products) - price
        tv_total_cash_component.text = "Rs. $price"
        tv_total_save_amount.text = "Save Rs. $savedPrice"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            layout_cart.setBackgroundColor(resources.getColor(android.R.color.white))
            supportActionBar?.show()
        }

        rv_cart_product.layoutManager = LinearLayoutManager(this)
        rv_cart_product.adapter = ProductAdapter(products, mDB, executor)

    }

    private fun showError() {
        rv_cart_product.gone()
        ll_total_cart_value.gone()
        groupCartRetry.visible()
        hideLoading()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            supportActionBar?.hide()
        }
    }

    private fun showLoading() {
        rv_cart_product.gone()
        ll_total_cart_value.gone()
        groupCartRetry.gone()
        pb_cart.visible()
    }

    private fun hideLoading() {
        pb_cart.gone()
    }

    override fun onDestroy() {
        super.onDestroy()
        AppDatabase.destroyInstance()
    }

    companion object {
        const val TAG = "CartActivity"

        fun start(context: Context) {
            val intent = Intent(context, CartActivity::class.java)
            context.startActivity(intent)
        }
    }
}
