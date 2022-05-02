<?php
include 'init.php';
include 'header.php';
if(isset($_SESSION['userID'])){
	header('Location: index.php');
}

?>
<div class="container">
  <div class="card publish-form-container">
    <form id="loginForm" class="col s12" method="POST" action="" onsubmit="login(); return false;">
    <input type="hidden" name="_webapp" value="true">
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
<?php
include 'footer.php';
?>