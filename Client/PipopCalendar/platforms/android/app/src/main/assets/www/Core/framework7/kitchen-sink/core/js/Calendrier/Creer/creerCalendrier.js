$(document).ready(function(){
   $("#creationCalendrierBouton").click(function(e){

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
     creerCalendrier($("#nomCalendrierForm").val(),$("#descriptionCalendrierForm").val(), localStorage.getItem("emailUtilisateur"));
     }
  });

  function creerCalendrier(nom, description, email){
      var smartSelectCouleur = app.smartSelect.get('#couleurSelect');
      var smartSelectTheme = app.smartSelect.get('#themeSelect');
      var arr = {"Request":"CreateCalendar","Nom":nom, "Description": description,"Couleur":smartSelectCouleur.getValue(), "Theme":smartSelectTheme.getValue(), "Email":email, "Mdp":localStorage.getItem("mdpUtilisateur")};
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
