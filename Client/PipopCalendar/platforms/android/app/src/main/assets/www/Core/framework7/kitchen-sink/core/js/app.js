var app = new Framework7({
  // App root element
  root: '#app',
  // App Name
  name: 'Pipop Calendar',
  // Enable swipe panel
  panel: {
    swipe: 'both',
  },
  // Add default routes
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
            url: "js/sha.js",
            dataType: "script",
            cache: true,
            success:function(msg) {
              console.log("Success!!");
            },
            error:function(msg) {
              console.log("Error chargement script de cryptage");
            },
          })
          $.ajax({
            url: "js/inscription.js",
            dataType: "script",
            cache: true,
            success:function(msg) {
              console.log("Success!!");
            },
            error:function(msg) {
              console.log("Error chargement script inscription");
            },
          })
        },
      },
    },
  ],
});

var mainView = app.views.create('.view-main');
