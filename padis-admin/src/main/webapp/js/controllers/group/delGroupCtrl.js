
function DelGroupCtrl($scope, $modalInstance, group, GroupService){
	
    $scope.name = 'group_'+group.id;
    $scope.group = group;

    $scope.alert = {
        type: '',
        msg: '',
        isShow: false
    };
    
    $scope.del = function(){
    	var groupResult = GroupService.delGroupByID({'id':group.id});
    	
        groupResult.$promise.then(function (data) {
        	$scope.isLoading = false;
        	if (data.success) {           		
				$scope.alert = {
    					type: 'info',
    					msg: 'OK',
    					isShow: true
    			};
        	}else{
				$scope.alert = {
    					type: 'alert-danger',
    					msg: data.messages,
    					isShow: true
    			};        		
        	}
        });
    };
    
    $scope.cancel = function(){
        $modalInstance.close();
    };
}