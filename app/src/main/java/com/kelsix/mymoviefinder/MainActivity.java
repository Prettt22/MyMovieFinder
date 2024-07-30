package com.kelsix.mymoviefinder;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowInsetsController;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav);
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        assert navHostFragment != null;
        NavController navController = navHostFragment.getNavController();

        NavigationUI.setupWithNavController(bottomNav, navController);

        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            if (destination.getId() == R.id.movieDetailFragment || destination.getId() == R.id.tvDetailFragment || destination.getId() == R.id.actorDetailFragment || destination.getId() == R.id.searchFragment) {
                bottomNav.setVisibility(View.GONE);
            } else {
                bottomNav.setVisibility(View.VISIBLE);
            }
        });

        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.menu_home) {
                navController.navigate(R.id.homeFragment);
                return true;
            } else if (itemId == R.id.menu_browse) {
                navController.navigate(R.id.browseFragment);
                return true;
            } else if (itemId == R.id.menu_wishlist) {
                navController.navigate(R.id.wishlistFragment);
                return true;
            } else if (itemId == R.id.menu_favorite) {
                navController.navigate(R.id.favoriteFragment);
                return true;
            } else {
                return false;
            }
        });
    }
}
