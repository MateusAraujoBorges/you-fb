package controllers;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ArrayNode;

import play.Play;
import play.libs.F.Function;
import play.libs.F.Promise;
import play.libs.WS;
import play.libs.WS.Response;
import play.mvc.Controller;
import play.mvc.Result;
import services.FacebookQueryService;


public class Application extends Controller {

	final static String REDIRECT_FACEBOOK;
	static String REDIRECT_LOGIN;
	
	static {
		REDIRECT_FACEBOOK = controllers.routes.Application.login().absoluteURL(request());
		try {
			REDIRECT_LOGIN = "https://www.facebook.com/dialog/oauth?" +
				"client_id=283491021745414&" +
 "scope=user_status,friends_status,user_events,user_birthday,friends_birthday," +
 "user_education_history,friends_education_history,user_work_history," +
 "friends_work_history,user_hometown,friends_hometown&"	+
				"state=PREVENT_CSRF_LATER&" +
				"redirect_uri=" + 
				URLEncoder.encode(REDIRECT_FACEBOOK,"UTF-8");
		} catch (UnsupportedEncodingException e) {
			REDIRECT_LOGIN = "DUMMY";
			e.printStackTrace();
		}
	}
	
	public static Result index() {
		return redirect(controllers.routes.Application.login());
	}
	
	//TODO check if token is expired
	public static Result query(String query) {
		String token = session("accessToken");
		if (token == null) {
			return redirect(controllers.routes.Application.login());
		} else {
			Promise<ArrayNode> users = FacebookQueryService.searchUserJSON(query, token); 
			return async(users.map(new Function<ArrayNode, Result>() {
				@Override
				public Result apply(ArrayNode arg0) throws Throwable {
					return ok(arg0);
				}
			}));
		}
	}

	public static Result me() {
		String token = session("accessToken");
		if (token == null) {
			return redirect(controllers.routes.Application.login());
		} else {
			Promise<JsonNode> users = FacebookQueryService.getUserData(token); 
			return async(users.map(new Function<JsonNode, Result>() {
				@Override
				public Result apply(JsonNode arg0) throws Throwable {
					return ok(arg0);
				}
			}));
		}
	}
	
	public static Result login() {
		String code = request().getQueryString("code");
		
		if (code == null || code.isEmpty()) {
			return redirect(REDIRECT_LOGIN);
		} else {
			String error = request().getQueryString("error");
			if (error != null && error.equals("access_denied")) {
				return unauthorized("You must grant the required permissions!");
			} else {
				Promise<WS.Response> rep = WS.url("https://graph.facebook.com/oauth/access_token")
						.setQueryParameter("client_id", "283491021745414")
						.setQueryParameter("client_secret", "2d959f4210ff43b4df0a8f3dad1a32a0")
						.setQueryParameter("redirect_uri", REDIRECT_FACEBOOK)
						.setQueryParameter("code", code)
						.get();
				
				return async(rep.map(new Function<WS.Response,Result>(){
					public Result apply(Response arg0) throws Throwable {
						System.out.println(arg0.getBody());
						
						if (arg0.getBody().startsWith("access_token=")) {
							String accessToken = arg0.getBody().split("[=&]")[1];
							session("accessToken",accessToken);
							return ok("Login done!");
						} else {
							return internalServerError("Error while retrieving accessToken");
						}
					}
				}));
			}
		}
	}
	
	/**
	 *	For now, it just cleans the session. If we store any other
	 *	sensitive data, this is the place to delete it (at the request
	 *	of the user). 
	 */
	public static Result unlink() {
		session().clear();
		return ok("Link data cleared! Remember to remove the permissions you granted" +
				" to this app on facebook! You will need to login again to use this app.");
	}
}
