jQuery(function($){

    var loginForm =    $('#password-form'),
    	oldpassword = $('#oldPassowrd'),
    	newpassword = $('#newPassowrd'),
    	newpassword2 = $('#newPassowrd2'),
    	divBox = $('#label-error-div'),
    	divBox1 = $('#label-error-div1'),
    	divBox2 = $('#label-error-div2'),
    	divBox3 = $('#label-error-div3'),
    	errorBox1 = $('#label-error-box1'),
    	errorBox2 = $('#label-error-box2'),
    	errorBox3 = $('#label-error-box3');

    function validOldPassword(){
        if($.trim(oldpassword.val())!=""){
        	errorBox1.addClass('hide');
        	divBox1.removeClass('error');
        	return true;
        }
        errorBox1.html('请输入密码');
        errorBox1.removeClass('hide');
        divBox1.addClass('error');
        oldpassword.trigger('focus');
        return false;
    }
    
    function validNewPassword(){
    	if($.trim(newpassword.val())!=""){
    		errorBox2.addClass('hide');
        	divBox2.removeClass('error');
    		return true;
    	}
        errorBox2.html('请输入密码');
        errorBox2.removeClass('hide');
        divBox2.addClass('error');
        newpassword.trigger('focus');
        return false;
        
    }
    
    function validRepeatedPassword(){
    	if($.trim(newpassword2.val())!=""){
    		errorBox3.addClass('hide');
        	divBox3.removeClass('error');
    		return true;
    	}
        errorBox3.html('请输入密码');
        errorBox3.removeClass('hide');
        divBox3.addClass('error');
        newpassword2.trigger('focus');
        return false;
        
    }
    
    function validPassEquals(){
    	var nps = newpassword.val();
    	var nps2 = newpassword2.val();
    	if (nps!=nps2){
    		errorBox2.html('两次输入的新密码不相等，请重新输入');
            errorBox2.removeClass('hide');
            divBox2.addClass('error');
    		return false;
    	}
    	return true;
    }

    loginForm.on('submit',function(){
        return validOldPassword()&&validNewPassword()&&validRepeatedPassword()&&validPassEquals();
    });
});