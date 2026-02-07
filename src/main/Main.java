package main;

import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;
import main.http.ScheduleHandler;

public class Main {

    private static final int DEFAULT_PORT = 8080;

    public static void main(String[] args) throws IOException {
        int port = resolvePort(args);
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/api/schedule", new ScheduleHandler());
        server.setExecutor(null);
        server.start();
        System.out.println("Scheduler API running on port " + port);
    }

    private static int resolvePort(String[] args) {
        if (args == null || args.length == 0) {
            return DEFAULT_PORT;
        }
        try {
            return Integer.parseInt(args[0]);
        } catch (NumberFormatException ex) {
            System.err.println("Invalid port provided. Falling back to " + DEFAULT_PORT);
            return DEFAULT_PORT;
        }
    }
}
