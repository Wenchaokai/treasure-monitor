jQuery(function($){

    var loginForm = $('#monitor-form'),
    	monitorName = $('#monitorName'),
    	sku = $('#sku');
    	

    function validMonitorName(){
        if($.trim(monitorName.val())!=""){
        	return true;
        }
        alert("请输入监控项目的名称");
        monitorName.trigger('focus');
        return false;
    }
    
    function validSku(){
        if($.trim(sku.val())!=""){
        	return true;
        }
        alert("请输入监控项目的SKU值");
        sku.trigger('focus');
        return false;
    }
    
    function validNorm(){
    	var obj=document.getElementsByName('norm');    
    	var index=0;  
		for(var i=0; i<obj.length; i++){  
			if(obj[i].checked) index++;   
		}  
		if (index==0){
			alert("请选择监控项目的指标");
			return false;
		}
		return true;
    }
    
    function validWareHouse(){
    	var obj=document.getElementsByName('wareHouse');    
    	var index=0;  
		for(var i=0; i<obj.length; i++){  
			if(obj[i].checked) index++;   
		}  
		if (index==0){
			alert("请选择监控项目的仓库");
			return false;
		}
		return true;
    }
    

    loginForm.on('submit',function(){
    	return validMonitorName()&&validWareHouse()&&validSku()&&validNorm();
    });
});