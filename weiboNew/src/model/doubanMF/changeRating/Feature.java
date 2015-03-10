package model.doubanMF.changeRating;

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
	}
	
}
