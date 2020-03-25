package dao;

import java.util.List;

import modele.Categorie;

public interface ICategorieDAO extends IDAO<Categorie, String>{
	/**
	 * retourne la liste des catégories feuilles sans les parents
	 * @return liste des catégories feuilles
	 */
	List<Categorie> getCategoriesFeuille();
}
