
var $chatForm = $('#chatForm'),
    $messageInput = $('#msg'),
    $chatArea = $('#chatArea'),
    $usersArea = $('#usersArea'),
    $receiver = $('#receiver');

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
    var receiver = $receiver.data('id'),
        messageContent = $messageInput.val().trim();
    if(messageContent && stompClient) {
        var chatMessage = {
            content: messageContent,
            type: 'CHAT'
        };
        if (receiver){
            chatMessage.receiverId = receiver;
        }
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
        onJoinUser(message.senderId,message.sender);
    } else if (message.type === 'LEAVE') {
        $messageElement.addClass('event-message');
        message.content = message.sender + ' покинул чат!';
        onLeaveUser(message.senderId);
    } else {
        $messageElement.addClass('chat-message').attr('id',message.id);
        var $user = $('<strong>')
            .addClass('nickname')
            .text(message.sender);
        $messageElement.append($user);
    }
    var $sendDate = $('<span>').addClass('send-date').text(message.sendDate);
    $messageElement.append($sendDate);

    if (message.receiver) {
        var $msgReceiver = $('<span>').addClass('receiver').text(message.receiver);
        $messageElement.append($msgReceiver);
    }
    var $messageText = $('<span>').text(message.content);

    $messageElement.append($messageText);

    $chatArea.append($messageElement);
}

function onJoinUser(userId, name){
    var $userLabel = $('<li>')
        .attr("id",'user-'+userId)
        .data("id",userId)
        .text(name)
        .on('click',function(){setReceiver(this)});
    $usersArea.append($userLabel);
    $usersArea.find('li')
        .sort(function(u1,u2){return $(u1).text() > $(u2).text() ? 1 : -1})
        .appendTo($usersArea);
}

function onLeaveUser(userId){
    $('#user-'+userId).remove();
}

function setReceiver(obj){
    var $obj=$(obj),
        receiverId = $obj.data('id'),
        receiver = $obj.text();
    $receiver
        .data('id',receiverId)
        .text(receiver);
    $messageInput.focus().val(receiver+', ');
}

function clearReceiver(){
    $receiver.removeAttr('data-id').text('');
}

$(function () {
    $chatForm.on('submit', sendMessage);
    // Connect to WebSocket Server.
    connect();
});