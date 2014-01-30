package com.test.worker;

import java.util.logging.Logger;

import javax.swing.SwingWorker;

import org.opencv.core.Mat;
import org.opencv.highgui.VideoCapture;

import com.test.ConsoleLogger;
import com.test.MotionDetector;
import com.test.VideoPanel;


public class VideoWorker extends SwingWorker<Integer, String> {

	public static final String RESULT = "result";
	public static final String ERROR = "error";
	public static final String VIDEO_MOTION = "video_motion";
	public static final String VIDEO_WIDTH = "video_width";
	public static final String VIDEO_HEIGHT = "video_height";
	
	private static Logger logger = ConsoleLogger.getLogger(VideoWorker.class.getName());
	private VideoPanel panel = null;
	
	public VideoWorker(VideoPanel videoPanel) {
		panel = videoPanel;
	}
		
	@Override
	protected Integer doInBackground() throws Exception {
		logger.info("video worker started");
		
		VideoCapture camera = new VideoCapture(0);
		//VideoCapture camera = new VideoCapture("test2.mp4");
		
		try {
			//if (!camera.open("test2.mp4")) {
			if (!camera.open(0)) {
				logger.info("can not find any camera");
				firePropertyChange(RESULT, "", ERROR);
				return null;
			}
			
			Mat  frame = new Mat();
			camera.read(frame);
			firePropertyChange(VIDEO_WIDTH, "", frame.width());
			firePropertyChange(VIDEO_HEIGHT, "", frame.height());			
			
			MotionDetector detector = new MotionDetector(frame.width(), frame.height());
			while(!isCancelled()) {
				camera.read(frame);
				detector.processFrame(frame);
				firePropertyChange(VIDEO_MOTION, "", detector.getMotionFactor());			
				panel.setImage(frame, detector.getCurrentFrame());
			}
			
			logger.info("cancelled");
		} catch(Exception e) {
			e.printStackTrace();
			logger.severe("video error: " + e.getMessage());
		} finally {
			camera.release();
		}
		
		return null;		
	}

}
