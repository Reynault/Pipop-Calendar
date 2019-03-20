//Script utilisé uniquement pour le bouton logout
$(document).ready(function(){
  $("#deconnexionBouton").click(function(){
    app.dialog.confirm('Voulez-vous vous déconnecter?', function () {
            window.location = "index.html";
            localStorage.setItem("emailUtilisateur","");
    });
  });
});
