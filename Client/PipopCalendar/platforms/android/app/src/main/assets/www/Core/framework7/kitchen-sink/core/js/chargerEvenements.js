$(document).ready(function(){

   chargerEvenements(localStorage.getItem("emailUtilisateur"), localStorage.getItem("nomCalendrierCourant"));

});


  function chargerEvenements(email, calendrier){
      var arr = {"Request":"LoadEvents","Mail":email,"CalendarName":calendrier};
      console.log("JSON : "+JSON.stringify(arr));
      app.preloader.show();
      $.ajax({
          url: adresse,
          type: 'GET',
          data: JSON.stringify(arr),
          dataType: 'text',
          async: true,
          success: function(data, textStatus, jqXHR) {
              app.preloader.hide();
              console.log(data);
              var obj = JSON.parse(data);
              console.log(obj);
              console.log("Err : "+ obj["RESULT"]+"          data : "+obj["MESSAGE"]);
              if(obj["RESULT"]==0){

                var nbEvents = Object.keys(obj.Data).length;
                let i = 0;
                while( i  < nbEvents ){
                obj["Data"][i]
                  /*
                  let today = new Date();
                  let weeklater = new Date();
                  let calendarEvents = app.calendar.create({

                  });*/
                }
              }else{
                $("#evenementContainer").empty();
                var p = $("#evenementContainer").append("<p id='0Evenement' class='row'>");
                $("<div class='block-title block-title-medium block-strong' style='margin-left: auto; margin-right: auto;'><p>No Events Found</p></div>").appendTo("#0Evenement");
                console.log($("#evenementContainer").prop('outerHTML'));
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
              console.log("ERREUR : "+jqXHR);
              console.log("ERREUR : "+textStatus);
              console.log("ERREUR : "+errorThrown);
              app.preloader.hide();
              $("#evenementContainer").empty();
              var p = $("#evenementContainer").append("<p id='0Evenement' class='row'>");
              $("<div class='block-title block-title-medium block-strong' style='margin-left: auto; margin-right: auto;'><p>Check your network connection</p></div>").appendTo("#0Evenement");
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
