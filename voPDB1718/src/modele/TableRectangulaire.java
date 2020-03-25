package modele;

public class TableRectangulaire extends Table {
	/**
	 * x,y désigne le point supérieur haut
	 */
	private final Double x, y, largeur, hauteur;

	public TableRectangulaire(String code, int nbPersonnes, Double x, Double y, Double largeur, Double hauteur) {
		super(code, nbPersonnes);
		this.x = x;
		this.y = y;
		this.largeur = largeur;
		this.hauteur = hauteur;
	}

	public Double getX() {
		return x;
	}

	public Double getY() {
		return y;
	}

	public Double getLargeur() {
		return largeur;
	}

	public Double getHauteur() {
		return hauteur;
	}

}
