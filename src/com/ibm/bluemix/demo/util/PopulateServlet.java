package com.ibm.bluemix.demo.util;

import java.io.PrintWriter;
import java.math.BigDecimal;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Date;
import java.util.Set;
import java.text.DateFormat;
import java.text.SimpleDateFormat;








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

@WebServlet("/vehicleinfo")
public class PopulateServlet extends HttpServlet {
	private static String databaseHost = "localhost";
	private static String port = "50000";
	private static String databaseName = "mongodb-demo";
	private static String username = "myuser";
	private static String password = "mypass";
	private static String databaseUrl = "type://hostname:port/dbname";
	private static String thekey = null;
	private static boolean dbSetup = false;
	private static String dbCollectionName = "VehicleInfo";
	private static final double THOUSAND_METER =1000;	

	private static final double LATITUDE_MAX = 90;

	private static final double LATITUDE_MIN = -90;

	private static final double LONGITUDE_MAX = 180;

	private static final double LONGITUDE_MIN = -180;
	
	private static final DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
	
	//GeoCoordinate geoc= new GeoCoordinate();
	
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

	public PopulateServlet(){
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
		String strNm = request.getParameter("pNm");
		String strLngtd = request.getParameter("pLngtd");
		String strLttd = request.getParameter("pLttd");
		//String strVchlTyp = request.getParameter("prVchlTyp");
		String strTrp = request.getParameter("pTrp");
		String strId = request.getParameter("pId");		
		String strOprtn = request.getParameter("pOprtn");
		
		writer.println("Vehicle Name is:"+ strNm );
		writer.println("Vehicle Id is:"+ strId);
		writer.println("Vehicle Longitutde  is:"+ strLngtd);
		writer.println("Vehicle Lattitude  is:"+ strLttd);
		writer.println("Trip Type  is:"+ strTrp);		
		writer.println("Operation  is:"+ strOprtn);	
		
	
		
		writer.println("Servlet: " + this.getClass().getName());
		writer.println();
		writer.println("VCAP Host: " + System.getenv("VCAP_APP_HOST") + ":"
				+ System.getenv("VCAP_APP_PORT"));
		writer.println("Host IP: "
				+ InetAddress.getLocalHost().getHostAddress());
		
		System.out.println("operation with which servlet called"+strOprtn);
		if (strOprtn ==null) strOprtn = "Create";
		try{
			this.dbSetup(writer);
			switch (strOprtn){
				case "Create":{
					BasicDBObject returnedObj = this.createRecord(strNm,strId,strLngtd,strLttd,strTrp);
					if(returnedObj !=null){
						writer.println("record created");
						//memberList = new MemberList(returnedObj);
					}
					break;
				}
				case "Update":{
					BasicDBObject returnedObj = this.updateRecord(strNm,strId,strLngtd,strLttd,strTrp);
					if(returnedObj!=null){
						writer.println("record updated");
						//memberList = new MemberList(returnedObj);
					}
					break;
				}
				case "Delete":{
					BasicDBObject returnedObj = this.deleteRecord(strNm,strId,strLngtd,strLttd,strTrp);
					if(returnedObj!=null) writer.println("record deleted");
					break;
				} 
				
				
				case "DeleteAll":{
					boolean returnedObj = this.deleteAllRecord(strNm,strId,strLngtd,strLttd,strTrp);
					if(returnedObj) writer.println("record deleted");
					break;
				} 
				
				/*case "GetLocation":{
					ArrayList<BasicDBObject> returnedObj = this.getLocation(strUsrLngtd,strUsrLttd,strUsrUnit);
					//to do -
					//if(returnedObj) writer.println("record deleted");
					break;
				} */
			}
			DB _db = this.getConnection();
			// get a DBCollection object
			System.out.println("Creating Collection vehicleCollection");
			DBCollection col = _db.getCollection(dbCollectionName);
			request.setAttribute("vehicleCollection", col);
			RequestDispatcher view = request.getRequestDispatcher("vehicleinfo.jsp");
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
	private BasicDBObject createRecord(String strNm, String strId, String strLngtd, String strLttd, String strTrp ) throws Exception{
		
	DB _db = getConnection();
	// get a DBCollection object
	try {
		System.out.println("Creating Collection");
		DBCollection col = _db.getCollection(dbCollectionName);
		
		
		
		String timestampForInsert = dateFormat.format(new Date());

		// create a document
		BasicDBObject json = new BasicDBObject();
			
			
		if( (null!=strId) && (strId.trim() !="") && !strId.isEmpty() && null != strLngtd && strLngtd.trim()!="" && !strLngtd.isEmpty() && null != strLttd && strLttd.trim()!="" &&  !strLttd.isEmpty() ){
			
		if (validateLongitude(Double.valueOf(strLngtd)) && validateLatitude(Double.valueOf(strLttd))){
			
		
		json.append("vhcl_nm", strNm);
		json.append("curr_lngtd",strLngtd);
		json.append("curr_lattd", strLttd);
		json.append("curr_trp", strTrp);
		json.append("vhcl_id",strId);
		json.append("timestamp", timestampForInsert);
		// insert the document
		col.insert(json);
		System.out.println("Inserted Sucessfully");
		
		}
		
		else {
			System.out.println("Longitude & Lattitude out of range ");
		}
		
		
		}
		else {
			System.out.println("Either  Vehicle Id or Longitude or Lattitude is null ");
		}
		
		
		return json;
	}
	catch(Exception e){
		throw e;
	}
	}
	
	private BasicDBObject readRecord(String strNm, String strId, String strLngtd, String strLttd, String strTrp) throws Exception{	
	try{
		System.out.println("starting object read..");
		DB _db = getConnection();
		BasicDBObject query = new BasicDBObject();
		
		if( (strNm!=null) && (strNm !="") ) query.append("vhcl_nm", strNm);		
		if( (strId!=null) && (strId !="") )	query.append("vhcl_id", strId);		
		if( (strLngtd!=null) && (strLngtd !="") ) query.append("curr_lngtd",strLngtd);		
		if( (strLttd!=null) && (strLttd !="") )		query.append("curr_lattd",strLttd);		
		if( (strTrp!=null) && (strTrp !="") )		query.append("curr_trp",strTrp);		
		
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

	
	private BasicDBObject updateRecord(String strNm, String strId, String strLngtd, String strLttd, String strTrp) throws Exception{
	try{
		System.out.println("starting object update..");
		DB _db = getConnection();
		BasicDBObject query = new BasicDBObject();
		query.append("vhcl_id", strId);
		System.out.println("Updating Record: " + query);
		DBCollection col = _db.getCollection(dbCollectionName);
		String timestampForUpdate = dateFormat.format(new Date());
		
		//check if data already exist else create
		DBCursor cursor =col.find(query);
		BasicDBObject updates = new BasicDBObject();
		if (cursor.hasNext()){		
		
		if( (strNm!=null) && (strNm !="") ) 			updates.append("vhcl_nm", strNm);
		
		if( (strId!=null) && (strId !="") )				updates.append("vhcl_id", strId);
		
		if( (strLngtd!=null) && (strLngtd !="") )		updates.append("curr_lngtd",strLngtd);
		
		if( (strLttd!=null) && (strLttd !="") )		updates.append("curr_lattd",strLttd);
		
		if( (strTrp!=null) && (strTrp !="") )		updates.append("curr_trp",strTrp);	
		
		updates.append("timestamp", timestampForUpdate);
		
		col.update(query,updates);
		
		}
		
		else {
			System.out.println("While updating -------- No data found so creating --------------");
			updates=createRecord(strNm,  strId, strLngtd, strLttd, strTrp );
		}
		return updates;
	}catch(Exception e){
		throw e;
	}
	}
	
	private BasicDBObject deleteRecord(String strNm, String strId, String strLngtd, String strLttd, String strTrp) throws Exception{
		try{
			System.out.println("starting object delete..");
			DB _db = getConnection();
			BasicDBObject query = new BasicDBObject();
			if( (strNm!=null) && (strNm !="") ) query.append("vhcl_nm", strNm);
			
			if( (strId!=null) && (strId !="") )	query.append("vhcl_id", strId);
			
			if( (strLngtd!=null) && (strLngtd !="") )	query.append("curr_lngtd",strLngtd);
			
			if( (strLttd!=null) && (strLttd !="") )	query.append("curr_lattd",strLttd);
			
			if( (strTrp!=null) && (strTrp !="") )		query.append("curr_trp",strTrp);	
			
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
	
	
	private boolean deleteAllRecord(String strNm, String strId, String strLngtd, String strLttd, String strTrp) throws Exception{
		try{
			System.out.println("starting object delete..");
			DB _db = getConnection();			
			DBCollection col = _db.getCollection(dbCollectionName);			
			col.drop();		
			return true;
		}catch(Exception e){
			throw e;
		}
	
	}
	
	
	/**
	 * validateLongitude.
	 * 
	 * @param pLongitude
	 *        longitude
	 * @return validateLongitude
	 */
	private boolean validateLongitude( double pLongitude) {
		boolean lLongitude=true;
		if (pLongitude < LONGITUDE_MIN) {
			lLongitude=false;
			throw new IllegalArgumentException("longitude=[" + pLongitude + "] is below min value");
		} else if (pLongitude > LONGITUDE_MAX) {
			lLongitude=false;
			throw new IllegalArgumentException("longitude=[" + pLongitude + "] is above max value");
		}
		return lLongitude;
	}


	/**
	 * validateLatitude.
	 * 
	 * @param pLatitude
	 *        latitude
	 * @return validateLatitude
	 */
	private boolean validateLatitude(double pLatitude) {
		boolean lLatitude=true;

		if (pLatitude < LATITUDE_MIN) {
			lLatitude=false;
			throw new IllegalArgumentException("latitude=[" + pLatitude + "] is below min value");
		} else if (pLatitude > LATITUDE_MAX) {
			lLatitude=false;
			throw new IllegalArgumentException("latitude=[" + pLatitude + "] is above max value");
		}
		return lLatitude;
	}
	

}//end of class
