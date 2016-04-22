'use strict';

//展示信息的dialog的controller
function DistSlotCtrl($scope, SlotService,GroupService,MigrateService) {
	
    /***********************执行开始***********************/
    initScope();
    /***********************执行结束***********************/

    /***********************functions***********************/
    function initScope() {
        initParameters();
    }
    
    function initParameters() {
    	 $scope.instances = [];
    	 $scope.groups = [];
    	 $scope.slots = [{fromId:'',toId:'',groupId:''}];
    	 $scope.slotsInfo = {
			 instance:'',
    	 	 slots:$scope.slots
    	 };
    	 
    	 refreshData();
    }
	
    function refreshData() {
    	getInstances();
		getGroups();
	}
    
    function getInstances() {
    	var instancesResult = MigrateService.getInstances();
    	instancesResult.$promise.then(function(data) {
    		if (data.success) {
    			$scope.instances = data.result;
    			console.log("instances: " +JSON.stringify($scope.instances));
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
    			console.log("groups: " +JSON.stringify($scope.groups));
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
    };
    
	
}