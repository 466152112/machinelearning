package MF.bean;

public class Feature {
	private double[] feature;

	public double[] getFeature() {
		return feature;
	}

	public void setFeature(double[] feature) {
		this.feature = feature;
	}
	
	public double[] copyFeature() {
		double result[]=feature.clone();
		return result;
	}/**
	 * 
	 */
	public Feature() {
		// TODO Auto-generated constructor stub
	}
	public void addFeature(double[] addfeature){
		if (this.feature.length!=addfeature.length) {
			System.out.println("error in addFeature");
			System.exit(0);
		}
		for (int i = 0,imax=addfeature.length; i <imax ; i++) {
			feature[i]+=addfeature[i];
		}
	}
	
}
