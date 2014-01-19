package com.test;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.logging.Logger;

import javax.swing.JPanel;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.highgui.Highgui;
import org.opencv.objdetect.CascadeClassifier;

public class VideoPanel extends JPanel {
	private static final long serialVersionUID = 5178434061598453625L;
	private static Logger logger = ConsoleLogger.getLogger(VideoPanel.class.getName());
	
	private Image img = null;
	
	public static BufferedImage matToBufferedImage(Mat matrix) {  
	     int cols = matrix.cols();  
	     int rows = matrix.rows();  
	     int elemSize = (int)matrix.elemSize();  
	     byte[] data = new byte[cols * rows * elemSize];  
	     int type;  
	     matrix.get(0, 0, data);  
	     switch (matrix.channels()) {  
	       case 1:  
	         type = BufferedImage.TYPE_BYTE_GRAY;  
	         break;  
	       case 3:  
	         type = BufferedImage.TYPE_3BYTE_BGR;  
	         // bgr to rgb  
	         byte b;  
	         for(int i=0; i<data.length; i=i+3) {  
	           b = data[i];  
	           data[i] = data[i+2];  
	           data[i+2] = b;  
	         }  
	         break;  
	       default:  
	         return null;  
	     }  
	     BufferedImage image = new BufferedImage(cols, rows, type);  
	     image.getRaster().setDataElements(0, 0, cols, rows, data);  
	     return image;  
	   }  	
	
	VideoPanel() {
	}
	
	public void initImage(String filename) {
		CascadeClassifier faceDetector = new CascadeClassifier("haarcascade_frontalface_alt.xml");
        Mat image = Highgui
                .imread("test.jpg");
 
        MatOfRect faceDetections = new MatOfRect();
        faceDetector.detectMultiScale(image, faceDetections);
 
        logger.info( String.format("Detected %s faces", faceDetections.toArray().length) );
 
        for (Rect rect : faceDetections.toArray()) {
            Core.rectangle(image, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height),
                    new Scalar(0, 255, 0));
        }		
		
        img = matToBufferedImage(image);
        
        repaint();
	}
	
	public void paintComponent(Graphics g) {
		if (img == null)
			return;
	    int x = 0;
	    int y = 0;
	    g.drawImage(img, x, y, 
	    		getWidth(),
	    		getHeight(),
	    		null);
	}		
}
