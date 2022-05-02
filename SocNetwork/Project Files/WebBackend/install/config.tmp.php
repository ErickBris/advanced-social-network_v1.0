<?php
ob_start();
session_start();
error_reporting(0);
$_config['DB_host'] = ':host:';
$_config['DB_user'] = ':user:';
$_config['DB_pass'] = ':pass:';
$_config['DB_name'] = ':db:';
$_config['DB_prefix'] = ':prefix:';
?>