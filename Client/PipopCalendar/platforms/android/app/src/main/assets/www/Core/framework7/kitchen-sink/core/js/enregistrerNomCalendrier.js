$(document).ready(function(){
  console.log($("#nomCalendrier").val());
  enregistrerNomCalendrier($("#nomCalendrier").text());

  function enregistrerNomCalendrier(nomCalendrierCourant){
  //Enregisrement
    localStorage.setItem("nomCalendrierCourant",nomCalendrierCourant);
    console.log("GET LOCALSTORAGE: "+localStorage.getItem("nomCalendrierCourant"));
  }
});
