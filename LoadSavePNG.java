import javax.imageio.ImageIO;

import java.awt.image.BufferedImage;
import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class LoadSavePNG
{
	public static int K = 8;
	public static int D = 3;
	
	static String path = "C:\\Users\\Toufic\\Documents\\M&Ms projet\\";
    static String imageMMS = path + "test.png";
    
    static String Savepath = "C:\\Users\\Toufic\\Pictures\\ProjetIA\\";
    
    public static void imageWriting(BufferedImage bui, Color[] tabColor, int height, int width, double[][] r) throws IOException {
		for(int c = 2; c < K; c++){
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
					pic[i] = new Color(255, 255, 255);
				}
			}
			for(int i=0; i<pic.length; i++){
				pic[i] = new Color(255-pic[i].getRed(),255-pic[i].getGreen(),255-pic[i].getBlue());
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
	        ImageIO.write(bui_out, "PNG", new File(Savepath +"image"+(c-1)+".png"));
		}
    }
    
	public static void main(String[] args) throws IOException {

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
			System.out.println("Maj : " + maj);
			nbEpoch++;
		}
		System.out.println("\nNb epoques: " + nbEpoch + "\n");
		/*System.out.println("Proba pt1: " + Arrays.toString(r[1]) + "\nProba pt4000: " + Arrays.toString(r[4000]));
		System.out.println("Pos ctr1: " + Arrays.toString(centres[1]) + "\nPos ctr2: " + Arrays.toString(centres[2]) + "\n");*/
		
		Color[] tabColor = new Color[im_pixels.length];
        
        for(int i = 0 ; i<im_pixels.length ; i++){
        	tabColor[i] = new Color(im_pixels[i]);
        }
        
        System.out.println("Begenning writing...");
        imageWriting(bui, tabColor, height, width, r);
		System.out.println("End of programm");
	}
		
}