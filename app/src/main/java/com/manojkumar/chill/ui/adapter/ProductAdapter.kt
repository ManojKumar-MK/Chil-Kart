package com.manojkumar.chill.ui.adapter

import android.graphics.Paint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.manojkumar.chill.R
import com.manojkumar.chill.data.model.Products
import com.manojkumar.chill.helper.gone
import com.manojkumar.chill.helper.visible
import com.manojkumar.chill.room.AppDatabase
import kotlinx.android.synthetic.main.layout_single_item.view.*
import java.util.concurrent.Executor

class ProductAdapter(
    private val results: List<Products.Product?>,
    private val appDatabase: AppDatabase,
    private val executor: Executor
) :
    RecyclerView.Adapter<ProductAdapter.ProductVH>() {

    companion object
    {
        var TAG : String = "ProdcutAdapter"
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductVH {
        return ProductVH(
            LayoutInflater.from(parent.context).inflate(
                R.layout.layout_single_item,
                parent,
                false), appDatabase, executor
        )
    }

    override fun getItemCount(): Int {
        return results.size
    }

    override fun onBindViewHolder(holder: ProductVH, position: Int) {
        holder.bind(results[position]!!)
        holder.setIsRecyclable(false)
    }


    class ProductVH(private val view: View,
                    private val db: AppDatabase,
                    private val executor: Executor): RecyclerView.ViewHolder(view)  {
        fun bind(result: Products.Product) {
            view.tv_product_name.text = result.name!!
            view.tv_product_price.text = result.price!!
            view.tv_product_spacial_price.text = result.special!!

            Log.d(TAG,">>> Check  ${removeSpacialChar(view.tv_product_price.text.toString())}")
            Log.d(TAG,">>> Check  ${removeSpacialChar(view.tv_product_spacial_price.text.toString())}")



            if(removeSpacialChar(view.tv_product_price.text.toString()) > removeSpacialChar(view.tv_product_spacial_price.text.toString()))
            {
                Log.d(TAG,">>> Check if working")

                view.tv_product_price.apply {paintFlags = paintFlags or Paint.STRIKE_THRU_TEXT_FLAG}


            }

            Glide.with(view)  //2
                .load(result.image!!) //3
                .placeholder(R.drawable.logo) //5
                .error(R.drawable.logo) //6
                .into(view.iv_product) //8

            executor.execute {
                db.productDao().getProduct(result.id)?.let {
                    (view.context as AppCompatActivity).runOnUiThread {
                        hideAddButton()
                        updateView(it.quantity!!)
                        onClicks(it)
                    }

                } ?: run {
                    (view.context as AppCompatActivity).runOnUiThread {
                        onClicks(result)
                    }
                }
            }
        }

        private fun removeSpacialChar(price: String): Int {
            val format: String = price.replace("₹", "").replace(",", "")
            return format.toInt()
        }
        private fun onClicks(result: Products.Product) {
            view.btn_add.setOnClickListener {
                executor.execute {
                    db.productDao().insertProduct(result)
                    (view.context as AppCompatActivity).runOnUiThread {
                        hideAddButton()
                        updateView(result.quantity!!)
                    }
                }
            }

            view.btn_plus.setOnClickListener {
                executor.execute {
                    when {
                        result.quantity!! > 0 -> {
                            result.quantity = result.quantity!!.inc()
                            updateProduct(result)
                            (view.context as AppCompatActivity).runOnUiThread {
                                updateView(result.quantity!!)
                            }
                        }
                    }
                }
            }

            view.btn_minus.setOnClickListener {
                executor.execute {
                    if (result.quantity!! > 0) {

                        if (result.quantity!! == 1) {
                            db.productDao().deleteProduct(result)
                            (view.context as AppCompatActivity).runOnUiThread {
                                updateView(result.quantity!!)
                                showAddButton()
                            }
                        } else {
                            result.quantity = result.quantity!!.dec()
                            updateProduct(result)
                            (view.context as AppCompatActivity).runOnUiThread {
                                updateView(result.quantity!!)
                            }
                        }
                    }
                }
            }
        }

        private fun updateProduct(result: Products.Product) {
            db.productDao().updateProduct(result)
        }

        private fun updateView(quantity: Int) {
            view.tv_item_count.text = quantity.toString()
        }

        private fun showAddButton() {
            view.btn_add.visible()
            view.groupAddToCart.gone()
        }

        private fun hideAddButton() {
            view.btn_add.gone()
            view.groupAddToCart.visible()
        }
    }
}
