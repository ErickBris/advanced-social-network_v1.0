<?php
ob_start();
include '../config.php';
$connection = mysqli_connect($_config['DB_host'],$_config['DB_user'],$_config['DB_pass']);
$db = mysqli_select_db($connection,$_config['DB_name']);
?>
<!DOCTYPE html>
<html>
<head>
	<meta charset="utf-8">
	<meta name="viewport" content="width=device-width, initial-scale=1">
	<title>Site Information</title>
	<link rel="stylesheet" href="bootstrap.css">
	<style type="text/css">
		body{
			background: #e9eaed;
		}
		.panel{
			width:70%;
			margin:30px auto;
		}
	</style>
</head>
<body>
	<div class="panel panel-default">
		<div class="panel-heading">
			Site information
		</div>
		<div class="panel-body">
		<?php
			if(isset($_POST['site_url'])){
				$query = 'UPDATE '.$_config['DB_prefix'].'config SET `value` = ';
				mysqli_query($connection, $query."'".addslashes(rtrim($_POST['site_url'],'/').'/')."' WHERE `name` = 'url' AND `for` = 'site'");
				$adminQuery = 'UPDATE '.$_config['DB_prefix'].'admins SET `username` = ';
				$adminQuery .= "'".$_POST['admin_username']."', `password` = ";
				$adminQuery .= "'".md5($_POST['admin_password'])."'";
				$admin = mysqli_query($connection,$adminQuery);
				if($admin){
					echo '<div class="alert alert-success">Installation Completed</div>';
					echo '<label class="label label-primary">Admin Username</label>: '.$_POST['admin_username'];
					echo '<br><label class="label label-success">Admin Password</label>: '.$_POST['admin_password'];
					echo '<hr><a href="../admin/" class="btn btn-primary form-control" target="_blank">Control Panel</a>';
					echo '</div></div></body></html>';
					exit;
				}
			}
		?>
			<form action="" method="post">
				<span class="label label-warning">Site Information</span><br><br>
					<input required type="text" class="form-control" name="site_url" placeholder="Site Url example: http://localhost/android/network/cp/"><br>
					<span class="label label-info">Admin Information</span><br><br>
					<input required type="text" class="form-control" name="admin_username" placeholder="Admin Username"><br>
					<input required type="password" class="form-control" name="admin_password" placeholder="Admin Password"><br>
					<input type="submit" value="Complete" class="form-control btn btn-success">
			</form>
		</div>
	</div>
</body>
</html>