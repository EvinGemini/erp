var exsitEditIndex = -1;
$(function () {
    $('#ordersgrid').datagrid({
        singleSelect:true,
        showFooter:true,
        columns:[[
            {field:'goodsuuid',title:'商品编号',width:100,editor:{type:'numberbox',options:{disabled:true}}},
            {field:'goodsname',title:'商品名称',width:100,editor:{type:'combobox',options:{
                        url:'goods_list',
                        textField:'name',
                        valueField:'name',
                        onSelect:function (goods) {
                            //获取商品编号编辑器
                            var goodsuuidEdit = getEdit('goodsuuid');
                            $(goodsuuidEdit.target).val(goods.uuid);
                            //获取商品价格编辑器
                            var priceEdit = getEdit('price');
                            if (Request['type'] * 1 == 1) {
                                //设置进货价
                                $(priceEdit.target).val(goods.inprice);
                            } else if (Request['type'] * 1 == 2) {
                                //设置销售价
                                $(priceEdit.target).val(goods.outprice);
                            }
                            //获取数量编辑器，选中的时候让数量编辑器选中
                            var numEdit = getEdit('num');
                            $(numEdit.target).select();
                            bindGridEvent();
                            calculate();
                        }
            }}},
            {field:'price',title:'价格',width:100,editor:{type:'numberbox',options:{precision:2}}},
            {field:'num',title:'数量',width:100,editor:{type:'numberbox'}},
            {field:'money',title:'金额',width:100,editor:{type:'numberbox',options:{
                        disabled:true,
                        precision:2
            }}},
            {field:'-',title:'操作',width:100,formatter: function(value,row,index) {
                if (row.num == '合计') {
                    return ;
                }
                return '<a href="javascript:void(0);" onclick="deleteRow('+index+')">' + '删除' + '</a>';
            }}
        ]],
        onClickRow:function(rowIndex,rowData) {
            $('#ordersgrid').datagrid('endEdit',exsitEditIndex);
            exsitEditIndex = rowIndex;
            $('#ordersgrid').datagrid('beginEdit',exsitEditIndex);
            bindGridEvent();
        },
        toolbar:[{
            text:'添加',
            iconCls: 'icon-add',
            handler: function(){
                if (exsitEditIndex != -1) {
                    $('#ordersgrid').datagrid('endEdit',exsitEditIndex);
                }
                $('#ordersgrid').datagrid('appendRow',{
                    num: 0,
                    money: 0
                });
                exsitEditIndex = $('#ordersgrid').datagrid('getRows').length - 1;
                $('#ordersgrid').datagrid('beginEdit',exsitEditIndex);
            }
        },'-',{
            text:'提交',
            iconCls: 'icon-save',
            handler: function(){
                //关闭编辑状态的行，保证获取完整数据
                if (exsitEditIndex > -1) {
                    $('#ordersgrid').datagrid('endEdit',exsitEditIndex);
                    //获取供应商id
                    var data = $('#orderForm').serializeJSON();
                    if (data['t.supplieruuid'] == '') {
                        $.messager.alert('提示','请选择供应商','info');
                        return;
                    }
                    //获取行的数据
                    var rows = $('#ordersgrid').datagrid('getRows');
                    if (rows.length <= 0) {
                        $.messager.alert('提示','请选择采购的商品','info');
                        return;
                    }
                    //把rows转json在设置给data
                    data.json = JSON.stringify(rows);
                    $.ajax({
                        url:'orders_add' + '?t.type=' + Request['type'],
                        data:data,
                        dataType:'json',
                        type:'post',
                        success:function (rtn) {
                            $.messager.alert('提示',rtn.message,'info',function () {
                                //清空供应商
                                $('#supplier').combogrid('clear');
                                //重新加载表格数据
                                $('#ordersgrid').datagrid('loadData',{total:0,rows:[],footer:[{num:'合计',money:0}]});
                                //关闭窗口
                                $('#addOrdersDlg').dialog('close');
                                //刷新表格
                                $('#grid').datagrid('reload');
                            });
                        }

                    });
                }

            }
        }],
    });

    //初始化页脚行
    $('#ordersgrid').datagrid('reloadFooter',[
        {num: '合计', money: 0}
    ]);

    //初始化供应商下拉列表
    $('#supplier').combogrid({
        panelWidth:700,
        idField:'uuid',
        textField:'name',
        url:'supplier_list?t1.type=' + Request['type'],
        columns:[[
            {field:'uuid',title:'编号',width:100},
            {field:'name',title:'名称',width:100},
            {field:'address',title:'联系地址',width:100},
            {field:'contact',title:'联系人',width:100},
            {field:'tele',title:'联系电话',width:100},
            {field:'email',title:'邮件地址',width:150},
        ]]
    });

});

function getEdit(_field) {
    return $('#ordersgrid').datagrid('getEditor',{index:exsitEditIndex,field:_field});
}

//计算金额
function calculate() {
    //获取商品数量
    var numEdit = getEdit('num');
    var num = $(numEdit.target).val();
    //获取商品价格
    var priceEdit = getEdit('price');
    var inprice = $(priceEdit.target).val();
    //计算金额,保留两位小数
    var money = (num * inprice).toFixed(2);
    //设置价格
    var moneyEdit = getEdit('money');
    var num = $(moneyEdit.target).val(money);
    //设置金额到rows中
    $('#ordersgrid').datagrid('getRows')[exsitEditIndex].money = money;
    sum();
}

//编辑器绑定事件
function bindGridEvent() {
    //获取价格编辑器并绑定事件
    var priceEdit = getEdit('price');
    $(priceEdit.target).bind('keyup',function () {
        calculate();
    });
    //获取数量编辑器并绑定事件
    var numEdit = getEdit('num');
    $(numEdit.target).bind('keyup',function () {
        calculate();
    });
}

//删除行
function deleteRow(index) {
    //关闭编辑
    $('#ordersgrid').datagrid('endEdit',exsitEditIndex);
    //删除行
    $('#ordersgrid').datagrid('deleteRow',index);
    //获取datagrid数据
    var data = $('#ordersgrid').datagrid('getData');
    //重新加载数据
    $('#ordersgrid').datagrid('loadData',data);
    sum();
}

//计算总金额
function sum() {
    var rows = $('#ordersgrid').datagrid('getRows');
    var total = 0;
    $.each(rows,function (index, element) {
        total += parseFloat(element.money);
    })
    total = total.toFixed(2);
    //加载页脚行
    $('#ordersgrid').datagrid('reloadFooter',[
        {num: '合计', money: total}
    ]);
}