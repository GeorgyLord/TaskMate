package com.example.taskmate;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class TaskUsers extends AppCompatActivity {
    private LinearLayout main_container_users;
    // Инициализация Firestore
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String owner;
    private List<String> participants;
    private DocumentReference newDocRef;
    private String title;
    private String description;
    private String date;
    private String invitation_cod;
    public String id_task;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_task_users);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_users), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        title = getIntent().getStringExtra("task_title");
        description = getIntent().getStringExtra("task_description");
        date = getIntent().getStringExtra("task_date");
        id_task = getIntent().getStringExtra("id_task");
        invitation_cod = getIntent().getStringExtra("invitation_cod");

        SharedPreferences sharedPref_email = getSharedPreferences("data", Context.MODE_PRIVATE);
        String tempEmail = sharedPref_email.getString("email", "example@email.ru");

        Random random = new Random();


        main_container_users = findViewById(R.id.main_container_users);
        String id_task = getIntent().getStringExtra("id_task");

        newDocRef = db.collection("tasks").document(id_task);
        newDocRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                participants = (ArrayList<String>) documentSnapshot.get("participants");
                owner = documentSnapshot.getString("owner");

                CardView newCard_owner = (CardView) LayoutInflater.from(this).inflate(R.layout.user_card, main_container_users, false);
                ImageView icon_user_owner = newCard_owner.findViewById(R.id.icon_user);
                TextView name_user_owner = newCard_owner.findViewById(R.id.name_user);
                ImageButton delete_user_owner = newCard_owner.findViewById(R.id.delete_user);

                icon_user_owner.setId(random.nextInt(9999999));
                name_user_owner.setId(random.nextInt(9999999));
                delete_user_owner.setId(random.nextInt(9999999));


                delete_user_owner.setVisibility(View.GONE);
                name_user_owner.setText(owner);
                main_container_users.addView(newCard_owner);

                for (int i = 0; i < participants.size(); i++) {
                    CardView newCard = (CardView) LayoutInflater.from(this).inflate(R.layout.user_card, main_container_users, false);
                    ImageView icon_user = newCard.findViewById(R.id.icon_user);
                    TextView name_user = newCard.findViewById(R.id.name_user);
                    ImageButton delete_user = newCard.findViewById(R.id.delete_user);
                    icon_user.setId(random.nextInt(9999999));
                    name_user.setId(random.nextInt(9999999));
                    delete_user.setId(random.nextInt(9999999));

                    if(!tempEmail.equals(owner)){
                        delete_user.setVisibility(View.GONE);
                    }

                    name_user.setText(participants.get(i));

                    main_container_users.addView(newCard);
                    // Обработчик нажатия на кнопку удаления
                    delete_user.setOnClickListener(v -> {
                        DocumentReference newDocRef3 = db.collection("users").document(String.valueOf(name_user.getText()));
                        newDocRef3.get().addOnSuccessListener(documentSnapshot2 -> {
                            if (documentSnapshot2.exists()) {
                                ArrayList<String> participants3 = (ArrayList<String>) documentSnapshot2.get("id_of_tasks");
                                participants3.remove(id_task);

                                newDocRef3.update("id_of_tasks", participants3)
                                        .addOnSuccessListener(aVoid -> Log.d("Firestore", "Массив обновлен!"))
                                        .addOnFailureListener(e -> Log.e("Firestore", "Ошибка: " + e.getMessage()));
                            }
                        }).addOnFailureListener(e -> {
                            System.err.println("Ошибка при чтении документа: " + e.getMessage());
                        });

                        newDocRef.get().addOnSuccessListener(documentSnapshot2 -> {
                            if (documentSnapshot2.exists()) {
                                ArrayList<String> participants2 = (ArrayList<String>) documentSnapshot2.get("participants");
                                participants2.remove(String.valueOf(name_user.getText()));

                                newDocRef.update("participants", participants2)
                                        .addOnSuccessListener(aVoid -> Log.d("Firestore", "Массив обновлен!"))
                                        .addOnFailureListener(e -> Log.e("Firestore", "Ошибка: " + e.getMessage()));
                            }
                        }).addOnFailureListener(e -> {
                            System.err.println("Ошибка при чтении документа: " + e.getMessage());
                        });

                        // Удаляем родительскую CardView из контейнера
                        if (newCard.getParent() != null) {
                            ((ViewGroup) newCard.getParent()).removeView(newCard);
                        }
                    });
                }
            }
        }).addOnFailureListener(e -> {
            System.err.println("Ошибка при чтении документа: " + e.getMessage());
        });
    }

    public void back_data_task(View view) {
        Intent intent = new Intent(TaskUsers.this, DataTask.class);
        intent.putExtra("id_task", id_task);
        intent.putExtra("title", title);
        intent.putExtra("description", description);
        intent.putExtra("date", date);
        intent.putExtra("invitation_cod", invitation_cod);
        //intent.putExtra("id_task", id_task);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_left_200, R.anim.slide_out_left_200);
    }
}