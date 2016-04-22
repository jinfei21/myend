angular.module("slotService", ['ngResource'])
    .factory("SlotService", ['$resource', '$routeParams',
        function ($resource, $routeParams) {
            return $resource("/padis-admin/slot/:action",
                {},
                {
                    distSlots:{
                    	method:'POST',
                        params: {
                            action: 'distSlots',
                            slotsInfo:'@slotsInfo'
                        }
                    },
                    getInstances: {
                        method: 'GET',
                        params: {
                            action: 'getInstances'
                        }
                    },
                    getSlotsInfo: {
                    	method: 'POST',
                    	params: {
                    		action: 'getSlotsInfo',
                    		instance:'@instance'
                    	}
                    }
                }
            );
        }
]);