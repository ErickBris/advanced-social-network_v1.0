<div class="panel panel-default smallP">
	<div class="panel-heading">
	{__LNG.shorten}
	</div>
	<div class="panel-body">
		<div id="message" class="text-center"></div>
		<form id="shorten-form" action="" method="post">
			<textarea class="form-control" name="url" placeholder="http://"></textarea>
			<IF NAME="{hash} neq 0">
				<input type="text" class="input form-control" value="{hash}" name="hash" placeholder="{__LNG.hash}" maxlength="{hash_num}">
			</IF>
			<IF NAME="{captcha} neq 0">
				<div class="label label-info captcha"><span class="cp">{__LNG.captcha}: </span>{captchaCode}</div>
				<input type="text" class="input form-control" name="captcha" placeholder="{__LNG.captcha}">
			</IF>
			<input type="submit" class="btn btn-success form-control" value="{__LNG.shorten}">
		</form>
	</div>
</div>