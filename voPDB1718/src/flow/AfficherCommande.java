package flow;

import flow.CommandeFlowControl.ActionCommande;
import modele.Commande.EtatCmd;

public class AfficherCommande implements Etats {
	CommandeFlowControl flowCtrl;
	// Action à faire pour afficher une commande
	private ActionCommande afficheCommande;

	public AfficherCommande(CommandeFlowControl flowCtrl, ActionCommande afficheCommande) {
		this.flowCtrl = flowCtrl;
		this.afficheCommande = afficheCommande;
	}

	@Override
	public void actionIn() {
		// Affiche la vue avec la commande sélectionnée
		afficheCommande.execute(flowCtrl);
	}

	@Override
	public void choisirArticle() {
		// Change d'état pour choisir un article
		if (flowCtrl.getSelectedCommande().getEtat() == EtatCmd.EN_COURS)
			flowCtrl.setEtat(CommandeFlowControl.Etat.AFFICHER_ARTICLES);
		else {// On ne peut pas rajouter des lignes car la commande est clôturée
			CommandeFlowControl.afficheErreur("Etat AfficherCommande",
					" impossible de rajouter des articles sur une commande clôturée");

		}
	}

	@Override
	public void exit() {
		// retourne dans l'état initial
		flowCtrl.start();

	}

}
