package test.databases;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import org.testng.annotations.Ignore;
import org.testng.annotations.Test;

import databases.Databases;

public class TestDBURL {
	@Test
	public void testH2() {
		// assertEquals(Databases.valueOf("H2"),Databases.H2);
		// assertEquals(Databases.H2.ordinal(), 0);
		// assertEquals(Databases.values()[0],Databases.H2);
		assertTrue(Databases.H2.hasMemoryMode());
		assertTrue(Databases.H2.hasEmbeddedMode());
		assertTrue(Databases.H2.hasServeurMode());
		assertEquals(Databases.H2.buildMemURL(null), "jdbc:h2:mem:");
		assertEquals(Databases.H2.buildMemURL("brol"), "jdbc:h2:mem:brol");
	}

	@Test
	public void testFirebirdURL() {
		// assertEquals(Databases.FIREBIRD, Databases.valueOf("FIREBIRD"));
		// assertEquals(Databases.FIREBIRD.ordinal(), 1);
		assertFalse(Databases.FIREBIRD.hasMemoryMode(), "Elle ne doit pas posséder un mode mémoire");
		assertTrue(Databases.FIREBIRD.hasEmbeddedMode(), "Elle doit posséder un mode embarqué");
		assertTrue(Databases.FIREBIRD.hasServeurMode(), "Elle doit posséder un mode serveur");
		assertEquals(Databases.FIREBIRD.buildEmbeddedURL("c:\test"), "jdbc:firebirdsql:embedded:c:\test");
		assertEquals(Databases.FIREBIRD.buildServeurURL("brol", "localhost"), "jdbc:firebirdsql:localhost/3050:brol");
		assertEquals(Databases.FIREBIRD.buildServeurURL("brol", "192.168.0.5", 3051),
				"jdbc:firebirdsql:192.168.0.5/3051:brol");
	}

	/**
	 * On ne doit pas pouvoir construire une URL mémoire avec Firebird ==> une
	 * exception doit être provoquée si on essaye de construire une url mémoire
	 */
	@Test(expectedExceptions = UnsupportedOperationException.class)
	public void testFirebirdNonMem() {
		Databases.FIREBIRD.buildMemURL("");
	}

	@Ignore
	@Test(timeOut = 1000)
	public void testPostGreSQLURL() {
		// Assert.assertEquals(Databases.POSTGRESQL, Databases.valueOf("POSTGRESQL"));
		// Assert.assertEquals(Databases.POSTGRESQL.ordinal(), 2);
		// Assert.assertFalse(Databases.POSTGRESQL.hasMemoryMode(),"Pas de mode
		// mémoire");
		// Assert.assertFalse(Databases.POSTGRESQL.hasEmbeddedMode(),"Pas de mode
		// embarqué");
		// Assert.assertTrue(Databases.POSTGRESQL.hasServeurMode(),"Possède un mode
		// serveur");
		// Assert.assertEquals(Databases.POSTGRESQL.buildServeurURL("c:\test",
		// "localhost"), "jdbc:postgresql://localhost:5740/c:\test");
		// Assert.assertEquals(Databases.POSTGRESQL.buildServeurURL("brol",
		// "192.168.10.5", 3050), "jdbc:postgresql://192.168.10.5:3050/brol");
	}

	/**
	 * On ne doit pas pouvoir construire une URL mémoire ou embarquée avec
	 * PostGreSQL ==> une exception doit être provoquée si on essaie
	 */
	@Ignore
	@Test(expectedExceptions = UnsupportedOperationException.class)
	public void testPostGreSQLNonMem() {
		// Databases.POSTGRESQL.buildMemURL("");
	}

	@Ignore
	@Test(expectedExceptions = UnsupportedOperationException.class)
	public void testPostGreSQLNonEmbedded() {
		// Databases.POSTGRESQL.buildEmbeddedURL("");
	}

	/////////// test MySQL_URL /////////////
	@Test
	public void testMySQL() {
		assertFalse(Databases.MYSQL.hasMemoryMode());
		assertFalse(Databases.MYSQL.hasEmbeddedMode());
		assertTrue(Databases.MYSQL.hasServeurMode());
		// assertEquals(Databases.MYSQL.buildMemURL(null), "jdbc:mysql:mem:");
		// assertEquals(Databases.MYSQL.buildMemURL("brol"), "jdbc:h2:mem:brol");
		assertEquals(Databases.MYSQL.buildServeurURL("testDB", "localhost"), "jdbc:mysql://localhost:3306/testDB");
		assertEquals(Databases.MYSQL.buildServeurURL("testDB", "127.0.0.1",2222),"jdbc:mysql://127.0.0.1:2222/testDB");
	}
	
	//Exceptions:
	@Test(expectedExceptions = UnsupportedOperationException.class)
	public void testMySQLNonMem() {
		Databases.MYSQL.buildMemURL("");
	}

	@Test(expectedExceptions = UnsupportedOperationException.class)
	public void testMySQLNonEmbeded() {
		Databases.MYSQL.buildEmbeddedURL("");
	}
	
	////////////////////////////////////////
}
