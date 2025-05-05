package com.example.taskmate;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.CalendarView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class AddNewTask extends AppCompatActivity {
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    DocumentReference newDocRef;

    private EditText editTextInput;
    private ArrayList<String> wordList = new ArrayList<>();
    private LinearLayout container;

    private ImageView CancelDateSelection;
    private ImageView CancelTimeSelection;
    private ImageView CancelRepeatSelection;

    private TextView textView11;
    private TextView textView20;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_new_task);

        /*
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        */
        /*
        if (savedInstanceState != null) {
            EditText editText = findViewById(R.id.editTextText);
            RadioButton radioButton1 = findViewById(R.id.radioButton);
            RadioButton radioButton2 = findViewById(R.id.radioButton2);
            RadioButton radioButton3 = findViewById(R.id.radioButton3);
            Switch mySwitch = findViewById(R.id.switch1);
            TextView textView11 = findViewById(R.id.textView11);
            TextView textView20 = findViewById(R.id.textView20);
            TextView textView21 = findViewById(R.id.textView21);

            editText.setText(savedInstanceState.getString("AddNewTask_name", "123"));
            radioButton1.setChecked(savedInstanceState.getBoolean("AddNewTask_radioButton1", false));
            radioButton2.setChecked(savedInstanceState.getBoolean("AddNewTask_radioButton2", false));
            radioButton3.setChecked(savedInstanceState.getBoolean("AddNewTask_radioButton3", false));
            mySwitch.setChecked(savedInstanceState.getBoolean("AddNewTask_mySwitch", false));
            textView11.setText(savedInstanceState.getString("AddNewTask_textView11", ""));
            textView20.setText(savedInstanceState.getString("AddNewTask_textView20", ""));
            textView21.setText(savedInstanceState.getString("AddNewTask_textView21", ""));
        }
        */




        CancelDateSelection = findViewById(R.id.imageView5);
        CancelTimeSelection = findViewById(R.id.imageView6);
        CancelRepeatSelection = findViewById(R.id.imageView7);

        ImageButton imageButton = findViewById(R.id.imageButton2);

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddNewTask.this, Test3.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
            }
        });

        Switch mySwitch = findViewById(R.id.switch1);
        LinearLayout blockToShowHide = findViewById(R.id.date_time);

        mySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    blockToShowHide.animate().alpha(1f).setDuration(300).withStartAction(() -> {
                        blockToShowHide.setVisibility(LinearLayout.VISIBLE);
                    });
                } else {
                    blockToShowHide.animate().alpha(0f).setDuration(300).withEndAction(() -> {
                        blockToShowHide.setVisibility(LinearLayout.GONE);
                    });
                }
            }
        });

        CardView myCardView = findViewById(R.id.cardview1);



        editTextInput = findViewById(R.id.editTextText2);
        container = findViewById(R.id.list_tags);
        editTextInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String input = s.toString();
                // Если последний символ — пробел
                /*if (input.charAt(input.length() - 1) == ' '){
                    System.out.println(3);
                }*/
