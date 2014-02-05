package com.test.worker;

import java.awt.image.BufferedImage;
import java.util.logging.Logger;

import javax.swing.SwingWorker;

import org.opencv.core.Mat;
import org.opencv.highgui.VideoCapture;

import com.test.ConsoleLogger;
import com.test.MotionDetector;
import com.test.VideoPanel;
import com.test.util.VideoUtil;


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
		
		//VideoCapture camera = new VideoCapture(0);
		VideoCapture camera = new VideoCapture("test3.mp4");
		
		BufferedImage image = null, processImage = null;
		int frameWidth = 0;
		int frameHeight = 0;
		
		try {
			if (!camera.open("test3.mp4")) {
			//if (!camera.open(0)) {
				logger.info("can not find any camera");
				firePropertyChange(RESULT, "", ERROR);
				return null;
			}
			
			Mat frame = new Mat();
			camera.read(frame);
			if (image == null) {
				frameWidth = frame.width();
				frameHeight = frame.height();
				firePropertyChange(VIDEO_WIDTH, "", frameWidth);
				firePropertyChange(VIDEO_HEIGHT, "", frame.height());
				image = new BufferedImage(frameWidth, frameHeight, BufferedImage.TYPE_INT_RGB);
				processImage = new BufferedImage(frameWidth, frameHeight, BufferedImage.TYPE_INT_RGB);
			}
			
			MotionDetector detector = new MotionDetector(frameWidth, frameHeight);
			while(!isCancelled()) {
				camera.read(frame);
				detector.processFrame(frame);
				
				VideoUtil.writeMatToImage(
						frame, image, frameWidth, frameHeight);
				VideoUtil.writeMatToImage(
						detector.getInProcessMatFrame(), processImage, frameWidth, frameHeight);
				
				firePropertyChange(VIDEO_MOTION, "", detector.getMotionFactor());			
				panel.setImages(image, processImage);
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
