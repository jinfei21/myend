/**
 * create by feiyongjun 
 */
'use strict';

var module = angular.module('portal', ['ngRoute',
				'ngResource',
				'groupService',
				'migrateService',
				'customService',
				'baseTableService',
				'ui.bootstrap']);
				
	module.config(['$routeProvider',
    function ($routeProvider) {

        $routeProvider
        	.when("/group",{
                templateUrl: '/padis-admin/partials/group/group.html',
                reloadOnSearch: false
            }).when("/group/:id",{
                templateUrl: '/padis-admin/partials/group/showGroup.html',
                reloadOnSearch: false
            }).when("/migrate",{
                templateUrl: '/padis-admin/partials/migrate/migrate.html',
                reloadOnSearch: false
            }).when("/custom",{
                templateUrl: '/padis-admin/partials/custom/custom.html',
                reloadOnSearch: false
            }).otherwise({
                templateUrl: '/padis-admin/partials/group/group.html',
                reloadOnSearch: false
            });
    }]);