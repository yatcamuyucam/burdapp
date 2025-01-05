package com.example.burdapp.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.burdapp.Domain.ItemDomain;
import com.example.burdapp.R;
import com.example.burdapp.databinding.ActivityDetailBinding;

import java.util.HashSet;
import java.util.Set;

public class DetailActivity extends BaseActivity {
    ActivityDetailBinding binding;
    private ItemDomain object;

    // Favori durumu için SharedPreferences
    private SharedPreferences sharedPreferences;
    private boolean isFavorited = false; // Varsayılan favori durumu

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // SharedPreferences başlatma
        sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);

        // Favori durumunu SharedPreferences'tan kontrol et
        getIntentExtra();
        checkFavoriteStatus(); // Favori durumunu kontrol et ve güncelle

        // Değişkenleri ayarla
        setVariable();

        // Favori simgesine tıklama dinleyicisi ekleme
        binding.favBtn.setOnClickListener(v -> toggleFavorite());
    }

    /**
     * Favori durumunu kontrol et ve simgeyi güncelle
     */
    private void checkFavoriteStatus() {
        // SharedPreferences'tan favoriler listesini al
        Set<String> favorites = sharedPreferences.getStringSet("favorites", new HashSet<>());

        // Favorilerde olup olmadığını kontrol et
        isFavorited = favorites.contains(object.getTitle());

        // Favori ikonunu güncelle
        updateFavIcon();
    }

    private void setVariable() {
        binding.titleTxt.setText(object.getTitle());
        binding.priceTxt.setText("$" + object.getPrice());
        binding.backBtn.setOnClickListener(v -> finish());
        binding.bedTxt.setText("" + object.getBed());
        binding.durationTxt.setText(object.getDuration());
        binding.distanceTxt.setText(object.getDistance());
        binding.descriptionTxt.setText(object.getDescription());
        binding.addressTxt.setText(object.getAddress());
        binding.ratingTxt.setText(object.getScore() + " Rating");
        binding.ratingBar.setRating((float) object.getScore());

        Glide.with(DetailActivity.this)
                .load(object.getPic())
                .into(binding.pic);

        binding.addToCardBtn.setOnClickListener(v -> {
            Intent intent = new Intent(DetailActivity.this, TicketActivity.class);
            intent.putExtra("object", object);
            startActivity(intent);
        });
    }

    private void getIntentExtra() {
        object = (ItemDomain) getIntent().getSerializableExtra("object");
    }

    /**
     * Favori durumunu değiştirme ve kaydetme
     */
    private void toggleFavorite() {
        isFavorited = !isFavorited;

        // Favori durumunu SharedPreferences'ta güncelle
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Set<String> favorites = sharedPreferences.getStringSet("favorites", new HashSet<>());

        if (isFavorited) {
            // İçeriği favorilere ekle
            favorites.add(object.getTitle());
        } else {
            // İçeriği favorilerden çıkar
            favorites.remove(object.getTitle());
        }

        // Güncellenen favorileri kaydet
        editor.putStringSet("favorites", favorites);
        editor.apply();

        // Simgeyi güncelle
        updateFavIcon();

        // Kullanıcıya bildirim göster
        String message = isFavorited ? "Added to favorites" : "Removed from favorites";
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    /**
     * Favori simgesini güncelle
     */
    private void updateFavIcon() {
        if (isFavorited) {
            binding.favBtn.setImageResource(R.drawable.red_fav_icon); // Dolmuş favori simgesi
        } else {
            binding.favBtn.setImageResource(R.drawable.fav_icon); // Boş favori simgesi
        }
    }
}
