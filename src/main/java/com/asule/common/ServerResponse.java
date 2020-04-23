package com.asule.common;


import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.io.Serializable;

//保证json序列化时，若有字段为null，不使其序列化。
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class ServerResponse<T> implements Serializable {

    private T data;

    private String message;

    private int status;


    public T getData() {
        return data;
    }


    public String getMessage() {
        return message;
    }


    public int getStatus() {
        return status;
    }

    public ServerResponse(int status) {
        this.status = status;
    }

    public ServerResponse(int status,String message) {
        this.message = message;
        this.status = status;
    }

    public ServerResponse(int status,String message,T data) {
        this.data = data;
        this.message = message;
        this.status = status;
    }


    public ServerResponse(int status,T data) {
        this.data = data;
        this.status = status;
    }


    //isXXX是boolean类型的get方法，若不处理会被json序列化。添加JsonIgnore，使其不在json序列化中。
    @JsonIgnore
    public boolean isSuccess(){
        return getStatus()==ResponseCode.SUCCESS.getCode();
    }

    public ServerResponse() {
    }

    //返回success数据

    public static ServerResponse createSuccess(){
        return new ServerResponse(ResponseCode.SUCCESS.getCode());
    }

    public static ServerResponse createSuccess(String message){
        return new ServerResponse(ResponseCode.SUCCESS.getCode(),message);
    }

    public static<T> ServerResponse createSuccessByData(T data){
        return new ServerResponse<T>(ResponseCode.SUCCESS.getCode(),data);
    }
    public static<T> ServerResponse createSuccessByData(String message,T data){
        return new ServerResponse<T>(ResponseCode.SUCCESS.getCode(),message,data);
    }
    
    public static ServerResponse createError(){
        return new ServerResponse(ResponseCode.ERROR.getCode());
    }

    public static ServerResponse createError(int code,String message){
        return new ServerResponse(code,message);
    }

    public static ServerResponse createError(String message){
        return new ServerResponse(ResponseCode.ERROR.getCode(),message);
    }

    public static<T> ServerResponse createErrorByData(String message,T data){
        return new ServerResponse<T>(ResponseCode.ERROR.getCode(),message,data);
    }

}
