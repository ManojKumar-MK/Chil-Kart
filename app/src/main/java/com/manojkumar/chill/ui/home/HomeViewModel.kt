package com.manojkumar.chill.ui.home

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.manojkumar.chill.data.repositary.ProductRepoI
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

import com.manojkumar.chill.ui.Result

class HomeViewModel: ViewModel() {
    companion object {
        const val TAG = "HomeViewModel"
    }


    var mutableProductData: MutableLiveData<Result> = MutableLiveData()

    private var compositeDisposable = CompositeDisposable()

    private lateinit var productRepoI: ProductRepoI

    var result = Result()
        set(value) {
            field = value
            publishState(value)
        }

    fun setRepository(productRepoI: ProductRepoI) {
        this.productRepoI = productRepoI
    }

    private fun publishState(state: Result) {
        Log.d(TAG," >>> Publish State : $state")
        mutableProductData.postValue(state)
    }

    fun getProductList() {
        Log.d(TAG, " >>> Getting product list")

        result = result.copy(loading = true, message = "Loading . . .")
        compositeDisposable.add(
            productRepoI.getProductList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe( {
                    result = if (it != null) {
                        result.copy(
                            loading = false,
                            success = true,
                            data = it
                        )
                    } else {
                        result.copy(
                            loading = false,
                            failure = true,
                            message = "Empty list came."
                        )
                    }
                }, {
                    result =
                        result.copy(
                            loading = false,
                            failure = true,
                            message = it.localizedMessage
                        )

                    Log.e(TAG, "Error while getting product list", it)
                })
        )
    }

    override fun onCleared() {
        super.onCleared()
        Log.d(TAG, " >>> Clearing compositeDisposable object")
        compositeDisposable.dispose()
    }


}