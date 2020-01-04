import java.util.Arrays;

public class MixGauss {
	static int D; // deux dimensions
	static int K; // trois centres
	static double[][] variance;
	static double[] rho;


	public static void setup(int k, int d) {
		K = k;
		D = d;
		variance = new double[K][D];
		rho = new double[K];

		for (int i = 0; i < variance.length; i++) {
			rho[i] = 1.0/K;
			for (int j = 0; j < variance[i].length; j++) {
				variance[i][j] = 0.00010;
			}
		}
	}

	public static double dist(double[] pt1, double[] pt2) {
		double distance = 0;
		for (int i = 0; i < pt1.length; i++) {
			distance += Math.pow(pt1[i] - pt2[i], 2);
		}
		return Math.sqrt(distance);
	}

	public static double calculate(double[] x, double[][] m, int k) {
		double tot = rho[k];
		for (int i = 0; i < x.length; i++) {
			tot *= (1 / Math.sqrt(2 * Math.PI * variance[k][i])) * Math.exp(-(x[i] - m[k][i]) / (2 * variance[k][i]));
		}
		return tot;
	}

	/**
	 * @param x n-dimension data
	 * @param m n-dimension centers
	 * @return Nearest center of each data
	 */
	public static double[][] Assign(double[][] x, double[][] m) {
		double[][] r = new double[x.length][m.length];
		for (int d = 0; d < r.length; d++) {
			float tot = 0;
			for (int k = 0; k < r[d].length; k++) {
				r[d][k] = calculate(x[d], m, k);
				tot += r[d][k];
			}
			for (int k = 0; k < r[d].length; k++) {
				r[d][k] /= tot;
			}
		}
		return r;
	}

	public static double[] Rk(double[][] r) {
		double[] R = new double[r[0].length];
		for (int k = 0; k < r[0].length; k++) {
			for (int d = 0; d < r.length; d++) {
				R[k] += r[d][k];
			}
		}
		return R;
	}

	public static double[][] updateM(double[][] r, double[][] x, double[][] m, double[] R) {
		for (int k = 0; k < m.length; k++) {
			for (int i = 0; i < x[k].length; i++) {
				double sum = 0;
				for (int d = 0; d < x.length; d++) {
					sum += r[d][k] * x[d][i];
				}
				m[k][i] = sum / R[k];
			}
		}
		return m;
	}

	public static void updateVar(double[][] r, double[][] x, double[][] m, double[] R) {
		for (int k = 0; k < r[0].length; k++) {
			for (int i = 0; i < x[k].length; i++) {
				double sum = 0;
				for (int d = 0; d < x.length; d++) {
					sum += r[d][k] * Math.pow(x[d][i] - m[k][i], 2);
				}
				variance[k][i] = sum / R[k];
			}
		}
	}

	public static void updateRho(double[] R, double M) {
		for (int k = 0; k < R.length; k++) {
			rho[k] = R[k] / M;
		}
	}

	/**
	 * @param x n-dimension data
	 * @param m n-dimension centers
	 * @param r Nearest center index for each data
	 * @return Total deplacement of the centers
	 */
	public static double Deplct(double[][] x, double[][] m, double[][] r) {
		double tot = 0;
		double[] R = new double[rho.length];
		for(int k=0; k<p.length; k=k+1){
			R[k] = 0;
		}
		for(int k=0; k<p.length; k++){
			for(int d=0; d<r.length; d++)
			{
				R[k] = R[k]+r[d][k];
			}
		}
		double[][] oldm = new double[m.length][m[0].length];
		for (int i = 0; i < oldm.length; i++) {
			for (int j = 0; j < oldm[i].length; j++) {
				oldm[i][j] = m[i][j];
			}
		}

		m = updateM(r, x, m, R);
		updateVar(r, x, m, R);
		updateRho(R, x.length);
		for (int i = 0; i < m.length; i++) {
			tot += dist(oldm[i], m[i]);
		}
		System.out.println("Tot: " + tot);
		return tot;
	}
}

