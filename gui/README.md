# CPU Scheduler GUI

A calculator-style React GUI for the CPU scheduling algorithms simulation.

## Prerequisites

- **Node.js** (v18 or later) and **npm**

To check if you have Node.js installed:

```bash
node --version
npm --version
```

### Installing Node.js (if needed)

**Option A: nvm (Node Version Manager)** – recommended on macOS/Linux

```bash
curl -o- https://raw.githubusercontent.com/nvm-sh/nvm/v0.39.0/install.sh | bash
```

Close and reopen your terminal, or run:

```bash
export NVM_DIR="$HOME/.nvm"
[ -s "$NVM_DIR/nvm.sh" ] && \. "$NVM_DIR/nvm.sh"
```

Then install Node.js:

```bash
nvm install --lts
nvm use --lts
```

**Option B: Homebrew** (macOS)

```bash
brew install node
```

**Option C: Official installer** – Download from [nodejs.org](https://nodejs.org)

---

## How to Run

### 1. Install dependencies

```bash
cd gui
npm install
```

### 2. Start the development server

```bash
npm run dev
```

### 3. Open in browser

Go to **http://localhost:5173**

---

## Build for production

```bash
npm run build
```

Output is in the `dist/` folder. Serve it with any static file server, e.g.:

```bash
npm run preview
```

---

## Usage

1. **Add processes** – Enter Arrival Time (AT), Burst Time (BT), and Priority (PR), then click `+`
2. **Select algorithm** – Choose FCFS, SRTF, Round Robin, or NPP
3. **Time quantum** – For Round Robin, set the time quantum
4. **Run** – Click Run to simulate and view results (TAT, WT, averages)

## File Structure

```
gui/
├── public/
├── src/
│   ├── algorithms/      # Scheduling algorithms (FCFS, SRTF, RR, NPP)
│   ├── components/      # UI components
│   ├── styles/          # Global styles
│   ├── types/           # Process type
│   ├── utils/           # Scheduler runner
│   ├── App.jsx
│   └── main.jsx
├── index.html
├── package.json
└── vite.config.js
```
