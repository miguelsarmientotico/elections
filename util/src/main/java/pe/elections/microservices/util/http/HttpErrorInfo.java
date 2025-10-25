package pe.elections.microservices.util.http;

import java.time.ZonedDateTime;

import org.springframework.http.HttpStatus;

public class HttpErrorInfo {
    private final ZonedDateTime timestamp;
    private final String path;
    private final HttpStatus httpStatus;
    private final String message;

    public HttpErrorInfo(
        HttpStatus httpStatus,
        String path,
        String message
    ) {
        timestamp = ZonedDateTime.now();
        this.httpStatus = httpStatus;
        this.path = path;
        this.message = message;
    }

    public ZonedDateTime getTimestamp() {
        return timestamp;
    }
    public String getPath() {
        return path;
    }
    public HttpStatus getStatus() {
        return httpStatus;
    }
    public String getMessage() {
        return message;
    }
    public String getError() {
        return httpStatus.getReasonPhrase();
    }

}
