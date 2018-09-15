/**
*   前端方法类 - 模块主页
*/

(function(){
	Ls.moduleIndex = Ls.lang.createClass(function(options){
		this.name="",
		this.icon="",
		this.title$Id = "#left-title",
		// 
		this.conCss = 'webform';
		// 
		this.menuTpl1 = '/app/common/tpl/nav_1.html';
		this.menuTpl2 = '/app/common/tpl/nav_2.html';
		this.menuTplCacheKey1='menuTplCache_nav1';
		this.menuTplCacheKey2='menuTplCache_nav2';
		this.menuCon$Id = '#left-nav';
		this.collspan$Id = '#collspan_div';
		this.menuDataAction =  'getMenu';
		this.menuDataUrl =  Ls.path.CUR_PATH + 'data/menu.txt';
		this.menuCss1 = '/app/common/css/nav_1.css';
		this.menuCss2 = '/app/common/css/nav_2.css';
		this.height__ = 110;
		this.leftCellWidth = 180;
		this.level = 2;
		this.leftCellId = "left";
		this.mainUrl = "";
		this.leftCell = null;
		this.loading$Id = "#pageLoading";
		this.isCache = false;
		this.pattern = '2U';
		this.indicatorId = Ls.isNul(GUR_PARENTID) ? Ls.url.get('indicatorId') : GUR_PARENTID ;
		
		Ls.object.extend(this,options ||{});
		
	}).extend({
		// 初始化
		init:function(){
			var me = this;
			// 根据level加载css
			if(me.level===1){
				Ls.__loadCss(me.menuCss1);
			}
			if(me.level===2){
				Ls.__loadCss(me.menuCss2);
			}
			Ls.log.add('me.mainUrl = '+me.mainUrl);
			var titCon = $(me.title$Id);
			var str = me.name;
			if(Ls.isNul(str)){
				alert("模块名称不能为空!");
				return;	
			}
			if(!Ls.isNul(me.icon))
				str = '<img src="'+me.icon+'" width="32" height="32">  '+me.name;
			titCon.html(str);
			
			me.loading = $(me.loading$Id);
			me.getMenuData(me);
			//me.createLayout(me).then(function(){
				//me.getMenuData(me);
			//});
		},
		// 设置主区域的内容
		setMainUrl:function(cls,url,indicatorId){
			if(Ls.isNul(url))return;
			url = url.addEtc();
			if(indicatorId)
				url = url.setUrlParam("indicatorId",indicatorId);
			//url = url.setUrlParam("curName",name);
			// 附加URL
			if(!Ls.isNul(url) && url.toLowerCase().indexOf('.txt')==-1 
		  		&& url.toLowerCase().indexOf('.json')==-1
		  		&& url.toLowerCase().indexOf('.html')==-1 
		  		&& url.toLowerCase().indexOf('.htm')==-1 
		  		&& !Ls.string.startWith(url.toLowerCase(),'http://')
		  		&& !Ls.string.startWith(url.toLowerCase(),'https://') ){		
		  		url = window.WEBCONTEXT ? window.WEBCONTEXT + url : url;
		  	}
			Ls.log.add('url = ' + url);
			//var iframeHtml='<iframe src="'+url+'" id="centerRegionFrame" name="centerRegionFrame"  frameborder="0" style="width:100%;height:100%; overflow:hidden"></iframe>';
			
			/*try{ 
				cls.mainFrame[0].src = "about:blank";
				cls.mainFrame[0].contentWindow.document.write('');
				cls.mainFrame[0].contentWindow.close();
				cls.mainFrame.remove();
				if( typeof CollectGarbage == "function") {
				  CollectGarbage();
				}
			}catch(e){}; */
			//以上可以清除大部分的内存和文档节点记录数了 
			//cur.layout.getRegionBodyEl('center').innerHTML=iframeHtml;
			//cls.mainFrame.attr({'src':url,'name':'m-'+new Date().getTime});
			cls.mainFrame[0].src = url; 
		},
		
		// 获取导航菜单数据
		getMenuData:function(cls){
			// 异步加载数据
			// 正式环境下请使用config.js中统一配置rpc.admin.pro每个模板所对应的远程服务地址
			// 然后在当前页的指定Ls.ajax.url = rpc.admin.pro.服务接口配置变量
			// 模板数据测试情况下
			// Ls.log.add('me.menuDataUrl = '+cls.menuDataUrl)
			// 引处加入本地缓存+cookie机制进行前端缓存
			// 1. 检查cookie是否有效
			Ls.log.add('getMenuData >>>');
			var userId = Ls.cookie.get('userId');
			var mdataKey ='m-'+userId+'-'+cls.indicatorId;
			Ls.log.add('mdataKey='+mdataKey);
			/*if(!Ls.isNul(Ls.cookie.get(mdataKey))){
				Ls.log.add('cookie有效 >>>');
			   	// 根据key查询本地存储
				Ls.log.add('从本地存储中读取数据 >>>');
				var mdata = Ls.data.getJson(mdataKey);
				Ls.log.add(mdata);
				if(!Ls.isNul(mdata)){
					setTimeout(function(){
                        cls.render(cls, mdata)},10);
				}else{
					cls.getMenuData4ajax(cls);
				}
			}else{
				cls.getMenuData4ajax(cls);
			}*/
			cls.getMenuData4ajax(cls);			
		},
		getMenuData4ajax:function(cls){
			Ls.log.add('从服务端读取数据 >>>');
			Ls.log.add('getMenuData4ajax >>>');
			return Ls.ajax.get(cls.menuDataUrl,{
				indicatorId : cls.indicatorId
			},function(json){
				var userId = Ls.cookie.get('userId');
				var mdataKey ='m-'+userId+'-'+cls.indicatorId;
				Ls.cookie.set(mdataKey,mdataKey,{expires:1000*60*60*6});
				Ls.data.setJson(mdataKey,json);
                setTimeout(function () {
                    cls.render(cls, json)
                }, 10);
			},null,null,{isShowLoading:false,async:false});
		},
		render:function(cls,json){
			// 使用模板渲染
				var tpl = cls.menuTpl2,
				data = json.data;				
				if(cls.level===1)tpl=cls.menuTpl1;				
				Ls.tpl.render(
					tpl, // 模板内容,可以是当前页,也可是外链的html
					cls.menuCon$Id, // 模板渲染成功后要填充的对象
					json // 模板渲染时所需要的数据对象
				,function(){
					// 此处通过cookie来判断是否是初始加载,如果是初次加载为了确保字体图标能够及时显示
					// 采用二次加载渲染机制来解决.
					//var checkFontIconInCache = Ls.cookie.get('checkFontIconInCache');
					//Ls.log.add('checkFontIconInCache='+checkFontIconInCache);
		  			//if(Ls.isNul(checkFontIconInCache)){
					//	Ls.log.add('set checkFontIconInCache=1 >>>')
					//	Ls.cookie.set('checkFontIconInCache',1);
		  			//	setTimeout(function(){cls.render(cls,json);},2000);
		  			//}else{
						// 将生成html存入到本地缓存
						//Ls.store.set(GlobalVer.cacheKey.menu.emergencyMgr,html);
						// 绑定事件
						cls.bindMenuItemActiveEvent(cls);
	
						
						// 绑定菜单的收缩和展开事件
						if(cls.level===2){
						    cls.bindMenuLev1ExpCollEvent(cls);
						}
	          
						//查找默认选种菜单，如果有设置右侧主页
                        var goUrl = "",goMenuID="";
                        for (var i = 0; i < data.length; i++) {
                            if (data[i].uri) {
                                goMenuID = data[i].indicatorId;
                                goUrl = data[i].uri;
                                break;
                            }
                            if (data[i].items && data[i].items.length > 0) {
                                for (var j = 0; j < data[i].items.length; i++) {		                                
                                    goMenuID = data[i].items[j].indicatorId;
                                    goUrl = data[i].items[j].uri;
                                    break;		                                
	                            }
	                            if(!Ls.isNul(goMenuID) && !Ls.isNul(goUrl))break;
                            }
                        }
						if(!Ls.isNul(cls.mainUrl)){
							cls.setMainUrl(cls,cls.mainUrl);
						}else{
							if(!Ls.isNul(goUrl)){
								cls.setMainUrl(cls,goUrl,goMenuID);
								// 设定激活样式
								$("#menu_"+goMenuID).addClass("menu-active");
							}
						}
				});
		},
		// 绑定菜单项的单击激活事件
		bindMenuItemActiveEvent:function(cls){
			$(".valid-node").on("click",function(){ 
				var me = $(this),
				url=me.attr('data-url'),
				indicatorId=me.attr('data-indicatorId');
				$(".valid-node").removeClass("menu-active");
				$(this).addClass("menu-active");
				if(!Ls.isNul(url))cls.setMainUrl(cls,Ls.string.decode(url),indicatorId);
			});
		},
		// 绑定一级菜单的展开收缩事件
		bindMenuLev1ExpCollEvent:function(){
			$(".leftNavCollspanExpendIco").on('click',function(){
              $('.subItems').hide();          
			  var me = $(this),iconCon = me.find('.collspanExpendIco');
			  var subItems = me.closest('li.pli').find('ul.subItems');
			  if(subItems.is(':hidden')){
				   subItems.show();
				   $('.collspanExpendIco').removeClass("icon_expend").addClass("icon_collspan");
				   iconCon.addClass("icon_expend");
				  
			  }else{
				  subItems.hide(); 
				  iconCon.removeClass("icon_collspan");
			  }
		   });	
		}		
	});
	
})();


