package com.harnet.devnewsradar.adapter

import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.harnet.devnewsradar.R
import com.harnet.devnewsradar.databinding.ItemAtricleBinding
import com.harnet.devnewsradar.model.Article

class ArticlesListAdapter(val articlesList: ArrayList<Article>) :
    RecyclerView.Adapter<ArticlesListAdapter.ArticleViewHolder>() {
    //new articles
    private var newParsedArticlesList = ArrayList<Article>()

    //for updating information from a backend
    fun updateArticlesList(newArticlesList: List<Article>) {
        // get new parsed articles
        if (articlesList.size > 0) {
            getNewArticles(articlesList, newArticlesList as ArrayList<Article>)
        }

        articlesList.clear()
        articlesList.addAll(newArticlesList)
        //reset RecycleView and recreate a list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        val inflator = LayoutInflater.from(parent.context)
        // elements of the list transforms into views. DataBinding approach
        val view = DataBindingUtil.inflate<ItemAtricleBinding>(
            inflator,
            R.layout.item_atricle,
            parent,
            false
        )
        return ArticleViewHolder(view)
    }

    override fun getItemCount() = articlesList.size

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {

        //attach article to holder by DataBinding approach to variable in the layout
        holder.view.article = articlesList[position]
        // mark articles were read already
        if (articlesList[position].isWasRead) {
            holder.view.articleTitleInList.setTypeface(
                holder.view.articleTitleInList.typeface,
                Typeface.ITALIC
            )
        }

        // mark newly parsed articles
        for (newArticle in newParsedArticlesList) {
            if (articlesList[position].id.equals(newArticle.id)) {
                //NEW button appearance
                holder.view.newArticle.visibility = View.VISIBLE
            }
        }
    }

    class ArticleViewHolder(var view: ItemAtricleBinding) : RecyclerView.ViewHolder(view.root)

    // fix blinking RecyclerView
    override fun getItemId(position: Int): Long {
        return articlesList.get(position).id.toLong()
    }

    // get new articles
    private fun getNewArticles(
        articlesList: ArrayList<Article>, newArticlesList: ArrayList<Article>
    ) {
        newParsedArticlesList = newArticlesList.toList() as ArrayList<Article>

        for (article in articlesList) {
            for (newArticle in newArticlesList) {
                if (article.id.equals(newArticle.id)) {
                    newParsedArticlesList.remove(newArticle)
                }
            }
        }
    }
}