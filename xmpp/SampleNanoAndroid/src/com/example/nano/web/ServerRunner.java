package com.example.nano.web;

import java.io.IOException;

public class ServerRunner {
    public static void run(Class serverClass) {
        try {
            executeInstance((NanoHTTPD) serverClass.newInstance());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void executeInstance(NanoHTTPD server) {
        try {
            server.start();
        } catch (IOException ioe) {
            System.err.println("Couldn't start server:\n" + ioe);
            System.exit(-1);
        }

        System.out.println("Server started, Hit Enter to stop.\n");

//        try {
//            System.in.read();
//        } catch (Throwable ignored) {
//        }

//        server.stop();
//        System.out.println("Server stopped.\n");
    }
    public static void stopInstance(NanoHTTPD server) {
    	
        server.stop();
        System.out.println("Server stopped.\n");
    }
}
