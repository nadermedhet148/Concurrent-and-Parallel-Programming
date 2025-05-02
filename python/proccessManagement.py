from multiprocessing import Lock, Process, Queue
import os
import sys

def publish(q):
    while True:
     q.put([42, None, 'hello'])

def consume(q):
    while True:
        item = q.get()
        if item is None:
            break
        print(item)
    
    
def lock_test(l, i):
    l.acquire()
    try:
        print('hello world', i)
    finally:
        l.release()

def run_processes_with_lock():
    lock = Lock()
    for num in range(10):
        Process(target=lock_test, args=(lock, num)).start()

def run_processes():
    q = Queue()
    p = Process(target=publish, args=(q,))
    p2 = Process(target=consume, args=(q,))
    p.start()
    p2.start()
    p.join()
    p2.join()



def run_external_file(external_file):
        current_dir = os.path.dirname(os.path.abspath(__file__))
        full_path = os.path.join(current_dir, external_file)
        if os.path.exists(full_path):
            # process = Process(target=os.system, args=(f'/usr/bin/python3 {full_path}',))
            process = Process(target=os.system, args=(f'echo $USER',))
            process.start()
            process.join()
        else:
            print(f"File {full_path} does not exist.")


if __name__ == '__main__':
    # run_processes()
    # run_processes_with_lock()
    run_external_file('subProcess.py')

