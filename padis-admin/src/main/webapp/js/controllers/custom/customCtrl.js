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
		refreshData();
	}

	function refreshData() {
		var instanceResult = MigrateService.getInstances();
		instanceResult.$promise.then(function(data) {
			if (data.success) {
				$scope.padis.instances = data.result;

			} else {
				console.log(data);
			}
		});

	}

	function initFunctions() {
		$scope.reFresh = function() {
			console.log($scope.curInstance);
			
			var customResult = CustomService.getCustom({
														 'data':$scope.curInstance
														});
			customResult.$promise.then(function(data) {
				if (data.success) {
					$scope.padis.customs = data.result;

				} else {
					console.log(data);
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
					templateUrl : '/padis-admin/partials/custom/updateCustom.html',
					controller : UpdateCustomCtrl,
					resolve : {
						data : function() {							
							return custom;
						}
					}
				});
				modalInstance.result.then(function(data) {
					loadTaskData();
				}, function() {
				});
		}
		
		$scope.open = function(custom) {
			
		}
		
		
	}
}