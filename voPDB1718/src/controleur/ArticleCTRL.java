package controleur;

import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import javax.swing.JOptionPane;

import dao.DAOFactory;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import modele.Article;
import modele.Article.Etape;
import modele.Categorie;

public class ArticleCTRL implements Initializable {
	// accès au modèle
	private DAOFactory usine;
	// permet de maintenir la liste d'article code_Art vers Article
	private Map<String, Article> mapArticle;
	private Map<String,Categorie>mapCatFeuille;

	@FXML
	private ComboBox<String> cbArticle;
	@FXML
	private TextField ztCode;
	@FXML
	private TextField ztNom;
	@FXML
	private TextField ztPrix;
	@FXML
	private TextField ztDesc;
	@FXML
	private TextField ztCalories;
    @FXML
    private ChoiceBox<Etape> chbEtapes;
    @FXML
    private ChoiceBox<String> chbCat;
	@FXML
	private CheckBox ChckBDispo;
	@FXML
	Button btQuitter;
	@FXML
	Button btValider;
	@FXML
	private Button btRefresh;

	private BooleanProperty changementFait = new SimpleBooleanProperty(false);

	// recharge les champs de l'article sélectionné
	@FXML
	void actionBtRefresh(ActionEvent e) {
		initZoneDeTextesAvecArticleSelect();
	}

