$(document).ready(function(){

  $("#connexionBouton").click(function(e){
    //e.preventDefault();
    //alert("Click!");
    app.input.checkEmptyState("#emailInput");
    app.input.checkEmptyState("mdpInput");
    if(!$("#emailInput").val() &&  !$("#mdpInput").val()){
       window.plugins.toast.showWithOptions({
              message: obj["Message"],
              duration: 1500, // ms
              position: "bottom",
              addPixelsY: -40,  // (optional) added a negative value to move it up a bit (default 0)
              styling: {
                    opacity: 0.75, // 0.0 (transparent) to 1.0 (opaque). Default 0.8
                    backgroundColor: '#FF0000', // make sure you use #RRGGBB. Default #333333
                    textSize: 12, // Default is approx. 13.
                    cornerRadius: 16, // minimum is 0 (square). iOS default 20, Android default 100
                    horizontalPadding: 20, // iOS default 16, Android default 50
                    verticalPadding: 16 // iOS default 12, Android default 30
                  }
            }
         );
    }else{

      app.preloader.show();
      connexion($("#emailInput").val(), $("#mdpInput").val());
    }
    e.preventDefault();
  });

  function connexion(email, mdp){
      var crypMdp =  new jsSHA("SHA-512", "TEXT");
      crypMdp.update(mdp);
      var hash = crypMdp.getHash("HEX");
      var arr = {"Request":"SignIn","Email":email, "Mdp":hash};
      console.log("JSON : "+JSON.stringify(arr));
      $.ajax({
          url: adresse,
          type: 'GET',
          data: JSON.stringify(arr),
          dataType: 'text',
          timeout:5000,
          success: function(data, textStatus, jqXHR) {
              app.preloader.hide();
              var obj = JSON.parse(data);
              console.log("BUG : data : "+obj["Message"]);
              if(obj["Result"]==0){
//                app.views.main.router.navigate("/user-home/");
                localStorage.setItem("emailUtilisateur",$("#emailInput").val());
                localStorage.setItem("mdpUtilisateur", hash);
                window.location = "user-home.html";
              }else{
              localStorage.setItem("emailUtilisateur","");
                window.plugins.toast.showWithOptions(
                {
                   message: ""+obj["Message"],
                   duration: 1500, // ms
                   position: "bottom",
                   addPixelsY: -40,  // (optional) added a negative value to move it up a bit (default 0)
                   styling: {
                     opacity: 0.75, // 0.0 (transparent) to 1.0 (opaque). Default 0.8
                     backgroundColor: '#FF0000', // make sure you use #RRGGBB. Default #333333
                     textSize: 12, // Default is approx. 13.
                     cornerRadius: 16, // minimum is 0 (square). iOS default 20, Android default 100
                     horizontalPadding: 22, // iOS default 16, Android default 50
                     verticalPadding: 20 // iOS default 12, Android default 30
                   }
                  }
                 );//
                 $("#connexionErrMsg").empty();
                 $("#connexionErrMsg").append("Connexion error. Please, check your login information.");
                 $("#emailInput").parents('li').addClass('item-input-invalid');
                 $("#mdpInput").parents('li').addClass('item-input-invalid');
                }
          },
          error: function(jqXHR, textStatus, errorThrown) {
              app.preloader.hide();
              localStorage.setItem("emailUtilisateur","");
              localStorage.setItem("mdpUtilisateur","");
              window.plugins.toast.showWithOptions(
                  {
                    message: "No network connection or server error",
                    duration: 1500, // ms
                    position: "bottom",
                    addPixelsY: -40,  // (optional) added a negative value to move it up a bit (default 0)
                    styling: {
                          opacity: 0.75, // 0.0 (transparent) to 1.0 (opaque). Default 0.8
                          backgroundColor: '#FF0000', // make sure you use #RRGGBB. Default #333333
                          textSize: 12, // Default is approx. 13.
                          cornerRadius: 16, // minimum is 0 (square). iOS default 20, Android default 100
                          horizontalPadding: 20, // iOS default 16, Android default 50
                          verticalPadding: 16 // iOS default 12, Android default 30
                        }
                  }
               );
          }
      });
  }

});
