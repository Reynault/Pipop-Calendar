$(document).ready(function(){

  $("#modifierEvenementBouton").click(function(e){
      app.input.checkEmptyState("#nomEvInput");
      app.input.checkEmptyState("#lieuEvInput");
      if(!$("#nomEvInput") && !$("#lieuEvInput")){
	        window.plugins.toast.showWithOptions({
             message: "Name or place of the event cannot be empty",
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
      }else{
        var ds=new Date($("#dateStart").val());
        var df=new Date($("#dateEnd").val());
        if(ds<df){
      	  window.plugins.toast.showWithOptions({
               message: "Ending date cannot be before start",
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
        }else{
          let check = $('#public').is(':checked');
          modifierEvenement( localStorage.getItem("idCalendrierCourant"), $("#nomEvInput").val(), $("#descEvInput").val(), $("#dateStart").val(), $("#dateEnd").val(), $("#lieuEvInput").val(),
          localStorage.getItem("emailUtilisateur"), check);
        }
      }
  });

  function modifierEvenement(nomCal, nomEv, descEv, dateEv, dateFin, lieuEv, auteurEv, visibiliteEv){
      var smartSelectCouleurTheme = app.smartSelect.get('#couleurSelectTheme');
      var arr = {"Request":"ModifyEvent", "IdCalendar": nomCal,"IdEvent": localStorage.getItem("idEvenementCourant").substring(0,2), "EventName": nomEv, "EventDescription": descEv,
               "EventDate": dateEv, "EventDateFin": dateFin, "EventLocation": lieuEv, "EventVisibility": String(visibiliteEv), "Email": auteurEv, "EventColor": smartSelectCouleurTheme.getValue(),"Mdp":localStorage.getItem("mdpUtilisateur")};
      $.ajax({
          url: adresse,
          type: 'POST',
          data: JSON.stringify(arr),
          dataType: 'text',
          async: true,
          success: function(data, textStatus, jqXHR) {
              app.preloader.hide();
              let obj = JSON.parse(data);
              if(obj["Result"]==0){
                app.views.main.router.back( "calendar-view.html" , {reloadPrevious: true, ignoreCache: true, reload: true} );
                var calendarInline = app.calendar.get();
                $.ajax({
                  url: "js/Evenement/Charger/chargerEvenements.js",
                  dataType: "script",
                  cache: true,
                  async:false,
                  success:function(msg) {
                  },
                  error:function(msg) {
                    console.log("Error chargement script de chargement d'événements");
                  }
                });
                calendarInline.params.events = eventFromServer;
                calendarInline.update();
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
                if(obj["Result"] == 2){
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
                    });
                }
            }
          },
          error: function(jqXHR, textStatus, errorThrown) {
	              window.plugins.toast.showWithOptions({
                   message: "No network connexion or server error",
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
      });
  }
});
