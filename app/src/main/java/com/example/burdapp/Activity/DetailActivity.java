package com.example.burdapp.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.burdapp.Domain.ItemDomain;
import com.example.burdapp.R;
import com.example.burdapp.databinding.ActivityDetailBinding;
import com.google.gson.Gson;

import java.util.HashSet;
import java.util.Set;

public class DetailActivity extends BaseActivity {
    private ActivityDetailBinding binding;
    private ItemDomain object;

    private SharedPreferences sharedPreferences;
    private boolean isFavorited = false;
    private Gson gson = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // SharedPreferences başlatma
        sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);

        // Intent'ten gelen veriyi al ve favori durumunu kontrol et
        getIntentExtra();
        checkFavoriteStatus();

        // Görsel ve işlevsel öğeleri ayarla
        setVariable();

        // Favori butonuna tıklama işlemi
        binding.favBtn.setOnClickListener(v -> toggleFavorite());
    }

    private void getIntentExtra() {
        object = (ItemDomain) getIntent().getSerializableExtra("object");
    }

    private void setVariable() {
        binding.titleTxt.setText(object.getTitle());
        binding.priceTxt.setText("$" + object.getPrice());
        binding.bedTxt.setText(String.valueOf(object.getBed()));
        binding.durationTxt.setText(object.getDuration());
        binding.distanceTxt.setText(object.getDistance());
        binding.descriptionTxt.setText(object.getDescription());
        binding.addressTxt.setText(object.getAddress());
        binding.ratingTxt.setText(object.getScore() + " Rating");
        binding.ratingBar.setRating((float) object.getScore());

        Glide.with(this).load(object.getPic()).into(binding.pic);

        binding.backBtn.setOnClickListener(v -> finish());
        binding.addToCardBtn.setOnClickListener(v -> {
            Intent intent = new Intent(DetailActivity.this, TicketActivity.class);
            intent.putExtra("object", object);
            startActivity(intent);
        });
    }

    /**
     * Favori durumunu kontrol eder ve simgeyi günceller.
     */
    private void checkFavoriteStatus() {
        Set<String> favorites = sharedPreferences.getStringSet("favorites", new HashSet<>());
        if (favorites != null) {
            for (String itemJson : favorites) {
                try {
                    ItemDomain item = gson.fromJson(itemJson, ItemDomain.class);
                    if (item.getTitle().equals(object.getTitle())) {
                        isFavorited = true;
                        break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        updateFavIcon();
    }

    /**
     * Favori durumunu değiştirir ve SharedPreferences'a kaydeder.
     */
    private void toggleFavorite() {
        isFavorited = !isFavorited;
        Set<String> favorites = new HashSet<>(sharedPreferences.getStringSet("favorites", new HashSet<>()));
        SharedPreferences.Editor editor = sharedPreferences.edit();

        if (isFavorited) {
            String itemJson = gson.toJson(object);
            favorites.add(itemJson);
        } else {
            String toRemove = null;
            for (String itemJson : favorites) {
                try {
                    ItemDomain item = gson.fromJson(itemJson, ItemDomain.class);
                    if (item.getTitle().equals(object.getTitle())) {
                        toRemove = itemJson;
                        break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (toRemove != null) {
                favorites.remove(toRemove);
            }
        }

        Intent intent = new Intent("com.example.UPDATE_FAVORITES");
        sendBroadcast(intent);

        editor.putStringSet("favorites", favorites);
        editor.apply();

        updateFavIcon();

        String message = isFavorited ? "Added to favorites" : "Removed from favorites";
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    /**
     * Favori simgesini günceller.
     */
    private void updateFavIcon() {
        int icon = isFavorited ? R.drawable.red_fav_icon : R.drawable.fav_icon;
        binding.favBtn.setImageResource(icon);
    }
}
