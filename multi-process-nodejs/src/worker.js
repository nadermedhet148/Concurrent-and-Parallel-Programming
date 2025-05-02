
process.on('message', (data) => {
  // Simulate some processing
  const result = data * 2; // Example processing: doubling the input

  process.send(result);

});