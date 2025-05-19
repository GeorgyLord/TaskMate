package com.example.taskmate;

import static android.graphics.Color.parseColor;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import java.util.concurrent.TimeUnit;

public class MainMenu2 extends AppCompatActivity {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    public TextView titleWindow;
    public FrameLayout main_content;
    public FrameLayout calendar_content;
    public FrameLayout my_content;
    public Button buttonOpenMain;
    public Button buttonOpenCalendar;
    public Button buttonOpenMy;
    public ImageView imageViewSearch;
    public LinearLayout renderingContainer;
    public LinearLayout emptyRenderingContainer;
    public long countTasksRendered;
    public long currentCountTasksRendered;
    private DocumentReference newDocRef;
    private Runnable runnable;
    public EditText lineSearch;
    private Handler handler = new Handler();
    public ImageView buttonClearSearchLine;
    private RecyclerView recyclerView;
    private List<Task> tasks = new ArrayList<>();
    private long numberSelectedMenu = 1;
    private TextView emailTitle;
    private EditText nameTitle;
    private String current_email;
    // Счетчики
    private TextView textCompletedTasks;
    private TextView textNotCompletedTasks;
    private LinearLayout calandary_container;
    private static final String CHANNEL_ID = "my_channel";
    private static final String CHANNEL_NAME = "My Channel";

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
        countTasksRendered = 0;
//        currentCountTasksRendered = 0;
        SharedPreferences sharedPref1 = getSharedPreferences("data", Context.MODE_PRIVATE);
        currentCountTasksRendered = sharedPref1.getLong("count_tasks", 0);
//        SharedPreferences.Editor editor = sharedPref1.edit();
//        editor.putLong("currentCountTasksRendered", currentCountTasksRendered);
//        editor.apply();

        textCompletedTasks = findViewById(R.id.textCompletedTasks);
        textNotCompletedTasks = findViewById(R.id.textNotCompletedTasks);
        calandary_container = findViewById(R.id.calandary_container);
        textCompletedTasks.setText("0");
        textNotCompletedTasks.setText("0");

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int screenWidth = displayMetrics.widthPixels; // Ширина экрана в пикселях
        int screenHeight = displayMetrics.heightPixels; // Высота экрана

        imageViewSearch = findViewById(R.id.imageViewSearch);
        lineSearch = findViewById(R.id.lineSearch);
        titleWindow = findViewById(R.id.screenName);
        buttonClearSearchLine = findViewById(R.id.buttonClearSearchLine);
        //Button buttonApplySearch = findViewById(R.id.applySearch);
        LinearLayout header = findViewById(R.id.block1);
        DrawerLayout main_window = findViewById(R.id.main);

        main_content = findViewById(R.id.main_content);
        calendar_content = findViewById(R.id.calendar_content);
        my_content = findViewById(R.id.my_content);

        buttonOpenMain = findViewById(R.id.buttonOpenMain);
        buttonOpenCalendar = findViewById(R.id.buttonOpenCalendar);
        buttonOpenMy = findViewById(R.id.buttonOpenMy);

        renderingContainer = findViewById(R.id.renderingContainer);
        emptyRenderingContainer = findViewById(R.id.emptyRenderingContainer);

        emailTitle = findViewById(R.id.emailTitle);
        SharedPreferences sharedPref_email = getSharedPreferences("data", Context.MODE_PRIVATE);
        String tempEmail = sharedPref_email.getString("email", "example@email.ru"); // второй параметр — значение по умолчанию
        emailTitle.setText(tempEmail);
        current_email = tempEmail;

        nameTitle = findViewById(R.id.nameTitle);

        DocumentReference taskRef3 = db.collection("users").document(tempEmail);

        db.collection("users")
                .document(tempEmail)
                .get()
                .addOnSuccessListener(documentSnapshot3 -> {
                    if (documentSnapshot3.exists()) {
                        // Получаем строковое значение по ключу "field_name"
                        nameTitle.setText(documentSnapshot3.getString("name"));

                    } else {
                        Log.d("Firestore", "Документ не существует");
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Ошибка чтения", e);
                });

        nameTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Вызывается ДО изменения текста
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Вызывается ВО ВРЕМЯ изменения текста

            }

