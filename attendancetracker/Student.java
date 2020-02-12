package com.example.attendancetracker;

import java.io.Serializable;

public class Student implements Serializable
{
    private String name, indicator;
    private boolean ifPresent;

    public Student(String name, String indicator) {
        this.name = name;
        this.indicator = indicator;
        ifPresent = false;
    }

    public Student(String name, String indicator, boolean ifPresent) {
        this.name = name;
        this.indicator = indicator;
        this.ifPresent = ifPresent;
    }

    public String getName() {
        return name;
    }

    public String getIndicator() {
        return indicator;
    }

    public boolean getIfPresent() {
        return ifPresent;
    }

    public void markPresent() {
        ifPresent = true;
    }
}