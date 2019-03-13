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
      name: 'calendar-form'
    },
    {
      path: '/calendar-view/',
      componentUrl: './pages/calendar-view.html',
      name: 'calendar-view'
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