            @Override
            public void afterTextChanged(Editable s) {
                // Вызывается ПОСЛЕ изменения текста
                String temp_string = s.toString(); // Обновляем переменную
                DocumentReference taskRef3 = db.collection("users").document(tempEmail);
                taskRef3.update("name", String.valueOf(temp_string))
                        .addOnSuccessListener(aVoid -> {
                            Log.d("Firestore", "Поле успешно обновлено");
                        })
                        .addOnFailureListener(e -> {
                            Log.e("Firestore", "Ошибка обновления", e);
                        });
            }
        });
        lineSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Вызывается ДО изменения текста
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Вызывается ВО ВРЕМЯ изменения текста

            }

            @Override
            public void afterTextChanged(Editable s) {
                // Вызывается ПОСЛЕ изменения текста
                String temp_string = s.toString(); // Обновляем переменную
                List<CardView> cardViews;
                // Список для хранения найденных CardView
                cardViews = new ArrayList<>();

                // Перебираем все дочерние элементы
                for (int i = 0; i < renderingContainer.getChildCount(); i++) {
                    View child = renderingContainer.getChildAt(i);

                    // Проверяем, является ли элемент CardView
                    if (child instanceof CardView) {
                        cardViews.add((CardView) child);
                    }
                }

                if(!temp_string.equals("")){
                    for (int i = 0; i < cardViews.size(); i++) {
                        TextView tw = cardViews.get(i).findViewById(R.id.taskTitle);
                        String name_t = String.valueOf(tw.getText());
                        System.out.println(name_t+" "+temp_string);
                        if(!name_t.contains(temp_string)){
                            cardViews.get(i).setVisibility(View.GONE);
                        }
                    }
                }else{
                    for (int i = 0; i < cardViews.size(); i++) {
                        cardViews.get(i).setVisibility(View.VISIBLE);
                    }
                }

            }
        });

        imageViewSearch.setOnClickListener(v -> {
            titleWindow.setVisibility(View.GONE);
            imageViewSearch.setVisibility(View.GONE);
            lineSearch.setVisibility(View.VISIBLE);
            buttonClearSearchLine.setVisibility(View.VISIBLE);
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
                    titleWindow.setVisibility(View.VISIBLE);
                    if (numberSelectedMenu==1) {
                        imageViewSearch.setVisibility(View.VISIBLE);
                    }
                    lineSearch.setVisibility(View.GONE);
                    buttonClearSearchLine.setVisibility(View.GONE);
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
            String date = dayOfMonth + "." + (month + 1) + "." + year;
            //Toast.makeText(this, "Выбрана дата: " + date, Toast.LENGTH_SHORT).show();


            DocumentReference userRef = db.collection("users").document(tempEmail);
            userRef.get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    List<String> user_tasks = (List<String>) documentSnapshot.get("id_of_tasks");
                    calandary_container.removeAllViews();
                    for (int i = 0; i < user_tasks.size(); i++) {
                        DocumentReference taskRef = db.collection("tasks").document(user_tasks.get(i));
                        taskRef.get().addOnSuccessListener(documentSnapshot2 -> {
                            if (documentSnapshot2.exists()) {
                                String tdate = String.valueOf(documentSnapshot2.getString("deadlineDate"));
//                                System.out.println("ДАТА "+tdate);
//                                System.out.println("ДОЛЖНО "+date);
                                if (!Objects.equals(tdate, "Указать дату")) {
                                    if(Objects.equals(removeLeadingZerosFromDate(tdate), date)) {
                                        CardView newCard = (CardView) LayoutInflater.from(this).inflate(R.layout.card_calandary, calandary_container, false);
                                        String tpriority = documentSnapshot2.getString("priority");
                                        String tnameTask = documentSnapshot2.getString("nameTask");
                                        TextView tt = newCard.findViewById(R.id.cal_name);
                                        View vv = newCard.findViewById(R.id.cal_color);
                                        tt.setText(tnameTask);
                                        if (Objects.equals(tpriority, "Низкий")) {
                                            vv.setBackgroundColor(getResources().getColor(R.color.identifier_green));
                                        } else if (Objects.equals(tpriority, "Средний")) {
                                            vv.setBackgroundColor(getResources().getColor(R.color.identifier_orange));
                                        } else if (Objects.equals(tpriority, "Высокий")) {
                                            vv.setBackgroundColor(getResources().getColor(R.color.identifier_red));
                                        }
                                        calandary_container.addView(newCard);
                                    }
                                }
                            }});
                    }
                }
            });
        });

        // Определяем задачу, которая будет выполняться каждые 5 секунд
        runnable = new Runnable() {
            @Override
            public void run() {
                // Вызываем вашу функцию
                update();

                // Повторяем задачу через 5 секунд
                handler.postDelayed(this, 1000); // 1000 миллисекунд = 1 секунда
            }
        };

        // Запускаем задачу
        handler.post(runnable);

        boolean value = getIntent().getBooleanExtra("must_draw", false);
        if (value){
            for (int i = 0; i < currentCountTasksRendered; i++) {
                CardView newCard = (CardView) LayoutInflater.from(this).inflate(R.layout.task_card, renderingContainer, false);

                // Устанавливаем ID
                newCard.setId(View.generateViewId());


                TextView titleText = newCard.findViewById(R.id.taskTitle);
                TextView descriptionText = newCard.findViewById(R.id.taskDescription);
                TextView taskDueDateText = newCard.findViewById(R.id.taskDueDate);
                View priorityIndicator = newCard.findViewById(R.id.priorityIndicator);
                CheckBox checkBoxTask = newCard.findViewById(R.id.checkBoxTask);

                String category = sharedPref1.getString("boolSpinnerCategory"+String.valueOf(i), "None");

                titleText.setText(sharedPref1.getString("title_task"+String.valueOf(i), "None"));

                String temp_dedescription = sharedPref1.getString("description"+String.valueOf(i), "None");
                if (!temp_dedescription.equals("")) {
                    descriptionText.setText(temp_dedescription);
                }
                else {
                    descriptionText.setText("Пусто");
                }
                String id_task = sharedPref1.getString("id_task"+String.valueOf(i), "None");

                String temp_string = sharedPref1.getString("deadlineDate"+String.valueOf(i), "None");
                if (!temp_string.equals("Указать дату")) {
                    taskDueDateText.setText(temp_string);

                    try {
                        // Парсим дату из строки "dd.MM.yyyy"
                        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
                        Date dueDate = sdf.parse(temp_string);

                        // Создаем календарь для dueDate
                        Calendar dueCalendar = Calendar.getInstance();
                        dueCalendar.setTime(dueDate);

                        // Вычитаем 1 день для напоминания
                        dueCalendar.add(Calendar.DAY_OF_YEAR, -1);

                        // Устанавливаем время напоминания (12:00)
                        dueCalendar.set(Calendar.HOUR_OF_DAY, 12);
                        dueCalendar.set(Calendar.MINUTE, 0);
                        dueCalendar.set(Calendar.SECOND, 0);

                        // Текущее время
                        long currentTime = System.currentTimeMillis();
                        long triggerTime = dueCalendar.getTimeInMillis();

                        // Проверяем, что напоминание в будущем
                        if (triggerTime > currentTime) {
                            long delay = triggerTime - currentTime;

                            scheduleNotification(
                                    this,
                                    delay,
                                    TimeUnit.MILLISECONDS,
                                    "Внимание",
                                    "Завтра у вас дедлайн (" + temp_string + ")"
                            );
                        } else {
                            Log.w("Notification", "Дата напоминания уже прошла");
                        }

                    } catch (ParseException e) {
                        e.printStackTrace();
                        Log.e("Notification", "Ошибка парсинга даты: " + temp_string);
                    }
                }
                else{
                    taskDueDateText.setText("");
                }

                String temp_priority = sharedPref1.getString("priority"+String.valueOf(i), "None");
                if (temp_priority.equals("0")){
                    priorityIndicator.setBackgroundColor(getResources().getColor(R.color.identifier_green));
                    //priorityIndicator.setBackgroundColor(0xFF82FF22); // 0xFF82FF22
                } else if (temp_priority.equals("1")) {
                    priorityIndicator.setBackgroundColor(getResources().getColor(R.color.identifier_orange));
                    //priorityIndicator.setBackgroundColor(0xFFB922FF); // 0xFFB922FF
                }else{
                    priorityIndicator.setBackgroundColor(getResources().getColor(R.color.identifier_red));
                    //priorityIndicator.setBackgroundColor(0xFFFF5722); // 0xFFFF5722
                }

                // Обрабатываем клик
                newCard.setOnClickListener(v -> {
                    Intent intent = new Intent(MainMenu2.this, DataTask.class);
                    // Передаем данные
                    intent.putExtra("id_task", id_task);
                    intent.putExtra("task_title", titleText.getText().toString());
                    intent.putExtra("task_description", temp_dedescription);
                    intent.putExtra("task_date", taskDueDateText.getText().toString());
                    intent.putExtra("category", category);

                    startActivity(intent);
                });

                renderingContainer.addView(newCard);

                checkBoxTask.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                        if (isChecked) {
                            // Зачеркиваем и делаем светлее
                            titleText.setPaintFlags(titleText.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                            titleText.setTextColor(ContextCompat.getColor(MainMenu2.this, R.color.light_gray));
                        } else {
                            // Возвращаем обычный текст
                            titleText.setPaintFlags(titleText.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                            titleText.setTextColor(ContextCompat.getColor(MainMenu2.this, android.R.color.primary_text_light));
                        }

                    }
                });
                /*
                if(checkBoxTask.isChecked()){
                    tasks_completed+=1;
                }
                else{
                    tasks_not_completed+=1;
                }
                */
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Удаляем задачу при уничтожении активности, чтобы избежать утечек памяти
        handler.removeCallbacks(runnable);
    }
    public static String removeLeadingZerosFromDate(String inputDate) {
        String[] parts = inputDate.split("\\.");
        if (parts.length != 3) {
            throw new IllegalArgumentException("Некорректный формат даты. Ожидается dd.MM.yyyy");
        }

        int day = Integer.parseInt(parts[0]);
        int month = Integer.parseInt(parts[1]);
        int year = Integer.parseInt(parts[2]);

        return day + "." + month + "." + year;
    }

    private void update() {
        drawingTaskCards();
        //showNotification(this, "Важное сообщение", "Привет, это тестовое уведомление!");
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
        textCompletedTasks.setText("0");
        textNotCompletedTasks.setText("0");
        calendar_content.setVisibility(View.GONE);
        main_content.setVisibility(View.VISIBLE);
        my_content.setVisibility(View.GONE);

        buttonOpenCalendar.setTextColor(parseColor("#5F5F5F"));
        buttonOpenMain.setTextColor(parseColor("#2196F3"));
        buttonOpenMy.setTextColor(parseColor("#5F5F5F"));

        titleWindow.setText("Главное меню");

        imageViewSearch.setVisibility(View.VISIBLE);

        numberSelectedMenu = 1;
    }

    public void openCalendar(View view) {
        textCompletedTasks.setText("0");
        textNotCompletedTasks.setText("0");
        calendar_content.setVisibility(View.VISIBLE);
        main_content.setVisibility(View.GONE);
        my_content.setVisibility(View.GONE);

        buttonOpenCalendar.setTextColor(parseColor("#2196F3"));
        buttonOpenMain.setTextColor(parseColor("#5F5F5F"));
        buttonOpenMy.setTextColor(parseColor("#5F5F5F"));

        titleWindow.setText("Календарь");

        imageViewSearch.setVisibility(View.GONE);

        numberSelectedMenu = 2;

        SharedPreferences sharedPref1 = getSharedPreferences("data", Context.MODE_PRIVATE);
        String user_email = sharedPref1.getString("email", "0");

        DocumentReference userRef = db.collection("users").document(user_email);
        userRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                List<String> user_tasks = (List<String>) documentSnapshot.get("id_of_tasks");
                calandary_container.removeAllViews();
                for (int i = 0; i < user_tasks.size(); i++) {
                    DocumentReference taskRef = db.collection("tasks").document(user_tasks.get(i));
                    taskRef.get().addOnSuccessListener(documentSnapshot2 -> {
                        if (documentSnapshot2.exists()) {
                            String tdate = String.valueOf(documentSnapshot2.getString("deadlineDate"));
                            if (!Objects.equals(tdate, "Указать дату")) {
                                if(Objects.equals(removeLeadingZerosFromDate(tdate), removeLeadingZerosFromDate(getCurrentDate()))) {
                                    CardView newCard = (CardView) LayoutInflater.from(this).inflate(R.layout.card_calandary, calandary_container, false);
                                    String tpriority = documentSnapshot2.getString("priority");
                                    String tnameTask = documentSnapshot2.getString("nameTask");
                                    TextView tt = newCard.findViewById(R.id.cal_name);
                                    View vv = newCard.findViewById(R.id.cal_color);
                                    tt.setText(tnameTask);
                                    if (Objects.equals(tpriority, "Низкий")) {
                                        vv.setBackgroundColor(getResources().getColor(R.color.identifier_green));
                                    } else if (Objects.equals(tpriority, "Средний")) {
                                        vv.setBackgroundColor(getResources().getColor(R.color.identifier_orange));
                                    } else if (Objects.equals(tpriority, "Высокий")) {
                                        vv.setBackgroundColor(getResources().getColor(R.color.identifier_red));
                                    }
                                    calandary_container.addView(newCard);
                                }
                            }
                        }});
                }
            }
        });
    }

    public void openMy(View view) {
        calendar_content.setVisibility(View.GONE);
        main_content.setVisibility(View.GONE);
        my_content.setVisibility(View.VISIBLE);

        buttonOpenCalendar.setTextColor(parseColor("#5F5F5F"));
        buttonOpenMain.setTextColor(parseColor("#5F5F5F"));
        buttonOpenMy.setTextColor(parseColor("#2196F3"));

        titleWindow.setText("Мой аккаунт");

        imageViewSearch.setVisibility(View.GONE);

        numberSelectedMenu = 3;

        SharedPreferences sharedPref = getSharedPreferences("data", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putLong("tasks_completed", 0);
        editor.putLong("tasks_not_completed", 0);
        editor.apply();

        SharedPreferences sharedPref1 = getSharedPreferences("data", Context.MODE_PRIVATE);
        String user_email = sharedPref1.getString("email", "0");

        textCompletedTasks.setText("0");
        textNotCompletedTasks.setText("0");
//        SharedPreferences sharedPref2 = getSharedPreferences("data", Context.MODE_PRIVATE);
//        SharedPreferences.Editor editor2 = sharedPref.edit();
        DocumentReference userRef = db.collection("users").document(user_email);
        userRef.get().addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        List<String> user_tasks = (List<String>) documentSnapshot.get("id_of_tasks");
                        for (int i = 0; i < user_tasks.size(); i++) {
                            DocumentReference taskRef = db.collection("tasks").document(user_tasks.get(i));
                            taskRef.get().addOnSuccessListener(documentSnapshot2 -> {
                                if (documentSnapshot2.exists()) {
                                    String bool_done = documentSnapshot2.getString("done");
                                    if (bool_done.equals("true")) {
                                        textCompletedTasks.setText(String.valueOf(Integer.parseInt((String) textCompletedTasks.getText())+1));
                                    } else {
                                        textNotCompletedTasks.setText(String.valueOf(Integer.parseInt((String) textNotCompletedTasks.getText())+1));
                                        //editor2.putLong("tasks_not_completed", sharedPref.getLong("tasks_not_completed", 0)+1);
                                    }
                                    System.out.println("MY");
                                }});
                        }
                    }
        });
    }

    public void openFormOfCreatingTask(View view) {
        Intent intent = new Intent(MainMenu2.this, CreatingTask.class);
        startActivity(intent);
    }

    public void logOutOfYourAccount(View view) {
        // Получаем экземпляр FirebaseAuth
        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        // Выход из аккаунта
        mAuth.signOut();

        Intent intent = new Intent(MainMenu2.this, LoginActivity.class);
        startActivity(intent);
    }

    public void deleteAccount(View view) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            user.delete()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Log.d("TAG", "Аккаунт удалён");
                            // Дополнительно: удалить данные из Firestore
                            SharedPreferences sharedPref = getSharedPreferences("data", Context.MODE_PRIVATE);
                            String email = sharedPref.getString("email", "0"); // второй параметр — значение по умолчанию
