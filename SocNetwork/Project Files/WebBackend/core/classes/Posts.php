<?php

/**
 *
 */
class Posts
{
    private $__GB;

    public $google_api_url = 'https://android.googleapis.com/gcm/send';
    function __construct($__GB)
    {
        $this->__GB = $__GB;
    }

    public function deleteComment($userID, $commentID)
    {
        $commentID = (int)$commentID;
        $query = $this->__GB->__DB->select('comments', '`id`', "`id` = {$commentID} AND `from` = {$userID}");
        if ($this->__GB->__DB->num_rows($query) != 0) {
            $delete = $this->__GB->__DB->delete('comments', "`id` = {$commentID}");
            if ($delete) {
                $this->__GB->prepareMessage(array('done' => true, 'message' => ''));
            } else {
                $this->__GB->prepareMessage(array('done' => false, 'message' => 'Please again later'));
            }
        } else {
            $this->__GB->prepareMessage(array('done' => false, 'message' => 'you don\'t have enough permissions'));
        }
    }

    public function updatePost($userID, $postID, $postStatus)
    {
        $postStatus = $this->__GB->__DB->escape_string($postStatus);
        $postID = (int)$postID;
        $query = $this->__GB->__DB->select('posts', '`id`', "`id` = {$postID} AND `ownerID` = {$userID}");

        if ($this->__GB->__DB->num_rows($query) != 0) {
            $update = $this->__GB->__DB->update('posts', "`status` = '{$postStatus}'", "`id` = {$postID}");
            if ($update) {
                $response = array(
                    'done' => true,
                    'message' => 'Post has been updated successfully'
                );
            } else {
                $response = array(
                    'done' => false,
                    'message' => 'Please try again something went wrong'
                );
            }
        } else {
            $response = array(
                'done' => false,
                'message' => 'Your are not the owner of this post'
            );
        }
        $this->__GB->prepareMessage($response);
        //$update = $reason = $this->__GB->__DB->update('')
    }

    public function reportPost($userID, $postID, $reason)
    {
        $postID = (int)$postID;
        $reason = $this->__GB->__DB->escape_string($reason);
        $query = $this->__GB->__DB->select('posts', '`id`', "`id` = {$postID}");
        if ($this->__GB->__DB->num_rows($query) != 0) {
            $data = array(
                'postID' => $postID,
                'reason' => $reason,
                'reporterID' => $userID
            );
            $insert = $this->__GB->__DB->insert('reports', $data);
            if ($insert) {
                $response = array(
                    'done' => true,
                    'message' => 'Thanks for your report'
                );
            } else {
                $response = array(
                    'done' => false,
                    'message' => 'Please try again something went wrong'
                );
            }

        } else {
            $response = array(
                'done' => false,
                'message' => 'This post is not exists any more'
            );
        }

        $this->__GB->prepareMessage($response);
    }

    public function deletePost($userID, $postID)
    {
        $postID = (int)$postID;
        $query = $this->__GB->__DB->select('posts', '`id`,`ownerID`', "`id` = {$postID}");
        if ($this->__GB->__DB->num_rows($query) != 0) {
            $fetch = $this->__GB->__DB->fetch_assoc($query);
            if ($fetch['ownerID'] != $userID) {
                $response = array(
                    'done' => false,
                    'message' => 'You are not the owner of this post'
                );
            } else {
                $delete = $this->__GB->__DB->delete('posts', "`id` = {$postID}");
                if ($delete) {
                    $response = array(
                        'done' => true,
                        'message' => 'Your post has been deleted'
                    );
                } else {
                    $response = array(
                        'done' => false,
                        'message' => 'Please try again something went wrong'
                    );
                }
            }

        } else {
            $response = array(
                'done' => false,
                'message' => 'Post already deleted'
            );
        }
        $this->__GB->prepareMessage($response);
    }

