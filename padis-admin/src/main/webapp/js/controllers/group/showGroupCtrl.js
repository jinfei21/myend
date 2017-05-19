

function ShowGroupCtrl($scope, $http, $modal, $routeParams, GroupService) {
    
	initScope();

    /***********************functions***********************/
    function initScope() {
        initParameters();
        initFunctions();
    }

    function initParameters() {
        //提示信息
        $scope.data = {
            type: '',
            msg: '',
            isShow: false
        };
        
        var groupResult = GroupService.getGroupByID({'id':$routeParams.id})
        
        groupResult.$promise.then(function (data) {
        	$scope.isLoading = false;
        	if (data.success) {           		
        		$scope.group = data.result;
        		$scope.master = data.result.master.host+':'+data.result.master.port;
        		$scope.slave = data.result.slave.host+':'+data.result.slave.port;
        		
        	}else{
        		showAlert('warning', data.messages);
        	}
        });
    
    }
    
    function initFunctions() {
    	
    	$scope.closeGroup = function(){
    		 window.location = "/padis-admin/#/group";
    	}
        
        $scope.closeAlert = function () {
        	$scope.data.isShow = false;
        }
        
        $scope.updateGroup = function (group) {
        	group.master = $scope.master;
        	group.slave = $scope.slave;
            var groupResult = GroupService.updateGroup({'group':group})
            
            groupResult.$promise.then(function (data) {
            	$scope.isLoading = false;
            	if (data.success) {           		
            		showAlert('info', 'OK'); 
            	}else{
            		showAlert('warning', data.messages);
            	}
            });
        }
    }
    
    //显示提示信息
    function showAlert(type, msg) {
        $scope.data.msg = msg;
        $scope.data.type = type;
        $scope.data.isShow = true;
    }
}