var mainView = app.views.create('.view-main', {
  stackPages: true,
  routes: [
    {
      path: '/panel-left/',
      panel: {
        componentUrl: './pages/panels/left.html'
      }
    },
    {
      path: '/panel-right/',
      panel: {
        componentUrl: './pages/panels/right.html'
      }
    },
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
      path: '/profil-settings/',
      componentUrl: './pages/profil-settings.html',
      name: 'profil-settings'
    },
    {
      path: '/preloaderTheme/',
      componentUrl: './pages/preloaderTheme.html',
      name: 'preloaderTheme'
    },
    {
      path: '/event-form-edit/:idE',
      componentUrl: './pages/event-form-edit.html',
      name: 'event-form-edit',
      on:{
        pageAfterIn: function (e, page) {
          $.ajax({
            url: "js/Evenement/Charger/chargerEvenement.js",
            dataType: "script",
            cache: true,
            success:function(msg) {
            },
            error:function(msg) {
              console.log("Error chargement script de chargement d'événement");
            },
          })
        }
      }
    },
    {
      path: '/calendar-form/',
      componentUrl: './pages/calendar-form.html',
      name: 'calendar-form',
      on: {
          pageAfterIn: function (e, page) {
            $.ajax({
              url: "js/Calendrier/Creer/creerCalendrier.js",
              dataType: "script",
              cache: true,
              success:function(msg) {
              },
              error:function(msg) {
                console.log("Error chargement script de création de calendrier");
              }
            });
          },
          pageAfterOut: function(e,page){
              $.ajax({
                url: "js/Calendrier/Charger/chargerCalendriers.js",
                dataType: "script",
                cache: true,
                success:function(msg) {
                },
                error:function(msg) {
                  console.log("Error chargement script de chargement de calendrier");
                }
              });
          }
      }
    },
    {
      path: '/calendar-form-edit/:idC',
      componentUrl: './pages/calendar-form-edit.html',
      name: 'calendar-form-edit',
      on: {
          pageAfterIn: function(e,page){
              $.ajax({
                url: "js/Calendrier/Charger/chargerCalendrier.js",
                dataType: "script",
                cache: true,
                success:function(msg) {
                },
                error:function(msg) {
                  console.log("Error chargement script de chargement de calendrier");
                }
              });
          },
      }
    },
    {
      path: '/calendar-view/',
      componentUrl: './pages/calendar-view.html',
      name: 'calendar-view',
      on:{
        pageAfterIn: function (e, page) {
           $.ajax({
             url: "js/Calendrier/Supprimer/supprimerCalendrier.js",
             dataType: "script",
             cache: true,
             success:function(msg) {
             },
             error:function(msg) {
               console.log("Error chargement script de suppression de calendrier");
             },
           })
         }
      }
    },
    {
     path: '/event-form/',
     componentUrl: './pages/event-form.html',
     name: 'event-form',
     on:{
       pageAfterIn: function (e, page) {
         $.ajax({
           url: "js/Evenement/Creer/creerEvenement.js",
           dataType: "script",
           cache: true,
           success:function(msg) {
           },
           error:function(msg) {
             console.log("Error chargement script de création d'événement");
           },
         })
       }
     }
   },
    // Default route, match to all pages (e.g. 404 page)
    {
      path: '(.*)',
      url: './pages/404.html'
    }
  ]
});
