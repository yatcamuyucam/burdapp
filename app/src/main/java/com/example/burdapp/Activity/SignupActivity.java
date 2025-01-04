package com.example.burdapp.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import com.example.burdapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignupActivity extends BaseActivity {

    // Kullanılacak bileşenlerin tanımları
    EditText signupName, signupEmail, signupPassword;
    TextView loginRedirectedText;
    Button signupButton;
    FirebaseAuth auth;  // Firebase Authentication nesnesi
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // UI elemanları ile bağlantı
        signupName = findViewById(R.id.signup_name);
        signupEmail = findViewById(R.id.signup_email);
        signupPassword = findViewById(R.id.signup_password);
        signupButton = findViewById(R.id.signup_button);
        loginRedirectedText = findViewById(R.id.loginRedirectText);

        // Firebase Authentication başlatma
        auth = FirebaseAuth.getInstance();

        // Sign-up butonuna tıklama işlemi
        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Kullanıcı girişlerinin alınması
                String name = signupName.getText().toString().trim();
                String email = signupEmail.getText().toString().trim();
                String password = signupPassword.getText().toString().trim();

                // Alanların boş olup olmadığını kontrol edin
                if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(SignupActivity.this, "Please fill all the fields!", Toast.LENGTH_SHORT).show();
                    return; // Eğer alanlar boşsa işlemi durdur
                }

                // Firebase Authentication ile kullanıcı oluşturma
                auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Kullanıcı Firebase Authentication'a başarıyla kaydedildi
                        FirebaseUser firebaseUser = auth.getCurrentUser();

                        if (firebaseUser != null) {
                            // Kullanıcının adı gibi bilgileri Firebase Realtime Database'e kaydet
                            FirebaseDatabase database = FirebaseDatabase.getInstance();
                            reference = database.getReference("users");
                            String userId = firebaseUser.getUid();  // Kullanıcının benzersiz ID'si

                            // Kullanıcı bilgilerini kaydetmek için HelperClass kullanımı
                            HelperClass helperClass = new HelperClass(name, email, password);
                            reference.child(userId).setValue(helperClass).addOnCompleteListener(dbTask -> {
                                if (dbTask.isSuccessful()) {
                                    Toast.makeText(SignupActivity.this, "You have signed up successfully!", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                                    startActivity(intent);
                                } else {
                                    Toast.makeText(SignupActivity.this, "Sign up failed in database!", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    } else {
                        // Kullanıcı oluşturma başarısızsa
                        Toast.makeText(SignupActivity.this, "Sign up failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        Log.e("SignupError", "Error: ", task.getException());
                    }
                });
            }
        });

        // Login sayfasına yönlendirme
        loginRedirectedText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }
}
