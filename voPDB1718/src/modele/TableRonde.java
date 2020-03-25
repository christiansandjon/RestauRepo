package modele;

public class TableRonde extends Table {
	private Double centreX, centreY;
	private Double Rayon;

	public TableRonde(String code, int nbPersonnes, Double centreX, Double centreY, Double rayon) {
		super(code, nbPersonnes);
		this.centreX = centreX;
		this.centreY = centreY;
		Rayon = rayon;
	}

	public Double getCentreX() {
		return centreX;
	}

	public Double getCentreY() {
		return centreY;
	}

	public Double getRayon() {
		return Rayon;
	}

}
