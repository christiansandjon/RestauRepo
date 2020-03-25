package flow;

import java.util.List;

import flow.CommandeFlowControl.ActionServeur;
import flow.CommandeFlowControl.Etat;
import modele.Commande;
import modele.Serveur;

public class ChoisirServeur implements Etats {
	private CommandeFlowControl flowControl;
	private ActionServeur afficheServeur;
	private List<Serveur> liste;

	public ChoisirServeur(CommandeFlowControl commandeFlowControl, ActionServeur afficheServeur) {
		this.flowControl = commandeFlowControl;
		this.afficheServeur = afficheServeur;
		this.liste = commandeFlowControl.getFabrique().getServeurDAO().getListe("");
	}

	@Override
	public void actionIn() {
		afficheServeur.execute(flowControl,liste);
	}

	@Override
	public void selectServeur(Serveur serveur) {
		if (serveur!=null) {
		//Création d'une commande pour ce serveur
		//Et occupation de la table
		try {
			Commande cmd=new Commande(serveur);
			flowControl.getFabrique().getCommandeDAO().insert(cmd);
			flowControl.setSelectedCommande(cmd);
			//indique que la table est occupée par la commande
			flowControl.getSelectedTable().setCommande(cmd);
			
			flowControl.setEtat(Etat.AFFICHER_COMMANDE);
			
		} catch (Exception e) {
			
			e.printStackTrace();
		}
		
		
		}
	}

	@Override
	public void exit() {
		// TODO Auto-generated method stub

	}

}
