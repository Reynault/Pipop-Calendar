$(document).ready(function(){

  console.log("Envois de données au serveur");
  $("#connexionBouton").click(function(e){
    e.preventDefault();
    //alert("Click!");
    connexion($("#emailInput").val(), $("#mdpInput").val());
  });

  function connexion(email, mdp){
      console.log("Connexion");
      var arr = {action:"SignIn", utilisateurId: email, utilisateurMdp: mdp};
      console.log(JSON.stringify(arr));
      app.preloader.show();
      $.ajax({
          url: '10.0.2.2',
          type: 'POST',
          data: JSON.stringify(arr),
          dataType: 'text',
          async: false,
          success: function(data) {
              console.log(data);
              console.log("Success!!");
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