	// traitement pour le bouton Valider
	@FXML
	public void actionBtValider(ActionEvent event) {
		String oldNomArticle = null;
		Double oldPrix = null;
		String oldDesc = null;
		boolean oldDispo = false;
		Integer oldCal = 0;
		Etape oldEtape = null;
		Categorie oldCat = null;
		
		// Article sélectionné
		Article a = mapArticle.get(ztCode.getText());
		try {
			// mémorise les anciennes valeurs
			oldNomArticle = a.getNom();
			oldPrix = a.getPrix();
			oldDesc = a.getDescription();
			oldDispo = a.getDispo();
			oldCal = a.getCalories();
			oldEtape = a.getEtape();
			oldCat = a.getCat();
			// modifie les données
			a.setNom(ztNom.getText());
			a.setPrix(Double.parseDouble(ztPrix.getText()));
			a.setDescription(ztDesc.getText());
			a.setDispo(ChckBDispo.isSelected());
			a.setCalories(Integer.parseInt(ztCalories.getText()));
			a.setEtape(chbEtapes.getValue());
			Categorie cat = mapCatFeuille.get(chbCat.getValue());
			a.setCat(cat);
			// tente la mise à jour
			usine.getArticleDAO().update(a);
			changementFait.set(false); // nouvelle valeur stable

		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "OOOPS " + e.getMessage());
			// en cas de problème remet les anciennes valeurs à l'article
			a.setNom(oldNomArticle);
			a.setPrix(oldPrix);
			a.setDescription(oldDesc);
			a.setDispo(oldDispo);
			a.setCalories(oldCal);
			a.setEtape(oldEtape);
			a.setCat(oldCat);
		}
		System.out.println(event.getSource().toString() + "Appel OnAction ");
	}

	// traitement pour le bouton Quitter
	@FXML
	public void actionBtQuitter(ActionEvent event) {
		Stage stage = (Stage) btQuitter.getScene().getWindow();
		stage.close();
	}

	// traitement sur la sélection d'un élément ds la comboBox
	@FXML
	public void actionSelectArticle(ActionEvent e) {
		initZoneDeTextesAvecArticleSelect();
	}

	// Initialise les zones de textes avec l'article sélectionné
	private void initZoneDeTextesAvecArticleSelect() {
		// récupére l'article à partir du code de l'article sélectionné
		Article a = mapArticle.get(cbArticle.getValue());
		// Si un article est sélectionné, maj des nodes
		if (a != null) {
			ztCode.setText(a.getCode());
			ztNom.setText(a.getNom());
			ztPrix.setText(a.getPrix().toString());
			ztDesc.setText(a.getDescription().toString());
			ChckBDispo.setSelected(a.getDispo());
			ztCalories.setText(a.getCalories().toString());
			chbEtapes.getSelectionModel().select((a.getEtape()==null)? Etape.AUCUNE :a.getEtape());
			chbCat.getSelectionModel().select((a.getCat()==null)?" ":a.getCat().getNom());
			changementFait.set(false);
		}
	}

	// Méthode appelée après chargement de la vue mais avant son affichage
	// pour lui fournir les classes métiers et ainsi
	// initialiser les nodes en fonction
	public void setUP(DAOFactory usine) {
		this.usine = usine;
		try {// Créer une map code_article ==>Article
			mapArticle = usine.getArticleDAO().getListe("").stream()
					.collect(Collectors.toMap(Article::getCode, i -> i));
			
			
			// transforme la liste d'articles en liste de code d'article et
			// l'enveloppe dans une liste Observable
			// pour la combobox
			cbArticle.setItems(
					FXCollections.observableList(mapArticle.keySet().stream().sorted().collect(Collectors.toList())));
			
			//chbEtapes.setItems(FXCollections.observableArrayList(mapEtape.keySet().stream().sorted().collect(Collectors.toList())));
			
			chbEtapes.setItems(FXCollections.observableArrayList(Etape.values()));
			
			mapCatFeuille = usine.getCategorieDAO().getCategoriesFeuille().stream().collect(Collectors.toMap(Categorie::getNom, i -> i));
			mapCatFeuille.put(" ", null);
			chbCat.setItems(FXCollections.observableArrayList(mapCatFeuille.keySet().stream().sorted().collect(Collectors.toList())));
		} catch (Exception e) {
			System.out.println("Erreur lors du chargement des Articles " + e.getMessage());
		}
	}

	// Appelé après le chargement mais avant SetUp
	@Override
	public void initialize(URL location, ResourceBundle resources) {

		// initialise les zones de texte avant chargement
		ztCode.setText("");
		ztNom.setText("");
		ztPrix.setText("");
		ztDesc.setText("");
		ChckBDispo.setSelected(false);
		ztCalories.setText("");
		chbEtapes.setItems(FXCollections.observableArrayList());
		chbCat.setItems(FXCollections.observableArrayList());
		
		// Active ou non la validation
		btValider.disableProperty().bind(changementFait.not().or(ztCode.textProperty().isEmpty()));
		// ajuste la propriété changementFait si la ztNom est modifiée
		ztNom.textProperty().addListener((o, oldV, newV) -> {
			if (!changementFait.get())
				changementFait.set(!oldV.equals(newV));
		});
		// ajuste la propriété changementFait si la ztPrix est modifiée
		ztPrix.textProperty().addListener((o, oldV, newV) -> {
			if (!changementFait.get())
				changementFait.set(!oldV.equals(newV));
		});
		ztDesc.textProperty().addListener((o,oldV,newV) -> {
			if (!changementFait.get()) {
				changementFait.set(!oldV.equals(newV));
			}
		});
		ChckBDispo.selectedProperty().addListener((o, oldV, newV) -> {
			if (!changementFait.get())
				changementFait.set(!oldV.equals(newV));
		});
		ztCalories.textProperty().addListener((o, oldV, newV) -> {
			if (!changementFait.get())
				changementFait.set(!oldV.equals(newV));
		});
		chbEtapes.getSelectionModel().selectedIndexProperty().addListener((o, oldV, newV) -> {
			if (!changementFait.get())
				changementFait.set(!oldV.equals(newV));
		});
		chbCat.getSelectionModel().selectedIndexProperty().addListener((o, oldV, newV) -> {
			if (!changementFait.get())
				changementFait.set(!oldV.equals(newV));
		});
		// transforme ztPrix en une zone de liste numérique
		ztPrix.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				try {
					// Si le texte newValue contient au moins un caractère
					// on teste sa validité en le parsant en double
					if (newValue.length() > 0)
						Double.parseDouble(newValue);
				} catch (Exception e) {
					// sinon on remet l'ancienne valeur
					ztPrix.setText(oldValue);
				}
			}
		});
		// vérifie que l'utilisateur a bien entré un entier sinon il replace l'ancienne valeur
		ztCalories.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				try {
					if (newValue.length() > 0)
						Integer.parseInt(newValue);
				} catch (Exception e) {
					ztCalories.setText(oldValue);
				}
			}
			
		});
		
		// Pour info voici l'équivalent en Lambda expression
		// ztPrix.textProperty().addListener((o, oldV, newV) -> {
		// try {
		// // Si le texte newValue contient au moins un caractère
		// // on teste sa validité en le parsant en double
		// if (newV.length() > 0)
		// Double.parseDouble(newV);
		// } catch (Exception e) {
		// // sinon on remet l'ancienne valeur
		// ztPrix.setText(oldV);
		// }
		//
		// });

	}
}
