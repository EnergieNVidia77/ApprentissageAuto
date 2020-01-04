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

	public static double[][] initialiseM(int K, int D) {
		double[][] m = new double[K][D];
		m[0] = new double[]{231/255.0, 221/255.0, 62/255.0};
		m[1] = new double[]{28/255.0, 113/255.0, 193/255.0};
		m[2] = new double[]{98/255.0, 228/255.0, 84/255.0};
		m[3] = new double[]{231/255.0, 117/255.0, 52/255.0};
		m[4] = new double[]{44/255.0, 51/255.0, 41/255.0};
		m[5] = new double[]{173/255.0, 23/255.0, 39/255.0};
		return m;
	}

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
		String path = System.getProperty("user.dir") + "/src/";
		String imageMMS = path + "mms.png";

		// Lecture de l'image ici
		BufferedImage bui = ImageIO.read(new File(imageMMS));

		int width = bui.getWidth();
		int height = bui.getHeight();
		System.out.println("Hauteur=" + width);
		System.out.println("Largeur=" + height);

		int pixel = bui.getRGB(0, 0);
		//System.out.println("Pixel 0,0 = "+pixel);
		Color c = new Color(pixel);
		System.out.println("RGB = "+c.getRed()+" "+c.getGreen()+" "+c.getBlue());
		// Calcul des trois composant de couleurs normalisé à 1
		double[] pix = new double[3];
		pix[0] = (double) c.getRed()/255.0;
		pix[1] = (double) c.getGreen()/255.0;
		pix[2] = (double) c.getBlue()/255.0;
		System.out.println("RGB normalisé= "+pix[0]+" "+pix[1]+" "+pix[2] + "\n");

		int[] im_pixels = bui.getRGB(0, 0, width, height, null, 0, width);

		/** Creation du tableau **/
		Color[] tabColor= new Color[im_pixels.length];
		for(int i=0 ; i<im_pixels.length ; i++)
			tabColor[i]=new Color(im_pixels[i]);

		/** inversion des couleurs **/
		for(int i=0 ; i<tabColor.length ; i++)
			tabColor[i]=new Color(255-tabColor[i].getRed(),255-tabColor[i].getGreen(),255-tabColor[i].getBlue());

		/** sauvegarde de l'image **/
		BufferedImage bui_out = new BufferedImage(bui.getWidth(),bui.getHeight(),BufferedImage.TYPE_3BYTE_BGR);
		for(int i=0 ; i<height ; i++)
		{
			for(int j=0 ; j<width ; j++)
				bui_out.setRGB(j,i,tabColor[i*width+j].getRGB());
		}
		ImageIO.write(bui_out, "PNG", new File(path+"test.png"));

		int K = 6;
		int D = 3;
		double eps=0.001;
		double maj = 10;
		double[][] x = new double[width*height][D];
		double[][] m = new double[K][D];
		double[][] r = new double[K][D];

		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				int idx = i*width + j;
				x[idx][0] = tabColor[idx].getRed()/255.0;
				x[idx][1] = tabColor[idx].getBlue()/255.0;
				x[idx][2] = tabColor[idx].getGreen()/255.0;
			}
		}
		m = initialiseM(K, D);
		MixGauss.setup(K, D);

		System.out.println("Pos ctr1: " + Arrays.toString(m[1]) + "\nPos ctr2: " + Arrays.toString(m[2]) + "\n");
		int nbEpoch = 0;
		while(maj > eps) {
			System.out.println("Epoque #" + nbEpoch);
			r = MixGauss.Assign(x, m);
			maj = MixGauss.Deplct(x, m, r);
			nbEpoch++;
		}
		System.out.println("\nNb epoques: " + nbEpoch + "\n");
		System.out.println("Proba pt1: " + Arrays.toString(r[1]) + "\nProba pt4000: " + Arrays.toString(r[4000]));
		System.out.println("Pos ctr1: " + Arrays.toString(m[1]) + "\nPos ctr2: " + Arrays.toString(m[2]) + "\n");

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