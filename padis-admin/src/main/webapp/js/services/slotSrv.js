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