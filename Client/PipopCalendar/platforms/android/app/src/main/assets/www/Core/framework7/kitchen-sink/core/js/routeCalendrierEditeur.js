$(document).ready(function(e){
  $("#modifierCalendrierBouton").click(function(e){
    app.popover.get("#calendar_settings").close(true);
    app.preloader.show('multi');
    app.views.main.router.navigate("/calendar-form-edit/"+localStorage.getItem("idCalendrierCourant"));
  });
});
