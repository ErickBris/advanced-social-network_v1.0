<?php

/**
 *
 */
class Users
{
    public $google_api_key = 'AIzaSyD2XyAIwej5UeOG06c4jEPdgU_QgALeotM';
    public $google_api_url = 'https://android.googleapis.com/gcm/send';
    private $__GB;

    function __construct($__GB)
    {
        $this->__GB = $__GB;
    }

    public function updateRegID($userID, $regID)
    {
        $regID = $this->__GB->__DB->escape_string($regID);
        $update = $this->__GB->__DB->update('users', "`reg_id` = '{$regID}'", "`id` = '$userID'");
        if ($update) {
            $this->__GB->prepareMessage(array('done' => true, 'message' => 'updated successfully'));
        } else {
            $this->__GB->prepareMessage(array('done' => false, 'message' => 'something went wrong'));
        }
    }

    public function searchFriend($name, $userID)
    {
        $name = $this->__GB->__DB->escape_string(trim($name));
        if ($name != 'suggestions') {
            $query = $this->__GB->__DB->select('users', '*', "`username` LIKE '%" . $name . "%' OR `email` LIKE '%" . $name . "%'");
        } else {
            $query = $this->__GB->__DB->select('users', '*', '', "RAND()", 6);
        }
        if ($this->__GB->__DB->num_rows($query) != 0) {
            $result = array();
            while ($fetch = $this->__GB->__DB->fetch_assoc($query)) {
                if ($fetch['id'] == $userID) {
                    continue;
                }
                unset($fetch['password'], $fetch['email'], $fetch['reg_id'], $fetch['cover'], $fetch['last_seen'], $fetch['date']);
                $result[] = $fetch;
            }
            $this->__GB->prepareMessage($result);
        } else {
            $this->__GB->prepareMessage(array());
        }
    }

    public function addMessage($userID, $array)
    {
        foreach ($array as $key => $value) {
            $array[$key] = $this->__GB->__DB->escape_string(trim($value));
        }
        if ($array['conversationID'] == 0) {
            $array['conversationID'] = $this->getConversationID($userID, $array['recipientID']);
        }
        $messageInfo = array(
            'message' => $array['message'],
            'from' => $userID,
            'to' => $array['recipientID'],
            'conversationID' => $array['conversationID'],
            'date' => time()
        );
        $insert = $this->__GB->__DB->insert('messages', $messageInfo);
        $messageID = $this->__GB->__DB->lastID();
        if ($insert) {
            $update = $this->__GB->__DB->update('conversations', '`date` = ' . time(), "`id` = " . $messageInfo['conversationID']);
            if ($update) {
                $userQuery = $this->__GB->__DB->select('users', '*', "`id` = {$userID}");
                $userFetch = $this->__GB->__DB->fetch_assoc($userQuery);
                $messageResponse = array(
                    'id' => $messageID,
                    'conversationID' => $messageInfo['conversationID'],
                    'ownerID' => $messageInfo['from'],
                    'ownerPicture' => $userFetch['picture'],
                    'ownerName' => $userFetch['name'],
                    'ownerUsername' => $userFetch['username'],
                    'date' => $this->__GB->TimeAgo($messageInfo['date']),
                    'message' => $messageInfo['message']
                );
                $notificationData = $messageResponse;
                $notificationData['recipientID'] = $messageInfo['to'];
                $notificationData['for'] = 'chat';
                $getUser = $this->__GB->__DB->select('users', '`reg_id`', '`id`=' . $messageInfo['to']);
                $fetchUser = $this->__GB->__DB->fetch_assoc($getUser);
                if (!empty($fetchUser['reg_id'])) {
                    $regIDs = array($fetchUser['reg_id']);
                    $this->sendMessageThroughGCM($regIDs, $notificationData);
                }

                $this->__GB->prepareMessage($messageResponse);
            } else {
                $this->__GB->prepareMessage(array());
            }
        } else {
            $this->__GB->prepareMessage(array());
        }

    }

