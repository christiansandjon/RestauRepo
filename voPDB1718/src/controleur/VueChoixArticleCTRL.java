package controleur;

import java.net.URL;
import java.util.ResourceBundle;

import flow.CommandeFlowControl;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import modele.Article;

public class VueChoixArticleCTRL implements Initializable{

    @FXML
    private TableView<Article> tblArticles;

    @FXML
    private TableColumn<Article, String> colCode;

    @FXML
    private Button btOk;

    @FXML
    void actionOk(ActionEvent event) {
    //  flow.ajoutArticleCMD(a);
    }
	
	private CommandeFlowControl flow;

	/**
	 * 
	 * @param flow l'automate qui contrôle le flux
	 */
	public void setUp(CommandeFlowControl flow) {
	 this.flow=flow;
	// tblArticles.setItems(arg0);
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		colCode.setCellValueFactory(new PropertyValueFactory<Article,String>("code"));
		
	}

}
