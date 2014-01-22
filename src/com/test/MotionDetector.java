package com.test;

import org.opencv.core.CvType;
import org.opencv.core.Mat;

public class MotionDetector {

    boolean isLastFrameInited = false;
    private double[] lastFrame;
    private double[] currentFrame;
    private double[] inProcessFrame;
    private int width;
    private int height;
   
    public MotionDetector(int width, int height) {
        this.width = width;
        this.height = height;
        lastFrame = new double[height*width];
        currentFrame = new double[height*width];
        inProcessFrame = new double[height*width];
    }
   
    public Mat getCurrentFrame() {
        Mat mat = new Mat(height, width, CvType.CV_8UC1);
       
        double[] data = new double[3];
       
        int pos = 0;
        for(int y=0; y<height; y++) {
            for(int x=0;x<width;x++) {
                data[0] = inProcessFrame[pos];
                data[1] = inProcessFrame[pos];
                data[2] = inProcessFrame[pos];
                mat.put(y, x, data);
                pos++;
            }
        }
       
        return mat;
    }
   
    private final int MULTIPLIER = 10;
    private final int WAIT_FRAMES_TIMEOUT = 100;
    private final int MOTION_THRESOLD = 5;
    private final int NOISE_MULTIPLIER_THRESOLD = 500;
    private final int NEIGHTBORS_THRESOLD = 300;
    
    private boolean isRemoveNoise = false;
   
    private int noiseThresold = 5;
    private int diffExceed = 0;
   
    private int currentTimeout = 0;
   
    public boolean isMotion() {
        return diffExceed >= MOTION_THRESOLD;
    }
   
    public void processFrame(Mat mat) {
        initCurrentFrame(mat);
       
        if (!isLastFrameInited)
            shiftFrames();
   
        calculateFramesHistogramValues();
       
        calcDiff(isRemoveNoise?noiseThresold:0, MULTIPLIER);
       
        applyNeighbors();

        if (diffExceed > 5) {
            currentTimeout = WAIT_FRAMES_TIMEOUT;
        } else
            if (currentTimeout-- == 0)
                shiftFrames();   
    }
   
    private void shiftFrames() {
        System.arraycopy( currentFrame, 0, lastFrame, 0, width*height );
        isLastFrameInited = true;
    }
   
    private void applyNeighbors() {
        for(int i = width+1; i< (width-1)*(height-1); i++) {
            double neightbors = inProcessFrame[i-width] +
                    inProcessFrame[i-1] + inProcessFrame[i+1] +
                    inProcessFrame[i+width];
            if (neightbors < NEIGHTBORS_THRESOLD)
                inProcessFrame[i] = 0;
        }
    }
   
    private void calculateFramesHistogramValues() {
        int[] histogram = new int[255];
        for(int i=0; i<width*height; i++) {
            int p = (int) Math.ceil( Math.abs(currentFrame[i] - lastFrame[i]) );
            histogram[p]++;
        }
       
        int noiseThresold = 0;
        for(int i=0; i<255; i++) {
            if (histogram[i] < 2) {
                noiseThresold = i;
                break;
            }
        }

        long diff = 0;
       
        for(int i=noiseThresold; i<255; i++) {
            diff += histogram[i];
        }
       
        diffExceed = (int)diff/(width*height);
    }
   
    private void calcDiff(int noiseThresold, int multiplier) {
        for(int i=0; i<width*height; i++) {
            double p = Math.abs(currentFrame[i] - lastFrame[i]);
           
            if (p < noiseThresold)
                p = 0;
           
            p *= multiplier;
           
            if (p < NOISE_MULTIPLIER_THRESOLD && isRemoveNoise)
                p = 0;
           
            if (p > 255)
                p = 255;
           
            inProcessFrame[i] = p;
        }
    }
   
    private void initCurrentFrame(Mat mat) {
        double[] pixel = new double[3];
       
        int pos = 0;
        for(int y=0; y<mat.height(); y++) {
            for(int x=0; x<mat.width(); x++) {
                pixel = mat.get(y, x);
                double greyscale =
                         pixel[2]*0.299 + pixel[1]*0.587 + pixel[0]*0.114;
                currentFrame[pos++] = greyscale;
            }
        }
    }
       
}