<%@ page import="com.example.exercisedb.*, com.example.exercisedb.web.*" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
  <head>
    <title>ExerciseDB</title>
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
    </style>
  </head>
  <body>
    <h1>ExerciseDB</h1>
    <p>Sample application to exercise a database</p>
    <ul>
    	<li><a href="<%= ExerciseDBServlet.URL %>?action=listtables">List tables in the schema '<%= Database.SCHEMA %>'</a></li>
    	<li><a href="<%= ExerciseDBServlet.URL %>?action=ensuretables">Ensure the table '<%= Database.FULLTABLE %>' exists</a></li>
    	<li><a href="<%= ExerciseDBServlet.URL %>?action=insert">Insert 1 row into '<%= Database.FULLTABLE %>'</a></li>
    	<li><a href="<%= ExerciseDBServlet.URL %>?action=insertselect">Insert 1 row into '<%= Database.FULLTABLE %>' and then select it</a></li>
    	<li><a href="<%= ExerciseDBServlet.URL %>?action=insertselectdelete">Insert 1 row into '<%= Database.FULLTABLE %>', select it, and then delete it</a></li>
    	<li><a href="<%= ExerciseDBServlet.URL %>?action=count">Show count of rows in '<%= Database.SCHEMA %>'</a></li>
    	<li><a href="<%= ExerciseDBServlet.URL %>?action=reset">Drop and re-create '<%= Database.SCHEMA %>'</a></li>
    	<li><a href="loadrunner.jsp">ExerciseDB Load Runner</a></li>
    </ul>
  </body>
</html>
