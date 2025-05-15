package com.example1.demo2.pojo.dto;

import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
public class ResponseMessage<T> {
    private String message;
    private Integer code;
    private T data;
    public ResponseMessage(String message, Integer code, T data) {
        this.message = message;
        this.code = code;
        this.data = data;
    }
    //请求接口成功
    public static <T>ResponseMessage success(T data){
        return new ResponseMessage("succssfully!",HttpStatus.OK.value(), data);
    }

    //请求接口失败
    public static  ResponseMessage error(String message){
        return new ResponseMessage(message,1,null);
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }


}
