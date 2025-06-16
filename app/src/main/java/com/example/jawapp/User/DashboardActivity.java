package com.example.jawapp.User;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.jawapp.Entity.User;
import com.example.jawapp.R;
import com.example.jawapp.User.Fragment.HomeFragment;
import com.example.jawapp.User.Fragment.ProfileFragment;
import com.example.jawapp.databinding.ActivityDashboardBinding;

public class DashboardActivity extends AppCompatActivity {

    private ActivityDashboardBinding root;

    public User currentUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        root = ActivityDashboardBinding.inflate(getLayoutInflater());
        setContentView(root.getRoot());


        ReplaceFragment(new HomeFragment());

        currentUser = (User) getIntent().getSerializableExtra("user");

        root.bottomNavigationView.setOnItemSelectedListener(item ->{

            if(item.getItemId() == R.id.userHome)
            {
                ReplaceFragment(new HomeFragment());
            }
            else if (item.getItemId() == R.id.userProfile) {
                ReplaceFragment(new ProfileFragment());
            }


            return true;
        });
    }

    public void ReplaceFragment(Fragment fragment)
    {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.FrameLayout,fragment);
        fragmentTransaction.commit();
    }
}