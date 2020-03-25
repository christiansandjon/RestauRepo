package flow;

import modele.Article;
import modele.Commande;
import modele.Serveur;
import modele.Table;

public interface Etats {
	default void selectTable(Table t) {
		traitementErreur("selectTable");
	};

	default void selectServeur(Serveur t) {
		traitementErreur("selectServeur");
	};
	
	default void choisirArticle() {
		traitementErreur("ChoisirArticle");
	}

	default void ajoutArticleCMD(Commande c, Article A) {
		traitementErreur("ajoutArticleCMD");
	};
	//action à faire à l'entrée de l'état
	void actionIn();

	default void exit() {
		traitementErreur("exit");
	}
	
	static void traitementErreur(String event) {
		CommandeFlowControl.afficheErreur("Transition Invalide ","l'évènement "+ event+" ne peut pas être appelé sur l'état courant");
	}
}
