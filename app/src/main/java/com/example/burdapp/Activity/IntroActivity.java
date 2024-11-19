package com.example.burdapp.Activity;

import android.os.Bundle;

import android.content.Intent;
import com.example.burdapp.databinding.ActivityIntroBinding;

public class IntroActivity extends BaseActivity {
    ActivityIntroBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // View Binding kullanılarak layout bağlanıyor
        binding = ActivityIntroBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // introBtn'e tıklama olayı tanımlanıyor
        binding.introBtn.setOnClickListener(v -> startActivity(new Intent(IntroActivity.this, MainActivity.class))
        );
    }
}
