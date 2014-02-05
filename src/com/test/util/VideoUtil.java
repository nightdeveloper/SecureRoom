package com.test.util;

import java.awt.image.BufferedImage;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Logger;

import org.opencv.core.Mat;

import com.test.ConsoleLogger;

public class VideoUtil {

	private static Logger logger = ConsoleLogger.getLogger(VideoUtil.class.getName());

	private static void joinObjects(double objectId, double duplicateObjectId, double[] objectsFrame, int pos) {
		for(int i=0; i<pos; i++) {
			if (objectsFrame[i] == duplicateObjectId)
				objectsFrame[i] = objectId;
		}
	}
	
	public static void calculateObjectRanges(double[] frame, double[] objectsFrame, 
			int width, int height, int thresold) {
		
		// using scanning algorithm to mark objects
		int currentObjectNum = 0;

		//   c
		// b a 
		
		int pos = 0;
		for(int y=0; y<height; y++) {
			for(int x=0; x<width; x++) {
				int a = frame[pos] > thresold ? 1 : 0;
				int b = (pos-1) < 0 ? 0 : (frame[pos-1] > thresold ? 1 : 0);
				int c = (pos-width) < 0 ? 0 : (frame[pos-width] > thresold ? 1 : 0);				
				
				double v = 0;

				if (b==1 && c==1) {
					double first = objectsFrame[pos-width];
					double second = objectsFrame[pos-1];
					joinObjects(first, second, objectsFrame, pos);
					v = first;
				} else if (a==0) {
					// do nothing
				} else if (b==0 && c==0) {
					v = ++currentObjectNum;
				} else if (c==1) {
					v = objectsFrame[pos-width];					
				} else if (b==1) {
					v = objectsFrame[pos-1];
				}
				
				objectsFrame[pos] = v;			
				pos++;
			}
		}
		
		//logger.info("objects num == " + currentObjectNum);
	}
	
	public static Map<String, Integer> calculateSquares(double[] objectsFrame, int width, int height) {

		// scan to calculate squares		
		Map<String, Integer> result = new TreeMap<String, Integer>();		
		for(int i=0; i<width*height; i++) {
			double v = objectsFrame[i];
			if (v == 0)
				continue;
			
			String objectName = "object"+(int)v;
			
			Integer cur = result.get(objectName);
			if (cur == null)
				result.put(objectName, 1);
			else
				result.put(objectName, ++cur);
		}
		
		return result;
	}
	
	public static void removeLessSquaresFromFrame(double[] objectsFrame, double[] frame, int width, int height, Map<String, Integer> objectSquares,
			int minSquareSize) {
		
		for(int i=0; i<width*height; i++) {
			double v = objectsFrame[i];
			
			Integer cur = objectSquares.get("object"+(int)v);
			if (cur != null && cur < minSquareSize)
				frame[i] = 0;
		}
	}
	
	public static void writeMatToImage(Mat mat, BufferedImage result, int width, int height) {
		
        double[] pixel;
        
        int rgb = 0;
		for(int y=0; y<height; y++) {
			for(int x=0; x<width; x++) {
				pixel = mat.get(y, x);
				if (pixel.length == 1)
					rgb = (int)pixel[0] << 16 | (int)pixel[0] << 8 | (int)pixel[0];				
				else {
					
					rgb = (int)pixel[2] << 16 | (int)pixel[1] << 8 | (int)pixel[0];
				}
				result.setRGB(x, y, rgb);
			}
		}
	}
	
	public static void convertMatToFrameGreyscale(Mat mat, double[] currentFrame) {
		   
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
	
	public static int[] calculateHistogram(double[] frame, int width, int height) {
		int[] histogram = new int[256];
        for(int i=0; i<width*height; i++) {
            int p = (int) Math.ceil(frame[i]);
            histogram[p]++;
        }
        return histogram;
	}
	
	public static int calculateNoiseThresold(int[] histogram, int searchRange, long ambientLevel) {
		int noiseThresold = searchRange;
        for(int i=0; i<searchRange; i++) {
            if (histogram[i] < ambientLevel) {
                noiseThresold = i;
                break;
            }
        }
        
        return noiseThresold;
	}
	
	public static int calculateDiffWeight(int[] histogram, int ambientLevel) {
		int result = 0;
        for(int i=ambientLevel; i<256; i++) {
        	result += histogram[i];
        }
		
		return result;
	}
	
	public static void applyNeighborsOperator(double[] frame, int width, int height) {
        for(int i = width+1; i< (width-1)*(height-1); i++) {
            double neightbors = frame[i-width] +
            		frame[i-1] + frame[i+1] +
                    frame[i+width];
            if (neightbors < frame[i]/4)
            	frame[i] = 0;
        }
	}
	
	public static void removeNoiseApplyMultiplier(double[] frame, int noiseThresold, int multiplier, int width, int height) {
        for(int i=0; i<width*height; i++) {
            double p = frame[i];
           
            if (p < noiseThresold)
                p = 0;
           
            p *= multiplier;
           
            if (p > 255)
                p = 255;
           
            frame[i] = p;
        }		
	}
	
	public static void makeDiffFrame(double[] diffFrame, double[] currentFrame, double[] previousFrame, int width, int height) {
        for(int i=0; i<width*height; i++) {
            double p = Math.abs(currentFrame[i] - previousFrame[i]);
            if (p > 255)
            	p = 255;
            
            diffFrame[i] = p;
        }		
	}
}
