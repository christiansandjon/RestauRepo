package dao;

import java.sql.Connection;

/**
 * Ma fabrique abstraite
 * 
 * @author Didier
 *
 */
public abstract class DAOFactory {
	/**
	 * Types de persistance possibles
	 * 
	 * @author Didier
	 *
	 */
	public enum TypePersistance {
		FIREBIRD, H2, XML
	}

	// retourne les implémentations des DAO en fonction du type de persistance
	public abstract IArticleDAO getArticleDAO();

	public abstract ICategorieDAO getCategorieDAO();

	public abstract IServeurDAO getServeurDAO();

	public abstract ICommandeDAO getCommandeDAO();

	public abstract ILigneCommandeDAO getLigneCommandeDAO();

	/**
	 * Méthode statique pour générer une fabrique concrète
	 * 
	 * @param type
	 *            de persistance
	 * @param connexion
	 *            une connexion SQL ou null si inutile pour du SQL
	 * @return une fabrique concrète pour le type de persistance
	 */
	public static DAOFactory getDAOFactory(TypePersistance type, Connection connexion) {
		switch (type) {
		case FIREBIRD:
			if (connexion!=null)
			return new FBDAOFactory(connexion);// une fabrique concrète pour Firebird
		case H2:
			return null;// new H2DAOFactory();
		default:
			break;
		}
		return null;
	}
}
