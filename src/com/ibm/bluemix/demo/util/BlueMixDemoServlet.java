package com.ibm.bluemix.demo.util;

import java.io.PrintWriter;
import java.net.InetAddress;
import java.sql.Timestamp;
import java.util.Set;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mongodb.DB;
import com.mongodb.BasicDBObject;
import com.mongodb.util.JSON;
import com.mongodb.BasicDBList;
import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

@WebServlet("/memberinfo")
public class BlueMixDemoServlet extends HttpServlet {
	static String databaseHost = "localhost";
	static String port = "50000";
	static String databaseName = "mongodb-demo";
	static String username = "myuser";
	static String password = "mypass";
	static String databaseUrl = "type://hostname:port/dbname";
	static String thekey = null;
	static boolean dbSetup = false;
	static String dbCollectionName = "MemberInfo";
	
	private void dbSetup(PrintWriter writer) throws Exception{
		
		if(dbSetup) return; //one time variable setting
		String VCAP_SERVICES = System.getenv("VCAP_SERVICES");

		if (VCAP_SERVICES != null) {
			// parse the VCAP JSON structure
			BasicDBObject obj = (BasicDBObject) JSON.parse(VCAP_SERVICES);
			Set<String> keys = obj.keySet();
			// Look for the VCAP key that holds the JSONFB (formerly IJDS)
			// or MongoDB information, it will pick the last one
			for (String eachkey : keys) {
				// The sample work with the IBM JSON Database as well as MongoDB
				// The JSONDB service used to be called IJDS
				if (eachkey.contains("JSONDB")) {
					thekey = eachkey;
				}
				if (eachkey.contains("mongodb")) {
					thekey = eachkey;
				}
			}
			if (thekey == null) {
				throw new Exception("Key is null");
			}
			// Parsing the parameters out of the VCAP JSON document
			BasicDBList list = (BasicDBList) obj.get(thekey);
			obj = (BasicDBObject) list.get("0");
			obj = (BasicDBObject) obj.get("credentials");
			databaseHost = (String) obj.get("host");
			
			//TODO: IJDS uses "db", Informix uses "database"
			databaseName = (String) obj.get("database");
			if (databaseName == null )
				databaseName = (String) obj.get("db");
			// the IJDS service has port as an String, MongoDB has it as a
			// Integer, Informix also uses an Integer
			if (obj.get("port") instanceof Integer) {
				port = (String) obj.get("port").toString();
			} else {
				port = (String) obj.get("port");
			}
			username = (String) obj.get("username");
			password = (String) obj.get("password");
			databaseUrl = (String) obj.get("url");
			
			writer.println("username:"+username+" password:"+ password +" databaseName:"+ databaseName + "port:"+port);
			dbSetup = true;
		} else {
			//throw new Exception("VCAP_SERVICES is null");
		}

	}
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5761861843295817278L;

