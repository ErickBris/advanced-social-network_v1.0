<?php
include 'header.php';
$page = (isset($_GET['page']) && !empty($_GET['page'])) ? (int)$_GET['page'] : 1;
$query = $__DB->select('posts');
$postsTotalCount = $__DB->num_rows($query);
$__PAG = new Pagination($page,
    $postsTotalCount
    , 30,
    'index.php?page=#i#');
if (isset($_GET['deletePost'])) {
    $id = (int)$_GET['deletePost'];
    $delete = $__DB->delete('posts', "`id` = {$id}");
    if ($delete) {
        echo $__GB->DisplayError('Item Deleted successfully', 'yes');
    }
}
if (isset($_GET['viewPost'])) {
    $id = (int)$_GET['viewPost'];
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

						WHERE P.id = {$id}
						GROUP BY P.id ORDER BY P.id DESC
					";
    $query = $__DB->query($querysql);
    if($__DB->num_rows($query) != 0) {
        $post = $__DB->fetch_object($query);
        $post->link = $__GB->getLink($post->link);
        $post->liked =
        $post->date = $__GB->TimeAgo($post->date);
        ?>
        <div class="card-panel">
            <div class="red-text text-darken-2"><a href="?deletePost=<?php echo $post->id; ?>">Delete Post</a></div>
        </div>
        <div class="card">
            <a href="users.php?viewUser=<?php echo $post->ownerID?>" target="_blank">
                <div class="row author-card valign-wrapper">
                    <div class="col s2 center-align">
                        <img src="../image/small/<?php echo $post->ownerPicture?>" alt=""
                             class="circle responsive-img user-image">
                    </div>
                    <div class="col s10 name-date-col">
                        <span class="author-name"><b> <?php echo $post->ownerUsername?></b></span><br>
                        <span class="post-date"> <?php echo $post->date?></span>
                    </div>
                </div>
            </a>
            <?php if ($post->image != null) { ?>
                <div class="card-image">';
                    <img class="materialboxed" src="../image/large/<?php echo $post->image ?>'">';

                </div>
            <?php
            }
            if ($post->status != null) { ?>
                <div class="card-content">
                    <p><?php echo $post->status ?></p>
                </div>
            <?php }
            if ($post->place != null) { ?>
                <div class="center-align" style="color:#2196F3">
                    <p><i class="small mdi-maps-place"></i><?php echo $post->place ?></p>
                </div>
            <?php }
            if ($post->link != null) {
                if ($post->link['type'] == 'youtube') { ?>
                    <div class="video-container">';
                        <iframe width="853" height="480"
                                src="https://www.youtube.com/embed/<?php echo $post->link['link'] ?>?rel=0"
                                frameborder="0" allowfullscreen></iframe>

                    </div>
                <?php } else { ?>
                    <div class="row">
                        <div class="col s12 m6">
                            <div class="card blue lighten-1">
                                <div class="card-content white-text" style="padding: 0px; ">
                                    <span class="card-title"> <?php echo $post->link['title'] ?></span>

                                    <div class="card-content"><p> <?php echo $post->link['desc'] ?></p></div>
                                    <p><img src="<?php echo $post->link['image'] ?>"></p>
                                </div>
                            </div>
                        </div>
                    </div>
                <?php }
            } ?>
        </div><!-- card//-->

    <?php
    }else{
        echo $__GB->DisplayError("Post doesnt exists");
    }
    include 'footer.php';
    exit;
}
?>
    <div class="card-panel">
        <div class="red-text text-darken-2">Recent Posts</div>
    </div>
    <table class="z-depth-1 bordered striped">
        <thead>
        <th>ID</th>
        <th>Status</th>
        <th>Image</th>
        <th>Place</th>
        <th>Link</th>
        <th>Privacy</th>
        <th>Date</th>
        <th>Options</th>
        </thead>
        <tbody>
        <?php
        $postsQuery = $__DB->select('posts', '*', '', '`id` DESC', $__PAG->limit);
        while ($post = $__DB->fetch_assoc($postsQuery)) {
            echo '<tr>';
            echo '<td>' . $post['id'] . '</td>';
            echo '<td>' . $post['status'] . '</td>';
            echo '<td>';
            if ($post['image'] != null) {
                echo '<img class="materialboxed" width="100px" src="../image/large/' . $post['image'] . '">';
            }
            echo '</td>';
            echo '<td>' . $post['place'] . '</td>';
            echo '<td>';
            if ($post['link'] != null) {
                $querLink = $__DB->select('links', '*', "`hash` = '{$post['link']}'");
                $fetchLink = $__DB->fetch_assoc($querLink);
                if ($fetchLink['type'] != 'youtube') {
                    echo '<a target="_blank" href="' . $fetchLink['link'] . '">' . $fetchLink['title'] . '</a>';
                } else {
                    echo '<a target="_blank" href="https://youtu.be/' . $fetchLink['link'] . '">' . $fetchLink['title'] . '</a>';
                }
            }
            echo '</td>';
            echo '<td>';
            if ($post['privacy'] == 1) {
                echo 'Public';
            } else {
                echo 'Private';
            }
            echo '</td>';
            echo '<td>' . $__GB->TimeAgo($post['date']) . '</td>';
            echo '<td>';
            echo '<a href="?deletePost=' . $post['id'] . '">Delete</a>';
            echo '</td>';
            echo '</tr>';
        }
        ?>
        </tbody>
    </table>

<?php
echo $__PAG->urls;
include 'footer.php'; ?>