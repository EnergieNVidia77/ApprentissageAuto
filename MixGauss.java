import java.awt.Color;
import java.util.Arrays;

public class MixGauss {
	static int D = 3; // deux dimensions
	static int K = 8; // trois centres
	
	public static double[][] genData(int[] tabPix) {
		double[][] result = new double[tabPix.length][D];
		for(int i = 0; i < tabPix.length; i++) {
			Color c = new Color(tabPix[i]);
			result[i][0] = (double) c.getRed()/255;
			result[i][1] = (double) c.getGreen()/255;
			result[i][2] = (double) c.getBlue()/255;
		}
		return result;
	}
	public static double[][] genCenter(){
		double[][] result = new double[K][D];
		//fond inversé
        result[0][0] = 59.0/255.0;
        result[0][1] = 78.0/255.0;
        result[0][2] = 147.0/255.0;
        
        //reflet inversé
        result[1][0] = 98.0/255.0;
        result[1][1] = 95.0/255.0;
        result[1][2] = 102.0/255.0;
        
        //jaune inversé
        result[2][0] = 32.0/255.0;
        result[2][1] = 35.0/255.0;
        result[2][2] = 255.0/255.0;
        
        //vert inversé
        result[3][0] = 216.0/255.0;
        result[3][1] = 48.0/255.0;
        result[3][2] = 206.0/255.0;
        
        //bleu inversé
        result[4][0] = 255.0/255.0;
        result[4][1] = 151.0/255.0;
        result[4][2] = 96.0/255.0;
        
        //rouge inversé
        result[5][0] = 90.0/255.0;
        result[5][1] = 206.0/255.0;
        result[5][2] = 221.0/255.0;
        
        //orange inversé
        result[6][0] = 4.0/255.0;
        result[6][1] = 160.0/255.0;
        result[6][2] = 245.0/255.0;
        
        //noir inversé
        result[7][0] = 196.0/255.0;
        result[7][1] = 207.0/255.0;
        result[7][2] = 229.0/255.0;
        
		return result;
	}

	
	public static double[][] InitVariance() {
		double[][] var = new double[K][D];
		for(int i = 0; i < var.length; i++) {
			for(int j = 0; j < var[i].length; j++) {
				var[i][j] = 0.00007;
			}
		}
		return var;
	}
	
	public static double[] InitDensite() {
		double[] p = new double[K];
        for(int i = 0; i<p.length; i++){
        	p[i] = 1.0/(double) K;
        }
        return p;
	}

	public static double dist(double[] pt1, double[] pt2) {
		double distance = 0;
		for (int i = 0; i < pt1.length; i++) {
			distance += Math.pow(pt1[i] - pt2[i], 2);
		}
		return Math.sqrt(distance);
	}
	

	public static double[][] Assigner(double[][] data, double[][] center, double[][] variance, double[] densite){
		double[][] result = new double[data.length][center.length];
		for(int d = 0; d < result.length; d++) {
			double sum = 0;
			for(int k = 0; k < result[d].length; k++) {
				double dens = densite[k];
				for(int i = 0; i < data[d].length; i++) {
					dens = dens*(1/Math.sqrt(2*Math.PI*variance[k][i]));
					dens = dens*Math.exp(-((data[d][i]-center[k][i])*(data[d][i]-center[k][i]))/(2*variance[k][i]));
				}
				sum += dens;
				result[d][k] = dens;
			}
			for(int k = 0; k < result[d].length; k++) {
				result[d][k] = result[d][k]/sum;
			}
		}
		return result;
	}

	public static double[] Rk(double[][] r, double[] rho) {
		double[] R = new double[rho.length];
		for (int k = 0; k < rho.length; k++) {
			for (int d = 0; d < r.length; d++) {
				R[k] += r[d][k];
			}
		}
		return R;
	}

	public static double[][] updateM(double[][] r, double[][] x, double[][] m, double[] R) {
		for (int k = 0; k < m.length; k++) {
			for (int i = 0; i < m[k].length; i++) {
				double sum = 0;
				for (int d = 0; d < x.length; d++) {
					sum += r[d][k] * x[d][i];
				}
				m[k][i] = sum / R[k];
			}
		}
		return m;
	}

	public static void updateVar(double[][] r, double[][] x, double[][] m, double[] R, double[][] variance) {
		for (int k = 0; k < variance.length; k++) {
			for (int i = 0; i < variance[k].length; i++) {
				double sum = 0;
				for (int d = 0; d < x.length; d++) {
					sum += r[d][k] * Math.pow(x[d][i] - m[k][i], 2);
				}
				variance[k][i] = sum / R[k];
			}
		}
	}

	public static void updateRho(double[] R, double M, double[] rho) {
		for (int k = 0; k < rho.length; k++) {
			rho[k] = R[k] / M;
		}
	}

	/**
	 * @param x n-dimension data
	 * @param m n-dimension centers
	 * @param r Nearest center index for each data
	 * @return Total deplacement of the centers
	 */
	public static double Deplct(double[][] x, double[][] m, double[][] r, double[][] variance, double[] rho) {
		double tot = 0;
		double[] R = Rk(r, rho);
		double[][] oldm = new double[m.length][m[0].length];
		for (int i = 0; i < m.length; i++) {
			for (int j = 0; j < m[i].length; j++) {
				oldm[i][j] = m[i][j];
			}
		}

		m = updateM(r, x, m, R);
		updateVar(r, x, m, R, variance);
		updateRho(R, x.length, rho);
		for (int i = 0; i < m.length; i++) {
			tot += dist(oldm[i], m[i]);
		}
		System.out.println("Tot: " + tot);
		return tot;
	}
}

