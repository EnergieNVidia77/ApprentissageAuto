import javax.imageio.ImageIO;

import java.awt.image.BufferedImage;
import java.awt.Color;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class LoadSavePNG
{
	public static int K = 10;
	public static int D = 3;

	static String path = System.getProperty("user.dir") + "/src/";
	static String imageMMS = path + "test.png";

	static String Savepath = System.getProperty("user.dir") + "/";

	public static void imageWriting(BufferedImage bui, Color[] tabColor, int height, int width, double[][] r, int n, boolean invert) throws IOException {
		for(int c = 0; c < K; c++){
			Color[] pic = new Color[tabColor.length];
			for(int i = 0; i < r.length; i++){
				double max = 0;
				int ind = 0;
				for(int j=0; j<r[i].length; j=j+1){
					if(max < r[i][j]){
						max = r[i][j];
						ind = j;
					}
				}
				if(ind == c){
					pic[i] = tabColor[i];
				}
				else{
					if (invert)
						pic[i] = new Color(255, 255, 255);
					else
						pic[i] = new Color(0, 0, 0);
				}
			}
			for(int i=0; i<pic.length; i++){
				if (invert)
					pic[i] = new Color(255-pic[i].getRed(),255-pic[i].getGreen(),255-pic[i].getBlue());
				else
					pic[i] = new Color(pic[i].getRed(),pic[i].getGreen(), pic[i].getBlue());
			}

			System.out.println("Saving new image !");

			/** sauvegarde de l'image **/
			BufferedImage bui_out = new BufferedImage(bui.getWidth(),bui.getHeight(),BufferedImage.TYPE_3BYTE_BGR);
			for(int i=0 ; i<height ; i++)
			{
				for(int j=0 ; j<width ; j++)
				{
					bui_out.setRGB(j,i,pic[i*width+j].getRGB());
				}
			}
			ImageIO.write(bui_out, "PNG", new File(Savepath +"image#" + n + "_" + c +".png"));
		}
	}

	public static void writeData(ArrayList<Double> data) throws IOException {
		File file = new File("data.d");
		file.createNewFile();
		FileWriter fw = new FileWriter(file);
		for (int i = 0; i < data.size(); i++) {
			fw.write(i + " " + data.get(i) + "\n");
		}
		fw.close();
	}

	public static void writeScore(double[] data) throws IOException{
		File file = new File("dataScore.d");
		file.createNewFile();
		FileWriter fw = new FileWriter(file);
		for (int i = 0; i < data.length; i++) {
			fw.write(i + " " + data[i] + "\n");
		}
		fw.close();
	}

	public static void base() throws IOException {
		K = 8;
		BufferedImage bui = ImageIO.read(new File(imageMMS));
		int width = bui.getWidth();
		int height = bui.getHeight();
		int[] im_pixels = bui.getRGB(0, 0, width, height, null, 0, width);
		double[][] data = MixGauss.genData(im_pixels);
		double[][] centres = MixGauss.genCenter(K);
		double[][] var = MixGauss.InitVariance();
		double[] dens = MixGauss.InitDensite();
		double[][] r = new double[data.length][centres.length];
		double eps=0.001;
		double maj = 10;
		int nbEpoch = 0;
		ArrayList<Double> majs = new ArrayList<Double>();

		while(maj > eps) {
			System.out.println("Epoque #" + nbEpoch);
			r = MixGauss.Assigner(data, centres, var, dens);
			maj = MixGauss.Deplct(data, centres, r, var, dens);
			majs.add(maj);
			System.out.println("Maj : " + maj);
			nbEpoch++;
		}
		writeData(majs);
		System.out.println("\nNb epoques: " + nbEpoch);
		System.out.println("Score: " + MixGauss.score(data, centres, var, dens) + "\n");

		Color[] tabColor = new Color[im_pixels.length];
		for(int i = 0 ; i<im_pixels.length ; i++){
			tabColor[i] = new Color(im_pixels[i]);
		}

		System.out.println("Begenning writing...");
		imageWriting(bui, tabColor, height, width, r, 42, true);
		System.out.println("End of programm");
	}

	public static void question1() throws IOException {
		K = 10;
		BufferedImage bui = ImageIO.read(new File(imageMMS));
		int width = bui.getWidth();
		int height = bui.getHeight();
		int[] im_pixels = bui.getRGB(0, 0, width, height, null, 0, width);
		double[] scores = new double[10];
		double[][] data = MixGauss.genData(im_pixels);

		for (int n = 0; n < 10; n++) {
			double[][] centres = MixGauss.randomGenCenter(K);
			double[] dens = MixGauss.InitDensite();
			double[][] var = MixGauss.randomInitVariance();
			double[][] r = new double[data.length][centres.length];
			double eps = 0.001;
			double maj = 10;
			int nbEpoch = 0;

			while (maj > eps) {
				r = MixGauss.Assigner(data, centres, var, dens);
				maj = MixGauss.Deplct(data, centres, r, var, dens);
				nbEpoch++;
			}
			double score = MixGauss.score(data, centres, var, dens);
			scores[n] = score;

			Color[] tabColor = new Color[im_pixels.length];

			for (int i = 0; i < im_pixels.length; i++) {
				tabColor[i] = new Color(im_pixels[i]);
			}
			imageWriting(bui, tabColor, height, width, r, n, true);
			System.out.println("End of loop #" + n);
		}
		writeScore(scores);
		System.out.println("End of programm");
	}

	public static void question2() throws IOException {
		BufferedImage bui = ImageIO.read(new File(imageMMS));
		int width = bui.getWidth();
		int height = bui.getHeight();
		int[] im_pixels = bui.getRGB(0, 0, width, height, null, 0, width);
		double[] scores = new double[90];
		double[][] data = MixGauss.genData(im_pixels);

		System.out.println("Creation of temporary database");

		for (int k = 0; k <= 8; k++) {
			K = k+2;
			for (int n = 0; n < 10; n++) {
				double[][] centers = MixGauss.randomGenCenter(K);
				double[] dens = MixGauss.InitDensite();
				double[][] var = MixGauss.randomInitVariance();
				double[][] r = new double[data.length][centers.length];
				double eps = 0.001;
				double maj = 10;
				int nbEpoch = 0;

				while (maj > eps) {
					r = MixGauss.Assigner(data, centers, var, dens);
					maj = MixGauss.Deplct(data, centers, r, var, dens);
					nbEpoch++;
				}
				double score = MixGauss.score(data, centers, var, dens);
				scores[k*10 + n] = score;
				System.out.println("End of loop #" + k + " " + n);
			}
		}
		writeScore(scores);
		System.out.println("End of programm");
	}

	public static void question3() throws IOException {
		K = 11;
		BufferedImage bui = ImageIO.read(new File(System.getProperty("user.dir") + "/src/lego.jpg"));
		int width = bui.getWidth();
		int height = bui.getHeight();
		int[] im_pixels = bui.getRGB(0, 0, width, height, null, 0, width);
		double[][] data = MixGauss.genData(im_pixels);
		double[][] centres = MixGauss.genLegoCenter(K);
		double[][] var = MixGauss.InitVariance();
		double[] dens = MixGauss.InitDensite();
		double[][] r = new double[data.length][centres.length];
		double eps=0.09;
		double maj = 10;
		int nbEpoch = 0;
		ArrayList<Double> majs = new ArrayList<Double>();

		while(maj > eps) {
			System.out.println("Epoque #" + nbEpoch);
			r = MixGauss.Assigner(data, centres, var, dens);
			maj = MixGauss.Deplct(data, centres, r, var, dens);
			majs.add(maj);
			System.out.println("Maj : " + maj);
			nbEpoch++;
		}
		writeData(majs);
		System.out.println("\nNb epoques: " + nbEpoch);
		System.out.println("Score: " + MixGauss.score(data, centres, var, dens) + "\n");

		Color[] tabColor = new Color[im_pixels.length];
		for(int i = 0 ; i<im_pixels.length ; i++){
			tabColor[i] = new Color(im_pixels[i]);
		}

		System.out.println("Begenning writing...");
		imageWriting(bui, tabColor, height, width, r, 0, true);
		System.out.println("End of programm");
	}

	public static void main(String[] args) throws IOException {
		//base();
		//question1();
		//question2();
		question3();
	}

}