//                            deleteUserSubcollections(email);
                            deleteUserDataFromFirestore(email);
                        } else {
                            Log.e("TAG", "Ошибка удаления", task.getException());
                        }
                    });

            // Получаем экземпляр FirebaseAuth
            FirebaseAuth mAuth = FirebaseAuth.getInstance();

            // Выход из аккаунта
            mAuth.signOut();
            Intent intent = new Intent(MainMenu2.this, LoginActivity.class);
            startActivity(intent);
        }
    }

    public static String getCurrentDate() {
        return new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(new Date());
    }

    public static void scheduleNotification(
            Context context,
            long delay,
            TimeUnit timeUnit,
            String title,
            String message
    ) {
        // Создаем входные данные
        Data inputData = new Data.Builder()
                .putString("title", title)
                .putString("message", message)
                .build();

        // Создаем запрос на выполнение работы
        OneTimeWorkRequest workRequest = new OneTimeWorkRequest.Builder(NotificationWorker.class)
                .setInputData(inputData)
                .setInitialDelay(delay, timeUnit) // Устанавливаем задержку
                .build();

        // Запускаем работу
        WorkManager.getInstance(context).enqueue(workRequest);
    }
    public static void showNotification(Context context, String title, String message) {
        NotificationManager manager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Создаем канал (требуется для API 26+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            manager.createNotificationChannel(channel);
        }

        // Строим уведомление
        Notification.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder = new Notification.Builder(context, CHANNEL_ID);
        } else {
            builder = new Notification.Builder(context);
        }

        Notification notification = builder
                .setContentTitle(title)
                .setContentText(message)
                .setSmallIcon(R.drawable.ic_notification) // Ваша иконка
                .setAutoCancel(true)
                .build();

        // Показываем уведомление
        manager.notify(1, notification); // ID может быть любым
    }

    private void deleteUserDataFromFirestore(String userId) {
        db.collection("users").document(userId)
                .delete()
                .addOnSuccessListener(aVoid -> Log.d("TAG", "Документ удалён"))
                .addOnFailureListener(e -> Log.e("TAG", "Ошибка удаления", e));
    }
