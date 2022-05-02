<?php

class General
{
    public $__DB, $__LNG, $__SEC, $__TPL;
    private $__GCM = "AIzaSyBMQCkVOrpPIWCKNzE9P2bJ22wKG9y27dA";

    function __construct($__DB, $__SEC)
    {
        $this->__DB = $__DB;
        $this->__SEC = $__SEC;
    }

    public function getMapImage($placeName)
    {
        $placeName = $this->__DB->escape_string($placeName);
        $query = $this->__DB->select('places', '*', "`place_name` = '{$placeName}'");
        if ($this->__DB->num_rows($query)) {
            $fetch = $this->__DB->fetch_assoc($query);
            $imageURL = "https://maps.googleapis.com/maps/api/staticmap?center=" . $fetch['latitude'] . "," . $fetch['longitude'] . "&zoom=9&sensor=false&markers=" . $fetch['latitude'] . "," . $fetch['longitude'] . "&";
            $this->prepareMessage(array('done' => true, 'message' => $imageURL));
        } else {
            $this->prepareMessage(array('done' => true, 'message' => "https://maps.googleapis.com/maps/api/staticmap?center={$placeName}&zoom=9&sensor=false&markers={$placeName}&"));
        }
    }

    public function prepareMessage($array)
    {
        ob_clean();
        header('Content-Type: application/json; charset=utf-8');
        if (is_array($array)) {
            echo json_encode($array);
        } else {
            echo $array;
        }
    }

    public function isURL($url)
    {
        if (!preg_match('/^http(s)?:\/\/[a-z0-9-]+(.[a-z0-9-]+)*(:[0-9]+)?(\/.*)?$/i', $url)) {
            return false;
        } else {
            return true;
        }
    }

    public function getLink($hash)
    {
        $hash = $this->__DB->escape_string($hash);
        $query = $this->__DB->select('links', '*', "`hash` = '{$hash}'");
        if ($this->__DB->num_rows($query) != 0) {
            return $this->__DB->fetch_assoc($query);
        } else {
            return null;
        }
    }

    public function getLinkHash($link)
    {
        $hash = md5(time() . $link);
        $videoID = $this->getVideoIDFromURL($link);
        if ($videoID != false) {
            $video = $this->getVideoInformation($link);
            $linkInfo = array(
                'hash' => $hash,
                'link' => $this->__DB->escape_string($videoID),
                'image' => $this->__DB->escape_string($video['thumbnail_url']),
                'title' => $this->__DB->escape_string($video['title']),
                'desc' => $this->__DB->escape_string($video['author_name']),
                'type' => 'youtube'
            );
        } else {
            $linkInfo = array(
                'hash' => $hash,
                'link' => $this->__DB->escape_string($link),
                'type' => 'other'
            );
            $content = $this->file_get_contents_curl($link);
            $metaTags = $this->getMetaTags($content);

            if (isset($metaTags['image']) && !empty($metaTags['image'])) {
                $linkInfo['image'] = $this->__DB->escape_string($metaTags['image']);
            } else {
                $imageSrc = $this->extractImage($content);
                if ($imageSrc != null) {
                    $linkInfo['image'] = $imageSrc;
                }
            }
            if (isset($metaTags['title'])) {
                $linkInfo['title'] = $this->__DB->escape_string($metaTags['title']);
            }
            if (isset($metaTags['description'])) {
                $linkInfo['desc'] = $this->__DB->escape_string($metaTags['description']);
            }

        }
        $query = $this->__DB->select('links', '`hash`', "`link` = '" . $linkInfo['link'] . "'");
        if ($this->__DB->num_rows($query) != 0) {
            $fetch = $this->__DB->fetch_assoc($query);
            return $fetch['hash'];
        } else {
            $insert = $this->__DB->insert('links', $linkInfo);
            if ($insert) {
                return $linkInfo['hash'];
            } else {
                return null;
            }
        }

    }

