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
      
      input[type=text] {
      	width: 100%;
      }
      
      input[type=submit] {
       	padding: 10px;
      }
    </style>
  </head>
  <body>
    <h1>ExerciseDB Load Runner</h1>
    <p>Execute concurrent requests</p>
    <form action="" method="get">
        <p>
        	<label for="url">URL:</label><br />
    		<input type="text" name="url" placeholder="URL" required />
    	</p>
        <p>
        	<label for="activity">Activity:</label><br />
        	<select name="activity">
				<option value="insert">Insert</option>
				<option value="insertselect">Insert and Select</option>
				<option value="insertselectdelete" selected>Insert, Select, and Delete</option>
			</select>
		</p>
        <p>
        	<label for="concurrentusers">Concurrent users:</label><br />
    		<input type="number" name="concurrentusers" placeholder="Concurrent users" value="5" min="1" />
    	</p>
        <p>
        	<label for="totalrequests">Total requests:</label><br />
    		<input type="number" name="totalrequests" placeholder="Total requests" value="100" min="1" />
    	</p>
    	<input type="submit" value="Start" />
    </form>
  </body>
</html>
