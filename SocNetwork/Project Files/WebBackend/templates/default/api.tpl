<div class="panel panel-default smallP">
	<div class="panel-heading">
		{__LNG.api}
	</div>
	<div class="panel-body">
		<label>{__LNG.token} :</label>
		<pre>{token}</pre>
		<label>{__LNG.example} :</label>
		<pre>{site_url}api.php?token=[{__LNG.token}]&url=[{__LNG.url}]</pre>
		<pre>{site_url}api.php?token=<label>{token}</label>&url=<label>http://google.com/</label></pre>
		<label>{__LNG.results} :</label>
		<pre class="number">{
"status":200
"details":{
	"url":"{site_url}HjSHk",
	"stats":"{site_url}HjSHk~s",
	"qr":"{site_url}HjSHk~q"
	}
}</pre>
	<label>{__LNG.status_codes} :</label>
	<ul>
		<li><label class="label label-success"><span class="number">200:</span></label> {__LNG.shortned_successfully}.</li>
		<li><label class="label label-danger"><span class="number">201:</span></label> {__LNG.account_blocked}.</li>
		<li><label class="label label-warning"><span class="number">202:</span></label> {__LNG.inactivate_account}.</li>
		<li><label class="label label-info"><span class="number">203:</span></label> {__LNG.blacklisted_url}.</li>
		<li><label class="label label-primary"><span class="number">204:</span></label> {__LNG.unknown_error}.</li>
	</ul>
	</div>
</div>