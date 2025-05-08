package com.example.taskmate;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class CreatingTask extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_creating_task);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Spinner spinnerCategory = findViewById(R.id.spinnerCategory);
        Spinner spinnerRepetition = findViewById(R.id.spinnerReperirion);

        // Создаем адаптер с массивом из ресурсов
        ArrayAdapter<CharSequence> adapterCategory = ArrayAdapter.createFromResource(
                this,
                R.array.categories_array,
                android.R.layout.simple_spinner_item
        );
        ArrayAdapter<CharSequence> adapterRepetition = ArrayAdapter.createFromResource(
                this,
                R.array.repetition_array,
                android.R.layout.simple_spinner_item
        );

        // Указываем layout для выпадающего списка
        adapterCategory.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapterRepetition.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Применяем адаптер
        spinnerCategory.setAdapter(adapterCategory);
        spinnerRepetition.setAdapter(adapterRepetition);
    }

    public void returnToMainMenu(View view) {
        Intent intent = new Intent(CreatingTask.this, MainMenu2.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
    }
}