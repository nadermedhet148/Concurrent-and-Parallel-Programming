package io.java;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.*;

import io.eventLoop.EventLoop;
import io.eventLoop.events.AbstractEvent;
import io.eventLoop.events.HttpEvent;
import org.apache.log4j.Logger;

public class WebServer {

    private static Logger log = Logger.getLogger(WebServer.class);

    private static final int DEFAULT_PORT = 8080;

    private static final int N_THREADS = 10;

    private static final EventLoop eventLoop = new EventLoop(Executors.newSingleThreadExecutor(),
            Executors.newVirtualThreadPerTaskExecutor());


    public static void main(String args[]) {
        try {
            eventLoop.addEventHandler(HttpRequest.class, (event) -> {
                new RequestHandler(event.getSocket()).run();
            });
            eventLoop.addEventHandler(HttpRequest.class, (event) -> {
                try {
                    System.out.printf("Method: %s\n", event.socket.getReceiveBufferSize());
                } catch (SocketException e) {
                    throw new RuntimeException(e);
                }
            });
            eventLoop.start();
            new WebServer().start(getValidPortParam(args));
        } catch (Exception e) {
            log.error("Startup Error", e);
        }
    }

    public void start(int port) throws IOException {
        ServerSocket s = new ServerSocket(port);
        System.out.println("Web server listening on port " + port + " (press CTRL-C to quit)");
//        ExecutorService executor = new ThreadPoolExecutor(
//                N_THREADS, N_THREADS * 3, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(500)
//        );

//        ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();
//        ExecutorService executor = Executors.newFixedThreadPool(N_THREADS);
//        while (true) {
//            executor.submit(new RequestHandler(s.accept()));
//        }


        while (true) {
            var socket = s.accept();
            if (socket == null) {
                continue;
            }
            var res = eventLoop.dispatch(new HttpRequest(socket));
        }

    }

    static int getValidPortParam(String args[]) throws NumberFormatException {
        if (args.length > 0) {
            int port = Integer.parseInt(args[0]);
            if (port > 0 && port < 65535) {
                return port;
            } else {
                throw new NumberFormatException("Invalid port! Port value is a number between 0 and 65535");
            }
        }
        return DEFAULT_PORT;
    }

    public static class HttpRequest extends AbstractEvent<HttpRequest> {

        Socket socket;

        public HttpRequest(Socket socket) {
            this.socket = socket;
        }

        public Socket getSocket() {
            return socket;
        }
    }
}
