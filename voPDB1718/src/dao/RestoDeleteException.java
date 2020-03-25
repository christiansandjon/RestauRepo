package dao;

import modele.Article;

public class RestoDeleteException extends RestoException {
/**
	 * 
	 */
	private static final long serialVersionUID = 7409543225684003892L;
private final Article a;

public RestoDeleteException(String message, Article a) {
	super(message);
	this.a = a;
}

public Article getA() {
	return a;
}

}
