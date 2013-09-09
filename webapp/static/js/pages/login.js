jQuery(function($){

    var loginForm =    $('#login-form'),
        userName = $('#userName'),
        pwd = $('#pwd'),
        errorBox = $('#label-error-box');



    function validUserName(){
        if($.trim(userName.val())!="") return true;
        errorBox.find('span').first().html('请输入用户名');
        errorBox.removeClass('hide');
        userName.trigger('focus');
        return false;
    }
    function validPwd(){
        if($.trim(pwd.val())!="") return true;
        errorBox.find('span').first().html('请输入密码');
        errorBox.removeClass('hide');
        pwd.trigger('focus');
        return false;
    }

    loginForm.on('submit',function(){
        return validUserName()&&validPwd();
    });
});
