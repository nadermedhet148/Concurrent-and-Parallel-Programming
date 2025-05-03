from multiprocessing import Pool
import time
from threading import Thread
from queue import Queue

def calculate_partial_sum(start, end):
    """Calculate the sum of numbers in the range [start, end)."""
    return sum(range(start, end))

if __name__ == "__main__":
    def parallel_sum(total_range, num_processes):
        """Calculate the sum of a range using parallel processing."""
        chunk_size = total_range // num_processes

        # Measure the start time
        start_time = time.time()

        # Create a pool of workers
        with Pool(processes=num_processes) as pool:
            # Divide the range into chunks
            tasks = [(i * chunk_size, (i + 1) * chunk_size) for i in range(num_processes)]

            # Calculate partial sums in parallel
            partial_sums = pool.starmap(calculate_partial_sum, tasks)

            # Combine the results
            total_sum = sum(partial_sums)

        # Measure the end time
        end_time = time.time()

        print(f"Time taken in parallel_sum: {end_time - start_time} seconds")

    def threaded_sum(total_range, num_threads):
        """Calculate the sum of a range using threading."""

        chunk_size = total_range // num_threads
        results = Queue()

        def worker(start, end):
            """Worker function to calculate partial sum."""
            results.put(calculate_partial_sum(start, end))

        # Measure the start time
        start_time = time.time()

        # Create and start threads
        threads = []
        for i in range(num_threads):
            start = i * chunk_size
            end = (i + 1) * chunk_size if i < num_threads - 1 else total_range
            thread = Thread(target=worker, args=(start, end))
            threads.append(thread)
            thread.start()

        # Wait for all threads to finish
        for thread in threads:
            thread.join()

        # Combine the results
        total_sum = sum(results.get() for _ in range(num_threads))

        # Measure the end time
        end_time = time.time()

        print(f"Time taken in threaded_sum: {end_time - start_time} seconds")

    # Call the method
    parallel_sum(total_range=15**8, num_processes=20)
    threaded_sum(total_range=15**8, num_threads=20)
