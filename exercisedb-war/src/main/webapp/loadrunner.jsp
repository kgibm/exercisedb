<%@ page import="com.example.exercisedb.*, com.example.exercisedb.web.*" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
  <head>
    <title>ExerciseDB Load Runner</title>
    <meta charset="UTF-8">
    <meta name="theme-color" content="#ffffff">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta name="description" content="ExerciseDB">
    <style type="text/css">
      body {
        margin: 40px auto;
        max-width: 650px;
        line-height: 1.6;
        font-size: 18px;
        color: #444;
        padding:0 10px;
      }
      
      h1, h2, h3 {
        line-height:1.2;
      }
      
      .fullwidth {
      	width: 100%;
      }
      
      input[type=submit] {
       	padding: 10px;
      }
      
      .notification {
      	font-weight: bold;
      	color: red;
      	padding-left: 18px;
      	padding-right: 18px;
      	border: 1px solid black;
      }
      </style>
  </head>
  <body>
    <h1>ExerciseDB Load Runner</h1>
    <%
    if (request.getParameter("started") != null) {
    %>
    	<div class="notification">
    		<p>
    		The Load Runner has been started at <%= request.getParameter("started") %>. Check the messages.log for a message of the following form for results:
    		</p>
    		<p style="margin-left: 20px;">
    		Load Runner finished in A ms; requests: B, average execution: C ms, max execution: D ms
    		</p>
    		<p>Refreshing this page does not start a new load runner; instead, execute the form again.</p>
    	</div>
    <%
    }
     %>
    <p>Execute concurrent requests</p>
    <form action="<%= LoadRunnerServlet.URL %>" method="get">
        <p>
        	<label for="url">URL with context root (e.g. https://localhost:9443/exercisedb):</label><br />
    		<input type="text" name="url" placeholder="URL with context root" class="fullwidth" value="<%= request.getParameter("url") == null ? "" : request.getParameter("url") %>" required />
    	</p>
        <p>
        	<label for="user">User:</label><br />
    		<input type="text" name="user" placeholder="User" value="<%= request.getParameter("user") == null ? "" : request.getParameter("user") %>" />
    	</p>
        <p>
        	<label for="password">Password:</label><br />
    		<input type="password" name="password" placeholder="Password" value="<%= request.getParameter("password") == null ? "" : request.getParameter("password") %>" />
    	</p>
        <p>
        	<label for="activity">Activity:</label><br />
        	<select name="activity">
				<option value="insert" <%= "insert".equals(request.getParameter("activity")) ? "selected" : "" %>>Insert</option>
				<option value="insertselect" <%= "insertselect".equals(request.getParameter("activity")) ? "selected" : "" %>>Insert and Select</option>
				<option value="insertselectdelete" <%= (request.getParameter("activity") == null || "insertselectdelete".equals(request.getParameter("activity"))) ? "selected" : "" %>>Insert, Select, and Delete</option>
			</select>
		</p>
        <p>
        	<label for="concurrentusers">Concurrent users:</label><br />
    		<input type="number" name="concurrentusers" placeholder="Concurrent users" value="<%= request.getParameter("concurrentusers") == null ? "5" : request.getParameter("concurrentusers") %>" min="1" />
    	</p>
        <p>
        	<label for="totalrequests">Total requests:</label><br />
    		<input type="number" name="totalrequests" placeholder="Total requests" value="<%= request.getParameter("totalrequests") == null ? "100" : request.getParameter("totalrequests") %>" min="1" />
    	</p>
    	<input type="submit" value="Start" />
    </form>
  </body>
</html>
