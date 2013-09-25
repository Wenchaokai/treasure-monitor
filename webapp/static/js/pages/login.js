jQuery(function($){

    var loginForm =    $('#login-form'),
        userName = $('#userName'),
        pwd = $('#pwd'),
        errorBox = $('#label-error-box');

    function cheLogSta(){
	    if ($.cookie("rmbUser-treasure")=="true"){ 
	    	$("#rmbUser").attr("checked", true); 
	    	userName.val($.cookie("userName-treasure")); 
	    	pwd.val($.cookie("passWord-treasure")); 
	    } 
    }
    
    function saveUserInfo(){ 
    	if ($("#rmbUser").attr("checked")){ 
    		var userNameVal = userName.val(); 
    		var passWord = pwd.val(); 
    		$.cookie("rmbUser-treasure", "true", { expires: 7 }); // 存储一个带7天期限的 cookie 
    		$.cookie("userName-treasure", userNameVal, { expires: 7 }); // 存储一个带7天期限的 cookie 
    		$.cookie("passWord-treasure", passWord, { expires: 7 }); // 存储一个带7天期限的 cookie 
    	}else{ 
    		$.cookie("rmbUser-treasure", "false", { expires: -1 }); 
    		$.cookie("userName-treasure", '', { expires: -1 }); 
    		$.cookie("passWord-treasure", '', { expires: -1 }); 
    	} 
    }


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
        var flag = validUserName()&&validPwd();
        if (flag)
        	saveUserInfo();
        return flag;
    });
    cheLogSta();
});
