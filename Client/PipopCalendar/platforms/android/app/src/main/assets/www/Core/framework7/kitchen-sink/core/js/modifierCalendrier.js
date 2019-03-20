
$(document).ready(function(){

  console.log("Envoi de données au serveur");
  $("#modifierCalendrierBouton").click(function(e){
    e.preventDefault();
    app.input.checkEmptyState("#nomCalendrierForm");
    if(localStorage.getItem("colorSelectForm")===null){
        localStorage.setItem("colorSelectForm","black");
    }
    if(!$("#nomCalendirerForm")){
	     window.plugins.toast.showWithOptions({
             message: obj["Message"],
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
    else{
    modifierCalendrier($("#idcInput").val(), $("#nomcInput").val(), $("#couleurInput").val());
	}
 });

  function modifierCalendrier(idc, nomc, couleur){
      console.log("ModifierCalendrier");
      // Ne pas oublier de crypter les données
      // Ne pas oublier de vérifier les données
      var arr = {"Request":"ModifyCalendar", "idc": idc, "nameC": nomc, "couleur": couleur};
      console.log(JSON.stringify(arr));
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
                //window.location = "user-home.html";
		//TODO Redirection vers la page du calendrier
              }else{
                window.plugins.toast.showWithOptions(
                {
                   message: "Modification Echouée",
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
          },
          error: function(jqXHR, textStatus, errorThrown) {
	              window.plugins.toast.showWithOptions({
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
                });
          }
      });
  }

});
