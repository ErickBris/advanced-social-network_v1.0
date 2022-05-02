<?php
include 'header.php';
$page = (isset($_GET['page']) && !empty($_GET['page'])) ? (int)$_GET['page'] : 1;
$query = $__DB->select('users');
$postsTotalCount = $__DB->num_rows($query);
$__PAG = new Pagination($page,
    $postsTotalCount
    , 30,
    'users.php?page=#i#');
if (isset($_GET['deleteUser'])) {
    $id = (int)$_GET['deleteUser'];
    $delete = $__DB->delete('users', "`id` = {$id}");
    $__DB->delete('posts', "`ownerID` = {$id}");
    $__DB->delete('comments', "`from` = {$id}");
    $__DB->delete('follows', "`to` = {$id}");
    $__DB->delete('follows', "`from` = {$id}");
    $__DB->delete('likes', "`from` = {$id}");
    if ($delete) {
        echo $__GB->DisplayError('Item Deleted successfully', 'yes');
    }
}
if (isset($_GET['viewUser'])) {
    $id = (int)$_GET['viewUser'];
    $query = $__DB->select('users','*',"`id` = {$id}");
    if($__DB->num_rows($query) != 0){
        $user = $__DB->fetch_object($query);
        ?>
        <div class="card-panel">
            <div class="red-text text-darken-2"><a href="?deleteUser=<?php echo $user->id; ?>">Delete User</a></div>
        </div>
        <div class="card">
            <div class="row author-card valign-wrapper">
                <div class="col s2 center-align">
                    <img src="../image/small/<?php echo $user->picture?>" alt=""
                         class="circle responsive-img user-image">
                </div>
                <div class="col s10 name-date-col">
                    <span class="author-name"><b> <?php echo $user->username?></b></span><br>
                    <span class="post-date"> <?php echo $user->email?></span>
                </div>
            </div>
        </div>

    <?php
    }else{
        echo $__GB->DisplayError("User doesnt exists");
    }
    include 'footer.php';
    exit;
}
?>
    <div class="card-panel">
        <div class="red-text text-darken-2">Recent Users</div>
    </div>
    <table class="z-depth-1 bordered striped">
        <thead>
        <th>ID</th>
        <th>Username</th>
        <th>Picture</th>
        <th>Name</th>
        <th>Job</th>
        <th>Address</th>
        <th>E-mail</th>
        <th>Date</th>
        <th>Options</th>
        </thead>
        <tbody>
        <?php
        $usersQuery = $__DB->select('users', '*', '', '`id` DESC', $__PAG->limit);
        while ($user = $__DB->fetch_assoc($usersQuery)) {
            echo '<tr>';
            echo '<td>' . $user['id'] . '</td>';

            echo '<td>' . $user['username'] . '</td>';
            echo '<td>';
            if ($user['picture'] != null) {
                echo '<img class="materialboxed" width="100px" src="../image/small/' . $user['picture'] . '">';
            }
            echo '</td>';
            echo '<td>' . $user['name'] . '</td>';
            echo '<td>' . $user['job'] . '</td>';
            echo '<td>' . $user['address'] . '</td>';
            echo '<td>' . $user['email'] . '</td>';
            echo '<td>' . $__GB->TimeAgo($user['date']) . '</td>';
            echo '<td>';
            echo '<a href="?deleteUser=' . $user['id'] . '">Delete</a>';
            echo '</td>';
            echo '</tr>';
        }
        ?>
        </tbody>
    </table>
<?php
echo $__PAG->urls;
include 'footer.php';
?>