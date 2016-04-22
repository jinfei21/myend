
function DelMigrateCtrl($scope, $modalInstance, migrate, MigrateService){
	
    $scope.name = 'slot_'+ migrate.slot_id;
    $scope.migrate = migrate;

    $scope.alert = {
        type: '',
        msg: '',
        isShow: false
    };
    
    $scope.del = function(){
    	var groupResult = MigrateService.delTask({'data':migrate});
    	
        groupResult.$promise.then(function (data) {
        	$scope.isLoading = false;
        	if (data.success) {           		
				 $modalInstance.close();
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