    public function getConversationID($userID, $recipientID)
    {
        $query = $this->__GB->__DB->select('conversations', '`id`',
            "`from` = {$userID} AND `to` = {$recipientID} OR `from` = {$recipientID} AND `to` = {$userID}");
        if ($this->__GB->__DB->num_rows($query)) {
            $fetch = $this->__GB->__DB->fetch_assoc($query);
            $conversationID = $fetch['id'];
        } else {
            $this->__GB->__DB->free_result($query);
            $data = array('from' => $userID, 'to' => $recipientID, 'date' => time());
            $insert = $this->__GB->__DB->insert('conversations', $data);
            if ($insert) {
                $conversationID = $this->__GB->__DB->lastID();
            }
        }
        return $conversationID;
    }

    public function sendMessageThroughGCM($IDs, $data)
    {
        $fields = array(
            'registration_ids' => $IDs,
            'data' => $data,

        );
        $headers = array(
            'Authorization: key=' . $this->__GB->GetConfig('googleApiConfig', 'site'),
            'Content-Type: application/json'
        );
        $ch = curl_init();
        curl_setopt($ch, CURLOPT_URL, $this->google_api_url);
        curl_setopt($ch, CURLOPT_POST, true);
        curl_setopt($ch, CURLOPT_HTTPHEADER, $headers);
        curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
        curl_setopt($ch, CURLOPT_SSL_VERIFYHOST, 0);
        curl_setopt($ch, CURLOPT_SSL_VERIFYPEER, false);
        curl_setopt($ch, CURLOPT_POSTFIELDS, json_encode($fields));
        $result = curl_exec($ch);
        if ($result === FALSE) {
            return null;
        }
        curl_close($ch);
        return $result;
    }

    public function addMessageOld($userID, $array)
    {
        foreach ($array as $key => $value) {
            $array[$key] = $this->__GB->__DB->escape_string(trim($value));
        }
        /*
        * // from = userid
        * // to = arra
*/
        if ($array['conversationID'] == 0) {
            //$sql = "`from` = {$recpientID} AND `to` = {$userID} OR `to` = {$recpientID} AND `from` = {$userID}";
            $sql = "`from` = " . $array['to'] . " AND `to` = {$userID} OR `to` = " . $array['to'] . " AND `from` = {$userID}";
            $query = $this->__GB->__DB->select('conversations', '*', $sql);
            if ($this->__GB->__DB->num_rows($query) != 0) {
                $fetch = $this->__GB->__DB->fetch_assoc($query);
                $array['conversationID'] = $fetch['id'];
            } else {
                $data = array('from' => $userID, 'to' => $array['to'], 'date' => time());
                $insert = $this->__GB->__DB->insert('conversations', $data);
                if ($insert) {
                    $array['conversationID'] = $this->__GB->__DB->lastID();
                }
            }
        }
        $array['from'] = $userID;
        $array['date'] = time();
        $insert = $this->__GB->__DB->insert('messages', $array);
        if ($insert) {
            $getUser = $this->__GB->__DB->select('users', '`reg_id`', '`id`=' . $array['to']);
            $fetchUser = $this->__GB->__DB->fetch_assoc($getUser);
            if ($fetchUser['reg_id'] != null) {
                $regIDs = array($fetchUser['reg_id']);
                $msg = array(
                    'message' => $array['message'],
                    'conversationID' => $array['conversationID'],
                    'recpientID' => $array['from'],
                    'username' => $this->getUserNameByID($array['from'])
                );
                $this->sendMessageThroughGCM($regIDs, $msg);
            }
            $this->__GB->prepareMessage('done');
        } else {
            $this->__GB->prepareMessage('error');
        }
    }

    public function getUserNameByID($id)
    {
        $query = $this->__GB->__DB->select('users', '`username`', "`id` = {$id}");
        $fetch = $this->__GB->__DB->fetch_assoc($query);
        return $fetch['username'];
    }

