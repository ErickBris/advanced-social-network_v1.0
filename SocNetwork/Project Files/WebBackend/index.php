<?php 
include 'init.php';
include 'header.php';
if(isset($_GET['activate'])){
    $hash = $__DB->escape_string($_GET['activate']);
    $query  = $__DB->select('activation', '`userid`',"`hash` = '{$hash}'");
    if($__DB->num_rows($query)){
      $fetch = $__DB->fetch_assoc($query);
      $update = $__DB->update('users','`active` = 1', "`id` = ".$fetch['userid']);
      if($update){
        echo '<div class="container"><div class="card"><blockquote>
     Your account has been activated successfully
    </blockquote></div></div>';
      }
    }else{
      echo '<div class="container"><div class="card"><blockquote>
      Invalid Activation Key
    </blockquote></div></div>';
    }
include 'footer.php';
exit();
}
if(!isset($_SESSION['userID'])){
  header('Location: login.php');
}
$userID = $_SESSION['userID'];
$querysql = "SELECT F.to,P.*,

            COUNT(L.to) AS likes,
            U.name AS ownerName,
            U.username AS ownerUsername,
            U.picture AS ownerPicture
            FROM pex_posts P

            LEFT JOIN pex_follows AS F
            ON F.from = {$userID}

            LEFT JOIN pex_users AS U
            ON U.id = P.ownerID

            LEFT JOIN pex_likes AS L
            ON L.to = P.id

            WHERE (P.ownerID = {$userID} OR P.ownerID = F.to)
            GROUP BY P.id ORDER BY P.id
          ";

$query =  $__DB->query($querysql);
$postsTotalCount = $__DB->num_rows($query);
$__PAG = new Pagination(1,
            $postsTotalCount
            ,10,
            'api.php?page=#i#');

?>











<div class="container">

  <div class="card publish-form-container">
    <form class="col s12" method="POST" enctype="multipart/form-data" action="">
      <div class="center">
        <button class="btn-large waves-effect waves-light" onclick="getFile();
            return false;">Choose Image 
              <i class="mdi-action-backup right"></i>
          </button>
        <input type="file" name="image" id="imageFile" style='height: 0px;width:0px; overflow:hidden;'>
      </div>
      <div class="input-field">
          <input id="status-field" type="text" name="status">
          <label for="status-field">Say Something...</label>
      </div>
      <div>
          <label>Privacy</label><br>
          <input name="public" type="radio" id="public" />
          <label for="public">Public</label>
          <input name="private" type="radio" id="private" />
          <label for="private">Private</label>
          <button class="btn waves-effect waves-light pull-right" type="submit">Submit
              <i class="mdi-content-send right"></i>
          </button>
      </div>
  </form>
  </div><!-- Post Card //-->
  <div id="postsContainer">
    
  </div>
  <div id="#loadingSpinner" class="center-align">
      <div  class="preloader-wrapper small active">
        <div class="spinner-layer spinner-green-only">
            <div class="circle-clipper left">
              <div class="circle"></div>
            </div>
            <div class="gap-patch">
              <div class="circle"></div>
            </div>
            <div class="circle-clipper right">
              <div class="circle"></div>
            </div>
        </div>
      </div>
  </div>
  
</div><!-- container//-->
<?php
include 'footer.php';
?>