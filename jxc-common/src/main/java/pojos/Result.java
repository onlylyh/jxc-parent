package pojos;

import java.io.Serializable;

public class Result implements Serializable {

    public boolean flag;//响应状态码
    public String message;//响应消息
    public Object obj;//响应数据

    public Result(boolean flag, String message) {
        this.flag = flag;
        this.message = message;
    }

    public Result(boolean flag, String message, Object obj) {
        this.flag = flag;
        this.message = message;
        this.obj = obj;
    }

    public boolean isFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getObj() {
        return obj;
    }

    public void setObj(Object obj) {
        this.obj = obj;
    }
}
