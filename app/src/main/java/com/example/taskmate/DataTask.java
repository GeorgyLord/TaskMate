package com.example.taskmate;

import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class DataTask extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_data_task);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        TextView t = findViewById(R.id.detail_title);
        // Получаем данные из Intent
        String title = getIntent().getStringExtra("task_title");
        String description = getIntent().getStringExtra("task_desc");
        String date = getIntent().getStringExtra("task_date");

        // Настраиваем View
        TextView titleView = findViewById(R.id.detail_title);
        TextView descView = findViewById(R.id.detail_description);
        TextView dateView = findViewById(R.id.detail_date);

        titleView.setText(title);
        descView.setText(description);
        dateView.setText(date);
    }
}