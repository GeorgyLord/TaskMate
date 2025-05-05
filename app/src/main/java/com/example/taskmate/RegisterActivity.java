package com.example.taskmate;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    // Элементы интерфейса
    private EditText emailEditText, passwordEditText;
    private Button registerButton;
    private TextView loginTextView;
    private FirebaseAuth mAuth;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DocumentReference newDocRef;
    private String Path = "users";
    private String gemail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Инициализация элементов интерфейса
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        registerButton = findViewById(R.id.registerButton);
        loginTextView = findViewById(R.id.loginTextView);

        // Инициализация Firebase Authentication
        mAuth = FirebaseAuth.getInstance();

        // Обработчик клика на кнопку "Зарегистрироваться"
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });

        // Обработчик клика на текст "Уже есть аккаунт? Войти"
        loginTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                finish(); // Закрыть текущую активность
            }
        });
    }

    // Функция для регистрации пользователя
    private void registerUser() {
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
        if (password.length() < 8) {
            passwordEditText.setError("Пароль должен содержать минимум 8 символов");
            return;
        }

        // Регистрация пользователя через Firebase Authentication
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Регистрация успешна
                        Calendar calendar = Calendar.getInstance();
                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.getDefault());
                        String currentDate = dateFormat.format(calendar.getTime());
                        ArrayList<String> id_of_tasks = new ArrayList<>();

                        Map<String, Object> data = new HashMap<>();
                        data.put("email", email);
                        data.put("password", password);
                        data.put("date_of_registration", currentDate);
                        data.put("id_of_tasks", id_of_tasks);

                        newDocRef = db.collection(Path).document(email);
                        //data.put("id_user", newDocRef.getId());
                        data.put("id_user", mAuth.getUid());
                        gemail = email;
                        newDocRef.set(data);

                        // Сохраняем строку
                        SharedPreferences sharedPref = getSharedPreferences("data", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putString("id", String.valueOf(newDocRef.getId()));
                        editor.apply(); // или editor.commit();

                        Toast.makeText(RegisterActivity.this, "Регистрация выполнена", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(RegisterActivity.this, LoginActivity.class)); // Переход на экран входа
                        finish(); // Закрыть текущую активность
                    } else {
                        // Регистрация не удалась
                        Toast.makeText(RegisterActivity.this, "Ошибка регистрации: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
    public String get_email(){
        return gemail;
    }
    public DocumentReference get_id(){
        return newDocRef;
    }
}