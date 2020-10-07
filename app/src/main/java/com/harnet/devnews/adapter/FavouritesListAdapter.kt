package com.harnet.devnews.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.harnet.devnews.R
import com.harnet.devnews.model.ArticleDatabase
import com.harnet.devnews.model.Favourite
import com.harnet.devnews.view.ArticlesListFragmentDirections
import com.harnet.devnews.view.FavouritesListFragmentDirections
import com.harnet.devnews.viewModel.FavouritesListViewModel
import kotlinx.android.synthetic.main.item_atricle.view.*

class FavouritesListAdapter(val favouritesList: ArrayList<Favourite>, val viewModel: FavouritesListViewModel) : RecyclerView.Adapter<FavouritesListAdapter.FavouritesViewHolder>() {
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
        var isFavourite = true
        //attach view to information from a list
        holder.view.articleTitle_in_list.text = favouritesList[position].title
        holder.view.articleAuthor_in_list.text = favouritesList[position].author
        holder.view.favourite_img.setImageResource(android.R.drawable.btn_star_big_on)

        // add click listener to favourite button
        holder.view.favourite_img.setOnClickListener {
            isFavourite = isFavourite != true
            if(isFavourite){
                //record the article to favourites
                holder.view.favourite_img.setImageResource(android.R.drawable.btn_star_big_on)
            }else{
                //delete the article from favourites
                //TODO ask if user want to delete it from favourites
                viewModel.deleteFromFavourites(holder.view.context, favouritesList[position].id)
                holder.view.favourite_img.setImageResource(android.R.drawable.btn_star_big_off)
                Toast.makeText(holder.view.context, "Remove from favourite", Toast.LENGTH_SHORT).show()
                viewModel.mFavourites.value?.let { it1 -> updateFavouritesList(it1) }
            }
        }
        //add click listener to article details item and bind it with detail page
        holder.view.article_details.setOnClickListener {
            // navigate to appropriate detail fragment
            val action = FavouritesListFragmentDirections.actionFavouritesListFragmentToFavouriteFragment()
            // send article id to ArticleFragment
            action.articleId = favouritesList[position].uuid
            //TODO hardcoding!!!
            action.isFavourite = true
            Log.i("FavouriteTracking", "onBindViewHolder: send " + favouritesList[position].uuid + isFavourite)
            Navigation.findNavController(it).navigate(action)
        }
    }

    class FavouritesViewHolder(var view: View) : RecyclerView.ViewHolder(view)
}