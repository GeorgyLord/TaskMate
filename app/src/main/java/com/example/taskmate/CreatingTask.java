package com.example.taskmate;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CreatingTask extends AppCompatActivity {

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    DocumentReference newDocRef;

    public TextInputEditText editTextNameTask;
    public TextInputEditText editTextMultiLineDescription;
    public Spinner spinnerCategory;
    public RadioButton radioButtonLow;
    public RadioButton radioButtonMedium;
    public RadioButton radioButtonHigh;
    public Switch switchDeadline;
    public TextView textViewDate;
    public TextView textViewTime;
    public Spinner spinnerRepetition;
    private CardView cardViewDate;
    private CardView cardViewTime;
    public Calendar calendar = Calendar.getInstance();
    private ImageView buttonClearDate;
    private ImageView buttonClearTime;
    private LinearLayout containerDeadline;
    private Spinner spinnerPriority;



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

        editTextNameTask = findViewById(R.id.editTextNameTask);
        editTextMultiLineDescription = findViewById(R.id.editTextMultiLineDescription);
        spinnerCategory = findViewById(R.id.spinnerCategory);
        spinnerRepetition = findViewById(R.id.spinnerReperirion);
        spinnerPriority = findViewById(R.id.spinnerPriority);
        radioButtonLow = findViewById(R.id.radioButtonLow);
        radioButtonMedium = findViewById(R.id.radioButtonMedium);
        radioButtonHigh = findViewById(R.id.radioButtonHigh);
        switchDeadline = findViewById(R.id.switchDeadline);
        textViewDate = findViewById(R.id.textViewDate);
        textViewTime = findViewById(R.id.textViewTime);
        containerDeadline = findViewById(R.id.containerDeadline);

        cardViewDate = findViewById(R.id.cardViewOpenCalendar);
        cardViewTime = findViewById(R.id.cardViewOpenClock);

        buttonClearTime = findViewById(R.id.buttonClearTime);
        buttonClearDate = findViewById(R.id.buttonClearDate);



        cardViewDate.setOnClickListener(v -> {
            // Сначала показываем диалог выбора даты
            openCalendar();
        });

        cardViewTime.setOnClickListener(v -> {
            // Сначала показываем диалог выбора даты
            openClock();
        });


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
        ArrayAdapter<CharSequence> adapterPriority = ArrayAdapter.createFromResource(
                this,
                R.array.priority_array,
                android.R.layout.simple_spinner_item
        );

        // Указываем layout для выпадающего списка
        adapterCategory.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapterRepetition.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapterPriority.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Применяем адаптер
        spinnerCategory.setAdapter(adapterCategory);
        spinnerRepetition.setAdapter(adapterRepetition);
        spinnerPriority.setAdapter(adapterPriority);
    }

    public void returnToMainMenu(View view) {
        Intent intent = new Intent(CreatingTask.this, MainMenu2.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
    }

    public void createTask(View view) {
        String nameTask = editTextNameTask.getText().toString();
        String description = editTextMultiLineDescription.getText().toString();
        String boolSpinnerCategory = spinnerCategory.getSelectedItem().toString();
        String boolSpinnerRepetition = spinnerRepetition.getSelectedItem().toString();
//        String boolRadioButtonLow = String.valueOf(radioButtonLow.isChecked());
//        String boolRadioButtonMedium = String.valueOf(radioButtonMedium.isChecked());
//        String boolRadioButtonHigh = String.valueOf(radioButtonHigh.isChecked());
//        String boolSwitchDeadline = String.valueOf(switchDeadline.isChecked());
        String priority = spinnerPriority.getSelectedItem().toString();
        String deadlineDate = textViewDate.getText().toString();
        String deadlineTime = textViewTime.getText().toString();


//        System.out.println(nameTask);
//        System.out.println(description);
//        System.out.println(boolSpinnerCategory);
//        System.out.println(boolSpinnerRepetition);
//        System.out.println(boolRadioButtonLow);
//        System.out.println(boolRadioButtonMedium);
//        System.out.println(boolRadioButtonHigh);
//        System.out.println(boolSwitchDeadline);
//        System.out.println(deadlineDate);
//        System.out.println(deadlineTime);
//        System.out.println("-----------------");
        Map<String, Object> data_task = new HashMap<>();
        data_task.put("nameTask", nameTask);
        data_task.put("description", description);
        data_task.put("boolSpinnerCategory", boolSpinnerCategory);
        data_task.put("boolSpinnerRepetition", boolSpinnerRepetition);
        data_task.put("priority", priority);
//        data_task.put("boolRadioButtonLow", boolRadioButtonLow);
//        data_task.put("boolRadioButtonMedium", boolRadioButtonMedium);
//        data_task.put("boolRadioButtonHigh", boolRadioButtonHigh);
//        data_task.put("boolSwitchDeadline", boolSwitchDeadline);
        data_task.put("deadlineDate", deadlineDate);
        data_task.put("deadlineTime",deadlineTime );

        // Создаем ArrayList (динамический массив)
        ArrayList<String> stringList = new ArrayList<>();
        data_task.put("subtasks", stringList);

        SharedPreferences sharedPref = getSharedPreferences("data", Context.MODE_PRIVATE);
        String id_user = sharedPref.getString("email", "0"); // второй параметр — значение по умолчанию
        data_task.put("owner", id_user);

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        String currentDate = dateFormat.format(calendar.getTime());
        String currentTime = timeFormat.format(calendar.getTime());
        data_task.put("date_of_creation", currentDate);
        data_task.put("time_of_creation", currentTime);

        String coolectionPath = "tasks";
        newDocRef = db.collection(coolectionPath).document();
        newDocRef.set(data_task);

        DocumentReference userRef = db.collection("users").document(id_user);
        userRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                // Получаем массив как List<String>
                List<String> list = documentSnapshot.toObject(User.class).id_of_tasks;

                // Добавляем новое значение
                list.add(newDocRef.getId());

                // Обновляем документ
                userRef.update("id_of_tasks", list)
                        .addOnSuccessListener(aVoid ->
                                System.out.println("Массив успешно обновлён"))
                        .addOnFailureListener(e ->
                                System.err.println("Ошибка при обновлении: " + e.getMessage()));
            }
        }).addOnFailureListener(e -> {
            System.err.println("Ошибка при чтении документа: " + e.getMessage());
        });

        Intent intent = new Intent(CreatingTask.this, MainMenu2.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
    }

    public void openCalendar() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    calendar.set(Calendar.YEAR, year);
                    calendar.set(Calendar.MONTH, month);
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                    // Форматируем дату в строку
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
                    String temp_string = dateFormat.format(calendar.getTime());
                    textViewDate.setText(temp_string);
                    buttonClearDate.setVisibility(View.VISIBLE);
                    //tvSelectedDate.setText("Дата: " + selectedDateStr);
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    public void openClock() {
        TimePickerDialog timePickerDialog = new TimePickerDialog(
                this,
                (view, hourOfDay, minute) -> {
                    calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    calendar.set(Calendar.MINUTE, minute);

                    // Форматируем время в строку
                    SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
                    String temp_string = timeFormat.format(calendar.getTime());
                    textViewTime.setText(temp_string);
                    buttonClearTime.setVisibility(View.VISIBLE);
                    //tvSelectedTime.setText("Время: " + selectedTimeStr);
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true // 24-часовой формат
        );
        timePickerDialog.show();
    }

    public void clearDate(View view) {
        textViewDate.setText("Укажите дату");
        buttonClearDate.setVisibility(View.GONE);
    }

    public void clearTime(View view) {
        textViewTime.setText("Укажите время");
        buttonClearTime.setVisibility(View.GONE);
    }

    public void changingSwitch(View view) {
        if (switchDeadline.isChecked()){
            containerDeadline.setVisibility(View.VISIBLE);
        }
        else{
            containerDeadline.setVisibility(View.GONE);
        }
    }
}