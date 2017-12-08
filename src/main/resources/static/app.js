var socket = null;

function setConnected(connected) {
    $("#connect").prop("disabled", connected);
    $("#disconnect").prop("disabled", !connected);
    if (connected) {
        $("#conversation").show();
    }
    else {
        $("#conversation").hide();
    }
    $("#greetings").html("");
}

function connect() {
    socket = new WebSocket('ws://' + window.location.host + '/ws');

    // Add an event listener for when a connection is open
    socket.onopen = function() {
      showGreeting('WebSocket connection opened. Ready to send messages.');
      setConnected(true);
    };
    socket.onclose = function (event) {
        showGreeting("WebSocket closed.");
    };
    // Add an event listener for when a message is received from the server
    socket.onmessage = function(event) {
      showGreeting('Message received from server: ' + event.data);
    };

    socket.onerror = function(error) {
      showGreeting('Error: ' + error.message);
    };
}

function disconnect() {
    if (socket !== null) {
        socket.disconnect();
    }
    setConnected(false);
    console.log("Disconnected");
}

function sendPit() {
    // Send a message to the server
    socket.send($("#name").val());
}

function showGreeting(message) {
    $("#greetings").append("<tr><td>" + message + "</td></tr>");
}

$(function () {
    connect();
    $("form").on('submit', function (e) {
        e.preventDefault();
    });
    $( "#connect" ).click(function() { connect(); });
    $( "#disconnect" ).click(function() { disconnect(); });
    $( "#send" ).click(function() { sendPit(); });
});