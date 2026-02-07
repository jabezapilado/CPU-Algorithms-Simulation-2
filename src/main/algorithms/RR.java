package main.algorithms;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import main.Process;
import main.Scheduler;

public class RR implements Scheduler {
    private int timeQuantum;
    
    public RR(int timeQuantum) {
        this.timeQuantum = timeQuantum;
    }
    
    public void schedule(List<Process> processes) {
        processes.sort(Comparator.comparingInt(p -> p.arrivalTime));
        
        Queue<Process> queue = new LinkedList<>();
        int time = 0;
        int index = 0;
        
        while (index < processes.size() || !queue.isEmpty()) {
            // Add all processes that have arrived at the current time slice
            while (index < processes.size() && processes.get(index).arrivalTime <= time) {
                queue.add(processes.get(index));
                index++;
            }
            
            if (queue.isEmpty()) {
                // No process available, jump to next arrival time
                if (index < processes.size()) {
                    time = processes.get(index).arrivalTime;
                    queue.add(processes.get(index));
                    index++;
                }
            } else {
                // Execute process for time quantum or remaining burst time
                Process p = queue.poll();
                if (p.startTime < 0) {
                    p.startTime = time;
                }
                int executeTime = Math.min(timeQuantum, p.remainingTime);
                time += executeTime;
                p.remainingTime -= executeTime;
                
                // Add newly arrived processes during execution
                while (index < processes.size() && processes.get(index).arrivalTime <= time) {
                    queue.add(processes.get(index));
                    index++;
                }
                
                if (p.remainingTime > 0) {
                    // Process not finished, add back to queue
                    queue.add(p);
                } else {
                    // Process finished
                    p.turnaroundTime = time - p.arrivalTime;
                    p.waitingTime = p.turnaroundTime - p.burstTime;
                    p.completionTime = time;
                }
            }
        }
    }
}