    public function getConversation($userID, $conversationID, $recipientID)
    {
        $conversationID = (int)$conversationID;
        if ($conversationID == 0) {
            $conversationID = $this->getConversationID($userID, $recipientID);
        }
        $this->__GB->__DB->update('messages', '`unseen` = 0', "`conversationID` = {$conversationID}");
        $sql = "SELECT M.*,
				u.id AS ownerID,
				u.picture AS ownerPicture,
				u.name AS ownerName,
				u.username AS ownerUsername
				FROM " . $this->__GB->__DB->DB_prefix . "messages as M
				LEFT JOIN " . $this->__GB->__DB->DB_prefix . "users as u
					ON
					u.id = M.from
				WHERE 
					M.conversationID = {$conversationID}
				GROUP BY M.id ORDER BY M.date ASC";
        $query = $this->__GB->__DB->query($sql);
        $messages = array();
        if ($this->__GB->__DB->num_rows($query) != 0) {
            while ($fetch = $this->__GB->__DB->fetch_assoc($query)) {
                unset($fetch['to'], $fetch['from']);
                $messages[] = $fetch;
            }
        }

        $this->__GB->prepareMessage($messages);

    }

    public function getConversations($userID)
    {

        $sql = "SELECT
				C.id AS conversationID,
				C.date,
				u.id AS recpientID,
				u.picture AS recpientPicture,
				u.name AS recpientName,
				u.username AS recpientUsername
				FROM " . $this->__GB->__DB->DB_prefix . "conversations as C
				LEFT JOIN " . $this->__GB->__DB->DB_prefix . "users as u
					ON
					CASE
					WHEN C.from = {$userID}
						THEN u.id = C.to
					WHEN C.to = {$userID}
						THEN u.id = C.from

					END
				WHERE
					CASE
					WHEN C.from = {$userID}
						THEN C.to = u.id
					WHEN C.to = {$userID}
						THEN C.from= u.id
					END

				GROUP BY C.id ORDER BY C.date DESC
		";
        $query = $this->__GB->__DB->query($sql);
        $conversations = array();
        if ($this->__GB->__DB->num_rows($query)) {
            while ($fetch = $this->__GB->__DB->fetch_assoc($query)) {
                $fetch['lastMessage'] = $this->getLastMessage($fetch['conversationID']);
                $fetch['date'] = $this->__GB->TimeAgo($fetch['date']);
                if ($fetch['lastMessage'] === null) {
                    continue;
                }
                $conversations[] = $fetch;

            }
            $this->__GB->prepareMessage($conversations);
        } else {
            $this->__GB->prepareMessage(array());
        }


    }

    public function getLastMessage($conversationID)
    {
        $query = $this->__GB->__DB->select('messages', '`message`', "`conversationID` = {$conversationID}", '`date` DESC', '1');
        if ($this->__GB->__DB->num_rows($query) != 0) {
            $fetch = $this->__GB->__DB->fetch_assoc($query);
            return $fetch['message'];
        } else {
            return null;
        }
    }

    public function getConversationsOld($userID)
    {
        $query = $this->__GB->__DB->select('conversations', '*', "`from` = {$userID} OR `to` = {$userID}");
        if ($this->__GB->__DB->num_rows($query) != 0) {
            $conversations['conversations'] = array();
            while ($fetch = $this->__GB->__DB->fetch_assoc($query)) {
                if ($userID != $fetch['from']) {
                    $fetch['userID'] = $fetch['from'];
                } else {
                    $fetch['userID'] = $fetch['to'];
                }
                $queryUser = $this->__GB->__DB->select('users', '`id`,`username`,`picture`', '`id` = ' . $fetch['userID']);
                $fetchUser = $this->__GB->__DB->fetch_assoc($queryUser);
                $fetch['last_message'] = $this->getLastMessage($fetch['id']);
                if ($fetch['last_message'] === null) {
                    continue;
                }
                $fetch['date'] = $this->__GB->TimeAgo($fetch['date']);
                unset($fetch['from'], $fetch['to'], $fetchUser['id']);
                $conversation = array_merge($fetch, $fetchUser);
                $conversations['conversations'][] = $conversation;
            }
            $this->__GB->prepareMessage($conversations);
        } else {
            $this->__GB->prepareMessage(array('conversations' => null));
        }
    }

