package com.test.util;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.highgui.Highgui;

public class VideoUtil {

	public static BufferedImage matToImage(Mat mat) {
		MatOfByte matOfByte = new MatOfByte();
		Highgui.imencode(".jpg", mat, matOfByte);
		byte[] byteArray = matOfByte.toArray();
		BufferedImage bufImage = null;
		InputStream in = new ByteArrayInputStream(byteArray);
		try {
			bufImage = ImageIO.read(in);
		} catch(IOException e) {
			// no action
		}
		return bufImage;
	}
}
