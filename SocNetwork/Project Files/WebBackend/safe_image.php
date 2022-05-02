<?php
include 'config.php';
include 'core/classes/Database.php';
include 'core/classes/Security.php';
include 'core/classes/General.php';
include 'core/classes/ImageResize.php';
$__DB = new Database($_config);
$__DB->connect();
$__DB->select_db();
use \Eventviva\ImageResize;

$__Sec = new Security($__DB);
$__GB = new General($__DB, $__Sec);
if (isset($_GET['id'])) {
    $path = $__GB->getSafeImage($_GET['id']);
    if ($path != null) {
        $image = new ImageResize($path);
        if (isset($_GET['p'])) {
            $image->crop(200, 200);
            $image->output(IMAGETYPE_PNG, 9);//50
        } else if (isset($_GET['c'])) {
            $image->crop(500, 280);

            $image->output(IMAGETYPE_PNG, 9);//50
        } else {
            if($image->getDestWidth() > 2200){
                $image->scale(45);
            }else if($image->getDestWidth() > 1200){
                $image->scale(70);
            }else if ($image->getDestWidth() > 800) {
                $image->scale(70);
            }

            $image->output(IMAGETYPE_PNG, 9);
        }
    } else {
        ob_clean();
        header('Content-Type: image/jpg');
        echo file_get_contents('uploads/logo.png');
    }

} else {
    ob_clean();
    header('Content-Type: image/jpg');
    echo file_get_contents('uploads/logo.png');
}
?>