package dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import modele.Article;
import modele.Article.Etape;
import modele.Categorie;

public abstract class SQLArticleDAO implements IArticleDAO {
	
	// codes SQL
	private static final String sqlFromId = "SELECT NOM_ART,DESCRIPTION_ART,CALORIE_ART,PRIX_ART,DISPO_ART,FKETAPE_ART,FKCATEGORIE_ART FROM TARTICLE WHERE TRIM(CODE_ART) = ?";
	private static final String sqlGet = "SELECT CODE_ART,NOM_ART,DESCRIPTION_ART,CALORIE_ART,PRIX_ART,DISPO_ART,FKETAPE_ART,FKCATEGORIE_ART FROM TARTICLE";
	private static final String sqlNewArt = "INSERT INTO TARTICLE (CODE_ART,NOM_ART, DESCRIPTION_ART, FKETAPE_ART, PRIX_ART, DISPO_ART, CALORIE_ART, FKCATEGORIE_ART) VALUES (?,?,?,?,?,?,?,?)";
	private static final String sqlDeleteArt = "DELETE FROM TARTICLE WHERE CODE_ART=?";
	private static final String sqlUpdateArt = "UPDATE TARTICLE SET NOM_ART=?, DESCRIPTION_ART=?, FKETAPE_ART=?, PRIX_ART=?, DISPO_ART=?, CALORIE_ART=?, FKCATEGORIE_ART=? WHERE CODE_ART=?";

	// Logger
	private static final Logger logger = LoggerFactory.getLogger(SQLArticleDAO.class);
	
	//La factory pour avoir la connexion
	private final SQLDAOFactory factory; 
	
/**
 * Fourni une factory SQL pour avoir la connexion
 * @param factory
 */
	public SQLArticleDAO(SQLDAOFactory factory) {
		this.factory = factory;
	}

	/**
	 * Retourne l'article à partir de son id
	 * 
	 * @param id
	 * @return l'article crée ou null
	 */
	@Override
	public Article getFromID(String id) {
		Article a = null; // l'article à créer
		try(PreparedStatement query = factory.getConnexion().prepareStatement(sqlFromId);) {
			query.setString(1, id);
			ResultSet rs = query.executeQuery();
			if (rs.next()) {// avance sur l'enregistrement
				// charge la catégorie si pas null
				Categorie cat = null;
				if (rs.getString(7) != null)// demande à l'usine un DAOCatégorie
					cat = factory.getCategorieDAO()
							.getFromID(rs.getString(7));
				// Création de l'article
				a = new Article(id, rs.getString(1), rs.getString(2), rs.getInt(3), rs.getDouble(4),
						rs.getInt(5) == 1 ? true : false,
						"E".equals(rs.getString(6)) ? Etape.ENTREE
								: "P".equals(rs.getString(6)) ? Etape.PLAT
										: "D".equals(rs.getString(6)) ? Etape.DESSERT : Etape.AUCUNE,
						cat);
			}
		} catch (SQLException e) {
			logger.error("Impossible de lire l'article: " + id + "  erreur: ", e);
			a=null;
		}
		return a;
	}

	// pour initialiser le paramatère d'un Where
	@FunctionalInterface
	private interface IInitParam {
		void setParam(PreparedStatement q) throws SQLException;
	};

	/**
	 * Permet d'avoir une méthode générale pour les liste, liste par catég et
	 * liste par étape
	 * 
	 * @param WHERE_ORDER
	 *            code sql suite avec clause where et order by
	 * @param initParam
	 *            une lambda qui a pour but d'init le les paramètres du
	 *            PreparedQuery
	 * 
	 * @return une liste d'article ou null en cas d'erreur
	 */

	private List<Article> getListeG(String WHERE_ORDER, IInitParam initParam) {
		String sqlListe = sqlGet + WHERE_ORDER;
		// obtenir un dao pour les catégories
		ICategorieDAO daoCat =factory.getCategorieDAO();
		// création d'une liste vide
		List<Article> liste = new ArrayList<Article>();
		try(PreparedStatement query = factory.getConnexion().prepareStatement(sqlListe);) {
			
			// spécifie les paramètres du query
			if (initParam != null)
				initParam.setParam(query);

			ResultSet rs = query.executeQuery();
			while (rs.next()) {// avance sur l'enregistrement
				// charge la catégorie si pas null
				Categorie cat = null;
				if (rs.getString(8) != null)// l'article possède une catégorie
					cat = daoCat.getFromID(rs.getString(8));
				// Création de l'article
				liste.add(
						new Article(rs.getString(1),
								rs.getString(2), rs.getString(3), rs.getInt(4), rs.getDouble(5), rs.getInt(6) == 1
										? true : false,
								"E".equals(rs.getString(7)) ? Etape.ENTREE
										: "P".equals(rs.getString(7)) ? Etape.PLAT
												: "D".equals(rs.getString(7)) ? Etape.DESSERT : Etape.AUCUNE,
								cat));
			}
		} catch (SQLException e) {
			logger.error("Impossible de lire les articles. Erreur: ", e);
			liste=null; 
		}

		return liste;
	}

