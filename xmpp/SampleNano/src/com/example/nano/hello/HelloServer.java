package com.example.nano.hello;

import java.util.Map;

import com.example.nano.NanoHTTPD;
import com.example.nano.ServerRunner;
import com.example.nano.NanoHTTPD.IHTTPSession;
import com.example.nano.NanoHTTPD.Method;
import com.example.nano.NanoHTTPD.Response;

/**
 * An example of subclassing NanoHTTPD to make a custom HTTP server.
 */
public class HelloServer extends NanoHTTPD {
    public HelloServer() {
        super(8080);
    }

    @Override 
    public Response serve(IHTTPSession session) {
        Method method = session.getMethod();
        String uri = session.getUri();
        System.out.println(method + " '" + uri + "' ");

        String msg = "<html><body><h1>Hello server</h1>\n";
        Map<String, String> parms = session.getParms();
        if (parms.get("username") == null)
            msg +=
                    "<form action='?' method='get'>\n" +
                            "  <p>Your name: <input type='text' name='username'></p>\n" +
                            "</form>\n";
        else
            msg += "<p>Hello, " + parms.get("username") + "!</p>";

        msg += "</body></html>\n";

        return new NanoHTTPD.Response(msg);
    }


    public static void main(String[] args) {
        ServerRunner.run(HelloServer.class);
    }
}