    public function updateProfile($array, $imageID, $coverID, $userID)
    {
        foreach ($array as $key => $value) {
            $array[$key] = $this->__GB->__DB->escape_string(trim($value));
        }
        $query = $this->__GB->__DB->select('users', '`username`', "`id` = {$userID}");
        $fetch = $this->__GB->__DB->fetch_assoc($query);
        if ($fetch['username'] != $array['username'] && $this->UserExist($array['username'])) {
            $this->__GB->prepareMessage(array('done' => false, 'message' => 'Username already exists'));
        } else if (filter_var($array['email'], FILTER_VALIDATE_EMAIL) === false) {
            $this->__GB->prepareMessage(array('done' => false, 'message' => 'Invalid E-mail'));
        } else {
            $fields = "`username` = '" . $array['username'] . "'";
            $fields .= ",`name` = '" . $array['name'] . "'";
            $fields .= ",`job` = '" . $array['job'] . "'";
            $fields .= ",`address` = '" . $array['address'] . "'";
            $fields .= ",`email` = '" . $array['email'] . "'";
            if (!empty($array['password'])) {
                $fields .= ",`password` = '" . md5($array['password']) . "'";
            }
            if ($imageID != null) {
                $fields .= ",`picture` = '{$imageID}'";
            }
            if ($coverID != null) {
                $fields .= ",`cover` = '{$coverID}'";
            }
            $update = $this->__GB->__DB->update('users', $fields, "`id` = {$userID}");
            if ($update) {
                $this->__GB->prepareMessage(array('done' => true, 'message' => 'Profile updated successfully'));
            } else {
                $this->__GB->prepareMessage(array('done' => false, 'message' => 'Profile update failed'));
            }
        }

    }

    public function UserExist($username)
    {
        $query = $this->__GB->__DB->select('users', '`id`', "`username` = '" . $username . "'");
        if ($this->__GB->__DB->num_rows($query) != 0) {
            return true;
        } else {
            return false;
        }
        $this->__GB->__DB->free_result();
    }

    public function getProfile($id)
    {
        $query = $this->__GB->__DB->select('users', '*', "`id` = {$id}");
        $fetch = $this->__GB->__DB->fetch_assoc($query);
        unset($fetch['password']);
        $this->__GB->prepareMessage(array('profile' => $fetch));
    }

    public function userRegister($array)
    {
        $username = $this->__GB->__DB->escape_string($array['username']);
        $email = $this->__GB->__DB->escape_string($array['email']);
        $password = md5($array['password']);
        if (strlen(trim($username)) <= 4) {
            $response = array(
                'done' => false,
                'message' => 'Username too short'
            );
            $this->__GB->prepareMessage($response);
        } else if (filter_var($email, FILTER_VALIDATE_EMAIL) === false) {
            $response = array(
                'done' => false,
                'message' => 'Invalid E-mail'
            );
            $this->__GB->prepareMessage($response);
        } else if (strlen(trim($array['password'])) <= 5) {
            $response = array(
                'done' => false,
                'message' => 'Password too short'
            );
            $this->__GB->prepareMessage($response);
        } else if ($this->UserExist($username)) {
            $response = array(
                'done' => false,
                'message' => 'Username already exists'
            );
            $this->__GB->prepareMessage($response);
        } else {
            if (isset($_FILES['image'])) {
                $imageID = $this->__GB->uploadImage($_FILES['image']);
            } else {
                $imageID = null;
            }
            $array = array(
                'username' => $username,
                'password' => $password,
                'email' => $email,
                'date' => time(),
                'picture' => $imageID
            );
            if ($this->__GB->GetConfig('emailactivation', 'users') == 1) {
                $array['active'] = 0;
            }
            $insert = $this->__GB->__DB->insert('users', $array);
            if ($insert) {
                if ($this->__GB->GetConfig('emailactivation', 'users') == 1) {
                    $this->sendEmailActivation($this->__GB->__DB->lastID(), $email);
                }
                $response = array(
                    'done' => true,
                    'message' => 'Your account has been created'
                );
                $this->__GB->prepareMessage($response);
            } else {
                $response = array(
                    'done' => false,
                    'message' => 'Oops Something Went Wrong'
                );
                $this->__GB->prepareMessage($response);
            }
        }


    }

