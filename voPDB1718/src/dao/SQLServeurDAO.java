package dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import modele.Serveur;

public abstract class SQLServeurDAO implements IServeurDAO {

	private static final String sqlFromId = "SELECT CODE_SER, NOM_SER,PRENOM_SER,TELFIXE_SER, TELGSM_SER, EMAIL_SER FROM TSERVEUR WHERE TRIM(CODE_SER) = ?";
	private static String sqlListe = "SELECT CODE_SER,NOM_SER,PRENOM_SER,TELFIXE_SER,TELGSM_SER,EMAIL_SER FROM TSERVEUR";

	// Logger
	private static final Logger logger = LoggerFactory.getLogger(SQLServeurDAO.class);

	// La factory pour avoir la connexion
	private final SQLDAOFactory factory;

	/**
	 * Construction pour avoir l'accès à la factory et ainsi obtenir la connexion
	 * 
	 * @param factory
	 */
	public SQLServeurDAO(SQLDAOFactory factory) {
		this.factory = factory;
	}

	@Override
	public Serveur getFromID(String id) {
		Serveur s = null;

		try (PreparedStatement query = factory.getConnexion().prepareStatement(sqlFromId)) {

			query.setString(1, id);
			ResultSet rs = query.executeQuery();
			if (rs.next()) {
				s = new Serveur(rs.getString("CODE_SER"), rs.getString("NOM_SER"), rs.getString("PRENOM_SER"),
						rs.getString("TELFIXE_SER"), rs.getString("TELGSM_SER"), rs.getString("EMAIL_SER"));
			}
		} catch (SQLException e) {
			logger.error("Impossible de charger le serveur: " + id + "  erreur: ", e);
			s = null;
		}
		return s;
	}

	@Override
	public List<Serveur> getListe(String regExpr) {
		List<Serveur> liste = new ArrayList<>();
		try (PreparedStatement query = factory.getConnexion().prepareStatement(sqlListe)) {
			Serveur ser;
			ResultSet rs;
			rs = query.executeQuery();
			while (rs.next()) {
				ser = new Serveur(rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5),
						rs.getString(6));
				liste.add(ser);
			}
		} catch (SQLException e) {
			logger.error("Erreur lors du chargement des serveurs", e);
		}
		return liste;
	}

	@Override
	public String insert(Serveur objet) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean delete(Serveur object) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean update(Serveur object) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

}
