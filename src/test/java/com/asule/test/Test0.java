package com.asule.test;

import com.asule.common.ServerResponse;
import com.asule.entity.TestEntity;
import com.asule.entity.User;
import com.asule.utils.FTPUtil;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.ObjectNode;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class Test0 {


    @Before
    public void setup() {

    }


    //POJO转json字符串
    @Test
    public void test0() throws JsonProcessingException {

//        TestEntity testEntity = new TestEntity();
//        testEntity.setTestName("测试NAME");
//        testEntity.setTestDesc("测试DESC");
//
//        ObjectMapper objectMapper = new ObjectMapper();
//        String string = objectMapper.writeValueAsString(testEntity);
//
//        System.out.println(string);
    }


    //json转POJO
    @Test
    public void test1() throws IOException {

//        String testString = "{\"testName\":\"测试NAME\",\"testDesc\":\"测试DESC\"}";
//        ObjectMapper objectMapper = new ObjectMapper();
//
//        TestEntity entity = objectMapper.readValue(testString, TestEntity.class);
//
//        System.out.println(entity.toString());
    }

    @Test
    public void test2() throws IOException {

//        JsonNodeFactory instance = JsonNodeFactory.instance;
//        ObjectNode jsonNodes = instance.objectNode();
//
//        ArrayNode urlPath = jsonNodes.putArray("urlPath");
//        urlPath.add("A");
//        urlPath.add("B");
//        urlPath.add("C");
//
//        jsonNodes.put("name", "asule");
//        jsonNodes.put("age", 12);
//
//        System.out.println(jsonNodes.toString());
//
//        //  {"urlPath":["A","B","C"],"name":"asule","age":12}
    }

    @Test
    public void test3() throws IOException {
//        TestEntity testEntity = new TestEntity();
//        testEntity.setTestName("测试NAME");
//        testEntity.setTestDesc("测试DESC");
//        ServerResponse<TestEntity> serverResponse = new ServerResponse<>(1, "xxxx", testEntity);
//
//        ObjectMapper objectMapper = new ObjectMapper();
//        String string = objectMapper.writeValueAsString(serverResponse);
//
//        System.out.println(string);

    }

    @Test
    public void test4(){

//        String testStr="";
//
//        boolean blank = StringUtils.isBlank(testStr);
//        boolean notBlank = StringUtils.isNotBlank(testStr);
//        boolean empty = StringUtils.isEmpty(testStr);
//
//
//        System.out.println("blank="+blank);
//        System.out.println("notBlank="+notBlank);
//        System.out.println("empty="+empty);
    }


    private String path="D:\\studystart\\code\\firstwebsite\\target\\firstwebsite\\upload";

    @Test
    public void test5() throws IOException {

//        File targetFile = new File(path,"qr-1587528536642.png");
//        try {
//            FTPUtil.uploadFile(Lists.newArrayList(targetFile));
//        } catch (IOException e) {
//        }
    }



}
