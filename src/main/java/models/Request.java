package models;

import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.io.Reader;

@Getter
@Setter
public class Request {
    private String method;
    private String urlPath;
    private Reader in;

    public Request(Reader in) {
        this.in = in;
    }

    public String parse() throws IOException {
        StringBuilder requestLine = new StringBuilder();
        while (true) {
            int c = in.read();
            if (c == '\r' || c == '\n') break;
            requestLine.append((char) c);
        }
        String request = requestLine.toString();

        String[] tokens = requestLine.toString().split("\\s+");
        setMethod(tokens[0]);
        setUrlPath(tokens[1]);

        return request;
    }
}
