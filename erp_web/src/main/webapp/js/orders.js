$(function () {
    var url = 'orders_listByPage';
    var btnText = '';
    var inoutTitle = '';
    if (Request['oper'] == 'myorders') {
        url = 'orders_myListByPage?t1.type=' + Request['type'];
        if (Request['type'] * 1 == 1) {
            url = 'orders_myListByPage?t1.type=' + Request['type'];
            document.title = '我的订单';
            btnText = '采购申请';

            $("#addOrderSupplier").html('供应商');
        } else if (Request['type'] * 1 == 2) {
            url = 'orders_myListByPage?t1.type=' + Request['type'] + '&t1.state = 0';
            document.title = '我的销售订单';
            btnText = '销售订单录入';

            $("#addOrderSupplier").html('客户');
        }

    }
    if (Request['oper'] == 'orders' && Request['type'] * 1 == 1) {
        url += '?t1.type=1';
        document.title = '采购订单查询';
    }
    if (Request['oper'] == 'orders' && Request['type'] * 1 == 2) {
        url += '?t1.type=2';
        document.title = '销售订单查询';
    }
    if (Request['oper'] == 'doCheck') {
        url += '?t1.type=1&t1.state=0';
        document.title = '采购订单审核';
    }
    if (Request['oper'] == 'doStart') {
        url += '?t1.type=1&t1.state=1';
        document.title = '采购订单确认';
    }
    if (Request['oper'] == 'doInStore') {
        url += '?t1.type=1&t1.state=2';
        inoutTitle = '入库';
        document.title = '采购订单入库';
    }
    if (Request['oper'] == 'doOutStore') {
        url += '?t1.type=2&t1.state=0';
        inoutTitle = '出库';
        document.title = '销售订单出库';
    }
    //初始化订单列表
    $('#grid').datagrid({
        url: url,
        columns: getColumns(),
        singleSelect: true,
        fitColumns: true,
        pagination: true,     //显示分页工具栏
        onDblClickRow: function (rowIndex, rowData) {
            //显示详情
            $('#uuid').html(rowData.uuid);
            $('#suppliername').html(rowData.supplierName);
            $('#state').html(getState(rowData.state));
            $('#creater').html(rowData.createrName);
            $('#checker').html(rowData.checkerName);
            $('#starter').html(rowData.starterName);
            $('#ender').html(rowData.enderName);
            $('#createtime').html(formatDate(rowData.createtime));
            $('#checktime').html(formatDate(rowData.checktime));
            $('#starttime').html(formatDate(rowData.starttime));
            $('#endtime').html(formatDate(rowData.endtime));
            $('#waybillsn').html(rowData.waybillsn);
            //获取窗口属性
            var options = $('#ordersDlg').dialog('options');
            var toolbar = options.toolbar;
            if (rowData.state * 1 == 1 && rowData.type * 1 == 2) {
                toolbar.push({
                    text:'运单详情',
                    iconCls:'icon-search',
                    handler:function(){
                        $('#waybillDlg').dialog('open');
                        //初始化表格
                        $('#waybillGrid').datagrid({
                            url:'orders_waybillDetailList?waybillSn=' + $('#waybillsn').html(),
                            columns:[[
                                {field:'exedate',title:'执行日期',width:100},
                                {field:'exetime',title:'执行时间',width:100},
                                {field:'info',title:'执行信息',width:100}
                            ]],
                            rownumbers:true
                        });
                    }
                });
                $("#ordersDlg").dialog({
                    toolbar:toolbar
                });
            }

            //打开窗口
            $('#ordersDlg').dialog('open');
            //加载明细表格
            $('#itemgrid').datagrid('loadData', rowData.orderdetails);
        }
    });

    //明细表格
    $('#itemgrid').datagrid({
        columns: [[
            {field: 'uuid', title: '编号', width: 100},
            {field: 'goodsuuid', title: '商品编号', width: 100},
            {field: 'goodsname', title: '商品名称', width: 100},
            {field: 'price', title: '价格', width: 100},
            {field: 'num', title: '数量', width: 100},
            {field: 'money', title: '金额', width: 100},
            {field: 'state', title: '状态', width: 100, formatter: getDetailState}
        ]],
        fitColumns: true,
        singleSelect: true
    });

    var toolbar = new Array();
    //添加审核按钮
    if (Request['oper'] == 'doCheck') {
        toolbar.push({
            text: '审核',
            iconCls: 'icon-search',
            handler: doCheck
        });
    }
    //添加确认按钮
    if (Request['oper'] == 'doStart') {
        toolbar.push({
            text: '确认',
            iconCls: 'icon-search',
            handler: doStart
        });
    }
    //添加导出按钮
    toolbar.push({
        text: '导出',
        iconCls: 'icon-excel',
        handler: doExport
    });

    $("#ordersDlg").dialog({
        toolbar:toolbar
    });

    //添加明细表格双击事件
    if (Request['oper'] == 'doInStore' || Request['oper'] == 'doOutStore') {
        $('#itemgrid').datagrid({
            onDblClickRow:function (rowIndex, rowData) {
                //打开窗口
                $('#itemDlg').dialog('open');
                //给弹窗设置值
                $('#itemuuid').val(rowData.uuid);
                $('#goodsuuid').html(rowData.goodsuuid);
                $('#goodsname').html(rowData.goodsname);
                $('#goodsnum').html(rowData.num);
            }
        });
    }

    //动态添加采购订单按钮
    if (Request['oper'] == 'myorders') {
        $('#grid').datagrid({
            toolbar: [{
                text: btnText,
                iconCls: 'icon-add',
                handler: function(){
                    $('#addOrdersDlg').dialog('open');
                }
            }]
        });
    }

    //初始化入库弹窗
    $('#itemDlg').dialog({
        title: inoutTitle,
        width: 400,
        height: 200,
        closed: true,
        modal: true,
        buttons:[{
            text:inoutTitle,
            iconCls:'icon-save',
            handler:doInOutStore
        }]
    });

    //初始化添加订单窗口
    $('#addOrdersDlg').dialog({
        title: '增加订单',
        width: 700,
        height: 400,
        closed: true,
        modal: true
    });
});

