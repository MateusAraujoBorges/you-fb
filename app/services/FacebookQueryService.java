package services;

import java.util.List;
import java.util.concurrent.Callable;

import models.FBUser;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ArrayNode;

import com.googlecode.batchfb.Batcher;
import com.googlecode.batchfb.FacebookBatcher;
import com.googlecode.batchfb.Later;

import play.libs.Akka;
import play.libs.F.Promise;

public class FacebookQueryService {

	private static String USER_QUERY = "select uid,username,name,first_name,last_name," +
			"middle_name,birthday,hometown_location,pic,work,education_history" +
			" from user where uid in "
			+ "(select uid2 from friend where uid1 = me()) and" +
			" strpos(lower(name),\"$NAME$\") >= 0 ";

	public static Promise<JsonNode> getUserData(String accessToken) {
		Batcher batcher = new FacebookBatcher(accessToken);
		final Later<JsonNode> userdata = batcher.graph("me");
		return Akka.future(new Callable<JsonNode>() {
			@Override
			public JsonNode call() throws Exception {
				return userdata.get();
			}
		});
	}
	
	// returns users which name contains query
	public static Promise<List<FBUser>> searchUserModel(String query, String accessToken) {
		Batcher batcher = new FacebookBatcher(accessToken);
		final Later<List<FBUser>> users = batcher.query(
				USER_QUERY.replace("$NAME$", query.toLowerCase()), FBUser.class);
		return Akka.future(new Callable<List<FBUser>>() {
			@Override
			public List<FBUser> call() throws Exception {
				return users.get();
			}
		});
	}
	
	public static Promise<ArrayNode> searchUserJSON(String query, String accessToken) {
		Batcher batcher = new FacebookBatcher(accessToken);
		final Later<ArrayNode> users = batcher.query(
				USER_QUERY.replace("$NAME$", query.toLowerCase()));
		return Akka.future(new Callable<ArrayNode>() {
			@Override
			public ArrayNode call() throws Exception {
				return users.get();
			}
		});
	}

}
