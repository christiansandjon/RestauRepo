package modele;

public class Serveur {
	private final String code;
	private String nom;
	private String prenom;
	private String telFixe;
	private String telGsm;
	private String email;

	public Serveur(String code, String nom, String prenom) {
		this.code = code.trim();
		this.nom = nom;
		this.prenom = prenom;
	}

	public Serveur(String code, String nom, String prenom, String telFixe, String telGsm, String email) {
		this(code, nom, prenom);
		this.telFixe = telFixe;
		this.telGsm = telGsm;
		this.email = email;
	}

	public String getNom() {
		return nom;
	}

	public void setNom(String nom) {
		this.nom = nom;
	}

	public String getPrenom() {
		return prenom;
	}

	public void setPrenom(String prenom) {
		this.prenom = prenom;
	}

	public String getTelFixe() {
		return telFixe;
	}

	public void setTelFixe(String telFixe) {
		this.telFixe = telFixe;
	}

	public String getTelGsm() {
		return telGsm;
	}

	public void setTelGsm(String telGsm) {
		this.telGsm = telGsm;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getCode() {
		return code;
	}

	@Override
	public String toString() {
		return "Serveur [code=" + code + ", nom=" + nom + ", prenom=" + prenom + ", telFixe=" + telFixe + ", telGsm="
				+ telGsm + ", email=" + email + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((code == null) ? 0 : code.hashCode());
		result = prime * result + ((email == null) ? 0 : email.hashCode());
		result = prime * result + ((nom == null) ? 0 : nom.hashCode());
		result = prime * result + ((prenom == null) ? 0 : prenom.hashCode());
		result = prime * result + ((telFixe == null) ? 0 : telFixe.hashCode());
		result = prime * result + ((telGsm == null) ? 0 : telGsm.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Serveur other = (Serveur) obj;
		if (code == null) {
			if (other.code != null)
				return false;
		} else if (!code.equals(other.code))
			return false;
		if (email == null) {
			if (other.email != null)
				return false;
		} else if (!email.equals(other.email))
			return false;
		if (nom == null) {
			if (other.nom != null)
				return false;
		} else if (!nom.equals(other.nom))
			return false;
		if (prenom == null) {
			if (other.prenom != null)
				return false;
		} else if (!prenom.equals(other.prenom))
			return false;
		if (telFixe == null) {
			if (other.telFixe != null)
				return false;
		} else if (!telFixe.equals(other.telFixe))
			return false;
		if (telGsm == null) {
			if (other.telGsm != null)
				return false;
		} else if (!telGsm.equals(other.telGsm))
			return false;
		return true;
	}

}
