package entity;

import lombok.Data;

@Data
public class Result<T> {
    private boolean flag;
    private Integer code;
    private String message;
    private T data;

    public Result() {
    }

    //适用于增删改
    public Result(boolean flag, Integer code, String message) {
        this.flag = flag;
        this.code = code;
        this.message = message;
    }

    //适用于查询
    public Result(boolean flag, Integer code, String message, T data) {
        this.flag = flag;
        this.code = code;
        this.message = message;
        this.data = data;
    }
}
