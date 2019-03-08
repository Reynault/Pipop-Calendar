$(document).ready(function(){

  console.log("Envois de données au serveur");
  $("#connexionBouton").click(function(e){
    e.preventDefault();
    //alert("Click!");
    connexion($("#emailInput").val(), $("#mdpInput").val());
  });

  function connexion(email, mdp){
      console.log("Test de connexion");
      var arr = {"Request":"SignIn","Email":"Oui?"};
      console.log("JSON : "+JSON.stringify(arr));
      app.preloader.show();
      $.ajax({
          url: 'http://10.0.2.2:3306',
          type: 'POST',
          data: JSON.stringify(arr),
          dataType: 'text',
          async: false,
          success: function(data, textStatus, jqXHR) {
              alert(data);
          },
          error: function(jqXHR, textStatus, errorThrown) {
              alert('Erreur de communication avec le serveur');
               console.log("ERREUR : "+jqXHR);
              console.log("ERREUR : "+textStatus);
               console.log("ERREUR : "+errorThrown);
          }
      });
  }

});
