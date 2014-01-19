package com.test;

import java.util.logging.Logger;

import org.opencv.core.Core;

public class Test {
	
	private static Logger logger = ConsoleLogger.getLogger(Test.class.getName());
	
	private static void initLibraries() {
        // load libraries		
		String path = System.getProperty("user.dir");
		logger.info("base dir is " + path);
		
		logger.info("loading opencv library");
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);				
	}
	
	public static void main(String[] args) {
		logger.info("application started");
		initLibraries();
		
		logger.info("loading UI");		
		new MainUI();
	}
}
