$(document).ready(function(){
$("#modifierCalendrierFormBouton").click(function(e){
    app.input.checkEmptyState("#nomCalendrierForm");

    if(!$("#nomCalendirerForm")){
	     window.plugins.toast.showWithOptions({
           message: "Calendar's name cannot be empty",
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
      modifierCalendrier(localStorage.getItem("idCalendrierCourant"), $("#nomCalendrierForm").val(), $("#descriptionCalendrierForm").val());
	  }
  });

  function modifierCalendrier(idc, nomc, descr){
      var smartSelectCouleur = app.smartSelect.get('#couleurSelect');
      var smartSelectTheme = app.smartSelect.get('#themeSelect');
      console.log("ModifierCalendrier");
      // Ne pas oublier de crypter les données
      // Ne pas oublier de vérifier les données
      var arr = {"Request":"ModifyCalendar", "IdCalendar": idc, "Nom": nomc, "Couleur": smartSelectCouleur.getValue(), "Theme":smartSelectTheme.getValue(), "Description": descr,"Email":localStorage.getItem("emailUtilisateur"), "Mdp":localStorage.getItem("mdpUtilisateur")};
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
                 localStorage.setItem("nomCalendrierCourant", nomc);
                 app.views.main.router.back( "calendar-view.html" , {reloadPrevious: true, ignoreCache: true, reload: true} );
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
          },
          error: function(jqXHR, textStatus, errorThrown) {
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
                     horizontalPadding: 22, // iOS default 16, Android default 50
                     verticalPadding: 20 // iOS default 12, Android default 30
                   }
                });
          }
      });
  }

});
