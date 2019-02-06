package com.example.luke_.pubquiz;

public class Question {

    private int id;
    private String question;
    private int time;

    public Question(int id, String question, int time) {
        this.id = id;
        this.question = question;
        this.time = time;
    }

    public int getId() {
        return id;
    }

    public String getQuestion() {
        return question;
    }

    public int getTime() {
        return time;
    }


}
