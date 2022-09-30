let canModify = !(role === "USER");
let isOwner = role === "OWNER";
let isModifying = true;

let linkToSubscribe = `/ws/${id}`;
let linkToSend = `/app/player/${id}`;

var stompClient = null;
connect();

function setConnected(connected) {
    $("#connect").prop("disabled", connected);
    $("#disconnect").prop("disabled", !connected);
    if (connected) {
        $("#conversation").show();
    } else {
        $("#conversation").hide();
    }
    $("#greetings").html("");
}

function connect() {
    var socket = new SockJS('/websocket');
    stompClient = Stomp.over(socket);

    stompClient.connect({}, function (frame) {
        setConnected(true);
        console.log('Connected: ' + frame);
        stompClient.subscribe(linkToSubscribe, function (greeting) {
            console.log(greeting.body)
        });
    });
}

function waitForSocketConnection(action) {
    if (stompClient.connected)
        action();
    else
        setTimeout(function () {
            waitForSocketConnection(action);
        }, 5);
}

function disconnect() {
    if (stompClient !== null) {
        stompClient.disconnect();
    }
    setConnected(false);
    console.log("Disconnected");
}

function sendConnected() {
    if (canModify)
        stompClient.send(linkToSend, {}, 42);
}


$(function () {
    $("form").on('submit', function (e) {
        e.preventDefault();
    });
    $("#connect").click(function () {
        connect();
    });
    $("#disconnect").click(function () {
        disconnect();
    });
    $("#send").click(function () {
        sendConnected();
    });

});