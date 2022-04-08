package com.ebiggerr.sims.DTO;

public class Result {

    public boolean status;

    public String[] arr;

    public String message;

    public Result(){

    }

    public Result(boolean status, String[] arr, String message) {
        this.status = status;
        this.arr = arr;
        this.message = message;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public void setArr(String[] arr) {
        this.arr = arr;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
