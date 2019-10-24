//用户表控制层
app.controller('payController' ,function($scope,$controller   ,payService) {

    $controller('baseController', {$scope: $scope});//继承

   $scope.createNative = function () {
       payService.createNative().success(
           function (response) {
               if(response){
                  $scope.out_trade_no =  response.out_trade_no;
                  $scope.total_amount =  response.total_amount;
                  var qri = new QRious({
                      element:document.getElementById('qrious'),
                      size:200,
                      level:'H',
                      value:response.qrcode
                  });
                  //查询状态
                   queryPayStatus($scope.out_trade_no);
               }
           }
       )
       queryPayStatus = function (out_trade_no) {
           payService.queryPayStatus(out_trade_no).success(
               function (response) {
                   if (response.success){
                       location.href="paysuccess.html";
                   } else {
                       //如果超时,重新生成二维码
                       if(response.tradestatus == 'timeout'){
                           $scope.createNative();
                       }else {
                           location.href="payfail.html";
                       }

                   }
               }
           )
       }
   }
    
})