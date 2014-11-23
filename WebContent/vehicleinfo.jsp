<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ page import="com.mongodb.DBCollection,com.mongodb.DBCursor,com.mongodb.DBObject,com.mongodb.BasicDBObject"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>BlueMix Demo Sample App</title>
<link rel='stylesheet' href='css/style.css' />
<script>
function tableselected(){
	var table = document.getElementsByTagName("table")[0];
	var tbody = table.getElementsByTagName("tbody")[0];
	tbody.onclick = function (e) {
	    e = e || window.event;
	    var data = [];
	    var target = e.srcElement || e.target;
	    while (target && target.nodeName !== "TR") {
	        target = target.parentNode;
	    }
	    if (target) {
	        var cells = target.getElementsByTagName("td");
	        for (var i = 0; i < cells.length; i++) {
	            data.push(cells[i].innerHTML);
	            
	        	if(i==0){
	         		document.getElementById('pNm').innerHTML= cells[i].innerHTML;
	        	}
	            if(i==1){
	        		document.getElementById('vid').innerHTML = cells[i].innerHTML;
	        	}
	        	if(i==2){
	        		document.getElementById('pLngtd').innerHTML = cells[i].innerHTML;
	        	}
	        	if(i==3){
	        		document.getElementById('pLttd').innerHTML = cells[i].innerHTML;
	        	}
	        	if(i==4){
	        		document.getElementById('pTrp').innerHTML = cells[i].innerHTML;
	        	}
	        	if(i==5){
	        		document.getElementById('pTm').innerHTML = cells[i].innerHTML;
	        	}
	        }
	    }
	   alert(data);
	};
}

function postForm(operation){
		document.getElementById('pOprtn').value=operation;
		document.getElementById('pOprtn').innerHTML=operation;
	}
	
</script>
</head>
<body onload="load();">
	<h1>Vehicle Info</h1>
	<div class="container"style="width:1000px">
		<h2>Vehicle Current Status</h2>
		<br />
		<form class="form-horizontal" action='vehicleinfo'>
			<div class="form-group">
				<label for="pNm">Name:</label> 
				<input id='pNm' class="form-control" type='text' name='pNm' placeholder="Name" />
			</div>
			<div class="form-group">
				<label for="pId">Id:</label> 
				<input id='pId' class="form-control" type="text" name='pId' style="width:120px" placeholder="VehicleId"/>
			</div>
			<div class="form-group">
				<label for="pLngtd">Longitude:</label> 
				<input id='pLngtd' class="form-control" type="text" name='pLngtd' placeholder="Longitude"/>
			</div>			
			<div class="form-group">
				<label for="pLttd">Latitude:</label> 
				<input id='pLttd' class="form-control" type="text" name='pLttd' placeholder="Lattitude"/>
			</div>			
			<div class="form-group">
				<label for="pTrp">Trip Type:</label> 
				<input id='pTrp' class="form-control" type="text" name='pTrp' placeholder="Trip"/>
			</div>
			
			<input id='pOprtn' class="form-control" type="hidden" name='pOprtn'/>
			<div class="buttonContainer">
			<button type="submit" class="btn btn-primary" id='read'   onclick="postForm('Create')">Create</button>
			<button type="submit" class="btn btn-primary" id='update' onclick="postForm('Update')">Update</button>
			<button type="submit" class="btn btn-primary" id='delete' onclick="postForm('Delete')">Delete</button>
			<button type="submit" class="btn btn-primary" id='deleteAll' onclick="postForm('DeleteAll')">DeleteALL</button>
			
		</div>	
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
			<%
				DBCollection col = (DBCollection) request.getAttribute("vehicleCollection");
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
<script>
function tableselected(){
	var table = document.getElementsByTagName("table")[0];
	var tbody = table.getElementsByTagName("tbody")[0];
	tbody.onclick = function (e) {
	    e = e || window.event;
	    var data = [];
	    var target = e.srcElement || e.target;
	    while (target && target.nodeName !== "TR") {
	        target = target.parentNode;
	    }
	    if (target) {
	        var cells = target.getElementsByTagName("td");
	        for (var i = 0; i < cells.length; i++) {
	            data.push(cells[i].innerHTML);
	            if(i==0){
	         		document.getElementById('pNm').value= cells[i].innerHTML;
	        	}
	            if(i==1){
	        		document.getElementById('pId').value = cells[i].innerHTML;
	        	}
	        	if(i==2){
	        		document.getElementById('pLngtd').value = cells[i].innerHTML;
	        	}
	        	if(i==3){
	        		document.getElementById('pLttd').value = cells[i].innerHTML;
	        	}
	        	if(i==4){
	        		document.getElementById('pTrp').value = cells[i].innerHTML;
	        	}
	        	if(i==5){
	        		document.getElementById('pTm').value = cells[i].innerHTML;
	        	}
	        }
	    }
	};
}

</script>
</html>
</html>