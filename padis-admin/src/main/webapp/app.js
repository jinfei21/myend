/**
 * create by feiyongjun 
 */
'use strict';

var module = angular.module('portal', ['ngRoute',
				'ngResource',
				'groupService',
				'migrateService',
				'customService',
				'loginService',
				'slotService',
				'baseTableService',
				'ui.bootstrap']);
				
	module.config(['$routeProvider',
    function ($routeProvider) {

        $routeProvider
        	.when("/group",{
                templateUrl: '/jedisx-admin/partials/group/group.html',
                reloadOnSearch: false
            }).when("/group/:id",{
                templateUrl: '/jedisx-admin/partials/group/showGroup.html',
                reloadOnSearch: false
            }).when("/migrate",{
                templateUrl: '/jedisx-admin/partials/migrate/migrate.html',
                reloadOnSearch: false
            }).when("/custom",{
                templateUrl: '/jedisx-admin/partials/custom/custom.html',
                reloadOnSearch: false
            }).when("/slot",{
            	templateUrl: '/jedisx-admin/partials/slot/slot.html',
            	reloadOnSearch: false	
            }).when("/distSlot",{
            	templateUrl: '/jedisx-admin/partials/slot/distSlot.html',
            	reloadOnSearch: false	
            }).otherwise({
                templateUrl: '/jedisx-admin/partials/group/group.html',
                reloadOnSearch: false
            });
    }]);
	 