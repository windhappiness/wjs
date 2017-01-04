<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <title>用户列表</title>
    <meta name="decorator" content="miniui"/>
    <style type="text/css">
    body{
        margin:0;padding:0;border:0;width:100%;height:100%;overflow:hidden;
    }    
   
    </style>
    
    
</head>
<body>
	<div class="mini-toolbar" style="padding:2px;border-bottom:0;">
		<table style="width:100%;">
        	<tr>
            	<td style="width:100%;">
                	<a class="mini-button" iconCls="icon-add" plain="false" onclick="toAdd()">新增根节点</a>
            	</td>	
            </tr>
        </table>
    </div>
    <!--撑满页面-->
    	
    <div id="dictGrid" class="mini-treegrid" style="width:100%;height:90%;" showTreeIcon="true" textField="name"
    	url="${ctx}/sys/code/loadlist"  idField="id" treeColumn="nodename" parentField="parentId">
    	<div property="columns">
           	<div name="nodename" field="name" width="120" headerAlign="center">字典名称</div>    
            <div field="value" width="120" headerAlign="center">字典代码</div>
            <div field="comCode" width="80" headerAlign="center">字典组合码</div>                             
            <div name="action" width="120" headerAlign="center" align="center" renderer="onActionRenderer" cellStyle="padding:0;">操作</div>
        </div>
	</div>
	
	<div id="nodeWin" class="mini-window" title="编辑节点" style="width:600px;height:400px;" 
    	showMaxButton="false" showCollapseButton="false" 
    	showToolbar="false" showFooter="false" 
    	showModal="false" allowResize="false" allowDrag="true">
    	<form id="nodeForm" method="post">
    		<input id="nodeId" name="id" class="mini-hidden" />
    		<input id="parentNodeId" name="parent.id" class="mini-hidden" />
    		<div style="padding-left:11px;padding-bottom:5px;">
    			<table style="table-layout:fixed;">
    				<tr>
    					<td style="width:100px;">节点名称：</td>
                    	<td style="width:200px;">    
                        	<input id="name" name="name" class="mini-textbox" required="true" emptyText="请输入节点名称" errorMode="border"/>
                    	</td>
    				</tr>
    				<tr>
    					<td style="width:100px;">节点代码：</td>
                    	<td style="width:200px;">    
                        	<input id="code" name="value" class="mini-textbox" required="true" emptyText="请输入节点代码" errorMode="border"/>
                    	</td>
    				</tr>
    				<tr>
    					<td style="width:100px;">节点组合码：</td>
                    	<td style="width:200px;">    
                        	<input id="comCode" name="comCode" class="mini-textbox" vtype="int" emptyText="请输入节点组合码" errorMode="border"/>
                    	</td>
    				</tr>
    				<tr>
    					<td style="width:100px;">节点顺序：</td>
                    	<td style="width:200px;">    
                        	<input id="sort" name="sort" class="mini-textbox" vtype="int" required="true" emptyText="请输入节点顺序" errorMode="border"/>
                    	</td>
    				</tr>
    				
    				
    				<tr>
    					<td colspan="2" align="center">
    						<a class="mini-button" iconCls="icon-save" plain="false" onclick="saveDict()">保存</a>
    					</td>
    				</tr>
    			</table>
    		</div>
    	</form>
	</div> 

    
    
     <script type="text/javascript">
        mini.parse();
        
        var currentNodeId='';
      
		function toAdd() {
			currentNodeId='';
			var form = new mini.Form("#nodeForm");
            form.clear();
			var win = mini.get("nodeWin");
	        win.showAtPos('center','middle');
		}
		
		function onActionRenderer(e){
        	var record = e.record;
            var nodeId = record.id;
          
            var s1 =  '<a href="javascript:editNode(\'' + nodeId + '\')">修改</a>&nbsp;';
            var s2 =  '<a href="javascript:delNode(\'' + nodeId + '\')">删除</a>&nbsp;';
            var s3 =  '<a href="javascript:toAddNextNode(\'' + nodeId + '\')">添加子节点</a>';
        	return s1+s2+s3;
        }
		
		function editNode(nodeId) {
			currentNodeId = nodeId;
			
			var url = '${ctx}/sys/code/loadDicNode/'+nodeId;
			$.ajax({
	            url: url,
	            type: "get",
	            success: function (data) {
	            	var form = new mini.Form("#nodeForm");
	                form.setData(data);
	            }
	        });
			mini.get("nodeId").setValue(nodeId);
			var win = mini.get("nodeWin");
	        win.showAtPos('center','middle');
			
		}
		
		function delNode(nodeId) {
			
			var grid = mini.get("dictGrid");
			var node = grid.getNode(nodeId);
			var parentNode = grid.getParentNode(node);
			mini.confirm("是否确认删除所选节点?", "删除确认",
	        	function (action) {
	        		if (action == "ok") {
	        		    $.ajax({
	        	            url: "${ctx}/sys/code/removeNode/"+nodeId,
	        	            type: "post",
	        	            success: function (data) {
	        	            	var resultObj = mini.decode(data)
	        	                if(resultObj.result == 0){
	        	                	mini.showTips({
	        	                    	content: "<b>信息</b> <br/>删除成功",
	        	                        state: 'info',
	        	                        x: 'center',
	        	                        y: 'center',
	        	                        timeout: 3000
	        	                    });
	        	                	if(parentNode) {
	        	                		grid.loadNode(parentNode);
	        	                	} else {
	        	                		grid.load();
	        	                	}
	        	                	
	        	        		}
	        	        		    	
	        	            }
	        	        });
	        		} else {
	        			return;
	        		}
	       		}
	        );

		}
		
		function toAddNextNode(nodeId) {
			currentNodeId = nodeId;
			var form = new mini.Form("#nodeForm");
            form.clear();
            mini.get("parentNodeId").setValue(nodeId);
			var win = mini.get("nodeWin");
	        win.showAtPos('center','middle');
		}
		
		function saveDict() {
			var form = new mini.Form("#nodeForm");
            form.validate();
            if (form.isValid()){
            	parentId = mini.get("parentNodeId").getValue();
            	if(!parentId || parentId == '') {
            		mini.get("parentNodeId").setValue('0');
            	}
            	
                var data = form.getData(false,false);      //获取表单多个控件的数据
              	
                $.ajax({
                    url: "${ctx}/sys/code/saveNode",
                    type: "post",
                    data: data,
                    success: function (data) {
                    	var resultObj = mini.decode(data)
                    	if(resultObj.result == 0){
                    		var win = mini.get("nodeWin");
                    		win.hide();
                    		mini.showTips({
    		                    content: "<b>信息</b> <br/>保存成功",
    		                    state: 'info',
    		                    x: 'center',
    		                    y: 'center',
    		                    timeout: 3000
    		                });
                    		var grid = mini.get("dictGrid");
                    		if(currentNodeId != '') {
                    			var node = grid.getNode(currentNodeId);
                    			grid.loadNode(node);
                    		} else {
                    			grid.load();
                    		}
                    		
                           
    		    		}
    		    		if(resultObj.result == 1){
    		    			mini.showTips({
    		                    content: "<b>警告</b> <br/>保存失败,代码重复",
    		                    state: 'warning',
    		                    x: 'center',
    		                    y: 'center',
    		                    timeout: 3000
    		                });
    		    		}
    		    		if(resultObj.result == 2){
    		    			mini.showTips({
    		                    content: "<b>警告</b> <br/>保存失败,组合码重复",
    		                    state: 'warning',
    		                    x: 'center',
    		                    y: 'center',
    		                    timeout: 3000
    		                });
    		    		}
                    }
                });
            }
		}
		
		
        
        
    </script>

</body>
</html>