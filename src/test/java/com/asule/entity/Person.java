package com.asule.entity;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * created by asule on 2020-04-24 16:58
 */
@Data
public class Person {

    private String name;
    private int age;
    private String address;
    private List<String> names;

//    private Date createTime;
//    private Date updateTime;


    public Person(String name, int age) {
        this.name = name;
        this.age = age;
    }

    public Person(String name, int age, String address) {
        this.name = name;
        this.age = age;
        this.address = address;
    }

    public Person(String name, int age, String address, List<String> names) {
        this.name = name;
        this.age = age;
        this.address = address;
        this.names = names;
    }

    public Person() {
    }



}
