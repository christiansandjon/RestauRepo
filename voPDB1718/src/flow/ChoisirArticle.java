package flow;

import flow.CommandeFlowControl.ActionArticles;
import flow.CommandeFlowControl.Etat;
import modele.Article;
import modele.Commande;
import modele.LigneCommande;
import modele.LigneCommande.Id;

public class ChoisirArticle implements Etats {
	CommandeFlowControl flowCtrl;
	// Action à faire pour afficher une commande
	private ActionArticles afficheArticles;

	public ChoisirArticle(CommandeFlowControl flowCtrl, ActionArticles afficheArticles) {
		this.flowCtrl = flowCtrl;
		this.afficheArticles = afficheArticles;
	}

	@Override
	public void actionIn() {
		//Affiche pour la sélection la liste des articles
		afficheArticles.execute(flowCtrl,flowCtrl.getFabrique().getArticleDAO().getListe(""));
	}

	
	@Override
	/**
	 * On désire rajouter un article à la commande
	 */
	public void ajoutArticleCMD(Commande c, Article a) {
				
		//Ajoute une ligne de commande dans la BD et obtenir un num ligne
		try {
			//Création d'une ligne sans numéro
			LigneCommande ligne=new LigneCommande(new Id(c.getNum(),null ),a);
			//Sauvegarde dans BD pour initialiser les champs comme num_ligne,...
			flowCtrl.getFabrique().getLigneCommandeDAO().insert(ligne);
			//ajoute la ligne à la commande avec son numéro
			c.getArticles().add(ligne);
		} catch (Exception e) {
			e.printStackTrace();
		}
		//On revient dans l'état
		flowCtrl.setEtat(Etat.AFFICHER_COMMANDE);
		
	}

	@Override
	public void exit() {
		//retourne dans l'état initial et indique qu'auncun élément est sélectionné
		flowCtrl.start();
	}

}
