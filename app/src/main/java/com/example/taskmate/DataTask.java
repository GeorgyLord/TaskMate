package com.example.taskmate;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.text.style.StrikethroughSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

import com.example.taskmate.R;

public class DataTask extends AppCompatActivity {
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    DocumentReference newDocRef;
    public LinearLayout containerSubtasks;
    public String id_task;
    private FrameLayout frameLayoutEnd;
    private FrameLayout frameLayoutInvitation;
    private String invitation_cod;
    private TextView text_invitation_cod;
    private EditText textDescription;
    private ImageButton btnOverflow;
    private PopupMenu popupMenu;

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


        containerSubtasks = findViewById(R.id.containerSubtasks);
        frameLayoutEnd = findViewById(R.id.frameLayoutEnd);
        frameLayoutInvitation = findViewById(R.id.frameLayoutInvitation);
        text_invitation_cod = findViewById(R.id.text_invitation_cod);
        textDescription = findViewById(R.id.textDescription);
        btnOverflow = findViewById(R.id.btn_overflow);


        // Создаем PopupMenu
        btnOverflow.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(DataTask.this, v);
            popupMenu.getMenuInflater().inflate(R.menu.overflow_menu, popupMenu.getMenu());

            popupMenu.setOnMenuItemClickListener(item -> {
                int id = item.getItemId();

                if (id == R.id.action_members) {
                    showMembersDialog();
                    return true;
                } else if (id == R.id.action_delete) {
                    confirmDelete();
                    return true;
                }
                return false;
            });

