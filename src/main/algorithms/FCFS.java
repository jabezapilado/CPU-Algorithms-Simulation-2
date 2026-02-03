package main.algorithms;

import java.util.Comparator;
import java.util.List;

import main.Process;
import main.Scheduler;

public class FCFS implements Scheduler {
    public void schedule(List<Process> processes) {
        processes.sort(Comparator.comparingInt(p -> p.arrivalTime));
        
        int time = 0;
        for (Process p : processes) {
            if (time < p.arrivalTime) {
                time = p.arrivalTime;
            }
            p.waitingTime = time - p.arrivalTime;
            time += p.burstTime;
            p.turnaroundTime = p.waitingTime + p.burstTime;
        }
    }
}
