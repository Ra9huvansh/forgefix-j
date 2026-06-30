package com.fixforge.gateway;

import quickfix.Application;
import quickfix.DefaultMessageFactory;
import quickfix.FieldNotFound;
import quickfix.FileLogFactory;
import quickfix.FileStoreFactory;
import quickfix.Initiator;
import quickfix.LogFactory;
import quickfix.Message;
import quickfix.MessageFactory;
import quickfix.MessageStoreFactory;
import quickfix.Session;
import quickfix.SessionID;
import quickfix.SessionNotFound;
import quickfix.SessionSettings;
import quickfix.SocketInitiator;
import quickfix.field.ClOrdID;
import quickfix.field.OrdType;
import quickfix.field.OrderQty;
import quickfix.field.Side;
import quickfix.field.Symbol;
import quickfix.field.TransactTime;
import quickfix.fix44.NewOrderSingle;

import java.io.InputStream;

public class GatewayInitiator implements Application {

    @Override
    public void onCreate(SessionID sessionId) {
    }

    @Override
    public void onLogon(SessionID sessionId) {
        System.out.println("Logged on: " + sessionId + " - sending NewOrderSingle");
        NewOrderSingle order = new NewOrderSingle(
                new ClOrdID("CLI-1"),
                new Side(Side.BUY),
                new TransactTime(),
                new OrdType(OrdType.MARKET));
        
        order.set(new Symbol("AAPL"));
        order.set(new OrderQty(100));
        try {
            Session.sendToTarget(order, sessionId);
        } catch (SessionNotFound e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onLogout(SessionID sessionId) {
    }

    @Override
    public void toAdmin(Message message, SessionID sessionId) {
    }

    @Override
    public void fromAdmin(Message message, SessionID sessionId) {
    }

    @Override
    public void toApp(Message message, SessionID sessionId) {
    }

    @Override
    public void fromApp(Message message, SessionID sessionId) throws FieldNotFound {
        System.out.println("Received from gateway: " + message);
    }

    public static void main(String[] args) throws Exception {
        try (InputStream cfg = GatewayInitiator.class.getResourceAsStream("/initiator.cfg")) {
            SessionSettings settings = new SessionSettings(cfg);
            Application application = new GatewayInitiator();
            MessageStoreFactory storeFactory = new FileStoreFactory(settings);
            LogFactory logFactory = new FileLogFactory(settings);
            MessageFactory messageFactory = new DefaultMessageFactory();

            Initiator initiator = new SocketInitiator(
                    application, storeFactory, settings, logFactory, messageFactory);
            
            initiator.start();
            System.out.println("Initiator started. Press <Enter> to quit.");
            System.in.read();
            initiator.stop();
        }
    }
}
