package modele;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import modele.LigneCommande.EtatLigne;

public class Commande {
	public enum EtatCmd {
		EN_COURS, PAYEE, ANNULEE, PERTE
	}

	private Integer num = null;
	private LocalDateTime momentA = null;
	private LocalDateTime momentS = null;
	private EtatCmd etat;
	private Double total;
	private final Serveur serveur;
	private List<LigneCommande> articles;

	/**
	 * Constructeur pour créer une nouvelle cmd à sauver dans la BD
	 * 
	 * @param serveur
	 */
	public Commande(Serveur serveur) {
		this.serveur = serveur;
		etat = EtatCmd.EN_COURS;
		total = 0.0;
		articles = new ArrayList<>();
	}

	/**
	 * Pour charger une commande existante dans la BD
	 * 
	 * @param num
	 * @param momentA
	 * @param momentS
	 * @param etat
	 * @param total
	 * @param serveur
	 */
	public Commande(Integer num, LocalDateTime momentA, LocalDateTime momentS, EtatCmd etat, Double total,
			Serveur serveur, List<LigneCommande> articles) {

		this.num = num;
		this.momentA = momentA;
		this.momentS = momentS;
		this.etat = etat;
		this.total = total;
		this.serveur = serveur;
		this.articles = articles;
	}

	public Integer getNum() {
		return num;
	}

	public void setNum(Integer num) {
		this.num = num;
	}

	public LocalDateTime getMomentA() {
		return momentA;
	}

	public void setMomentA(LocalDateTime momentA) {
		this.momentA = momentA;
	}

	public LocalDateTime getMomentS() {
		return momentS;
	}

	public void setMomentS(LocalDateTime momentS) {
		this.momentS = momentS;
	}

	public EtatCmd getEtat() {
		return etat;
	}

	public void setEtat(EtatCmd etat) {
		this.etat = etat;
	}

	public Double getTotal() {
		// reCalcul le total
		if (articles.isEmpty())
			total = 0.0;
		else
			total = articles.stream().filter((c) -> c.getEtat() != EtatLigne.ERREUR && c.getEtat() != EtatLigne.ANNULE)
					.mapToDouble((c) -> c.getPrix()).sum();
		return total;
	}

//	public void setTotal(Double total) {
//		this.total = total;
//	}

	public Serveur getServeur() {
		return serveur;
	}

	/**
	 * Permet de rajouter une ligne de commande
	 * 
	 * @return
	 */
	public List<LigneCommande> getArticles() {
		return articles;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((etat == null) ? 0 : etat.hashCode());
		result = prime * result + ((momentA == null) ? 0 : momentA.hashCode());
		result = prime * result + ((momentS == null) ? 0 : momentS.hashCode());
		result = prime * result + ((num == null) ? 0 : num.hashCode());
		result = prime * result + ((serveur == null) ? 0 : serveur.hashCode());
		result = prime * result + ((total == null) ? 0 : total.hashCode());
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
		Commande other = (Commande) obj;
		if (etat != other.etat)
			return false;
		if (momentA == null) {
			if (other.momentA != null)
				return false;
		} else if (!momentA.equals(other.momentA))
			return false;
		if (momentS == null) {
			if (other.momentS != null)
				return false;
		} else if (!momentS.equals(other.momentS))
			return false;
		if (num == null) {
			if (other.num != null)
				return false;
		} else if (!num.equals(other.num))
			return false;
		if (serveur == null) {
			if (other.serveur != null)
				return false;
		} else if (!serveur.equals(other.serveur))
			return false;
		if (total == null) {
			if (other.total != null)
				return false;
		} else if (!total.equals(other.total))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Commande [num=" + num + ", momentA=" + momentA + ", momentS=" + momentS + ", etat=" + etat + ", total="
				+ total + ", serveur=" + serveur + "]";
	}

}
