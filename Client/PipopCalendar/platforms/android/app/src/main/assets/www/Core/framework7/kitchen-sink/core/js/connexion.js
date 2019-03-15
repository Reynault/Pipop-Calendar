$(document).ready(function(){
  $("#connexionBouton").click(function(e){
    //e.preventDefault();
    //alert("Click!");
    app.input.checkEmptyState("#emailInput");
    app.input.checkEmptyState("mdpInput");
    if(!$("#emailInput").val() &&  !$("#mdpInput").val()){
       window.plugins.toast.showWithOptions({
              message: "Informations érronées",
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
            },
            // implement the success callback
            function(result) {
              if (result && result.event) {
                console.log("The toast was tapped or got hidden, see the value of result.event");
                console.log("Event: " + result.event); // "touch" when the toast was touched by the user or "hide" when the toast geot hidden
                console.log("Message: " + result.message); // will be equal to the message you passed in

                if (result.event === 'hide') {
                  console.log("The toast has been shown");
                }
              }
            }
         );
    }else{
      connexion($("#emailInput").val(), $("#mdpInput").val());
    }
    e.preventDefault();
  });

  function connexion(email, mdp){
      // Il faut crypter les données
      // Il faut vérifier les données
      var crypMdp =  new jsSHA("SHA-512", "TEXT");
      crypMdp.update(mdp);
      var hash = crypMdp.getHash("HEX");
      var arr = {"Request":"SignIn","Email":email, "Mdp":hash};
      console.log("JSON : "+JSON.stringify(arr));
      app.preloader.show('multi');
      $.ajax({
          url: 'http://10.0.2.2:3307',
          type: 'GET',
          data: JSON.stringify(arr),
          dataType: 'text',
          timeout: 512,
          async: false,
          success: function(data, textStatus, jqXHR) {
              app.preloader.hide();
              var obj = JSON.parse(data);
              console.log("data : "+obj["Result"]);
              if(obj["Result"]==0){
                window.location = "user-home.html";
                localStorage.setItem("emailUtilisateur",$("#emailInput").val());
              }else{
              localStorage.setItem("emailUtilisateur","");
                window.plugins.toast.showWithOptions(
                {
                   message: "Connexion Echouée",
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
              console.log("ERREUR : "+jqXHR);
              console.log("ERREUR : "+textStatus);
              console.log("ERREUR : "+errorThrown);
              app.preloader.hide();
              localStorage.setItem("emailUtilisateur","");
              window.plugins.toast.showWithOptions(
                  {
                    message: "Connexion échoué",
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
                  },
                  // implement the success callback
                  function(result) {
                    if (result && result.event) {
                      console.log("The toast was tapped or got hidden, see the value of result.event");
                      console.log("Event: " + result.event); // "touch" when the toast was touched by the user or "hide" when the toast geot hidden
                      console.log("Message: " + result.message); // will be equal to the message you passed in

                      if (result.event === 'hide') {
                        console.log("The toast has been shown");
                      }
                    }
                  }
               );
          }
      });
  }

});
