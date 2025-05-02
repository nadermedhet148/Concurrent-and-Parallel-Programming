# Multi-Process Node.js Example

This project demonstrates a multi-process architecture using Node.js. It utilizes the `child_process` module to spawn multiple worker processes that can perform tasks concurrently.

## Project Structure

```
multi-process-nodejs
├── src
│   ├── index.js       # Entry point of the application
│   ├── worker.js      # Logic for the worker processes
├── package.json       # npm configuration file
└── README.md          # Project documentation
```

## Getting Started

To set up and run the multi-process example, follow these steps:

1. **Clone the repository** (if applicable):
   ```
   git clone <repository-url>
   cd multi-process-nodejs
   ```

2. **Install dependencies**:
   ```
   npm install
   ```

3. **Run the application**:
   ```
   npm start
   ```

## How It Works

- The `index.js` file serves as the master process, which spawns multiple worker processes defined in `worker.js`.
- Each worker listens for messages from the master, processes the data, and sends results back.
- This architecture allows for efficient handling of tasks, leveraging the capabilities of multi-core processors.

## License

This project is licensed under the MIT License.