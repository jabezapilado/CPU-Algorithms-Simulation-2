import { useState } from 'react'
import { ProcessInput, AlgorithmSelector, ResultsDisplay } from './components'
import { runScheduler } from './utils/scheduler'
import styles from './App.module.css'

function App() {
  const [processes, setProcesses] = useState([])
  const [algorithm, setAlgorithm] = useState('FCFS')
  const [timeQuantum, setTimeQuantum] = useState(2)
  const [results, setResults] = useState(null)
  const [isLoading, setIsLoading] = useState(false)
  const [error, setError] = useState(null)

  const handleRun = async () => {
    if (processes.length === 0) return
    setIsLoading(true)
    setError(null)
    try {
      const scheduled = await runScheduler(processes, algorithm, timeQuantum)
      setResults(scheduled)
    } catch (err) {
      setError(err?.message ?? 'Failed to run scheduler')
      setResults(null)
    } finally {
      setIsLoading(false)
    }
  }

  const handleClear = () => {
    setResults(null)
    setError(null)
  }

  return (
    <div className={styles.app}>
      <header className={styles.header}>
        <h1 className={styles.logo}>CPU Algorithms Simulator</h1>
        <p className={styles.subtitle}>Apilado, Gurango, Maninang, Parungao, and Quilantang</p>
      </header>

      <main className={styles.calculator}>
        <div className={styles.display}>
          {isLoading ? (
            <div className={styles.placeholder}>Running algorithms…</div>
          ) : results ? (
            <ResultsDisplay results={results} />
          ) : error ? (
            <div className={styles.placeholder} role="alert">
              {error}
            </div>
          ) : (
            <div className={styles.placeholder}>
              Add processes, pick an algorithm, then press <strong>Run</strong>
            </div>
          )}
        </div>

        <div className={styles.controls}>
          <ProcessInput processes={processes} onProcessesChange={setProcesses} />
          <AlgorithmSelector
            selected={algorithm}
            onSelect={setAlgorithm}
            timeQuantum={timeQuantum}
            onQuantumChange={setTimeQuantum}
          />
        </div>

        <div className={styles.actions}>
          <button
            onClick={handleRun}
            disabled={processes.length === 0 || isLoading}
            className={styles.runBtn}
          >
            {isLoading ? 'Running…' : 'Run'}
          </button>
          <button
            onClick={handleClear}
            disabled={!results && !error}
            className={styles.clearBtn}
          >
            Clear
          </button>
        </div>
      </main>

      <footer className={styles.footer}>
        FCFS • SRTF • Round Robin • NPP
      </footer>
    </div>
  )
}

export default App
