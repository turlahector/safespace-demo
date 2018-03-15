
jQuery("#login").click(function() {
	var username = getCookie(jQuery('.login-input').val());
	if(username != "") {
		location.href = "/wallet/"+ jQuery('.login-input').val();
		console.log(username);
	}else {
		alert("INVALID account");
		
	}
});

jQuery( ".side-links" ).click(function() {
	jQuery(".side-links").removeClass("side-links-active");
 	if (jQuery(this).hasClass("new-account"))  {
 		jQuery(this).addClass("side-links-active");
 		jQuery(".create-account").show();
 		jQuery(".access-account").hide();
 	}else {
 		jQuery(this).addClass("side-links-active");
 		jQuery(".access-account").show();
 		jQuery(".create-account").hide();
 	}
});

jQuery( "#generateKey" ).click(function() {
	$.ajax({
    url : '/api/stellar/generateKeypair',
          method: "GET",
          async: true,
          success : function(data) {
			jQuery(".keysform").show();
			jQuery(".publicKey").html(data.keypair.publicKey);
			jQuery(".secretKey").html(data.keypair.secretKey);
            console.log(data.keypair);
     		setCookie(data.keypair.publicKey,data.keypair.secretKey,30);
          }
   });
});




function setCookie(cname, cvalue, exdays) {
    var d = new Date();
    d.setTime(d.getTime() + (exdays*24*60*60*1000));
    var expires = "expires="+ d.toUTCString();
    document.cookie = cname + "=" + cvalue + ";" + expires + ";path=/";
}

function getCookie(cname) {
    var name = cname + "=";
    var decodedCookie = decodeURIComponent(document.cookie);
    var ca = decodedCookie.split(';');
    for(var i = 0; i <ca.length; i++) {
        var c = ca[i];
        while (c.charAt(0) == ' ') {
            c = c.substring(1);
        }
		console.log(c.indexOf(name));
        if (c.indexOf(name) == 0) {
            return c.substring(name.length, c.length);
        }
    }
    return "";
}


