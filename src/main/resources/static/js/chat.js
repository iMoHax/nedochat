
var $chatForm = $('#chatForm');
var $messageInput = $('#msg');
var $chatArea = $('#chatArea');

var stompClient = null;

function connect() {
    var socket = new SockJS('/ws');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, onConnected, onError);
}

function onConnected() {
    stompClient.subscribe('/topic/publicChatRoom', onMessageReceived);
}

function onError(error) {
    var $errorMsg = $('<li>');
    $errorMsg.text('Не удаеться соединиться с сервером. Попробуте позже');
    $errorMsg.addClass("error");
    $chatArea.append($errorMsg);
}

function sendMessage(event) {
    var messageContent = $messageInput.val().trim();
    if(messageContent && stompClient) {
        var chatMessage = {
            content: messageContent,
            type: 'CHAT'
        };
        stompClient.send("/app/chat.sendMessage", {}, JSON.stringify(chatMessage));
        $messageInput.val('');
    }
    event.preventDefault();
}


function onMessageReceived(payload) {
    var message = JSON.parse(payload.body);

    var $messageElement = $('<li>');

    if(message.type === 'JOIN') {
        $messageElement.addClass('event-message');
        message.content = message.sender + ' присоединился!';
    } else if (message.type === 'LEAVE') {
        $messageElement.addClass('event-message');
        message.content = message.sender + ' покинул чат!';
    } else {
        $messageElement.addClass('chat-message').attr('id',message.id);
        var $user = $('<strong>')
            .addClass('nickname')
            .text(message.sender);
        $messageElement.append($user);
    }
    var $sendDate = $('<span>').addClass('send-date').text(message.sendDate);
    $messageElement.append($sendDate);

    var $messageText = $('<span>').text(message.content);

    $messageElement.append($messageText);

    $chatArea.append($messageElement);
}



$(function () {
    $chatForm.on('submit', sendMessage);
    // Connect to WebSocket Server.
    connect();
});