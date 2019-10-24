//用户表控制层
app.controller('loginController' ,function($scope,$controller  ,loginService){

    $controller('baseController',{$scope:$scope});//继承

    //展示用户名
    $scope.showName=function () {
        loginService.showName().success(
            function (response) {
                $scope.loginName = response;

            }
        )
    }

});