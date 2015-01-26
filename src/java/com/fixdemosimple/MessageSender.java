package com.fixdemosimple;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import quickfix.Application;
import quickfix.Connector;
import quickfix.DefaultMessageFactory;
import quickfix.FileLogFactory;
import quickfix.FileStoreFactory;
import quickfix.LogFactory;
import quickfix.Message;
import quickfix.MessageFactory;
import quickfix.MessageStoreFactory;
import quickfix.Session;
import quickfix.SessionID;
import quickfix.SessionSettings;
import quickfix.SocketAcceptor;
import quickfix.SocketInitiator;


public class MessageSender {

	private Connector connector;
	private final CnnType cnnType;

	public enum CnnType {
		INITIATOR,
		ACCEPTOR;
	}

	public MessageSender (CnnType connectorType) {
		cnnType = connectorType;
	}

	public void run() throws Exception {
		run("./settings/settings-prov");
	}

	public void run(String settingsPath) throws Exception {
		Application application = new SimpleApplication();

		SessionSettings settings = new SessionSettings(new FileInputStream(settingsPath));
		MessageStoreFactory storeFactory = new FileStoreFactory(settings);
		LogFactory logFactory = new FileLogFactory(settings);
		MessageFactory messageFactory = new DefaultMessageFactory();
		if (cnnType == CnnType.INITIATOR) {
			connector = new SocketInitiator(application, storeFactory, settings, logFactory, messageFactory);
		} else {
			connector = new SocketAcceptor(application, storeFactory, settings, logFactory, messageFactory);
		}

		connector.start();
		while ((connector instanceof SocketInitiator) && !connector.isLoggedOn()) {}

		SessionID sid = connector.getSessions().get(0);
		TestMessages testMsgs = new TestMessages();

		System.out.println("\nEnter 'q' to quit;\n"
				+ "'request' for QuoteRequest;\n"
				+ "'quote' for Quote;\n"
				+ "'order' for NewOrderSingle;\n"
				+ "'report' for ExecutionReport:\n");
		BufferedReader bufferRead = new BufferedReader(new InputStreamReader(System.in));
		String input = "";
		Message msg = null;
		while(!"q".equalsIgnoreCase(input)) {
			try {
				input = bufferRead.readLine();
				switch (input) {
				case "request":
					msg = testMsgs.getMessage(TestMessages.MsgType.QUOTE_REQUEST);
					break;
				case "quote":
					msg = testMsgs.getMessage(TestMessages.MsgType.QUOTE);
					break;
				case "order":
					msg = testMsgs.getMessage(TestMessages.MsgType.NEW_ORDER_SINGLE);
					break;
				case "report":
					msg = testMsgs.getMessage(TestMessages.MsgType.EXECUTION_REPORT);
					break;
				default:
					break;
				}
				if (msg != null) {
					//					System.out.println(msg.toXML());
					Session.sendToTarget(msg, sid);
					System.out.println("Sent: " + msg + "\n");
				}
				msg = null;
			}
			catch(IOException e) {
				e.printStackTrace();
				break;
			}
		}

		connector.stop();
	}

}