//                System.out.println("len = "+input.length());
//                System.out.println("last = <"+input.charAt(input.length() - 1)+">");
                if (!input.isEmpty() && input.charAt(input.length() - 1) == ' ') {
                    String wordToAdd = input.substring(0, input.length() - 1).trim(); // убираем пробел
                    System.out.println(2);
                    if (!wordToAdd.isEmpty()) {
                        addWordToContainer(wordToAdd);
                        wordList.add(wordToAdd);
                        editTextInput.setText(""); // очищаем поле
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }

        });

    }
    private void addWordToContainer(String word) {
        TextView textView_temp = new TextView(this);
        textView_temp.setText("• " + word);
        textView_temp.setTextSize(16);
        textView_temp.setPadding(15, 20, 15, 10);
        container.addView(textView_temp);

        textView_temp.setOnClickListener(v -> {
            container.removeView(textView_temp); // Удаляем из контейнера
        });
    }

    public void create_task(View view) {

        // Название задачи
        EditText et = findViewById(R.id.editTextText);
        String s = et.getText().toString().trim();
        if (s.isEmpty()){
            Toast.makeText(AddNewTask.this, "Название не должно быть пустым.", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> data = new HashMap<>();
        data.put("name", s);

        // Приоритет
        RadioButton rb2 = findViewById(R.id.radioButton2);
        RadioButton rb3 = findViewById(R.id.radioButton3);
        int id = 0;
        if(rb2.isChecked()){
            id = 1;
        } else if (rb3.isChecked()) {
            id = 2;
        }
        data.put("priority", id);

        Switch mySwitch;
        mySwitch = findViewById(R.id.switch1);
        boolean isSwitchOn = mySwitch.isChecked();
        if (isSwitchOn){
            data.put("deadline", true);
            CalendarView calendarView = findViewById(R.id.calendarView);
            calendarView.setOnDateChangeListener((CalendarView view2, int year, int month, int dayOfMonth) -> {
                String date = dayOfMonth + "." + (month + 1) + "." + year;
                Toast.makeText(this, "Дата: " + date, Toast.LENGTH_SHORT).show();
                data.put("date", date);
            });

        } else {
            data.put("deadline", false);
            data.put("date", null);
            data.put("time", null);
        }

        data.put("tags", null);
        data.put("time", "12:00");

        SharedPreferences sharedPref = getSharedPreferences("data", Context.MODE_PRIVATE);
        String id_user = sharedPref.getString("id", "0"); // второй параметр — значение по умолчанию
        data.put("owner", id_user);

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.getDefault());
        String currentDate = dateFormat.format(calendar.getTime());
        data.put("date_and_time_of_creation", currentDate);

        String coolectionPath = "tasks";
        newDocRef = db.collection(coolectionPath).document();
        newDocRef.set(data);

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

        Intent intent = new Intent(AddNewTask.this, Test3.class);
        /*
        // Передача списка карточек как массива строк
        String[][] cardsData = {
                {"Привет", "Это первая карточка"},
                {"Добро пожаловать", "Второй блок"},
                {"Информация", "Третья запись"}
        };

// Преобразуем в ArrayList<String[]> для передачи
        ArrayList<String[]> dataList = new ArrayList<>(Arrays.asList(cardsData));

        intent.putExtra("cards_data", dataList);*/
        startActivity(intent);
    }

    public void specify_date(View view) {
        showDatePicker();
    }
    //private TextView tvSelectedDate;
    private String selectedDate = ""; // Переменная для хранения даты
    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH); // Январь = 0
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, selectedYear, selectedMonth, selectedDayOfMonth) -> {
                    // Сохраняем дату в переменную как строку
                    selectedDate = selectedDayOfMonth + "." + (selectedMonth + 1) + "." + selectedYear;

                    // Обновляем TextView
                    textView11 = findViewById(R.id.textView11);
                    textView11.setText(selectedDate);
                    //tvSelectedDate.setText("Выбранная дата: " + selectedDate);

                    // Теперь ты можешь использовать selectedDate где угодно!
                    //useSelectedDate(selectedDate);
                    CancelDateSelection.setVisibility(View.VISIBLE);
                }, year, month, day);

        datePickerDialog.show();
    }

    public void specify_time(View view) {
        showTimePickerDialog();
    }
    public void showTimePickerDialog(){
        // Получаем текущее время
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        // Создаём диалог
        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        // Обрабатываем выбор времени
                        String selectedTime = hourOfDay + ":" + String.format("%02d", minute);
                        textView20 = findViewById(R.id.textView20);
                        textView20.setText(selectedTime);
                        CancelTimeSelection.setVisibility(View.VISIBLE);
                    }
                }, hour, minute, true); // true = формат 24 часа

        timePickerDialog.show();
    }

    public void functionCancelDateSelection(View view) {
        textView11.setText("Укажите дату");
        CancelDateSelection.setVisibility(View.GONE);
    }

    public void functionCancelTimeSelection(View view) {
        textView20.setText("Укажите время");
        CancelTimeSelection.setVisibility(View.GONE);
    }

    public void functionCancelRepeatSelection(View view) {
        //("Повтор");
        CancelRepeatSelection.setVisibility(View.GONE);
    }

    public void openingRepeatSettings(View view) {
        Intent intent = new Intent(AddNewTask.this, SelectRepeat.class);
        overridePendingTransition(R.anim.slide_center, R.anim.slide_center);
        startActivity(intent);
    }

    /*
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        EditText editText = findViewById(R.id.editTextText);
        RadioButton radioButton1 = findViewById(R.id.radioButton);
        RadioButton radioButton2 = findViewById(R.id.radioButton2);
        RadioButton radioButton3 = findViewById(R.id.radioButton3);
        Switch mySwitch = findViewById(R.id.switch1);
        TextView textView11 = findViewById(R.id.textView11);
        TextView textView20 = findViewById(R.id.textView20);
        TextView textView21 = findViewById(R.id.textView21);

        // Сохраняем данные
        outState.putString("AddNewTask_name", editText.getText().toString());
        outState.putBoolean("AddNewTask_radioButton1", radioButton1.isChecked());
        outState.putBoolean("AddNewTask_radioButton2", radioButton2.isChecked());
        outState.putBoolean("AddNewTask_radioButton3", radioButton3.isChecked());
        outState.putBoolean("AddNewTask_mySwitch", mySwitch.isChecked());
        outState.putString("AddNewTask_textView11", textView11.getText().toString());
        outState.putString("AddNewTask_textView20", textView20.getText().toString());
        outState.putString("AddNewTask_textView21", textView21.getText().toString());
    }
    */
}