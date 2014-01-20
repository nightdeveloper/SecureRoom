package com.test.worker;

import java.util.logging.Logger;

import javax.swing.SwingWorker;

import com.test.ConsoleLogger;
import com.test.VideoPanel;


public class VideoWorker extends SwingWorker<Integer, String> {

	private static Logger logger = ConsoleLogger.getLogger(VideoWorker.class.getName());	
	private VideoPanel panel = null;
	
	public VideoWorker(VideoPanel videoPanel) {
		panel = videoPanel;
	}
	
	@Override
	protected Integer doInBackground() throws Exception {
		
		logger.info("video worker started");
		
		panel.initImage("test.jpg");
		
		logger.info("video worker finished");
		
		return null;
	}

}
