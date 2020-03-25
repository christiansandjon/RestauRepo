package test.databases;

import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.sql.Connection;
import java.sql.SQLException;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.Ignore;
import org.testng.annotations.Test;

import databases.ConnexionFromFile;
import databases.ConnexionSingleton;
import databases.Databases;
import databases.IConnexionInfos;
import databases.PersistanceException;

public class TestConnexions {
	/**
	 * Libère la connexion après chaque méthode de test
	 */
	@AfterMethod
	public void libereConnexion(){
		ConnexionSingleton.liberationConnexion();
		ConnexionSingleton.setInfoConnexion(null);
	}
	

	@Test
	public void testConnexionFBParking() throws PersistanceException {
		ConnexionSingleton.setInfoConnexion(
				new ConnexionFromFile("./ressources/connexionParking.properties", Databases.FIREBIRD));
		Connection c = ConnexionSingleton.getConnexion();
		assertNotNull(c);
		try {
			boolean res = c.createStatement().execute("Select count(*) from TPlace ");
			assertTrue(res);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		ConnexionSingleton.liberationConnexion();
	}

	@Ignore
	@Test
	public void testConnexionH2Parking() throws PersistanceException {
		ConnexionSingleton
				.setInfoConnexion(new ConnexionFromFile("./ressources/connexionH2Parking.properties", Databases.H2));
		Connection c = ConnexionSingleton.getConnexion();
		assertNotNull(c);
		ConnexionSingleton.liberationConnexion();
	}

	@Test
	public void testConnexionRestoFichier() throws PersistanceException {
		ConnexionSingleton.setInfoConnexion(
				new ConnexionFromFile("./ressources/connexionRestoTest.properties", Databases.FIREBIRD));

		Connection c = ConnexionSingleton.getConnexion();
		assertNotNull(c);
		ConnexionSingleton.liberationConnexion();
	}

	@Test(expectedExceptions = PersistanceException.class)
	public void testBadFileConnexion() throws PersistanceException {
		ConnexionSingleton
				.setInfoConnexion(new ConnexionFromFile("./ressources/connexionBAD.properties", Databases.FIREBIRD));
	}

	@Test(expectedExceptions = PersistanceException.class)
	public void testBadDBConnexion() throws PersistanceException {
		IConnexionInfos cInfo = new ConnexionFromFile("./ressources/connexionRestoTest.properties", Databases.FIREBIRD);
		// fourni une mauvaise URL pour la DB
		cInfo.getProperties().setProperty("url", "localhost://MauvaiseDatabase.fdb");

		ConnexionSingleton.setInfoConnexion(cInfo);
		ConnexionSingleton.getConnexion();// doit provoquer une erreur
		
	}

	@Test(expectedExceptions = PersistanceException.class)
	public void testSansInfoConnexion() throws PersistanceException {
		// ConnexionSingleton.setInfoConnexion(null);
		ConnexionSingleton.getConnexion();// doit provoquer une erreur
	}
}
