$(document).ready(function(){

  console.log("Envois de données au serveur");
  $("#connexionBouton").click(function(e){
    e.preventDefault();
    //alert("Click!");
    premiereConnexion();
  });

  function premiereConnexion(){
      console.log("Teste de connexion");
      var arr = {City: 'Moscow', Age: 25};
      $.ajax({
          url: 'https://localhost:3306',
          type: 'POST',
          data: JSON.stringify(arr),
          contentType: 'application/json; charset=utf-8',
          dataType: 'json',
          async: false,
          success: function(msg) {
              alert(msg);
          }
      });
      location.replace("https://wwww.google.fr");
  }


});
