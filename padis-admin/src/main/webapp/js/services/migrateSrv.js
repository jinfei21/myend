angular.module("migrateService", ['ngResource'])
    .factory("MigrateService", ['$resource', '$routeParams',
        function ($resource, $routeParams) {
            return $resource("/jedisx-admin/migrate/:action",
                {},
                {
                    getTask: {
                        method: 'GET',
                        params: {
                            action: 'getTask',
                            data: '@data'
                        }
                    }, 
                    delTask: {
                        method: 'POST',
                        params: {
                            action: 'delTask',
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