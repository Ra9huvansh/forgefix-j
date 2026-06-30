package com.fixforge.gateway;

import quickfix.Acceptor;
import quickfix.Application;
import quickfix.DefaultMessageFactory;
import quickfix.FileLogFactory;
import quickfix.LogFactory;
import quickfix.FileStoreFactory;
import quickfix.MessageFactory;
import quickfix.MessageStoreFactory;
import quickfix.SessionSettings;
import quickfix.SocketAcceptor;

import java.io.InputStream;

public class GatewayMain {

    public static void main(String[] args) throws Exception {
        try (InputStream cfg = GatewayMain.class.getResourceAsStream("/acceptor.cfg")) {
            SessionSettings settings = new SessionSettings(cfg);

            Application application = new GatewayApplication();
            MessageStoreFactory storeFactory = new FileStoreFactory(settings);
            LogFactory logFactory = new FileLogFactory(settings);
            MessageFactory messageFactory = new DefaultMessageFactory();

            Acceptor acceptor = new SocketAcceptor(
                    application, storeFactory, settings, logFactory, messageFactory);
            
            acceptor.start();
            System.out.println("FIXFORGE acceptor started on port 9876. Press <Enter> to quit.");
            System.in.read();
            acceptor.stop();
        }
    }
}