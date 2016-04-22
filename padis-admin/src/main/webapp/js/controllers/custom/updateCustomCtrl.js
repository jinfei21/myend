
function UpdateCustomCtrl($scope,$rootScope,CustomService,data,BaseTableService,$modalInstance,$filter){

	

	initScope();
	  
/***********************functions***********************/
	function initScope() {
	      initParameters();
	      initFunctions();
	}
	
	function initParameters() {
		$scope.custom = data;
	}
    
    function initFunctions() {
    	
        //不显示提示信息
        $scope.closeAlert = function () {
            $scope.data.isShow = false;
            
        };  
        
        $scope.cancel = function(){
            $modalInstance.dismiss('cancel');
        };
        
        $scope.ok = function(custom){
			custom.status = 'limit';
        	var result = CustomService.updateCustom({
                'data': custom
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