package com.ebiggerr.sims.DTO;

import com.ebiggerr.sims.DTO.token.TokenDto;
import org.springframework.http.HttpStatus;

import java.io.Serializable;

public class API_RESPONSE implements Serializable {

    private int status;
    private String message;
    private Object data;


    private final String SUCCESS_MSG="Successful";
    private final String SUCCESS_OP_MSG="Successful operation";
    private final String UNAUTHORIZED_MSG="Unauthorized";
    private final String INVALID_CREDENTIALS="Invalid Credentials";
    private final String NO_RECORD_FOUND="No Record Found";
    private final String ERROR="Something is wrong";

    public API_RESPONSE(){
    }

    public int getStatus(){
        return status;
    }

    public void setStatus(int code){
        this.status = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public API_RESPONSE Success(Object data){

        API_RESPONSE response=new API_RESPONSE();
        response.setStatus(HttpStatus.OK.value());
        response.setMessage(SUCCESS_MSG);
        response.setData(data);

        return response;
    }

    public API_RESPONSE Success(TokenDto token){

        API_RESPONSE response=new API_RESPONSE();
        response.setStatus(HttpStatus.OK.value());
        response.setMessage(SUCCESS_MSG);
        response.setData(token);

        return response;
    }

    public API_RESPONSE Success(){

        API_RESPONSE response=new API_RESPONSE();
        response.setStatus(HttpStatus.OK.value());
        response.setMessage(SUCCESS_OP_MSG);
        response.setData(null);

        return response;
    }

    public API_RESPONSE Unauthorized(){

        API_RESPONSE response=new API_RESPONSE();
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setMessage(UNAUTHORIZED_MSG);
        response.setData(null);

        return response;

    }

    public API_RESPONSE Error(){

        API_RESPONSE response=new API_RESPONSE();
        response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        response.setMessage(ERROR);
        response.setData(null);

        return response;
    }

    public API_RESPONSE NotFound(String exceptionMessage) {

        API_RESPONSE response=new API_RESPONSE();
        response.setStatus(HttpStatus.OK.value());
        response.setMessage(exceptionMessage);
        response.setData(null);

        return response;
    }
}
