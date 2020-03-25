package dao;

import java.util.List;

import modele.Article;
import modele.Article.Etape;
import modele.Categorie;

public interface IArticleDAO extends IDAO<Article, String> {
	/**
	 * retourne la liste des articles pour une étape
	 * 
	 * @param etape
	 * @return
	 * @throws RestoException
	 */
	List<Article> getArticlesEtape(Etape etape) throws RestoException;

	/**
	 * retourne la liste des articles pour une catégorie
	 * 
	 * @param cat
	 * @return
	 * @throws RestoException
	 */
	List<Article> getArticlesCategorie(Categorie cat) throws RestoException;

}
