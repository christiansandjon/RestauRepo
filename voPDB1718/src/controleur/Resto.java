package controleur;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import dao.DAOFactory;
import dao.DAOFactory.TypePersistance;
import databases.ConnexionFromFile;
import databases.ConnexionSingleton;
import databases.Databases;
import flow.CommandeFlowControl;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.TilePane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import modele.Article;
import modele.Serveur;
import modele.Table;

public class Resto extends Application {
	// Crée une classe pour mémoriser la vue des commandes
	// pour maintenir son controleur et la vue comme un tout
	private class VueCommande {
		// Maintient le contrôleur de la vue
		private VueCommandeController vueCommandeController;
		private Pane vueCommande;

		/**
		 * Constructeur de la Vue d'une Commande
		 * 
		 * @param flow
		 *            :l'automate
		 * @param cheminVue
		 *            :chemin et fichier fxml (/ entre les noms de package)
		 * @param CheminBundle
		 *            :chemin du Bundle (points entre les noms de package)
		 * @param locale
		 *            :la locale à utiliser
		 */
		public VueCommande(CommandeFlowControl flow, String cheminVue, String CheminBundle, Locale locale) {

			ResourceBundle bundle;// Fichier i18n
			// crée un chargeur pour la vue FXML
			FXMLLoader loader = new FXMLLoader(getClass().getResource(cheminVue));
			try {
				bundle = ResourceBundle.getBundle(CheminBundle, locale);
				loader.setResources(bundle);
			} catch (Exception e) {
				showErreur(e.getMessage());
			}

			// charge la vue avec ses données
			Pane root;
			try {
				root = loader.load();
				// récupère et mémorise le ctrl
				vueCommandeController = loader.getController();
				// fourni l'objet métier au ctrl pour charger les articles
				vueCommandeController.setUp(flow);
				// Mémorise la vue pour éviter de la reconstruire à chaque fois
				vueCommande = root;
			} catch (IOException e) {
				showErreur(e.getMessage());
			}

		}

		/**
		 * Permet d'obyenir le controleur pour rafraîchir la vue
		 * 
		 * @return le controleur de la vueCommande
		 */
		VueCommandeController getController() {
			return vueCommandeController;
		}

		/**
		 * Permet de retourer le Pane de la vueCommande
		 * 
		 * @return
		 */
		Pane getVueCommande() {
			return vueCommande;
		}
	}

	/*********************************************************/
	private final Locale DEFAULT_LOCALE = new Locale("fr", "BE");
	// Fenêtre pour modifier les articles
	private Stage vueArticle = null;
	// Fenêtre pour modifier les serveurs
	private Stage vueListeServeurs = null;
	// la vue pour gérer une commande (possède un conteneur)
	private VueCommande vueCommande = null;

	// Locale par défaut de l'application
	private Locale locale = DEFAULT_LOCALE;

	// Accès aux DAO
	private DAOFactory fabrique;

	// Conteneur principal de la vue principale
	private BorderPane contentPane;

	// nodes de la vue principale
	private Label lbl;
	private Button btArticle;
	private Button btServeurs;
	private Button btStartAutomate;

	// Automate pour gérer le flux des commandes
	CommandeFlowControl commandeFlow;

