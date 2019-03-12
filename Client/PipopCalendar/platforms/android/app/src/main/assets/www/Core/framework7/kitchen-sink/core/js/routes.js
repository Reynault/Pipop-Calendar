var mainView = app.views.create('.view-main', {
  routes: [
    {
      path: '/themes/',
      componentUrl: './pages/themes.html',
      name: 'themes'
    },
    {
      path: '/calendar-form/',
      componentUrl: './pages/calendar-form.html',
      name: 'calendar-form'
    },
    {
      path: '/calendar-view/',
      componentUrl: './pages/calendar-view.html',
      name: 'calendar-view'
    },
    // Default route, match to all pages (e.g. 404 page)
    {
      path: '(.*)',
      url: './pages/404.html'
    }
  ]
});
