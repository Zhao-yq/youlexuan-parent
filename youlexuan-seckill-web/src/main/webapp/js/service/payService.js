//服务层
app.service('payService',function($http){

    //查询购物车列表
    this.createNative = function () {
        return $http.get('../pay/createNative.do');
    }

    this.queryPayStatus = function (out_trade_no) {
        return $http.get('../pay/queryPayStatus.do?out_trade_no='+out_trade_no);
    }

})