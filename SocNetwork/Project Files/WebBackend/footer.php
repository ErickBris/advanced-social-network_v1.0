<footer class="page-footer" style="padding-top:0px;">
  <div class="footer-copyright">
    <div class="container">
    © <?php echo date("Y");?> Copyright 
    </div>
  </div>
</footer>

	<script type="text/javascript" src="https://code.jquery.com/jquery-2.1.1.min.js"></script>
      <script type="text/javascript" src="libs/js/materialize.min.js"></script>
      <?php if(isset($_SESSION['userID'])){ ?>
      <script type="text/javascript" src="libs/js/core.js"></script>
      <script type="text/javascript">
      	function getFile(){
		        document.getElementById("imageFile").click();
		        return false;
		    }
        $(document).ready(function(){
          
          var page = 1;
          var totalPages = <?php echo $__PAG->pages;?>;
          $(window).scroll(function(){
            if ((window.innerHeight + window.scrollY) >= document.body.offsetHeight) {
              page = page + 1
              if(page <= totalPages){
                getPosts(page);
              }
            }
          });
          getPosts(1);
        });
      </script>
      <?php } ?>
    </body>
  </html>
