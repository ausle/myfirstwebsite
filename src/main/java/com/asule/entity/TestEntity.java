package com.asule.entity;

public class TestEntity {

    private String testName;
    private String testDesc;

    public String getTestName() {
        return testName;
    }

    public void setTestName(String testName) {
        this.testName = testName;
    }

    public String getTestDesc() {
        return testDesc;
    }

    public void setTestDesc(String testDesc) {
        this.testDesc = testDesc;
    }

    @Override
    public String toString() {
        return "TestEntity{" +
                "testName='" + testName + '\'' +
                ", testDesc='" + testDesc + '\'' +
                '}';
    }
}
