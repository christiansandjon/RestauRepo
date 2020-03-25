package test.databases;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import dao.DAOFactory;
import dao.DAOFactory.TypePersistance;
import dao.ICommandeDAO;
import databases.ConnexionFromFile;
import databases.ConnexionSingleton;
import databases.Databases;
import databases.PersistanceException;
import modele.Article;
import modele.Commande;
import modele.Serveur;

public class TestDAO {
	private static DAOFactory fabrique;

	@BeforeClass
	public void init() throws PersistanceException {
		// crée une fabrique pour les vues
		ConnexionSingleton
				.setInfoConnexion(new ConnexionFromFile("./ressources/connexionRestoTest.properties", Databases.FIREBIRD));
		fabrique = DAOFactory.getDAOFactory(TypePersistance.FIREBIRD, ConnexionSingleton.getConnexion());
	}

	@Test
	public void daoArticle() {
		Article a = fabrique.getArticleDAO().getFromID("BCOCA");
		assertEquals(a.getCode(), "BCOCA");
		assertEquals(a.getCalories(), new Integer(120));
		assertEquals(a.getNom(), "Cocacola");
	}

	@Test
	public void daoCommandeInsert() throws Exception {
		ICommandeDAO cmdDAO = fabrique.getCommandeDAO();
		Serveur s = fabrique.getServeurDAO().getFromID("Lola");
		Commande c = new Commande(s);
		Integer id = cmdDAO.insert(c);
		assertNotNull(c.getNum());
		assertEquals(id,c.getNum());
		assertNotNull(c.getMomentA());
		assertEquals(0.0, c.getTotal());
		assertEquals(s, c.getServeur());
	}
}
