angular.module("groupService", ['ngResource'])
    .factory("GroupService", ['$resource', '$routeParams',
        function ($resource, $routeParams) {
            return $resource("/padis-admin/group/:action",
                {},
                {
                	getAllGroups: {
                		method: 'GET',
                		params: {
                            action: 'groupList'
                		}
                	},    
                    getGroupByID: {
                        method: 'GET',
                        params: {
                            action: 'getGroup',
                            id: '@id'
                        }
                    }, 
                    delGroupByID: {
                        method: 'GET',
                        params: {
                            action: 'delGroup',
                            id: '@id'
                        }
                    }, 
                    updateGroup:{
                    	method:'POST',
                        params: {
                            action: 'updateGroup',
                            group:'@group'
                        }
                    },
                    addGroup:{
                    	method:'POST',
                        params: {
                            action: 'addGroup',
                            group:'@group'
                        }
                    }
                }
            );
        }
]);