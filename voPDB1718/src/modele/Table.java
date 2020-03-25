package modele;

import java.util.Optional;

public abstract class Table {
	// code unique de la table
	private final String code;

	private final int nbPersonnes;
	private Commande commande;

	public int getNbPersonnes() {
		return nbPersonnes;
	}

	public Table(String code, int nbPersonnes) {
		this.nbPersonnes = nbPersonnes;
		this.code = code;
		commande = null;
	}

	public Optional<Commande> getCommande() {
		return Optional.ofNullable(commande);
	}

	public String getCode() {
		return code;
	}

	public void setCommande(Commande commande) {
		this.commande = commande;
	}

}
