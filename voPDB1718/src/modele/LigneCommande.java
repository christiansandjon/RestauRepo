package modele;

public class LigneCommande {

	public enum EtatLigne {
		INIT, CUISINE, PRET, SERVI, ANNULE, ERREUR
	}

	// Id d'une ligne
	public static final class Id {
		private final Integer numCmd;
		private final Integer numLigne;

		public Id(Integer numCmd, Integer numLigne) {
			this.numCmd = numCmd;
			this.numLigne = numLigne;
		}

		public Integer getNumCmd() {
			return numCmd;
		}

		public Integer getNumLigne() {
			return numLigne;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((numCmd == null) ? 0 : numCmd.hashCode());
			result = prime * result + ((numLigne == null) ? 0 : numLigne.hashCode());
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
			Id other = (Id) obj;
			if (numCmd == null) {
				if (other.numCmd != null)
					return false;
			} else if (!numCmd.equals(other.numCmd))
				return false;
			if (numLigne == null) {
				if (other.numLigne != null)
					return false;
			} else if (!numLigne.equals(other.numLigne))
				return false;
			return true;
		}

		@Override
		public String toString() {
			return "Id [numCmd=" + numCmd + ", numLigne=" + numLigne + "]";
		}
    	
	}

	private Id id;
	private final Article article;
	private EtatLigne etat;
	private Boolean payee;
	private Double prix;
		
	public LigneCommande(Id id, Article article, EtatLigne etat, Boolean payee, Double prix) {
		this.id = id;
		this.article = article;
		this.etat = etat;
		this.payee = payee;
		this.prix = prix;
	}

	public LigneCommande(Id id, Article article, Double prix) {
		this.id = id;
		this.article = article;
		this.prix = prix;
	}

	public LigneCommande(Id id, Article article) {
		this(id,article,article.getPrix());
	}
	/**
	 * Spécifie le numéro de ligne lorsqu'une nouvelle ligne de commande est crée
	 * et que le numéro de ligne est spécifié par la BD
	 * 
	 * @param num celui généré par la BD
	 */
	public void setNumLigne(Integer num) {
		id=new Id(id.numCmd,num);
	}

	public Id getId() {
		return id;
	}

	public void setId(Id id) {
		this.id = id;
	}

	public EtatLigne getEtat() {
		return etat;
	}

	public void setEtat(EtatLigne etat) {
		this.etat = etat;
	}

	public Boolean getPayee() {
		return payee;
	}

	public void setPayee(Boolean payee) {
		this.payee = payee;
	}

	public Double getPrix() {
		return prix;
	}

	public void setPrix(Double prix) {
		this.prix = prix;
	}

	public Article getArticle() {
		return article;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((article == null) ? 0 : article.hashCode());
		result = prime * result + ((etat == null) ? 0 : etat.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((payee == null) ? 0 : payee.hashCode());
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
		LigneCommande other = (LigneCommande) obj;
		if (article == null) {
			if (other.article != null)
				return false;
		} else if (!article.equals(other.article))
			return false;
		if (etat != other.etat)
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (payee == null) {
			if (other.payee != null)
				return false;
		} else if (!payee.equals(other.payee))
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
		return "LigneCommande [id=" + id + ", article=" + article + ", etat=" + etat + ", payee=" + payee + ", prix="
				+ prix + "]";
	}
	
	
	
	

}
