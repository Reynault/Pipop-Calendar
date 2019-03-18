$(document).ready(function(){

  if(localStorage.getItem("colorSelectForm")===null){
      localStorage.setItem("colorSelectForm","black");
  }
   $("#creationCalendrierBouton").click(function(e){
     creerCalendrier($("#nomCalendrierForm").val(),$("#descriptionCalendrierForm").val(),localStorage.getItem("colorSelectForm"),"art",localStorage.getItem("emailUtilisateur"));
   });

  function creerCalendrier(nom, description, couleur, theme, email){
      var smartSelect = app.smartSelect.get('.smart-select');
      var arr = {"Request":"CreateCalendar","Nom":nom, "Description": description,"Couleur":smartSelect.getValue(), "Theme":theme, "Auteur":email};
      console.log("JSON : "+JSON.stringify(arr));
      app.preloader.show();
      $.ajax({
          url: 'http://10.0.2.2:3307',
          type: 'GET',
          data: JSON.stringify(arr),
          dataType: 'text',
          async: true,
          success: function(data, textStatus, jqXHR) {
          console.log(data);
              app.preloader.hide();
              var obj = JSON.parse(data);
              if(obj["Result"]==0){
                  app.views.main.router.back( "user-home.html" , {reloadPrevious: true, ignoreCache: true, reload: true} );
                  window.plugins.toast.showWithOptions({
                    message: ""+obj["Message"],
                    duration: 1500, // ms
                    position: "bottom",
                    addPixelsY: -40,  // (optional) added a negative value to move it up a bit (default 0)
                    styling: {
                      opacity: 0.75, // 0.0 (transparent) to 1.0 (opaque). Default 0.8
                      backgroundColor: '#00FF00', // make sure you use #RRGGBB. Default #333333
                      textSize: 12, // Default is approx. 13.
                      cornerRadius: 16, // minimum is 0 (square). iOS default 20, Android default 100
                      horizontalPadding: 22, // iOS default 16, Android default 50
                      verticalPadding: 20 // iOS default 12, Android default 30
                    }
                  });
              }else{
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
                }
          },
          error: function(jqXHR, textStatus, errorThrown) {
              console.log("ERREUR : "+jqXHR);
              console.log("ERREUR : "+textStatus);
              console.log("ERREUR : "+errorThrown);
              app.preloader.hide();
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
                          horizontalPadding: 20, // iOS default 16, Android default 50
                          verticalPadding: 16 // iOS default 12, Android default 30
                        }
                  }
               );
          }
      });
  }
});
