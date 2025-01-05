package com.example.burdapp.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.burdapp.Adapter.ItemAdapter;
import com.example.burdapp.Domain.ItemDomain;
import com.example.burdapp.R;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FavoritesActivity extends BaseActivity {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private TextView emptyMessage;
    private ItemAdapter adapter;
    private List<ItemDomain> favoriteItems;
    private SharedPreferences sharedPreferences;
    private ChipNavigationBar chipNavigationBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        // Bileşenleri tanımla
        recyclerView = findViewById(R.id.recyclerViewExplorer);
        progressBar = findViewById(R.id.progressBarExplorer);
        emptyMessage = findViewById(R.id.emptyMessage);
        chipNavigationBar = findViewById(R.id.chipNavigationBar);

        // RecyclerView için düzenleyici ayarla
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Favori içerikleri yükle
        loadFavoriteItems();

        // ChipNavigationBar'ı ayarla
        setupNavigationBar();
    }

    private void loadFavoriteItems() {
        // ProgressBar'ı göster
        progressBar.setVisibility(View.VISIBLE);

        // SharedPreferences'tan favorileri yükle
        sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        Set<String> favorites = sharedPreferences.getStringSet("favorites", new HashSet<>());

        // Favori içerikleri listeye dönüştür
        favoriteItems = getFavoriteItems(favorites);

        // Favori içerik yoksa boş mesajı göster, RecyclerView'i gizle
        if (favoriteItems.isEmpty()) {
            progressBar.setVisibility(View.GONE);
            emptyMessage.setVisibility(View.VISIBLE); // Boş mesajı göster
            recyclerView.setVisibility(View.GONE); // RecyclerView'i gizle
            return;
        }

        // Adapter'i RecyclerView'e bağla
        adapter = new ItemAdapter(favoriteItems);
        recyclerView.setAdapter(adapter);

        // Mesajı gizle ve RecyclerView'i göster
        emptyMessage.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);

        // ProgressBar'ı gizle
        progressBar.setVisibility(View.GONE);
    }

    private List<ItemDomain> getFavoriteItems(Set<String> favorites) {
        List<ItemDomain> items = new ArrayList<>();

        for (String title : favorites) {
            ItemDomain item = new ItemDomain();
            item.setTitle(title); // Başlığı ayarla
            item.setDescription("Description for " + title); // Örnek açıklama
            item.setPic("image_url"); // Örnek resim URL'si
            items.add(item);
        }

        return items;
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
            } else if (id == R.id.bookmark) {
                // Zaten FavoritesActivity'deyiz
            }
        });
    }
}
