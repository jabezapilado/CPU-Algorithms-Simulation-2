package main;

import main.algorithms.FCFS;
import main.algorithms.NPP;
import main.algorithms.RR;
import main.algorithms.SRTF;

public final class SchedulerFactory {

    private SchedulerFactory() {
    }

    public static Scheduler create(String algorithmId, Integer timeQuantum) {
        if (algorithmId == null || algorithmId.isEmpty()) {
            throw new IllegalArgumentException("Algorithm identifier is required");
        }
        switch (algorithmId.toUpperCase()) {
            case "FCFS":
                return new FCFS();
            case "SRTF":
                return new SRTF();
            case "RR":
                int quantum = timeQuantum == null ? 2 : timeQuantum;
                if (quantum <= 0) {
                    throw new IllegalArgumentException("Time quantum must be greater than zero for Round Robin");
                }
                return new RR(quantum);
            case "NPP":
                return new NPP();
            default:
                throw new IllegalArgumentException("Unsupported algorithm: " + algorithmId);
        }
    }
}
