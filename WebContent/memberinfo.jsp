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
	            	alert(document.getElementById('name').innerHTML+"name");
	         		document.getElementById('name').innerHTML= cells[i].innerHTML;
	        	}
	            if(i==1){
	            	alert(document.getElementById('age').innerHTML+"age");
	        		document.getElementById('age').innerHTML = cells[i].innerHTML;
	        	}
	        	if(i==2){
	        		alert(document.getElementById('address').innerHTML+"address");
	        		document.getElementById('address').innerHTML = cells[i].innerHTML;
	        	}
	        }
	    }
	   alert(data);
	};
}

function postForm(operation){
		document.getElementById('operation').value=operation;
		document.getElementById('operation').innerHTML=operation;
	}
	
</script>
</head>
<body onload="load();">
	<h1>Bluemix: Sample Web Application </h1>
	<div class="container"style="width:1000px">
		<h2>Member Information</h2>
		<br />
		<form class="form-horizontal" action='memberinfo'>
			<div class="form-group">
				<label for="name">Name:</label> 
				<input id='name' class="form-control" type='text' name='name' placeholder="Name" />
			</div>
			<div class="form-group">
				<label for="age">Age:</label> 
				<input id='age' class="form-control" type="text" name='age' style="width:60px" placeholder="Age "/>
			</div>
			<div class="form-group">
				<label for="address">Address:</label> 
				<input id='address' class="form-control" type="text" name='address' placeholder="Value"/>
			</div>
			<input id='operation' class="form-control" type="hidden" name='operation'/>
			<div class="buttonContainer">
			<button type="submit" class="btn btn-primary" id='read'   onclick="postForm('Create')">Create</button>
			<button type="submit" class="btn btn-primary" id='update' onclick="postForm('Update')">Update</button>
			<button type="submit" class="btn btn-primary" id='delete' onclick="postForm('Delete')">Delete</button>
		</div>	
		</form>
		<div id='echo' class="messageInfo"></div>
		
		<br />
		<h2>Member List</h2>
		<table id="the_list" class='table-striped clearfix'style="width:900px" onclick="tableselected()">
			<thead>
				<th style="width:100px" fixed><h5>Name</h5></th>
				<th style="width:30px"><h5>Age</h5></th>
				<th style="width:200px"><h5>Address</h5>
			</th>
			</thead>
			<tfoot>
			</tfoot>
			<tbody id="res">
			<%
				DBCollection col = (DBCollection) request.getAttribute("memberCollection");
				if(col!=null){
					DBCursor cursor = (DBCursor)col.find();
					BasicDBObject obj=null;
					while (cursor.hasNext()) {
						obj = (BasicDBObject) cursor.next();
						out.println("<tr>");
						out.println("<td>");
						out.println(obj.get("name"));
						out.println("</td>");
						out.println("<td>");
						out.println(obj.get("age"));
						out.println("</td>");
						out.println("<td>");
						out.println(obj.get("address"));
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
	         		document.getElementById('name').value= cells[i].innerHTML;
	        	}
	            if(i==1){
	        		document.getElementById('age').value = cells[i].innerHTML;
	        	}
	        	if(i==2){
	        		document.getElementById('address').value = cells[i].innerHTML;
	        	}
	        }
	    }
	};
}

</script>
</html>
</html>