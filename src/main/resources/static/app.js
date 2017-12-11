var socket = null;

function connect() {
    // before we connect to a server we nned to ask the user's name
    // because we'd like to puch it with the connection
    var person = prompt("Please enter username", "");

    socket = new WebSocket('ws://' + window.location.host + '/ws?'+person);

    // Add an event listener for when a connection is open
    socket.onopen = function() {
      showMessage('WebSocket connection opened. Ready to send messages.');
    };

    socket.onclose = function (event) {
        showMessage("WebSocket closed.");
        alert("Game closed.");
    };

    // Add an event listener for when a message is received from the server
    socket.onmessage = function(event) {
        showMessage('Message received from server: ' + event.data);
        updateDesk(JSON.parse(event.data));
    };

    socket.onerror = function(error) {
        showMessage('Error: ' + error.message);
    };
}

function disconnect() {
    if (socket !== null) {
        socket.disconnect();
    }
    setConnected(false);
    console.log("Disconnected");
}

// send pit index to the server
function sendPit(idx) {
    socket.send(idx);
}

// update the desk based on information we received
function updateDesk(game){
    //update all td's
    $.map(game.opponentSeeds, function(val, i) {
        updateTdValue($("#opponent-"+i), val);
    });
    $.map(game.yourSeeds, function(val, i) {
        updateTdValue($("#my-"+i), val);
    });
    updateTdValue($("#my-basket"), game.yourBasket);
    updateTdValue($("#opponent-basket"), game.opponentBasket);

    //update player's names
    $("#my-name").html(game.yourName);
    $("#opponent-name").html(game.opponentName);

    if(game.yourTurn) {
        //here we'd like to enable/disable ability to click on TD's elements
        // based on yourTurn value
        $.map(game.yourSeeds, function(val, i) {
            $("#my-"+i).click(function(){ sendPit(i)});
            $("#my-"+i).addClass("clickable");

        });
        if(game.gameStarted){
            $("#my-name").addClass("turn");
            $("#opponent-name").removeClass("turn");
        }
    } else {
        $.map(game.yourSeeds, function(val, i) {
                    $("#my-"+i).unbind('click');
                    $("#my-"+i).prop("onclick", null);
                    $("#my-"+i).removeClass("clickable");
        });
        if(game.gameStarted){
            $("#my-name").removeClass("turn");
            $("#opponent-name").addClass("turn");
        }
    }

    if(game.gameFinished){
        //some scenario if game is finished
        $("#opponent-name").removeClass("turn");
        $("#my-name").removeClass("turn");
        if(game.yourBasket+ game.opponentBasket !=game.totalSeeds){
            alert("Game closed.\nLooks like your opponent has left the game.\nTo start another game reload the page.");
        } else if (game.yourBasket>game.opponentBasket){
            alert("Your win !");
        } else {
            alert("You lost :(");
        }
    }
}

// update element with animation
function updateTdValue(td, val){
    if(!td.html() || td.html() != val){
        td.html(val);
        animate_bg(td, 10, 2);
    }
}

function showMessage(message) {
    console.log(message);
}

function animate_bg(ele, from, to) {
    ele.css("background-color", "rgba(255, 255, 255, " + (from += from > to ? -1 : 1) / 10 + ")");
    if(from != to)
        setTimeout(function() { animate_bg(ele, from, to) }, 100);
}

$(function () {
    connect();
});