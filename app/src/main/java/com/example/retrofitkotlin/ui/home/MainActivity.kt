package com.example.retrofitkotlin.ui.home

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.retrofitkotlin.R
import com.example.retrofitkotlin.adapter.HomePageAdapter
import com.example.retrofitkotlin.api.CryptoAPI
import com.example.retrofitkotlin.api.CryptoResponse
import com.example.retrofitkotlin.databinding.ActivityMainBinding
import com.example.retrofitkotlin.model.CryptoModel
import com.example.retrofitkotlin.ui.detail.DetailsFragment
import com.example.retrofitkotlin.ui.favorite.FavoriteActivity
import com.example.retrofitkotlin.ui.profile.ProfileActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity(), HomePageAdapter.OnItemClickListener {

    private lateinit var binding: ActivityMainBinding
    private val BASE_URL = "https://openapiv1.coinstats.app"
    private val API_KEY = "EgIIWiVFikbvh3qs0Zurhi+/L0txRgbmxharYC5Yts0="
    private var cryptoModel: List<CryptoModel>? = null
    private var recyclerViewAdapter: HomePageAdapter? = null
    private var compositeDisposable: CompositeDisposable? = null
    private lateinit var actionBar: ActionBar
    private lateinit var fireBaseAuth: FirebaseAuth
    private val REFRESH_DELAY: Long = 20000
    private val handler: Handler = Handler(Looper.getMainLooper())
    private lateinit var refreshRunnable: Runnable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        actionBar = supportActionBar!!
        actionBar.hide()

        fireBaseAuth = FirebaseAuth.getInstance()

        compositeDisposable = CompositeDisposable()

        recyclerViewAdapter = HomePageAdapter(ArrayList(), this)
        binding.recyclerView.adapter = recyclerViewAdapter

        binding.recyclerView.layoutManager = LinearLayoutManager(this)

        startAutoRefresh()

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNav)

        bottomNavigationView.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.favorites -> {
                    val intent = Intent(this@MainActivity, FavoriteActivity::class.java)
                    startActivity(intent)
                    return@setOnItemSelectedListener true
                }
                R.id.profile -> {
                        val intent = Intent(this@MainActivity, ProfileActivity::class.java)
                        startActivity(intent)
                        return@setOnItemSelectedListener true
                }
            }
            false
        }
    }

    override fun onStop() {
        super.onStop()
        stopAutoRefresh()
    }

    private fun loadData() {
        cryptoModel = ArrayList()

        val okHttpClient = OkHttpClient
            .Builder()
            .addInterceptor (Interceptor {chain ->
                val original = chain.request()
                val requestBuilder = original.newBuilder()
                    .header("X-API-KEY", API_KEY)

                val request = requestBuilder.build()
                chain.proceed(request)
            } )
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
            .create(CryptoAPI::class.java)

        compositeDisposable?.add(
            retrofit.getAllData(API_KEY,100, "USD")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    this::handleResponse,
                    this::handleError
                )
        )
    }

    private fun handleResponse(cryptoResponse: CryptoResponse) {
        val cryptoList = ArrayList(cryptoResponse.result)
        recyclerViewAdapter?.setData(cryptoList)
    }

    override fun onItemClick(crypto: CryptoModel) {
        Toast.makeText(this,"Clicked : ${crypto.name}",Toast.LENGTH_LONG).show()
        supportFragmentManager
            .beginTransaction()
            .add(R.id.mainContainer, DetailsFragment.newInstance(crypto))
            .addToBackStack(null)
            .commit()
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable?.clear()
    }

    private fun handleError(error: Throwable) {
        Toast.makeText(this, "Error: ${error.message}", Toast.LENGTH_LONG).show()
    }

    private fun startAutoRefresh() {
        refreshRunnable = object: Runnable {
            override fun run() {
                loadData()
                handler.postDelayed(this, REFRESH_DELAY)
            }
        }
        handler.post(refreshRunnable)
    }

    private fun stopAutoRefresh() {
        handler.removeCallbacks(refreshRunnable)
    }
}