$(document).ready(function(){

   chargerCalendrier(localStorage.getItem("emailUtilisateur"));

});


  function chargerCalendrier(email){
      var arr = {"Request":"LoadCalendars","Email":email};
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
              console.log(data);
              var obj = JSON.parse(data);
              console.log(obj);
              console.log("Err : "+ obj["RESULT"]+"          data : "+obj["MESSAGE"]);
              if(obj["RESULT"]==0){
                $("#calendrierContainer").empty();
                 var nbCalendriers = Object.keys(obj.DATA[0]).length;
                 var y = 0;
                 if(nbCalendriers > 0){
                   for(var i = 0; i<nbCalendriers; i++){
                     if(i%2==0){
                       var p = $("#calendrierContainer").append("<p id='"+ y +"Calendrier' class='row'>");
                       $("<a href='/calendar-view/' class='col-50 button button-large button-fill color-orange'>"+ y/*nom calendrier*/+"</a>").appendTo("#"+y+"Calendrier");
                     }else{
                       $("<a href='/calendar-view/' class='col-50 button button-large button-fill color-orange'>"+ y/*nom calendrier*/+"</a>").appendTo("#"+ (y-1) +"Calendrier");
                     }
                   y++;
                   }
                 }else{
                    var p = $("#calendrierContainer").append("<p id='"+ y +"Calendrier' class='row'>");
                    $("<div class='block-title block-title-medium block-strong' style='margin-left: auto; margin-right: auto;'><p>No Calendar Found</p></div>").appendTo("#"+y+"Calendrier");
                 }
              }else{
                $("#calendrierContainer").empty();
                var p = $("#calendrierContainer").append("<p id='0Calendrier' class='row'>");
                $("<div class='block-title block-title-medium block-strong' style='margin-left: auto; margin-right: auto;'><p>Error</p></div>").appendTo("#0Calendrier");
                console.log($("#calendrierContainer").prop('outerHTML'));
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
                       horizontalPadding: 20, // iOS default 16, Android default 50
                       verticalPadding: 16 // iOS default 12, Android default 30
                     }
               });
          }
      });
  }
