var miniui_datagrid = function () {

  var datagrid = function () {

    //初始化布局
    mini.parse();

    //实例化datagrid
    var grid = mini.get("datagrid1");
    grid.load();

  }

  return {
    datagrid: datagrid
  }

}();