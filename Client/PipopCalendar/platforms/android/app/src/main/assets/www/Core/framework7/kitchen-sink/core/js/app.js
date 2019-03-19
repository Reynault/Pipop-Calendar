var $$ = Dom7;
var app = new Framework7({
  // App root element
  root: '#app',
  // App Name
  name: 'Pipop Calendar',
  template7Pages: true,
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
            }
          })
        }
      }
    }
  ]
});

var $ptrContent = $$('.ptr-content');
$ptrContent.on('ptr:refresh', function (e) {
  // Emulate 2s loading
  setTimeout(function () {
    console.log("Loading Calendar");
    chargerCalendrier(localStorage.getItem("emailUtilisateur"));
    app.ptr.done(); // or e.detail();
  }, 2000);
});
var mainView = app.views.create('.view-main');

document.addEventListener("backbutton", onBackKeyDown, false);
function onBackKeyDown() {
  var path = window.location.pathname;
  var page = path.split("/").pop();
  if(page != "index.html"){
    app.dialog.confirm('Are you sure you want to log out?', function () {
        localStorage.setItem("emailUtilisateur","");
        window.location = "index.html";
    });
  }else{
    app.dialog.confirm('Do you really want to exit Pipop?', function () {
            localStorage.setItem("emailUtilisateur","");
            window.navigator.app.exitApp();
    });
  }
}
