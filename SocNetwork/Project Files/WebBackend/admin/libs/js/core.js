function ajax (url, data) {
	$.ajax({
      	url: url,
      	type: "post",
      	data: data,
  		datatype: 'json',
      success: function(data){
            return data;
      },
      error:function(){
          return null;
      }   
    }); 

}
function tt(message,time) {
	Materialize.toast(message, time);
}
function login () {
	var data = $('#loginForm').serialize();
	$.ajax({
      	url: "./login",
      	type: "post",
      	data: data,
  		datatype: 'json',
      success: function(data){
      	if (data.success != true) {
      		tt("Couldn't sign you in with those details",4000);
      	}else{
      		tt("Welcome Back :)");
      		window.location.replace("./index.php");
      	}
      },
      error:function(){
          return null;
      }   
    });
}

function postsCard (item) {

	var card = 
	'<div class="card">'+
		'<div class="row author-card valign-wrapper">'+
        '<div class="col s2 center-align">'+
              '<img src="./image/small/'+item.ownerPicture+'" alt="" class="circle responsive-img user-image">'+
            '</div>'+
            '<div class="col s10 name-date-col">'+
              '<span class="author-name"><b>'+item.ownerName+'</b></span><br>'+
              '<span class="post-date">'+item.date+'</span>'+
            '</div>'+
          '</div>';
	if(item.image != null){
	    card +='<div class="card-image">';
	    card += '<img class="materialboxed"';
	    if(item.status != null){
	    	card += ' data-caption="'+item.status+'"';
	    }
	    card += ' src="./image/large/'+item.image+'">';

	    card += '</div>';
	}
	if(item.status != null){
		card +='<div class="card-content">'+
			      '<p>'+item.status+'</p>'+
			    '</div>';
	}
	if(item.place != null){
		card +='<div class="center-align" style="color:#2196F3">'+
			      '<p><i class="small mdi-maps-place"></i>'+item.place+'</p>'+
			    '</div>';
	}
	if (item.link != null) {
		if(item.link.type == 'youtube'){
			card += '<div class="video-container">';
			card += '<iframe width="853" height="480" src="https://www.youtube.com/embed/'+item.link.link+'?rel=0" frameborder="0" allowfullscreen></iframe>';
			card += '</div>';
		}else{
			card += '<div class="row"><div class="col s12 m6"><div class="card blue lighten-1">';
			card += '<div class="card-content white-text">';
            card += '<span class="card-title">'+item.link.title+'</span>';
            card += '<div class="card-content"><p>'+item.link.desc+'</p></div>';
            card += '<p><img src="'+item.link.image+'"></p>';
            card +='</div></div></div></div>';
            eval("$('.materialboxed').materialbox();");
		}		
	}
    card +='<div class="card-action">';
    card += '<a href="javascript:void(0);" onClick="alert(555);"><i ';
    if(item.liked){
    	card += 'style="color:red;" ';
    }else{
    	card += 'style="color:black;" '
    }
    card += 'class="small mdi-action-favorite"></i></a>';
    card += '<a href=""><i style="color:black;" class="small mdi-social-share"></i></a>';
    card += '</div></div><!-- card//-->';
    return card;
}
function getPosts (page) {
	$.ajax({
      	url: "./posts/all/"+page,
      	type: "get",
  		datatype: 'json',
  		beforeSend: function(){
  			$('#loadingSpinner').show();
  		},
	      success: function(data){
	      	$('#loadingSpinner').hide();
	      	$.each(data, function(index, item){
	      		$('#postsContainer').append(postsCard(item));
	      	});
	      },
	      error:function(){
	          tt('Oops Somthing Went Wrong', 6000);
	      }   
    });	
}