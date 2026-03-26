package com.intelligent.driver.exception;

import cn.dev33.satoken.exception.NotLoginException;
import com.intelligent.driver.responce.R;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ControllerExceptionHandler {
    @ExceptionHandler(Exception.class)
    public R handException(Exception e){
        return R.fail(e.getMessage());
    }

    @ExceptionHandler(BussinessException.class)
    public R handlerBusinessException(BussinessException e){
        return R.fail(e.getCode(),e.getMessage());
    }

    @ExceptionHandler(NotLoginException.class)
    public R handlerNotLoginException(NotLoginException e){
        return R.fail(e.getCode(),e.getMessage());
    }
}
