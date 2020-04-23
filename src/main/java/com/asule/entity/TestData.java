package com.asule.entity;

/**
 * created by asule on 2020-02-10 09:23
 */
public class TestData {

    private String name;
    private int age;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public TestData(String name, int age) {
        this.name = name;
        this.age = age;
    }
}
