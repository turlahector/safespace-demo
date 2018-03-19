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
		var secretCode = {"secretCode" : jQuery("#secretCode").val()};
		console.log("accountId===" + accountId);
		console.log("recipeint===" + recipeint);
		console.log("amount===" + amount);
		console.log("transactionMemo===" + transactionMemo);
		console.log("assetCode==="  + assetCode);
		console.log("sourceKey===" + sourceKey);
		console.log("secretCode===" + secretCode);
		
		
		$.ajax({
	    url : '/api/stellar/sendPayment/'+assetCode+'/'+sourceKey+'/'+recipeint+'/'+amount+'/'+transactionMemo,
	          method: "POST",
	          data: JSON.stringify(secretCode),
	          contentType : 'application/json', 
	          async: true,
	          success : function(data) {
				location.reload();
	          }
	   }); 
		
	});
	
	jQuery("#sendPaymentWallet").click(function() {
		var recipient = jQuery("#recipient").val();
		var amount = jQuery("#amount").val();
		var transactionMemo = jQuery("#memo").val();
		var accountId = jQuery("#accountId").val();		
		var assetCode = jQuery(".tokenName").text();
		var secretCode = jQuery("#secretCode").val();
		var jsonData = {"amount" : amount , "transactionMemo" :transactionMemo ,"accountId": accountId, "recipient" : recipient,"assetCode":assetCode,"secretCode":secretCode };
		console.log("accountId===" + accountId);
		console.log("recipeint===" + recipient);
		console.log("amount===" + amount);
		console.log("transactionMemo===" + transactionMemo);
		console.log("assetCode==="  + assetCode);
		console.log("secretCode===" + jsonData);
		
		
		$.ajax({
	    url : '/api/stellar/sendPaymentWallet',
	          method: "POST",
	          data: JSON.stringify(jsonData),
	          contentType : 'application/json', 
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
 


