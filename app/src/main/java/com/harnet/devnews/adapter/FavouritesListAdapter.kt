package com.harnet.devnews.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.harnet.devnews.R
import com.harnet.devnews.model.Favourite
import com.harnet.devnews.view.ArticlesListFragmentDirections
import com.harnet.devnews.view.FavouritesListFragmentDirections
import kotlinx.android.synthetic.main.item_atricle.view.*

class FavouritesListAdapter(val favouritesList: ArrayList<Favourite>) : RecyclerView.Adapter<FavouritesListAdapter.FavouritesViewHolder>() {
    //for updating information from a backend

    fun updateFavouritesList(newFavouritesList: List<Favourite>){
        favouritesList.clear()
        favouritesList.addAll(newFavouritesList)
        //reset RecycleView and recreate a list
        notifyDataSetChanged()
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavouritesViewHolder {
        val inflator = LayoutInflater.from(parent.context)
        // elements of the list transforms into views
        val view = inflator.inflate(R.layout.item_atricle, parent, false)
        return FavouritesViewHolder(view)
    }

    override fun getItemCount() = favouritesList.size

    override fun onBindViewHolder(holder: FavouritesViewHolder, position: Int) {
        var isFavourite = false
        //attach view to information from a list
        holder.view.articleTitle_in_list.text = favouritesList[position].title
        holder.view.articleAuthor_in_list.text = favouritesList[position].author

        // add click listener to favourite button
        holder.view.favourite_img.setOnClickListener {
            isFavourite = isFavourite != true
            if(isFavourite){
                holder.view.favourite_img.setImageResource(android.R.drawable.btn_star_big_on)
                //TODO record the article to favourites
            }else{
                holder.view.favourite_img.setImageResource(android.R.drawable.btn_star_big_off)
                //TODO delete the article from favourites
            }
        }
        //add click listener to article details item and bind it with detail page
        holder.view.article_details.setOnClickListener {
            // navigate to appropriate detail fragment
            val action =
                FavouritesListFragmentDirections.actionFavouritesListFragmentToFavouriteFragment()
            // send article id to ArticleFragment
            action.articleId = favouritesList[position].uuid
            action.isFavourite = isFavourite
            Log.i("FavouriteTracking", "onBindViewHolder: send " + favouritesList[position].uuid + isFavourite)
            Navigation.findNavController(it).navigate(action)
        }
    }

    class FavouritesViewHolder(var view: View) : RecyclerView.ViewHolder(view)
}