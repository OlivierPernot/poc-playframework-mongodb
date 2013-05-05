package controllers;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.google.gson.Gson;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.Mongo;

import models.Task;
import play.Logger;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.index;

public class Application extends Controller {

	static Form<Task> taskForm = Form.form(Task.class);
	static DB db;
	static long globalid = 0;
	static {
		db = getDb();
	}

	public static Result index() {
		return ok(index.render("Your new application"));
	}

	public static Result bootstrap() {
		return ok(views.html.page.render());
		// return views.html.helper.twitterBootstrap. ok();
	}

	public static Result tasks() {

		return ok(views.html.mainTodoApp.render(getTaskList(), taskForm));
		// return ok(
		// views.html.mainTo .render(Task.all(), taskForm)
		// );
	}

	public static List<Task> getTaskList() {
		DBCollection coll = db.getCollection("tasklist");
		DBCursor cursor = coll.find();
		StringBuffer sBuffer = new StringBuffer();
		List<Task> listTask = new LinkedList();
		Gson gson = new Gson();
		try {
			while (cursor.hasNext()) {
				String json = cursor.next().toString();
				sBuffer.append(json);
				Task jTask = gson.fromJson(json, Task.class);
				listTask.add(jTask);
			}
		} finally {
			cursor.close();
		}
		return listTask;
	}

	public static Result newTask() {
		// Form<Task> filledForm = taskForm.bindFromRequest();
		// if (filledForm.hasErrors()) {
		// return badRequest(views.html.mainTodoApp.render(Task.all(),
		// filledForm));
		// } else {
		// Task.create(filledForm.get());
		// return redirect(routes.Application.tasks());
		// }

		Form<Task> filledForm = taskForm.bindFromRequest();
		if (filledForm.hasErrors()) {
			return badRequest(views.html.mainTodoApp.render(getTaskList(),
					filledForm));
		} else {
			java.util.Map<String, String> data = filledForm.data();
			String label = data.get("label");
			BasicDBObject doc = new BasicDBObject();
			long id = globalid++;

			doc.put("id", id);
			doc.put("label", label);
			DBCollection coll = db.getCollection("tasklist");
			coll.insert(doc);
			return tasks();
		}
	}

	public static Result deleteTask(Long id) {
		Task.delete(id);
		return redirect(routes.Application.tasks());
	}

	/**
	 * get db connection
	 * 
	 * @return
	 */
	private static DB getDb() {

		String langs = play.Configuration.root().getString("application.langs");
		boolean local = true;

		String localHostName = play.Configuration.root().getString(
				"mongo.host");
		Integer localPort = play.Configuration.root().getInt("mongo.port");
		
		Mongo m;
		DB db = null;

		String hostname = localHostName;
		int port = localPort;
		try {
			m = new Mongo(hostname, port);
			db = m.getDB("db");
		} catch (Exception e) {
			Logger.error("Exception while intiating Local MongoDB", e);
		}

		return db;
	}
}
