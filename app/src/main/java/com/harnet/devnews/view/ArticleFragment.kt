package com.harnet.devnews.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.harnet.devnews.R
import com.harnet.devnews.model.ArticleDatabase
import com.harnet.devnews.viewModel.ArticleViewModel
import com.harnet.devnews.viewModel.ArticlesListViewModel
import kotlinx.android.synthetic.main.fragment_article.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class ArticleFragment : Fragment() {
    lateinit var viewModel: ArticleViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_article, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel =  ViewModelProviders.of(this).get(ArticleViewModel::class.java)
        // get ID from Articles list adapter
        //TODO implement functionality for getting argument from nav
        // receive arguments from sending fragment
        arguments?.let {
            val articleId = ArticleFragmentArgs.fromBundle(it).articleId
            //TODO make a coroutine for invoking suspended fun
            GlobalScope.launch {
                viewModel.fetch(view.context, articleId.toString())
            }
        }

        observeViewModel()
    }

    // observes article object and binds its data to view elements
    fun observeViewModel(){
        viewModel.mArticleLiveData.observe(this, Observer { article ->
            article?.let{
                article_id.text = article.id
                article_title.text = article.title
                article_author.text = article.author
                article_time.text = article.time
                article_score.text = article.score
                article_url.text = article.url
            }
        })
    }

}