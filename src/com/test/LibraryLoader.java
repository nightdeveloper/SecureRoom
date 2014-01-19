package com.test;

import java.util.logging.Logger;

import org.opencv.core.Core;

public class LibraryLoader {

	private static Logger logger = ConsoleLogger.getLogger(LibraryLoader.class.getName());

	public static void loadOpenCV() {
		String path = System.getProperty("user.dir");
		logger.info("base dir is " + path);
		
		logger.info("loading opencv library");
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);				
	}
}
