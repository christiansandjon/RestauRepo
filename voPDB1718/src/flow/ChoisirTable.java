package flow;

import java.util.ArrayList;
import java.util.List;

import flow.CommandeFlowControl.ActionTable;
import modele.Table;
import modele.TableRectangulaire;
import modele.TableRonde;

/**
 * Etat où l'on doit choisir une table pour ajouter ou modifier une commande
 * 
 * @author Didier
 *
 */
public class ChoisirTable implements Etats {
	CommandeFlowControl flowCtrl;
	// Action à faire pour sélectionner une table
	private ActionTable afficheTable;

	private List<Table> tables = new ArrayList<>();

	public ChoisirTable(CommandeFlowControl flowCtrl,ActionTable afficheTable) {
		this.flowCtrl = flowCtrl;
		this.afficheTable=afficheTable;
		//TODO A VOUS
		tables.add(new TableRonde("T1",6, 50.0, 50.0, 40.0));
		tables.add(new TableRonde("T2",6, 100.0, 50.0, 40.0));
		tables.add(new TableRectangulaire("T3",8, 50.0, 100.0, 100.0, 50.0));
	}

	@Override
	public void exit() {
		//on retourne dans le même état
		flowCtrl.start();
	}

	@Override
	public void selectTable(Table t) {
		//Vérifie si on a bien une table et pas null
		if (t==null) flowCtrl.setEtat(CommandeFlowControl.Etat.CHOISIR_TABLE);
		else //vérifie si la table est libre
			if (!t.getCommande().isPresent())
				//si libre on va choisir un serveur
			flowCtrl.setEtat(CommandeFlowControl.Etat.CHOISIR_SERVEUR);
			else
			{//La table est occupée
			//On récupère la commande en cours et on change d'état
			flowCtrl.setSelectedCommande(t.getCommande().get());
			flowCtrl.setEtat(CommandeFlowControl.Etat.AFFICHER_COMMANDE);
			}
	}

	@Override
	public void actionIn() {
		afficheTable.execute(flowCtrl,tables);
	}

}
