$(document).ready(function(e){
  $("a[id^=supprEventBouton]").click(function(e){
    e.preventDefault();
    console.log("SUPPRIMER POPUP"+$(this).attr('id').substring(16));
    supprimerEvenement(localStorage.getItem("emailUtilisateur"),$(this).attr('id').substring(16));
  });

  function supprimerEvenement(email, id){
      app.popover.get("#evenement_settings"+id).close(true);
      var arr = {"Request":"DeleteEvent","Email":email,"ID":id.substring(0,2), "Mdp":localStorage.getItem("mdpUtilisateur")};
      console.log("JSON : "+JSON.stringify(arr));
      app.preloader.show();
      $.ajax({
          url: adresse,
          type: 'GET',
          data: JSON.stringify(arr),
          dataType: 'text',
          async: false,
          success: function(data, textStatus, jqXHR) {
              app.preloader.hide();
              var obj = JSON.parse(data);
              if(obj["Result"]==0){
                $("#evenement_card"+id).remove();
                var calendarInline = app.calendar.get();
                $.ajax({
                  url: "js/Evenement/Charger/chargerEvenements.js",
                  dataType: "script",
                  cache: true,
                  async: false,
                  success: function(msg) {
                  },
                  error: function(msg) {
                    console.log("Error chargement script de chargement d'événements");
                  }
                });
                calendarInline.params.events = eventFromServer;
                calendarInline.update();
                //mainView.router.navigate("/calendar-view/",{ ignoreCache:true, reloadCurrent:true});
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
                 );
              }
          },
          error: function(jqXHR, textStatus, errorThrown) {
              app.preloader.hide();
              window.plugins.toast.showWithOptions({
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
               });
          }
      });
  }
});
