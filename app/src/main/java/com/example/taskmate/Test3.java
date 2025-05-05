package com.example.taskmate;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Source;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Test3 extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    DocumentReference newDocRef;
    private Runnable runnable;
    private Handler handler = new Handler();

    private ArrayList<Task> list_tasks = new ArrayList<>();
    private ArrayList<Task> new_list_tasks;
    private SharedPreferences sharedPref = getSharedPreferences("tasks", Context.MODE_PRIVATE);
    private SharedPreferences.Editor editor = sharedPref.edit();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_test3);


        // Определяем задачу, которая будет выполняться каждые 5 секунд
        runnable = new Runnable() {
            @Override
            public void run() {
                // Вызываем вашу функцию
                //update();

                // Повторяем задачу через 5 секунд
                handler.postDelayed(this, 5000); // 5000 миллисекунд = 5 секунд
            }
        };

        // Запускаем задачу
        //handler.post(runnable);


        drawerLayout = findViewById(R.id.drawer_layout);
        //Button btnOpenMenu = findViewById(R.id.btn_open_menu);
        ImageButton imageButton = findViewById(R.id.imageButton);

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(findViewById(R.id.nav_view));
            }
        });
        /*
        btnOpenMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(findViewById(R.id.nav_view));
            }
        });
        */

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.item_home) {
                // Нажали "Главная"
            } else if (id == R.id.item_categories) {
                // Нажали "Категории"
            } else if (id == R.id.item_settings) {
                // Нажали "Настройки"
            } else if (id == R.id.item_logout) {
                // Нажали "Выход"
                drawerLayout.closeDrawers();
            }
            return true;
        });
        /*
        Intent intent = getIntent();
        if (intent.hasExtra("cards_data")) {
            ArrayList<String[]> receivedData = (ArrayList<String[]>) intent.getSerializableExtra("cards_data");

            //LinearLayout container = findViewById(R.id.container_card);
            //LayoutInflater inflater = getLayoutInflater();

            LinearLayout card = (LinearLayout) inflater.inflate(R.layout.copy_card, container, false);
            card.setId(View.generateViewId());
            container.addView(card);
            //View myView = getLayoutInflater().inflate(R.layout.copy_card, container, false);
            //container.addView(myView); // ✅ Теперь добавятся обе

        }*/
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Удаляем задачу при уничтожении активности, чтобы избежать утечек памяти
        handler.removeCallbacks(runnable);
    }

    public void update(){
        System.out.println("UPDATE");
        //System.out.println("SIZE "+list_tasks.size());
        //Toast.makeText(Test3.this, "SIZE "+list_tasks.size(), Toast.LENGTH_SHORT).show();
        LinearLayout container = findViewById(R.id.container_card);
        new_list_tasks = new ArrayList<>();
        ArrayList<String> ls = new ArrayList<>();
        container.removeAllViews();
        //new_list = new ArrayList<>();

        SharedPreferences sharedPref = getSharedPreferences("data", Context.MODE_PRIVATE);
        String id_user = sharedPref.getString("id", "0");
        System.out.println(id_user);

        //SharedPreferences shared = getSharedPreferences("data_tasks", Context.MODE_PRIVATE);
        //SharedPreferences.Editor editor2 = shared.edit();

        DocumentReference userRef = db.collection("users").document(id_user);
        userRef.get().addOnSuccessListener(documentSnapshot -> {
            //if (documentSnapshot.exists()) {
                // Получаем массив как List<String>
                List<String> list = documentSnapshot.toObject(User.class).id_of_tasks;
                System.out.println("c1");

                if (list_tasks.size() != list.size() || true) {
                    for (int i = 0; i < list.size(); i++) {
                        DocumentReference taskRef = db.collection("tasks").document(list.get(i));

                        //Map<String, Object> userData = documentSnapshot.getData();
                        //Task task1 = new Task("taskName");
                        //list_tasks.add(task1);
                        taskRef.get().addOnSuccessListener(documentSnapshot2 -> {
                            if (documentSnapshot2.exists()) {
                                // Получаем всё содержимое документа как Map
                                //Map<String, Object> taskData = documentSnapshot2.getData();
                                Task new_task = documentSnapshot2.toObject(Task.class);
                                new_list_tasks.add(new_task);
                                //Toast.makeText(Test3.this, new_task.getName(), Toast.LENGTH_SHORT).show();
                                // Теперь можем работать с полями:
                                //String name = (String) taskData.get("name");
                                //editor2.putString("name", name);
                                //editor2.apply();

                            }
                        }).addOnFailureListener(e -> {
                            System.err.println("Ошибка загрузки данных: " + e.getMessage());
                        });
                        System.out.println("2)");
                        System.out.println(new_list_tasks.size());

                        LayoutInflater inflater = getLayoutInflater();
                        LinearLayout card = (LinearLayout) inflater.inflate(R.layout.copy_card, container, false);
                        card.setId(View.generateViewId());
                        TextView textView4 = card.findViewById(R.id.textView4);
                        SharedPreferences shared3 = getSharedPreferences("data_tasks", Context.MODE_PRIVATE);
                        String savedText = shared3.getString("name", "");
                        textView4.setText(savedText);
                        container.addView(card);
                        //View myView = getLayoutInflater().inflate(R.layout.copy_card, container, false);
                        //container.addView(myView); // ✅ Теперь добавятся обе
                    }
               // }
            }
        }).addOnFailureListener(e -> {
            System.err.println("Ошибка при чтении документа: " + e.getMessage());
        });
        /*
        if(list_tasks.size() != new_list_tasks.size()) {
            Toast.makeText(Test3.this, list_tasks.size()+"Есть что-то новое"+new_list_tasks.size(), Toast.LENGTH_SHORT).show();
            list_tasks = new_list_tasks;
        }
        else{
            Toast.makeText(Test3.this, list_tasks.size()+" "+new_list_tasks.size(), Toast.LENGTH_SHORT).show();
        }*/
    }

    public void new_task(View view) {
        Intent intent = new Intent(Test3.this, AddNewTask.class);
        startActivity(intent);
    }

    public void log_out_of_account(MenuItem item) {
        System.out.println("EXIT");
        // Получаем экземпляр FirebaseAuth
        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        // Выход из аккаунта
        mAuth.signOut();

        Intent intent = new Intent(Test3.this, LoginActivity.class);
        startActivity(intent);
    }
}