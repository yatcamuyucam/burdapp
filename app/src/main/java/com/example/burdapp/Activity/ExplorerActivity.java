package com.example.burdapp.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.burdapp.Adapter.ExplorerAdapter;
import com.example.burdapp.Domain.ItemDomain;
import com.example.burdapp.Domain.Location;
import com.example.burdapp.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;

import java.util.ArrayList;

public class ExplorerActivity extends BaseActivity {

    RecyclerView recyclerViewExplorer;
    ArrayList<ItemDomain> itemList;
    ExplorerAdapter explorerAdapter;
    DatabaseReference itemReference, popularReference;
    ProgressBar progressBarExplorer;
    Spinner locationSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explorer);

        // Spinner tanımlaması
        locationSpinner = findViewById(R.id.locationSp);

        initLocation();

        // RecyclerView ve ProgressBar Tanımlamaları
        recyclerViewExplorer = findViewById(R.id.recyclerViewExplorer);
        progressBarExplorer = findViewById(R.id.progressBarExplorer);
        recyclerViewExplorer.setLayoutManager(new LinearLayoutManager(this));

        // Liste ve Adapter Tanımlamaları
        itemList = new ArrayList<>();
        explorerAdapter = new ExplorerAdapter(itemList);
        recyclerViewExplorer.setAdapter(explorerAdapter);

        // Firebase Referansları
        itemReference = FirebaseDatabase.getInstance().getReference("Item");
        popularReference = FirebaseDatabase.getInstance().getReference("Popular");

        // Verileri Yükle
        loadCombinedData();

        // Navigation Bar'ı başlat
        setupNavigationBar();

        // Geri Butonu Tanımlaması
        findViewById(R.id.backBtn).setOnClickListener(v -> finish());
    }

    private void setupNavigationBar() {
        // ChipNavigationBar bileşenini tanımlayın
        ChipNavigationBar chipNavigationBar = findViewById(R.id.chipNavigationBar);

        // Varsayılan olarak 'explorer' butonunu seçili yapmak
        chipNavigationBar.setItemSelected(R.id.explorer, true);

        // Navigation bar için tıklama işlemleri
        chipNavigationBar.setOnItemSelectedListener(id -> {
            if (id == R.id.home) {
                // HomeActivity'yi başlat
                Intent homeIntent = new Intent(ExplorerActivity.this, MainActivity.class);
                homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(homeIntent);
                finish(); // ExplorerActivity'yi kapat
            } else if (id == R.id.explorer) {
                // Zaten Explorer sayfasındayız, bir işlem yapmaya gerek yok
            }
        });
    }

    private void initLocation() {
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("Location");
        ArrayList<Location> list = new ArrayList<>();

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot issue : snapshot.getChildren()) {
                        list.add(issue.getValue(Location.class));
                    }
                }
                ArrayAdapter<Location> adapter = new ArrayAdapter<>(
                        ExplorerActivity.this,
                        android.R.layout.simple_spinner_dropdown_item,
                        list
                );
                locationSpinner.setAdapter(adapter); // Spinner'ı adapter ile bağladık
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ExplorerActivity.this, "Lokasyonlar yüklenemedi.", Toast.LENGTH_SHORT).show();
                Log.e("FirebaseError", "Hata: " + error.getMessage());
            }
        });
    }

    private void loadCombinedData() {
        progressBarExplorer.setVisibility(View.VISIBLE);

        // "Item" Düğümünden Veri Çek
        itemReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    ItemDomain item = dataSnapshot.getValue(ItemDomain.class);
                    if (item != null) {
                        itemList.add(item);
                    }
                }

                // "Popular" Düğümünden Veri Çek
                popularReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            ItemDomain item = dataSnapshot.getValue(ItemDomain.class);
                            if (item != null) {
                                itemList.add(item);
                            }
                        }

                        // Verileri Güncelle
                        explorerAdapter.notifyDataSetChanged();
                        progressBarExplorer.setVisibility(View.GONE);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        progressBarExplorer.setVisibility(View.GONE);
                        Log.e("FirebaseError", "Hata: " + error.getMessage());
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressBarExplorer.setVisibility(View.GONE);
                Log.e("FirebaseError", "Hata: " + error.getMessage());
            }
        });
    }
}
