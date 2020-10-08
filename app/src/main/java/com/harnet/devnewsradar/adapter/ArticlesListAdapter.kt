package com.harnet.devnewsradar.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.harnet.devnewsradar.R
import com.harnet.devnewsradar.model.Article
import com.harnet.devnewsradar.view.ArticlesListFragmentDirections
import com.harnet.devnewsradar.viewModel.FavouritesListViewModel
import kotlinx.android.synthetic.main.item_atricle.view.*

class ArticlesListAdapter(val articlesList: ArrayList<Article>, val favouritesListViewModel: FavouritesListViewModel) : RecyclerView.Adapter<ArticlesListAdapter.ArticleViewHolder>() {

    //for updating information from a backend
    fun updateArticlesList(newArticlesList: List<Article>){
        articlesList.clear()
        articlesList.addAll(newArticlesList)
        //reset RecycleView and recreate a list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        val inflator = LayoutInflater.from(parent.context)
        // elements of the list transforms into views
        val view = inflator.inflate(R.layout.item_atricle, parent, false)
        return ArticleViewHolder(view)
    }

    override fun getItemCount() = articlesList.size

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        var isFavourite = true
        //attach view to information from a list
        holder.view.articleTitle_in_list.text = articlesList[position].title
        holder.view.articleAuthor_in_list.text = articlesList[position].author

        // add click listener to favourite button
        holder.view.favourite_img.setOnClickListener {
            println("Click!!!")
            if(isFavourite){
                holder.view.favourite_img.setImageResource(android.R.drawable.btn_star_big_on)
                favouritesListViewModel.addToFavourite(holder.view.context, articlesList[position])
                //TODO record the article to favourites
            }else{
                holder.view.favourite_img.setImageResource(android.R.drawable.btn_star_big_off)
                favouritesListViewModel.deleteFromFavourites(holder.view.context, articlesList[position].id)
                //TODO delete the article from favourites
            }
            isFavourite = isFavourite != true
        }
        //add click listener to article details item and bind it with detail page
        holder.view.article_details.setOnClickListener {
            // navigate to appropriate detail fragment
            val action =
                ArticlesListFragmentDirections.actionArticlesListFragmentToArticleFragment()
            // send article id to ArticleFragment
            action.articleId = articlesList[position].uuid
            action.isFavourite = isFavourite
            Navigation.findNavController(it).navigate(action)
        }
    }

    class ArticleViewHolder(var view: View) : RecyclerView.ViewHolder(view)
}