package main.algorithms;

import java.util.Comparator;
import java.util.List;

import main.Process;
import main.Scheduler;

public class NPP implements Scheduler {
    public void schedule(List<Process> processes) {
        processes.sort(Comparator.comparingInt(p -> p.arrivalTime));

        int total = processes.size();
        int completed = 0;
        int time = processes.isEmpty() ? 0 : processes.get(0).arrivalTime;

        while (completed < total) {
            Process current = null;

            for (Process p : processes) {
                if (p.remainingTime <= 0 || p.arrivalTime > time) {
                    continue;
                }

                if (current == null || p.priority < current.priority
                        || (p.priority == current.priority && p.arrivalTime < current.arrivalTime)
                        || (p.priority == current.priority && p.arrivalTime == current.arrivalTime && p.pid < current.pid)) {
                    current = p;
                }
            }

            if (current == null) {
                int nextArrival = Integer.MAX_VALUE;
                for (Process p : processes) {
                    if (p.remainingTime > 0 && p.arrivalTime > time) {
                        nextArrival = Math.min(nextArrival, p.arrivalTime);
                    }
                }
                if (nextArrival == Integer.MAX_VALUE) {
                    break;
                }
                time = nextArrival;
                continue;
            }

            if (current.startTime < 0) {
                current.startTime = time;
            }

            time += current.remainingTime;
            current.remainingTime = 0;
            completed++;

            current.turnaroundTime = time - current.arrivalTime;
            current.waitingTime = current.turnaroundTime - current.burstTime;
            current.completionTime = time;
        }
    }
}
