import styles from './ResultsDisplay.module.css'

export function ResultsDisplay({ results }) {
  const processes = results?.processes ?? []
  if (processes.length === 0) return null

  const sorted = [...processes].sort((a, b) => a.pid - b.pid)
  const avgWT = (results?.averageWaitingTime ?? 0).toFixed(2)
  const avgTAT = (results?.averageTurnaroundTime ?? 0).toFixed(2)

  const fmt = (v) => (v < 0 ? '−' : String(v))

  return (
    <div className={styles.panel}>
      <h3 className={styles.title}>Results</h3>
      <div className={styles.tableWrap}>
        <table className={styles.table}>
          <thead>
            <tr>
              <th>PID</th>
              <th>AT</th>
              <th>BT</th>
              <th>PR</th>
              <th>Start</th>
              <th>End</th>
              <th>TAT</th>
              <th>WT</th>
            </tr>
          </thead>
          <tbody>
            {sorted.map((p) => (
              <tr key={p.pid}>
                <td>P{p.pid}</td>
                <td>{p.arrivalTime}</td>
                <td>{p.burstTime}</td>
                <td>{p.priority}</td>
                <td>{fmt(p.startTime)}</td>
                <td>{p.completionTime || '−'}</td>
                <td>{p.turnaroundTime}</td>
                <td>{p.waitingTime}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
      <div className={styles.averages}>
        <span>Avg TAT: <strong>{avgTAT}</strong></span>
        <span>Avg WT: <strong>{avgWT}</strong></span>
      </div>
    </div>
  )
}
