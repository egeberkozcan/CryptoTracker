package com.example.retrofitkotlin.ui.detail

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.retrofitkotlin.R
import com.example.retrofitkotlin.databinding.FragmentDetailsBinding
import com.example.retrofitkotlin.model.CryptoModel
import com.example.retrofitkotlin.repository.FavoriteCoinRepository
import java.text.NumberFormat
import java.util.Locale


class DetailsFragment : Fragment() {

    private lateinit var binding: FragmentDetailsBinding
    private lateinit var cryptoModel: CryptoModel
    private var favoriteButton: ImageView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            cryptoModel = it.getParcelable(ARG_CRYPTO)!!
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentDetailsBinding.inflate(inflater, container, false)
        favoriteButton = binding.favoriteButton
        setupFavoriteButton(requireContext())

        binding.twitterButton
            .setOnClickListener{
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(cryptoModel.twitterUrl)
            startActivity(intent)
        }
        binding.websiteButton
            .setOnClickListener{
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(cryptoModel.websiteUrl)
            startActivity(intent)
        }

        val view = binding.root
        val marketCapFormatted = notShowLastTwoDigit(cryptoModel.marketCap)
        val priceFormatted = showLastTwoDigit(cryptoModel.price)
        val volumeFormatted = notShowLastTwoDigit(cryptoModel.volume)
        val availableSupplyFormatted = notShowLastTwoDigit((cryptoModel.availableSupply))
        val totalSupplyFormatted = notShowLastTwoDigit(cryptoModel.totalSupply)

        Glide.with(this)
            .load(cryptoModel.icon)
            .centerInside()
            .into(binding.detailImageIcon)

        binding.detailSymbol.text = cryptoModel.symbol.substring(0,3)
        binding.detailPrice.text = priceFormatted
        binding.detailOneDayChange.text = "${String.format(cryptoModel.priceChange1d.toString())}%"
        binding.detailRank.text = cryptoModel.rank.toString()
        binding.detailMarketCap.text = marketCapFormatted
        binding.detailVolume.text = volumeFormatted
        binding.detailAvailableSupply.text = availableSupplyFormatted
        binding.detailTotalSupply.text = totalSupplyFormatted

        val arrowUp: ImageView = binding.arrowUp
        val arrowDown: ImageView = binding.arrowDown

        if (cryptoModel.priceChange1d > 0 ){
            binding.detailOneDayChange.setTextColor(Color.GREEN)
            arrowUp.visibility = View.VISIBLE
            arrowDown.visibility = View.GONE
        } else {
            binding.detailOneDayChange.setTextColor(Color.RED)
            arrowUp.visibility = View.VISIBLE
            arrowUp.visibility = View.GONE
        }
        return view
    }

    private fun storeCoinFavoriteStatus(coinKey: String, isFavorited: Boolean) {
        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE) ?: return
        with(sharedPref.edit()) {
            putBoolean(coinKey, isFavorited)
            apply()
        }
    }

    private fun isCoinFavorited(coinKey: String): Boolean{
        val sharedPrefs = activity?.getPreferences(Context.MODE_PRIVATE) ?: return false
        return sharedPrefs.getBoolean(coinKey,false)
    }

    private fun setupFavoriteButton(context: Context){
        val coinKey = "coin_favorited_${cryptoModel.id}"
        val isFavorited = isCoinFavorited(coinKey)
        favoriteButton?.isSelected = isFavorited

        if (isFavorited) {
            favoriteButton?.setImageResource(R.drawable.baseline_favorite_24)
        } else {
            favoriteButton?.setImageResource(R.drawable.baseline_favorite_border_24)
        }
        favoriteButton?.setOnClickListener {
            val updatedFavoriteStatus = !(favoriteButton?.isSelected ?: false)
            if (updatedFavoriteStatus) {
                favoriteButton?.setImageResource(R.drawable.baseline_favorite_24)
                Toast.makeText(context, "${cryptoModel.name} succesfully added to favorites", Toast.LENGTH_SHORT).show()
            } else {
                favoriteButton?.setImageResource(R.drawable.baseline_favorite_border_24)
            }
            favoriteButton?.isSelected = updatedFavoriteStatus
            storeCoinFavoriteStatus(coinKey, updatedFavoriteStatus)

            if (updatedFavoriteStatus) {
                val favoriteCoinRepository = FavoriteCoinRepository()
                favoriteCoinRepository.createFavoriteCoinDocument(cryptoModel, requireContext())
            }
        }
    }

    private fun showLastTwoDigit(priceTag: Double): String {
        val format = NumberFormat
            .getCurrencyInstance(Locale.US)
        format.maximumFractionDigits = 2
        return format.format(priceTag)
    }
    private fun notShowLastTwoDigit(marketCap: Double): String {
        val format = NumberFormat
            .getCurrencyInstance(Locale.US)
        format.maximumFractionDigits = 0
        return format.format(marketCap)
    }

    companion object {

        private const val ARG_CRYPTO = "selected_crypto"

        fun newInstance(cryptoModel: CryptoModel): DetailsFragment {
            val fragment = DetailsFragment()
            val args = Bundle()
            args.putParcelable(ARG_CRYPTO, cryptoModel)
            fragment.arguments = args
            return fragment
        }
    }
}