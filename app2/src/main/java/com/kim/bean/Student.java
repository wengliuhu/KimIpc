package com.kim.bean;

import java.io.Serializable;

/**
 * @author : wengliuhu
 * @version : 0.1
 * @since : 2021/7/9 14:39
 * Describe：
 */
public class Student implements Serializable {
    String name;
    int age;

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
}
