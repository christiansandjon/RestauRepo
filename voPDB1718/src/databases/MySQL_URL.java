package databases;


public class MySQL_URL implements IDbURL {

	@Override
	public String getUrl() {
		return "jdbc:mysql://";
	}

	@Override
	public String buildMemURL(String file) {
		throw new UnsupportedOperationException("MySQL ne supporte pas le mode memoire!");
	}

	@Override
	public String buildEmbeddedURL(String file) {
		throw new UnsupportedOperationException("MySQL ne supporte pas le mode embarque!");
	}
	
	@Override
	public String buildServeurURL(String file, String ip) {
		return buildServeurURL(file, ip, getDefaultPort());
	}
	
	@Override
	public String buildServeurURL(String file, String ip, int port) {
		return this.getUrl() + ip + ":" + port + "/" + file;
	}

	@Override
	public boolean hasMemoryMode() {
		return false;
	}

	@Override
	public boolean hasEmbeddedMode() {
		return false;
	}

	@Override
	public boolean hasServeurMode() {
		return true;
	}

	@Override
	public int getDefaultPort() {
		return 3306;
	}
}
