package com.atguigu.aiservice.param;

import lombok.Data;

@Data
public class Response<T>
{
    private T data;
    private String message;
    private Integer code;

    public static <T> Response<T> success(T data)
    {
        Response<T> response = new Response<>();
        response.setData(data);
        response.setCode(200);
        response.setMessage("success");
        return response;
    }

    public static <T> Response<T> error(String message)
    {
        Response<T> response = new Response<>();
        response.setCode(500);
        response.setMessage(message);
        return response;
    }
}
