package com.example.taskmate;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class MainMenuActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main_menu);
        setContentView(R.layout.test2);
        /*
        // Инициализация Firebase Authentication
        mAuth = FirebaseAuth.getInstance();

        // Находим элементы интерфейса
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);

        // Настройка Toolbar
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Добавляем ActionBarDrawerToggle для кнопки меню
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.open_drawer, R.string.close_drawer);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Обработка нажатий на пункты меню
        navigationView.setNavigationItemSelectedListener(item -> {
            drawerLayout.closeDrawer(GravityCompat.START); // Закрываем меню
            return true;
        });

         */
    }

    // Функция выхода из аккаунта
    public void logoutUser(MenuItem item) {
        // Выход через Firebase Authentication
        mAuth.signOut();

        // Показываем сообщение об успешном выходе
        Toast.makeText(this, "Вы вышли из аккаунта", Toast.LENGTH_SHORT).show();

        // Переход на экран входа
        Intent intent = new Intent(MainMenuActivity.this, LoginActivity.class);
        startActivity(intent);

        // Закрываем текущую активность
        finish();
    }

    // Закрытие меню при нажатии кнопки "Назад"
    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    /*
    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mAuth.getCurrentUser() != null) {
            mAuth.signOut();
        }
    }*/

}