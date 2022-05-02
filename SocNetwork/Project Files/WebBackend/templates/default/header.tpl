<!DOCTYPE html>
<html dir="{__LNG.dir}">
<head>
	<meta charset="utf-8">
	<meta name="viewport" content="width=device-width, initial-scale=1">
	<title>{site_title}</title>
	<meta content='{site_desc}' name='description'/>
	<meta content='{site_keywords}' name='keywords'/>
	<meta content='index,follow,all' name='robots'/>
	<meta content='index, follow, ydir, odp, imageindex' name='googlebot'/>
	<meta content='index, follow, ydir, odp, archive' name='slurp'/>
	<meta content='all' name='audience'/>
	<meta content='general' name='rating'/>
	<meta content='all' name='robots'/>
	<meta content='{site_title}' property='og:title'/>
	<meta content='{site_title}' property='og:site_name'/>
	<meta content='{site_desc}' property='og:description'/>
	<meta content='{site_logo}' property='og:image'/>
	<link href="{site_url}public/css/jquery.dataTables.css" rel="stylesheet">
	<link rel="stylesheet" href="{tpl_url}css/bootstrap.css">
	<IF NAME="{__LNG.dir} == 'rtl'">
		<link rel="stylesheet" href="{tpl_url}css/bootstrap-rtl.min.css">
		<link rel="stylesheet" href="{tpl_url}css/fonts-rtl.css">
	<ELSE>
		<link rel="stylesheet" href="{tpl_url}css/fonts.css">
	</IF>
	<link rel="stylesheet" href="{tpl_url}css/style.css">
	<link href="//cdnjs.cloudflare.com/ajax/libs/font-awesome/4.1.0/css/font-awesome.min.css" rel="stylesheet">
</head>
<body>
<nav class="navbar navbar-default navbar-fixed-top" role="navigation">
<div class="navbar-header">
<a class="logo" href="index.php"><img src="{site_logo}" style="height: 46px;" /></a>
    <button type="button" class="navbar-toggle" data-toggle="collapse" data-target="#bs-example-navbar-collapse-1">
      <span class="sr-only">Toggle navigation</span>
      <span class="icon-bar"></span>
      <span class="icon-bar"></span>
      <span class="icon-bar"></span>
    </button>
    <div class="dropdown lang-btn">
	  <button class="btn btn-default dropdown-toggle" type="button" id="dropdownMenu1" data-toggle="dropdown">
	    {__LNG.language}
	    <span class="caret"></span>
	  </button>
	  <ul class="dropdown-menu" role="menu" aria-labelledby="dropdownMenu1">
	    <li role="presentation"><a role="menuitem" tabindex="-1" href="?language=en">{__LNG.english}</a></li>
	    <li role="presentation"><a role="menuitem" tabindex="-1" href="?language=ar">{__LNG.arabic}</a></li>
	  </ul>
	</div>
</div>
  <div class="collapse navbar-collapse" id="bs-example-navbar-collapse-1">
    <ul class="nav navbar-nav">
    	<li>
    		<a href=".">{__LNG.home}</a>
    	</li>
		<li>
    		<a href="contact.php">{__LNG.contact}</a>
    	</li>
	</ul>
	<ul class="nav navbar-nav navbar-{__LNG.pull}">
	<IF NAME="{logged} neq 1">
		<li>
			<a href="register.php">{__LNG.register}</a>
		</li>
		<li>
			<a href="login.php">{__LNG.login}</a>
		</li>
	<ELSE>
		<li class="dropdown">
		  <a href="#" class="dropdown-toggle" data-toggle="dropdown"><span class="glyphicon glyphicon-user"></span> {_SESSION.username} <b class="caret"></b></a>
		  <ul class="dropdown-menu">
		    <li><a href="account.php"><span class="glyphicon glyphicon-briefcase"></span>
		     {__LNG.account}</a></li>
		    <li class="divider"></li>
		    <li><a href="logout.php"><span class="glyphicon glyphicon-log-out"></span>
		     {__LNG.logout}</a></li>
		  </ul>
		</li>
	</IF>
	</ul>
</div>
</nav>
<div class="container main">
<div class="text-center mainLogo"><img src="{site_logo}"><br><br></div>