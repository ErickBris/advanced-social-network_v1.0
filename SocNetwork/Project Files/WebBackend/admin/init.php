<?
include '../config.php';
include '../core/classes/Database.php';
include '../core/classes/Security.php';
include '../core/classes/General.php';
include '../core/classes/Posts.php';
include '../core/classes/Users.php';
include '../core/classes/Pagination.php';
$__DB = new Database($_config);
$__DB->connect();
$__DB->select_db();
$__Sec 	= new Security($__DB);
$__GB 	= new General($__DB,$__Sec);
$__PO	= new Posts($__GB);
$__USERS	= new Users($__GB);
$tr = '<tr>';
$trc = '</tr>';
$td = '<td style="vertical-align: middle;">';
$tdc = '</td>';
?>