	@Override
	public void start(Stage scenePrincipale) throws Exception {
		// Création du conteneur principal
		contentPane = new BorderPane();

		try {
			// crée une fabrique pour les vues
			ConnexionSingleton.setInfoConnexion(
					new ConnexionFromFile("./ressources/connexionResto.properties", Databases.FIREBIRD));
			fabrique = DAOFactory.getDAOFactory(TypePersistance.FIREBIRD, ConnexionSingleton.getConnexion());

			// Création d'un automate pour gérer le flux des commandes
			commandeFlow = new CommandeFlowControl(fabrique, (f, tables) -> {
				showTable(f, tables);
			}, // Vue pour choisir une // table
					(f, liste) -> {
						showServeur(f, liste);
					}, // Vue pour choisir un serveur
					(f) -> {
						showCommande(f);
					}, // vue pour voir une commande
					(f, liste) -> {
						showArticles(f, liste);
					});// vue pour choisir un article

			// Titre de l'aplication
			TilePane lstBoutons = new TilePane();
			lbl = new Label("  BIENVENUE AU RESTO ISFCE  ");
			lbl.setId("AccueilResto");// id pour CSS
			contentPane.setPadding(new Insets(5, 5, 5, 5));
			// La taille doit être celle du conteneur
			lbl.prefWidthProperty().bind(contentPane.widthProperty());
			lbl.setAlignment(Pos.CENTER);
			contentPane.setTop(lbl);

			// Bouton pour afficher la vueArticle
			btArticle = new Button("Article");
			btArticle.setOnAction(a -> {
				if (vueArticle != null)
					vueArticle.show();
			});
			// Bouton pour afficher la liste des serveurs
			btServeurs = new Button("Serveurs");
			btServeurs.setOnAction(a -> {
				if (vueListeServeurs != null)
					vueListeServeurs.show();
			});

			// Bouton qui lance le processus de commande
			btStartAutomate = new Button("Start Automate");
			btStartAutomate.setOnAction(a -> {		
					commandeFlow.start();
				((Button) a.getSource()).setDisable(true);
			});

			// Ajout des 3 boutons au conteneur left
			lstBoutons.getChildren().addAll(btStartAutomate, btArticle, btServeurs);
			// rajoute le conteneur des boutons à gauche dans le cp
			contentPane.setLeft(lstBoutons);
			lstBoutons.setStyle("-fx-background-color: azure;");

			// création d'une scène avec son conteneur parent
			Scene scene = new Scene(contentPane, 900, 600);
			scene.getStylesheets().add("/vue/css/resto.css");
			scenePrincipale.setTitle("RESTO");
			// Ajout de la scène à la Stage
			scenePrincipale.setScene(scene);

			// Constructions des vues pour modifier les articles et modifier les serveurs
			vueArticle = getVueArticle(scenePrincipale);
			vueListeServeurs = getVueListeServeurs(scenePrincipale);
			// Affichage de la vue principale
			scenePrincipale.show();
		} catch (Exception e) {
			showErreur(e.getMessage());
		}

	}

	/**
	 * retourne la vue pour voir ou éditer chaque article Elle sera créée si elle ne
	 * l'a pas encore été
	 * 
	 * @param scenePrincipale
	 * @return une Stage
	 */
	public Stage getVueArticle(Stage scenePrincipale) {
		if (vueArticle != null)
			return vueArticle;

		ResourceBundle bundle;
		// Création d'une nouvelle fenêtre
		Stage stage = new Stage(StageStyle.DECORATED);
		stage.initModality(Modality.NONE);
		stage.initOwner(scenePrincipale);
		// Position lors de l'ouverture;
		stage.setX(scenePrincipale.getX() + 100);
		stage.setY(scenePrincipale.getY() + 40);

		// Crée un loader pour charger la vue FXML
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/vue/VueArticle.fxml"));
		try {
			bundle = ResourceBundle.getBundle("vue.bundles.VueArticle", locale);
			loader.setResources(bundle);
			// Obtenir la traduction du titre dans la locale
			stage.setTitle(bundle.getString("Titre"));
		} catch (Exception e) {
			showErreur(e.getMessage());
			stage.setTitle("Vue Article");
		}

		// Charge la vue à partir du Loader
		// et initialise son contenu en appelant la méthode setUp du controleur
		Pane root;
		try {
			root = loader.load();
			// récupère le ctrl (après l'initialisation)
			ArticleCTRL ctrl = loader.getController();
			// fourni la fabrique au ctrl pour charger les articles
			ctrl.setUP(fabrique);
			// charge le Pane dans la Stage
			stage.setScene(new Scene(root, 400, 400));
		} catch (IOException e) {
			showErreur("Impossible de charger la vueArticle: " + e.getMessage());
			stage = null;
		}

		return stage;
	}

	/**
	 * retourne la vue pour voir ou éditer les serveur
	 * 
	 * @param scenePrincipale
	 * @return une Stage
	 */
	public Stage getVueListeServeurs(Stage scenePrincipale) {
		if (vueListeServeurs != null)
			return vueListeServeurs;

		ResourceBundle bundle;
		// Création d'une nouvelle fenêtre
		Stage stage = new Stage(StageStyle.DECORATED);
		stage.initModality(Modality.NONE);
		stage.initOwner(scenePrincipale);
		// Position lors de l'ouverture;
		stage.setX(scenePrincipale.getX() + 200);
		stage.setY(scenePrincipale.getY() + 80);

		// crée un chargeur pour la vue FXML
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/vue/VueListeServeurs.fxml"));
		try {
			bundle = ResourceBundle.getBundle("vue.bundles.VueListeServeurs", locale);
			loader.setResources(bundle);
			stage.setTitle(bundle.getString("Titre"));
		} catch (Exception e) {
			showErreur(e.getMessage());
			stage.setTitle("Vue Serveurs");
		}

		// charge la vue et lui fourni ses données
		Pane root;
		try {
			root = loader.load();
			// récupère le ctrl (après l'initialisation)
			ListeServeursCTRL ctrl = loader.getController();
			// fourni l'objet métier au ctrl pour charger les articles
			ctrl.setUP(fabrique);
			// mets le Pane dans la Stage principale
			stage.setScene(new Scene(root, 400, 400));
		} catch (IOException e) {
			showErreur("Impossible de charger la liste serveur: " + e.getMessage());
			stage = null;
		}

		return stage;
	}

