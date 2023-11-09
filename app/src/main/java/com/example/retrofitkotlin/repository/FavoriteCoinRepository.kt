package com.example.retrofitkotlin.repository

import android.content.Context
import android.widget.Toast
import com.example.retrofitkotlin.adapter.FavoritePageAdapter
import com.example.retrofitkotlin.model.CryptoModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class FavoriteCoinRepository {
    private val db = FirebaseFirestore.getInstance()
    private val user = FirebaseAuth.getInstance().currentUser
    private var adapter: FavoritePageAdapter? = null

    fun createFavoriteCoinDocument (cryptoModel: CryptoModel, context: Context) {
        val userCollection = user?.uid?.let {uid ->
            db.collection("users")
                .document(uid)
                .collection("favorite_coins")
        }

        userCollection?.whereEqualTo("id", cryptoModel.id)
            ?.get()
            ?.addOnSuccessListener { querySnapshot ->
                if (querySnapshot.isEmpty) {

                userCollection.add(
                    hashMapOf(
                        "id" to cryptoModel.id,
                        "name" to cryptoModel.name,
                        "price" to cryptoModel.price,
                        "icon" to cryptoModel.icon,
                        "priceChange1d" to cryptoModel.priceChange1d,
                        "symbol" to cryptoModel.symbol
                    )
                )

                } else {
                    Toast.makeText(context, "Coin already favorited", Toast.LENGTH_SHORT).show()
                }
            }
    }

    fun fetchFavoriteCoinsFromFirestore(context: Context) {
        val userCollection = user?.uid?.let { uid ->
            db.collection("users")
                .document(uid)
                .collection("favorite_coins")
        }

        userCollection?.get()
            ?.addOnSuccessListener { querySnapshot ->
            val favoriteCoinList = ArrayList<CryptoModel>()
            for (documentSnapshot in querySnapshot.documents) {
                val favoriteCoin = documentSnapshot.toObject(CryptoModel::class.java)
                if (favoriteCoin != null){
                    favoriteCoinList.add(favoriteCoin)
                }
            }
                adapter?.setData(favoriteCoinList)
        }?.addOnFailureListener { exception ->
            Toast.makeText(context, "Failed to fetch favorite coins: $exception", Toast.LENGTH_SHORT).show()
        }
    }

    fun deleteCoinFromFavorites(coinId: String, context: Context) {
        val userCollection = user?.uid?.let {uid ->
            db.collection("users")
                .document(uid)
                .collection("favorite_coins")
        }
        userCollection
            ?.whereEqualTo("id", coinId)
            ?.get()
            ?.addOnSuccessListener { querySnapshot ->
                for (document in querySnapshot) {
                    document.reference.delete()
                        .addOnSuccessListener {
                            fetchFavoriteCoinsFromFirestore(context)
                        }
                }

            }
    }

    fun setAdapter(adapter: FavoritePageAdapter){
        this.adapter = adapter
    }
}