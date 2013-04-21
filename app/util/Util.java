package util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import play.Play;

public class Util {

	public static String getReturnString() {
		String login = Play.application().configuration().getString("facebook.request");
		String clientId = Play.application().configuration().getString("facebook.key");
		String redirect = Play.application().configuration().getString("facebook.redirect");
		try {
			return login.replace("$CLIENTID$", clientId).concat(URLEncoder.encode(redirect,"UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return "ERROR!";
		}
	}
}