    public function addComment($comment, $to, $from)
    {
        $comment = $this->__GB->__DB->escape_string($comment);
        $to = $this->__GB->__DB->escape_string($to);
        $array = array(
            'from' => $from,
            'to' => $to,
            'text' => $comment,
            'date' => time()
        );
        $commenter = $this->__GB->__DB->select('users', '*', '`id` = ' . $from);
        $commenterFetch = $this->__GB->__DB->fetch_assoc($commenter);
        $insert = $this->__GB->__DB->insert('comments', $array);
        if ($insert) {
            $query = $this->__GB->__DB->select('posts', '`ownerID`', "`id` = $to");
            $fetch = $this->__GB->__DB->fetch_assoc($query);
            if ($fetch['ownerID'] != $from) {
                $userquery = $this->__GB->__DB->select('users', '`id`,`reg_id`', '`id` = ' . $fetch['ownerID']);
                $userfetch = $this->__GB->__DB->fetch_assoc($userquery);
                $notificationData = array(
                    'for' => 'comment',
                    'recipientID' => $userfetch['id'],
                    'username' => $commenterFetch['username'],
                    'postID' => $to
                );
                $regIDs = array($userfetch['reg_id']);
                $this->sendMessageThroughGCM($regIDs, $notificationData);
            }

            $this->__GB->prepareMessage(array('done' => true, 'message' => ''));
        } else {
            $this->__GB->prepareMessage(array('done' => false, 'message' => 'Error, Try Again'));
        }
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

    public function getPostComments($postID)
    {
        $query = $this->__GB->__DB->select('comments', '*', '`to` = ' . $postID, '`id` DESC');
        if ($this->__GB->__DB->num_rows($query) != 0) {
            $comments = array();
            while ($fetch = $this->__GB->__DB->fetch_assoc($query)) {
                $owner = $this->getPostOwner($fetch['from']);
                $fetch['ownerID'] = $owner['id'];
                $fetch['ownerName'] = $owner['name'];
                $fetch['ownerUsername'] = $owner['username'];
                $fetch['comment'] = $fetch['text'];
                $fetch['ownerPicture'] = $owner['picture'];
                $fetch['date'] = $this->__GB->TimeAgo($fetch['date']);
                unset($fetch['from'], $fetch['to'], $fetch['text']);
                $comments[] = $fetch;
            }
            $this->__GB->prepareMessage($comments);

        } else {
            $this->__GB->prepareMessage(array());
        }
    }

    public function getPostOwner($id)
    {
        $query = $this->__GB->__DB->select('users', '*', "`id` = $id");
        return $this->__GB->__DB->fetch_assoc($query);
    }

    public function getPost($id, $userID)
    {
        $id = $this->__GB->__DB->escape_string($id);
        $query = $this->__GB->__DB->select('posts', '*', '`id` = ' . $id, '`id` DESC');
        $post = array();
        if ($this->__GB->__DB->num_rows($query) != 0) {
            $fetch = $this->__GB->__DB->fetch_assoc($query);
            $this->__GB->__DB->update('posts', '`views` = `views`+1', '`id`=' . $fetch['id']);
            $owner = $this->getPostOwner($fetch['ownerID']);
            $fetch['ownerName'] = $owner['name'];
            $fetch['date'] = $this->__GB->TimeAgo($fetch['date']);
            $fetch['ownerPicture'] = $owner['picture'];
            $fetch['liked'] = $this->__GB->isLikeIt($userID, $fetch['id']);
            $fetch['mine'] = ($fetch['ownerID'] != $userID) ? false : true;
            $fetch['likes'] = $this->__GB->CountRows('likes', '`to` = ' . $fetch['id']);
            $fetch['status'] = (empty($fetch['status'])) ? null : $fetch['status'];
            $fetch['image'] = (empty($fetch['image'])) ? null : $fetch['image'];
            //$post['post'] = $fetch;
            //$post['comments'] = $this->getPostComments($fetch['id']);
            $this->__GB->prepareMessage($fetch);
        }
    }

    public function getUserPosts($limit, $where = '', $userID)
    {
        $query = $this->__GB->__DB->select('posts', '*', $where, '`id` DESC', $limit);
        if ($this->__GB->__DB->num_rows($query) != 0) {
            $posts = array();
            while ($fetch = $this->__GB->__DB->fetch_assoc($query)) {
                $owner = $this->getPostOwner($fetch['ownerID']);
                $fetch['ownerName'] = $owner['name'];
                $fetch['date'] = $this->__GB->TimeAgo($fetch['date']);
                $fetch['ownerPicture'] = $owner['picture'];
                $fetch['likes'] = $this->__GB->CountRows('likes', '`to` = ' . $fetch['id']);
                $fetch['liked'] = $this->__GB->isLikeIt($userID, $fetch['id']);
                $posts[] = $fetch;

            }
        } else {
            $posts = array();
        }
        return $posts;
    }

    public function unlikePost($userID, $postID)
    {
        $postID = $this->__GB->__DB->escape_string($postID);
        $delete = $this->__GB->__DB->delete('likes', "`from` = {$userID} AND `to` = {$postID}");
        if ($delete) {
            $this->__GB->prepareMessage(array('done' => true, 'message' => 'Post removed from your favourite list'));
        } else {
            $this->__GB->prepareMessage(array('done' => false, 'message' => 'try again, something went wrong'));
        }
    }

    public function likePost($userID, $postID)
    {
        $postID = $this->__GB->__DB->escape_string($postID);
        $query = $this->__GB->__DB->select('posts', '*', "`id` = {$postID}");
        if ($this->__GB->__DB->num_rows($query) != 0) {
            $fetch = $this->__GB->__DB->fetch_assoc($query);
            if ($fetch['ownerID'] != $userID) {
                $followsQuery = $this->__GB->__DB->select('follows', "`from` = {$userID} AND `to` = " . $fetch['ownerID']);
                if ($this->__GB->__DB->num_rows($followsQuery) != 0) {
                    if ($this->__GB->isLikeIt($userID, $fetch['id']) != true) {
                        $like = $this->__GB->__DB->insert('likes', array('from' => $userID, 'to' => $fetch['id'], 'date' => time()));
                        if ($like) {
                            $this->__GB->prepareMessage(array('done' => true, 'message' => 'post has been added to your favourte list'));
                        } else {
                            $this->__GB->prepareMessage(array('done' => false, 'message' => 'try again, something went wrong'));
                        }
                    } else {
                        $this->__GB->prepareMessage(array('done' => false, 'message' => 'already liked'));
                    }

                } else {
                    $this->__GB->prepareMessage(array('done' => false, 'message' => 'you donot have enough perms :)'));
                }
            } else {
                if ($this->__GB->isLikeIt($userID, $fetch['id']) != true) {
                    $like = $this->__GB->__DB->insert('likes', array('from' => $userID, 'to' => $fetch['id'], 'date' => time()));
                    if ($like) {
                        $this->__GB->prepareMessage(array('done' => true, 'message' => 'post has been added to your favourte list'));
                    } else {
                        $this->__GB->prepareMessage(array('done' => false, 'message' => 'error'));
                    }
                } else {
                    $this->__GB->prepareMessage(array('done' => true, 'message' => 'post has been added to your favourte list'));
                }
            }


        } else {
            $this->__GB->prepareMessage(array('done' => false, 'message' => 'Post not availabe'));
        }
    }

    public function publishStatusWeb($array, $userID, $imageID)
    {
        foreach ($array as $key => $val) {
            $array[$key] = $this->__GB->__DB->escape_string(trim($val));
        }
        $status = (isset($array['status'])) ? $array['status'] : null;
        $privacy = (isset($array['privacy']) && $array['privacy'] == 'public') ? 1 : 0;
        $statusData = array(
            'status' => $this->__GB->__DB->escape_string($status),
            'image' => $imageID,
            'privacy' => $privacy,
            'ownerID' => $userID,
            'date' => time(),
            'views' => 0);
        $insert = $this->__GB->__DB->insert('posts', $statusData);
        return $insert;
    }

    public function publishStatus($array, $userID, $imageID)
    {
        $statusData = array(
            'ownerID' => $userID,
            'date' => time(),
            'views' => 0);
        if ($imageID != null) {
            $statusData['image'] = $imageID;
        }
        if (isset($array['status']) && !empty($array['status'])) {
            $statusData['status'] = $this->__GB->__DB->escape_string($array['status']);
        }
        if (isset($array['privacy'])) {
            $statusData['privacy'] = ($array['privacy'] == 'public') ? 1 : 0;
        } else {
            $statusData['privacy'] = 0;
        }

        if (isset($array['link']) && $this->__GB->isURL($array['link'])) {
            $link = $this->__GB->getLinkHash($array['link']);
            if ($link != null) {
                $statusData['link'] = $link;
            }

        }

        if (isset($array['place']) && !empty($array['place'])) {
            $statusData['place'] = $this->__GB->__DB->escape_string($array['place']);
        }
        $insert = $this->__GB->__DB->insert('posts', $statusData);
        if ($insert) {
            $this->__GB->prepareMessage(array('done' => true, 'message' => 'post has been added successfully'));
        } else {
            $this->__GB->prepareMessage(array('done' => false, 'message' => 'Something went wrong please try again'));
        }
    }

    public function getLikedPosts($userID, $limit)
    {
        $query = "SELECT P.*,

						COUNT(L.to) AS likes,
						U.name AS ownerName,
						U.username AS ownerUsername,
						U.picture AS ownerPicture
						FROM " . $this->__GB->__DB->DB_prefix . "posts P

						LEFT JOIN " . $this->__GB->__DB->DB_prefix . "users AS U
						ON U.id = P.ownerID

						LEFT JOIN " . $this->__GB->__DB->DB_prefix . "likes AS L
						ON L.from = {$userID}

						WHERE P.id = L.to
						GROUP BY P.id ORDER BY P.id DESC LIMIT {$limit}";

        $query = $this->__GB->__DB->query($query);
        if ($this->__GB->__DB->num_rows($query) != 0) {
            $posts = array();
            while ($fetch = $this->__GB->__DB->fetch_assoc($query)) {
                $fetch['date'] = $this->__GB->TimeAgo($fetch['date']);
                $fetch['status'] = (empty($fetch['status'])) ? null : $fetch['status'];
                $fetch['image'] = (empty($fetch['image'])) ? null : $fetch['image'];
                $fetch['liked'] = $this->__GB->isLikeIt($userID, $fetch['id']);
                $fetch['link'] = $this->__GB->getLink($fetch['link']);

                unset($fetch['to']);
                $posts[] = $fetch;
            }
            $this->__GB->prepareMessage($posts);
        } else {
            $this->__GB->prepareMessage(array());
        }
    }

    public function getUPosts($userID, $limit, $query)
    {
        $query = $query . " LIMIT {$limit}";

        $query = $this->__GB->__DB->query($query);
        if ($this->__GB->__DB->num_rows($query) != 0) {
            $posts = array();
            while ($fetch = $this->__GB->__DB->fetch_assoc($query)) {
                $fetch['date'] = $this->__GB->TimeAgo($fetch['date']);
                $fetch['liked'] = $this->__GB->isLikeIt($userID, $fetch['id']);
                $fetch['link'] = $this->__GB->getLink($fetch['link']);
                unset($fetch['to']);
                $posts[] = $fetch;
            }
            $this->__GB->prepareMessage($posts);
        } else {
            $this->__GB->prepareMessage(array());
        }
    }

    public function getPosts($userID, $querysql, $limit)
    {
        $query = $querysql . " LIMIT {$limit}";
        $query = $this->__GB->__DB->query($query);
        if ($this->__GB->__DB->num_rows($query) != 0) {
            $posts = array();
            while ($fetch = $this->__GB->__DB->fetch_assoc($query)) {
                $fetch['date'] = $this->__GB->TimeAgo($fetch['date']);
                $fetch['liked'] = $this->__GB->isLikeIt($userID, $fetch['id']);
                $fetch['link'] = $this->__GB->getLink($fetch['link']);
                unset($fetch['to']);
                $posts[] = $fetch;
            }
            $this->__GB->prepareMessage($posts);
        } else {
            $this->__GB->prepareMessage(array());
        }
    }
}

?>