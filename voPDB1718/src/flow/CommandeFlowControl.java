package flow;

import java.util.List;

import dao.DAOFactory;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import modele.Article;
import modele.Commande;
import modele.Serveur;
import modele.Table;

public class CommandeFlowControl {
	public enum Etat {
		CHOISIR_TABLE, CHOISIR_SERVEUR, AFFICHER_COMMANDE, AFFICHER_ARTICLES;
		private Etats etat;

		public Etats getEtat() {
			return etat;
		}

		public void setEtat(Etats etat) {
			this.etat = etat;
		}
	};

	// CHOIX_SERV,AFFICHE_CMD,AFFICHE_ARTICLE,AFFICHE_LIGNECMD}
	// public enum Event {
	// SELECT_TABLE, SELECT_SERVEUR, SELECT_LIGNE, AJOUT_ARTICLE, EXIT
	// };
	/*-----------------------------------------------------------------*/
	@FunctionalInterface
	public interface ActionTable {
		void execute(CommandeFlowControl f, List<Table> tables);
	}

	/*-----------------------------------------------------------------*/
	@FunctionalInterface
	public interface ActionServeur {
		void execute(CommandeFlowControl f, List<Serveur> liste);
	}

	/*-----------------------------------------------------------------*/
	@FunctionalInterface
	public interface ActionCommande {
		void execute(CommandeFlowControl f);
	}

	/*-----------------------------------------------------------------*/
	@FunctionalInterface
	public interface ActionArticles {
		void execute(CommandeFlowControl f, List<Article> liste);
	}

	/*-----------------------------------------------------------------*/
	/**
	 * Permet de découpler la manière d'afficher les messages d'erreur
	 * 
	 * @author Didier
	 *
	 */
	@FunctionalInterface
	public interface AffichageErreur {
		void affiche(String title, String message);
	}

	/*-----------------------------------------------------------------*/
	private Etat etat;

	private DAOFactory fabrique;

	/**
	 * Contient le traitement pour afficher les erreurs. Il utilise comme affichage
	 * par défaut: showError
	 */
	private static AffichageErreur afficheErreur = CommandeFlowControl::showError;
	// (t, m) -> { showError(t, m);};

	private Table selectedTable = null;
	private Commande selectedCommande = null;
	private Serveur selectedServeur = null;

	/*-----------------------------------------------------------------*/
	public CommandeFlowControl(DAOFactory fabrique, ActionTable afficheTable, ActionServeur afficheServeur,
			ActionCommande afficheCommande, ActionArticles afficheArticles) {

		// Fabrique pour avoir accès aux données
		this.fabrique = fabrique;

		// Création des états
		Etat.CHOISIR_TABLE.setEtat(new ChoisirTable(this, afficheTable));
		Etat.CHOISIR_SERVEUR.setEtat(new ChoisirServeur(this, afficheServeur));
		Etat.AFFICHER_COMMANDE.setEtat(new AfficherCommande(this, afficheCommande));
		Etat.AFFICHER_ARTICLES.setEtat(new ChoisirArticle(this, afficheArticles));
		// Se mets dans l'état initial
		etat = Etat.CHOISIR_TABLE;
	}

	/**
	 * Permet de démarrer le flux avec la sélection de la table
	 */
	public void start() {
		selectedTable = null;
		selectedServeur = null;
		selectedCommande = null;
		// se met sans l'état initial
		setEtat(Etat.CHOISIR_TABLE);
	}

	// permet à l'automate de changer d'état
	// ne doit pas être appelé manuellement
	void setEtat(Etat etat) {
		this.etat = etat;
		// Lance l'action dans un autre thread
		// new Thread(()->etat.getEtat().actionIn()).run();
		etat.getEtat().actionIn();
	}

	// Evenement lorsqu'une table est sélectionnée
	public void selectTable(Table t) {
		
		if (t == null)
			setEtat(etat);
		else {
			selectedTable = t;
			etat.getEtat().selectTable(t);
		}
	};

	// Evenement lorsqu'un serveur est sélectionné
	public void selectServeur(Serveur s) {
		
		if (s == null)
			setEtat(etat);
		else {
			selectedServeur = s;
			etat.getEtat().selectServeur(s);
		}
	};

	// Evènement lorqu'on désire afficher la liste des articles
	public void choisirArticle() {
				
		etat.getEtat().choisirArticle();
	}

	// Evenement lorsqu'un article est rajouté à une commande sélectionnée
	public void ajoutArticleCMD(Article a) {
		
		if (a == null)
			setEtat(etat);
		if (selectedCommande != null)
			etat.getEtat().ajoutArticleCMD(selectedCommande, a);		
	}

	// Evènement lorsqu'on doit revenir à l'état initial
	public void exit() {
		
		etat.getEtat().exit();
	};

	public DAOFactory getFabrique() {
		return fabrique;
	}

	public Table getSelectedTable() {
		return selectedTable;
	}

	public Commande getSelectedCommande() {
		return selectedCommande;
	}

	public Serveur getSelectedServeur() {
		return selectedServeur;
	}

	/**
	 * Ne pas appeler. Utilisé en interne par les états
	 * 
	 * @param cmd
	 */
	void setSelectedCommande(Commande cmd) {
		selectedCommande = cmd;

	}

	/**
	 * Traitement par défaut pour afficher les erreurs
	 * 
	 * @param title
	 * @param message
	 */
	private static void showError(String title, String message) {
		if (Platform.isFxApplicationThread()) {
			Alert a = new Alert(Alert.AlertType.ERROR);
		a.getDialogPane().getStylesheets().add("/vue/css/alerts.css");
		a.setHeaderText(title);
		a.setContentText(message);
		a.showAndWait();
		}
		else
		Platform.runLater(() -> {
		Alert a = new Alert(Alert.AlertType.ERROR);
		a.getDialogPane().getStylesheets().add("/vue/css/alerts.css");
		a.setHeaderText(title);
		a.setContentText(message);
		a.showAndWait();});
	}

	/**
	 * Permet de remplacer le traitement par défaut qui affiche les erreurs
	 * 
	 * @param afficheErreur
	 *            doit implémenter l'interface: AffichageErreur
	 */
	public static void setTraitementErreur(AffichageErreur traitement) {
		afficheErreur = traitement;
	}

	/**
	 * Permet d'afficher un message d'erreur Utilise un comportement par défaut mais
	 * celui-ci peut être redéfini en appelant le méthode "setTraitementErreur"
	 * 
	 * @param titre
	 * @param message
	 */
	public static void afficheErreur(String titre, String message) {
		afficheErreur.affiche(titre, message);
	}

}
