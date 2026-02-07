export const ALGORITHMS = {
  FCFS: { id: 'FCFS', name: 'FCFS', label: 'First Come First Served', needsQuantum: false },
  SRTF: { id: 'SRTF', name: 'SRTF', label: 'Shortest Remaining Time First', needsQuantum: false },
  RR: { id: 'RR', name: 'RR', label: 'Round Robin', needsQuantum: true },
  NPP: { id: 'NPP', name: 'NPP', label: 'Non-Preemptive Priority', needsQuantum: false },
}

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL ?? 'http://localhost:8080'

export const runScheduler = async (processes, algorithmId, timeQuantum = 2) => {
  const payload = {
    algorithm: algorithmId,
    timeQuantum,
    processes: processes.map((p, index) => ({
      pid: p.pid ?? index + 1,
      arrivalTime: Number(p.arrivalTime),
      burstTime: Number(p.burstTime),
      priority: Number.isFinite(p.priority) ? Number(p.priority) : 0,
    })),
  }

  const response = await fetch(`${API_BASE_URL}/api/schedule`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(payload),
  })

  if (!response.ok) {
    let message = 'Failed to run scheduler'
    try {
      const error = await response.json()
      if (error?.error) message = error.error
    } catch (err) {
      // ignore JSON parsing errors for error responses
    }
    throw new Error(message)
  }

  return response.json()
}
