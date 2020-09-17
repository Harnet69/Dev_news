package com.harnet.devnews.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.harnet.devnews.model.Article

class ArticleViewModel: ViewModel() {
    val mArticleLiveData = MutableLiveData<Article>()

    //retrieve data from a database by id
    fun fetch(articleId: String){
        //mock data for testing
        val article1 = Article(articleId,"This electrical transmission tower", "Twitter", "www.somewhere.com", "54", "4")
        mArticleLiveData.value = article1
    }
}