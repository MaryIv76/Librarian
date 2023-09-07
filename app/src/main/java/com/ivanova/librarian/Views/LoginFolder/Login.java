package com.ivanova.librarian.Views.LoginFolder;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.ivanova.librarian.R;
import com.ivanova.librarian.Views.HomePage;

public class Login extends AppCompatActivity implements FragmentNavigation {

    private FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        fAuth = FirebaseAuth.getInstance();

        FirebaseUser currentUser = fAuth.getCurrentUser();
        if (currentUser != null) {
            Intent intent = new Intent(Login.this, HomePage.class);
            startActivity(intent);
        } else {
            getSupportFragmentManager().beginTransaction().add(R.id.login_container, new LoginFragment()).commit();
        }
    }

    @Override
    public void navigateFragments(Fragment fragment, boolean toHomePage) {
        if (toHomePage) {
            Intent intent = new Intent(Login.this, HomePage.class);
            startActivity(intent);
        } else {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction().replace(R.id.login_container, fragment);
            transaction.commit();
        }
    }

    @Override
    public void onBackPressed() {
        finishAffinity();
    }
}