	// retourne null en cas de non sélection
	/**
	 * Traitement temporaire pour choisir une table
	 * 
	 * @param liste
	 *            des tables
	 */
	private void showTable(CommandeFlowControl flow, List<Table> liste) {
		Optional<Table> table = Optional.empty();
		List<String> codesTables = liste.stream().map((t) -> t.getCode()).sorted().collect(Collectors.toList());
		ChoiceDialog<String> dlg = new ChoiceDialog<>("", codesTables);
		dlg.setTitle(" Boite de sélection ");
		dlg.setHeaderText(" Faite votre choix ");
		dlg.setContentText(" Choisir une Table :");

		// récupère le résultat
		Optional<String> result = dlg.showAndWait();
		if (result.isPresent()) {
			table = liste.stream().filter((e) -> e.getCode().equals(result.get())).findFirst();
		}
		// Envoi des évènements sur l'automate
		if (table.isPresent())
			commandeFlow.selectTable(table.get());
		else
			commandeFlow.exit();

	}

	/**
	 * Traitement temporaire pour choisir un serveur
	 * 
	 * @param flow
	 *            l'automate
	 * @param liste
	 *            la liste des serveurs
	 */
	private void showServeur(CommandeFlowControl flow, List<Serveur> liste) {
		Optional<Serveur> serveur = Optional.empty();
		List<String> codesTables = liste.stream().map((t) -> t.getCode()).sorted().collect(Collectors.toList());
		ChoiceDialog<String> dlg = new ChoiceDialog<>("", codesTables);
		dlg.setTitle(" Boite de sélection ");
		dlg.setHeaderText(" Faite votre choix ");
		dlg.setContentText(" Choisir un Serveur :");
		// récupère le résultat
		do {
			Optional<String> result = dlg.showAndWait();
			if (result.isPresent()) {
				serveur = liste.stream().filter((e) -> e.getCode().equals(result.get())).findFirst();
			}
		} while (!serveur.isPresent());
		// Envoi des évènements transitions à l'automate en fonction
		if (serveur.isPresent()) // pas de sélection retour dans l'état initial
			flow.selectServeur(serveur.get());
		else // précise le serveur pour passer à l'étape suivante
			flow.exit();

	}

	/**
	 * Crée la vue commande si elle n'existe pas encore et affiche la vueCommande
	 * dans le centre du contentPane
	 * 
	 * @param flow
	 *            l'automate qui servira lors de la création de la vue
	 */
	private void showCommande(CommandeFlowControl flow) {
		// Crée la vue si elle n'existe pas encore
		if (vueCommande == null) {
			vueCommande = new VueCommande(flow, "/vue/VueCommande.fxml", "vue.bundles.VueCommande", locale);
		}
		if (vueCommande != null) {

			// demande à la vue de recharger sa commande
			vueCommande.getController().chargeCommandeEnCours();
			// affiche la vueScene
			//System.out.println("Affiche vueCommande: " + contentPane.getChildren().size());
			contentPane.setCenter(vueCommande.getVueCommande());
		}
	}

	/**
	 * Vue temporaire pour choisir un article (actuellement renvoi un article au
	 * hasard)
	 * 
	 * @param f
	 *            l'automate
	 * @param articles
	 *            la liste des articles
	 */
	private void showArticles(CommandeFlowControl f, List<Article> articles) {
		contentPane.setCenter(new Label("Choix Article"));
		f.ajoutArticleCMD(articles.get((int) Math.round((Math.random() * 50))));
	}

	/**
	 * Vue pour afficher les messages d'erreur
	 * 
	 * @param message
	 */
	private void showErreur(String message) {
		Alert a = new Alert(AlertType.ERROR, message);
		a.showAndWait();
	}

	/**
	 * Point d'entrée de l'application
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		Application.launch(Resto.class, args);

	}
}
