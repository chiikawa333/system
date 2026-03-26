package com.intelligent.driver.exception;

import com.intelligent.driver.responce.ResponseCode;
import lombok.Data;

@Data
public class BussinessException extends RuntimeException{
    private Integer code;
    private String message;

    public BussinessException(Integer code,String message){
        this.code = code;
        this.message = message;
    }
    public BussinessException(String message){
        this.message = message;
    }
    public BussinessException(ResponseCode responseCode){
        this.code = responseCode.getCode();
        this.message = responseCode.getMessage();
    }
}
