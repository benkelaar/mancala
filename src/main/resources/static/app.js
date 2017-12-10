var socket = null;

function connect() {
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

function sendPit(idx) {
    // Send a message to the server
    socket.send(idx);
}

function updateDesk(game){
    $.map(game.yourSeeds, function(val, i) {
        $("#my-"+i).html(val)
    });
    $.map(game.opponentSeeds, function(val, i) {
            $("#opponent-"+i).html(val)
        });
    $("#my-basket").html(game.yourBasket);
    $("#opponent-basket").html(game.opponentBasket);
    $("#my-name").html(game.yourName);
    $("#opponent-name").html(game.opponentName);
    if(game.yourTurn) {
        $.map(game.yourSeeds, function(val, i) {
            $("#my-"+i).click(function(){ sendPit(i)});
            $("#my-"+i).addClass("clickable");

        });
        $("#my-name").addClass("turn");
        $("#opponent-name").removeClass("turn");
    } else {
        $.map(game.yourSeeds, function(val, i) {
                    $("#my-"+i).unbind('click');
                    $("#my-"+i).prop("onclick", null);
                    $("#my-"+i).removeClass("clickable");
        });
        $("#my-name").removeClass("turn");
        $("#opponent-name").addClass("turn");
    }
    if(game.gameFinished){
        $("#opponent-name").removeClass("turn");
        $("#my-name").removeClass("turn");
        if(game.yourBasket+ game.opponentBasket !=game.totalSeeds){
            alert("Game closed. Looks like your opponent has left.");
        } else if (game.yourBasket>game.opponentBasket){
            alert("Your win!");
        } else {
            alert("You lose (");
        }
    }
}

function showMessage(message) {
    console.log(message);
}

$(function () {
    connect();
});