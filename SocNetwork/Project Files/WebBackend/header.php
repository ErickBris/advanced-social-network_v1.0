<html>
    <head>
      <!--Import materialize.css-->
      <title>Home - SocialNetwork</title>
      <link type="text/css" rel="stylesheet" href="libs/css/materialize.min.css"  media="screen,projection"/>
      <link type="text/css" rel="stylesheet" href="libs/css/style.css"  media="screen,projection"/>
    </head>
    <body>
      <nav>
    <div class="nav-wrapper">
      <a href="#" class="brand-logo">SN</a>
      <ul id="nav-mobile" class="right side-nav">
        <li><a href="index.php">Home</a></li>
        <?php if($__GB->GetSession('userID') != false){
            ?>
              <li><a href="account.php">Account</a></li>
              <li><a href="logout.php">Logout</a></li>
              <?php
            }else{
              ?>
              <li><a href="login.php">Login</a></li>
              <?php
            }
        ?>
        
      </ul>
    </div>
  </nav>