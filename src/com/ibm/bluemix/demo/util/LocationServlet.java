package com.ibm.bluemix.demo.util;

import java.io.PrintWriter;
import java.net.InetAddress;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Set;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.MongoClient;
import com.mongodb.util.JSON;

@WebServlet("/getlocation")
public class LocationServlet extends HttpServlet {
	
	static String databaseHost = "localhost";
	static String port = "50000";
	static String databaseName = "mongodb-demo";
	static String username = "myuser";
	static String password = "mypass";
	static String databaseUrl = "type://hostname:port/dbname";
	static String thekey = null;
	static boolean dbSetup = false;
	static String dbCollectionName = "VehicleInfo";
	public static final double THOUSAND_METER =1000;
	
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

	public LocationServlet(){
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
		
		String strLngtd = request.getParameter("pLngtd");
		String strLttd = request.getParameter("pLttd");
		//String strVchlTyp = request.getParameter("prVchlTyp");
		String strUnit = request.getParameter("pUnt");
		//String strId = request.getParameter("pId");		
		//String strOprtn = request.getParameter("pOprtn");
		
		
		writer.println("Vehicle Longitutde  is:"+ strLngtd);
		writer.println("Vehicle Lattitude  is:"+ strLttd);
		writer.println("Trip Type  is:"+ strUnit);		
	
		
	
		
		writer.println("Servlet: " + this.getClass().getName());
		writer.println();
		writer.println("VCAP Host: " + System.getenv("VCAP_APP_HOST") + ":"
				+ System.getenv("VCAP_APP_PORT"));
		writer.println("Host IP: "
				+ InetAddress.getLocalHost().getHostAddress());
		
		
		try{
			this.dbSetup(writer);			
			DBCollection returnedObj = this.getLocation(strLngtd,strLttd,strUnit);	
			String s;
			ArrayList<String> respData=new ArrayList<String>();			
			DBCursor cursor = returnedObj.find();
			int i=0;
			while (cursor.hasNext()) {
				s = String.format("%s",cursor.next());
				respData.add(s);
				response.addHeader("resp"+i++, s);
		     }
			//response.setAttribute("respdata",respData);
			request.setAttribute("vehicleList", returnedObj);
			//request.getSession();
			
			
			RequestDispatcher view = request.getRequestDispatcher("locationinfo.jsp");
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
	
		
	private DBCollection getLocation(String strLngtd, String strLttd, String strUnt) throws Exception{	
		
		DB _db = getConnection();
		int lrange=100;
		try{
			System.out.println("###########################starting to GetLocation#############################################");
			if (null !=strUnt){
				lrange=Integer.parseInt(strUnt.toString());
			}
			
			GeoCoordinate lFromGeoCoordinate = new GeoCoordinate(Double.valueOf(strLngtd), Double.valueOf(strLttd));
			//DB _db = getConnection();
			//ArrayList<BasicDBObject> lVhclLocs = new ArrayList<BasicDBObject>();
			//System.out.println("starting object delete..");
			//DB _db = getConnection();			
		    DBCollection col1= _db.getCollection("vehicleList");			
		    col1.drop();
			DBCollection lVhclLocs = _db.getCollection("vehicleList");
			//lVhclLocs.drop();	
			
			DBCollection col = _db.getCollection(dbCollectionName);
			DBCursor cursor = col.find();
			BasicDBObject lVhclLoc=null;
			String lstrLng="";
			String lstrLtt="";			
			while (cursor.hasNext()) {
				    lVhclLoc =(BasicDBObject) cursor.next();
					System.out.println("############################Retrieved##########################################: " + lVhclLoc);
					if (null != lVhclLoc.get("curr_lngtd")){
						
						lstrLng = (String) lVhclLoc.get("curr_lngtd");
					}
					if (null !=lVhclLoc.get("curr_lattd")){
						lstrLtt = (String) lVhclLoc.get("curr_lattd");
					}
					GeoCoordinate lToGeoCoordinate = new GeoCoordinate(Double.valueOf(lstrLng), Double.valueOf(lstrLtt));
					double lDistance = lFromGeoCoordinate.distanceTo(lToGeoCoordinate);		
					double lDistanceInKilometer = lDistance / THOUSAND_METER; 
					System.out.println("############################lDistanceInKilometer##########################################: " + lDistanceInKilometer);
					if (lDistanceInKilometer <= lrange) {					
						lVhclLoc.append("distance", lDistanceInKilometer);
						lVhclLocs.insert(lVhclLoc);
						
					}
					
			}
			cursor.close();
			return lVhclLocs;
		}catch(Exception e){
			throw e;
		}
		}
	

}//end of Class
