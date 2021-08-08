import server.TaskServer;


public class Init {
    public static void main(String[] args) {
        TaskServer httpServer = new TaskServer();
        httpServer.start();
    }
}
