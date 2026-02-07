package main.algorithms;

import java.util.Comparator;
import java.util.List;

import main.Process;
import main.Scheduler;

public class SRTF implements Scheduler {
    public void schedule(List<Process> processes) {
        processes.sort(Comparator.comparingInt(p -> p.arrivalTime));

        int time = processes.isEmpty() ? 0 : processes.get(0).arrivalTime;
        int completed = 0;
        int total = processes.size();

        while (completed < total) {
            Process current = null;
            int minRemaining = Integer.MAX_VALUE;

            for (Process p : processes) {
                if (p.arrivalTime <= time && p.remainingTime > 0 && p.remainingTime < minRemaining) {
                    minRemaining = p.remainingTime;
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
                time = nextArrival == Integer.MAX_VALUE ? time + 1 : nextArrival;
                continue;
            }

            if (current.startTime < 0) {
                current.startTime = time;
            }

            current.remainingTime--;
            time++;

            if (current.remainingTime == 0) {
                completed++;
                current.turnaroundTime = time - current.arrivalTime;
                current.waitingTime = current.turnaroundTime - current.burstTime;
                current.completionTime = time;
            }
        }
    }
}