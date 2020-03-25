package databases;

import java.util.Properties;

public class ConnexionFB_Employee implements IConnexionInfos {

	
	@Override
	public Properties getProperties() {
		Properties props = new Properties();
//		String user=JOptionPane.showInputDialog(null, " User:?");
//		if (user ==null) user="SYSDBA";
//		String pw=JOptionPane.showInputDialog(null, " PW:?");
		String user="etudiant";
		String pw="isfce";
		props.setProperty("user", user);
		props.setProperty("password", pw);
		props.setProperty("encoding", "NONE");
		props.setProperty("url",
				Databases.FIREBIRD.buildServeurURL("emp", "192.168.0.5"));
		return props;
	}

}
