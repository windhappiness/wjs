/**
 * 给页面上的值设置readonly
 * @param formId			表单form
 * @param isFormReadonly	是否给表单form中的值设置为readonly
 * @param businessType		业务类型
 * @param networkType		网络类型、inner内网、outer外网、document一户一档
 * @param ctx				前缀
 * @param showPopover		是否是制证、或者发证环节
 * 注意： 如果isFormReadonly设置为true，那么formId一定要传，如果isFormReadonly设置为false，业务类型、网络类型、前缀一定要传
 * 例如toReadonly("foodForm",false,"6061","outer","${ctx}");//食品备案调用
 */

function toReadonly(formId,isFormReadonly,businessType,networkType,ctx,showPopover){
	if(isFormReadonly){
		if(!formId){
			mini.alert("请输入表单Form的ID!","提醒...");
			return;
		}
		
		var form = new mini.Form("#"+formId);
		var fields = form.getFields();                
		for (var i = 0, l = fields.length; i < l; i++) {
		    var c = fields[i];
		    if(showPopover){
		    	var cname = c.getName();
		    	if(cname == "effectBeginDate"){
		    		continue;
		    	}
		    	if(cname == "nextUser"){
		    		continue;
		    	}
		    }
	    	if (c.setReadOnly) c.setReadOnly(true);     //只读
	    	if (c.setIsValid) c.setIsValid(true);      //去除错误提示
	    	if (c.addCls) c.addCls("asLabel");          //增加asLabel外观
	    	if (c.required) c.setRequired(false);		//去除必填项
		} 
		
	}else{
		if(!businessType || !networkType){
			mini.alert("“businessType”和“networkType”不能为空！","提醒...");
			return;
		}
		$.post(ctx + "/licBusinessType/queryFields",{"businessType":businessType,"networkType":networkType},function(r){
			var fieldNames = r.fieldNames;
			if(fieldNames){
				var fileds = fieldNames.split(",");
				if(fileds && fileds.length>0){
					for(var i=0;i<fileds.length;i++){
						var filed = fileds[i];
						var c = mini.get(filed);
						if (c.setReadOnly) c.setReadOnly(true);
						if (c.setIsValid) c.setIsValid(true);      //去除错误提示
						if (c.addCls) c.addCls("asLabel");
						if (c.required) c.setRequired(false);		//去除只读
					}
				}
			}
		});
	}
}

function containObj(arr,obj){
	if(arr != null && arr.length>0){
		for (var i = 0; i < arr.length; i++) {
			if(arr[i] == obj){
				return true;
			}
		}
	}
	return false;
}


/**
 * 一户一档readonly
 * @param formId			表单formId
 * @param isFormReadonly   	是否给表单form中的值设置为readonly
 * @param businessType		业务类型
 * @param networkType		网络类型、inner内网、outer外网、document一户一档
 * @param ctx				前缀
 * 无论 isFormReadonly是true还是false --》 formId都必填
 */
function toDocumentReadonly(formId,isFormReadonly,businessType,networkType,ctx){
	
	var form = new mini.Form("#"+formId);
	var fields = form.getFields();
	
	if(isFormReadonly){
		if(!formId){
			mini.alert("请输入表单Form的ID!","提醒...");
			return;
		}
		for (var i = 0, l = fields.length; i < l; i++) {
		    var c = fields[i];
	    	if (c.setReadOnly) c.setReadOnly(true);     //只读
	    	if (c.setIsValid) c.setIsValid(true);      //去除错误提示
	    	if (c.addCls) c.addCls("asLabel");          //增加asLabel外观
	    	if (c.required) c.setRequired(false);		//去除必填项
		} 
	}else{
		if(!businessType || !networkType){
			mini.alert("“businessType”和“networkType”不能为空！","提醒...");
			return;
		}
		
		
		$.post(ctx + "/licBusinessType/queryFields",{"businessType":businessType,"networkType":networkType},function(r){
			var fieldNames = r.fieldNames;
			if(fieldNames){
				var fileds = fieldNames.split(",");
				
				for (var i = 0, l = fields.length; i < l; i++) {
					var c = fields[i];
					var controlId = c.getId();
					var isDisabled = true;
					  
					if(fileds && fileds.length>0){
						for(var j=0;j<fileds.length;j++){
							var filed = fileds[j];
							if(filed){
								var filedObj = filed.split(":");
								var filedId = filedObj[0];
								if(filedId == controlId){
									isDisabled = false;
									break;
								}
							}
						}
					}
					
					if(isDisabled){
						if (c.setReadOnly) c.setReadOnly(true);
						if (c.setIsValid) c.setIsValid(true);     
						if (c.addCls) c.addCls("asLabel");
						if (c.required) c.setRequired(false);
					}
					
				}
				
			}
		});
	}
}

