package com.fixdemosimple;
import java.util.Random;

import quickfix.ConfigError;
import quickfix.DataDictionary;
import quickfix.DefaultMessageFactory;
import quickfix.FieldNotFound;
import quickfix.Group;
import quickfix.InvalidMessage;
import quickfix.Message;
import quickfix.MessageFactory;
import quickfix.MessageUtils;
import quickfix.StringField;
import quickfix.field.BidPx;
import quickfix.field.ClOrdID;
import quickfix.field.CumQty;
import quickfix.field.ExecID;
import quickfix.field.ExecType;
import quickfix.field.LastPx;
import quickfix.field.LeavesQty;
import quickfix.field.NoRelatedSym;
import quickfix.field.OfferPx;
import quickfix.field.OrdStatus;
import quickfix.field.OrdType;
import quickfix.field.OrderID;
import quickfix.field.Price;
import quickfix.field.QuoteID;
import quickfix.field.QuoteReqID;
import quickfix.field.Side;
import quickfix.field.Symbol;
import quickfix.field.TransactTime;
import quickfix.fix50.ExecutionReport;
import quickfix.fix50.NewOrderSingle;
import quickfix.fix50.Quote;
import quickfix.fix50.QuoteRequest;


public class TestMessages {

	private static final String PROVIDER_CLIENT = "PROVIDER-CLIENT";
	private static final String REQUESTER_CLIENT = "REQUESTER-CLIENT";

	public enum MsgType {
		QUOTE_REQUEST,
		QUOTE,
		NEW_ORDER_SINGLE,
		EXECUTION_REPORT
	}

	private final MessageFactory mf = new DefaultMessageFactory();
	private final Random rnd = new Random();

	public Message getMessage(MsgType msgType) throws InvalidMessage, ConfigError, FieldNotFound {
		switch (msgType) {
		case QUOTE_REQUEST:
			return getQuoteRequest();
		case QUOTE:
			return getQuote();
		case NEW_ORDER_SINGLE:
			return getNewOrderSingle();
		case EXECUTION_REPORT:
			return getExecutionReport();
		default:
			return null;
		}
	}

	private void setReqAndProvFields(Message msg) {
		msg.setField(new StringField(5001, REQUESTER_CLIENT));
		msg.setField(new StringField(5002, PROVIDER_CLIENT));
	}

	private QuoteRequest getQuoteRequest() throws InvalidMessage, ConfigError, FieldNotFound {
		QuoteReqID quoteReqID = new QuoteReqID("quoteRequest" + rnd.nextLong());

		QuoteRequest quoteRequest = new QuoteRequest(quoteReqID);
		Group group = new Group(NoRelatedSym.FIELD, Symbol.FIELD);
		group.setField(new Symbol("EUR/USD"));
		//		group.setField(new OrderQty(500));
		quoteRequest.addGroup(group);

		setReqAndProvFields(quoteRequest);
		return quoteRequest;
	}


	private Quote getQuote() {
		return getQuote(new QuoteReqID("quoteRequest" + rnd.nextLong()));
	}

	public Quote getQuote(QuoteReqID quoteReqID){
		QuoteID quoteID = new QuoteID("quote" + rnd.nextLong());

		Quote quote = new Quote(quoteID);
		quote.setField(quoteReqID);
		quote.setField(new BidPx(1.33745));
		quote.setField(new OfferPx(1.34255));

		setReqAndProvFields(quote);
		return quote;
	}

	private NewOrderSingle getNewOrderSingle() throws InvalidMessage, ConfigError {
		ClOrdID orderID = new ClOrdID("clOrdID" + rnd.nextLong());
		Side side = new Side(Side.BUY);
		TransactTime time = new TransactTime();
		OrdType type = new OrdType('D');

		NewOrderSingle nos = new NewOrderSingle(orderID, side, time, type);
		nos.setField(new Symbol("EUR/USD"));
		nos.setField(new QuoteID("quote" + rnd.nextLong()));
		nos.setField(new Price(0.91234));

		setReqAndProvFields(nos);
		return nos;
	}

	private ExecutionReport getExecutionReport() {
		return getExecutionReport(new OrderID("order" + rnd.nextLong()),
				new Symbol("EUR/USD"));
	}

	public ExecutionReport getExecutionReport(OrderID orderID, Symbol symbol) {
		ExecID execID = new ExecID("exec" + rnd.nextLong());
		ExecType execType = new ExecType('2');		// 2:Filled; 4:Canceled; 8:Rejected; C:Expired
		OrdStatus ordStatus = new OrdStatus('2');	// 2:Filled; 4:Canceled; 8:Rejected; C:Expired
		Side side = new Side(Side.BUY);
		LeavesQty leavesQty = new LeavesQty(0);
		CumQty cumQty = new CumQty(0);

		ExecutionReport execRep = new ExecutionReport(orderID, execID, execType, ordStatus, side, leavesQty, cumQty);
		execRep.setField(symbol);
		execRep.setField(new LastPx(0.91234));
		//		execRep.setField(new OrdRejReason(OrdRejReason.EXCHANGE_CLOSED));

		setReqAndProvFields(execRep);
		return execRep;
	}

	public Message getMsgFromStr(String msgStr) throws InvalidMessage, ConfigError {
		String msgType = MessageUtils.getMessageType(msgStr);
		Message msg = mf.create("FIXT.1.1", msgType);
		DataDictionary sessionDict = new DataDictionary("./resources/datadictionaries/FIXT11.xml");
		DataDictionary appDict = new DataDictionary("./resources/datadictionaries/FIX50.xml"); 
		msg.fromString(msgStr, sessionDict, appDict, true);
		return msg;
	}


}