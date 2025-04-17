from multiprocessing import Lock, Process, Queue

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

if __name__ == '__main__':
    # run_processes()
    run_processes_with_lock()

