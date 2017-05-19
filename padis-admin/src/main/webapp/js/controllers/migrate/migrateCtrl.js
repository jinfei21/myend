function MigrateListCtrl($scope, $rootScope, MigrateService, BaseTableService,
		$modal, $filter, $timeout) {

	initScope();

	/***********************functions***********************/
	function initScope() {
		initParameters();
		initFunctions();
	}

	function initParameters() {

		$scope.padis = {
			'instances' : [],
			'migrates' : [],
			'fresh' : '刷新'
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
		
		$scope.addTask = function() {
			$scope.message = {
				title : 'Add Migrate',
				msg : '',
				type : '',
				isShow : false,
				instance:$scope.curInstance
			};
			var modalInstance = $modal.open({
				templateUrl : '/padis-admin/partials/migrate/addMigrate.html',
				controller : AddMigrateCtrl,
				resolve : {
					data : function() {
						$scope.message.instance = $scope.curInstance;
						return $scope.message;
					}
				}
			});
			modalInstance.result.then(function(data) {
				loadTaskData();
			}, function() {
			});
		};
		
		$scope.reFresh = function(){
			if('刷新' === $scope.padis.fresh){
				$scope.padis.fresh = '停止'
					
				$scope.timer = setInterval(loadTaskData,1000); 
			}else{
				clearInterval( $scope.timer );
				$scope.padis.fresh = '刷新'
			}
		}
		
	    $scope.deleteMigrate = function(migrate){
	        var modalInstance = $modal.open({
	 
	            templateUrl: '/padis-admin/partials/common/deleteConfirm.html',
	            controller: 'DelMigrateCtrl',
	            resolve: {
	                migrate: function () {
	                	migrate.instance = $scope.curInstance;
	                    return migrate;
	                }
	            }
	        });
	       

	        modalInstance.result.then(function () {
	        	loadTaskData();
	        }, function () {
	        });
	    }
	}
	
	function loadTaskData(){
		var taskResult = MigrateService.getTask({
										'data':$scope.curInstance
										});
		taskResult.$promise.then(function(data) {
			if (data.success) {
				$scope.padis.migrates = data.result;

			} else {
				$scope.padis.migrates = [];
				console.log(data.messages);
			}
		});
	}

}