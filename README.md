# CPU Algorithms Simulation

The **CPU Algorithms Simulation** project pairs a Java-based scheduling API with a React interface to showcase classic CPU scheduling strategies: First-Come-First-Served (FCFS), Shortest Remaining Time First (SRTF), Round Robin (RR), and Non-preemptive Priority (NPP). The React UI operates strictly as a frontend client, delegating all scheduling work to the Java backend.

## Tech Stack

- **Java 11+** using `com.sun.net.httpserver.HttpServer` for the REST API
- **React 18** with Vite for the client UI
- **Node.js 18+** for frontend tooling

## Prerequisites

Ensure the following are installed on your workstation:

1. Java Development Kit (JDK) 11 or newer
2. Node.js 18 or newer (npm ships with Node.js)
3. Git (optional) for cloning the repository

## Running the Backend API (Java)

1. From the project root, compile the Java sources:
   ```bash
   javac -d out $(find src -name "*.java")
   ```
2. Start the HTTP server (defaults to port `8080`):
   ```bash
   java -cp out main.Main
   ```
   - Pass a custom port as the first argument if needed, e.g. `java -cp out main.Main 9090`.
   - The API becomes available at `http://localhost:<port>/api/schedule`.

### API Contract

- **Endpoint:** `POST /api/schedule`
- **Request body:**
  ```json
  {
    "algorithm": "RR",
    "timeQuantum": 2,
    "processes": [
      { "pid": 1, "arrivalTime": 0, "burstTime": 5, "priority": 1 },
      { "pid": 2, "arrivalTime": 2, "burstTime": 3, "priority": 2 }
    ]
  }
  ```
  - `timeQuantum` is required for Round Robin; ignored otherwise.
- **Response body:**
  ```json
  {
    "processes": [
      {
        "pid": 1,
        "arrivalTime": 0,
        "burstTime": 5,
        "priority": 1,
        "waitingTime": 3,
        "turnaroundTime": 8,
        "startTime": 0,
        "completionTime": 8
      }
    ],
    "averageWaitingTime": 1.5,
    "averageTurnaroundTime": 6.0
  }
  ```

All responses include CORS headers (`Access-Control-Allow-Origin: *`) to support local frontend development. Error payloads follow the shape `{ "error": "message" }`.

## Running the Frontend (React)

1. Install dependencies:
   ```bash
   cd gui
   npm install
   ```
2. Provide the backend URL if it differs from the default by creating `.env` in `gui/`:
   ```bash
   echo "VITE_API_BASE_URL=http://localhost:8080" > .env
   ```
   - The default base URL is `http://localhost:8080` when `.env` is omitted.
3. Start the Vite development server:
   ```bash
   npm run dev
   ```
4. Open the printed URL (typically `http://localhost:5173`).
5. Add processes, select an algorithm, and click **Run**. The UI displays loading and error states while it awaits the API response.

## Project Structure

```
CPU-Algorithms-Simulation/
├── README.md               # Project overview and instructions
├── .vscode/                # Editor settings (Java source path, etc.)
├── gui/                    # React + Vite frontend
│   ├── package.json        # Frontend dependencies and scripts
│   ├── public/             # Static assets served by Vite
│   └── src/
│       ├── App.jsx         # Top-level React component
│       ├── components/     # UI building blocks (ProcessInput, ResultsDisplay)
│       ├── styles/         # Global CSS + modules
│       ├── types/          # Frontend process helpers
│       └── utils/          # API client and algorithm metadata
├── src/
│   └── main/               # Java backend sources
│       ├── Main.java       # REST API entry point
│       ├── Process.java    # Process model
│       ├── Scheduler.java  # Scheduler contract
│       ├── SchedulerFactory.java # Scheduler resolver
│       ├── algorithms/     # FCFS, SRTF, RR, NPP implementations
│       └── http/           # JSON helper + HTTP handler
└── out/                    # (Generated) compiled Java classes after running javac
```

