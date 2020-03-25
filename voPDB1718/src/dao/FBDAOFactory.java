package dao;

import java.sql.Connection;

/**
 * Fabrique concrète pour Firebird
 * 
 * @author Didier
 *
 */
public class FBDAOFactory extends SQLDAOFactory {

	public FBDAOFactory(Connection connexion) {
		super(connexion);
	}

	/**
	 * retourne une implémentation DAOArticle pour Firebird
	 */
	@Override
	public IArticleDAO getArticleDAO() {

		return new FBArticleDAO(this);
	}

	/**
	 * retourne une implémentation DAOCategorie pour Firebird
	 */
	@Override
	public ICategorieDAO getCategorieDAO() {

		return new FBCategorieDAO(this);
	}

	@Override
	public IServeurDAO getServeurDAO() {
		return new FBServeurDAO(this);
	}

	@Override
	public ICommandeDAO getCommandeDAO() {
		return new FBCommandeDAO(this);
	}

	@Override
	public ILigneCommandeDAO getLigneCommandeDAO() {
		return new FBLigneCommandeDAO(this);
	}

}
