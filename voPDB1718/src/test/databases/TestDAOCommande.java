package test.databases;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import dao.DAOFactory;
import dao.DAOFactory.TypePersistance;
import databases.ConnexionFromFile;
import databases.ConnexionSingleton;
import databases.Databases;
import databases.PersistanceException;
import modele.Article;
import modele.Commande;
import modele.LigneCommande;
import modele.LigneCommande.EtatLigne;
import modele.LigneCommande.Id;
import modele.Serveur;

public class TestDAOCommande {
	private static DAOFactory fabrique;

	@BeforeClass
	public void init() throws PersistanceException {
		// crée une fabrique pour les vues
		ConnexionSingleton
				.setInfoConnexion(new ConnexionFromFile("./ressources/connexionRestoTest.properties", Databases.FIREBIRD));
		fabrique = DAOFactory.getDAOFactory(TypePersistance.FIREBIRD, ConnexionSingleton.getConnexion());
	}
	
	
	@Test
	  public void getCommande() {
		Commande cmd=fabrique.getCommandeDAO().getFromID(1);
		  assertEquals(cmd.getServeur().getCode(),"Phil");
		  
		  assertEquals(cmd.getTotal(),69.0);  
		  assertEquals(cmd.getArticles().size(),6);
		  Double tot=cmd.getArticles().stream().mapToDouble((c)->c.getPrix()).sum();
		  assertEquals(tot,69.0); 
	  }
	
	@Test
	public void nouvelleCommande() throws Exception {
		Serveur s= fabrique.getServeurDAO().getFromID("Lola");
		Commande commande=new Commande(s);
		Article a=fabrique.getArticleDAO().getFromID("JAMSER");
		Integer numCMD=fabrique.getCommandeDAO().insert(commande);
		assertNotNull(numCMD);
		LigneCommande lc=new LigneCommande(new Id(numCMD,null), a,a.getPrix());
		fabrique.getLigneCommandeDAO().insert(lc);
		commande.getArticles().add(lc);
		assertEquals(lc.getId().getNumLigne(),(Integer)1);
		assertEquals(lc.getArticle(),a);
		assertTrue(fabrique.getCommandeDAO().delete(commande));
	}
	@Test
	public void updateCommande() throws Exception {
		//Création d'une commande
		Serveur s= fabrique.getServeurDAO().getFromID("Lola");
		Commande commande=new Commande(s);
		Integer numCMD=fabrique.getCommandeDAO().insert(commande);
		//Ajout d'une ligne de commande que l'on sauve en BD avant
		Article a=fabrique.getArticleDAO().getFromID("JAMSER");
		LigneCommande lc=new LigneCommande(new Id(numCMD,null), a,a.getPrix());
		fabrique.getLigneCommandeDAO().insert(lc);
		assertNotNull(lc.getId().getNumLigne());//elle doit avoir un numéro de ligne
		commande.getArticles().add(lc);
		//changement des infos de la ligne
		lc.setEtat(EtatLigne.SERVI);
		lc.setPayee(true);
		lc.setPrix(1.0);
		//Ajout d'une ligne non sauvée à la commande:
		LigneCommande lc2=new LigneCommande(new Id(numCMD,null), a,a.getPrix());
		commande.getArticles().add(lc2);
		//Update de la commande
		fabrique.getCommandeDAO().update(commande);
		
		//Recharge la commande et tests
		Commande commandeL=fabrique.getCommandeDAO().getFromID(numCMD);
		assertEquals(2,commandeL.getArticles().size());//2 lignes
		LigneCommande lc01=commandeL.getArticles().get(0);
		LigneCommande lc02=commandeL.getArticles().get(1);
		assertEquals(lc01,lc);
		lc2.setNumLigne(2);
		assertEquals(lc02,lc2);
			
		assertTrue(fabrique.getCommandeDAO().delete(commande));
	}
	
}
