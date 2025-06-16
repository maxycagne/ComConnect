package com.example.jawapp.User;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.jawapp.Entity.User;
import com.example.jawapp.R;
import com.example.jawapp.databinding.ActivityLoginBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding root;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        root =ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(root.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        root.btnLogin.setOnClickListener(view -> {
           login();
        });

        root.btnSignup.setOnClickListener(view -> {
            startActivity(new Intent(this,SignUpActivity.class));
        });
    }
    public void login()
    {
        String username = root.edtUserName.getText().toString();
        String password = root.edtPassword.getText().toString();

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference loginRef = firebaseDatabase.getReference("users");
        Query queryUserName = loginRef.orderByChild("username").equalTo(username);


        queryUserName.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists())
                {
                    for (DataSnapshot userSnapshot : snapshot.getChildren())
                    {
                        String dbPass = userSnapshot.child("password").getValue(String.class);
                        if(dbPass != null && dbPass.equals(password))
                        {
                            Toast.makeText(LoginActivity.this, "Login Successfully. \nWelcome " + userSnapshot.child("firstName").getValue(String.class), Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(LoginActivity.this,DashboardActivity.class)
                                    .putExtra("user", userSnapshot.getValue(User.class))
                            );
                            finish();
                        }
                        else
                        {
                            Toast.makeText(LoginActivity.this, "Password is incorrect!", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                else
                {
                    Toast.makeText(LoginActivity.this, "Username is incorrect!", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}