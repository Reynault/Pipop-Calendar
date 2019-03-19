var mainView = app.views.create('.view-main', {
  stackPages: true,
  routes: [
    {
      path: '/themes/',
      componentUrl: './pages/themes.html',
      name: 'themes'
    },
    {
      path: '/preloaderTheme/',
      componentUrl: './pages/preloaderTheme.html',
      name: 'preloaderTheme'
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
              },
              error:function(msg) {
                console.log("Error chargement script de création de calendrier");
              },
            });
          },
          pageAfterOut: function(e,page){
          console.log("Chargement Back");
              $.ajax({
                url: "js/chargerCalendrier.js",
                dataType: "script",
                cache: true,
                success:function(msg) {
                },
                error:function(msg) {
                  console.log("Error chargement script de création de calendrier");
                },
              });
          }
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
