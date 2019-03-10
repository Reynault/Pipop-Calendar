
$(document).ready(function(){

  console.log("Envois de données au serveur");
  $("#inscriptionBouton").click(function(e){
    e.preventDefault();
    inscription($("#emailInscripInput").val(), $("#mdpInscripInput").val(), $("#nomInput").val(), $("#prenomInput").val());
  });

  function inscription(email, mdp, nom, prenom){
      console.log("Inscription");
      var arr = {action:"SignUp", utilisateurId: email, utilisateurMdp: mdp, utilisateurNom: nom, utilisateurPrenom: prenom};
      console.log(JSON.stringify(arr));
      $.ajax({
          url: '10.0.2.2:3306',
          type: 'POST',
          data: JSON.stringify(arr),
          dataType: 'text',
          async: false,
          success: function(msg) {
              console.log("Success de l'envois du json : "+JSON.stringify(arr));
          },
          error: function(jqXHR, textStatus, errorThrown) {
              alert('Erreur de communication avec le serveur');
              console.log("ERREUR : "+jqXHR);
              console.log("ERREUR : "+textStatus);
              console.log("ERREUR : "+errorThrown);
          }
      });
      //location.replace("https://www.google.fr");
  }

});
