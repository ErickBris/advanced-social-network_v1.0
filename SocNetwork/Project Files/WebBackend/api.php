<?php
include 'config.php';
include 'core/classes/Database.php';
include 'core/classes/Security.php';
include 'core/classes/General.php';
include 'core/classes/Posts.php';
include 'core/classes/Users.php';
include 'core/classes/Pagination.php';
$__DB = new Database($_config);
$__DB->connect();
$__DB->select_db();
$__Sec = new Security($__DB);
$__GB = new General($__DB, $__Sec);
$__PO = new Posts($__GB);
$__USERS = new Users($__GB);
if (isset($_GET['cmd'])) {
    if (isset($_SERVER['HTTP_TOKEN'])) {
        $userID = $__USERS->getUserID($_SERVER['HTTP_TOKEN']);
    } else if (isset($_SESSION['userID'])) {
        $userID = $_SESSION['userID'];
    } else {
        $userID = 0;
    }
    switch ($_GET['cmd']) {
        case 'map':

            if ($userID != 0 && isset($_POST['place_name'])) {
                $__GB->getMapImage($_POST['place_name']);
            }
            break;
        case 'getLink':
            $__GB->getLink($_GET['hash']);
            break;
        case 'place':
            if ($userID != 0) {
                $__GB->getaddress($_GET['lng'], $_GET['lat']);
            }
            break;
        case 'updateRegID':
            if ($userID != 0) {
                $__USERS->updateRegID($userID, $_POST['regID']);
            }
            break;
        case 'addMessage':
            if (isset($_POST['message']) && $userID != 0) {
                $__USERS->addMessage($userID, $_POST);
            }
            break;
        case 'getConversation':
            if ($userID != 0 && isset($_GET['id'])) {
                $__USERS->getConversation($userID, $_GET['id'], $_GET['recipientID']);
            }
            break;
        case 'conversations':
            if ($userID != 0) {
                $__USERS->getConversations($userID);
            }
            break;
        case 'getProfile':
            if ($userID != 0) {
                $__USERS->getProfile($userID);
            }
            break;
        case 'comment':
            if ($userID != 0) {
                if (isset($_POST['comment'])) {
                    $__PO->addComment($_POST['comment'], $_GET['id'], $userID);
                }
            }
            break;
        case 'comments':
            if ($userID != 0) {
                if (isset($_GET['id'])) {
                    $__PO->getPostComments($_GET['id']);
                }
            }
            break;
        case 'post':
            if ($userID != 0) {
                $__PO->getPost($_GET['id'], $userID);
            }
            break;
        case 'getFollowing':
            if ($userID != 0) {
                $__USERS->getFollowing($userID);
            }
            break;
        case 'getFollowers':
            if ($userID != 0) {
                $__USERS->getFollowers($userID);
            }
            break;
        case 'simpleUserInfo':
            if (isset($_GET['id'])) {
                if ($_GET['id'] != 0) {
                    $user = $__USERS->getUser($_GET['id']);
                } else {
                    $user = $__USERS->getUser($userID);
                }
                $__GB->prepareMessage($user);

            }
            break;
        case 'followToggle':
            if ($userID != 0) {
                $__USERS->FollowToggle($userID, $_GET['id']);
            }
            break;
        case 'unlike':
            if ($userID != 0) {
                $__PO->unlikePost($userID, $_GET['id']);
            }
            break;
        case 'liked':
            if ($userID != 0) {

                $querysql = "SELECT P.*,

						COUNT(L.to) AS likes,
						U.name AS ownerName,
						U.username AS ownerUsername,
						U.picture AS ownerPicture
						FROM " . $_config['DB_prefix'] . "posts P

						LEFT JOIN " . $_config['DB_prefix'] . "users AS U
						ON U.id = P.ownerID

						LEFT JOIN " . $_config['DB_prefix'] . "likes AS L
						ON L.from = {$userID}

						WHERE P.id = L.to
						GROUP BY P.id ORDER BY P.id DESC
					";

                $query = $__DB->query($querysql);
                $rows = $__DB->num_rows($query);
                $page = (isset($_GET['page']) && !empty($_GET['page'])) ? $__Sec->MA_INT($_GET['page']) : 1;
                $__PAG = new Pagination($page,
                    $rows
                    , 10,
                    'api.php?page=#i#');
                if ($page > $__PAG->pages) {
                    $__GB->prepareMessage(array());
                } else {
                    $__PO->getLikedPosts($userID, $__PAG->limit);
                }

            }
            break;
        case 'like':
            if ($userID != 0) {
                if (isset($_GET['id'])) {
                    $__PO->likePost($userID, $_GET['id']);
                }
            }
            break;
        case 'publish':
            if ($userID != 0) {
                if (isset($_FILES['image'])) {
                    $imageID = $__GB->uploadImage($_FILES['image']);
                } else {
                    $imageID = null;
                }
                $__PO->publishStatus($_POST, $userID, $imageID);
            }
            break;
        case 'posts':
            if ($userID != 0) {
                if (isset($_GET['userid'])) {

                    $reqID = (int)$_GET['userid'];
                    if ($reqID != $userID) {
                        if ($__GB->isFollowing($userID, $reqID)) {
                            $querysql = "SELECT P.*,
                            COUNT(L.to) AS likes,
                            U.name AS ownerName,
                            U.username AS ownerUsername,
                            U.picture AS ownerPicture
                            FROM " . $_config['DB_prefix'] . "posts P

                            LEFT JOIN " . $_config['DB_prefix'] . "users AS U
                            ON U.id = P.ownerID

                            LEFT JOIN " . $_config['DB_prefix'] . "likes AS L
                            ON L.to = P.id

                            WHERE (P.ownerID = {$reqID})
                            GROUP BY P.id ORDER BY P.id DESC
                        ";
                        } else {
                            $querysql = "SELECT P.*,
                            COUNT(L.to) AS likes,
                            U.name AS ownerName,
                            U.username AS ownerUsername,
                            U.picture AS ownerPicture
                            FROM " . $_config['DB_prefix'] . "posts P

                            LEFT JOIN " . $_config['DB_prefix'] . "users AS U
                            ON U.id = P.ownerID

                            LEFT JOIN " . $_config['DB_prefix'] . "likes AS L
                            ON L.to = P.id

                            WHERE P.ownerID = {$reqID} AND P.privacy = 1
                            GROUP BY P.id ORDER BY P.id DESC
                        ";
                        }

                    } else {

                        $querysql = "SELECT P.*,

						COUNT(L.to) AS likes,
						U.name AS ownerName,
						U.username AS ownerUsername,
						U.picture AS ownerPicture
						FROM " . $_config['DB_prefix'] . "posts P

						LEFT JOIN " . $_config['DB_prefix'] . "users AS U
						ON U.id = P.ownerID

						LEFT JOIN " . $_config['DB_prefix'] . "likes AS L
						ON L.to = P.id

						WHERE (P.ownerID = {$userID})
						GROUP BY P.id ORDER BY P.id DESC
					";
                    }

                    $query = $__DB->query($querysql);
                    $rows = $__DB->num_rows($query);
                    $page = (isset($_GET['page']) && !empty($_GET['page'])) ? $__Sec->MA_INT($_GET['page']) : 1;
                    $__PAG = new Pagination($page,
                        $rows
                        , 10,
                        'api.php?page=#i#');
                    if ($page > $__PAG->pages) {
                        $__GB->prepareMessage(array());
                    } else {
                        $__PO->getUPosts($userID, $__PAG->limit, $querysql);
                    }
                } else if(isset($_GET['hashtag'])){
                    $hashtag = $__DB->escape_string($_GET['hashtag']);
                    $querysql = "SELECT F.to,P.*,

						COUNT(L.to) AS likes,
						U.name AS ownerName,
						U.username AS ownerUsername,
						U.picture AS ownerPicture
						FROM " . $_config['DB_prefix'] . "posts P

						LEFT JOIN " . $_config['DB_prefix'] . "follows AS F
						ON F.from = {$userID}

						LEFT JOIN " . $_config['DB_prefix'] . "users AS U
						ON U.id = P.ownerID

						LEFT JOIN " . $_config['DB_prefix'] . "likes AS L
						ON L.to = P.id

						WHERE P.status LIKE '%#$hashtag%'
						GROUP BY P.id ORDER BY P.id DESC
					";

                }else {

                    $querysql = "SELECT F.to,P.*,

						COUNT(L.to) AS likes,
						U.name AS ownerName,
						U.username AS ownerUsername,
						U.picture AS ownerPicture
						FROM " . $_config['DB_prefix'] . "posts P

						LEFT JOIN " . $_config['DB_prefix'] . "follows AS F
						ON F.from = {$userID}

						LEFT JOIN " . $_config['DB_prefix'] . "users AS U
						ON U.id = P.ownerID

						LEFT JOIN " . $_config['DB_prefix'] . "likes AS L
						ON L.to = P.id

						WHERE (P.ownerID = {$userID} OR P.ownerID = F.to)
						GROUP BY P.id ORDER BY P.id DESC
					";
                }
                    $query = $__DB->query($querysql);
                    $rows = $__DB->num_rows($query);
                    $page = (isset($_GET['page']) && !empty($_GET['page'])) ? $__Sec->MA_INT($_GET['page']) : 1;
                    $__PAG = new Pagination($page,
                        $rows
                        , 10,
                        'api.php?page=#i#');
                    if ($page > $__PAG->pages) {
                        $__GB->prepareMessage(array());
                    } else {
                        $__PO->getPosts($userID, $querysql,$__PAG->limit);
                    }

            }
            break;
        case 'users':
            if ($userID != 0) {
                $user = $__USERS->getUserDetails($_GET['id'], $userID);
                $__GB->prepareMessage($user);

            }
            break;
        case 'login':
            if (isset($_POST['username'], $_POST['password'])) {
                if ($_POST['_webapp']) {
                    $__USERS->userLogin($_POST['username'], $_POST['password'], true);
                } else {
                    $__USERS->userLogin($_POST['username'], $_POST['password']);
                }
            } else {
                $array = array(
                    'success' => false,
                    'userID' => null,
                    'token' => null
                );
                $__GB->prepareMessage($array);
            }
            break;
        case 'register':
            if (isset($_POST['username'], $_POST['password'], $_POST['email'])) {
                $__USERS->userRegister($_POST);
            } else {
                $response = array(
                    'done' => false,
                    'message' => 'Some Params are Missing'
                );
                $__GB->prepareMessage($response);
            }
            break;
        case 'updateProfile':
            if ($userID != 0) {
                if (isset($_FILES['image'])) {
                    $imageID = $__GB->uploadImage($_FILES['image']);
                } else {
                    $imageID = null;
                }
                if (isset($_FILES['cover'])) {
                    $coverID = $__GB->uploadImage($_FILES['cover']);
                } else {
                    $coverID = null;
                }
                $__USERS->updateProfile($_POST, $imageID, $coverID, $userID);
            }
            break;
        case 'disclaimer':
            $dis = $__GB->getConfig('disclaimer', 'site');
            $__GB->prepareMessage(array('done' => true, 'message' => $dis));
            break;
        case 'searchFriend':
            if (isset($_POST['value']) && $userID != 0) {
                $__USERS->searchFriend($_POST['value'], $userID);
            }
            break;
        case 'updatePost':
            if ($userID != 0) {
                if (isset($_GET['id'], $_POST['status'])) {
                    $__PO->updatePost($userID, $_GET['id'], $_POST['status']);
                } else {
                    $response = array(
                        'done' => false,
                        'message' => 'Some Params are Missing'
                    );
                    $__GB->prepareMessage($response);
                }
            }
            break;
        case 'deletePost':
            if ($userID != 0) {
                if (isset($_GET['id'])) {
                    $__PO->deletePost($userID, $_GET['id']);
                } else {
                    $response = array(
                        'done' => false,
                        'message' => 'Some Params are Missing'
                    );
                    $__GB->prepareMessage($response);
                }
            }
            break;
        case 'reportPost':
            if ($userID != 0) {
                if (isset($_GET['id'])) {
                    $reason = (isset($_POST['reason'])) ? $_POST['reason'] : null;
                    $__PO->reportPost($userID, $_GET['id'], $reason);
                } else {
                    $response = array(
                        'done' => false,
                        'message' => 'Some Params are Missing'
                    );
                    $__GB->prepareMessage($response);
                }
            }
            break;
        case 'getComments':
            if ($userID != 0) {
                $__PO->getPostComments($_GET['id']);
            }
            break;
        case 'deleteComment':
            if ($userID != 0) {
                $__PO->deleteComment($userID, $_GET['id']);
            }
            break;

    }

} else {
    $response = array(
        'done' => false,
        'message' => 'Some Params are Missing'
    );
    $__GB->prepareMessage($response);
}
?>