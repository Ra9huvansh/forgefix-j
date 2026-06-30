package com.fixforge.gateway;

import quickfix.Application;
import quickfix.DoNotSend;
import quickfix.FieldNotFound;
import quickfix.IncorrectDataFormat;
import quickfix.IncorrectTagValue;
import quickfix.Message;
import quickfix.RejectLogon;
import quickfix.Session;
import quickfix.SessionID;
import quickfix.SessionNotFound;
import quickfix.UnsupportedMessageType;
import quickfix.field.AvgPx;
import quickfix.field.CumQty;
import quickfix.field.ExecID;
import quickfix.field.ExecType;
import quickfix.field.LeavesQty;
import quickfix.field.OrdStatus;
import quickfix.field.OrderID;
import quickfix.fix44.ExecutionReport;
import quickfix.fix44.NewOrderSingle;

import java.util.concurrent.atomic.AtomicInteger;

public class GatewayApplication implements Application {
    private final AtomicInteger seq = new AtomicInteger(0);
    
    @Override
    public void onCreate(SessionID sessionId) {
        System.out.println("Session created: " + sessionId);
    }

    @Override
    public void onLogon(SessionID sessionId) {
        System.out.println("Logon: " + sessionId);
    }

    @Override
    public void onLogout(SessionID sessionId) {
        System.out.println("Logout: " + sessionId);
    }

    @Override
    public void toAdmin(Message message, SessionID sessionId) {
    }

    @Override
    public void fromAdmin(Message message, SessionID sessionId) 
            throws FieldNotFound, IncorrectDataFormat, IncorrectTagValue {
    }

    @Override
    public void toApp(Message message, SessionID sessionId) throws DoNotSend {
    }

    @Override
    public void fromApp(Message message, SessionID sessionId)
            throws FieldNotFound, IncorrectDataFormat, IncorrectTagValue, UnsupportedMessageType {
        if (message instanceof NewOrderSingle order) {
            echo(order, sessionId);
        }
    }

    private void echo(NewOrderSingle order, SessionID sessionId) throws FieldNotFound {
        System.out.println("Received NewOrderSingle: " + order);

        int n = seq.incrementAndGet();
        ExecutionReport report = new ExecutionReport(
                new OrderID("ORD-" + n),
                new ExecID("EXEC-" + n),
                new ExecType(ExecType.NEW),
                new OrdStatus(OrdStatus.NEW),
                order.getSide(),
                new LeavesQty(order.getOrderQty().getValue()),
                new CumQty(0),
                new AvgPx(0));
        
        report.set(order.getClOrdID());
        report.set(order.getSymbol());
        report.set(order.getOrderQty());

        try {
            Session.sendToTarget(report, sessionId);
            System.out.println("Echoed ExecutionReport (NEW ack)");
        } catch (SessionNotFound e) {
            e.printStackTrace();
        }
    }
}
