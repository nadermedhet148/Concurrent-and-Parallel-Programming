// src/index.js
const { fork } = require('child_process');

const numWorkers = 4;
const workers = [];
let completedTasks = 0;

for (let i = 0; i < numWorkers; i++) {
  const worker = fork('./worker.js');

  if (!worker) {
    console.error(`Failed to create worker ${i}`);
    continue;
  }

  workers.push(worker);

  

  worker.on('message', (result) => {
    console.log(`Worker ${worker.pid} completed task with result: ${result}`);
    completedTasks++;

    if (completedTasks === numWorkers) {
      console.log('All tasks completed.');
      workers.forEach(worker => worker.kill());
    }
  });
}

// Send tasks to workers
workers.forEach((worker, index) => {
  worker.send(index);
});