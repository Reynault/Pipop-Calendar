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
    },
  ],
});

var mainView = app.views.create('.view-main');
