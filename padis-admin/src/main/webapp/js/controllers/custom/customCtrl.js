function CustomListCtrl($scope, $rootScope, MigrateService, CustomService,
		$modal, $filter) {

	initScope();

	/***********************functions***********************/
	function initScope() {
		initParameters();
		initFunctions();
	}

	function initParameters() {

		$scope.padis = {
			'instances' : [],
			'customs' : [],
		}
		$scope.data = {
				msg : '',
				type : '',
				isShow : false,
		}
		refreshData();
	}

	function refreshData() {
		var instanceResult = MigrateService.getInstances();
		instanceResult.$promise.then(function(data) {
			if (data.success) {
				$scope.padis.instances = data.result;

			} else {
				showAlert('warning', data.messages);
			}
		});

	}

	function initFunctions() {
        //不显示提示信息
        $scope.closeAlert = function () {
            $scope.data.isShow = false;
            
        };  
        
		$scope.reFresh = function() {
			console.log($scope.curInstance);
			
			var customResult = CustomService.getCustom({
														 'data':$scope.curInstance
														});
			customResult.$promise.then(function(data) {
				if (data.success) {
					$scope.padis.customs = data.result;

				} else {
					showAlert('warning', data.messages);
				}
			});
		}
		
		
		$scope.close = function(custom) {
			$scope.message = {
					title : 'Limit custom',
					msg : '',
					type : '',
					isShow : false,
					custom:custom
				};
				var modalInstance = $modal.open({
					templateUrl : '/jedisx-admin/partials/custom/updateCustom.html',
					controller : UpdateCustomCtrl,
					resolve : {
						data : function() {		
							return custom;
						}
					}
				});
				modalInstance.result.then(function(data) {
					$scope.reFresh();
				}, function() {
				});
		}
		
		$scope.open = function(custom) {
			custom.status = 'oline'
			custom.limit = -1;
        	var result = CustomService.updateCustom({
                'data': custom
              });
            result.$promise.then(
                    function (data) {
                    	console.log(data);
                        $scope.isLoading = false;
                        if (data.success) {         
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
}