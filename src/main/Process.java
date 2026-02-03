package main;

public class Process {

    public int pid;
    public int arrivalTime;
    public int burstTime;
    public int priority;

    public int waitingTime;
    public int turnaroundTime;
    public int remainingTime;

    public Process(int pid, int arrivalTime, int burstTime, int priority) {
        this.pid = pid;
        this.arrivalTime = arrivalTime;
        this.burstTime = burstTime;
        this.priority = priority;
        this.remainingTime = burstTime;
    }
}
