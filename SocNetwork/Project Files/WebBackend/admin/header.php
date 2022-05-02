<?php
include 'init.php';
if(!isset($_SESSION['admin'])){
	header('Location: login.php');
}
?><!DOCTYPE html>
<html>
<head>
	<meta charset="utf-8">
      <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no"/>
	<title>Welcome - CPanel</title>
	<link type="text/css" rel="stylesheet" href="libs/css/materialize.min.css"  media="screen,projection"/>
      <link type="text/css" rel="stylesheet" href="libs/css/style.css"  media="screen,projection"/>
	
</head>
<body>
<!-- Dropdown Structure -->
<ul id="dropdown1" class="dropdown-content">
  <li><a href="logout.php">Logout</a></li>
</ul>
<div class="navbar-fixed">
    <nav>
      <div class="nav-wrapper">
        <a href="#!" class="brand-logo">SocNetwork</a>
        <ul class="right hide-on-med-and-down">
          <li>
    		<a href=".">Home</a>
    	</li>
    	
      	<li>
      		<a href="users.php">Users</a>
      	</li>
      	<li>
      		<a href="messages.php">Messages</a>
      	</li>
      	<li>
      		<a href="reports.php">Reports</a>
      	</li>
        <li>
          <a href="settings.php">Settings</a>
        </li>
        <li>
          <a class="dropdown-button" href="#!" data-activates="dropdown1"><?php echo $_SESSION['adminUsername'] ?><i class="mdi-navigation-arrow-drop-down right"></i></a>
        </li>
        </ul>
      </div>
    </nav>
  </div>

<div class="container main">
