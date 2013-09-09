jQuery(function($){

    var loginForm =    $('#newUser-form'),
    	countDiv = $('#countDiv'),
    	countSpan = $('#countSpan'),
    	userCount = $('#userCount'),
    	nameDiv = $('#nameDiv'),
    	nameSpan = $('#nameSpan'),
    	userName = $('#userName'),
    	pasDiv = $('#pasDiv'),
    	pasSpan = $('#pasSpan'),
    	userPassword = $('#userPassword');
    	

    function validUserCount(){
        if($.trim(userCount.val())!=""){
        	countSpan.addClass('hide');
        	countDiv.removeClass('error');
        	return true;
        }
        countSpan.html('请输入用户账号');
        countSpan.removeClass('hide');
        countDiv.addClass('error');
        userCount.trigger('focus');
        return false;
    }
    
    function validUserName(){
    	if($.trim(userName.val())!=""){
    		nameSpan.addClass('hide');
    		nameDiv.removeClass('error');
    		return true;
    	}
    	nameSpan.html('请输入用户名');
    	nameSpan.removeClass('hide');
        nameDiv.addClass('error');
        userName.trigger('focus');
        return false;
        
    }
    
    function validUserPassword(){
    	if($.trim(userPassword.val())!=""){
    		pasSpan.addClass('hide');
    		pasDiv.removeClass('error');
    		return true;
    	}
    	pasSpan.html('请输入密码');
    	pasSpan.removeClass('hide');
        pasDiv.addClass('error');
        userPassword.trigger('focus');
        return false;
        
    }

    loginForm.on('submit',function(){
        return validUserCount()&&validUserName()&&validUserPassword();
    });
});