    public function sendEmailActivation($userID, $email)
    {
        $hash = md5(time() . $email . $userID . microtime(true));
        $array = array('hash' => $hash,
            'userid' => $userID,
            'date' => time()
        );
        $subject = $this->__GB->GetConfig('site_name', 'site') . " Activate Your Account";
        $message = "Please Follow This Link to Activate Your account<br>";
        $message .= '<a href="' . $this->__GB->GetConfig('url', 'site') . 'index.php?activate=' . $hash . '">Activate</a>';
        $mail = mail($email, $subject, $message);
        if ($mail) {
            $this->__GB->__DB->insert('activation', $array);
        }

    }

    public function userLogin($username, $password, $webApp = false)
    {
        $username = $this->__GB->__DB->escape_string($username);
        $password = md5($password);
        $userQuery = $this->__GB->__DB->select('users', '`id`,`active`', "`username` = '{$username}' AND `password` = '{$password}'");
        if ($this->__GB->__DB->num_rows($userQuery) != 0) {
            $fetch = $this->__GB->__DB->fetch_assoc($userQuery);
            $token = md5(time() . uniqid() . $username);
            if ($fetch['active'] != 1) {
                $array = array(
                    'success' => false,
                    'userID' => null,
                    'token' => null,
                    'message' => 'Account need activation'
                );
                $this->__GB->prepareMessage($array);
            } else {

                if ($webApp != false) {
                    $this->__GB->SetSession('userID', $fetch['id']);
                    $array = array('success' => true);
                } else {
                    $array = array(
                        'userID' => $fetch['id'],
                        'token' => $token,
                        'date' => time()
                    );

                    $insert = $this->__GB->__DB->insert('sessions', $array);
                    if ($insert) {
                        $array = array(
                            'success' => true,
                            'userID' => $fetch['id'],
                            'token' => $token,
                            'message' => ''
                        );

                    }
                }
                $this->__GB->prepareMessage($array);
            }
        } else {
            $array = array(
                'success' => false,
                'userID' => null,
                'token' => null,
                'message' => 'Incorrect login details'
            );
            $this->__GB->prepareMessage($array);
        }

    }

    public function getFollowers($id)
    {
        $id = $this->__GB->__DB->escape_string($id);
        $query = $this->__GB->__DB->select('follows', '`from`', "`to` = {$id}");
        if ($this->__GB->__DB->num_rows($query) != 0) {
            $following = array();
            while ($fetch = $this->__GB->__DB->fetch_assoc($query)) {
                $user = $this->getUser($fetch['from']);
                unset($user['password'], $user['reg_id'], $user['email']);
                $following[] = $user;
            }
            $this->__GB->prepareMessage($following);
        } else {

            $this->__GB->prepareMessage(array());
        }
    }

    public function getUser($id)
    {
        $id = $this->__GB->__DB->escape_string($id);
        $query = $this->__GB->__DB->select('users', '*', "`id` = {$id}");
        if ($this->__GB->__DB->num_rows($query) != 0) {
            $fetch = $this->__GB->__DB->fetch_assoc($query);
            return $fetch;
        }
    }

    public function getFollowing($id)
    {
        $id = $this->__GB->__DB->escape_string($id);
        $query = $this->__GB->__DB->select('follows', '`to`', "`from` = {$id}");
        if ($this->__GB->__DB->num_rows($query) != 0) {
            $following = array();
            while ($fetch = $this->__GB->__DB->fetch_assoc($query)) {
                $user = $this->getUser($fetch['to']);
                unset($user['password'], $user['reg_id'], $user['email']);

                $following[] = $user;
            }
            $this->__GB->prepareMessage($following);
        } else {

            $this->__GB->prepareMessage(array());
        }
    }

