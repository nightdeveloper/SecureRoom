package com.test;

import java.awt.Graphics;
import java.awt.Image;
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

import com.test.util.VideoUtil;

public class VideoPanel extends JPanel {
	private static final long serialVersionUID = 5178434061598453625L;
	private static Logger logger = ConsoleLogger.getLogger(VideoPanel.class.getName());
	
	private Image img = null;
	private Image debugImg = null;
	
	VideoPanel() {
	}
	
	public void testImage(String filename) {
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
		
        img = VideoUtil.matToImage(image);
        
        repaint();
	}
	
	public void setImage(Mat image, Mat debugImage) {
		img = VideoUtil.matToImage(image);
		if (debugImage != null) {
			debugImg = VideoUtil.matToImage(debugImage);
		}
		repaint();
	}
	
	public void paintComponent(Graphics g) {
		if (img == null)
			return;
	    int x = 0;
	    int y = 0;
	    g.drawImage(img, x, y, 
	    		getWidth()/2,
	    		getHeight(),
	    		null);
	    if (debugImg != null)
		    g.drawImage(debugImg, getWidth()/2+x, y, 
		    		getWidth()/2,
		    		getHeight(),
		    		null);
	}		
}
