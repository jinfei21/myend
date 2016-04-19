
function AddMigrateCtrl($scope,$rootScope,MigrateService,data,BaseTableService,$modalInstance,$filter,$timeout){

	

	initScope();
	  
/***********************functions***********************/
	function initScope() {
	      initParameters();
	      initFunctions();
	}
	
	function initParameters() {
		$scope.data = data;
	}
    
    function initFunctions() {
    	
        //不显示提示信息
        $scope.closeAlert = function () {
            $scope.data.isShow = false;
            
        };  
        
        $scope.cancel = function(){
            $modalInstance.dismiss('cancel');
        };
        
        $scope.ok = function(task){
        	task.instance = $scope.data.instance;
        	var result = MigrateService.addTask({
                'data': task
              });
        	processData(result);
        }
        
    }
	
	function processData(result) {
        result.$promise.then(
            function (data) {
            	console.log(data);
                $scope.isLoading = false;
                if (data.success) {
                   $modalInstance.close('ok');    
 
                } else {
                    showAlert('warning', data.messages);
                }
         });
    }
	
    //显示提示信息
    function showAlert(type, msg) {
        $scope.data.msg = msg;
        $scope.data.type = type;
        $scope.data.isShow = true;
    }
}