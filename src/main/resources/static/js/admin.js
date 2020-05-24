

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
    var userId = $(obj).parent('li').data('id'),
        newName = $(obj).val();
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