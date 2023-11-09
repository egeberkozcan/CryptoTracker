package com.example.retrofitkotlin.adapter

import androidx.appcompat.app.AlertDialog
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.retrofitkotlin.R
import com.example.retrofitkotlin.model.CryptoModel
import com.example.retrofitkotlin.repository.FavoriteCoinRepository

class FavoritePageAdapter(
    private val cryptoList: ArrayList<CryptoModel>,
    private val listener: OnItemClickListener,
    private val favoriteCoinRepository: FavoriteCoinRepository,
) : RecyclerView.Adapter<FavoritePageAdapter.ViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(crypto: CryptoModel)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.row_layout_favorite, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val crypto = cryptoList[position]
        holder.bind(crypto)
    }

    override fun getItemCount(): Int {
        return cryptoList.size
    }

    fun setData(newCryptoList: ArrayList<CryptoModel>) {
        val previousSize = cryptoList.size
        cryptoList.clear()
        cryptoList.addAll(newCryptoList)
        val newSize = cryptoList.size
        if (previousSize < newSize) {
            notifyItemRangeInserted(previousSize, newSize - previousSize)
        } else if (previousSize > newSize) {
            notifyItemRangeRemoved(newSize, previousSize - newSize)
        } else {
            notifyItemRangeChanged(0, newSize)
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textName: TextView = itemView.findViewById(R.id.textName)
        private val textPrice: TextView = itemView.findViewById(R.id.textPrice)
        private val imageIcon: ImageView = itemView.findViewById(R.id.imageIcon)
        private val textSymbol: TextView = itemView.findViewById(R.id.textSymbol)
        private val textPrice1d: TextView = itemView.findViewById(R.id.textPrice1d)
        private val removeFromFavorites: ImageView = itemView.findViewById(R.id.removeFromFavorites)

        fun bind(cryptoModel: CryptoModel) {
            textName.text = cryptoModel.name
            textSymbol.text = cryptoModel.symbol
            textPrice.text = String.format("$%.2f", cryptoModel.price)
            textPrice1d.text = String.format("%.2f%%", cryptoModel.priceChange1d)

            if (cryptoModel.priceChange1d > 0 ){
                textPrice1d.setTextColor(Color.GREEN)
            } else {
                textPrice1d.setTextColor(Color.RED)
            }

            Glide.with(itemView.context)
                .load(cryptoModel.icon)
                .into(imageIcon)
            itemView.setOnClickListener {
                listener.onItemClick(cryptoModel)
            }

            removeFromFavorites.setOnClickListener {
                val builder = AlertDialog.Builder(itemView.context)
                val coinId = cryptoModel.id
                builder.setTitle("Remove Coin")
                    .setMessage("Are you sure you want to remove ${cryptoModel.name} from favorites ?")
                    .setPositiveButton("YES") {_, _->
                        favoriteCoinRepository.deleteCoinFromFavorites(coinId, itemView.context)
                        Toast.makeText(itemView.context, "${cryptoModel.name} removed from favorites", Toast.LENGTH_SHORT).show()
                    }
                    .setNegativeButton("NO") {d, _->
                        d.dismiss()
                    }
                    .show()
            }
        }
    }
}