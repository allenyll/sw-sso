package com.sw.sso.auth.server.util;

/**
 * @Description:  通用返回结果
 * @Author:       allenyll
 * @Date:         2020/6/1 7:07 下午
 * @Version:      1.0
 */
public class Result<T> {

    private static final long serialVersionUID = 1L;

    private T object;

    protected boolean success;

    protected  String code;

    protected String message;

    public Result() {
        this.success = true;
        this.code = BaseConstants.SUCCESS;
        this.message = "操作成功！";
    }

    public void fail(String message) {
        this.success = false;
        this.code = BaseConstants.FAIL;
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getObject() {
        return object;
    }

    public void setObject(T object) {
        this.object = object;
    }
}