            popupMenu.show();
        });

        // Получаем данные из Intent
        String title = getIntent().getStringExtra("task_title");
        String description = getIntent().getStringExtra("task_description");
        String date = getIntent().getStringExtra("task_date");
        id_task = getIntent().getStringExtra("id_task");
        invitation_cod = getIntent().getStringExtra("invitation_cod");

        // Настраиваем View
        EditText titleView = findViewById(R.id.detail_title);
        //TextView descView = findViewById(R.id.detail_description);
        TextView dateView = findViewById(R.id.detail_date);

        titleView.setText(title);
        textDescription.setText(description);
        //descView.setText("Описание: "+description);

        if (!Objects.equals(date, "")) {
            dateView.setText("Окончание: " + date);
        }else{
            frameLayoutEnd.setVisibility(View.GONE);
        }

        text_invitation_cod.setText("Код приглашения: "+invitation_cod);

        //копирование в буфер обмена
        text_invitation_cod.setOnClickListener(v -> {
            // Получаем ClipboardManager
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);

            // Создаем ClipData
            ClipData clip = ClipData.newPlainText("label1", invitation_cod);

            // Устанавливаем данные в буфер обмена
            clipboard.setPrimaryClip(clip);

            // Показываем уведомление
            Toast.makeText(this, "Текст скопирован", Toast.LENGTH_SHORT).show();
        });




        newDocRef = db.collection("tasks").document(id_task);
        newDocRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                // 3. Получаем текущий массив задач (или создаем новый, если его нет)
                List<Map<String, Object>> subtasks = new ArrayList<>();

                if (documentSnapshot.contains("subtasks")) {
                    // Если поле "tasks" существует, получаем его
                    subtasks = (List<Map<String, Object>>) documentSnapshot.get("subtasks");
                }


                Iterator<Map<String, Object>> iterator = subtasks.iterator();
                while (iterator.hasNext()) {
                    Map<String, Object> task = iterator.next();
                    CardView newCard2 = (CardView) LayoutInflater.from(this).inflate(R.layout.subtask_card, containerSubtasks, false);

                    // Устанавливаем ID
                    Random random2 = new Random();
                    newCard2.setId(Integer.parseInt(task.get("id").toString()));
                    String id_card2 = String.valueOf(newCard2.getId());

                    EditText title_subtask2 = newCard2.findViewById(R.id.title_subtask);
                    title_subtask2.setId(random2.nextInt(999999));

                    CheckBox checkBoxSubtask2 = newCard2.findViewById(R.id.checkBoxSubtask);
                    checkBoxSubtask2.setId(random2.nextInt(999999));

                    checkBoxSubtask2.setChecked(Boolean.parseBoolean(task.get("done").toString()));
                    title_subtask2.setText(task.get("title").toString());

                    // Находим кнопку удаления внутри CardView
                    ImageButton btnDelete = newCard2.findViewById(R.id.buttonDelete);
                    btnDelete.setId(random2.nextInt(999999));

                    containerSubtasks.addView(newCard2);

                    if (checkBoxSubtask2.isChecked()) {
                        // Зачеркиваем и делаем светлее
                        title_subtask2.setPaintFlags(title_subtask2.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                        title_subtask2.setTextColor(ContextCompat.getColor(DataTask.this, R.color.light_gray));
                    } else {
                        // Возвращаем обычный текст
                        title_subtask2.setPaintFlags(title_subtask2.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                        title_subtask2.setTextColor(ContextCompat.getColor(DataTask.this, android.R.color.primary_text_light));
                    }

                    /*
                    String text = title_subtask2.getText().toString();
                    SpannableString spannableString = new SpannableString(text);
                    if (checkBoxSubtask2.isChecked()){
                        // Зачеркивание
                        spannableString.setSpan(new StrikethroughSpan(), 0, text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        // Изменение цвета
                        spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#BDBDBD")),
                                0, text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }else{
                        // Удаление всех стилей
                        StrikethroughSpan[] strikeSpans = spannableString.getSpans(0, text.length(), StrikethroughSpan.class);
                        ForegroundColorSpan[] colorSpans = spannableString.getSpans(0, text.length(), ForegroundColorSpan.class);

                        for (StrikethroughSpan span : strikeSpans) {
                            spannableString.removeSpan(span);
                        }
                        for (ForegroundColorSpan span : colorSpans) {
                            spannableString.removeSpan(span);
                        }
                    }
                    title_subtask2.setText(spannableString);
                    */


                    // Обработчик нажатия на кнопку удаления
                    btnDelete.setOnClickListener(v -> {
                        newDocRef.get().addOnSuccessListener(documentSnapshot2 -> {
                            if (documentSnapshot2.exists()) {
                                // 3. Получаем текущий массив задач (или создаем новый, если его нет)
                                List<Map<String, Object>> subtasks2 = new ArrayList<>();

                                if (documentSnapshot2.contains("subtasks")) {
                                    // Если поле "tasks" существует, получаем его
                                    subtasks2 = (List<Map<String, Object>>) documentSnapshot2.get("subtasks");
                                }

                                Iterator<Map<String, Object>> iterator2 = subtasks2.iterator();
                                while (iterator2.hasNext()) {
                                    Map<String, Object> task2 = iterator2.next();
                                    if (task2.get("id") != null && task.get("id").equals(id_card2)) {
                                        iterator2.remove(); // Безопасное удаление
                                        break; // Если нужно удалить только первый найденный
                                    }
                                }
                                // 5. Обновляем документ с новым массивом
                                newDocRef.update("subtasks", subtasks2)
                                        .addOnSuccessListener(aVoid -> Log.d("Firestore", "Массив обновлен!"))
                                        .addOnFailureListener(e -> Log.e("Firestore", "Ошибка: " + e.getMessage()));
                            }
                        }).addOnFailureListener(e -> {
                            System.err.println("Ошибка при чтении документа: " + e.getMessage());
                        });

                        // Удаляем родительскую CardView из контейнера
                        if (newCard2.getParent() != null) {
                            ((ViewGroup) newCard2.getParent()).removeView(newCard2);
                        }
                    });


                    title_subtask2.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                            // Вызывается ДО изменения текста
                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            // Вызывается ВО ВРЕМЯ изменения текста
                            String temp_string = s.toString(); // Обновляем переменную
                            newDocRef.get().addOnSuccessListener(documentSnapshot3 -> {
                                if (documentSnapshot3.exists()) {
                                    // 3. Получаем текущий массив задач (или создаем новый, если его нет)
                                    List<Map<String, Object>> subtasks = new ArrayList<>();

                                    if (documentSnapshot3.contains("subtasks")) {
                                        // Если поле "tasks" существует, получаем его
                                        subtasks = (List<Map<String, Object>>) documentSnapshot.get("subtasks");
                                    }

                                    Iterator<Map<String, Object>> iterator3 = subtasks.iterator();
                                    while (iterator3.hasNext()) {
                                        Map<String, Object> task = iterator3.next();
                                        if (task.get("id") != null && task.get("id").equals(id_card2)) {
                                            task.put("title", temp_string);
                                            break; // Если нужно удалить только первый найденный
                                        }
                                    }
                                    // 5. Обновляем документ с новым массивом
                                    newDocRef.update("subtasks", subtasks)
                                            .addOnSuccessListener(aVoid -> Log.d("Firestore", "Массив обновлен!"))
                                            .addOnFailureListener(e -> Log.e("Firestore", "Ошибка: " + e.getMessage()));
                                }
                            }).addOnFailureListener(e -> {
                                System.err.println("Ошибка при чтении документа: " + e.getMessage());
                            });
                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                            // Вызывается ПОСЛЕ изменения текста
                        }
                    });


                    checkBoxSubtask2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                            if (isChecked) {
                                // Зачеркиваем и делаем светлее
                                title_subtask2.setPaintFlags(title_subtask2.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                                title_subtask2.setTextColor(ContextCompat.getColor(DataTask.this, R.color.light_gray));
                            } else {
                                // Возвращаем обычный текст
                                title_subtask2.setPaintFlags(title_subtask2.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                                title_subtask2.setTextColor(ContextCompat.getColor(DataTask.this, android.R.color.primary_text_light));
                            }

                            /*
                            String text = title_subtask2.getText().toString();
                            SpannableString spannableString = new SpannableString(text);
                            if (isChecked){
                                // Зачеркивание
                                spannableString.setSpan(new StrikethroughSpan(), 0, text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                                // Изменение цвета
                                spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#BDBDBD")),
                                        0, text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                            }else{
                                // Удаление всех стилей
                                StrikethroughSpan[] strikeSpans = spannableString.getSpans(0, text.length(), StrikethroughSpan.class);
                                ForegroundColorSpan[] colorSpans = spannableString.getSpans(0, text.length(), ForegroundColorSpan.class);

                                for (StrikethroughSpan span : strikeSpans) {
                                    spannableString.removeSpan(span);
                                }
                                for (ForegroundColorSpan span : colorSpans) {
                                    spannableString.removeSpan(span);
                                }
                            }
                            title_subtask2.setText(spannableString);

                             */

                            newDocRef.get().addOnSuccessListener(documentSnapshot -> {
                                if (documentSnapshot.exists()) {
                                    List<Map<String, Object>> subtasks = new ArrayList<>();

                                    if (documentSnapshot.contains("subtasks")) {
                                        subtasks = (List<Map<String, Object>>) documentSnapshot.get("subtasks");
                                    }

                                    // Ищем задачу с нужным ID и обновляем статус
                                    for (Map<String, Object> task : subtasks) {
                                        if (task.get("id") != null && task.get("id").equals(id_card2)) {
                                            task.put("done", isChecked); // Обновляем поле "done"
                                            break;
                                        }
                                    }

                                    // Обновляем документ в Firestore
                                    newDocRef.update("subtasks", subtasks)
                                            .addOnSuccessListener(aVoid -> Log.d("Firestore", "Массив обновлен!"))
                                            .addOnFailureListener(e -> Log.e("Firestore", "Ошибка: " + e.getMessage()));
                                }
                            }).addOnFailureListener(e -> {
                                Log.e("Firestore", "Ошибка чтения документа", e);
                                checkBoxSubtask2.setChecked(!isChecked);
                            });
                        }
                    });
                }

                // 5. Обновляем документ с новым массивом
                newDocRef.update("subtasks", subtasks)
                        .addOnSuccessListener(aVoid -> Log.d("Firestore", "Массив обновлен!"))
                        .addOnFailureListener(e -> Log.e("Firestore", "Ошибка: " + e.getMessage()));
            }
        }).addOnFailureListener(e -> {
            System.err.println("Ошибка при чтении документа: " + e.getMessage());
        });



        titleView.addTextChangedListener(new TextWatcher() {
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
                newDocRef.get().addOnSuccessListener(documentSnapshot3 -> {
                    if (documentSnapshot3.exists()) {
                        // 3. Получаем текущий массив задач (или создаем новый, если его нет)
                        //String t_string = documentSnapshot3.get("nameTask").toString();
//                        String t_string;
//                        t_string = titleView.getText().toString();

                        // 5. Обновляем документ с новым массивом
                        newDocRef.update("nameTask", temp_string)
                                .addOnSuccessListener(aVoid -> Log.d("Firestore", "Массив обновлен!"))
                                .addOnFailureListener(e -> Log.e("Firestore", "Ошибка: " + e.getMessage()));
                    }
                }).addOnFailureListener(e -> {
                    System.err.println("Ошибка при чтении документа: " + e.getMessage());
                });
            }
        });

        textDescription.addTextChangedListener(new TextWatcher() {
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
                newDocRef.get().addOnSuccessListener(documentSnapshot3 -> {
                    if (documentSnapshot3.exists()) {
                        // 3. Получаем текущий массив задач (или создаем новый, если его нет)
                        //String t_string = documentSnapshot3.get("nameTask").toString();
//                        String t_string;
//                        t_string = titleView.getText().toString();

                        // 5. Обновляем документ с новым массивом
                        newDocRef.update("description", temp_string)
                                .addOnSuccessListener(aVoid -> Log.d("Firestore", "Массив обновлен!"))
                                .addOnFailureListener(e -> Log.e("Firestore", "Ошибка: " + e.getMessage()));
                    }
                }).addOnFailureListener(e -> {
                    System.err.println("Ошибка при чтении документа: " + e.getMessage());
                });
            }
        });

    }

    public void returnBack(View view) {
        Intent intent = new Intent(DataTask.this, MainMenu2.class);
        intent.putExtra("must_draw", true);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_left_200, R.anim.slide_out_left_200);
    }

    public void addSubtask(View view) {
        CardView newCard1 = (CardView) LayoutInflater.from(this).inflate(R.layout.subtask_card, containerSubtasks, false);

        // Устанавливаем ID
        // Создаем объект Random
        Random random = new Random();
        newCard1.setId(random.nextInt(999999));
        String id_card1 = String.valueOf(newCard1.getId());

        containerSubtasks.addView(newCard1);

        // Находим кнопку удаления внутри CardView
        ImageButton btnDelete = newCard1.findViewById(R.id.buttonDelete);
        btnDelete.setId(random.nextInt(999999));

        EditText title_subtask = findViewById(R.id.title_subtask);
        title_subtask.setId(random.nextInt(999999));
        //title_subtask.setId(Integer.parseInt("title_subtask"+String.valueOf(random.nextInt(999999))));

        CheckBox checkBoxSubtask1 = findViewById(R.id.checkBoxSubtask);
        checkBoxSubtask1.setId(random.nextInt(999999));
        //checkBoxSubtask1.setId(Integer.parseInt("checkBoxSubtask"+String.valueOf(random.nextInt(999999))));

        // Обработчик нажатия на кнопку удаления
        btnDelete.setOnClickListener(v -> {
            newDocRef.get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    // 3. Получаем текущий массив задач (или создаем новый, если его нет)
                    List<Map<String, Object>> subtasks = new ArrayList<>();

                    if (documentSnapshot.contains("subtasks")) {
                        // Если поле "tasks" существует, получаем его
                        subtasks = (List<Map<String, Object>>) documentSnapshot.get("subtasks");
                    }

                    Iterator<Map<String, Object>> iterator = subtasks.iterator();
                    while (iterator.hasNext()) {
                        Map<String, Object> task = iterator.next();
                        if (task.get("id") != null && task.get("id").equals(id_card1)) {
                            iterator.remove(); // Безопасное удаление
                            break; // Если нужно удалить только первый найденный
                        }
                    }
                    // 5. Обновляем документ с новым массивом
                    newDocRef.update("subtasks", subtasks)
                            .addOnSuccessListener(aVoid -> Log.d("Firestore", "Массив обновлен!"))
                            .addOnFailureListener(e -> Log.e("Firestore", "Ошибка: " + e.getMessage()));
                }
            }).addOnFailureListener(e -> {
                System.err.println("Ошибка при чтении документа: " + e.getMessage());
            });

            // Удаляем родительскую CardView из контейнера
            if (newCard1.getParent() != null) {
                ((ViewGroup) newCard1.getParent()).removeView(newCard1);
            }
        });

        newDocRef = db.collection("tasks").document(id_task);

        newDocRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                // 3. Получаем текущий массив задач (или создаем новый, если его нет)
                List<Map<String, Object>> subtasks = new ArrayList<>();

                if (documentSnapshot.contains("subtasks")) {
                    // Если поле "tasks" существует, получаем его
                    subtasks = (List<Map<String, Object>>) documentSnapshot.get("subtasks");
                }

                // 4. Создаем новую задачу (Map)
                Map<String, Object> newTask = new HashMap<>();
                newTask.put("title", "");
                newTask.put("done", "false");
                newTask.put("id", id_card1);
                subtasks.add(newTask);

                // 5. Обновляем документ с новым массивом
                newDocRef.update("subtasks", subtasks)
                        .addOnSuccessListener(aVoid -> Log.d("Firestore", "Массив обновлен!"))
                        .addOnFailureListener(e -> Log.e("Firestore", "Ошибка: " + e.getMessage()));
            }
        }).addOnFailureListener(e -> {
            System.err.println("Ошибка при чтении документа: " + e.getMessage());
        });


        title_subtask.addTextChangedListener(new TextWatcher() {
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
                newDocRef.get().addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // 3. Получаем текущий массив задач (или создаем новый, если его нет)
                        List<Map<String, Object>> subtasks = new ArrayList<>();

                        if (documentSnapshot.contains("subtasks")) {
                            // Если поле "tasks" существует, получаем его
                            subtasks = (List<Map<String, Object>>) documentSnapshot.get("subtasks");
                        }

                        Iterator<Map<String, Object>> iterator = subtasks.iterator();
                        while (iterator.hasNext()) {
                            Map<String, Object> task = iterator.next();
                            if (task.get("id") != null && task.get("id").equals(id_card1)) {
                                task.put("title", temp_string);
                                break; // Если нужно удалить только первый найденный
                            }
                        }
                        // 5. Обновляем документ с новым массивом
                        newDocRef.update("subtasks", subtasks)
                                .addOnSuccessListener(aVoid -> Log.d("Firestore", "Массив обновлен!"))
                                .addOnFailureListener(e -> Log.e("Firestore", "Ошибка: " + e.getMessage()));
                    }
                }).addOnFailureListener(e -> {
                    System.err.println("Ошибка при чтении документа: " + e.getMessage());
                });
            }
        });


        checkBoxSubtask1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    // Зачеркиваем и делаем светлее
                    title_subtask.setPaintFlags(title_subtask.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    title_subtask.setTextColor(ContextCompat.getColor(DataTask.this, R.color.light_gray));
                } else {
                    // Возвращаем обычный текст
                    title_subtask.setPaintFlags(title_subtask.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                    title_subtask.setTextColor(ContextCompat.getColor(DataTask.this, android.R.color.primary_text_light));
                }

                newDocRef.get().addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        List<Map<String, Object>> subtasks = new ArrayList<>();

                        if (documentSnapshot.contains("subtasks")) {
                            subtasks = (List<Map<String, Object>>) documentSnapshot.get("subtasks");
                        }

                        // Ищем задачу с нужным ID и обновляем статус
                        for (Map<String, Object> task : subtasks) {
                            if (task.get("id") != null && task.get("id").equals(id_card1)) {
                                System.out.println(task.get("id")+"aaaaaa");
                                task.put("done", isChecked); // Обновляем поле "done"
                                break;
                            }
                        }

                        // Обновляем документ в Firestore
                        newDocRef.update("subtasks", subtasks)
                                .addOnSuccessListener(aVoid -> Log.d("Firestore", "Массив обновлен!"))
                                .addOnFailureListener(e -> Log.e("Firestore", "Ошибка: " + e.getMessage()));
                    }
                }).addOnFailureListener(e -> {
                    Log.e("Firestore", "Ошибка чтения документа", e);
                    checkBoxSubtask1.setChecked(!isChecked);
                });
            }
        });
    }

    private void showMembersDialog() {
        // Реализация показа участников
        Toast.makeText(this, "Показать участников", Toast.LENGTH_SHORT).show();
    }

    private void confirmDelete() {
        new AlertDialog.Builder(this)
                .setTitle("Подтверждение")
                .setMessage("Вы уверены, что хотите удалить?")
                .setPositiveButton("Удалить", (dialog, which) -> {
                    // Действие при удалении

                    db.collection("tasks")
                            .document(id_task)
                            .get()
                            .addOnSuccessListener(documentSnapshot -> {
                                if (documentSnapshot.exists()) {
                                    // Документ существует - удаляем
                                    documentSnapshot.getReference().delete();
                                } else {
                                    Log.d("Firestore", "Документ не найден");
                                }
                            });

                    Toast.makeText(this, "Удалено", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(DataTask.this, MainMenu2.class);
                    intent.putExtra("must_draw", true);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_left_200, R.anim.slide_out_left_200);
                })
                .setNegativeButton("Отмена", null)
                .show();
    }
}