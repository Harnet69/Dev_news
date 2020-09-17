package com.harnet.devnews.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.harnet.devnews.model.Article

class ArticlesListViewModel: ViewModel() {
    val mArticles = MutableLiveData<List<Article>>()
    val mIsArticleLoadError = MutableLiveData<Boolean>()
    val mIsLoading = MutableLiveData<Boolean>()

    // refresh mArticles with a new data
    fun refresh(){
        val article1 = Article("1","This electrical transmission tower has a problem (twitter.com)", "Twi", "", "54", "4")
        val article2 = Article("2","electrical transmission tower has a problem (twitter.com)", "Ttter", "", "12", "78")
        val article3 = Article("3","transmission tower has a problem (twitter.com)", "tter", "", "10", "35")
        val article4 = Article("4","has a problem (twitter.com)", "Twir", "", "15", "67")
        val article5 = Article("5","problem (twitter.com)", "ter", "", "22", "234")
        val article6 = Article("6","This electrical transmission tower has a problem (twitter.com)", "r", "", "3", "0")
        val article7 = Article("7","transmission tower has", "Tw", "", "67", "234")
        val article8 = Article("8","This electrical", "itt", "", "17", "7")
        val articles = listOf(article1, article2, article3, article4, article5, article6, article7, article8)

        mArticles.value = articles
        mIsArticleLoadError.value = false
        mIsLoading.value = false
    }
}