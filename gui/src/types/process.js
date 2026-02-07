/**
 * Process type for CPU scheduling simulation
 */
export const createProcess = (pid, arrivalTime, burstTime, priority) => ({
  pid,
  arrivalTime,
  burstTime,
  priority,
  waitingTime: 0,
  turnaroundTime: 0,
  remainingTime: burstTime,
  startTime: -1,
  completionTime: 0,
})

export const copyProcess = (p) => createProcess(p.pid, p.arrivalTime, p.burstTime, p.priority)
