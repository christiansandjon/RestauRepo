package exemples.javaFX;


import java.util.Arrays;
import java.util.Optional;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import modele.Article;
import modele.Article.Etape;

public class ApplicationJavaFX extends Application {
	private Label lbl;
	private Button bt1;
	private Button bt2;
	private Button bt3;
	private Button bt4;
	private Button bt5;
	private TextField ztEntier;

	@Override
	public void start(Stage scenePrincipale) throws Exception {
		BorderPane cp = new BorderPane();
		VBox lstBoutons = new VBox(5.0);
		lbl = new Label("  BIENVENUE AU COURS DE PDB  ");
		cp.setPadding(new Insets(5, 5, 5, 5));
		BorderPane.setAlignment(lbl, Pos.CENTER);
		lbl.setStyle("-fx-background-color: lightGreen;");
		cp.setTop(lbl);
		// BT1 création de Stage
		bt1 = new Button("Lancer");
		

		bt1.setOnAction(e -> {
			
			Stage s1 = new Stage();
			s1.initStyle(StageStyle.DECORATED);
			s1.initModality(Modality.WINDOW_MODAL);
			s1.initOwner(scenePrincipale);
			s1.setScene(new Scene(new BorderPane()));
			s1.setTitle("Stage de style: " + s1.getStyle()+" modality: "+s1.getModality() );
			s1.showAndWait();
			System.out.println("Suite du code");

		});
		// BT2 test Dialog
		bt2 = new Button(" Test Dialog Information");
		
		bt2.setOnAction(e -> {
			Alert al1 = new Alert(AlertType.ERROR);
			al1.setHeaderText(" Il est l'heure de la pause !!!");
			al1.showAndWait();
		});
		// BT3 test Dialog
		bt3 = new Button(" Test Dialog Confirmation");
		
		bt3.setOnAction(e -> {
			Alert al1 = new Alert(AlertType.CONFIRMATION);
			al1.setHeaderText(" La pause est finie ?");
			al1.setContentText("Votre choix ");
			Optional<ButtonType> result = al1.showAndWait();
			result.ifPresent(b -> {
				Alert al2;
				// en fonction du bouton appuyé, initialise une alerte
				// différente
				if (b == ButtonType.OK)
					al2 = new Alert(AlertType.INFORMATION);
				else
					al2 = new Alert(AlertType.WARNING);

				al2.setHeaderText("Vous avez appuyé sur le bouton");
				al2.setContentText(b.getText());
				al2.showAndWait();
			});

		});
		// BT4
		bt4 = new Button("Test Choice");
		
		bt4.setOnAction(e -> {
			ChoiceDialog<Article.Etape> dlg = new ChoiceDialog<>(Etape.PLAT, Arrays.asList(Etape.values()));
			dlg.setHeaderText("Choisir une étape");
			Optional<Etape> result = dlg.showAndWait();
			result.ifPresent(et -> {
				Alert al2 = new Alert(AlertType.INFORMATION);
				al2.setHeaderText("Vous avez choisi");
				al2.setContentText(et.toString());
				al2.showAndWait();
			});

		});
		bt5 = new Button("Test Input");
		bt5.setOnAction(e->{
			TextInputDialog dlg = new TextInputDialog ("VO");
			dlg.setHeaderText (" Quel est le nom de votre Professeur ?");
			dlg.setTitle (" Votre Professeur ");
			dlg.setContentText (" Encodez :");
			dlg.showAndWait().ifPresent (n-> System . out . println (n));			
		});
		
		ztEntier=new TextField("0");
		ztEntier.textProperty().addListener( new ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> observable, 
					String oldValue, String newValue) {
				try {
					if(newValue.length()>0) Integer.parseInt(newValue);
				} catch (NumberFormatException e) {
					ztEntier.setText(oldValue);
				}
				
			}

			
		});
		TextField txt=new TextField("HEllo");
		txt.textProperty().bind(ztEntier.textProperty());
		
		lstBoutons.getChildren().addAll(bt1,bt2,bt3,bt4,bt5,ztEntier);
		cp.setLeft(lstBoutons);
		lstBoutons.setStyle("-fx-background-color: AQUA;");
		cp.setCenter(txt);
		// création d'une scène avec son conteneur parent
		Scene scene = new Scene(cp, 300, 400);
		scenePrincipale.setTitle("Exemple TP01 ");
		// Ajout de la scène à la Stage
		scenePrincipale.setScene(scene);
		// Affichage de la vue
		scenePrincipale.show();
	}

	public static void main(String[] args) {
		Application.launch(ApplicationJavaFX.class, args);

	}

}
