<?php
include 'header.php';
$page = (isset($_GET['page']) && !empty($_GET['page'])) ? (int)$_GET['page'] : 1;
$query =  $__DB->select('messages');
$postsTotalCount = $__DB->num_rows($query);
$__PAG = new Pagination($page,
            $postsTotalCount
            ,30,
            'messages.php?page=#i#');
if (isset($_GET['deleteMessage'])) {
	$id = (int)$_GET['deleteMessage'];
	$delete = $__DB->delete('messages',"`id` = {$id}");
	if($delete){
		echo $__GB->DisplayError('Item Deleted successfully','yes');
	}
}
?>
<div class="card-panel"><div class="red-text text-darken-2">Recent Messages</div></div>
<table class="z-depth-1 bordered striped">
	<thead>
		<th>ID</th>
		<th>Message</th>
		<th>From</th>
		<th>To</th>
		<th>Date</th>
		<th>Options</th>
	</thead>
	<tbody>
	<?php
		$usersQuery = $__DB->select('messages','*','','`id` DESC',$__PAG->limit);
		while ($fetch = $__DB->fetch_assoc($usersQuery)) {
			$userFromQuery = $__DB->select('users','*',"`id` = ".$fetch['from']);
			$fetchUserFrom = $__DB->fetch_assoc($userFromQuery);
			$userToQuery = $__DB->select('users','*',"`id` = ".$fetch['to']);
			$fetchUserTo = $__DB->fetch_assoc($userToQuery);
			echo '<tr>';
			echo '<td>'.$fetch['id'].'</td>';
			echo '<td>'.$fetch['message'].'</td>';
			echo '<td><a target="_blank" href="users.php?viewUser='.$fetch['id'].'">'.$fetchUserFrom['username'].'</a></td>';
			echo '<td><a target="_blank" href="users.php?viewUser='.$fetch['id'].'">'.$fetchUserTo['username'].'</a></td>';
			echo '<td>'.$__GB->TimeAgo($fetch['date']).'</td>';
			echo '<td>';
				echo '<a href="?deleteMessage='.$fetch['id'].'">Delete</a>';
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