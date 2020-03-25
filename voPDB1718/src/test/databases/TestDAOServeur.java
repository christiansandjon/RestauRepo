package test.databases;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import dao.DAOFactory;
import dao.DAOFactory.TypePersistance;
import databases.ConnexionFromFile;
import databases.ConnexionSingleton;
import databases.Databases;
import databases.PersistanceException;
import modele.Serveur;

public class TestDAOServeur {

	private static DAOFactory fabrique;

	@BeforeClass
	public void init() throws PersistanceException {
		// crée une fabrique pour les vues
		ConnexionSingleton
				.setInfoConnexion(new ConnexionFromFile("./ressources/connexionRestoTest.properties", Databases.FIREBIRD));
		fabrique = DAOFactory.getDAOFactory(TypePersistance.FIREBIRD, ConnexionSingleton.getConnexion());
	}

	@Test
	public void getServeur() {
		Serveur s = fabrique.getServeurDAO().getFromID("Lola");
		Serveur expected = new Serveur("Lola", "Crown", "Lola", "02/654.89.13", null, "lola@resto.be");
		assertEquals(s, expected);
	}
}
