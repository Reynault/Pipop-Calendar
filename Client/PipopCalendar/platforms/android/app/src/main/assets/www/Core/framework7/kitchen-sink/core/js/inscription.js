
$(document).ready(function(){

  console.log("Envoi de données au serveur");
  $("#inscriptionBouton").click(function(e){
    e.preventDefault();
    inscription($("#emailInscripInput").val(), $("#mdpInscripInput").val(), $("#nomInput").val(), $("#prenomInput").val());
  });

  function inscription(email, mdp, nom, prenom){
      console.log("Inscription");
      var crypMdp =  new jsSHA("SHA-512", "TEXT");
      crypMdp.update(mdp);
      var hash = crypMdp.getHash("HEX");
      // Ne pas oublier de vérifier les données
      var arr = {"Request":"SignUp", "Email": email, "Mdp": hash, "Nom": nom, "Prenom": prenom};
      console.log(JSON.stringify(arr));
      $.ajax({
          url: 'http://10.0.2.2:3306',
          type: 'POST',
          data: JSON.stringify(arr),
          dataType: 'text',
          timeout: 512,
          async: false,
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
              console.log("ERREUR : "+jqXHR);
              console.log("ERREUR : "+textStatus);
              console.log("ERREUR : "+errorThrown);
          }
      });
  }

});
