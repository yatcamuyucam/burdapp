package com.example.burdapp.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.burdapp.Adapter.JoinAdapter;
import com.example.burdapp.Domain.ItemDomain;
import com.example.burdapp.Domain.User;
import com.example.burdapp.R;
import com.example.burdapp.databinding.ActivityUserBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class UserActivity extends BaseActivity {
    private ActivityUserBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();

        if (currentUser != null) {
            loadUserInfo(currentUser);
            loadUserProfileData(currentUser);
            setupNavigationBar();
        } else {
            Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show();
        }

        // Back button tıklama işlemi
        binding.backBtn.setOnClickListener(v -> handleBackButton());
    }

    private void loadUserInfo(FirebaseUser user) {
        DatabaseReference userReference = FirebaseDatabase.getInstance().getReference("users").child(user.getUid());

        userReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    User userData = snapshot.getValue(User.class);
                    if (userData != null) {
                        String fullName = userData.getName();
                        String gender = userData.getGender();

                        binding.emailTxt.setText(user.getEmail());
                        binding.nameTxt.setText(fullName);

                        // Kullanıcıya özel benzersiz avatar URL'si oluşturma ve Firebase'de saklama
                        String avatarUrl = generateAvatarUrl(userData);

                        // Glide ile avatar yükleme
                        Glide.with(binding.getRoot().getContext())
                                .load(avatarUrl)
                                .placeholder(R.drawable.placeholder_avatar)
                                .diskCacheStrategy(DiskCacheStrategy.ALL) // Önbellek stratejisini etkinleştir
                                .into(binding.profileImage);
                    } else {
                        Log.e("FirebaseError", "User data is null");
                    }
                } else {
                    Log.e("FirebaseError", "User snapshot does not exist");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FirebaseError", "Error: " + error.getMessage());
            }
        });
    }

    private String generateAvatarUrl(User userData) {
        String gender = userData.getGender();
        String baseUrl = "https://avatar.iran.liara.run/public/";

        // Erkek veya kadın avatarı için doğru URL oluşturma
        String uniqueId = "?uid=" + userData.getName().hashCode(); // Benzersiz parametre

        if ("male".equalsIgnoreCase(gender)) {
            return baseUrl + "boy" + uniqueId;
        } else {
            return baseUrl + "girl" + uniqueId;
        }
    }

    private void loadUserProfileData(FirebaseUser user) {
        DatabaseReference userTicketsReference = FirebaseDatabase.getInstance().getReference("userTickets").child(user.getUid());
        ArrayList<ItemDomain> list = new ArrayList<>();

        userTicketsReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot issue : snapshot.getChildren()) {
                        list.add(issue.getValue(ItemDomain.class));
                    }
                    if (!list.isEmpty()) {
                        binding.recyclerViewRecommended.setLayoutManager(new LinearLayoutManager(UserActivity.this, LinearLayoutManager.VERTICAL, false));
                        RecyclerView.Adapter<JoinAdapter.Viewholder> adapter = new JoinAdapter(list);
                        binding.recyclerViewRecommended.setAdapter(adapter);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FirebaseError", "Error loading tickets: " + error.getMessage());
            }
        });
    }

    private void setupNavigationBar() {
        ChipNavigationBar chipNavigationBar = findViewById(R.id.chipNavigationBar);
        chipNavigationBar.setItemSelected(R.id.profile, true);

        chipNavigationBar.setOnItemSelectedListener(id -> {
            if (id == R.id.home) {
                navigateToActivity(MainActivity.class);
            } else if (id == R.id.explorer) {
                navigateToActivity(ExplorerActivity.class);
            } else if (id == R.id.bookmark) {
                navigateToActivity(FavoritesActivity.class);
            }
        });
    }

    private void navigateToActivity(Class<?> targetActivity) {
        Intent intent = new Intent(UserActivity.this, targetActivity);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void handleBackButton() {
        Intent intent = new Intent(UserActivity.this, MainActivity.class); // Ana ekrana dön
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    public void onImageClick(View view) {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(UserActivity.this, SignupActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}
