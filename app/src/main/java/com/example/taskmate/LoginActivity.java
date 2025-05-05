package com.example.taskmate;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    // Элементы интерфейса
    private EditText emailEditText, passwordEditText;
    private Button loginButton;
    private TextView registerTextView;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Инициализация элементов интерфейса
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);
        registerTextView = findViewById(R.id.registerTextView);

        // Инициализация Firebase Authentication
        mAuth = FirebaseAuth.getInstance();

        // Обработчик клика на кнопку "Войти"
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });

        // Обработчик клика на текст "Нет аккаунта? Зарегистрироваться"
        registerTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });
    }

    // Функция для входа пользователя
    private void loginUser() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        // Проверка на пустые поля
        if (TextUtils.isEmpty(email)) {
            emailEditText.setError("Введите email");
            return;
        }
        if (TextUtils.isEmpty(password)) {
            passwordEditText.setError("Введите пароль");
            return;
        }

        // Попытка входа через Firebase Authentication
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Вход успешен
                        Toast.makeText(LoginActivity.this, "Вход выполнен", Toast.LENGTH_SHORT).show();
                        //startActivity(new Intent(LoginActivity.this, Test4.class)); // Переход на главный экран
                        startActivity(new Intent(LoginActivity.this, Test5.class));
                        //finish(); // Закрыть текущую активность
                    } else {
                        // Вход не удался
                        Toast.makeText(LoginActivity.this, "Ошибка входа: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }

    // Проверка, есть ли уже зарегистрированный пользователь
    @Override
    protected void onStart() {
        super.onStart();

        // Если пользователь уже вошел, переходим на главный экран
        if (mAuth.getCurrentUser() != null) {
            //startActivity(new Intent(LoginActivity.this, Test4.class));
            //finish();
        }

    }
}