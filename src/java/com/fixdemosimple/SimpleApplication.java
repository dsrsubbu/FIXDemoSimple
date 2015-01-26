package com.fixdemosimple;
import quickfix.Application;
import quickfix.FieldNotFound;
import quickfix.IncorrectDataFormat;
import quickfix.IncorrectTagValue;
import quickfix.Message;
import quickfix.MessageCracker;
import quickfix.Session;
import quickfix.SessionID;
import quickfix.SessionNotFound;
import quickfix.UnsupportedMessageType;
import quickfix.field.OrderID;
import quickfix.field.QuoteReqID;
import quickfix.field.Symbol;
import quickfix.fix50.ExecutionReport;
import quickfix.fix50.NewOrderSingle;
import quickfix.fix50.Quote;
import quickfix.fix50.QuoteRequest;

public class SimpleApplication extends MessageCracker implements Application{

	private static final long STREAMING_TIME = 60*1000;
	private static final long WAITING_TIME = 10*1000;
	private TestMessages testMessages = new TestMessages();


	@Override
	public void fromAdmin(Message message, SessionID arg1) {
	}

	@Override
	public void fromApp(Message msg, SessionID sid) throws FieldNotFound, IncorrectDataFormat,
	IncorrectTagValue, UnsupportedMessageType
	{
		System.out.println("Received: " + msg + "\n");
		super.crack(msg, sid);
	}

	@Override
	public void onCreate(SessionID arg0) {
	}

	@Override
	public void onLogon(SessionID sessionId) {
	}

	@Override
	public void onLogout(SessionID arg0) {
	}

	@Override
	public void toAdmin(Message arg0, SessionID arg1) {
	}

	@Override
	public void toApp(Message arg0, SessionID arg1) {
	}


	@Handler
	public void quoteRequestHandler(QuoteRequest quoteRequest, SessionID id){
		// stream quotes for one minute
		long end = System.currentTimeMillis() + STREAMING_TIME;
		QuoteReqID quoteReqID = null;
		try {
			quoteReqID = quoteRequest.getQuoteReqID();
		} catch (FieldNotFound e1) {
			e1.printStackTrace();
			return;
		}
		while (System.currentTimeMillis() < end) {
			try {
				Quote quote = testMessages.getQuote(quoteReqID);
				Session.sendToTarget(quote, id);
				System.out.println("Sent: " + quote + "\n");
				Thread.sleep(WAITING_TIME);
			} catch ( SessionNotFound e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	@Handler
	public void orderHandler(NewOrderSingle order, SessionID id){
		// reply with an execution report
		try {
			OrderID orderID = new OrderID(order.getClOrdID().getValue());
			Symbol symbol = order.getSymbol();
			ExecutionReport execRep = testMessages.getExecutionReport(orderID, symbol);
			Session.sendToTarget(execRep, id);
			System.out.println("Sent: " + execRep + "\n");
		} catch (FieldNotFound | SessionNotFound e) {
			e.printStackTrace();
		}
	}

}