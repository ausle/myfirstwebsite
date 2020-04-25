package com.asule.test;

import com.asule.common.RedisPool;
import com.asule.entity.Person;
import com.asule.entity.User;
import com.asule.utils.JsonUtil;
import com.asule.utils.RedisPoolUtil;
import org.apache.avro.data.Json;
import org.apache.avro.util.internal.JacksonUtils;
import org.codehaus.jackson.type.TypeReference;
import org.junit.Assert;
import org.junit.Test;
import redis.clients.jedis.Jedis;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * created by asule on 2020-04-24 16:41
 */


public class Test1 {


    @Test
    public void redisTest(){
        Jedis jedis = RedisPool.getJedis();
        RedisPoolUtil.set("keyTest","value");
        String value = RedisPoolUtil.get("keyTest");

        RedisPoolUtil.setEx("keyex","valueex",60*10);

        RedisPoolUtil.expire("keyTest",60*20);

        RedisPoolUtil.del("keyTest");
    }


    @Test
    public void jacksonTest(){
        List<String> names= new ArrayList<>();
//        Person person = new Person("asule", 28,"11",names);

        Person person=new Person();
//        person.setCreateTime(new Date());
        String obj2String = JsonUtil.obj2StringPretty(person);
//        String obj2String="{\"name\":\"asule\",\"age\":28,\"address\":\"11\",\"names\":[],\"createTime\":\"2020-04-24 17:23:20\",\"updateTime\":null}\n";

        System.out.println(obj2String);

//        person=JsonUtil.string2Obj(obj2String,Person.class);
//        System.out.println(person.toString());
    }

    @Test
    public void jacksonTest1(){

        List<Person> personList=new ArrayList();
        Person person1 = new Person("asule", 28,"11",new ArrayList());
        Person person2= new Person("asule", 28,"11",new ArrayList());
        personList.add(person1);
        personList.add(person2);

        String obj2String = JsonUtil.obj2String(personList);
        System.out.println(obj2String);

        List list = JsonUtil.string2Obj(obj2String, new TypeReference<List>() {

        });
        System.out.println(list);


    }


}
