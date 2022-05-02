<?php
include 'init.php';
if(isset($_SESSION['admin'])){
    header('Location: index.php');
}
?>
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no"/>
    <title>Login - CPanel</title>
    <link type="text/css" rel="stylesheet" href="libs/css/materialize.min.css"  media="screen,projection"/>
    <link type="text/css" rel="stylesheet" href="libs/css/style.css"  media="screen,projection"/>

</head>
<body>
<div class="container">
    <div class="card publish-form-container">
        <?php
        if(isset($_POST['username'],$_POST['password'])){
            $__USERS->adminLogin($_POST);
        }
        ?>
        <form  class="col s12" method="POST" action="" >

            <div class="input-field">
                <input id="username-field" type="text" name="username">
                <label for="username-field">Username...</label>
            </div>
            <div class="input-field">
                <input id="password-field" type="password" name="password">
                <label for="password-field">Password...</label>
            </div>
            <div>
                <button class="btn waves-effect waves-light pull-right" type="submit">Login
                    <i class="mdi-content-send right"></i>
                </button>
            </div>
        </form>
    </div><!-- Post Card //-->
</div>

<script type="text/javascript" src="https://code.jquery.com/jquery-2.1.1.min.js"></script>
<script type="text/javascript" src="libs/js/materialize.min.js"></script>
<script type="text/javascript">
    $(document).ready(function(){
        $('.materialboxed').materialbox();
    });
</script>
</body>
</html>