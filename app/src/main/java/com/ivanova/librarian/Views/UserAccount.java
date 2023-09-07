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
import com.ivanova.librarian.ViewModels.CommonFeaturesFolder.CommonFeatures;
import com.ivanova.librarian.Views.LoginFolder.Login;

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
        CommonFeatures.initMenu(this, menu, new boolean[]{false, false, false, false, true}, false);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        menu.getMenu().getItem(4).setChecked(true);
    }

    @Override
    public void onBackPressed() {
        finishAffinity();
    }
}
