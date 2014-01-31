jQuery(function($){

    var loginForm =    $('#Alert-Form'),
    	alertMonitor = $('#alertMonitor'),
    	alertMonitorIndex = $('#alertMonitorIndex'),
    	alertMonitorWareHouse = $('#alertMonitorWareHouse'),
    	alertMonitorSku = $('#alertMonitorSku'),
    	alertMonitorDay = $('#alertMonitorDay'),
    	alertMonitorNum = $('#alertMonitorNum'),
    	alertMonitorCompare = $('#alertMonitorCompare'),
    	alertMonitorUnit = $('#alertMonitorUnit'),
    	alertMonitorMsg = $('#alertMonitorMsg'),
    	alertMonitorSmsCheckbox = $('#alertMonitorSmsCheckbox'),
    	alertMonitorEmailCheckbox = $('#alertMonitorEmailCheckbox');
    
    function enabled(){
    	alertMonitorIndex.attr("disabled",false);
    	alertMonitorWareHouse.attr("disabled",false);
    	alertMonitorSku.attr("disabled",false);
    	alertMonitorCompare.attr("disabled",false);
    	alertMonitorUnit.attr("disabled",false);
    	alertMonitorMsg.attr("disabled",false);
    	alertMonitorDay.attr("disabled",false);
    	alertMonitorNum.attr("disabled",false);
    }
    
    
    
    function disabled(){
    	alertMonitorIndex.attr("disabled",true);
    	alertMonitorWareHouse.attr("disabled",true);
    	alertMonitorSku.attr("disabled",true);
    	alertMonitorDay.attr("disabled",true);
    	alertMonitorCompare.attr("disabled",true);
    	alertMonitorUnit.attr("disabled",true);
    	alertMonitorMsg.attr("disabled",true);
    	alertMonitorNum.attr("disabled",true);
    	
    }
    
    alertMonitorIndex.change(function(){
    	var value = $(this).children('option:selected').val();
    	if (value=="3"){
    		var distinctDiv = $('#distinctDiv');
    		distinctDiv.removeClass('hide');
    	}else {
    		var distinctDiv = $('#distinctDiv');
    		distinctDiv.addClass('hide');
    	}
    	
    	if (value=="3" || value=="4"){
    		var indexHtml = "<option value=\"1\">%</option>";
    		alertMonitorUnit.html(indexHtml);
    		
    	}else{
    		var indexHtml = "<option value=\"2\">单</option>";
    		alertMonitorUnit.html(indexHtml);
    	}
    });
    
    alertMonitor.change(function(){
    	var value = $(this).children('option:selected').val();
    	if (value=="-1"){
    		disabled();
    		alertMonitorIndex.html("<option value=\"-1\">请选择监控指标</option>");
    		alertMonitorWareHouse.html("<option value=\"-1\">请选择监控仓库</option>");
    		alertMonitorSku.html("<option value=\"-1\">请选择监控SKU</option>");
    	}
    	else{
    		

    		enabled();
    		var parts = value.split("#");
    		// 监控指标处理
    		var indexIdList = parts[2].split(",");
    		var indexHtml = "<option value=\"-1\">请选择监控指标</option>";
    		for (var i=0;i<indexIdList.length;i++){
    			var indexId = indexIdList[i];
    			if (indexId==1)
    				indexHtml += "<option value=\"1\">分仓SKU各仓库订单量</option>";
    			else if (indexId==2)
    				indexHtml += "<option value=\"2\">分仓SKU各仓库库存量</option>";
    			else if (indexId==3)
    				indexHtml += "<option value=\"3\">SKU订单来源的区域分布占比</option>";
    			else if (indexId==4)
    				indexHtml += "<option value=\"4\">SKU关联销售占比情况</option>";
    		}
    		alertMonitorIndex.html(indexHtml);
    		
    		// 监控仓库
    		if ($.trim(parts[3])!=""){
	    		var wareHouseNameList = parts[3].split(",");
	    		var wareHouseIdList = parts[4].split(",");
	    		var minSize = wareHouseNameList.length;
	    		if (minSize>wareHouseIdList)
	    			minSize = wareHouseIdList;
	    		
	    		var indexHtml = "<option value=\"-1#所有仓库\">所有仓库</option>";
	    		for (var i=0;i<minSize;i++){
	    			
	    			indexHtml += "<option value=\""+wareHouseIdList[i]+"#"+wareHouseNameList[i]+"\">"+wareHouseNameList[i]+"</option>";
	    		}
	    		alertMonitorWareHouse.html(indexHtml);
    		}else{
    			alertMonitorWareHouse.html("<option value=\"-1\">请选择监控仓库</option>");
    		}
    		
    		// sku
    		if ($.trim(parts[5])!=""){
	    		var skuList = parts[5].split(";");
	    		
	    		var indexHtml = "<option value=\"-1#所有SKU\">所有SKU</option>";
	    		for (var i=0;i<skuList.length;i++){
	    			
	    			indexHtml += "<option value=\""+skuList[i]+"\">"+skuList[i]+"</option>";
	    		}
	    		alertMonitorSku.html(indexHtml);
    		}else{
    			alertMonitorSku.html("<option value=\"-1\">请选择监控SKU</option>");
    		}
    	}
    	
    	
    });
    
    alertMonitorEmailCheckbox.change(function(){
    	if($(this).attr("checked")){
    		$("[name='alertMonitorEmail']").attr("disabled",false);
    	}else
    		$("[name='alertMonitorEmail']").attr("disabled",true);
    });
    alertMonitorSmsCheckbox.change(function(){
    	if($(this).attr("checked")){
    		$("[name='alertMonitorSms']").attr("disabled",false);
    	}else
    		$("[name='alertMonitorSms']").attr("disabled",true);
    });
    
    function validMonitorProject(){
    	var value = alertMonitor.val();
    	if (value=="-1"){
    		alert("请选择监控项目");
    		return false;
    	}
    	return true;
    }
    
    function validMonitorDay(){
    	var value = alertMonitorDay.val();
    	if ($.trim(value)=="" ){
    		alert("请输入天/月数阀值");
    		alertMonitorDay.trigger('focus');
    		return false;
    	}
    	if (!$.isNumeric(value)){
    		alert("输入的天/月数阀值不是数字，请重新输入");
    		alertMonitorDay.trigger('focus');
    		alertMonitorDay.attr("value",'');
    		return false;
    	}
    	return true;
    }
    
    function validMonitorNum(){
    	var value = alertMonitorNum.val();
    	if ($.trim(value)=="" ){
    		alert("请输入数量阀值");
    		alertMonitorNum.trigger('focus');
    		return false;
    	}
    	if (!$.isNumeric(value)){
    		alert("输入的数量阀值不是数字，请重新输入");
    		alertMonitorNum.trigger('focus');
    		alertMonitorNum.attr("value",'');
    		return false;
    	}
    	return true;
    }
    
    function validMonitorMsg(){
    	var value = alertMonitorMsg.val();
    	if ($.trim(value)=="" ){
    		alert("请输入告警消息");
    		alertMonitorMsg.trigger('focus');
    		alertMonitorMsg.attr("value",'分仓产品[sku-Name]，在[ofc-Name]的每日订单量已经连续[date-Num][date-Unit][compare-Unit][warning-Value][warning-Value-Unit]，请知晓！');
    		return false;
    	}
    	return true;
    }
    
    function validMonitorIndex(){
    	var value = alertMonitorIndex.val();
    	if (value!=-1)
    		return true;
    	else{
    		alert("请选择监控指标");
    		return false;
    	}
    }
    
    function validMonitorDistrict(){
    	var value = alertMonitorIndex.val();
    	if (value!=3)
    		return true;
    	else{
    		var obj=document.getElementsByName('districtName');    
        	var index=0;  
    		for(var i=0; i<obj.length; i++){  
    			if(obj[i].checked) index++;   
    		}  
    		if (index==0){
    			alert("请选择监控区域");
    			return false;
    		}
    		return true;
    	}
    }
    
    loginForm.on('submit',function(){
         return validMonitorProject()&&validMonitorIndex()&&validMonitorDistrict()&&validMonitorDay()&&validMonitorNum()&&validMonitorMsg();
    });
});