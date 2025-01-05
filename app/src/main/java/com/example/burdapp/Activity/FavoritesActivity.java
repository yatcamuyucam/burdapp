package com.example.burdapp.Activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.burdapp.Adapter.ExplorerAdapter;
import com.example.burdapp.Domain.ItemDomain;
import com.example.burdapp.R;
import com.google.gson.Gson;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class FavoritesActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private TextView emptyMessage;
    private ExplorerAdapter adapter;
    private ArrayList<ItemDomain> favoriteItems; // Tip ArrayList olarak düzenlendi
    private SharedPreferences sharedPreferences;
    private ChipNavigationBar chipNavigationBar;
    private BroadcastReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        // Bileşenleri tanımla
        recyclerView = findViewById(R.id.recyclerViewExplorer);
        progressBar = findViewById(R.id.progressBarExplorer);
        emptyMessage = findViewById(R.id.emptyMessage);
        chipNavigationBar = findViewById(R.id.chipNavigationBar);

        // RecyclerView ve Adapter'i ayarla
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        favoriteItems = new ArrayList<>();
        adapter = new ExplorerAdapter(favoriteItems);
        recyclerView.setAdapter(adapter);

        // Favorileri yükle
        loadFavoriteItems();

        // Navigasyon barını ayarla
        setupNavigationBar();

        // BroadcastReceiver tanımla ve kaydet
        setupBroadcastReceiver();
    }

    private void loadFavoriteItems() {
        progressBar.setVisibility(View.VISIBLE);

        // SharedPreferences'tan favori verilerini al
        sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        Set<String> favorites = sharedPreferences.getStringSet("favorites", new HashSet<>());

        // Eğer favoriler boşsa mesaj göster ve işlemi durdur
        if (favorites == null || favorites.isEmpty()) {
            showEmptyState();
            return;
        }

        Gson gson = new Gson();
        favoriteItems.clear();

        for (String itemJson : favorites) {
            try {
                // JSON formatını ItemDomain nesnesine dönüştür
                ItemDomain item = gson.fromJson(itemJson, ItemDomain.class);
                favoriteItems.add(item);
            } catch (Exception e) {
                // JSON hatalarını logla
                e.printStackTrace();
                Log.e("FavoritesActivity", "JSON dönüşüm hatası: " + e.getMessage());
            }
        }

        if (favoriteItems.isEmpty()) {
            showEmptyState();
        } else {
            emptyMessage.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }

        adapter.notifyDataSetChanged();
        progressBar.setVisibility(View.GONE);
    }

    private void showEmptyState() {
        emptyMessage.setVisibility(View.VISIBLE);
        emptyMessage.setText("You don't have any favorites yet. Start exploring and add items to your favorites!");
        recyclerView.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
    }

    private void setupNavigationBar() {
        chipNavigationBar.setItemSelected(R.id.bookmark, true);

        chipNavigationBar.setOnItemSelectedListener(id -> {
            if (id == R.id.home) {
                Intent homeIntent = new Intent(FavoritesActivity.this, MainActivity.class);
                startActivity(homeIntent);
                finish();
            } else if (id == R.id.explorer) {
                Intent explorerIntent = new Intent(FavoritesActivity.this, ExplorerActivity.class);
                startActivity(explorerIntent);
                finish();
            }else if (id == R.id.profile) {
                Intent explorerIntent = new Intent(FavoritesActivity.this, UserActivity.class);
                startActivity(explorerIntent);
                finish();
            }
        });
    }

    private void setupBroadcastReceiver() {
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if ("com.example.UPDATE_FAVORITES".equals(intent.getAction())) {
                    loadFavoriteItems(); // Favorileri yeniden yükle
                }
            }
        };

        // IntentFilter oluştur ve register işlemini yap
        IntentFilter filter = new IntentFilter("com.example.UPDATE_FAVORITES");
        registerReceiver(receiver, filter, Context.RECEIVER_NOT_EXPORTED); // Flag eklenmiş
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (receiver != null) {
            unregisterReceiver(receiver);
        }
    }

    /**
     * Test veya hata ayıklama sırasında kullanılabilecek bir yöntem:
     * Favori verilerini temizler.
     */
    private void clearFavorites() {
        sharedPreferences.edit().clear().apply();
    }
}