    public function FollowToggle($userID, $to)
    {
        $to = $this->__GB->__DB->escape_string($to);
        $query = $this->__GB->__DB->select('users', '*', "`id` = {$to}");
        if ($this->__GB->__DB->num_rows($query) != 0) {
            $fetch = $this->__GB->__DB->fetch_assoc($query);
            if ($fetch['id'] != $userID) {
                if ($this->__GB->isFollowing($userID, $fetch['id']) != true) {
                    $like = $this->__GB->__DB->insert('follows', array('from' => $userID, 'to' => $fetch['id'], 'date' => time()));
                    if ($like) {
                        if ($fetch['reg_id'] != null) {
                            $followerQuery = $this->__GB->__DB->select('users', '`username`', "`id` = {$userID}");
                            $fetchFollower = $this->__GB->__DB->fetch_assoc($followerQuery);
                            $notificationData = array(
                                'for' => 'following',
                                'recipientID' => $fetch['id'],
                                'username' => $fetchFollower['username'],
                                'userID' => $userID
                            );
                            $regIDs = array($fetch['reg_id']);
                            $this->sendMessageThroughGCM($regIDs, $notificationData);
                        }
                        $this->__GB->prepareMessage(array('done' => true, 'message' => ''));
                    } else {
                        $this->__GB->prepareMessage(array('done' => false, 'message' => 'Can\'t follow the user Try Again'));
                    }
                } else {
                    $this->unFollow($userID, $to);
                }
            }
        } else {
            $this->__GB->prepareMessage(array('done' => false, 'message' => 'User doesn\'t Exists anymore'));
        }
    }

    public function unFollow($userID, $to)
    {
        $to = $this->__GB->__DB->escape_string($to);
        $delete = $this->__GB->__DB->delete('follows', "`from` = {$userID} AND `to` = {$to}");
        if ($delete) {
            $this->__GB->prepareMessage(array('done' => true, 'message' => ''));
        } else {
            $this->__GB->prepareMessage(array('done' => false, 'message' => 'Can\'t follow the user Try Again'));
        }
    }

    public function getLikes($id)
    {
        $query = $this->__GB->__DB->select('likes', '`to`', "`from`= {$id}");
        if ($this->__GB->__DB->num_rows($query) != 0) {
            $ids = array();
            while ($fetch = $this->__GB->__DB->fetch_assoc($query)) {
                $ids[] = $fetch['to'];
            }
            return $ids;
        } else {
            return 0;
        }
    }

    public function getFollows($id)
    {
        $query = $this->__GB->__DB->select('follows', '`to`', "`from`= {$id}");
        if ($this->__GB->__DB->num_rows($query) != 0) {
            $ids = array();
            while ($fetch = $this->__GB->__DB->fetch_assoc($query)) {
                $ids[] = $fetch['to'];
            }
            return $ids;
        } else {
            return 0;
        }
    }

    public function getUserID($token)
    {

        $token = $this->__GB->__DB->escape_string($token);
        $query = $this->__GB->__DB->select('sessions', '*', "`token`= '{$token}'");
        if ($this->__GB->__DB->num_rows($query) != 0) {
            $fetch = $this->__GB->__DB->fetch_assoc($query);
            return $fetch['userID'];
        } else {
            return 0;
        }
    }

    public function UsersTotalVisits($id)
    {
        $query = $this->__GB->__DB->select('links', 'sum(`visits`) as `totalVisits`', '`user_id`=' . $id);
        $fetch = $this->__GB->__DB->fetch_assoc($query);
        return $fetch['totalVisits'];
    }

    public function adminLogin($array)
    {
        foreach ($array as $key => $val) {
            $array[$key] = trim($this->__GB->__DB->escape_string($val));
        }
        $query = $this->__GB->__DB->select('admins', '*', "`username` = '" . $array['username'] . "' AND `password` = '" . md5($array['password']) . "'");
        if (empty($array['username']) || empty($array['password'])) {
            echo $this->__GB->DisplayError('All fields required');
        } else if ($this->__GB->__DB->num_rows($query) <= 0) {
            echo $this->__GB->DisplayError('Login failed please try again');
        } else {
            $fetch = $this->__GB->__DB->fetch_assoc($query);
            $this->__GB->SetSession('admin', $fetch['id']);
            $this->__GB->SetSession('adminUsername', $fetch['username']);
            header('Location: index.php');
        }
        $this->__GB->__DB->free_result($query);
    }