function formatDate(value) {
    return new Date(value).Format('yyyy-MM-dd');
}

function getState(value) {
    if (Request['type'] * 1 == 1) {
        switch (value * 1) {
            case 0:
                return '未审核';
            case 1:
                return '已审核';
            case 2:
                return '已确认';
            case 3:
                return '已入库';
            default :
                return '';
        }
    } else if (Request['type'] * 1 == 2) {
        switch (value * 1) {
            case 0:
                return '未出库';
            case 1:
                return '已出库';
            default :
                return value;
        }
    }

}

function getDetailState(value) {
    if (Request['type'] * 1 == 1) {
        switch (value * 1) {
            case 0:
                return '未入库';
            case 1:
                return '已入库';
            default :
                return '';
        }
    } else if (Request['type'] * 1 == 2) {
        switch (value * 1) {
            case 0:
                return '未出库';
            case 1:
                return '已出库';
            default :
                return '';
        }
    }

}

/**
 * 审核发方
 */
function doCheck() {
    $.messager.confirm('确认', '确认要审核吗？', function (flag) {
        if (flag) {
            $.ajax({
                url: 'orders_doCheck?id=' + $('#uuid').html(),
                dataType: 'json',
                type: 'post',
                success: function (data) {
                    $.messager.alert('提示', data.message, 'info', function () {
                        //关闭弹窗
                        $('#ordersDlg').dialog('close');
                        //重新订单列表加载数据
                        $('#grid').datagrid('reload');
                    })
                }
            });
        }
    });
}

/**
 * 确认方法
 */
