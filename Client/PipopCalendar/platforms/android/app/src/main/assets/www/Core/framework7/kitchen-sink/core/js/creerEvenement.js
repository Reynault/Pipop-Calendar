$(document).ready(function(){

  $("#creerEvenementBouton").click(function(e){
      console.log("TEST RECUP:"+ localStorage.getItem("nomCalendrierCourant"));
    //creerEvenement($("#nomCalInput").val(), $("#nomEvInput").val(), $("#descEvInput").val(), $("imageEvInput").val(), $("#dateEvInput").val(), $("#lieuEvInput").val(), $("#auteurEvInput").val(), $("#visibiliteEvInput").val());
  });



  function creerEvenement(nomCal, nomEv, descEv, imageEv, dateEv, lieuEv, auteurEv, visibiliteEv){
      console.log("CreerEvenement");
      // Ne pas oublier de crypter les données
      // Ne pas oublier de vérifier les données
      var arr = {"Request":"AddEvent", "CalendarName": nomCal, "EventName": nomEv, "EventDescription": descEv, "EventPicture": imageEv, "EventDate": dateEv, "EventLocation": lieuEv, "EventAuthor": auteurEv, "EventVisibility": visibiliteEv};
      console.log(JSON.stringify(arr));
      $.ajax({
          url: 'https://10.0.2.2:3306',
          type: 'POST',
          data: JSON.stringify(arr),
          dataType: 'text',
          async: true,
          success: function(data, textStatus, jqXHR) {
              app.preloader.hide();
              let obj = JSON.parse(data);
              if(obj["Result"]==0){
                //window.location = "user-home.html";
		//TODO Redirection vers la page du calendrier
              }else{
                window.plugins.toast.showWithOptions(
                {
                   message: "Création Evénement Echouée",
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
          },
          error: function(jqXHR, textStatus, errorThrown) {
	    window.plugins.toast.showWithOptions(
                {
                   message: "Erreur connexion serveur",
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
              console.log("ERREUR : "+jqXHR);
              console.log("ERREUR : "+textStatus);
              console.log("ERREUR : "+errorThrown);
          }
      });
  }

});
