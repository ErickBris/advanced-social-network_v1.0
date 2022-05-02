<?php
include 'header.php';
if (isset($_POST['disclaimer'])) {
    $disclaimer = $__DB->escape_string($_POST['disclaimer']);
    $siteName = $__DB->escape_string($_POST['site_name']);
    $siteUrl = $__DB->escape_string($_POST['site_url']);
    $apikey = $__DB->escape_string($_POST['googleApiConfig']);
    if (isset($_POST['email']) && $_POST['email'] == 'on') {
        $emailactivation = 1;
    } else {
        $emailactivation = 0;
    }
    $__GB->updateConfig('emailactivation', $emailactivation, 'users');
    $__GB->updateConfig('site_name', $siteName, 'site');
    $__GB->updateConfig('url', $siteUrl, 'site');

    $__GB->updateConfig('disclaimer', $disclaimer, 'site');
    $__GB->updateConfig('googleApiConfig', $apikey, 'site');
    echo $__GB->DisplayError('Disclaimer updated successfully', 'yes');

}
?>
    <div class="card-panel">
        <div class="red-text text-darken-2">Site & Application</div>
    </div>
    <table class="z-depth-1 bordered striped">
        <thead>
        <th>Property</th>
        <th>Value</th>
        </thead>
        <tbody>
        <form action="settings.php" method="post">
            <tr>
                <td>Site Title</td>
                <td><input type="text" name="site_name"
                           value="<?php echo htmlentities($__GB->getConfig('site_name', 'site')); ?>"></td>
            </tr>
            <tr>
                <td>Google API Key</td>
                <td><input type="text" name="googleApiConfig"
                           value="<?php echo htmlentities($__GB->getConfig('googleApiConfig', 'site')); ?>"></td>
            </tr>
            <tr>
                <td>Site Url</td>
                <td><input type="text" name="site_url"
                           value="<?php echo htmlentities($__GB->getConfig('url', 'site')); ?>"></td>
            </tr>
            <tr>
                <td>App Users Need Accounts Activation</td>
                <td>
                    <!-- Switch -->
                    <div class="switch">
                        <label>
                            Off
                            <?php
                            if ($__GB->getConfig('emailactivation', 'users') == 1) {
                                echo '<input type="checkbox" name="email" checked>';
                            } else {
                                echo '<input type="checkbox" name="email" >';

                            }
                            ?>
                            <span class="lever"></span>
                            On
                        </label>
                    </div>
                </td>
            </tr>
            <tr>
                <td>Disclaimer</td>
                <td><textarea style="
height: 300px;" name="disclaimer"
                              class="materialize-textarea"><?php echo $__GB->getConfig('disclaimer', 'site'); ?></textarea>
                </td>
            </tr>
            <tr>
                <td colspan="2">
                    <input type="submit" class="btn" value="Save">
                </td>
            </tr>
        </form>
        </tbody>
    </table>
<?php
include 'footer.php';
?>