

function GroupListCtrl($scope,$rootScope,GroupService,BaseTableService,$modal,$filter){

	

	initScope();
	  
/***********************functions***********************/
	function initScope() {
	      initParameters();
	      initFunctions();
	}
	   
	function initParameters() {
		refreshData();
	}
	
	
    function refreshData() {
    	
    	var groupResult = GroupService.getAllGroups();
    	
    	groupResult.$promise.then(
                function (data) {
                    $scope.isLoading = false;
                    if (data.success) {
	                    $scope.dataList = data.result;    
	                    $scope.table = BaseTableService.getCustomizedTable($scope, $filter);  
	                    
                    }else{
                    	displayMsgDialog('warning', data.messages);
                    }
                }
         );
    	
    }
	
    function initFunctions() {
	    $scope.addGroup = function () {
	           $scope.message = {
	                title: 'Add Group',
	                msg: '',
	                type:'',
	                isShow:false
	            };
	            var modalInstance = $modal.open({
	                templateUrl: '/jedisx-admin/partials/group/addGroup.html',
	                controller: AddGroupCtrl,
	                resolve: {
	                    data: function () {
	                        return $scope.message;
	                    }
	                }
	            });
	            modalInstance.result.then(function (data) {
					refreshData();
	            }, function () {
	            });
	    };   
	    
	    $scope.deleteGroup = function(group){
	        var modalInstance = $modal.open({
	 
	            templateUrl: '/jedisx-admin/partials/common/deleteConfirm.html',
	            controller: 'DelGroupCtrl',
	            resolve: {
	                group: function () {
	                    return group;
	                }
	            }
	        });
	       

	        modalInstance.result.then(function () {
	            refreshData();
	        }, function () {
	        });
	    }
	    
    }
    
    
    
    function displayMsgDialog(header,body){
		var modalInstance = $modal.open({
				            templateUrl: '/padis-admin/partials/common/message.html',
				            controller: 'MessageCtrl',
				            resolve: {
				                msg: function () {
				                    return {
												headerText:header,
												bodyText:body
											};
				                }
				            }
		});
	}
}