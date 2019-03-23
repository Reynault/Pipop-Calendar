//$(document).ready(function(){
var eventFromServer = [];
chargerEvenements(localStorage.getItem("emailUtilisateur"), localStorage.getItem("nomCalendrierCourant"));

//});

  function chargerEvenements(email, calendrier){
      var arr = {"Request":"LoadEvents","Mail":email,"CalendarName":calendrier};
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
              console.log(data);
              var obj = JSON.parse(data);
              if(obj["Result"]==0){
                var nbEvents = Object.keys(obj.Data).length;
                var objData = obj["Data"];
                for(var i=0; i < nbEvents; i++){
                console.log(objData[i]["Date"]);
                  var dateDeb = {
                    year: new Date(objData[i]["Date"]).getFullYear(),
                    month: new Date(objData[i]["Date"]).getMonth(),
                    day: new Date(objData[i]["Date"]).getDate(),
                    hour: new Date(objData[i]["Date"]).getHours(),
                    min: new Date(objData[i]["Date"]).getMinutes()
                  };

                  var dateFin ={
                    year: new Date(objData[i]["DateFin"]).getFullYear(),
                    month: new Date(objData[i]["DateFin"]).getMonth(),
                    day: new Date(objData[i]["DateFin"]).getDate(),
                    hour: new Date(objData[i]["DateFin"]).getHours(),
                    min: new Date(objData[i]["DateFin"]).getMinutes()
                  };

                  var t = {
                    from: new Date(dateDeb.year, dateDeb.month, dateDeb.day,dateDeb.day,dateDeb.hour,dateDeb.min),
                    to: new Date(dateFin.year, dateFin.month,dateFin.day, dateFin.day, dateFin.hour, dateFin.min),
                    color: ''+objData[i]["EventColor"],
                    title: ''+objData[i]["EventName"],
                    description: ''+objData[i]["Description"]
                  };
                  eventFromServer.push(t);
                }
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
