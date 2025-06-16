package com.example.jawapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.jawapp.User.LoginActivity;
import com.example.jawapp.databinding.ActivityMainBinding;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding root;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        root = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(root.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        new Handler().postDelayed(()->{
            startActivity(new Intent(this, LoginActivity.class));
            finish();

        },4000);

        root.txt1.setTranslationX(1000);
        root.txt2.setTranslationX(1000);
        root.txt3.setTranslationX(1000);

        long animationDuration = 200L;

        root.txt1.animate()
                .translationXBy(-1000)
                .setDuration(animationDuration)
                .setStartDelay(250)
                .withEndAction(() -> root.txt2.animate()
                        .translationXBy(-1000)
                        .setDuration(animationDuration)
                        .translationXBy(-1000)
                        .setDuration(animationDuration)
                        .withEndAction(() -> root.txt3.animate()
                                .translationXBy(-1000)
                                .setDuration(animationDuration)
                                .setStartDelay(250)));
    }


}