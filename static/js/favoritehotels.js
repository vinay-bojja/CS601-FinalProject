async function deleteAllFavoriteHotels(){
let response = await fetch('/deletefavoritehotels',{method :'get'});
setFavoriteHotels();
}

async function deleteFavoriteHotel(hotelName){
let response = await fetch('/deletefavoritehotels?hotelName='+hotelName,{method :'get'})
setFavoriteHotels()
}

async function setFavoriteHotels(){
let response = await fetch('/getuserallfavoritehotels',{method :'get'})
let output = await response.json();

 let body = "";
    if(output == null){
        body = "<tr>"+
            "<td>No reviews found.</td>"+
        "</tr>";
        document.getElementById("head1").innerHTML = "";
    }
    else{
        let head = "<tr>"+
            "<th scope=\"col\">Favorite Hotels</th>"+
            "<th scope=\"col\"><button type=\"button\" class=\"btn btn-danger\""+
            "onclick=\"deleteAllFavoriteHotels();\">Delete All"+
            "</button>"+
            "</th>"+
        "</tr>";
        document.getElementById("head1").innerHTML = head;

        for(let hotel in output.allHotels){
            body = body + "<tr>"+
            "<td>"+output.allHotels[hotel]+"</td>"+
            "<td>"+
            "<button type=\"button\" class=\"btn btn-danger\""+
            "onclick=\"deleteFavoriteHotel('" + output.allHotels[hotel] + "');\">Delete"+
            "</button>"+
            "</td>"+
            "</tr>";
        }
    }
    document.getElementById("body").innerHTML = body;

}



