function SlotCtrl($scope, SlotService,MigrateService) {

	initScope();

	/***********************functions***********************/
	function initScope() {
		initParameters();
		initFunctions();
	}

	function initParameters() {
		$scope.isShow = false;
		$scope.instances = [];
		$scope.slotsInfo = {'instance':'','slots':[], 'groups':[]};
		$scope.tableLength = [];
		$scope.group = {};
		$scope.status = '';
		$scope.slotGidMap = {};
		$scope.colors = ['red','yellow','lightgreen','lightskyblue','lightgray','gold','violet','pink','thistle','lavender','aqua'];
		$scope.fresh = '刷新';
		
		refreshData();
	}

	function refreshData() {
		getInstances();
		setTableLength();
	}
	
	function initFunctions() {
		$scope.getSlotsInfo = function (instance){
			var slotsInfoResult = SlotService.getSlotsInfo({'instance': instance});
			slotsInfoResult.$promise.then(function(data) {
				if (data.success) {
					$scope.slotsInfo = data.result;
					$scope.isShow = false;
					initSlotGidMap();
				} else {
					//do nothing
				}
			});
		};
		
		$scope.reFresh = function(instance){
			if('刷新' === $scope.fresh){
				$scope.fresh = '停止';
				$scope.timer = setInterval($scope.getSlotsInfo, 1000, instance); 
			}else{
				clearInterval( $scope.timer );
				$scope.fresh = '刷新';
			}
		};
		
		$scope.showGroup = function(index){
		    var grpId = getGid(index);
		    if(grpId === -1){
		    	 $scope.isShow = false;
		    	 return;
		    }
		    var groups = $scope.slotsInfo.groups;
		    for(var i in groups){
		        if(groups[i].id === grpId){
		            $scope.group = groups[i];
		            $scope.isShow = true;
		            break;
		        }
		    }
		};
		
		$scope.getStatus = function(index){
			var slots = $scope.slotsInfo.slots;
	        if(slots[index]){
	           $scope.status = "id : "+ index +"\nstatus : " + slots[index].status;
	           return;
	        }
		    $scope.status = "id : "+ index +"\nstatus : OFFLINE";
		}
	}
	
	
	function getInstances() {
		var instanceResult = MigrateService.getInstances();
		instanceResult.$promise.then(function(data) {
			if (data.success) {
				$scope.instances = data.result;
				
			} else {
				//do nothing
			}
		});
	}
	
	function setTableLength(){
	    for(var i=0; i<32; i++){
	        $scope.tableLength.push(i);
	    }
	}

	function getGid (slotId){
	    var slots = $scope.slotsInfo.slots;
	    for(var i in slots){
	        if(slots[i] && slotId === slots[i].id){
	           return slots[i].src_gid;
	        }
	    }
	    return -1;
	}
	
	function initSlotGidMap(){
	    var slots = $scope.slotsInfo.slots;
	    var grpId = 8;
	    var prevGid = null;
	    $scope.slotGidMap = {};
	    for(var i in slots){
	    	if(slots[i]){	    		
	    		if(slots[i].src_gid === null){
	    			$scope.slotGidMap[slots[i].id] = null;
	    		}else{
	    			if(slots[i].src_gid !== prevGid){
	    				prevGid = slots[i].src_gid;
	    				grpId++;
	    			}
	    			$scope.slotGidMap[slots[i].id] = grpId%8;
	    		}
	    	}
	    }
	}

}

