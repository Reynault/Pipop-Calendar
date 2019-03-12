
$(document).ready(function(){

  console.log("Envoi de données au serveur");
  $("#inscriptionBouton").click(function(e){
    e.preventDefault();
    inscription($("#emailInscripInput").val(), $("#mdpInscripInput").val(), $("#nomInput").val(), $("#prenomInput").val());
  });

  function inscription(email, mdp, nom, prenom){
      console.log("Inscription");
      // Ne pas oublier de crypter les données
      // Ne pas oublier de vérifier les données
      var arr = {"Request":"SignUp", "Email": email, "Mdp": mdp, "Nom": nom, "Prenom": prenom};
      console.log(JSON.stringify(arr));
      $.ajax({
          url: 'https://10.0.2.2:3306',
          type: 'POST',
          data: JSON.stringify(arr),
          dataType: 'text',
          async: true,
          success: function(data, textStatus, jqXHR) {
              app.preloader.hide();
              let obj = JSON.parse(data);
              switch(obj["Result"]){
                case "0":
                {
                  window.location = "user-home.html";
                  break;
                }
                case "1":
                {
                  // Message d'erreur : L'utilisateur existe déjà
                  break;
                }
                case "2":
                {
                  // Message d'erreur : données trop longues
                  break;
                }
                default:
                {
                  // Message d'erreur : autre
                  break;
                }
              }
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
