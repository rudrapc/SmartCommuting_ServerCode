<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ page import="com.mongodb.DBCollection,com.mongodb.DBCursor,com.mongodb.DBObject,com.mongodb.BasicDBObject,java.util.ArrayList"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>location info</title>
<link rel='stylesheet' href='css/style.css' />
</head>
<body onload="load();">
	<h1>User Location</h1>
	<div class="container"style="width:1000px">
		<h2>User Location</h2>
		<br />
		<form class="form-horizontal" action='vehicleinfo'>
			
			<div class="form-group">
				<label for="pLngtd">Longitude:</label> 
				<input id='pLngtd' class="form-control" type="text" name='pLngtd' placeholder="Longitude"/>
			</div>			
			<div class="form-group">
				<label for="pLttd">Latitude:</label> 
				<input id='pLttd' class="form-control" type="text" name='pLttd' placeholder="Lattitude"/>
			</div>			
			<div class="form-group">
				<label for="pUnt">Range:</label> 
				<input id='pUnt' class="form-control" type="text" name='pUnt' placeholder="Unit"/>
			</div>
			
			<input id='pOprtn' class="form-control" type="submit" name='getLocation'/>
			
		</form>
		<div id='echo' class="messageInfo"></div>
		
		<br />
		<h2>Vehicle List</h2>
		<table id="the_list" class='table-striped clearfix'style="width:900px" onclick="tableselected()">
			<thead>
				<th style="width:100px" fixed><h5>Name</h5></th>
				<th style="width:30px"><h5>Id</h5></th>
				<th style="width:200px"><h5>Longitude</h5>
				<th style="width:200px"><h5>Latitude</h5>
				<th style="width:200px"><h5>Trip</h5>
				<th style="width:200px"><h5>TimeStamp</h5>
				
				
			</th>
			</thead>
			<tfoot>
			</tfoot>
			<tbody id="res">
			<%  //ArrayList<BasicDBObject> returnedObj = (ArrayList<BasicDBObject>) request.getAttribute("vehicleCollection");
				DBCollection col = (DBCollection) request.getAttribute("vehicleList");
				if(col!=null){
					DBCursor cursor = (DBCursor)col.find();
					BasicDBObject obj=null;
					while (cursor.hasNext()) {
						obj = (BasicDBObject) cursor.next();
						out.println("<tr>");
						out.println("<td>");
						out.println(obj.get("vhcl_nm"));
						out.println("</td>");
						out.println("<td>");
						out.println(obj.get("vhcl_id"));
						out.println("</td>");
						out.println("<td>");
						out.println(obj.get("curr_lngtd"));
						out.println("</td>");
						out.println("<td>");
						out.println(obj.get("curr_lattd"));
						out.println("</td>");
						out.println("<td>");
						out.println(obj.get("curr_trp"));
						out.println("</td>");
						out.println("<td>");
						out.println(obj.get("timestamp"));
						out.println("</td>");
					out.println("</tr>");
				}
		}
			 %>
			</tbody>
		</table>
	</div>
</body>

</html>
</html>