package com.example.luke_.pubquiz;

public class Team {

    private String id;

    private String name;

    // static variable single_instance of type Singleton
    private static Team instance = null;

    private Team() {}

    // static method to create instance of Singleton class
    public static Team getInstance()
    {
        if (instance == null)
            instance = new Team();
        return instance;
    }

    public void init(String id, String name){
        this.id = id;
        this.name = name;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}