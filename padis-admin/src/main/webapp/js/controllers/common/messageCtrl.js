'use strict';

//展示信息的dialog的controller
function MessageCtrl($scope, $modalInstance, msg) {
    /***********************执行开始***********************/
    initScope();
    /***********************执行结束***********************/


    /***********************functions***********************/
    function initScope() {
        initParameters();
        initFunctions();
    }

    function initParameters() {
        //展示的信息
        $scope.msg = msg;
    }

    function initFunctions() {
        //点击确认按钮
        $scope.ok = function () {
            $modalInstance.close('ok');
        };
        //点击取消按钮
        $scope.cancel = function () {
            $modalInstance.dismiss('cancel');
        };
    }
}
