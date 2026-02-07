import { useState } from 'react'
import { createProcess } from '../types/process'
import styles from './ProcessInput.module.css'

export function ProcessInput({ processes, onProcessesChange }) {
  const [at, setAt] = useState('')
  const [bt, setBt] = useState('')
  const [pr, setPr] = useState('1')

  const addProcess = () => {
    const arrivalTime = parseInt(at, 10)
    const burstTime = parseInt(bt, 10)
    const priority = parseInt(pr, 10)

    if (isNaN(arrivalTime) || isNaN(burstTime) || isNaN(priority)) return
    if (burstTime < 1) return

    const pid = processes.length + 1
    onProcessesChange([...processes, createProcess(pid, arrivalTime, burstTime, priority)])
    setAt('')
    setBt('')
    setPr('1')
  }

  const removeProcess = (index) => {
    const updated = processes.filter((_, i) => i !== index)
    onProcessesChange(updated.map((p, i) => ({ ...p, pid: i + 1 })))
  }

  const clearAll = () => onProcessesChange([])

  return (
    <div className={styles.panel}>
      <h3 className={styles.title}>Processes</h3>

      <div className={styles.inputRow}>
        <input
          type="number"
          placeholder="AT"
          value={at}
          onChange={(e) => setAt(e.target.value)}
          min="0"
          className={styles.input}
          title="Arrival Time"
        />
        <input
          type="number"
          placeholder="BT"
          value={bt}
          onChange={(e) => setBt(e.target.value)}
          min="1"
          className={styles.input}
          title="Burst Time"
        />
        <input
          type="number"
          placeholder="PR"
          value={pr}
          onChange={(e) => setPr(e.target.value)}
          min="0"
          className={styles.input}
          title="Priority (lower = higher)"
        />
        <button onClick={addProcess} className={styles.addBtn} title="Add process">
          +
        </button>
      </div>

      {processes.length > 0 && (
        <div className={styles.list}>
          {processes.map((p, i) => (
            <div key={i} className={styles.processChip}>
              <span>P{p.pid}: {p.arrivalTime},{p.burstTime},{p.priority}</span>
              <button onClick={() => removeProcess(i)} className={styles.removeBtn} aria-label="Remove">
                ×
              </button>
            </div>
          ))}
          <button onClick={clearAll} className={styles.clearBtn}>
            Clear All
          </button>
        </div>
      )}
    </div>
  )
}
