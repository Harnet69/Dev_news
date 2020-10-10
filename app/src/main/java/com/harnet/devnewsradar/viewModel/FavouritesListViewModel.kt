package com.harnet.devnewsradar.viewModel

import android.app.Application
import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.harnet.devnewsradar.model.ArticleDatabase
import com.harnet.devnewsradar.model.Favourite
import io.reactivex.disposables.CompositeDisposable
import kotlinx.coroutines.launch

class FavouritesListViewModel(application: Application) : BaseViewModel(application) {
    private val disposable = CompositeDisposable()

    val mFavourites = MutableLiveData<List<Favourite>>()
    val mIsFavouriteLoadError = MutableLiveData<Boolean>()
    val mIsLoading = MutableLiveData<Boolean>()

    // refresh mArticles with a new data TWO WAYS TO DO IT: PARSER & RETROFIT
    fun refresh(context: Context) {
        mIsFavouriteLoadError.value = false
        // get data by old fashion manner parser
        fetchFromFavourites(context)
    }

    private fun fetchFromFavourites(context: Context){
        launch {
            val favourites = ArticleDatabase.invoke(context).favouriteDAO().getFavourites()
            mFavourites.postValue(favourites)
            mIsFavouriteLoadError.postValue(false)
            mIsLoading.postValue(false)
        }
    }

    fun deleteFromFavourites(context: Context, id: String){
        launch {
            ArticleDatabase.invoke(context).favouriteDAO().deleteFavourite(id)
            val favourites = ArticleDatabase.invoke(context).favouriteDAO().getFavourites()
            mFavourites.postValue(favourites)
        }
    }
}