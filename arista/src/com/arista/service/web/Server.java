package com.arista.service.web;


import java.io.IOException;
import java.net.InetAddress;
import java.net.URI;
import java.net.URL;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Locale;

import org.glassfish.grizzly.http.server.CLStaticHttpHandler;
import org.glassfish.grizzly.http.server.HttpHandler;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.server.Request;
import org.glassfish.grizzly.http.server.Response;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;

import com.arista.data.source.Yahoo;

public class Server {

    transient private HttpServer httpServer = null;
    private String               hostname   = null;

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    private HttpServer toServer(String uri) {
        if (httpServer == null) {
            ResourceConfig rc = new ResourceConfig().packages("com.arista.core", "com.arista.data.source");
            rc.register(JacksonFeature.class);
            httpServer = GrizzlyHttpServerFactory.createHttpServer(URI.create(uri), rc);
        }
        return httpServer;
    }

    public HttpServer getHttpServer() {
        return httpServer;
    }

    public void shutDownSever() {
        if (httpServer != null) {
            httpServer.shutdown();
        }
    }

    public void startServer(String uri) {
        toServer(uri);
    }

    public static void main(String[] args) throws IOException {
        Server server = new Server();
        server.setHostname(InetAddress.getLocalHost().getHostName());
        System.out.println("http://" + server.getHostname() + ":8080/api");
        server.startServer("http://" + server.getHostname() + ":8080/api");
        server.path();
        server.index();
        server.time();
        server.yahoo();
        server.jason();
        System.out.println("httpServer is started: " + server.getHttpServer().isStarted());
        System.in.read();
        server.shutDownSever();
        System.out.println("httpServer is stopped: " + server.getHttpServer().isStarted());
    }

    private void time() {
        httpServer.getServerConfiguration().addHttpHandler(new HttpHandler() {
            @Override public void service(Request request, Response response) throws Exception {
                final SimpleDateFormat format = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.US);
                final String date = format.format(new Date(System.currentTimeMillis()));
                response.setContentType("text/plain");
                response.setContentLength(date.length());
                response.getWriter().write(date);
            }
        }, "/time");
    }
    
    private void yahoo() {
        httpServer.getServerConfiguration().addHttpHandler(new HttpHandler() {
            @Override public void service(Request request, Response response) throws Exception {
                String data = Yahoo.history("SPY");
                response.setContentType("text/plain");
                response.setContentLength(data.length());
                response.getWriter().write(data);
            }
        }, "/yahoo");
    }

    private void jason() {
        httpServer.getServerConfiguration().addHttpHandler(new HttpHandler() {
            @Override public void service(Request request, Response response) throws Exception {
//                String data = Csv.toJsonString(null);
//                response.setContentType("text/plain");
//                response.setContentLength(data.length());
//                response.getWriter().write(data);
            }
        }, "/jason");
    }
    private void index() {
        HttpHandler staticHandler = new CLStaticHttpHandler(HttpServer.class.getClassLoader(), "/");
        httpServer.getServerConfiguration().addHttpHandler(staticHandler, "/");
    }

    public String getHostname() {
        return hostname;
    }

    private void path() {
        URL location = Server.class.getProtectionDomain().getCodeSource().getLocation();
        System.out.println(location.getFile());
    }
}
