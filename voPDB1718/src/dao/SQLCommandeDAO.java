package dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import modele.Commande;
import modele.Commande.EtatCmd;
import modele.LigneCommande;
import modele.Serveur;

public abstract class SQLCommandeDAO implements ICommandeDAO {
	private static final String sqlFromId = "SELECT NUM_COM, MOMENTA_COM, MOMENTS_COM, ETAT_COM, TOTAL_COM, FKSERVEUR_COM FROM TCOMMANDE WHERE NUM_COM = ?";
	private static final String sqlInsert = "INSERT INTO TCOMMANDE (FKSERVEUR_COM) VALUES (?) RETURNING NUM_COM, MOMENTA_COM";
	private static final String sqlDeleteCom = "DELETE FROM TCOMMANDE WHERE NUM_COM=?";
	private static final String sqlUpdateCom = "UPDATE TCOMMANDE SET ETAT_COM=?,TOTAL_COM=?, MOMENTS_COM=? WHERE NUM_COM=?";

	// Logger
	private static final Logger logger = LoggerFactory.getLogger(SQLCommandeDAO.class);

	// La factory pour avoir la connexion
	private final SQLDAOFactory factory;

	/**
	 * Construction pour avoir l'accès à la factory et ainsi obtenir la connexion
	 * 
	 * @param factory
	 */
	public SQLCommandeDAO(SQLDAOFactory factory) {
		this.factory = factory;
	}

	@Override
	public Commande getFromID(Integer id) {
		try (PreparedStatement query = factory.getConnexion().prepareStatement(sqlFromId)) {
			query.setInt(1, id);
			ResultSet res = query.executeQuery();
			if (res.next()) {
				// Charge le serveur
				Serveur s = factory.getServeurDAO().getFromID(res.getString(6));
				// charge les lignes de commande
				List<LigneCommande> lignes = factory.getLigneCommandeDAO().getListe("where FKCOMMANDE_LIG=" + id);

				Commande com = new Commande(id, res.getTimestamp(2).toLocalDateTime(),
						res.getTimestamp(3) == null ? null : res.getTimestamp(3).toLocalDateTime(),
						EtatCmd.valueOf(res.getString(4).trim()), res.getDouble(5), s, lignes);
				return com;
			}

		} catch (SQLException e) {

		}

		return null;
	}

	@Override
	public List<Commande> getListe(String regExpr) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Integer insert(Commande cmd) throws Exception {
		Integer retour = null;
		ResultSet rs;
		if (cmd.getNum() != null)
			update(cmd);
		else {
			try (PreparedStatement query = factory.getConnexion().prepareStatement(sqlInsert)) {
				query.setString(1, cmd.getServeur().getCode());
				rs = query.executeQuery();
				// rs=query.getGeneratedKeys();
				if (rs.next()) {
					retour = rs.getInt(1);
					cmd.setNum(retour);
					cmd.setMomentA(rs.getTimestamp(2).toLocalDateTime());
				}
				query.getConnection().commit();
				logger.info("Une nouvelle commande est créée avec l'id: " + retour);
			} catch (SQLException e) {
				logger.error("Impossible de créer l'objet: " + e.getMessage());
				throw (e);
			}
		}
		return retour;
	}

	/**
	 * Supprime la commande et ses lignes
	 */
	@Override
	public boolean delete(Commande commande) throws Exception {
		try (PreparedStatement query = factory.getConnexion().prepareStatement(sqlDeleteCom)) {
			// Supprime les lignes de commandes
			if (!commande.getArticles().isEmpty()) {
				factory.getLigneCommandeDAO().deleteLignesCommande(commande);
			}
			query.setInt(1, commande.getNum());
			if (query.executeUpdate() != 0) {
				query.getConnection().commit();

				return true;
			}

		} catch (SQLException e) {
			logger.error("Impossible de supprimer la commande: " + commande + " " + e.getMessage());
		}
		return false;
	}

	
	@Override
	public boolean update(Commande cmd) throws Exception {
		boolean ok;
		ResultSet rs;
		if (cmd.getNum()==null) return false;//la commande doit déjà exister
		try(PreparedStatement query = factory.getConnexion().prepareStatement(sqlUpdateCom)){;
		 query.setString(1, cmd.getEtat().toString());
		 query.setDouble(2, cmd.getTotal());
		 query.setTimestamp(3, cmd.getMomentS()==null?null:Timestamp.valueOf(cmd.getMomentS()));
		 
		 query.setInt(4, cmd.getNum());
		 ok=query.executeUpdate() != 0; 
		 if (ok) {//on gère les lignes de commande
			 query.getConnection().commit();
			 //sauve les lignes de commande
			 for (LigneCommande l:cmd.getArticles()) {
				 //Les lignes doivent déjà exister en BD
				 ok=ok & factory.getLigneCommandeDAO().update(l);
			 }
		 }
		return ok;
		}
		catch (SQLException e) {
			logger.error("Impossible de mettre à jour la commande: " + cmd + " " + e.getMessage());
		}
		return false;
	}

}
