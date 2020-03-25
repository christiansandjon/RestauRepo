package dao;

import modele.Commande;
import modele.LigneCommande;

public interface ILigneCommandeDAO extends IDAO<LigneCommande,LigneCommande.Id > {
	/**
	 * Permet de supprimer toutes les lignes d'une commande
	 * @param cmd une commande
	 * @return
	 */
	boolean deleteLignesCommande(Commande cmd);
}
