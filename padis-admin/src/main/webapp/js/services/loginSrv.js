angular.module("loginService", ['ngResource'])
    .factory("LoginService", ['$resource', '$routeParams',
        function ($resource, $routeParams) {
            return $resource("/padis-admin/login/:action",
                {},
                {
                    login: {
                        method: 'GET',
                        params: {
                            action: 'authorize',
                            name: '@name',
                            passwd:'@passwd'
                        }
                    }
                }
            );
        }
]);