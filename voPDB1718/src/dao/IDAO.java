package dao;

import java.util.List;

/**
 * 
 * @author Didier
 *
 * @param <T> type des objets DAO exemple Client,...
 * @param <K> type de l'id des objets
 */
public interface IDAO<T,K> {
	/**
	 * 
	 * @param id L'id de l'objet
	 * @return l'objet crée et ses dépendances éventuelles
	 */
	T getFromID(K id);
	/**
	 * 
	 * @param Expression régulière à appliquer sur l'ID. 
	 * 		  null: pas de condition
	 * @return une liste avec les objets
	 */
	List<T> getListe(String regExpr);
	/**
	 * rend l'objet persistant
	 * @param objet
	 * @return son id 
	 */
	K insert(T objet) throws  Exception;
	/**
	 * supprime l'objet de la persistance
	 * @param object
	 * @return true si la suppression est ok
	 */
	boolean delete(T object) throws Exception;
	/**
	 * met la persistance à jour pour cet objet
	 * @param object à mettre à jour
	 * @return true si ok
	 */
	boolean update(T object) throws Exception;
	
}
