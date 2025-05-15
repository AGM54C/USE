package com.example1.demo2.exception;

import ch.qos.logback.core.util.StringUtil;
import com.example1.demo2.pojo.dto.ResponseMessage;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

//全局报错器
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(Exception.class)
    public ResponseMessage handleException(Exception e) {
        // 检查异常是否为 MethodArgumentNotValidException（参数校验异常）
        if (e instanceof MethodArgumentNotValidException) {
            MethodArgumentNotValidException ex = (MethodArgumentNotValidException) e;
            // 获取第一个字段错误
            String errorMessage = ex.getBindingResult().getFieldError().getDefaultMessage();
            return ResponseMessage.error(errorMessage);
        } else {
            // 其他异常情况，返回原始的处理逻辑
            e.printStackTrace();
            return ResponseMessage.error(StringUtils.hasLength(e.getMessage())? e.getMessage() : "操作失败");
        }
    }
}
