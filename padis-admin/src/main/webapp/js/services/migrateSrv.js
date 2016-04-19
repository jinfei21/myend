angular.module("migrateService", ['ngResource'])
    .factory("MigrateService", ['$resource', '$routeParams',
        function ($resource, $routeParams) {
            return $resource("/padis-admin/migrate/:action",
                {},
                {
                    getTask: {
                        method: 'GET',
                        params: {
                            action: 'getTask',
                            data: '@data'
                        }
                    }, 
                    getInstances: {
                        method: 'GET',
                        params: {
                            action: 'getInstances'
                        }
                    }, 
                    addTask:{
                    	method:'POST',
                        params: {
                            action: 'addTask',
                            data:'@data'
                        }
                    }
                }
            );
        }
]);