	public BlueMixDemoServlet(){
		super();
	}
	
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, java.io.IOException {
		//set request and response configuration
		request.setCharacterEncoding("UTF-8");
		response.setContentType("text/plain");
		response.setCharacterEncoding("UTF-8");
		response.setStatus(200);
		
		PrintWriter writer = response.getWriter();
		String name = request.getParameter("name");
		String age = request.getParameter("age");
		String address = request.getParameter("address");
		String operation = request.getParameter("operation");
		
		writer.println("name is:"+ name );
		writer.println("age is:"+ age);
		writer.println("address is:"+ address);
		writer.println("operation is:"+ operation);	
		
		//response.getWriter().write("<name>swanand</name>");
		
		writer.println("Servlet: " + this.getClass().getName());
		writer.println();
		writer.println("VCAP Host: " + System.getenv("VCAP_APP_HOST") + ":"
				+ System.getenv("VCAP_APP_PORT"));
		writer.println("Host IP: "
				+ InetAddress.getLocalHost().getHostAddress());
		
		System.out.println("operation with which servlet called"+operation);
		if (operation ==null) operation = "Create";
		try{
			this.dbSetup(writer);
			switch (operation){
				case "Create":{
					BasicDBObject returnedObj = this.createRecord(name,age,address);
					if(returnedObj !=null){
						writer.println("record created");
						//memberList = new MemberList(returnedObj);
					}
					break;
				}
				case "Update":{
					BasicDBObject returnedObj = this.updateRecord(name,age,address);
					if(returnedObj!=null){
						writer.println("record updated");
						//memberList = new MemberList(returnedObj);
					}
					break;
				}
				case "Delete":{
					BasicDBObject returnedObj = this.deleteRecord(name,age,address);
					if(returnedObj!=null) writer.println("record deleted");
					break;
				}
			}
			DB _db = this.getConnection();
			// get a DBCollection object
			System.out.println("Creating Collection");
			DBCollection col = _db.getCollection(dbCollectionName);
			request.setAttribute("memberCollection", col);
			RequestDispatcher view = request.getRequestDispatcher("memberinfo.jsp");
			view.forward(request, response);
		}catch(Exception e){
			throw new ServletException(e);
		}
	}

	
	private DB getConnection() throws Exception{
	try{
		MongoClient mongoClient;
		DB db = null;
		mongoClient = new MongoClient(databaseHost, Integer.valueOf(port));
		db = mongoClient.getDB(databaseName);
		boolean auth = db.authenticate(username, password.toCharArray());
			if (!auth) {
				throw new Exception("Authorization Error");
			} else {
				System.out.println("Authenticated");
			}
		return db;
		} catch (Exception e) {
			throw e;
		}
	}
	private BasicDBObject createRecord(String name, String age, String address) throws Exception{
		
	DB _db = getConnection();
	// get a DBCollection object
	try {
		System.out.println("Creating Collection");
		DBCollection col = _db.getCollection(dbCollectionName);

		// create a document
		BasicDBObject json = new BasicDBObject();
		json.append("name", name);
		json.append("age", age);
		json.append("address", address);
		//json.append("timestamp", timestampForInsert);


		// insert the document
		col.insert(json);
		System.out.println("after insert");
		return json;
	}
	catch(Exception e){
		throw e;
	}
	}
	
	private BasicDBObject readRecord(String name, String age, String address) throws Exception{	
	try{
		System.out.println("starting object read..");
		DB _db = getConnection();
		BasicDBObject query = new BasicDBObject();
		if( (name!=null) && (name !="") ) 			query.append("name", name);
		if( (age!=null) && (age !="") )				query.append("age", age);
		if( (address!=null) && (address !="") )		query.append("address",address);
		System.out.println("Querying for: " + query);
		DBCollection col = _db.getCollection(dbCollectionName);
		DBCursor cursor = col.find(query);
		BasicDBObject obj=null;
		while (cursor.hasNext()) {
				obj =(BasicDBObject) cursor.next();
				System.out.println("Retrieved: " + obj);
		}
		cursor.close();
		return obj;
	}catch(Exception e){
		throw e;
	}
	}

	
	private BasicDBObject updateRecord(String name, String age, String address) throws Exception{
	try{
		System.out.println("starting object update..");
		DB _db = getConnection();
		BasicDBObject query = new BasicDBObject();
		query.append("name", name);
		System.out.println("Updating Record: " + query);
		DBCollection col = _db.getCollection(dbCollectionName);
		BasicDBObject updates = new BasicDBObject();
		if( (name!=null) && (name !="") ) 			query.append("name", name);
		if( (age!=null) && (age !="") )				query.append("age", age);
		if( (address!=null) && (address !="") )		query.append("address",address);
		
		col.update(query,updates);
		return updates;
	}catch(Exception e){
		throw e;
	}
	}
	
	private BasicDBObject deleteRecord(String name, String age, String address) throws Exception{
		try{
			System.out.println("starting object delete..");
			DB _db = getConnection();
			BasicDBObject query = new BasicDBObject();
			if( (name!=null) && (name !="") ) 			query.append("name", name);
			if( (age!=null) && (age !="") )				query.append("age", age);
			if( (address!=null) && (address !="") )		query.append("address",address);
			System.out.println("Querying for Delete: " + query);
			DBCollection col = _db.getCollection(dbCollectionName);
			DBCursor cursor = col.find(query);
			BasicDBObject obj=null;
			while (cursor.hasNext()) {
					obj = (BasicDBObject)cursor.next();
					System.out.println("Retrieved: " + obj);
			}
			col.remove(query);
			cursor.close();
			return obj;
		}catch(Exception e){
			throw e;
		}
	
	}
	

}//end of class
