<?php
error_reporting(0);
ob_start();
if(file_exists('../config.php')){
	header('Location: ../index.php');
	exit;
}
?>
<!DOCTYPE html>
<html>
<head>
	<meta charset="utf-8">
	<meta name="viewport" content="width=device-width, initial-scale=1">
	<title>Install Wizard</title>
	<link rel="stylesheet" href="bootstrap.css">
	<style type="text/css">
		body{
			background: #e9eaed;
		}
		.panel{
			width:70%;
			margin:50px auto;
		}
	</style>
</head>
<body>
	<div class="panel panel-default">
		<div class="panel-heading">
			Please fill the information bellow
		</div>
		<div class="panel-body	text-center">
			<?php
					if(isset($_POST['dbhost'],$_POST['dbname'],$_POST['dbuser'],$_POST['dbpass'],$_POST['dbprefix'])){
						$connect = mysqli_connect($_POST['dbhost'], $_POST['dbuser'], $_POST['dbpass']);
						if($connect){
							$selectDB = mysqli_select_db($connect,$_POST['dbname']);
							if($selectDB){
									$tmp_config = file_get_contents('config.tmp.php');
									$replaceConfig = str_replace(
										array(':host:',
											':db:',
											':user:',
											':pass:',
											':prefix:'),
										array($_POST['dbhost'],
											$_POST['dbname'],
											$_POST['dbuser'],
											$_POST['dbpass'],
											rtrim($_POST['dbprefix'],'_').'_'), $tmp_config);
									$createConfig = file_put_contents('../config.php', $replaceConfig);
									if(!$createConfig){
										echo '<div class="alert alert-danger">Can\'t Create Config File</div>';
									}else{
										@unlink('config.tmp.php');
										$lines = file('dump.sql');
										foreach ($lines as $line)
										{
											if (substr($line, 0, 2) == '--' || $line == ''){
											    continue;
											}
											$templine .= $line;
											if (substr(trim($line), -1, 1) == ';')
											{
											    mysqli_query($connect,str_replace('sn_', rtrim($_POST['dbprefix'],'_').'_', $templine)) or print('<div class="alert alert-danger">Error performing query \'<strong>' . $templine . '\': ' . mysqli_error($connect) . '</div>');
											    $templine = '';
											}
										}
										header('Location: info.php');
									}

							}else{
								echo '<div class="alert alert-danger">Can\'t Connect To Database ('.$_POST['dbname'].')</div>';
							}
						}else{
							echo '<div class="alert alert-danger">Can\'t Connect The Server</div>';
						}
					}
					?>
				<form action="" method="post">
					<input value="<?php if(isset($_POST['dbhost'])){ echo $_POST['dbhost'];}?>" required type="text" class="form-control" name="dbhost" placeholder="Host Name"><br>
					<input value="<?php if(isset($_POST['dbname'])){ echo $_POST['dbname'];}?>" required type="text" class="form-control" name="dbname" placeholder="Database Name"><br>
					<input value="<?php if(isset($_POST['dbuser'])){ echo $_POST['dbuser'];}?>" required type="text" class="form-control" name="dbuser" placeholder="Database Username"><br>
					<input value="<?php if(isset($_POST['dbpass'])){ echo $_POST['dbpass'];}?>" type="text" class="form-control" name="dbpass" placeholder="Database Password"><br>
					<input value="<?php if(isset($_POST['dbprefix'])){ echo $_POST['dbprefix'];}?>" required type="text" class="form-control" name="dbprefix" placeholder="Prefix ex: sn_"><br>
					<input type="submit" value="Next Step" class="btn btn-success form-control">
				</form>
		</div>
	</div>
</body>
</html>