function doStart() {
    $.messager.confirm('确认', '确认要确认订单吗？', function (flag) {
        if (flag) {
            $.ajax({
                url: 'orders_doStart?id=' + $('#uuid').html(),
                dataType: 'json',
                type: 'post',
                success: function (data) {
                    $.messager.alert('提示', data.message, 'info', function () {
                        if (data.success) {
                            //关闭窗口
                            $('#ordersDlg').dialog('close');
                            //刷新订单列表数据
                            $('#grid').datagrid('reload');
                        }
                    })
                }
            });
        }
    });
}

/**
 * 入库方法
 */
function doInOutStore() {
    var hintMesseg = '';
    var url = '';
    if (Request['type'] * 1 == 1) {
        hintMesseg = '您确认要入库吗？';
        url = 'orderdetail_doInStore';
    } else if (Request['type'] * 1 == 2) {
        hintMesseg = '您确认要出库吗？';
        url = 'orderdetail_doOutStore';
    }
    var formData = $('#itemForm').serializeJSON();
    if (formData.storeuuid == null || formData.storeuuid == "") {
        $.messager.alert('提示','请选择仓库','info');
        return;
    }
    //询问用户是否入库
    $.messager.confirm('确认对话框', hintMesseg, function(flag){
        if (flag){
            $.ajax({
                url:url,
                dataType:'json',
                data:formData,
                type:'post',
                success:function (data) {
                    if (data.success) {
                        $.messager.alert('提示',data.message,'info',function () {
                            //关闭窗口
                            $('#itemDlg').dialog('close');
                            //修改明细状态为已入库
                            $('#itemgrid').datagrid('getSelected').state = 1;
                            //刷新明细列表更新明细状态
                            var itemData = $('#itemgrid').datagrid('getData');
                            //重新加载
                            $('#itemgrid').datagrid('loadData',itemData);

                            //判断是否所有明细都已经入库
                            var storeFlag = true;
                            $.each(itemData.rows,function (index, element) {
                                if (element.state * 1 == 0) {
                                    storeFlag = false;
                                    return false;
                                }
                            });
                            if (storeFlag) {
                                //关闭明细弹窗
                                $('#ordersDlg').dialog('close');
                                //刷新订单列表
                                $('#grid').datagrid('reload');
                            }
                        });
                    }else {
                        $.messager.alert('提示',data.message,'info');
                    }
                }
            });
        }
    });
}

function getColumns() {
    if (Request['type'] * 1 == 1) {
        return [[
            {field: 'uuid', title: '编号', width: 100},
            {field: 'createtime', title: '生成日期', width: 100, formatter: formatDate},
            {field: 'checktime', title: '审核日期', width: 100, formatter: formatDate},
            {field: 'starttime', title: '确认日期', width: 100, formatter: formatDate},
            {field: 'endtime', title: '入库日期', width: 100, formatter: formatDate},
            {field: 'createrName', title: '下单员', width: 100},
            {field: 'checkerName', title: '审核员', width: 100},
            {field: 'starterName', title: '采购员', width: 100},
            {field: 'enderName', title: '库管员', width: 100},
            {field: 'supplierName', title: '供应商', width: 100},
            {field: 'totalmoney', title: '合计金额', width: 100},
            {field: 'state', title: '状态', width: 100, formatter: getState},
            {field: 'waybillsn', title: '运单号', width: 100}
        ]];
    } else if (Request['type'] * 1 == 2) {
        return [[
            {field: 'uuid', title: '编号', width: 100},
            {field: 'createtime', title: '生成日期', width: 100, formatter: formatDate},
            {field: 'endtime', title: '出库日期', width: 100, formatter: formatDate},
            {field: 'createrName', title: '下单员', width: 100},
            {field: 'enderName', title: '库管员', width: 100},
            {field: 'supplierName', title: '客户', width: 100},
            {field: 'totalmoney', title: '合计金额', width: 100},
            {field: 'state', title: '状态', width: 100, formatter: getState},
            {field: 'waybillsn', title: '运单号', width: 100}
        ]];
    }
}

function doExport() {
    $.download('orders_exportById',{id:$("#uuid").html()});
}