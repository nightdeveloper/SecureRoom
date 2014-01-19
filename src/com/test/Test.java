package com.test;

import java.util.logging.Logger;

public class Test {
	
	private static Logger logger = ConsoleLogger.getLogger(Test.class.getName());
	
	public static void main(String[] args) {
		logger.info("application started");
		
		logger.info("loading UI");		
		new MainUI();
	}
}