## Extending the System

- Add a new algorithm by implementing `Scheduler`, placing it under `src/main/algorithms`, and updating `SchedulerFactory` plus the frontend `ALGORITHMS` map.
- Surface new metrics by enhancing `ScheduleHandler.buildResponse` and rendering them inside the React UI.
- For deployment, wrap the Java server with Maven/Gradle or containerization, and configure Vite to proxy API requests for production builds.

## Troubleshooting

- **Backend compile errors:** Confirm you are using JDK 11+ and that the `out` directory exists (created automatically by the `javac` command above).
- **Port conflicts:** Supply a free port via `java -cp out main.Main 9000` and update `VITE_API_BASE_URL` accordingly.
- **CORS issues:** Ensure the React app targets the correct base URL; the backend exposes permissive headers for development.
- **Frontend build failures:** Verify Node.js 18+ and reinstall dependencies with `npm install`.

## Contributors

- Apilado, Jabez Timothy E. — Backend lead, Java REST API, and React integration
- Maninang, Allein Dane G. — Scheduling algorithms (back end)
- Parungao, Nikko S. — Scheduling algorithms (back end)
- Gurango, Christine Francoise O. — React interface (front end)
- Quilantang, Grant Mihkael D. — React interface (front end)

## References

- **Backend**
  - GeeksforGeeks. (2020, March 18). FCFS First Come First Serve CPU Scheduling. GeeksforGeeks. https://www.geeksforgeeks.org/dsa/first-come-first-serve-cpu-scheduling-non-preemptive/
  - GeeksforGeeks. (2025, January 13). Priority Scheduling in Operating System. GeeksforGeeks. https://www.geeksforgeeks.org/operating-systems/priority-scheduling-in-operating-system/
  - GeeksforGeeks. (2026, January 5). Shortest remaining time first (Preemptive SJF) scheduling algorithm. GeeksforGeeks. https://www.geeksforgeeks.org/dsa/shortest-remaining-time-first-preemptive-sjf-scheduling-algorithm/
  - GeeksforGeeks. (2026b, January 6). Round Robin scheduling in operating system. GeeksforGeeks. https://www.geeksforgeeks.org/operating-systems/round-robin-scheduling-in-operating-system/
  - GeeksforGeeks. (2018, November 13). Preemptive and NonPreemptive Scheduling. GeeksforGeeks. https://www.geeksforgeeks.org/operating-systems/preemptive-and-non-preemptive-scheduling/
  - GeeksforGeeks. (2023, January 17). Spooling in Operating System. GeeksforGeeks. https://www.geeksforgeeks.org/operating-systems/spooling-in-operating-system/
  - Operating Systems: CPU Scheduling. (2026). University of Illinois Chicago. https://www.cs.uic.edu/~jbell/CourseNotes/OperatingSystems/5_CPU_Scheduling.html
  - Short, M. (2010). The Case For Non-preemptive, Deadline-driven Scheduling In Real-time Embedded Systems. IAENG International Journal of Computer Science, 1(1).
  - sched(7) - Linux manual page. (2026). Man7.org. https://man7.org/linux/man-pages/man7/sched.7.html
  - CFS Scheduler — The Linux Kernel documentation. https://docs.kernel.org/scheduler/sched-design-CFS.html
  - Harchol-Balter, M., Schroeder, B., Bansal, N., & Agrawal, M. (2003). Size-based scheduling to improve web performance. ACM Transactions on Computer Systems, 21(2), 207–233.

- **Frontend**
  - DeepSeek. https://www.deepseek.com/
  - Vite Plugin React. https://github.com/vitejs/vite-plugin-react
  - CSS Modules. https://github.com/css-modules/css-modules
  - MDN Web Docs: Using CSS custom properties. https://developer.mozilla.org/en-US/docs/Web/CSS/Guides/Cascading_variables/Using_custom_properties

## License

This project is released for educational use. Apply additional licensing as needed before redistribution.
