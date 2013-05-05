package models;

import java.util.*;

import javax.persistence.Entity;
import javax.persistence.Id;

import com.mongodb.DB;
import com.mongodb.Mongo;

import play.Logger;
import play.data.validation.Constraints.Required;
import play.db.ebean.Model;
import play.db.ebean.Model.Finder;

@Entity
public class Task extends Model {

	@Id
	public Long id;
	@Required
	public String label;
	

	/**
	 * finder
	 */
	public static Finder<Long, Task> find = new Finder(Long.class, Task.class);

	public static List<Task> all() {
		return find.all();
	}

	public static void create(Task task) {
		task.save();
	}

	public static void delete(Long id) {
		find.ref(id).delete();
	}
	
	

}