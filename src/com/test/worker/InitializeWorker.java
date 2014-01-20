package com.test.worker;

import java.util.logging.Logger;

import javax.swing.SwingWorker;

import org.opencv.core.Core;

import com.test.ConsoleLogger;


public class InitializeWorker extends SwingWorker<Integer, String> {

	public static final String OK = "ok";
	public static final String FAILED = "failed";
	public static final String RESULT = "result";
	private static Logger logger = ConsoleLogger.getLogger(InitializeWorker.class.getName());
	
	@Override
	protected Integer doInBackground() throws Exception {
		
		logger.info("Initialization started");
		setProgress(0);

		String path = System.getProperty("user.dir");
		logger.info("base dir is " + path);
		
		setProgress(10);
		logger.info("loading opencv library");
		try {
			System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		} catch(UnsatisfiedLinkError e) {
			logger.info("can not load native library - " + Core.NATIVE_LIBRARY_NAME);
			firePropertyChange(RESULT, "", FAILED);
		}
		setProgress(50);
		logger.info("library loaded");
		
		setProgress(100);
		firePropertyChange(RESULT, "", OK);
		return null;
	}

}
