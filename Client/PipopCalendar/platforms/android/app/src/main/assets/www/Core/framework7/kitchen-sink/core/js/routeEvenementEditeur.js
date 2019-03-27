$(document).ready(function(e){
  $("a[id^=editEventBouton]").click(function(e){
    var idEv = $(this).attr('id').substring(15);
    app.popover.get("#evenement_settings"+idEv).close(true);
    localStorage.setItem("idEvenementCourant",idEv)
    localStorage.setItem("eventWasOpen",1);
    app.preloader.show('multi');
    var popup = app.popup.get()
    popup.close();
    app.views.main.router.navigate("/event-form-edit/"+idEv,{context:{idEv: idEv}});
  });
});
