package org.example.pojo;

import lombok.Data;

/**
 * 后端统一响应体，所有 Controller 接口均返回此结构。
 *
 * <pre>
 * 成功：{"code":1, "msg":"success", "data":{...}}
 * 失败：{"code":0, "msg":"错误原因", "data":null}
 * </pre>
 */
@Data
public class Result {

    /** 状态码：1=成功，0=失败 */
    private Integer code;
    /** 提示消息 */
    private String msg;
    /** 响应数据，可能为 null */
    private Object data;

    /** 无数据成功响应 */
    public static Result success() {
        Result result = new Result();
        result.code = 1;
        result.msg = "success";
        return result;
    }

    /**
     * 携带数据的成功响应
     *
     * @param object 响应数据（实体/列表/Map 等）
     */
    public static Result success(Object object) {
        Result result = new Result();
        result.code = 1;
        result.msg = "success";
        result.data = object;
        return result;
    }

    /**
     * 错误响应
     *
     * @param msg 面向用户的错误提示
     */
    public static Result error(String msg) {
        Result result = new Result();
        result.code = 0;
        result.msg = msg;
        return result;
    }
}
