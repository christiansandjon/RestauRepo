package test.flow;

import org.testng.annotations.Test;

import dao.DAOFactory;
import dao.DAOFactory.TypePersistance;
import databases.ConnexionFromFile;
import databases.ConnexionSingleton;
import databases.Databases;
import databases.PersistanceException;
import flow.CommandeFlowControl;
import modele.Article;
import modele.Serveur;
import modele.Table;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import java.util.List;

import org.testng.annotations.BeforeTest;

public class TestFlow {
	private static DAOFactory fabrique;
    private Table t1=null;
	private Serveur s1=null;
	private List<Article> articles=null;
	private Boolean erreur=false;

	@BeforeTest
	public void beforeTest() throws PersistanceException {
		ConnexionSingleton.setInfoConnexion(
				new ConnexionFromFile("./ressources/connexionRestoTest.properties", Databases.FIREBIRD));
		fabrique = DAOFactory.getDAOFactory(TypePersistance.FIREBIRD, ConnexionSingleton.getConnexion());

	}
	
	
	@Test
	public void f() {
		
		
		CommandeFlowControl flow=
		new CommandeFlowControl(fabrique,
				(f,table)->{t1=table.get(0);}, 
				(f,serveur)->{s1=serveur.get(0);}, 
				(f)->{}, 
				(f,l)->{articles=l;});
		CommandeFlowControl.setTraitementErreur((t,m)->erreur=true);
		flow.start();
		assertNotNull(t1);
		flow.selectTable(t1);
		assertEquals(flow.getSelectedTable(),t1);
		assertNotNull(s1);
		flow.selectServeur(s1);
		assertEquals(flow.getSelectedServeur(),s1);
		
		assertNotNull(flow.getSelectedCommande());
		t1=null;s1=null;
		flow.choisirArticle();
		assertNotNull(articles);
		flow.ajoutArticleCMD(articles.get(0));
		assertFalse(erreur);
		assertEquals(1,flow.getSelectedCommande().getArticles().size());
		flow.exit();
		assertFalse(erreur);
		flow.selectTable(t1);
		assertFalse(erreur);
		flow.choisirArticle();
		flow.ajoutArticleCMD(articles.get(0));
		assertFalse(erreur);
		t1=null;s1=null;
		assertEquals(2,flow.getSelectedCommande().getArticles().size());
		flow.exit();
		assertFalse(erreur);
		assertNull(flow.getSelectedTable());
		flow.choisirArticle();
		assertTrue(erreur);
	}

}
