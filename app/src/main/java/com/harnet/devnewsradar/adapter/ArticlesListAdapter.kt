package com.harnet.devnewsradar.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.harnet.devnewsradar.R
import com.harnet.devnewsradar.databinding.ItemAtricleBinding
import com.harnet.devnewsradar.model.Article

class ArticlesListAdapter(val articlesList: ArrayList<Article>) :
    RecyclerView.Adapter<ArticlesListAdapter.ArticleViewHolder>() {

    //for updating information from a backend
    fun updateArticlesList(newArticlesList: List<Article>) {
        articlesList.clear()
        articlesList.addAll(newArticlesList)
        //reset RecycleView and recreate a list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        val inflator = LayoutInflater.from(parent.context)
        // elements of the list transforms into views
        //classic approach
//        val view = inflator.inflate(R.layout.item_atricle, parent, false)
        // DataBinding approach
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
        val isFavourite = false

        //attach article to holder by DataBinding approach to variable in the layout
        holder.view.article = articlesList[position]
    }

    class ArticleViewHolder(var view: ItemAtricleBinding) : RecyclerView.ViewHolder(view.root)

    //Fix blinking RecyclerView
    override fun getItemId(position: Int): Long {
        return articlesList.get(position).id.toLong()
    }
}