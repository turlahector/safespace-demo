# Ajax-Aws-Signature-V4
Generates an Authorization Header (AWS Signature Version 4)  for a given Ajax request


##From the [AWS-Docs](http://docs.aws.amazon.com/general/latest/gr/signing_aws_api_requests.html),  26 August 2016:

<b>When Do You Need to Sign Requests?</b>

When you write custom code to send HTTP requests to AWS, you need to include code to sign the requests. You might do this for the following reasons:

- You are working with a programming language for which there is no AWS SDK.
- You want complete control over how a request is sent to AWS.

## Dependencies:

- [JQuery Ajax] (http://jquery.com/)
- [crypto-js](https://code.google.com/archive/p/crypto-js/) `sha256-min.js` and `hmac-sha256.js` 

##Usage:

Normally you would build an Ajax-Request like that:


    var params = {
        url: "https://<your-api-url>.execute-api.eu-west-1.amazonaws.com/<>",
        method: 'POST',
        data: JSON.stringify({username: "Emmanuel", items: ["Windsurf", "Sailing"]})
    };
    
    $.ajax(params).done(function( data, textStatus, jqXHR) {
                    console.log(data);
                    console.log(textStatus);
                }).fail(function(xhr, status, ex) {
                    console.log(xhr);
                    alert(ex);
                });
                
This code is a wrapper around `$.ajax`. Addtitional to the arguments, you provide the `awsCredentials` as well as the `serviceName = 'execute-api'`  you are trying to call:

    var awsCredentials = {
        region: '<your-aws-region>',
        accessKeyId: '<your-access-key-id>',
        secretAccessKey: '<your-secret-access-key>'
    };
    
    new AuthenticatedAWSAjaxRequest(awsCredentials, 'execute-api', params)
                .done(function( data, textStatus, jqXHR) {
                    console.log(data);
                    console.log(textStatus);
                }).fail(function(xhr, status, ex) {
                    console.log(xhr);
                    alert(ex);
                });


See [demo.html](https://github.com/emmanuelmillionaer/Ajax-Aws-Signature-V4/blob/master/demo.html) for a complete example.
