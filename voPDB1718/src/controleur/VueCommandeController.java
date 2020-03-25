package controleur;

import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;

import dao.DAOFactory;
import flow.CommandeFlowControl;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.StringConverter;
import javafx.util.converter.DoubleStringConverter;
import modele.Commande;
import modele.Commande.EtatCmd;
import modele.LigneCommande;
import modele.LigneCommande.EtatLigne;

public class VueCommandeController implements Initializable {

	// si une ligne est modifiée, changeMade sera à vrai
	private BooleanProperty changeMade = new SimpleBooleanProperty(false);
	// dès qu'on modifie un champ d'une ligne on rajoute cette ligne à un ensemble
	// ceci permettra de sauver en BD que les lignes altérées
	private Set<LigneCommande> lignesToUpdate = new HashSet<>();

	@FXML
	private TableView<LigneCommande> tblView;

	@FXML
	private TableColumn<LigneCommande, Integer> colNumL;

	@FXML
	private TableColumn<LigneCommande, String> colArticle;

	@FXML
	private TableColumn<LigneCommande, EtatLigne> colEtatL;

	@FXML
	private TableColumn<LigneCommande, Boolean> colPayeeL;

	@FXML
	private TableColumn<LigneCommande, Double> colPrixL;

	@FXML
	private JFXTextField ztNumCmd;

	@FXML
	private JFXTextField ztMomentA;

	@FXML
	private JFXTextField ztMomentS;

	@FXML
	private JFXTextField ztTotal;

	@FXML
	private JFXComboBox<EtatCmd> cbEtatC;

	@FXML
	private Button btSave;

	@FXML
	private Button btRetour;

	@FXML
	private Button btAjoutArticle;
	private DAOFactory factory;
	private Commande commande;
	private ObservableList<LigneCommande> listeLignes;
	private CommandeFlowControl flowControl;

	@FXML
	void actionSave(ActionEvent event) {
		
	}

	@FXML
	void actionAjoutArticle(ActionEvent event) {
		flowControl.choisirArticle();
	}

	@FXML
	void actionExit(ActionEvent event) {
		flowControl.exit();
	}

	// Action sur un changement d'état de la commande
	@FXML
	void chmtEtatCmd(ActionEvent event) {
		this.commande.setEtat(cbEtatC.getValue());

	}

	/**
	 * Permet de fournir les données métiers à la vue. Appelé une seule fois lors de
	 * la création de la vue
	 * 
	 * @param flow
	 *            l'automate qui contrôle le flux
	 */
	public void setUp(CommandeFlowControl flow) {
		// sauve le flux et extrait la fabrique et la commande en cours
		this.flowControl = flow;
		this.factory = flow.getFabrique();
		// Charge les infos de la commande en cours
		chargeCommandeEnCours();
	}

	/**
	 * Recharge une commande avec ses lignes
	 */
	public void chargeCommandeEnCours() {
		// Charge la commande en cours
		this.commande = flowControl.getSelectedCommande();
		if (commande == null)
			return;

		// Associe à la table la liste des articles de la commande
		// sous forme de liste observable
		List<LigneCommande> liste = commande.getArticles();
		this.listeLignes = FXCollections.observableArrayList(liste);
		tblView.setItems(listeLignes);
		tblView.setEditable(true);

		// Pas de ligne à modifier
		lignesToUpdate.clear();
		changeMade.set(false);

		// Initialise les autres contrôles
		rafraichirLesChampsDeLaCommande();
	}

