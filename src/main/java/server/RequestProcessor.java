package server;

import enums.ResponseStatuses;
import lombok.SneakyThrows;
import models.Request;
import models.Task;
import services.TaskService;

import java.io.*;
import java.net.Socket;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


public class RequestProcessor implements Runnable {
    private final static Logger logger = Logger.getLogger(
            RequestProcessor.class.getCanonicalName());
    private Socket connection;

    public RequestProcessor(Socket connection) {
        this.connection = connection;
    }

    @Override
    public void run() {
        try {
            OutputStream raw = new BufferedOutputStream(
                    connection.getOutputStream()
            );
            Writer out = new OutputStreamWriter(raw);
            Reader in = new InputStreamReader(
                    new BufferedInputStream(
                            connection.getInputStream()
                    ), StandardCharsets.US_ASCII
            );

            Request request = new Request(in);
            String requestFirstLine = request.parse();
            logger.info(connection.getRemoteSocketAddress() + " " + requestFirstLine);

            String contentType = null;
            if (request.getMethod().equals("GET")) {
                contentType =
                        URLConnection.getFileNameMap().getContentTypeFor(TaskService.TASKS_FILE);

                logger.info(TaskService.TASKS.getCanonicalPath());

                if (TaskService.TASKS.canRead()
                        && TaskService.TASKS.getCanonicalPath().startsWith(TaskService.RESOURCE_DIR.getPath())) {
                    List<Task> tasks = TaskService.getAll();
                    if (request.getUrlPath().length() != 1) {
//                    if there is a param id along with urlpath
                        tasks = TaskService.getById(request.getUrlPath().substring(1), tasks);
                    }
                    logger.info(TaskService.printAll(tasks));

                    sendResponse(out, TaskService.printAll(tasks), ResponseStatuses.SUCCESS.getResponse(), contentType);
                } else {
                    sendResponse(out, ResponseStatuses.NOT_FOUND.getMessage(), ResponseStatuses.NOT_FOUND.getResponse(), contentType);
                }

            } else {
                sendResponse(out, ResponseStatuses.NOT_IMPLEMENTED.getMessage(), ResponseStatuses.NOT_IMPLEMENTED.getResponse(), contentType);
            }
        } catch (IOException ex) {
            logger.log(Level.WARNING,
                    "Error talking to " + connection.getRemoteSocketAddress(), ex);
        } finally {
            try {
                connection.close();
            } catch (IOException ex) {
            }
        }
    }


    @SneakyThrows(IOException.class)
    private void sendResponse(Writer out, String message, String responseCode, String contentType) {
        sendHeader(out, responseCode,
                contentType == null ? "text/plain" : contentType, message.length());
        out.write(message);
        out.flush();
    }

    @SneakyThrows(IOException.class)
    private void sendHeader(Writer out, String responseCode,
                            String contentType, int length) {
        out.write("HTTP/1.0 " + responseCode + "\r\n");
        Date now = new Date();
        out.write("Date: " + now + "\r\n");
        out.write("Server: TaskServer 2.0\r\n");
        out.write("Content-length: " + length + "\r\n");
        out.write("Content-type: " + contentType + "\r\n\r\n");
        out.flush();
    }
}
