package com.ivanova.librarian.Views;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.ivanova.librarian.R;

public class UserAccount extends AppCompatActivity {

    private BottomNavigationView menu;
    private Button logoutBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_account);

        initLogOutBtn();
        initMenu();
    }

    private void initLogOutBtn() {
        logoutBtn = findViewById(R.id.btn_logout);
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(UserAccount.this, Login.class);
                startActivity(intent);
            }
        });
    }

    private void initMenu() {
        menu = findViewById(R.id.bottomNavigation);
        menu.getMenu().getItem(4).setChecked(true);
        menu.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.homeItem: {
                        Intent intent = new Intent(UserAccount.this, HomePage.class);
                        startActivity(intent);
                        return true;
                    }
                    case R.id.searchItem: {
                        return true;
                    }
                    case R.id.bookItem: {
                        Intent intent = new Intent(UserAccount.this, Library.class);
                        startActivity(intent);
                        return true;
                    }
                    case R.id.heartItem: {
                        Intent intent = new Intent(UserAccount.this, FavouriteBooks.class);
                        startActivity(intent);
                        return true;
                    }
                    case R.id.userItem: {
                        return true;
                    }
                }
                return true;
            }
        });
    }
}