//    private void deleteUserSubcollections(String userId) {
//        DocumentReference userRef = db.collection("users").document(userId);
//        userRef.get().addOnSuccessListener(documentSnapshot -> {
//            List<String> list = Objects.requireNonNull(documentSnapshot.toObject(User.class)).id_of_tasks;
//            for (int i = 0; i < list.size(); i++) {
//                System.out.println(list.get(i));
//                db.collection("tasks").document(list.get(i))
//                        .delete()
//                        .addOnSuccessListener(aVoid -> Log.d("TAG", "Документ удалён"))
//                        .addOnFailureListener(e -> Log.e("TAG", "Ошибка удаления", e));
//            }
//        }).addOnFailureListener(e -> {
//            System.err.println("Ошибка при чтении документа: " + e.getMessage());
//        });

//        db.collection("users").document(userId).collection("tasks")
//                .get()
//                .addOnCompleteListener(task -> {
//                    if (task.isSuccessful()) {
//                        for (DocumentSnapshot doc : task.getResult()) {
//                            doc.getReference().delete();
//                        }
//                    }
//                });
//    }

    public void drawingTaskCards(){

        SharedPreferences sharedPref1 = getSharedPreferences("data", Context.MODE_PRIVATE);
        String user_email = sharedPref1.getString("email", "0"); // второй параметр — значение по умолчанию

//        Map<String, Object> data = new HashMap<>();
//        data.put("countTasksRendered", countTasksRendered);
//        data.put("currentCountTasksRendered", currentCountTasksRendered);


        DocumentReference userRef = db.collection("users").document(user_email);
        userRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                List<String> list = Objects.requireNonNull(documentSnapshot.toObject(User.class)).id_of_tasks;
//                Map<String, Object> data = new HashMap<>();
//                data.put("countTasksRendered", list.size());
//                data.put("currentCountTasksRendered", currentCountTasksRendered);
                // Сохраняем строку
                SharedPreferences sharedPref = getSharedPreferences("data", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putLong("count_tasks", list.size());
                editor.putLong("currentCountTasksRendered", list.size());
                editor.apply();

                for (int i = 0; i < list.size(); i++) {

                    DocumentReference userRef2 = db.collection("tasks").document(list.get(i));
                    int finalI = i;
                    userRef2.get().addOnSuccessListener(documentSnapshot2 -> {
                        if (documentSnapshot2.exists()) {
                            SharedPreferences sharedPref2 = getSharedPreferences("data", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor2 = sharedPref.edit();
                            //Task new_task = documentSnapshot2.toObject(Task.class);
                            String temp_nameTask = documentSnapshot2.getString("nameTask");
                            String temp_description = documentSnapshot2.getString("description");
                            String temp_deadlineDate = documentSnapshot2.getString("deadlineDate");
                            String temp_priority = documentSnapshot2.getString("priority");
                            String temp_done = documentSnapshot2.getString("done");
                            String temp_invitationCode = documentSnapshot2.getString("invitationCode");
                            String temp_category = documentSnapshot2.getString("boolSpinnerCategory");
//                            String temp_boolRadioButtonHigh = documentSnapshot2.getString("boolRadioButtonHigh");
//                            String temp_boolRadioButtonLow = documentSnapshot2.getString("boolRadioButtonLow");
//                            String temp_boolRadioButtonMedium = documentSnapshot2.getString("boolRadioButtonMedium");

                            editor2.putString("id_task"+finalI, list.get(finalI));
                            editor2.putString("title_task"+finalI, temp_nameTask);
                            editor2.putString("description"+finalI, temp_description);
                            editor2.putString("deadlineDate"+finalI, temp_deadlineDate);
                            editor2.putString("priority"+finalI, temp_priority);
                            editor2.putString("done"+finalI, temp_done);
                            editor2.putString("invitationCode"+finalI, temp_invitationCode);
                            editor2.putString("category"+finalI, temp_category);

//                            if (Objects.equals(temp_boolRadioButtonHigh, "true")){
//                                editor2.putString("priority"+finalI, "0");
//                            } else if (Objects.equals(temp_boolRadioButtonMedium, "true")) {
//                                editor2.putString("priority"+finalI, "1");
//                            }else {
//                                editor2.putString("priority"+finalI, "2");
//                            }

                            editor2.apply();
                        }
                        else{
                            db.collection("users")
                                    .document(current_email)
                                    .get()
                                    .addOnSuccessListener(documentSnapshot_delete_task -> {
                                        if (documentSnapshot_delete_task.exists()) {
                                            // Документ существует - удаляем

                                            List<String> user_tasks = new ArrayList<>();

                                            if (documentSnapshot_delete_task.contains("id_of_tasks")) {
                                                // Если поле "tasks" существует, получаем его
                                                user_tasks = (List<String>) documentSnapshot.get("id_of_tasks");
                                            }

                                            user_tasks.remove(list.get(finalI));

                                            // Обновляем документ в Firestore
                                            db.collection("users")
                                                    .document(current_email)
                                                    .update("id_of_tasks", user_tasks);
                                        } else {
                                            Log.d("Firestore", "Документ не найден");
                                        }
                                    });
                        }
                    }).addOnFailureListener(e -> {
                        System.err.println("Ошибка при чтении документа: " + e.getMessage());
                    });
                }
                editor.apply(); // или editor.commit();
            }
        }).addOnFailureListener(e -> {
            System.err.println("Ошибка при чтении документа: " + e.getMessage());
        });

        SharedPreferences sharedPref2 = getSharedPreferences("data", Context.MODE_PRIVATE);
        currentCountTasksRendered = sharedPref2.getLong("currentCountTasksRendered", 0);

        if (currentCountTasksRendered > 0) {
            if (countTasksRendered != currentCountTasksRendered) {
                //System.out.println("Есть новые задачи!!!");
                renderingContainer.removeAllViews();
//                tasks_completed=0;
//                tasks_not_completed=0;
                for (int i = 0; i < currentCountTasksRendered; i++) {
                    CardView newCard = (CardView) LayoutInflater.from(this).inflate(R.layout.task_card, renderingContainer, false);

                    // Устанавливаем ID
                    newCard.setId(View.generateViewId());


                    TextView titleText = newCard.findViewById(R.id.taskTitle);
                    TextView descriptionText = newCard.findViewById(R.id.taskDescription);
                    TextView taskDueDateText = newCard.findViewById(R.id.taskDueDate);
                    View priorityIndicator = newCard.findViewById(R.id.priorityIndicator);
                    CheckBox checkBox = newCard.findViewById(R.id.checkBoxTask);

                    /*
                    if(checkBox.isChecked()){
                        tasks_completed+=1;
                    }
                    else{
                        tasks_not_completed+=1;
                    }
                    */

                    titleText.setText(sharedPref2.getString("title_task"+String.valueOf(i), "None"));

                    String temp_dedescription = sharedPref2.getString("description"+String.valueOf(i), "None");
                    if (!temp_dedescription.equals("")) {
                        descriptionText.setText(temp_dedescription);
                    }
                    else {
                        descriptionText.setText("Пусто");
                    }
                    String id_task = sharedPref2.getString("id_task"+String.valueOf(i), "None");
                    String bool_done = sharedPref2.getString("done"+String.valueOf(i), "false");

                    String temp_string = sharedPref2.getString("deadlineDate"+String.valueOf(i), "None");
                    String t_cat = sharedPref2.getString("category"+String.valueOf(i), "None");
                    if (!temp_string.equals("Указать дату")) {
                        taskDueDateText.setText(temp_string);
                    }
                    else{
                        taskDueDateText.setText("");
                    }

                    String temp_priority = sharedPref2.getString("priority"+String.valueOf(i), "None");
                    if (temp_priority.equals("Низкий")){
                        priorityIndicator.setBackgroundColor(getResources().getColor(R.color.identifier_green));
//                        priorityIndicator.setBackgroundColor(0xFF82FF22);
                        //priorityIndicator.setBackground(Drawable.createFromPath("#82FF22FF"));
                    } else if (temp_priority.equals("Средний")) {
                        priorityIndicator.setBackgroundColor(getResources().getColor(R.color.identifier_orange));
//                        priorityIndicator.setBackgroundColor(0xFFB922FF);
                        //priorityIndicator.setBackground(Drawable.createFromPath("#FFB922FF"));
                    }else{
                        priorityIndicator.setBackgroundColor(getResources().getColor(R.color.identifier_red));
//                        priorityIndicator.setBackgroundColor(0xFFFF5722);
                        //priorityIndicator.setBackground(Drawable.createFromPath("#FF5722FF"));
                    }
                    String temp_invitationCode = sharedPref2.getString("invitationCode"+String.valueOf(i), "None");

                    // Обрабатываем клик
                    newCard.setOnClickListener(v -> {
//                        // 1. Получаем позицию (если нужно)
//                        int position = renderingContainer.indexOfChild(v);
//
//                        // 2. Выполняем действие
//                        Toast.makeText(this, "Нажата карточка " + position, Toast.LENGTH_SHORT).show();
//
//                        // 3. Пример изменения карточки
//                        CardView card = (CardView) v;
//                        TextView title = card.findViewById(R.id.titleText);
//                        title.setText("Нажато!");
                        Intent intent = new Intent(MainMenu2.this, DataTask.class);
                        // Передаем данные
                        intent.putExtra("id_task", id_task);
                        intent.putExtra("task_title", titleText.getText().toString());
                        intent.putExtra("task_description", temp_dedescription);
                        intent.putExtra("task_date", taskDueDateText.getText().toString());
                        intent.putExtra("priority", temp_priority);
                        intent.putExtra("invitation_cod", temp_invitationCode);
                        intent.putExtra("category", t_cat);


                        startActivity(intent);
                    });

                    renderingContainer.addView(newCard);

                    checkBox.setChecked(Boolean.valueOf(bool_done));

                    if (checkBox.isChecked()) {
                        // Зачеркиваем и делаем светлее
                        titleText.setPaintFlags(titleText.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                        titleText.setTextColor(ContextCompat.getColor(MainMenu2.this, R.color.light_gray));
                    } else {
                        // Возвращаем обычный текст
                        titleText.setPaintFlags(titleText.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                        titleText.setTextColor(ContextCompat.getColor(MainMenu2.this, android.R.color.primary_text_light));
                    }

                    checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                            if (isChecked) {
                                // Зачеркиваем и делаем светлее
                                titleText.setPaintFlags(titleText.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                                titleText.setTextColor(ContextCompat.getColor(MainMenu2.this, R.color.light_gray));
                            } else {
                                // Возвращаем обычный текст
                                titleText.setPaintFlags(titleText.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                                titleText.setTextColor(ContextCompat.getColor(MainMenu2.this, android.R.color.primary_text_light));
                            }

                            DocumentReference taskRef3 = db.collection("tasks").document(id_task);
                            taskRef3.update("done", String.valueOf(isChecked))
                                    .addOnSuccessListener(aVoid -> {
                                        Log.d("Firestore", "Поле успешно обновлено");
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.e("Firestore", "Ошибка обновления", e);
                                    });

                        }
                    });
                }

                countTasksRendered = currentCountTasksRendered;
            } else {
                //System.out.println("Нет новых задач");
            }
            emptyRenderingContainer.setVisibility(View.GONE);
            renderingContainer.setVisibility(View.VISIBLE);
        }
        else{
            emptyRenderingContainer.setVisibility(View.VISIBLE);
            renderingContainer.setVisibility(View.GONE);
        }
    }

    public void clearInputLine(View view) {
        lineSearch.setText("");
        titleWindow.setVisibility(View.VISIBLE);
        imageViewSearch.setVisibility(View.VISIBLE);
        lineSearch.setVisibility(View.GONE);
        buttonClearSearchLine.setVisibility(View.GONE);
    }
/*
    public void showParticipantsDialog(Context context, List<User> participants) {
        // Создаем диалог
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_participants, null);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();

        // Контейнер для карточек
        LinearLayout container = dialogView.findViewById(R.id.participantsContainer);

        // Добавляем карточки для каждого участника
        for (User user : participants) {
            View cardView = LayoutInflater.from(context).inflate(R.layout.item_participant, container, false);

            // Настраиваем карточку
            TextView nameView = cardView.findViewById(R.id.userName);
            nameView.setText(user.getName());

            ImageButton deleteButton = cardView.findViewById(R.id.deleteButton);
            deleteButton.setOnClickListener(v -> {
                // Удаляем участника
                removeParticipant(user.getId());
                container.removeView(cardView);
                Toast.makeText(context, user.getName() + " удален", Toast.LENGTH_SHORT).show();
            });

            // Добавляем карточку в контейнер
            container.addView(cardView);
        }

        // Показываем диалог
        dialog.show();
    }

    private void removeParticipant(String userId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("tasks").document(taskId)
                .update("participants", FieldValue.arrayRemove(userId))
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Ошибка удаления участника", e);
                });
    }
    */
}

// E8DBFDFF - бледно фиолетовый
/*

moscowt3485@mail.ru
12345678

 */