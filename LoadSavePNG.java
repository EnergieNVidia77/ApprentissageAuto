import javax.imageio.ImageIO;

import java.awt.image.BufferedImage;
import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class LoadSavePNG
{
	public static int K;
	public static int D;
	
	static String path = "C:\\Users\\Toufic\\Documents\\M&Ms projet\\";
    static String imageMMS = path + "mms.png";

	public static boolean checkClass(double[] r, int idx) {
		int highestIdx = 0;
		double highest = 0;
		for (int i = 0; i < r.length; i++) {
			if (r[i] > highest) {
				highest = r[i];
				highestIdx = i;
			}
		}
		return highestIdx == idx;
	}

	public static void main(String[] args) throws IOException
	{

		// Lecture de l'image ici
		BufferedImage bui = ImageIO.read(new File(imageMMS));

		int width = bui.getWidth();
		int height = bui.getHeight();

		int[] im_pixels = bui.getRGB(0, 0, width, height, null, 0, width);

		/** Creation du tableau **/
		System.out.println("Creation of temporary database");
		 
		 double[][] data = MixGauss.genData(im_pixels);
		 double[][] centres = MixGauss.genCenter();
		 
		double[][] var = MixGauss.InitVariance();
		double[] dens = MixGauss.InitDensite();
		
		double[][] r = new double[data.length][centres.length];
		
		double eps=0.001;
		double maj = 10;
		int nbEpoch = 0;
		while(maj > eps) {
			System.out.println("Epoque #" + nbEpoch);
			r = MixGauss.Assigner(data, centres, var, dens);
			maj = MixGauss.Deplct(data, centres, r, var, dens);
			System.out.println(maj);
			nbEpoch++;
		}
		System.out.println("\nNb epoques: " + nbEpoch + "\n");
		System.out.println("Proba pt1: " + Arrays.toString(r[1]) + "\nProba pt4000: " + Arrays.toString(r[4000]));
		System.out.println("Pos ctr1: " + Arrays.toString(centres[1]) + "\nPos ctr2: " + Arrays.toString(centres[2]) + "\n");

		BufferedImage res = new BufferedImage(bui.getWidth(), bui.getHeight(), BufferedImage.TYPE_INT_RGB);
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				int idx = i*width+j;

				if (checkClass(r[idx], 3)) {
					res.setRGB(j, i, bui.getRGB(j, i));
				} else {
					res.setRGB(j, i, Color.black.getRGB());
				}
			}
		}
		ImageIO.write(res, "PNG", new File(path + "res.png"));
	}
}