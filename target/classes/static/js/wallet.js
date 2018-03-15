$( document ).ready(function() {

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
	
	jQuery("#buylumen").click(function() {
		$.ajax({
	    url : '/api/stellar/generateLumen/'+jQuery("#accountId").val(),
	          method: "GET",
	          async: true,
	          success : function(data) {
				location.reload();
	          }
	   });
	});
	
	
	jQuery("#saveAsset").click(function() {
		var assetCode = jQuery("#assetCode").val();
		var limit = jQuery("#limit").val();
		var balance = jQuery("#balance").val();
		$.ajax({
	    url : '/api/stellar/generateAsset/'+assetCode+'/'+limit+'/'+balance,
	          method: "GET",
	          async: true,
	          success : function(data) {
				location.reload();
	          }
	   });
	});
	
	jQuery(".sendAsset").click(function() { 
		var assetCode = jQuery(this).parent().parent().find("#accountCodeHidden").val();
		console.log(assetCode);
		if (assetCode == "") {
			assetCode = "LUMENS";
		}
		jQuery(".tokenName").html(assetCode);
	});
	
	
	jQuery("#sendPayment").click(function() {
		var recipeint = jQuery("#recipient").val();
		var amount = jQuery("#amount").val();
		var transactionMemo = jQuery("#memo").val();
		var accountId = jQuery("#accountId").val();
		var sourceKey = getCookie(accountId);
		var assetCode = jQuery(".tokenName").text();
		console.log("accountId===" + accountId);
		console.log("recipeint===" + recipeint);
		console.log("amount===" + amount);
		console.log("transactionMemo===" + transactionMemo);
		console.log("assetCode==="  + assetCode);
		console.log("sourceKey===" + sourceKey);
		console.log("assetCode===" + assetCode);
		
		
		$.ajax({
	    url : '/api/stellar/sendPayment/'+assetCode+'/'+sourceKey+'/'+recipeint+'/'+amount+'/'+transactionMemo,
	          method: "POST",
	          async: true,
	          success : function(data) {
				location.reload();
	          }
	   }); 
		
	});
	 
	
	jQuery( ".asset-box" ).hover(function() {
	  jQuery(this).removeClass("asset-box-white");
	});
	
	
	$( ".asset-box" )
	  .mouseout(function() {
	    jQuery(this).addClass("asset-box-white");
	  })
	  .mouseover(function() {
	    jQuery(this).removeClass("asset-box-white");
	});



});
 


