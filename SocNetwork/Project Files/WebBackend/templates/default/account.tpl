<div class="panel panel-default">
	<div class="panel-heading">
		{__LNG.account} : {_SESSION.username}
	</div>
	<div class="panel-body">
		<!-- Nav tabs -->
		<ul class="nav nav-tabs" role="tablist">
		  <li class="active"><a href="#links" role="tab" data-toggle="tab">{__LNG.links}</a></li>
		  <li><a href="#settings" role="tab" data-toggle="tab">{__LNG.settings}</a></li>
		</ul>

		<!-- Tab panes -->
		<div class="tab-content">
		  <div class="tab-pane active" id="links">
		 <div class="text-center">
			 <label class="label label-success number">{__LNG.total_links}: {UserLinksCount}</label>
			  <label class="label label-primary number">{__LNG.total_visits}: {UsersTotalVisits}</label>
		  </div>
		  	<table id="table" class="table table-striped table-bordered">
		  	<thead>
		  		<tr>
		  			<td>{__LNG.url}</td>
		  			<td>{__LNG.total_visits}</td>
		  			<td>{__LNG.last_visit}</td>
		  			<td>{__LNG.created_date}</td>
		  			<td style="width: 87px;">{__LNG.options}</td>
		  		</tr>
		  	</thead>
		  		<LOOP NAME="{links}">
		  		<tr>
		  			<td><span class="number">{site_url}{links.hash}<br><span style="color:#939393;">{links.url}</span></span></td>
		  			<td><span class="number">{links.visits}</span></td>
		  			<td><span class="number">{links.last_visit}</span></td>
		  			<td><span class="number">{links.date}</span></td>
		  			<td>
		  				<a href="{links.hash}~s"
		  					title="{__LNG.details}"
		  					class="btn btn-info"><span class="glyphicon glyphicon-info-sign"></span></a>
		  				<a 	href="?delete={links.id}"
		  					onclick="if(!confirm('{__LNG.delete_confirm}')){ return false; }"
		  					title="{__LNG.delete}"
		  					class="btn btn-danger"><span class="glyphicon glyphicon-remove"></span></a>
		  			</td>
		  		</tr>
		  		</LOOP>
		  	</table>
		  	{pagNav}
		  </div>
		  <!-- links//-->
		  <div class="tab-pane" id="settings">
		  <div id="message" class="text-center"></div>
		  	<form id="profile-form" action="" method="post">
		  	<input name="username" type="text" class="input form-control" placeholder="{__LNG.username}" value="{user.username}">
		  	<input name="email" type="text" class="input form-control" placeholder="{__LNG.email}" value="{user.email}">
		  	<input type="submit" value="{__LNG.save}" class="btn btn-success form-control">
		  	</form>
		  	<hr>
		  	<form id="password-form" action="" method="post">
		  	<input name="current_password" type="password" class="input form-control" placeholder="{__LNG.current_password}">
		  	<input name="password" type="password" class="input form-control" placeholder="{__LNG.new_password}">
		  	<input name="password_confirm" type="password" class="input form-control" placeholder="{__LNG.new_password_confirm}">
		  	<input type="submit" value="{__LNG.change}" class="btn btn-success form-control">
		  	</form>
		  </div>
		  <!-- settings//-->
		</div>
	</div>
	<!-- paenl-body //-->
</div>