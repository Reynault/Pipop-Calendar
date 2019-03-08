$(document).ready(function(){

  console.log("Envois de données au serveur");
  $("#connexionBouton").click(function(e){
    e.preventDefault();
    //alert("Click!");
    premiereConnexion();
  });

  function premiereConnexion(){
      console.log("Test de connexion");
      var arr = {"Request":"SignIn","Email":"Oui?"};
      console.log("JSON : "+JSON.stringify(arr));
      $.ajax({
          url: 'http://10.0.2.2:3306',
          type: 'GET',
          data: JSON.stringify(arr),
          dataType: 'text',
          async: false,
          success: function(data, textStatus, jqXHR) {
              alert(data);
          },
          error: function(jqXHR, textStatus, errorThrown) {
              alert('An error occurred... Look at the console (F12 or Ctrl+Shift+I, Console tab) for more information!');
               console.log("ERREUR : "+jqXHR);
              console.log("ERREUR : "+textStatus);
               console.log("ERREUR : "+errorThrown);
          }
      });
      //location.replace("https://www.google.fr");
  }


});
