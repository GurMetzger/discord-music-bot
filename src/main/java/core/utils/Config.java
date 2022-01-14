package core.utils;

import io.github.cdimascio.dotenv.Dotenv;

public class Config {
	
	private static final Dotenv DOTENV = Dotenv.load();
	
	private Config() {}
	
	public static String get(String key) {
		return DOTENV.get(key.toUpperCase());
	}
	
}
