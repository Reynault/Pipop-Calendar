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
      path: '/',
      url: './index.html',
      name: 'home',
    },
    {
      path: '/themes/',
      componentUrl: './pages/themes.html',
      name: 'themes',
    },
    {
      path: '/sign-up/',
      url: './pages/sign-up.html',
      name: 'sign-up',
    },
    {
      path: '/user-home/',
      componentUrl: './pages/user-home.html',
      name: 'user-home',
    },
    // Default route, match to all pages (e.g. 404 page)
    {
      path: '(.*)',
      url: './pages/404.html',
    },
  ],
});

var mainView = app.views.create('.view-main');
