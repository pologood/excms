var cur = {
	vm : "",
	editor : "",
	opt : []
};

var editor;
var recUnits = function() {

	var init = function() {

		cur.columnId = content_mgr.indicatorId;
		cur.opt = content_mgr.node.functions || [];
		var synColumnIds = content_mgr.node.synColumnIds;
		if (synColumnIds != null) {
			cur.synColumnIds = synColumnIds.split(",");
		}
		var param = [ "siteId=" + GLOBAL_SITEID, "columnId=" + cur.columnId ];

        $("#mycolor").colorpicker({
            color: "#000",
            hideButton: true,
            strings: " , , 无颜色 , , , , "
        }).on('change.color', function(evt, color){
            $('#title').css('color', color);
        });
        $("#colorpicker").on("click", function (evt) {
            evt.stopImmediatePropagation();
            $("#mycolor").colorpicker("showPalette");
        });

/*		editor = KindEditor.create('textarea[name="factReason"]', {
			uploadJson: GLOBAL_CONTEXTPATH + '/videoNews/upload?siteId=' + GLOBAL_SITEID + "&&columnId=" + content_mgr.node.indicatorId,//上传文件
			fileManagerJson: '/fileCenter/getPage',//文件空间
			allowImageUpload: true,
			allowFileManager: false,
			allowFlashUpload: false,
			allowMediaUpload: false,
			filterMode: false,
			siteId: GLOBAL_SITEID,
			columnId:content_mgr.node.columnId,
			fileServerPath: GLOBAL_FILESERVERPATH,
			afterCreate: function () {
				this.sync();
			},
			afterBlur: function () {
				this.sync();
			},
			resizeType: 1
		});*/

/*		// 取色器
		var K = KindEditor;
		var colorpicker;
		K('#colorpicker').bind('click', function(e) {
			e.stopPropagation();
			if (colorpicker) {
				colorpicker.remove();
				colorpicker = null;
				return;
			}
			var colorpickerPos = K('#colorpicker').pos();
			colorpicker = K.colorpicker({
				x : colorpickerPos.x,
				y : colorpickerPos.y + K('#colorpicker').height(),
				z : 19811214,
				selectedColor : 'default',
				noColor : '无颜色',
				click : function(color) {
					K('#title').css('color', color);
					colorpicker.remove();
					article.titleColor = color;
					colorpicker = null;
				}
			});
		});

		K(document).click(function() {
			if (colorpicker) {
				colorpicker.remove();
				colorpicker = null;
			}

		});*/
	}

	var getResource = function() {
		getOrganUnits('recUnit', function(e, treeId, node) {
			console.info(node);
			curr.vm.recUnitId = node.recUnitId;
			curr.vm.recUnitName = node.name;
			// $("#recUnitName").val(node.name);
			$("#dropdowns2").hide();
		})
	}

	var getOrganUnits = function (id, callback, options) {
		var self = this;
		var settings = {
			async: {
				url: "/onlineDeclaration/getRecUnits?columnId=" + cur.columnId,
				dataFilter: function (treeId, parentNode, resData) {
					var data = resData.data;
					for(var i=0, l=data.length;i<l;i++){
						data[i].name = data[i].recUnitName;
						data[i].pid = 0;
					}
					return data;
				}
			},
			data: {
				key: {
					title: "name",
					name: "name"
				},
				data: {
					simpleData: {
						enable: true,
						idKey: "recUnitId",
						pIdKey: "pid"
					}
				}
			},
			callback: {
				onClick: function (e, treeId, node) {
					callback && callback.call(this, e, treeId, node);
				}
			}
		}
		return Ls.dropDowns2(id, $.extend({}, settings, options));
	}

/*	var getOrganUnit = function() {
		getOrganUnits('recUnit', function(e, treeId, node) {
			$("#recUnitId").val(node.recUnitName);
			// article.createOrganId = node.organId;
			$("#dropdowns2").hide();
		})
	}*/

	return {
		init : init,
		getResource : getResource
		// getOrganUnit: getOrganUnit
	}

}();