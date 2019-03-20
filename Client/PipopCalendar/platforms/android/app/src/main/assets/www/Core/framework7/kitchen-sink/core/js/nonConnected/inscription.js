
$(document).ready(function(){
  $("#inscriptionBouton").click(function(e){
    app.input.checkEmptyState("#emailInscripInput");
    app.input.checkEmptyState("#mdpInscripInput");
    app.input.checkEmptyState("#nomInscripInput");
    app.input.checkEmptyState("#prenomInscripInput");
    if(!$("#emailInscripInput").val() &&  !$("#mdpInscripInput").val() && !$("#nomInscripInput").val() && !$("#prenomInscripInput").val()){
           window.plugins.toast.showWithOptions({
                  message: "Informations incomplètes",
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
            inscription($("#emailInscripInput").val(), $("#mdpInscripInput").val(), $("#nomInscripInput").val(), $("#prenomInscripInput").val());
        }
        e.preventDefault();
  });

  function inscription(email, mdp, nom, prenom){
      var crypMdp =  new jsSHA("SHA-512", "TEXT");
      crypMdp.update(mdp);
      var hash = crypMdp.getHash("HEX");
      // Ne pas oublier de vérifier les données
      var arr = {"Request":"SignUp", "Email": email, "Mdp": hash, "Nom": nom, "Prenom": prenom};
      $.ajax({
          url: adresse,
          type: 'POST',
          data: JSON.stringify(arr),
          dataType: 'text',
          timeout: 512,
          async: false,
          success: function(data, textStatus, jqXHR) {
              app.preloader.hide();
              let obj = JSON.parse(data);
              switch(obj["Result"]){
                case "0":
                {
                  window.location = "user-home.html";
                  localStorage.setItem("emailUtilisateur",$("#emailInscripInput").val());
                  break;
                }
                default:
                {
                  localStorage.setItem("emailUtilisateur","");
                  window.plugins.toast.showWithOptions({
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
                  );
                  // Message d'erreur : autre
                  break;
                }
              }
          },
          error: function(jqXHR, textStatus, errorThrown) {
            window.plugins.toast.showWithOptions({
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
            });
          }
      });
  }

});
