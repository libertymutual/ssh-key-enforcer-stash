AJS.$( document ).ready(function() {
	AJS.$("#ssh-key-gen-button").click(function(e){
		AJS.$("#ssh-key-gen-button").hide();
		e.preventDefault();
		//JS used to submit changes on button click
		console.log(e);
		var configEndpoint = AJS.contextPath() + "/rest/enterprisessh/1.0/keys/" ;
	 jQuery.ajax({
	     url: configEndpoint,
	     type: 'POST',
		beforeSend: function (request)
		{
		  request.setRequestHeader("X-Atlassian-Token", "no-check");
		},
	     dataType: 'json',
	     async: false,
	     success: function(data) {
	         AJS.messages.success({
	            title: "Saved!",
	            body: "Make sure to download your keypair."
	         }); 
	         AJS.$("#ssh-key-rez-private").val(data.privateKey);
	         AJS.$("#ssh-key-rez-public").val(data.publicKey);
	         AJS.$("#ssh-key-rez-fingerprint").val(data.fingerprint);
	         AJS.$("#keybox").show();
	     } ,
	     error: function(data) {
	        AJS.messages.warning({
	            title: "Oh no!",
	            body: "Recieved error code: " + data.status + ", " + data.statusText
	         }); 
	     } 
	  });
	 
	});



	AJS.$("#dialog-show-link").click(function() {
        AJS.dialog2("#policy-dialog").show();

    });

});