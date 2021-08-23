package com.wifi.xcracker.task;

public enum RequestMethod {
    GET("GET"),
    POST("POST"),
    HEAD("HEAD"),
    DELETE("DELETE");

    private String value;
    RequestMethod(String value){
        this.value=value;
    }

    public String value(){
        return value;
    };
    @Override
    public String toString() {
        return value;
    }

    public boolean isOutputMethod()
    {
        switch (this){
            case POST:
            case DELETE:
                return true;
            default:
                return false;
        }
    }
}
