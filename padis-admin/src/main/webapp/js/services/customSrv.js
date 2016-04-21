angular.module("customService", ['ngResource'])
    .factory("CustomService", ['$resource', '$routeParams',
        function ($resource, $routeParams) {
            return $resource("/padis-admin/custom/:action",
                {},
                {
                    getCustom: {
                        method: 'GET',
                        params: {
                            action: 'getCustom',
                            data: '@data'
                        }
                    }, 
                    updateCustom:{
                    	method:'POST',
                        params: {
                            action: 'updateCustom',
                            data:'@data'
                        }
                    }
                }
            );
        }
]);