var mainView = app.views.create('.view-main', {
  routes: [
    {
      path: '/themes/',
      componentUrl: './pages/themes.html',
      name: 'themes',
    },
    // Default route, match to all pages (e.g. 404 page)
    {
      path: '(.*)',
      url: './pages/404.html',
    },
  ],
});

var left = app.panel.create({
  el: '<div class="panel panel-left panel-cover">' +
    '<div class="page">' +
      '<div class="navbar no-shadow">' +
        '<div class="navbar-inner sliding">' +
          '<div class="title">Navigation</div>' +
        '</div>' +
      '</div>' +
      '<div class="page-content">' +
        '<div class="list links-list no-hairlines">' +
          '<ul>' +
            '<li>' +
              '<a href="#">Link Item 1</a>' +
            '</li>' +
            '<li>' +
              '<a href="#">Link Item 1</a>' +
            '</li>' +
          '</ul>' +
        '</div>' +
      '</div>' +
    '</div>' +
  '</div>',
});

var right = app.panel.create({
  el: '<div class="panel panel-right panel-cover">' +
    '<div class="page">' +
      '<div class="navbar no-shadow">' +
        '<div class="navbar-inner sliding">' +
          '<div class="title">Settings</div>' +
        '</div>' +
      '</div>' +
      '<div class="page-content">' +
        '<div class="list links-list no-hairlines">' +
          '<ul>' +
            '<li>' +
              '<a href="/themes/" class="panel-close">Theme</a>' +
            '</li>' +
          '</ul>' +
        '</div>' +
      '</div>' +
    '</div>' +
  '</div>',
});
