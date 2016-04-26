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
		$scope.slotGidMap = {};
		$scope.colors = ['red','yellow','lightgreen','lightskyblue','lightgray','gold','violet','pink','thistle','lavender','aqua'];

		refreshData();
	}

	function refreshData() {
		getInstances();
		setTableLength();
		initSlotGidMap();
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
	    var grpId = '';
	    $scope.slotGidMap = {};
	    for(var i in slots){
	    	if(slots[i]){
	    		grpId = slots[i].src_gid;
		        $scope.slotGidMap[slots[i].id] = grpId;
	    	}
	    }
	}

}





