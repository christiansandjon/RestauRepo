package dao;

import modele.Article;

public class RestoInsertException extends RestoException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final Article a;

	public Article getA() {
		return a;
	}

	public RestoInsertException(String message, Article a) {
		super(message);
		this.a = a;
	}

}
