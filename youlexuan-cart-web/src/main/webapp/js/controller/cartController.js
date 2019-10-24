//用户表控制层
app.controller('cartController' ,function($scope,$controller   ,cartService) {

    $controller('baseController', {$scope: $scope});//继承

    //查询购物车列表
    $scope.findCartList = function () {
        cartService.findCartList().success(
            function (response) {
                $scope.carList = response;
            }
        )
    }
    //添加商品到购物车
    $scope.addGoodsToCartList= function (itemId,num) {
        cartService.addGoodsToCartList(itemId,num).success(
            function (response) {
                if (response.success){
                    $scope.findCartList();
                }else {
                    alert(response.message);
                }
            }
        )
    }
    
    //查询购物车列表
    $scope.findCartList = function () {
        cartService.findCartList().success(
            function (response) {
                $scope.carList = response;
                $scope.totalValue = cartService.sum($scope.carList);//求合计数
            }
        )
    }

    $scope.findAddressList = function () {
        cartService.findAddressList().success(
            function (response) {
                $scope.addressList = response;
                for(var i =0;i<$scope.addressList.length;i++){
                    //判断默认地址
                    if($scope.addressList[i].isDefault=='1'){
                        $scope.sAddress = $scope.addressList[i];
                        break;
                    }
                }
            }
        )
    }

    $scope.selectAddress = function (addr) {
        $scope.sAddress = addr;
    }

    //判断当前选择的地址和地址列表中的项是否一样   可以不写
    $scope.isSelectedAddress = function (addr) {
        if (addr == $scope.sAddress){
            return true;
        } else {
            return false;
        }
    }

    /**
     * 支付方式  加工提交订单的基本信息
     * @type {{paymentType: string}}
     */
    $scope.order = {'paymentType':'1'};
    $scope.selectPayType = function (type) {
        $scope.order.paymentType = type;
    }

    /**
     * 提交订单信息
     */
    $scope.submitOrder = function () {
        //加工数据
        $scope.order.receiverAreaName = $scope.sAddress.address;
        $scope.order.receiverMobile = $scope.sAddress.mobile;
        $scope.order.receiver = $scope.sAddress.contact;

        cartService.submitOrder($scope.order).success(
            function (response) {
                if (response.success){
                    if($scope.order.paymentType=='1'){
                        window.location.href='pay.html';
                    }else {
                        alert("订单提交成功");
                    }
                }else {
                 alert(response.message);
                }
            }
        )
    }
    
})