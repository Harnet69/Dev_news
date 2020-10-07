package com.harnet.devnews.viewModel

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.harnet.devnews.model.ArticleDatabase
import com.harnet.devnews.model.Favourite
import io.reactivex.disposables.CompositeDisposable
import kotlinx.coroutines.launch

class FavouritesListViewModel(application: Application) : BaseViewModel(application) {
    private val disposable = CompositeDisposable()

    val mFavourites = MutableLiveData<List<Favourite>>()
    val mIsFavouriteLoadError = MutableLiveData<Boolean>()
    val mIsLoading = MutableLiveData<Boolean>()

    // refresh mArticles with a new data TWO WAYS TO DO IT: PARSER & RETROFIT
    fun refresh(context: Context) {
        mIsFavouriteLoadError.value = false//TODO think is it necessary
        // get data by old fashion manner parser
        fetchFromFavourites(context)
    }

    fun fetchFromFavourites(context: Context){
        launch {
            var favourites = ArticleDatabase.invoke(context).favouriteDAO().getFavourites()
            mFavourites.postValue(favourites)
            mIsFavouriteLoadError.postValue(false)
            mIsLoading.postValue(false)
            Log.i("FavArticlees", "fetchFromFavourites: " + favourites)
        }
    }



}