var mainView = app.views.create('.view-main', {
  stackPages: true,
  routes: [
    {
      path: '/user-home/',
      componentUrl: './user-home.html',
      name: 'user-home'
    },
    {
      path: '/themes/',
      componentUrl: './pages/themes.html',
      name: 'themes'
    },
    {
      path: '/calendar-form/',
      componentUrl: './pages/calendar-form.html',
      name: 'calendar-form',
      on: {
          pageAfterIn: function (e, page) {
            console.log("Chargement formulaire");
            $.ajax({
              url: "js/creerCalendrier.js",
              dataType: "script",
              cache: true,
              success:function(msg) {
                console.log("Success!!");
              },
              error:function(msg) {
                console.log("Error chargement script de création de calendrier");
              },
            })
          },
        }
    },
    {
      path: '/calendar-view/',
      componentUrl: './pages/calendar-view.html',
      name: 'calendar-view',
      on: {
          pageAfterIn: function (e, page) {
            console.log("Chargement formulaire");
            $.ajax({
              url: "js/enregistrerNomCalendrier.js",
              dataType: "script",
              cache: true,
              success:function(msg) {
                console.log("Success!!");
              },
              error:function(msg) {
                console.log("Error chargement script de l'enregistreur de nom de calendrier");
              },
            })
          }
        }
    },
    {
      path: '/event-form/',
      componentUrl: './pages/event-form.html',
      name: 'event-form'
    },
    // Default route, match to all pages (e.g. 404 page)
    {
      path: '(.*)',
      url: './pages/404.html'
    }
  ]
});
//document.addEventListener("backbutton", app.methods.onBackKeyDown, false);
