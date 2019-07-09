$(function () {
    // $('#tree').tree({
    //     url:'role_readRoleMenus?id=' + 0,
    //     animate:true,
    //     checkbox:true
    // });

    $('#grid').datagrid({
        url:'emp_list',
        columns:[[
            {field:'uuid',title:'编号',width:100},
            {field:'name',title:'名称',width:100}
        ]],
        singleSelect:true,
        onClickRow:function (rowIndex, rowData) {
            $('#tree').tree({
                url:'emp_readEmpRoles?id=' + rowData.uuid,
                animate:true,
                checkbox:true
            });
        }
    });

    $('#btnSave').click(function () {
        var checkMenu = $('#tree').tree('getChecked');
        var ids = new Array();
        $.each(checkMenu,function (i, e) {
            ids.push(e.id);
        });
        var fromData = {};
        var checkedStr = ids.join(',');
        var id = $('#grid').datagrid('getSelected').uuid;
        $.ajax({
            url:'emp_updateEmpRoles',
            data: {id:id,checkedStr:checkedStr},
            dataType:'json',
            success:function (rtn) {
                $.messager.alert('提示',rtn.message,'info');
            }
        });
    });
});