	/**
	 * Initialise les contrôles de la commande (autres que la liste)
	 */
	private void rafraichirLesChampsDeLaCommande() {
		// Initialise les champs de la commande
		ztNumCmd.setText(commande.getNum().toString());
		ztMomentA.setText(commande.getMomentA().format(DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm")));
		ztMomentS.setText(commande.getMomentS() == null ? "" : commande.getMomentS().toString());
		ztTotal.setText(commande.getTotal().toString() + " €");
		cbEtatC.setValue(commande.getEtat());
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// Binding des boutons pour les rendre actif en cas de modif
		btSave.disableProperty().bind(changeMade.not());

		/* Définition des champs de la Table */
		// Num_Lig (LigneCmd.num)
		colNumL.setCellValueFactory(l -> new ReadOnlyObjectWrapper<>(l.getValue().getId().getNumLigne()));
		// Colonne Article
		colArticle.setCellValueFactory(a -> new ReadOnlyObjectWrapper<>(a.getValue().getArticle().getCode()));

		// Colonne EtatLigne (LigneCmd.etat)
		colEtatL.setCellValueFactory(new PropertyValueFactory<LigneCommande, EtatLigne>("etat"));
		colEtatL.setCellFactory(ComboBoxTableCell.forTableColumn(new StringConverter<LigneCommande.EtatLigne>() {

			@Override
			public EtatLigne fromString(String s) {
				return EtatLigne.valueOf(s.trim());
			}

			@Override
			public String toString(EtatLigne etat) {
				return String.valueOf(etat).trim();
			}
		}, // Suivi de la liste des états sous forme de liste Observable
		   FXCollections.observableArrayList(LigneCommande.EtatLigne.values()
		)));

		colEtatL.setEditable(true);
		// Trigger lorsqu'un changement d'état est commité
		colEtatL.setOnEditCommit(e -> {
			e.getRowValue().setEtat(e.getNewValue());
			changeMade.set(true);
			lignesToUpdate.add(e.getRowValue());
			//demande le rafraîchissement du total
			ztTotal.setText(commande.getTotal().toString() + " €");
		});

		// Colonne Payée
		//
		colPayeeL.setCellValueFactory(new PropertyValueFactory<LigneCommande, Boolean>("payee"));
		// Comme le champ payé d'une ligne de commande n'est pas une property, il faut
		// alors
		// rajouter une BooleanProperty au niveau de chaque cellule pour lui associer
		// une gestion d'évènements
		colPayeeL.setCellValueFactory(cellData -> {
			LigneCommande lc = cellData.getValue();
			// Va fournir une BooleanProperty pour la cellule (car dans une ligne on n'a
			// qu'un Boolean)
			// On peut maintenant associer un évènement sur la property
			// ceci est nécessaire car avec une checkbox dans une cellule l'évent setOnEdit
			// ne s'active pas
			BooleanProperty property = new SimpleBooleanProperty(lc.getPayee());

			// Ajoute un écouteur sur le changement d'état
			property.addListener((observable, oldValue, newValue) -> {
				lc.setPayee(newValue);
				this.changeMade.set(true);
				this.lignesToUpdate.add(lc);
			});
			return property;
		});

		// Lorsque l'on utilise une checkBox, l'évènement "setOnEditCommit" n'est pas
		// enclenché, la
		// modification se fait directement sur la property BooleanProperty
		colPayeeL.setCellFactory(c -> new CheckBoxTableCell<>());

		colPayeeL.setEditable(true);

		// Colonne prix ligne
		colPrixL.setCellValueFactory(new PropertyValueFactory<LigneCommande, Double>("prix"));
		colPrixL.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
		colPrixL.setEditable(true);
		colPrixL.setOnEditCommit(e -> {

			e.getRowValue().setPrix(e.getNewValue());
			lignesToUpdate.add(e.getRowValue());
			changeMade.set(true);
		});

		/* Définition des contrôles de la Commande */
		// Donne les valeurs possibles à la liste de choix pour l'état d'une commande
		cbEtatC.setItems(FXCollections.observableArrayList(EtatCmd.values()));
		// Empêche la modification directe en encodant un texte
		cbEtatC.setEditable(false);
		
		ztNumCmd.setEditable(false);
		ztMomentA.setEditable(false);
		ztMomentS.setEditable(false);

	}

}
