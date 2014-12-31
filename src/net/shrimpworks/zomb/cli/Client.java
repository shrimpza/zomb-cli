package net.shrimpworks.zomb.cli;

public class Client {

	private final String url;
	private final String key;
	private final ResponseOutput output;

	public Client(String url, String key, ResponseOutput output) {
		this.url = url;
		this.key = key;
		this.output = output;
	}

	public boolean execute(String user, String query) {
		throw new UnsupportedOperationException("Not implemented");
	}

	public static final class Response {

		private final String plugin;
		private final String user;
		private final String query;
		private final String[] response;
		private final String image;

		public Response(String plugin, String user, String query, String[] response, String image) {
			this.plugin = plugin;
			this.user = user;
			this.query = query;
			this.response = response;
			this.image = image;
		}

		public String plugin() {
			return plugin;
		}

		public String user() {
			return user;
		}

		public String query() {
			return query;
		}

		public String[] response() {
			return response.clone();
		}

		public String image() {
			return image;
		}
	}

	public interface ResponseOutput {

		public void output(Response response);
	}
}
