package com.test;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

public class VideoPanel extends JPanel {
	private static final long serialVersionUID = 5178434061598453625L;
	//private static Logger logger = ConsoleLogger.getLogger(VideoPanel.class.getName());
	
	private Image img = null;
	private Image debugImg = null;
	
	VideoPanel() {
	}
	
	public void setImages(BufferedImage image, BufferedImage debugImage) {
		img = image;
		debugImg = debugImage;
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
