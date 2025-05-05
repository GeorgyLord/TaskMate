package com.example.taskmate;

public class Task {
    /*
    private String name; // 0
    private String owner; // 1
    private Object tags; // 2
    private Object time; // 3
    private Object date; // 4
    private boolean deadline; // 5
    private long priotity; // 6
    private String date_and_time_of_creation; // 7

    public Task() {
        // Пустой конструктор нужен для Firebase
    }
    public Task(String n){
        name = n;
    }
    public Task(String date_and_time_of_creation, int priotity, boolean deadline, Object date, Object time, Object tags, String owner, String name) {
        this.date_and_time_of_creation = date_and_time_of_creation;
        this.priotity = priotity;
        this.deadline = deadline;
        this.date = date;
        this.time = time;
        this.tags = tags;
        this.owner = owner;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    */
    private String name; // 0
    private String owner; // 1
    private String tags; // 2
    private String time; // 3
    private String date; // 4
    private String deadline; // 5
    private String priotity; // 6
    private String date_and_time_of_creation;


    public Task() {

    }

    public Task(String name, String owner, String tags, String time, String date, String deadline, String priotity, String date_and_time_of_creation) {
        this.name = name;
        this.owner = owner;
        this.tags = tags;
        this.time = time;
        this.date = date;
        this.deadline = deadline;
        this.priotity = priotity;
        this.date_and_time_of_creation = date_and_time_of_creation;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