    public function getVideoIDFromURL($url)
    {
        $pattern =
            '%^# Match any youtube URL
	        (?:https?://)?  # Optional scheme. Either http or https
	        (?:www\.)?      # Optional www subdomain
	        (?:             # Group host alternatives
	          youtu\.be/    # Either youtu.be,
	        | youtube\.com  # or youtube.com
	          (?:           # Group path alternatives
	            /embed/     # Either /embed/
	          | /v/         # or /v/
	          | /watch\?v=  # or /watch\?v=
	          )             # End path alternatives.
	        )               # End host alternatives.
	        ([\w-]{10,12})($|&)  # Allow 10-12 for 11 char youtube id.
	        $%x';
        $result = preg_match($pattern, $url, $matches);
        if (false != $result) {
            return $matches[1];
        }
        return false;
    }

    public function getVideoInformation($link)
    {
        $apiurl = sprintf('http://www.youtube.com/oembed?url=%s&format=json', urlencode($link));
        $content = file_get_contents($apiurl);
        return json_decode($content, true);
    }

    public function file_get_contents_curl($url)
    {
        $ch = curl_init();

        curl_setopt($ch, CURLOPT_HEADER, 0);
        curl_setopt($ch, CURLOPT_RETURNTRANSFER, 1);
        curl_setopt($ch, CURLOPT_URL, $url);
        curl_setopt($ch, CURLOPT_FOLLOWLOCATION, 1);
        curl_setopt($ch, CURLOPT_SSL_VERIFYPEER, false);

        curl_setopt($ch, CURLOPT_USERAGENT, $_SERVER['HTTP_USER_AGENT']);

        //$data = curl_exec($ch);
        $data = curl_exec($ch);
        if ($data === FALSE) {
            return null;
        }
        curl_close($ch);

        return $data;
    }

    public function getMetaTags($contents)
    {
        $result = false;
        if (isset($contents)) {
            $list = array(
                "UTF-8",
                "EUC-CN",
                "EUC-JP",
                "EUC-KR",
                'ISO-8859-1', 'ISO-8859-2', 'ISO-8859-3', 'ISO-8859-4', 'ISO-8859-5',
                'ISO-8859-6', 'ISO-8859-7', 'ISO-8859-8', 'ISO-8859-9', 'ISO-8859-10',
                'ISO-8859-13', 'ISO-8859-14', 'ISO-8859-15', 'ISO-8859-16',
                'Windows-1251', 'Windows-1252', 'Windows-1254',
            );
            $encoding_check = mb_detect_encoding($contents, $list, true);
            $encoding = ($encoding_check === false) ? "UTF-8" : $encoding_check;
            $metaTags = $this->getMetaTagsEncoding($contents, $encoding);
            $result = $metaTags;
        }
        return $result;
    }

    private function getMetaTagsEncoding($contents, $encoding)
    {
        $result = false;
        $metaTags = array("url" => "", "title" => "", "description" => "", "image" => "");
        if (isset($contents)) {
            $doc = new DOMDocument('1.0', 'utf-8');
            @$doc->loadHTML($contents);
            $metas = $doc->getElementsByTagName('meta');
            for ($i = 0; $i < $metas->length; $i++) {
                $meta = $metas->item($i);
                if ($meta->getAttribute('name') == 'description')
                    $metaTags["description"] = $meta->getAttribute('content');
                if ($meta->getAttribute('name') == 'keywords')
                    $metaTags["keywords"] = $meta->getAttribute('content');
                if ($meta->getAttribute('property') == 'og:title')
                    $metaTags["title"] = $meta->getAttribute('content');
                if ($meta->getAttribute('property') == 'og:image')
                    $metaTags["image"] = $meta->getAttribute('content');
                if ($meta->getAttribute('property') == 'og:description')
                    $metaTags["og_description"] = $meta->getAttribute('content');
                if ($meta->getAttribute('property') == 'og:url')
                    $metaTags["url"] = $meta->getAttribute('content');
            }
            if (!empty($metaTags["og_description"])) {
                $metaTags["description"] = $metaTags["og_description"];
                unset($metaTags["og_description"]);
            }
            if (empty($metaTags["title"])) {
                $nodes = $doc->getElementsByTagName('title');
                $metaTags["title"] = $nodes->item(0)->nodeValue;
            }
            $result = $metaTags;
        }
        return $result;
    }

    // http://www.datasciencetoolkit.org/coordinates2politics/32.0000,-5.0000

    public function extractImage($text)
    {
        $imageRegex = "/<img(.*?)src=(\"|\')(.+?)(gif|jpg|png|bmp)(.*?)(\"|\')(.*?)(\/)?>(<\/img>)?/";

        $srcRegex = '/src=(\"|\')(.+?)(\"|\')/i';
        $httpRegex = "/https?\:\/\//i";
        $imgSrc = null;
        if (preg_match_all($imageRegex, $text, $matching)) {
            for ($i = 0; $i < count($matching[0]); $i++) {
                preg_match($srcRegex, $matching[0][$i], $imgSrc);
                $imgSrc = str_replace("../", "", $imgSrc[2]);
                $imgSrc = str_replace("./", "", $imgSrc);
                $imgSrc = str_replace(" ", "%20", $imgSrc);
                if (!preg_match($httpRegex, $imgSrc)) {
                    if (strpos($imgSrc, "//") === 0) {
                        if (preg_match($httpRegex, "http:" . $imgSrc)) {
                            $imgSrc = "http:" . $imgSrc;
                        }
                    }
                }
            }

        }
        return $imgSrc;

    }

    public function getaddress($lat, $lng)
    {

        $longitude = addslashes($lng);
        $latitude = addslashes($lat);

        $query = $this->__DB->select('places', '`place_name`', "`longitude` = '{$longitude}' AND `latitude` = '{$latitude}'");
        if ($this->__DB->num_rows($query)) {
            $fetch = $this->__DB->fetch_assoc($query);
            $response = array('status' => true, 'address' => $fetch['place_name']);
        } else {
            $url = 'http://maps.googleapis.com/maps/api/geocode/json?latlng=' . trim($latitude) . ',' . trim($longitude) . '&sensor=false';
            $json = @file_get_contents($url);
            $data = json_decode($json);
            $status = $data->status;
            if ($status == "OK") {
                $place_name = $data->results[0]->formatted_address;
            } else {
                $place_name = null;
            }
            if ($place_name != null) {
                $insert = $this->__DB->insert('places', array('longitude' => $longitude, 'latitude' => $latitude, 'place_name' => $place_name));
                if ($insert) {
                    $response = array('status' => true, 'address' => $place_name);
                } else {
                    $response = array('status' => true, 'address' => $place_name);
                }
            } else {
                $response = array('status' => false, 'address' => '');
            }
        }
        $this->prepareMessage($response);

    }

    public function send_notification($registatoin_ids, $pushMessage)
    {
        $url = 'https://android.googleapis.com/gcm/send';
        $message = array("m" => $pushMessage);
        $fields = array(
            'registration_ids' => array($registatoin_ids),
            'data' => $message,
        );

        $headers = array(
            'Authorization: key=' . $this->__GCM,
            'Content-Type: application/json'
        );
        // Open connection
        $ch = curl_init();
        curl_setopt($ch, CURLOPT_URL, $url);
        curl_setopt($ch, CURLOPT_POST, true);
        curl_setopt($ch, CURLOPT_HTTPHEADER, $headers);
        curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
        curl_setopt($ch, CURLOPT_SSL_VERIFYPEER, false);
        curl_setopt($ch, CURLOPT_POSTFIELDS, json_encode($fields));
        $result = curl_exec($ch);
        if ($result === FALSE) {
            die('Curl failed: ' . curl_error($ch));
        }

        // Close connection
        curl_close($ch);
        return $result;
    }

    public function isFollowing($from, $to)
    {
        $count = $this->CountRows('follows', "`from` = {$from} AND `to` = {$to}");
        if ($count != 0) {
            return true;
        } else {
            return false;
        }
    }

    public function CountRows($table, $where = '')
    {
        if ($where != '') {
            $query = $this->__DB->select($table, '*', $where);
        } else {
            $query = $this->__DB->select($table, '`id`');
        }
        return $this->__DB->num_rows($query);
    }

    public function isLikeIt($userID, $postID)
    {
        $count = $this->CountRows('likes', "`from` = {$userID} AND `to` = {$postID}");
        if ($count != 0) {
            return true;
        } else {
            return false;
        }
    }

    public function uploadImage($array, $dir = './')
    {
        if (!empty($array)) {
            $tmp = $array["tmp_name"];
            $name = $array["name"];
            $new_name = md5(time() . '-' . $name) . '.jpg';
            $day_folder = date('d-m-y', time());
            if (is_dir($dir . 'uploads/' . $day_folder)) {
                $path = $day_folder;
            } else {
                if (mkdir($dir . 'uploads/' . $day_folder)) {
                    $path = $day_folder;
                } else {
                    $path = '';
                }
            }
            if (move_uploaded_file($tmp, $dir . 'uploads/' . $path . '/' . $new_name)) {
                $imgHash = md5($tmp . $new_name . uniqid() . time());
                $imageData = array(
                    'original_name' => $this->__DB->escape_string($name),
                    'new_name' => $new_name,
                    'path' => $path,
                    'hash' => $imgHash
                );
                $query = $this->__DB->insert('images', $imageData);
                if ($query) {
                    return $imgHash;
                } else {
                    return null;
                }
            } else {
                return null;
            }
        }
    }

    public function getSafeImage($id)
    {
        $id = $this->__DB->escape_string($id);
        $query = $this->__DB->select('images', '*', "`hash` = '$id'");
        if ($this->__DB->num_rows($query) != 0) {
            $fetch = $this->__DB->fetch_assoc($query);
            $path = 'uploads/' . $fetch['path'] . '/' . $fetch['new_name'];
            return $path;
        } else {
            return null;
        }
        /*ob_clean();
        header('Content-Type: image/jpg');
        echo file_get_contents($path);*/

    }

    public function GetConfig($name, $for)
    {
        $query = $this->__DB->select('config', '`value`', "`name` = '{$name}' AND `for` = '{$for}'");
        $fetch = $this->__DB->fetch_assoc($query);
        return $fetch['value'];
    }

    public function TimeAgo($ptime)
    {
        $etime = time() - $ptime;
        if ($etime < 1) {
            return '0 seconds';
        }
        $a = array(12 * 30 * 24 * 60 * 60 => 'year',
            30 * 24 * 60 * 60 => 'month',
            24 * 60 * 60 => 'day',
            60 * 60 => 'hour',
            60 => 'minute',
            1 => 'second'
        );

        foreach ($a as $secs => $str) {
            $d = $etime / $secs;
            if ($d >= 1) {
                $r = round($d);
                return $r . ' ' . $str . ($r > 1 ? 's' : '') . ' ago';
            }
        }
    }

    public function SetSession($key, $value)
    {
        $_SESSION[$key] = $value;
    }

    public function GetSession($key)
    {
        if (isset($_SESSION[$key])) {
            return $_SESSION[$key];
        } else {
            return false;
        }
    }

    public function UnsetSession($key)
    {
        if (isset($_SESSION[$key])) {
            unset($_SESSION[$key]);
        } else {
            return false;
        }
    }

    public function GenerateJson($message, $status = 'error', $type = 'no')
    {
        if (is_array($message)) {
            $msg = '';
            $done = false;
            for ($x = 0; $x < count($message); $x++) {
                if ($message[$x]['type'] === 'yes') {
                    $done = true;
                }
                if (isset($message[$x]['fin']) && $message[$x]['fin'] === true) {
                    $msg .= $message[$x]['message'];
                } else {
                    $msg .= $this->DisplayError($message[$x]['message'], $message[$x]['type']);
                }

            }
            if ($done === true)
                $status = 'done';
            else
                $status = 'error';
            $this->prepareMessage(
                array(
                    'status' => $status,
                    'message' => $msg, $type));
        } else {
            $this->prepareMessage(
                array(
                    'status' => $status,
                    'message' => $this->DisplayError($message, $type)));
        }

    }

    public function DisplayError($error, $error_type = 'no')
    {
        switch ($error_type) {
            case 'no':
                $msg = '<div class="card-panel"><div class="red-text text-darken-2">';
                $msg .= $error;
                $msg .= '</div></div>';
                return $msg;
                break;
            case 'yes':
                $msg = '<div class="card-panel"><div class="teal-text  darken-1">';
                $msg .= $error;
                $msg .= '</div></div>';
                return $msg;
                break;
        }
    }

    public function MetaRefresh($url, $time = '0')
    {
        return "<meta http-equiv=\"refresh\" content=\"$time;URL='$url'\" /> ";
    }

    public function UpdateConfig($name, $value, $for)
    {
        $value = $this->__DB->escape_string($value);
        $this->__DB->update('config', "`value` = '{$value}'", "`name` = '{$name}' AND `for` = '{$for}'");
    }

}

?>