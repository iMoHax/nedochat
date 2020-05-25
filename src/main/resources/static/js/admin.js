

function deleteUser(obj){
    var userId = $(obj).parent('li').data('id');
    $.ajax({
        method: "DELETE",
        url: "/api/user/"+userId
    })
        .done(function(msg) {
            onDeleteUser(userId);
        });
}

function renameUser(obj){
    var $obj = $(obj),
        $userData = $obj.parents('li'),
        usrId = $userData.data('id'),
        $name = $userData.find('.user-name');
    var $tb = $name.find('input');
    if ($tb.length) {
        $name.text($tb.val());
        sendRenameUserRequest(usrId, $tb.val());
        $obj.text('Переименовать');
    } else {
        $tb = $('<input>').prop('type','text').val($name.text());
        $name.empty().append($tb);
        $tb.focus();
        $obj.text('Сохранить');
    }
}

function sendRenameUserRequest(userId,newName){
    $.ajax({
        method: "PATCH",
        contentType: "application/json",
        dataType: "json",
        url: "/api/user/"+userId,
        data: JSON.stringify({name: newName})
    })
        .done(function(msg) {
            onRenameUser();
        })
    ;
}


function onDeleteUser(userId){
    $('#user-'+userId).remove();
}

function onRenameUser(){
}