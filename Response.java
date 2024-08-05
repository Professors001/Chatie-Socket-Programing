import java.io.Serializable;

public class Response implements Serializable {
    private String statusCode;
    private Object data;

    public Response(String statusCode, Object data) {
        this.statusCode = statusCode;
        this.data = data;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "Response{" +
                "statusCode=" + statusCode +
                ", data=" + data +
                '}';
    }
}