<div class="panel panel-default smallP">
	<div class="panel-heading">
		{__LNG.please_wait}
	</div>
	<div class="panel-body text-center">
		<div>{__LNG.you_well_redirect_to}: <span class="label label-info">{url.url} </span></div><hr>
		<label id="timer" class="label label-warning"><span class="number">{time}</span></label>
	</div>
</div>
<meta http-equiv="refresh" content="{time}; url={url.url}">