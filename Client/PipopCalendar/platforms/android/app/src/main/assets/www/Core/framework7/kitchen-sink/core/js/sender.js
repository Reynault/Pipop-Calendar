$(document).ready(function(){

  console.log("Envois de données au serveur");
  $("#connexionBouton").click(function(e){
    e.preventDefault();
    //alert("Click!");
    premiereConnexion();
  });

  function premiereConnexion(){
      console.log("Teste de connexion");
      var arr = "City";
      console.log(JSON.stringify(arr));
      $.ajax({
          url: '10.0.2.2',
          type: 'POST',
          data: JSON.stringify(arr),
          dataType: 'text',
          async: false,
          success: function(msg) {
              alert("Success!!");
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
