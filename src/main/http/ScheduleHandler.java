package main.http;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import main.Process;
import main.Scheduler;
import main.SchedulerFactory;

public class ScheduleHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        addCorsHeaders(exchange.getResponseHeaders());

        String method = exchange.getRequestMethod();
        if ("OPTIONS".equalsIgnoreCase(method)) {
            sendResponse(exchange, 204, "");
            return;
        }

        if (!"POST".equalsIgnoreCase(method)) {
            sendResponse(exchange, 405, errorMessage("Method not allowed"));
            return;
        }

        String body = readBody(exchange.getRequestBody());
        try {
            Map<String, Object> payload = castToMap(Json.parse(body));
            String algorithm = toStringValue(payload.get("algorithm"));
            Integer timeQuantum = toInteger(payload.get("timeQuantum"));
            List<Object> rawProcesses = castToList(payload.get("processes"));
            List<Process> processes = toProcesses(rawProcesses);

            Scheduler scheduler = SchedulerFactory.create(algorithm, timeQuantum);
            List<Process> scheduled = cloneList(processes);
            scheduler.schedule(scheduled);

            Map<String, Object> response = buildResponse(scheduled);
            sendResponse(exchange, 200, Json.stringify(response));
        } catch (IllegalArgumentException ex) {
            sendResponse(exchange, 400, errorMessage(ex.getMessage()));
        } catch (Exception ex) {
            sendResponse(exchange, 500, errorMessage("Unexpected server error"));
            ex.printStackTrace();
        }
    }

    private void addCorsHeaders(Headers headers) {
        headers.set("Access-Control-Allow-Origin", "*");
        headers.set("Access-Control-Allow-Methods", "POST, OPTIONS");
        headers.set("Access-Control-Allow-Headers", "Content-Type");
        headers.set("Access-Control-Max-Age", "300");
    }

    private String readBody(InputStream input) throws IOException {
        byte[] data = input.readAllBytes();
        return new String(data, StandardCharsets.UTF_8);
    }

    private Map<String, Object> buildResponse(List<Process> processes) {
        List<Object> serialized = new ArrayList<>();
        long totalWaiting = 0;
        long totalTurnaround = 0;

        for (Process p : processes) {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("pid", p.pid);
            row.put("arrivalTime", p.arrivalTime);
            row.put("burstTime", p.burstTime);
            row.put("priority", p.priority);
            row.put("waitingTime", p.waitingTime);
            row.put("turnaroundTime", p.turnaroundTime);
            row.put("startTime", p.startTime);
            row.put("completionTime", p.completionTime);
            serialized.add(row);
            totalWaiting += p.waitingTime;
            totalTurnaround += p.turnaroundTime;
        }

        int count = processes.size();
        double avgWaiting = count == 0 ? 0 : (double) totalWaiting / count;
        double avgTurnaround = count == 0 ? 0 : (double) totalTurnaround / count;

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("processes", serialized);
        response.put("averageWaitingTime", round(avgWaiting));
        response.put("averageTurnaroundTime", round(avgTurnaround));
        return response;
    }

    private double round(double value) {
        return Double.parseDouble(String.format(Locale.US, "%.4f", value));
    }

    private List<Process> cloneList(List<Process> original) {
        List<Process> copy = new ArrayList<>();
        for (Process p : original) {
            copy.add(p.copy());
        }
        return copy;
    }

    private List<Process> toProcesses(List<Object> rawProcesses) {
        if (rawProcesses == null) {
            throw new IllegalArgumentException("Processes array is required");
        }
        List<Process> processes = new ArrayList<>();
        int index = 0;
        for (Object obj : rawProcesses) {
            Map<String, Object> node = castToMap(obj);
            int pid = toIntegerOrDefault(node.get("pid"), index + 1);
            int arrival = requireInt(node.get("arrivalTime"), "arrivalTime");
            int burst = requirePositiveInt(node.get("burstTime"), "burstTime");
            int priority = toIntegerOrDefault(node.get("priority"), 0);
            processes.add(new Process(pid, arrival, burst, priority));
            index++;
        }
        return processes;
    }

    private int requireInt(Object value, String field) {
        Integer iv = toInteger(value);
        if (iv == null) {
            throw new IllegalArgumentException("Missing required field '" + field + "'");
        }
        return iv;
    }

    private int requirePositiveInt(Object value, String field) {
        int iv = requireInt(value, field);
        if (iv <= 0) {
            throw new IllegalArgumentException("Field '" + field + "' must be greater than zero");
        }
        return iv;
    }

    private Integer toInteger(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        if (value instanceof String) {
            return Integer.parseInt((String) value);
        }
        throw new IllegalArgumentException("Expected numeric value but found " + value.getClass());
    }

    private int toIntegerOrDefault(Object value, int defaultValue) {
        Integer iv = toInteger(value);
        return iv == null ? defaultValue : iv;
    }

    private String toStringValue(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof String) {
            return (String) value;
        }
        return String.valueOf(value);
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> castToMap(Object value) {
        if (value instanceof Map) {
            return (Map<String, Object>) value;
        }
        throw new IllegalArgumentException("Expected object but found " + typeName(value));
    }

    @SuppressWarnings("unchecked")
    private List<Object> castToList(Object value) {
        if (value instanceof List) {
            return (List<Object>) value;
        }
        throw new IllegalArgumentException("Expected array but found " + typeName(value));
    }

    private String typeName(Object value) {
        return value == null ? "null" : value.getClass().getSimpleName();
    }

    private String errorMessage(String message) {
        Map<String, Object> error = new LinkedHashMap<>();
        error.put("error", message == null ? "Unknown error" : message);
        return Json.stringify(error);
    }

    private void sendResponse(HttpExchange exchange, int status, String body) throws IOException {
        byte[] bytes = body == null ? new byte[0] : body.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8");
        exchange.sendResponseHeaders(status, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }
}
