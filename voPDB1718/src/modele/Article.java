package modele;

public class Article {
	// Classe interne
	public enum Etape {
		ENTREE, PLAT, DESSERT, AUCUNE
	};

	private final String code;
	private String nom;
	private String description;
	private Integer calories;
	private Double prix;
	private Boolean dispo;
	private Etape etape;
	private Categorie cat;

	public Article(String code, String nom, String description, Integer calories, Double prix, Boolean dispo,
			Etape etape, Categorie cat) {
		super();
		this.code = code.trim();
		this.nom = nom;
		this.description = description;
		this.calories = calories;
		this.prix = prix;
		this.dispo = dispo;
		this.etape = etape;
		this.cat = cat;
	}

	public String getNom() {
		return nom;
	}

	public void setNom(String nom) {
		this.nom = nom;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Integer getCalories() {
		return calories;
	}

	public void setCalories(Integer calories) {
		this.calories = calories;
	}

	public Double getPrix() {
		return prix;
	}

	public void setPrix(Double prix) {
		this.prix = prix;
	}

	public Boolean getDispo() {
		return dispo;
	}

	public void setDispo(Boolean dispo) {
		this.dispo = dispo;
	}

	public Etape getEtape() {
		return etape;
	}

	public void setEtape(Etape etape) {
		this.etape = etape;
	}

	public Categorie getCat() {
		return cat;
	}

	public void setCat(Categorie cat) {
		this.cat = cat;
	}

	public String getCode() {
		return code;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((calories == null) ? 0 : calories.hashCode());
		result = prime * result + ((cat == null) ? 0 : cat.hashCode());
		result = prime * result + ((code == null) ? 0 : code.hashCode());
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((dispo == null) ? 0 : dispo.hashCode());
		result = prime * result + ((etape == null) ? 0 : etape.hashCode());
		result = prime * result + ((nom == null) ? 0 : nom.hashCode());
		result = prime * result + ((prix == null) ? 0 : prix.hashCode());
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
		Article other = (Article) obj;
		if (calories == null) {
			if (other.calories != null)
				return false;
		} else if (!calories.equals(other.calories))
			return false;
		if (cat == null) {
			if (other.cat != null)
				return false;
		} else if (!cat.equals(other.cat))
			return false;
		if (code == null) {
			if (other.code != null)
				return false;
		} else if (!code.equals(other.code))
			return false;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (dispo == null) {
			if (other.dispo != null)
				return false;
		} else if (!dispo.equals(other.dispo))
			return false;
		if (etape != other.etape)
			return false;
		if (nom == null) {
			if (other.nom != null)
				return false;
		} else if (!nom.equals(other.nom))
			return false;
		if (prix == null) {
			if (other.prix != null)
				return false;
		} else if (!prix.equals(other.prix))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Article [code=" + code + ", nom=" + nom + ", prix=" + prix + "]";
	}

}
