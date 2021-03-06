package com.harnet.devnewsradar.adapter

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.harnet.devnewsradar.R
import com.harnet.devnewsradar.databinding.ItemFavouriteBinding
import com.harnet.devnewsradar.model.Favourite
import com.harnet.devnewsradar.service.OnSingleClickListenerService
import com.harnet.devnewsradar.view.FavouritesListFragmentDirections
import com.harnet.devnewsradar.viewModel.FavouritesListViewModel
import kotlinx.android.synthetic.main.item_favourite.view.*
import java.io.IOException

class FavouritesListAdapter(
    val favouritesList: ArrayList<Favourite>,
    val viewModel: FavouritesListViewModel
) : RecyclerView.Adapter<FavouritesListAdapter.FavouritesViewHolder>() {

    //for updating information from a backend
    fun updateFavouritesList(newFavouritesList: List<Favourite>) {
        favouritesList.clear()
        favouritesList.addAll(newFavouritesList)
        //reset RecycleView and recreate a list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavouritesViewHolder {
        val inflator = LayoutInflater.from(parent.context)
        // elements of the list transforms into views
        val view = DataBindingUtil.inflate<ItemFavouriteBinding>(
            inflator,
            R.layout.item_favourite,
            parent,
            false
        )
        return FavouritesViewHolder(view)
    }

    override fun getItemCount() = favouritesList.size

    override fun onBindViewHolder(holder: FavouritesViewHolder, position: Int) {
        var isFavourite = true
        holder.view.favouriteImg.setImageResource(android.R.drawable.btn_star_big_on)
//        // bind view to information from a list
        holder.view.favourite = favouritesList[position]
//        // add click listener to favourite button
        holder.view.favouriteImg.setOnClickListener {
            isFavourite = isFavourite != true
            val context = holder.view.favouriteImg.context
            if (isFavourite) {
                //record the article to favourites
                holder.view.favouriteImg.setImageResource(android.R.drawable.btn_star_big_on)
            } else {
                //delete the article from favourites
                try {
                    AlertDialog.Builder(context)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Delete")
                        .setMessage("Do you want to remove the article from favorite?")
                        .setPositiveButton("Delete") { dialogInterface: DialogInterface, i: Int ->
                            try {
                                viewModel.deleteFromFavourites(
                                    holder.view.favouriteImg.context,
                                    favouritesList[position].id
                                )
                                holder.view.favouriteImg.setImageResource(android.R.drawable.btn_star_big_off)
                                Toast.makeText(
                                    context,
                                    "Remove from favourite",
                                    Toast.LENGTH_SHORT
                                ).show()
                                viewModel.mFavourites.value?.let { it1 -> updateFavouritesList(it1) }
                            } catch (e: IOException) {
                                e.printStackTrace()
                            }
                        }.setNegativeButton("Cancel") { dialogInterface: DialogInterface, i: Int ->
                        }.show()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
    }

    class FavouritesViewHolder(var view: ItemFavouriteBinding) : RecyclerView.ViewHolder(view.root)

    //Fix blinking RecyclerView
    override fun getItemId(position: Int): Long {
        return favouritesList.get(position).id.toLong()
    }
}