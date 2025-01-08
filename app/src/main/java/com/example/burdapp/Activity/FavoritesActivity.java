package com.example.burdapp.Activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

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

public class FavoritesActivity extends BaseActivity {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private TextView emptyMessage;
    private ExplorerAdapter adapter;
    private ArrayList<ItemDomain> favoriteItems; // Tip ArrayList olarak düzenlendi
    private SharedPreferences sharedPreferences;
    private ChipNavigationBar chipNavigationBar;
    private BroadcastReceiver receiver;
    private ImageView backBtn; // Geri tuşu

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        // Bileşenleri tanımla
        recyclerView = findViewById(R.id.recyclerViewExplorer);
        progressBar = findViewById(R.id.progressBarExplorer);
        emptyMessage = findViewById(R.id.emptyMessage);
        chipNavigationBar = findViewById(R.id.chipNavigationBar);
        backBtn = findViewById(R.id.backBtn); // Back tuşunu tanımla

        // Intent'ten önceki aktivite bilgisini al
        String previousActivity = getIntent().getStringExtra("previousActivity");

        // Back tuşuna tıklama işlemi
        backBtn.setOnClickListener(v -> handleBackButton(previousActivity));

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

        // ProgressBar'ı 1 saniye göster
        new Handler().postDelayed(() -> {
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
        }, 1000); // 1 saniye sonra yüklenmiş say
    }

    private void showEmptyState() {
        emptyMessage.setVisibility(View.VISIBLE);
        emptyMessage.setText("You don't have any bookmarks yet. Start exploring and add travels to your bookmarks!");
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
            } else if (id == R.id.profile) {
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
    private void handleBackButton(String previousActivity) {
        // Hangi sayfadan gelindiğine göre geri dön
        if (previousActivity != null) {
            try {
                // Dinamik olarak önceki activity'ye dön
                Class<?> previousClass = Class.forName("com.example.burdapp.Activity." + previousActivity);
                Intent intent = new Intent(FavoritesActivity.this, previousClass);
                startActivity(intent);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                // Eğer belirtilen activity bulunamazsa varsayılan ana ekrana dön
                Intent intent = new Intent(FavoritesActivity.this, MainActivity.class);
                startActivity(intent);
            }
        } else {
            // Eğer previousActivity boşsa varsayılan ana ekrana dön
            Intent intent = new Intent(FavoritesActivity.this, MainActivity.class);
            startActivity(intent);
        }
        finish(); // Mevcut aktiviteyi kapat
    }
    private void clearFavorites() {
        sharedPreferences.edit().clear().apply();
    }
}