    public function AdminExists($username)
    {
        $query = $this->__GB->__DB->select('admins', '`id`', "`username` = '" . $username . "'");
        if ($this->__GB->__DB->num_rows($query) != 0) {
            return true;
        } else {
            return false;
        }
    }

    public function getUserDetails($id, $userID)
    {
        $id = (int)$id;
        $userID = (int)$userID;
        if ($id != 0 && $id != $userID) {
            $query = $this->__GB->__DB->select('follows', '`id`', "`from` = {$userID} AND `to` = {$id}");
            if ($this->__GB->__DB->num_rows($query) != 0) {
                $query = $this->__GB->__DB->select('users', '`id`,`username`,`email`,`cover`,`name`,`job`,`address`,`picture`,`username`', "`id` = {$id}");
                $profile = array();
                $fetch = $this->__GB->__DB->fetch_assoc($query);
                if (empty($fetch['name'])) {
                    $fetch['name'] = $fetch['username'];
                }
                //$fetch['picture'] = $this->__GB->GetConfig('url','site').'safe_image.php?id='.$fetch['picture'];
                $fetch['totalFollowers'] = $this->__GB->CountRows('follows', "`to` = {$id}");
                $fetch['totalPosts'] = $this->__GB->CountRows('posts', "`ownerID` = {$id}");
                $fetch['mine'] = false;
                $fetch['followed'] = $this->__GB->isFollowing($userID, $fetch['id']);
                return $fetch;
            } else {
                $query = $this->__GB->__DB->select('users', '`id`,`username`,`email`,`cover`,`name`,`job`,`address`,`picture`', "`id` = {$id}");
                $profile = array();
                $fetch = $this->__GB->__DB->fetch_assoc($query);
                //$fetch['picture'] = $this->__GB->GetConfig('url','site').'safe_image.php?id='.$fetch['picture'];
                $fetch['totalFollowers'] = $this->__GB->CountRows('follows', "`to` = {$id}");
                $fetch['totalPosts'] = $this->__GB->CountRows('posts', "`ownerID` = {$id}");
                $fetch['mine'] = false;
                $fetch['followed'] = $this->__GB->isFollowing($userID, $fetch['id']);
                return $fetch;
            }
        } else {
            $query = $this->__GB->__DB->select('users', '`id`,`username`,`email`,`cover`,`name`,`job`,`address`,`picture`', "`id` = {$userID}");
            $profile = array();
            $fetch = $this->__GB->__DB->fetch_assoc($query);
            //$fetch['picture'] = $this->__GB->GetConfig('url','site').'safe_image.php?id='.$fetch['picture'];
            $fetch['totalFollowers'] = $this->__GB->CountRows('follows', "`to` = {$userID}");
            $fetch['totalPosts'] = $this->__GB->CountRows('posts', "`ownerID` = {$userID}");
            $fetch['mine'] = true;
            $fetch['followed'] = true;

            return $fetch;
        }
    }

    public function GetAdmins($limit)
    {
        $query = $this->__GB->__DB->select('admins', '*', '', '`id` DESC', $limit);
        $links = '';
        while ($fetch = $this->__GB->__DB->fetch_assoc($query)) {
            $links[] = $fetch;
        }
        return $links;
    }

    public function GetUsers($limit)
    {
        $query = $this->__GB->__DB->select('users', '*', '', '`id` DESC', $limit);
        $links = '';
        while ($fetch = $this->__GB->__DB->fetch_assoc($query)) {
            $fetch['date'] = date('Y-m-d H:i', $fetch['date']);
            $links[] = $fetch;
        }
        return $links;
    }

    public function EmailExist($email)
    {
        $query = $this->__GB->__DB->select('users', '`id`', "`email` = '" . $email . "'");
        if ($this->__GB->__DB->num_rows($query) != 0) {
            return true;
        } else {
            return false;
        }
        $this->__GB->__DB->free_result();
    }

}

?>