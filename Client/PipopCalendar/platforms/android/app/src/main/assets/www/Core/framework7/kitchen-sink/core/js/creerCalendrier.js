$(document).ready(function(){

  if(localStorage.getItem("colorSelectForm")==""){
      localStorage.setItem("colorSelectForm","Black");
  }

   $("#creationCalendrierBouton").click(function(e){
     creerCalendrier($("#nomCalendrierForm").val(),$("#descriptionCalendrierForm").val(),localStorage.getItem("colorSelectForm"),"art",localStorage.getItem("emailUtilisateur"));
   });

  function creerCalendrier(nom, description, couleur, theme, email){
      // Il faut crypter les données
      // Il faut vérifier les données
      var arr = {"Request":"CreateCalendar","Nom":nom, "Description": description,"Couleur":couleur, "Theme":theme, "Auteur":email};
      console.log("JSON : "+JSON.stringify(arr));
      app.preloader.show();
      $.ajax({
          url: 'http://10.0.2.2:3307',
          type: 'GET',
          data: JSON.stringify(arr),
          dataType: 'text',
          timeout: 512,
          async: true,
          success: function(data, textStatus, jqXHR) {
              app.preloader.hide();
              var obj = JSON.parse(data);
              if(obj["RESULT"]==0){

              }else{
                window.plugins.toast.showWithOptions(
                {
                   message: ""+obj["MESSAGE"],
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
                }
          },
          error: function(jqXHR, textStatus, errorThrown) {
              console.log("ERREUR : "+jqXHR);
              console.log("ERREUR : "+textStatus);
              console.log("ERREUR : "+errorThrown);
              app.preloader.hide();
              window.plugins.toast.showWithOptions(
                  {
                    message: ""+obj["MESSAGE"],
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
