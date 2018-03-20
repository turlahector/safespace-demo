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
		var secretCode = {"secretCode" : jQuery("#secretCode").val(),"limit" : jQuery("#limitSendPayment").val()};
		
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
		var secretCode = getCookie(accountId);
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

	jQuery("#processOffer").click(function() {
		var sourceSecretKey = jQuery("#secretKey").val();
		var assetCodeSell = $.trim($("#dd_sellButton").text());
		var assetCodeBuy =  $.trim($("#dd_buyButton").text());
		var assetAmountSell = jQuery("#amountSell").val();
		var assetAmountBuy = jQuery("#amountBuy").val();
		var transactionMemo = jQuery("#memo").val();
		console.log(sourceSecretKey);
		console.log(assetCodeSell);
		console.log(assetCodeBuy);
		console.log(assetAmountSell);
		console.log(assetAmountBuy);
		console.log(transactionMemo);
		
		$.ajax({
	    url : '/api/stellar/createOffer/'+sourceSecretKey+'/'+assetCodeSell+'/'+assetAmountSell+'/'+assetCodeBuy+'/'+assetAmountBuy+'/'+transactionMemo,
	          method: "POST",
	          contentType : 'application/json', 
	          async: true,
	          success : function(data) {
				location.reload();
	          }
	   }); 
	   
	});
	
	jQuery("#processOfferConsumer").click(function() {
		var accountId = jQuery("#accountId").val();
		var sourceSecretKey = getCookie(accountId);
		var assetCodeSell = $.trim($("#dd_sellButton").text());
		var assetCodeBuy =  $.trim($("#dd_buyButton").text());
		var assetAmountSell = jQuery("#amountSell").val();
		var assetAmountBuy = jQuery("#amountBuy").val();
		var transactionMemo = jQuery("#memo").val();
		console.log(sourceSecretKey);
		console.log(assetCodeSell);
		console.log(assetCodeBuy);
		console.log(assetAmountSell);
		console.log(assetAmountBuy);
		console.log(transactionMemo);
		
		$.ajax({
	    url : '/api/stellar/createOffer/'+sourceSecretKey+'/'+assetCodeSell+'/'+assetAmountSell+'/'+assetCodeBuy+'/'+assetAmountBuy+'/'+transactionMemo,
	          method: "POST",
	          contentType : 'application/json', 
	          async: true,
	          success : function(data) {
				location.reload();
	          }
	   }); 
	   
	});
	
	jQuery("#buyAssets li").click(function() {
		$("#dd_buyButton").text($(this).text());
	});
	
	jQuery("#sellAssets li").click(function() {
		$("#dd_sellButton").text($(this).text());
		
	});
	
	jQuery("#sellOfferAsset li").click(function() {
		$("#dd_sellerOffer").text($(this).text());
		location.href = "/admin/exchange/"+jQuery.trim($("#dd_sellerOffer").text())+"/"+jQuery.trim($("#dd_buyerOffer").text()) ;
		
	});
	
	jQuery("#buyOfferAssets li").click(function() {
		$("#dd_buyerOffer").text($(this).text());
		location.href = "/admin/exchange/"+jQuery.trim($("#dd_sellerOffer").text())+"/"+jQuery.trim($("#dd_buyerOffer").text()) ;
		
	});
	setExchangeDefaults();
	jQuery("#sellOfferAssetClient li").click(function() {
		var accountId = jQuery("#accountId").val();
		$("#dd_sellerOfferClient").text($(this).text());
		location.href = "/exchange/"+jQuery.trim(accountId)+"/"+jQuery.trim($("#dd_sellerOfferClient").text())+"/"+jQuery.trim($("#dd_buyerOfferClient").text()) ;
			
	});
	setExchangeDefaultsClient();		
	jQuery("#buyOfferAssetsClient li").click(function() {
		var accountId = jQuery("#accountId").val();
		$("#dd_buyerOfferClient").text($(this).text());
		location.href = "/exchange/"+jQuery.trim(accountId)+"/"+jQuery.trim($("#dd_sellerOfferClient").text())+"/"+jQuery.trim($("#dd_buyerOfferClient").text()) ;
				
	});
});

function setExchangeDefaults(){
	var url = window.location.href;
	var arr=url.split('/');
	for(var i = 0;i < arr.length;i++){
		try{
			if(arr[i]==='admin' && arr[i+1]==='exchange'){
				$("#dd_sellerOffer").text(arr[5]);				
				$("#dd_buyerOffer").text(arr[6]);
			}
		} catch(err){
			
		}
	}
}

function setExchangeDefaultsClient(){
	var url = window.location.href;
	var arr=url.split('/');
	for(var i = 0;i < arr.length;i++){
		try{
			if(arr[i]==='exchange'){
				$("#dd_sellerOfferClient").text(arr[5]);				
				$("#dd_buyerOfferClient").text(arr[6]);
			}
		} catch(err){
			
		}
	}
}
 


