
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
    stompClient.subscribe('/topic/updateMessages', onUpdateMessageReceived);
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

    var $messageElement = $('<li class="chat-message left clearfix">'),
        $header = $('<div>');

    if(message.type === 'JOIN') {
        message.content = message.sender + ' присоединился!';
        onJoinUser(message.senderId,message.sender);
    } else if (message.type === 'LEAVE') {
        message.content = message.sender + ' покинул чат!';
        onLeaveUser(message.senderId);
    } else {
        $messageElement.addClass('chat-message')
            .attr('id','msg-'+message.id)
            .data('id',message.id);
        var $user = $('<strong>')
            .addClass('nickname')
            .text(message.sender);
        $header.append($user);

        if (message.receiver) {
            var $msgReceiver = $('<strong>').addClass('receiver').text('-> '+message.receiver);
            $header.append(' ').append($msgReceiver);
        }

        if ($chatArea.data('admin')){
            var $buttons = $('<div class="float-right"><button class="btn btn-secondary" onclick="editMessage(this)">Изменить</button> '+
                                                      '<button class="btn btn-secondary" onclick="deleteMessage(this)">Удалить</button></div');
            $header.append(' ').append($buttons);
        }
    }
    $messageElement.append($header);

    var $chatBody = $('<div class="chat-body1 clearfix">');

    var $messageText = $('<p class="content">').text(message.content);
    if (message.type != 'CHAT'){
        $messageText.addClass('alert');
    }
    $chatBody.append($messageText);
    var $sendDate = $('<div class="chat_time float-right">').text(moment(message.sendDate).format('HH:mm:ss DD.MM.yyyy'));
    $chatBody.append($sendDate);

    $messageElement.append($chatBody);
    $chatArea.append($messageElement);
}

function onJoinUser(userId, name){
    var $userLabel = $('<li class="left clearfix">')
        .attr("id",'user-'+userId)
        .data("id",userId)
        .on('click',function(){setReceiver(this)});
    var $nickname = $('<div class="chat-body clearfix">'+
                        '<div class="header_sec">'+
                        '<strong class="primary-font nickname">'+name+'</strong>'+
                      '</div></div>');
    $userLabel.append($nickname);
    $usersArea.append($userLabel);
    $usersArea.find('li')
        .sort(function(u1,u2){return $(u1).find('.nickname').text() > $(u2).find('.nickname').text() ? 1 : -1})
        .appendTo($usersArea);
}

function onLeaveUser(userId){
    $('#user-'+userId).remove();
}

function setReceiver(obj){
    var $obj=$(obj),
        receiverId = $obj.data('id'),
        receiver = $obj.find('.nickname').text();
    $receiver
        .data('id',receiverId)
        .text(receiver);
    $messageInput.focus().val(receiver+', ');
}

function clearReceiver(){
    $receiver.removeAttr('data-id').text('Всем');
}

function sendUpdateMessage(messageId, newContent) {
    if(stompClient) {
        var chatMessage = {
            id: messageId,
            content: newContent,
            type: newContent ? 'UPDATE' : 'DELETE'
        };
        stompClient.send("/app/chat.updateMessage", {}, JSON.stringify(chatMessage));
    }
}

function onUpdateMessageReceived(payload) {
    var message = JSON.parse(payload.body),
        $msg = $("#msg-"+message.id);
    if (message.type === 'DELETE'){
        $msg.remove();
    } else if (message.type === 'UPDATE'){
        $msg.find('.content').text(message.content);
    }
}

function editMessage(obj){
    var $obj = $(obj),
        $chatMessage = $obj.parents('li'),
        messId = $chatMessage.data('id'),
        $mess = $chatMessage.find('.content');
    var $ta = $mess.find('textarea');
    if ($ta.length) {
        $mess.text($ta.val());
        sendUpdateMessage(messId, $ta.val());
        $obj.text('Изменить');
    } else {
        $ta = $('<textarea>').val($mess.text());
        $mess.empty().append($ta);
        $ta.focus();
        $obj.text('Сохранить');
    }
}


function deleteMessage(obj){
    var messId = $(obj).parents('li').data('id');
    sendUpdateMessage(messId);
}


$(function () {
    $chatForm.on('submit', sendMessage);
    // Connect to WebSocket Server.
    connect();
});