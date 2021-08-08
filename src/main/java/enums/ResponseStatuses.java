package enums;

public enum ResponseStatuses {
    SUCCESS(200, "OK"),
    NOT_FOUND(404, "File Not Found"),
    NOT_IMPLEMENTED(501, "Not Implemented");

    private int code;
    private String message;

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public String getResponse() {
        return code + " " + message;
    }

    ResponseStatuses(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
