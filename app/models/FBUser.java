package models;

import javax.persistence.Entity;

import org.codehaus.jackson.annotate.JsonProperty;

import play.db.ebean.Model;

@Entity
public class FBUser extends Model {

	@JsonProperty
	long uid;
	
	@JsonProperty
	String username;
	
	@JsonProperty
	String name;

	public String toString() {
		return name + " " + username + " " + uid; 
	}
	
}
