package com.example.retrofitkotlin.ui.favorite

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.retrofitkotlin.R
import com.example.retrofitkotlin.adapter.FavoritePageAdapter
import com.example.retrofitkotlin.databinding.ActivityFavoriteBinding
import com.example.retrofitkotlin.model.CryptoModel
import com.example.retrofitkotlin.repository.FavoriteCoinRepository
import com.example.retrofitkotlin.ui.home.MainActivity
import com.example.retrofitkotlin.ui.profile.ProfileActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class FavoriteActivity: AppCompatActivity(), FavoritePageAdapter.OnItemClickListener {

    private lateinit var binding : ActivityFavoriteBinding
    private lateinit var favoriteCoinsRecyclerView: RecyclerView
    private lateinit var favoritePageAdapter: FavoritePageAdapter
    private lateinit var actionBar: ActionBar
    private lateinit var favoriteCoinRepository: FavoriteCoinRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityFavoriteBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        favoriteCoinRepository = FavoriteCoinRepository()
        favoritePageAdapter= FavoritePageAdapter(ArrayList(), this, favoriteCoinRepository)
        favoriteCoinsRecyclerView = binding.favoriteCoinsRecyclerView
        favoriteCoinsRecyclerView.layoutManager = LinearLayoutManager(this)
        favoriteCoinsRecyclerView.adapter = favoritePageAdapter

        favoriteCoinRepository.setAdapter(favoritePageAdapter)
        favoriteCoinRepository.fetchFavoriteCoinsFromFirestore(this)

        actionBar = supportActionBar!!
        actionBar.hide()

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNav)
        bottomNavigationView.selectedItemId = R.id.favorites

        bottomNavigationView.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.home -> {
                    val intent = Intent(this@FavoriteActivity, MainActivity::class.java)
                    startActivity(intent)
                    return@setOnItemSelectedListener true
                }
                R.id.profile -> {
                    val intent = Intent(this@FavoriteActivity, ProfileActivity::class.java)
                    startActivity(intent)
                    return@setOnItemSelectedListener true
                }
            }
            false
        }
    }

    override fun onItemClick(crypto: CryptoModel) {

    }
}