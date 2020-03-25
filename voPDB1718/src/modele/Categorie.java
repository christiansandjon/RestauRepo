package modele;

public class Categorie {

	private final String code;
	private final String nom;
	private Categorie pereCat;

	public Categorie(String code, String nom, Categorie pereCat) {
		super();
		this.code = code;
		this.nom = nom;
		this.pereCat = pereCat;
	}

	public Categorie(String code, String nom) {
		//Appel de l'autre constructeur
		this(code, nom, null);
	}

	public String getCode() {
		return code;
	}

	public String getNom() {
		return nom;
	}

	public Categorie getPereCat() {
		return pereCat;
	}
	
	public void setPereCat(Categorie pereCat) {
		this.pereCat = pereCat;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((code == null) ? 0 : code.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Categorie other = (Categorie) obj;
		if (code == null) {
			if (other.code != null)
				return false;
		} else if (!code.equals(other.code))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Categorie [code=" + code + ", nom=" + nom + "]";
	}

}
