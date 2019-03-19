var mainView = app.views.create('.view-main', {
  routes: [
    {
      path: '/sign-up/',
      url: './pages/sign-up.html',
      name: 'sign-up',
      on: {
        pageAfterIn: function (e, page) {
          console.log("Chargement formulaire");
          console.log($("#emailInput"));
          $.ajax({
            url: "js/nonConnected/sha.js",
            dataType: "script",
            cache: true,
            success:function(msg) {
              console.log("Success!!");
            },
            error:function(msg) {
              console.log("Error chargement script de cryptage");
            }
          });
          $.ajax({
            url: "js/nonConnected/inscription.js",
            dataType: "script",
            cache: true,
            success:function(msg) {
              console.log("Success!!");
            },
            error:function(msg) {
              console.log("Error chargement script inscription");
            }
          });
        }
      }
    }
  ]
});
