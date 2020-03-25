package dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import modele.Article;
import modele.Commande;
import modele.LigneCommande;
import modele.LigneCommande.EtatLigne;
import modele.LigneCommande.Id;

public abstract class SQLLigneCommandeDAO implements ILigneCommandeDAO {
	private static final String sqlGet = "SELECT FKCOMMANDE_LIG,NUM_LIG, FKARTICLE_LIG, ETAT_LIG,PAYE_LIG,PRIX_LIG FROM TLIGNECMD";
	private static final String sqlInsert = "INSERT INTO TLIGNECMD(FKCOMMANDE_LIG, FKARTICLE_LIG, PRIX_LIG) VALUES(?,?,?) RETURNING NUM_LIG,ETAT_LIG, PAYE_LIG";
	private static final String sqlDelete = "DELETE FROM TLIGNECMD WHERE   FKCOMMANDE_LIG = ? AND NUM_LIG = ?";
	private static final String sqlDeleteLignes = "DELETE FROM TLIGNECMD WHERE FKCOMMANDE_LIG = ?";
	private static final String sqlUpdate = "UPDATE TLIGNECMD SET ETAT_LIG = ?, PAYE_LIG=?,PRIX_LIG=? WHERE NUM_LIG = ? AND FKCOMMANDE_LIG = ?";

	// Logger
	private static final Logger logger = LoggerFactory.getLogger(SQLLigneCommandeDAO.class);

	// La factory pour avoir la connexion
	private final SQLDAOFactory factory;

	/**
	 * Construction pour avoir l'accès à la factory et ainsi obtenir la connexion
	 * 
	 * @param factory
	 */
	public SQLLigneCommandeDAO(SQLDAOFactory factory) {
		this.factory = factory;
	}

	@Override
	public LigneCommande getFromID(Id id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<LigneCommande> getListe(String regExpr) {
		List<LigneCommande> liste = new ArrayList<>();

		try (Statement query = factory.getConnexion().createStatement();) {

			ResultSet res = query.executeQuery(sqlGet + " " + regExpr);

			while (res.next()) {
				// Récupère l'article
				Article a = factory.getArticleDAO().getFromID(res.getString(3));
				// Crée la ligne de commande
				LigneCommande lig = new LigneCommande(new LigneCommande.Id(res.getInt(1), res.getInt(2)), a,
						EtatLigne.valueOf(res.getString(4).trim()), res.getBoolean(5), res.getDouble(6));
				liste.add(lig);
			}
			return liste;
		} catch (SQLException e) {
			logger.error("Erreur de chargement d'une ligne de commande " + e.getSQLState() + " | " + e.getMessage());
		}

		return null;
	}

	@Override
	public Id insert(LigneCommande lig) throws Exception {
		if (lig.getId().getNumLigne() != null)
			update(lig);// La ligne existe déjà donc vers l'update
		else
			try (PreparedStatement query = factory.getConnexion().prepareStatement(sqlInsert)) {

				query.setInt(1, lig.getId().getNumCmd());
				query.setString(2, lig.getArticle().getCode());
				query.setDouble(3, lig.getPrix());

				query.executeQuery();
				query.getConnection().commit();
				ResultSet res = query.getGeneratedKeys();

				if (res.next()) {

					lig.setNumLigne(res.getInt(1));
					lig.setEtat(EtatLigne.valueOf(res.getString(2).trim()));
					lig.setPayee(Boolean.valueOf(res.getInt(3) == 0 ? false : true));
					lig.setPrix(res.getDouble(5));
					return lig.getId();
				}
			} catch (SQLException e) {
				logger.error("Erreur de sauvegarde d'une nouvelle ligne de commande " + e.getSQLState() + " | "
						+ e.getMessage());
			}
		return null;
	}

	@Override
	public boolean delete(LigneCommande ligne) throws Exception {
		try (PreparedStatement query = factory.getConnexion().prepareStatement(sqlDelete)) {
			query.setInt(1, ligne.getId().getNumCmd());
			query.setInt(2, ligne.getId().getNumLigne());
			if (query.executeUpdate() != 0) {
				query.getConnection().commit();
				return true;
			} else {
				query.getConnection().rollback();
				return false;
			}
		} catch (SQLException e) {
			logger.error("Erreur de suppression des lignes " + e.getSQLState() + " | " + e.getMessage());
			return false;
		}
	}

	@Override
	public boolean update(LigneCommande lig) throws Exception {
		//si la ligne n'existe pas encore, on fait un insert
		if (lig.getId().getNumLigne() == null) {
			insert(lig);
			return lig.getId().getNumLigne() != null;
		}
		try (PreparedStatement query = factory.getConnexion().prepareStatement(sqlUpdate)) {

			query.setString(1, lig.getEtat().toString());
			query.setBoolean(2, lig.getPayee());
			query.setDouble(3, lig.getPrix());
			query.setInt(4, lig.getId().getNumLigne());
			query.setInt(5, lig.getId().getNumCmd());

			if (query.executeUpdate() != 0) {
				query.getConnection().commit();
				return true;
			} else {
				query.getConnection().rollback();
				return false;
			}
		} catch (SQLException e) {
			logger.error("Erreur lors de la mise à jour de la  ligne " + e.getSQLState() + " | " + e.getMessage());

		}
		return false;
	}

	@Override
	public boolean deleteLignesCommande(Commande cmd) {

		try (PreparedStatement query = factory.getConnexion().prepareStatement(sqlDeleteLignes)) {
			query.setInt(1, cmd.getNum());
			if (query.executeUpdate() != 0) {
				query.getConnection().commit();
				return true;
			} else {
				query.getConnection().rollback();
				return false;
			}
		} catch (SQLException e) {
			logger.error("Erreur de suppression des lignes " + e.getSQLState() + " | " + e.getMessage());
			return false;
		}
	}

}
