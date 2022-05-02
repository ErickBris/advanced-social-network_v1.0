<?php
include 'header.php';
$page = (isset($_GET['page']) && !empty($_GET['page'])) ? (int)$_GET['page'] : 1;
$query =  $__DB->select('reports');
$postsTotalCount = $__DB->num_rows($query);
$__PAG = new Pagination($page,
            $postsTotalCount
            ,30,
            'reports.php?page=#i#');
if (isset($_GET['deleteReport'])) {
	$id = (int)$_GET['deleteReport'];
	$delete = $__DB->delete('reports',"`id` = {$id}");
	if($delete){
		echo $__GB->DisplayError('Item Deleted successfully','yes');
	}
}
?>
<div class="card-panel"><div class="red-text text-darken-2">Recent Messages</div></div>
<table class="z-depth-1 bordered striped">
	<thead>
		<th>ID</th>
		<th>Post</th>
		<th>Reporter</th>
		<th>Options</th>
	</thead>
	<tbody>
	<?php
		$usersQuery = $__DB->select('reports','*','','`id` DESC',$__PAG->limit);
		while ($fetch = $__DB->fetch_assoc($usersQuery)) {
			$userFromQuery = $__DB->select('users','*',"`id` = ".$fetch['reporterID']);
			$fetchUserFrom = $__DB->fetch_assoc($userFromQuery);
			echo '<tr>';
			echo '<td>'.$fetch['id'].'</td>';
			echo '<td><a href="index.php?viewPost='.$fetch['postID'].'">View Post</a></td>';
			echo '<td><a target="_blank" href="users.php?viewUser='.$fetch['id'].'">'.$fetchUserFrom['username'].'</a></td>';
			echo '<td>';
				echo '<a href="?deleteReport='.$fetch['id'].'">Delete</a>';
			echo'</td>';
			echo '</tr>';
		}
	?>
	</tbody>
</table>
<?php
echo $__PAG->urls;
include 'footer.php';
?>