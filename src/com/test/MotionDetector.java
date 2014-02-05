package com.test;

import java.util.Map;
import java.util.logging.Logger;

import org.opencv.core.CvType;
import org.opencv.core.Mat;

import com.test.util.VideoUtil;

public class MotionDetector {

	private static Logger logger = ConsoleLogger.getLogger(MotionDetector.class.getName());
	
    boolean isLastFrameInited = false;
    private double[] lastFrame;
    private double[] currentFrame;
    private double[] inProcessFrame;
    private double[] objectsFrame;
    private int width;
    private int height;
    
    private Mat inProcessMatFrame;
    
    public MotionDetector(int width, int height) {
        this.width = width;
        this.height = height;
        lastFrame = new double[width*height];
        currentFrame = new double[width*height];
        inProcessFrame = new double[width*height];
        inProcessMatFrame = new Mat(height, width, CvType.CV_8UC1);
		objectsFrame = new double[width*height];        
    }
   
    public Mat getInProcessMatFrame() {
        double[] data = new double[3];
       
        int pos = 0;
        for(int y=0; y<height; y++) {
            for(int x=0;x<width;x++) {
                data[0] = inProcessFrame[pos];
                data[1] = inProcessFrame[pos];
                data[2] = inProcessFrame[pos];
                inProcessMatFrame.put(y, x, data);
                pos++;
            }
        }
       
        return inProcessMatFrame;
    }
   
    private final int MULTIPLIER = 10;
    private final double MOTION_THRESOLD = 0.1;
   
    private final int MAX_NOISE_THRESOLD = 30;
    private final int MIN_SQUARE_THRESOLD = 50;
    private double diffExceed = 0;
    
    private int currentTimeout = 0;
    private final int WAIT_FRAMES_TIMEOUT = 100;
   
    public boolean isMotion() {
        return diffExceed >= MOTION_THRESOLD;
    }
   
    public int getMotionFactor() {
    	int factor = (int)(diffExceed*10);
    	if (factor > 100)
    		factor = 100;
    	
    	return factor;
    }
    
    public void processFrame(Mat mat) {
    	VideoUtil.convertMatToFrameGreyscale(mat, currentFrame);
       
        if (!isLastFrameInited) {
            shiftFrames();
        }
    	VideoUtil.makeDiffFrame(inProcessFrame, currentFrame, lastFrame, width, height);
        int[] histogram = VideoUtil.calculateHistogram(inProcessFrame, width, height); 
        int ambientLevel = VideoUtil.calculateNoiseThresold(histogram, MAX_NOISE_THRESOLD, 5);
        VideoUtil.removeNoiseApplyMultiplier(inProcessFrame, ambientLevel, MULTIPLIER, width, height);
        VideoUtil.applyNeighborsOperator(inProcessFrame, width, height);
        
        VideoUtil.calculateObjectRanges(inProcessFrame, objectsFrame, width, height, MAX_NOISE_THRESOLD);
        Map<String, Integer> squares = VideoUtil.calculateSquares(objectsFrame, width, height);
        VideoUtil.removeLessSquaresFromFrame(objectsFrame, inProcessFrame, width, height, squares, MIN_SQUARE_THRESOLD);

        int diffWeight = VideoUtil.calculateDiffWeight(histogram, ambientLevel);
        diffExceed = (double)diffWeight / (width*height) * 100;
        
        if (isMotion()) {
            currentTimeout = WAIT_FRAMES_TIMEOUT;
        } else
            if (currentTimeout-- == 0) {
                shiftFrames();
            }
            else {
            	logger.info("wating timeout - " + currentTimeout);
            }
    }
   
    private void shiftFrames() {
        System.arraycopy( currentFrame, 0, lastFrame, 0, width*height );
        isLastFrameInited = true;
    }
   
}