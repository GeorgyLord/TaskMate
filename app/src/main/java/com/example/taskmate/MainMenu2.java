package com.example.taskmate;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;

public class MainMenu2 extends AppCompatActivity {
    public TextView title;
    public FrameLayout main_content;
    public FrameLayout calendar_content;
    public FrameLayout my_content;
    public Button buttonOpenMain;
    public Button buttonOpenCalendar;
    public Button buttonOpenMy;
    public ImageView imageViewSearch;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main_menu2);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int screenWidth = displayMetrics.widthPixels; // Ширина экрана в пикселях
        int screenHeight = displayMetrics.heightPixels; // Высота экрана

        imageViewSearch = findViewById(R.id.imageViewSearch);
        EditText lineSearch = findViewById(R.id.lineSearch);
        title = findViewById(R.id.screenName);
        //Button buttonApplySearch = findViewById(R.id.applySearch);
        LinearLayout header = findViewById(R.id.block1);
        DrawerLayout main_window = findViewById(R.id.main);

        main_content = findViewById(R.id.main_content);
        calendar_content = findViewById(R.id.calendar_content);
        my_content = findViewById(R.id.my_content);

        buttonOpenMain = findViewById(R.id.buttonOpenMain);
        buttonOpenCalendar = findViewById(R.id.buttonOpenCalendar);
        buttonOpenMy = findViewById(R.id.buttonOpenMy);

        imageViewSearch.setOnClickListener(v -> {
            title.setVisibility(View.GONE);
            imageViewSearch.setVisibility(View.GONE);
            lineSearch.setVisibility(View.VISIBLE);
            //buttonApplySearch.setVisibility(View.VISIBLE);
            lineSearch.requestFocus();
            // Показать клавиатуру
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(lineSearch, InputMethodManager.SHOW_IMPLICIT);
            lineSearch.requestFocus();
        });
        // Обработчик нажатий на корневой layout
        main_window.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                // Проверяем, было ли нажатие вне LinearLayout
                if (header.getVisibility() == View.VISIBLE && !isTouchInsideView(event, header)) {

                    // Вызываем функцию (например, скрываем EditText и клавиатуру)
                    title.setVisibility(View.VISIBLE);
                    imageViewSearch.setVisibility(View.VISIBLE);
                    lineSearch.setVisibility(View.GONE);
                    //buttonApplySearch.setVisibility(View.GONE);
                    lineSearch.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(lineSearch.getWindowToken(), 0);
                    //hideInputLayout();
                    return true; // Событие обработано
                }
            }
            return false; // Событие не обработано
        });

        CalendarView calendarView = findViewById(R.id.calendar);
        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            String date = dayOfMonth + "/" + (month + 1) + "/" + year;
            Toast.makeText(this, "Выбрана дата: " + date, Toast.LENGTH_SHORT).show();
        });


        /*
        lineSearch.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                title.setVisibility(View.VISIBLE);
                lineSearch.setVisibility(View.GONE);
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(lineSearch.getWindowToken(), 0);
                lineSearch.setVisibility(View.GONE);
            }
        });
        */
    }
    // Проверка, было ли нажатие внутри View
    private boolean isTouchInsideView(MotionEvent event, View view) {
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        int x = (int) event.getRawX();
        int y = (int) event.getRawY();
        return x >= location[0] &&
                x <= (location[0] + view.getWidth()) &&
                y >= location[1] &&
                y <= (location[1] + view.getHeight());
    }

    public void openHomePage(View view) {
        calendar_content.setVisibility(View.GONE);
        main_content.setVisibility(View.VISIBLE);
        my_content.setVisibility(View.GONE);

        buttonOpenCalendar.setTextColor(Color.parseColor("#5F5F5F"));
        buttonOpenMain.setTextColor(Color.parseColor("#2196F3"));
        buttonOpenMy.setTextColor(Color.parseColor("#5F5F5F"));

        title.setText("Главное меню");

        imageViewSearch.setVisibility(View.VISIBLE);
    }

    public void openCalendar(View view) {
        calendar_content.setVisibility(View.VISIBLE);
        main_content.setVisibility(View.GONE);
        my_content.setVisibility(View.GONE);

        buttonOpenCalendar.setTextColor(Color.parseColor("#2196F3"));
        buttonOpenMain.setTextColor(Color.parseColor("#5F5F5F"));
        buttonOpenMy.setTextColor(Color.parseColor("#5F5F5F"));

        title.setText("Календарь");

        imageViewSearch.setVisibility(View.GONE);
    }

    public void openMy(View view) {
        calendar_content.setVisibility(View.GONE);
        main_content.setVisibility(View.GONE);
        my_content.setVisibility(View.VISIBLE);

        buttonOpenCalendar.setTextColor(Color.parseColor("#5F5F5F"));
        buttonOpenMain.setTextColor(Color.parseColor("#5F5F5F"));
        buttonOpenMy.setTextColor(Color.parseColor("#2196F3"));

        title.setText("Мой аккаунт");

        imageViewSearch.setVisibility(View.GONE);
    }

    public void openFormOfCreatingTask(View view) {
        Intent intent = new Intent(MainMenu2.this, CreatingTask.class);
        startActivity(intent);
    }
}