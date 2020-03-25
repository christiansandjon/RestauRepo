package controleur;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;

import dao.DAOFactory;
import dao.IServeurDAO;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import modele.Serveur;
import tools.Validation;

/**
 * 
 * @author Didier Contrôleur pour afficher une liste modifiable de serveurs
 */
public class ListeServeursCTRL implements Initializable {
	// Les codes des serveurs ayant été modifiés
	private Set<String> serveursToUpdate = new HashSet<>();
	// true si un changement a été fait
	private BooleanProperty changementFait = new SimpleBooleanProperty(false);
	// Accès à la BD
	private DAOFactory usine;

	@FXML
	private TableView<Serveur> tblServeurs;

	@FXML
	private TableColumn<Serveur, String> colCode;

	@FXML
	private TableColumn<Serveur, String> colNom;

	@FXML
	private TableColumn<Serveur, String> colPrenom;

	@FXML
	private TableColumn<Serveur, String> colEmail;
	
	@FXML
	private Button btRecharge;

	@FXML
	private Button btValide;
/**
 * Fait un Update dans la BD
 * @param e
 */
	@FXML
	void actionValider(ActionEvent e) {
		
		IServeurDAO dao = usine.getServeurDAO();
		for (String code : serveursToUpdate) {
			try {// Recherche le serveur ayant le code recherché
				dao.update(tblServeurs.getItems().filtered((s) -> s.getCode().equals(code)).get(0));
				serveursToUpdate.remove(code);// retire de la liste des maj à
												// faire
			} catch (Exception e1) {
				showErreur(e1.getMessage());
			}
			
		}
		if (serveursToUpdate.isEmpty())
				changementFait.set(false);
	}
/**
 * Recharge de la bd les serveurs modifiés
 * @param e
 */
	@FXML
	void actionRecharge(ActionEvent e) {
		IServeurDAO dao = usine.getServeurDAO();

		List<String> listeOk = new ArrayList<>();
		for (String code : serveursToUpdate) {
			try {
				// Recherche le serveur ayant le code recherché
				Serveur bdServ = dao.getFromID(code);
				// Remplace par celui de la BD
				tblServeurs.getItems().replaceAll(o -> o.getCode().equals(code) ? bdServ : o);

				// mémorise les rechargements
				listeOk.add(code);

			} catch (Exception e1) {
				showErreur(e1.getMessage());
			}

		}
		// supprime de la liste tous les codes rechargé
		serveursToUpdate.removeAll(listeOk);
		if (serveursToUpdate.isEmpty())
			changementFait.set(false);

	}
/**
 * Appelée après l'initialisation pour introduire les données
 * @param usine accès aux DAO
 */

	public void setUP(DAOFactory usine) {
		this.usine = usine;
		// donne une liste observable à la vue
		tblServeurs.setItems(FXCollections.observableArrayList(usine.getServeurDAO().getListe("")));
		//pas de serveurs modifiés
		serveursToUpdate.clear();
		//le bouton recharge est inactif car pas de changement
		btRecharge.disableProperty().bind(changementFait.not());
		btValide.disableProperty().bind(changementFait.not());
		changementFait.set(false);
		
	}

	@Override
	public void initialize(URL location, ResourceBundle bundles) {

		// Prépration de la structure de la table sans les données

		/* Code du serveur non modifiable */
		// Fourni la valeur en enveloppant la donnée dans une property
		colCode.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getCode()));

		// Enveloppe le nom du serveur dans une property
		colNom.setCellValueFactory(new PropertyValueFactory<Serveur, String>("nom"));
		// Type de noeud pour l'édition
		colNom.setCellFactory(TextFieldTableCell.forTableColumn());
		// traitement à faire lors de l'édition
		colNom.setOnEditCommit(e -> {
			e.getRowValue().setNom(e.getNewValue());
			serveursToUpdate.add(e.getRowValue().getCode());
			changementFait.set(true);
		});

		colPrenom.setCellValueFactory(new PropertyValueFactory<Serveur, String>("prenom"));
		// Type de noeud pour l'édition
		colPrenom.setCellFactory(TextFieldTableCell.forTableColumn());
		// traitement à faire lors de l'édition
		colPrenom.setOnEditCommit(e -> {
			e.getRowValue().setPrenom(e.getNewValue());
			serveursToUpdate.add(e.getRowValue().getCode());
			changementFait.set(true);
		});

		colEmail.setCellValueFactory(new PropertyValueFactory<Serveur, String>("email"));
		// Type de noeud pour l'édition
		colEmail.setCellFactory(TextFieldTableCell.forTableColumn());
		// traitement à faire lors de l'édition
		colEmail.setOnEditCommit(e -> {
			if (!Validation.isEmailValid(e.getNewValue())) {
				//Message internationnalisé si email incorrect
				showErreur(bundles.getString("ErrEmail"));

			} else {
				e.getRowValue().setEmail(e.getNewValue());
				//on mémoirse le fait que ce serveur a été modifié
				serveursToUpdate.add(e.getRowValue().getCode());
				changementFait.set(true);

			}
		});
	}
/**
 * Affiche une Alerte en cas d'erreur
 * @param message
 */
	private void showErreur(String message) {
		Alert a = new Alert(AlertType.ERROR, message);
		a.showAndWait();
	}
}
