import { ALGORITHMS } from '../utils/scheduler'
import styles from './AlgorithmSelector.module.css'

export function AlgorithmSelector({ selected, onSelect, timeQuantum, onQuantumChange }) {
  const needsQuantum = ALGORITHMS[selected]?.needsQuantum ?? false

  return (
    <div className={styles.panel}>
      <h3 className={styles.title}>Algorithm</h3>
      <div className={styles.grid}>
        {Object.entries(ALGORITHMS).map(([id, algo]) => (
          <button
            key={id}
            onClick={() => onSelect(id)}
            className={`${styles.algBtn} ${selected === id ? styles.selected : ''}`}
            title={algo.label}
          >
            {algo.name}
          </button>
        ))}
      </div>
      {needsQuantum && (
        <div className={styles.quantumRow}>
          <label htmlFor="quantum">Time Quantum:</label>
          <input
            id="quantum"
            type="number"
            min="1"
            value={timeQuantum}
            onChange={(e) => onQuantumChange(parseInt(e.target.value, 10) || 1)}
            className={styles.quantumInput}
          />
        </div>
      )}
    </div>
  )
}
