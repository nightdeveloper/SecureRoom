package com.test;

import java.util.Calendar;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.Handler;

import javax.swing.JTextArea;

public class ConsoleLogger extends Handler {

	private static JTextArea textArea = null;
	private static ConsoleLogger instance = null;
	
	private static ConsoleLogger getInstance() {
			if (instance == null) {
				instance = new ConsoleLogger();
			}
		return instance;
	}
	
	public static void setLogAreal(JTextArea logTextArea) {
		textArea = logTextArea;
	}
	
	public static Logger getLogger(String name) {
		Logger logger = Logger.getLogger(name);
		logger.addHandler(getInstance());		
		return logger;
	}

	@Override
	public void close() throws SecurityException {
	}

	@Override
	public void flush() {
	}

	@Override
	public void publish(LogRecord record) {
		if (textArea == null)
			return;

		synchronized(textArea) {
			String line = Calendar.getInstance().getTime().toString();
			line += " " + record.getMessage();
			line += "\n";
			textArea.append(line);
		}
	}
}
