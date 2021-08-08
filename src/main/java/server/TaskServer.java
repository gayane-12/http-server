package server;

import lombok.SneakyThrows;

import java.io.*;
import java.net.*;
import java.util.concurrent.*;
import java.util.logging.*;

public class TaskServer {
    private static final Logger logger = Logger.getLogger(
            TaskServer.class.getCanonicalName());
    private static final int NUM_THREADS = 50;
    private static final int PORT = 80;

    public TaskServer() {
    }

    @SneakyThrows(IOException.class)
    public void start() {
        ExecutorService pool = Executors.newFixedThreadPool(NUM_THREADS);
        try (ServerSocket server = new ServerSocket(PORT)) {
            logger.info("Accepting connections on port " + server.getLocalPort());
            while (true) {
                try {
                    Socket request = server.accept();
                    Runnable r = new RequestProcessor(request);
                    pool.submit(r);
                } catch (IOException ex) {
                    logger.log(Level.WARNING, "Error accepting connection", ex);
                }
            }
        }
    }

}
