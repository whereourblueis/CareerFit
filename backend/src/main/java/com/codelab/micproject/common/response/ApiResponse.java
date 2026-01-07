package com.codelab.micproject.common.response;


import lombok.*;


@Getter @AllArgsConstructor @NoArgsConstructor @Builder
public class ApiResponse<T> {
    private boolean success;
    private String message;
    private T data;


    public static <T> ApiResponse<T> ok(T data){
        return ApiResponse.<T>builder().success(true).message("OK").data(data).build();
    }
    public static ApiResponse<Void> ok(){
        return ApiResponse.<Void>builder().success(true).message("OK").build();
    }
    public static ApiResponse<Void> error(String msg){
        return ApiResponse.<Void>builder().success(false).message(msg).build();
    }
}