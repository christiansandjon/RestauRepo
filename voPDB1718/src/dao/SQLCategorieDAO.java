package dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.naming.OperationNotSupportedException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import modele.Categorie;

public abstract class SQLCategorieDAO implements ICategorieDAO {
	private static String sqlFromId = "Select Nom_Cat,FKCATPRINC_CAT from TCategorie where Code_Cat=?";
	private static String sqlCatFeuilles = "Select Code_Cat,Nom_Cat,FKCATPRINC_CAT from TCategorie where Code_Cat not in (Select c.FKCATPRINC_CAT From TCATEGORIE c where c.FKCATPRINC_CAT is not null)";
	private static String sqlListeCat = "Select Code_Cat,Nom_Cat,FKCATPRINC_CAT from TCategorie";
	private static String sqlInsert = "INSERT INTO TCATEGORIE (CODE_CAT, NOM_CAT,FKCATPRINC_CAT) VALUES (?,?,?)";
	private static String sqlDelete = "Delete from TCATEGORIE WHERE CODE_CAT=?";
	private static String sqlNbEnfants = "Select count(CODE_CAT) From TCategorie Where FKCATPRINC_CAT=?";

	// Logger
	private static final Logger logger = LoggerFactory.getLogger(SQLCategorieDAO.class);

	// La factory pour avoir la connexion
	private final SQLDAOFactory factory;

	/**
	 * Construction pour avoir l'accès à la factory et ainsi obtenir la connexion
	 * 
	 * @param factory
	 */
	public SQLCategorieDAO(SQLDAOFactory factory) {
		this.factory = factory;
	}

	@Override
	public Categorie getFromID(String id) {

		Categorie cat = null, catE, catP;
		if (id != null)
			id = id.trim();
		try (PreparedStatement query = factory.getConnexion().prepareStatement(sqlFromId);) {
			ResultSet rs;
			// préparation d'un query

			// associe une valeur au paramètre (code_cat)
			query.setString(1, id);
			// exécution
			rs = query.executeQuery();
			// parcourt du ResultSet
			if (rs.next()) {
				cat = new Categorie(id, rs.getString(1), null);
				logger.debug("Création d'une catégorie Enfant");
				// initialise Id sur la catégorie père
				id = rs.getString(2);
				// Charge les catégories parents
				catE = cat;
				while (id != null) {
					id = id.trim();
					query.setString(1, id);
					rs = query.executeQuery();
					if (rs.next()) {
						// Création du père
						catP = new Categorie(id, rs.getString(1), null);
						logger.debug("Création d'une catégorie Parent");
						catE.setPereCat(catP);
						id = rs.getString(2);
						catE = catP;
					}
				}
			}
		} catch (SQLException e) {
			logger.error("Erreur SQL pour créer une catégorie", e);
		}
		return cat;

	}

	@Override
	public List<Categorie> getListe(String regExpr) {
		List<Categorie> liste = new ArrayList<>();
		try (PreparedStatement query = factory.getConnexion().prepareStatement(sqlListeCat)) {
			Categorie cat;
			ResultSet rs;

			rs = query.executeQuery();
			while (rs.next()) {
				cat = new Categorie(rs.getString(1), rs.getString(2), null);
				liste.add(cat);
			}
		} catch (SQLException e) {
			logger.error("Erreur lors du chargement des catégories feuilles", e);
		}
		return liste;
	}

	@Override
	public String insert(Categorie c) throws Exception {
		ResultSet rs = null;
		if (c == null)
			return null;
		// il faut créer les catégories pères si elles n'existent pas
		// regarde si la catégorie père existe
		if (c.getPereCat() != null) {
			try (PreparedStatement queryExist = factory.getConnexion().prepareStatement(sqlFromId)) {
				queryExist.setString(1, c.getPereCat().getCode());
				rs = queryExist.executeQuery();
				if (!rs.next())
					insert(c.getPereCat());// ajout récursif
			} catch (SQLException e) {
				logger.error("Erreur d'insertion de la catégorie", e);
				c = null;
			}
		}
		try (PreparedStatement query = factory.getConnexion().prepareStatement(sqlInsert)) {
			// la catégorie père existe
			query.setString(1, c.getCode());
			query.setString(2, c.getNom());
			query.setString(3, c.getPereCat() == null ? null : c.getPereCat().getCode());
			// effectue d'insertion
			query.executeUpdate();
			query.getConnection().commit();
		} catch (SQLException e) {
			logger.error("Erreur d'insertion de la catégorie", e);
			c = null;
		}
		return c.getCode();
	}

	/**
	 * Supprime une catégorie que si c'est une feuille
	 */
	@Override
	public boolean delete(Categorie c) throws Exception {
		boolean ok = false;
		if (c == null)
			return true;
		// Vérifie que le code existe et que c'est une feuille coté OObj
		if (c.getCode() == null || c.getPereCat() != null)
			return false;

		String code = c.getCode().trim();
		ResultSet rs = null;
		int cpt;
		try (PreparedStatement query = factory.getConnexion().prepareStatement(sqlNbEnfants)) {
			query.setString(1, code);
			rs = query.executeQuery();
			rs.next();

			if (rs.getInt(1) == 0) {
				// c'est une feuille on peut la supprimer
				try (PreparedStatement querySupp = factory.getConnexion().prepareStatement(sqlDelete)) {
					querySupp.setString(1, code);
					cpt = querySupp.executeUpdate();
					ok = (cpt != 0);
					logger.debug("Une catégorie feuille a été supprimée:", code);
					factory.getConnexion().commit();
				} catch (SQLException e) {
					factory.getConnexion().rollback();

				}
			}
		}
		return ok;
	}

	@Override
	public boolean update(Categorie object) throws Exception {
		throw new OperationNotSupportedException();

	}

	@Override
	public List<Categorie> getCategoriesFeuille() {
		List<Categorie> liste = new ArrayList<>();
		try (PreparedStatement query = factory.getConnexion().prepareStatement(sqlCatFeuilles)) {
			Categorie cat;
			ResultSet rs;
			rs = query.executeQuery();
			while (rs.next()) {
				cat = new Categorie(rs.getString(1), rs.getString(2), null);
				liste.add(cat);
			}
		} catch (SQLException e) {
			logger.error("Erreur lors du chargement des catégories feuilles", e);
		}
		return liste;
	}

}
