'use strict';

//展示信息的dialog的controller
function DistSlotCtrl($scope, SlotService,GroupService,MigrateService) {
	
    /***********************执行开始***********************/
    initScope();
    /***********************执行结束***********************/

    /***********************functions***********************/
    function initScope() {
        initParameters();
        initFunctions();
    }
    
    function initParameters() {
    	$scope.data = [{msg:'',type:'',isShow:''}];
    	 $scope.instances = [];
    	 $scope.groups = [];
    	 $scope.slots = [{fromId:'',toId:'',groupId:''}];
    	 $scope.slotsInfo = {
			 instance:'',
    	 	 slots:$scope.slots
    	 };
    	 $scope.isShowGrpInfo = false;
    	 $scope.grpInfo = {};
    	 refreshData();
    }
	
    function refreshData() {
    	getInstances();
		getGroups();
	}
    
    function initFunctions() {
    	
        //不显示提示信息
        $scope.closeAlert = function () {
            $scope.data.isShow = false;
            
        };  
    	
    }
    
    function getInstances() {
    	var instancesResult = MigrateService.getInstances();
    	instancesResult.$promise.then(function(data) {
    		if (data.success) {
    			$scope.instances = data.result;
    		} else {
    			//do nothing
    		}
    	});
    	
    }
    
    function getGroups() {
    	var groupResult = GroupService.getAllGroups();
    	groupResult.$promise.then(function(data) {
    		if (data.success) {
    			$scope.groups = data.result;
    		} else {
    			//do nothing
    		}
    	});
    	
    }
    
    
    $scope.addSlot = function(){
    	$scope.slots.push({fromId:'',toId:'',groupId:''});
		
    };
    
    $scope.remove = function (index) {
        $scope.slots.splice(index, 1);
    };
    
    
    $scope.distSlots = function(){
    	var result = SlotService.distSlots({
            'slotsInfo': $scope.slotsInfo
          });
		showAlert('success', '提交成功！');  
    	processData(result);
    };
    
    function processData(result) {
        result.$promise.then(
            function (data) {
                $scope.isLoading = false;
                if (data.success) {
//                	showAlert('success', 'SLot分配成功！');    
 
                } else {
                    showAlert('warning', '错误！' + data.messages);
                }
         });
    }
    
    function showAlert(type, msg) {
        $scope.data.msg = msg;
        $scope.data.type = type;
        $scope.data.isShow = true;
    }
    
    $scope.showGroup = function(grpId){
    	var groups = $scope.groups;
	    for(var i in groups){
	        if(groups[i].id === grpId){
	        	var grpStr = "master : " + groups[i].master.host + ":" + groups[i].master.port
				+ ",  slave : " + groups[i].slave.host + ":" + groups[i].slave.port;
	        	
	            $scope.grpInfo[grpId+""] = grpStr;
	            $scope.isShowGrpInfo = true;
	            return;
	        }
	    }
	    $scope.isShowGrpInfo = false;
	};
	
}