	/**
	 * Retourne une liste d'articles triée par code
	 * 
	 * @param regExpr
	 *            pas utilisé ici
	 * 
	 * @return une liste d'article triée par code_art
	 */
	@Override
	public List<Article> getListe(String regExpr)  {
		return getListeG(" ORDER BY CODE_ART", null);
	}

	/**
	 * Retourne la liste d'articles pour une étape
	 * 
	 * @param etape
	 *            une étape ou null
	 * 
	 * @return une liste d'article triée par code_art
	 */
	@Override
	public List<Article> getArticlesEtape(Etape etape) {
		if (etape == null)
			return getListeG(" WHERE FKETAPE_ART is null ORDER BY CODE_ART", null);

		return getListeG(" WHERE FKETAPE_ART= ? ORDER BY CODE_ART", (q) -> {
			q.setString(1, etape.name().substring(0, 1));
		});

	}

	/**
	 * Retourne la liste d'articles pour une catégorie
	 * 
	 * @param cat
	 *            une catégorie ou null
	 * 
	 * @return une liste d'article triée par code_art
	 */
	@Override
	public List<Article> getArticlesCategorie(Categorie cat) {
		if (cat == null)
			return getListeG(" WHERE FKCATEGORIE_ART is null ORDER BY CODE_ART", null);

		return getListeG(" WHERE FKCATEGORIE_ART=? ORDER BY CODE_ART", (q) -> {
			q.setString(1, cat.getCode());
		});
	}

	/**
	 * @param a
	 *            une article
	 * @return le code de l'article créé (ici déjà présent dans l'article in) ou
	 *         null si erreur
	 */
	@Override
	public String insert(Article a) throws RestoException {
		try {
			PreparedStatement query = factory.getConnexion().prepareStatement(sqlNewArt);
			query.setString(1, a.getCode());
			query.setString(2, a.getNom());
			query.setString(3, a.getDescription());
			// si étape alors renvoie la 1ère lettre
			query.setString(4, a.getEtape() == null || a.getEtape().equals(Etape.AUCUNE) ? null
					: a.getEtape().name().substring(0, 1));
			query.setDouble(5, a.getPrix());
			query.setInt(6, a.getDispo() == null ? 1 : a.getDispo() ? 1 : 0);
			query.setInt(7, a.getCalories());
			query.setString(8, a.getCat() == null ? null : a.getCat().getCode());
			query.execute();
			query.getConnection().commit();
			return a.getCode();
		} catch (SQLException e) {
			logger.error("Impossible de créé l'article " + a + " Erreur: ", e);
			throw new RestoInsertException(e.getMessage(), a);
		}
	}

	/**
	 * @throws RestoException
	 *             si l'opération ne peut pas ce faire
	 * @param a
	 *            article à supprimer
	 * @return true si ok
	 */
	@Override
	public boolean delete(Article a) throws RestoException {

		PreparedStatement query;
		try {
			query =factory.getConnexion().prepareStatement(sqlDeleteArt);
			query.setString(1, a.getCode());
			query.execute();
			query.getConnection().commit();
		} catch (SQLException e) {
			logger.error("Impossible de supprimer l'article " + a + " Erreur: ", e);
			throw new RestoDeleteException(e.getMessage(),a);
		}

		return true;
	}

	/**
	 * Ne fait pas de contrôle et fait un update sur tout les champs sauf PK
	 * 
	 * @throws RestoException
	 *             si l'opération ne peut pas ce faire
	 * @param a
	 *            article à mettre à jour
	 * @return true si ok
	 */
	@Override
	public boolean update(Article a) throws RestoException {
		// Version simple qui ne se pose pas de question en mettant tout à jour
		PreparedStatement query;
		try {
			query =factory.getConnexion().prepareStatement(sqlUpdateArt);
			query.setString(1, a.getNom());
			query.setString(2, a.getDescription());
			// si étape alors renvoie la 1ère lettre
			query.setString(3, a.getEtape() == null||a.getEtape().equals(Etape.AUCUNE) ? null : a.getEtape().name().substring(0, 1));
			query.setDouble(4, a.getPrix());
			query.setInt(5, a.getDispo() == null ? 1 : a.getDispo() ? 1 : 0);
			query.setInt(6, a.getCalories());
			query.setString(7, a.getCat() == null ? null : a.getCat().getCode());
			query.setString(8, a.getCode());
			query.execute();
			query.getConnection().commit();
		} catch (SQLException e) {
			logger.error("Impossible de mettre à jour l'article " + a + " Erreur: ", e);
			throw new RestoException(e.getMessage());
		}
		return true;
	}


}
