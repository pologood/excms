if (typeof jQuery === "undefined") {
    throw new Error("requires jQuery")
}
//IIFE
!(function($, _win) {
    var ME = this,
        _doc = _win.document,
        _url = _win.location.href,
        _tmp = {},
        _cache = {};

    doc = function(selector, context) {
        return new doc.fn.init(selector, context);
    };

    doc.fn = doc.prototype = {
        version: "0.0.1 bate",
        init: function(selector, context) {
            return $(selector, context);
        }
    };

    doc.fn.init.prototype = doc.fn;

    doc.extend = doc.fn.extend = $.extend;

    //按扭事件
    doc.extend({


        //办结
        complete : function(){
            if(validate){
                if(!validate()) return;
            }
            var formData = getData();
            var getNextStepTypePostData = formData;
            getNextStepTypePostData.taskId = CUR_PROCESS.params.taskId;
            Ls.ajax({
                type: "POST",
                data: getNextStepTypePostData,
                url: '/process/getNextStepType',
                success: function (json) {
                    var data = json.data;
                    if(data == 0){//下一步
                        doc.step_next();
                    }else{
                        var confirmTitle = '请您确认是否要办结？';
                        if(confirm(confirmTitle)){
                            formData.fileEditData = JSON.stringify(CUR_PROCESS.fileEditVOs);
                            formData.activityId = CUR_PROCESS.params.activityId;
                            formData.taskId = CUR_PROCESS.params.taskId;
                            var url = "/process/processComplete";
                            $(".tbtn[data-code='complete']",$("#btn_tools")).prop("disabled",true);
                            Ls.ajax({
                                type: "POST",
                                data: formData,
                                url: url,
                                success: function (json) {
                                    Ls.tipsOk("办结成功",function () {
                                        try{
                                            window.opener.cur.grid.reload();
                                        }catch(e){}
                                        window.close();
                                    })
                                }
                            });
                        }
                    }
                }
            });
        },
        //办理办毕
        finish : function(){
            if(validate){
                if(!validate()) return;
            }
            //验证任务是否已经办理完毕
            var isComplete = false;
            var formData = getData();
            var getNextStepTypePostData = formData;
            getNextStepTypePostData.taskId = CUR_PROCESS.params.taskId;
            Ls.ajax({
                type: "POST",
                data: getNextStepTypePostData,
                url: '/process/getNextStepType',
                success: function (json) {
                    var data = json.data;
                    if(data == 0){//下一步
                        doc.step_next();
                    }else if(data == 100){//办结
                        doc.complete();
                    }else{
                        var data = getData();
                        data.activityId = CUR_PROCESS.params.activityId;
                        data.taskId = CUR_PROCESS.params.taskId;
                        var url = "/process/taskFinish";
                        $(".tbtn[data-code='finish']",$("#btn_tools")).prop("disabled",true);
                        Ls.ajax({
                            type: "POST",
                            data: data,
                            url: url,
                            success: function (json) {
                                Ls.tipsOk("办理办毕",function () {
                                    try{
                                        window.opener.cur.grid.reload();
                                    }catch(e){}
                                    try {
                                        if (CUR_PROCESS.dg.opener.cur.fn.reload){
                                            CUR_PROCESS.dg.opener.cur.fn.reload();
                                        }
                                    } catch (e) {}
                                    window.close();
                                })
                            }
                        });
                    }
                }
            });
        },
        //下一步
        step_next : function(){
            if(validate){
                if(!validate()) return;
            }
            var data = getData();
            CUR_PROCESS.formData = data;
            var autoToOwnerTask = CUR_PROCESS.params.autoToOwner;//当前任务是否是自动流转给发起人的任务
            var canAutoToOwner = 0;
            if(1 != autoToOwnerTask && 1 != CUR_PROCESS.params.startFlow){//非启动流程且自动流转给发起人任务需要判断当前所在活动是否设置自动提交给发起人
                Ls.ajax({
                    type: "POST",
                    data: {activityId:CUR_PROCESS.params.activityId},
                    url: '/process/getActivity',
                    success: function (json) {
                        var activity = json.data;
                        canAutoToOwner = activity.canAutoToOwner;
                    }
                });
            }
            if( 1 == canAutoToOwner){//自动提交给发起人
                var url = "/process/processAutoToOwner";
                var processPostData = {};//流程相关提交参数
                processPostData.activityId = CUR_PROCESS.params.activityId;
                processPostData.taskId = CUR_PROCESS.params.taskId;
                var postData = $.extend(true,CUR_PROCESS.formData,processPostData);
                Ls.ajax({
                    type: "POST",
                    data: postData,
                    url: url,
                    success: function (json) {
                        Ls.tip.success("办理成功", 1, function() {
                            window.close();
                        });
                    }
                });
                return;
            }
            var getNextStepTypePostData = CUR_PROCESS.formData;
            getNextStepTypePostData.taskId = CUR_PROCESS.params.taskId;
            //Ls.ajax.post('/process/getNextStepType',getNextStepTypePostData,function(json){
            var data = 0;
            if(data == 100){//办结
                doc.complete();
            }else{
                var getDefaultHandlerPostData = CUR_PROCESS.formData;
                getDefaultHandlerPostData.activityId = CUR_PROCESS.params.activityId;
                getDefaultHandlerPostData.taskId = CUR_PROCESS.params.taskId;
                //判断是否存在默认办理人
                Ls.ajax({
                    type: "POST",
                    data: getDefaultHandlerPostData,
                    url: '/process/getDefaultHandler4NextActivity',
                    success: function (json) {
                        var result = json.data;
                        var receiver = result.receiver;//办理人
                        var nextActivity = result.nextActivity;//下一个活动
                        if(!Ls.isEmpty(receiver) && !Ls.isEmpty(nextActivity)){//存在默认办理人
                            var confirmInfo = "确定提交给"+receiver[0].personName+"进行"+nextActivity.name+"吗?";
                            if(!confirm(confirmInfo)){return;}
                            var processPostData = {};//流程相关提交参数
                            processPostData.elementId = nextActivity.elementId;//下一个活动ID
                            processPostData.nextActivityName = nextActivity.name;//下一个活动的名称
                            processPostData.participation = nextActivity.participation;//参与者类型
                            processPostData.canPeriodicAgent = nextActivity.canPeriodicAgent;//是否允许发起人代理
                            processPostData.activityId = CUR_PROCESS.params.elementId;//当前活动ID
                            processPostData.transactType = 'single';//办理方式(单人办理)
                            processPostData.receivers = JSON.stringify(receiver);//办理人
                            var url = '';
                            if(1 == CUR_PROCESS.params.startFlow){
                                url = '/process/processStart';
                                processPostData.processId = CUR_PROCESS.params.processId;
                            } else {
                                url = '/process/processRun';
                                processPostData.taskId = CUR_PROCESS.params.taskId;
                            }
                            var postData = $.extend(true,CUR_PROCESS.formData,processPostData);
                            Ls.ajax({
                                type: "POST",
                                data: postData,
                                url: url,
                                success: function (json) {
                                    Ls.tip.success("办理成功",1,function(){
                                        window.close();
                                    });
                                }
                            });

                        }else{//打开下一步页面
                            var url = "/process/processNextStep";
                            url += "?elementId="+CUR_PROCESS.params.activityId;
                            if(!Ls.isEmpty(CUR_PROCESS.params.startFlow) && CUR_PROCESS.params.startFlow==1){//流程启动
                                url += "&processId="+CUR_PROCESS.params.processId;
                                url += "&startFlow=1";
                            }else{
                                url += "&taskId="+CUR_PROCESS.params.taskId;
                            }
                            Ls.openWin(url,600,250,{title:'下一步骤'});
                        }
                    }
                });

            }
            //},null,null,{async:false});
            return this;
        },


        //查看流程图
        view_flowchart : function(){
            var url = "/process/diagramView?processId="+CUR_PROCESS.params.processId+"&recordId="+CUR_PROCESS.params.recordId+"&activityId="+CUR_PROCESS.params.activityId
            Ls.openWin(url, {
                winType: 1,
                title: '查看流程图',
                width: '600px',
                height: '600px'
            });
        },
        //查看办理日志
        view_processHistLog : function(){
            var url = '/process/processHistLog?procInstId='+CUR_PROCESS.params.procInstId+"&recordId="+CUR_PROCESS.params.recordId;
            Ls.openWin(url, {
                winType: 1,
                title: '办理日志',
                width: '1000px',
                height: '400px'
            });
        },
        //退回
        fall_back : function(){
            var url = '/process/getFallbackOptions';
            Ls.ajax({
                type: "POST",
                data: {procInstId:CUR_PROCESS.params.procInstId,taskId:CUR_PROCESS.params.taskId},
                url: url,
                success: function (json) {
                    var data = json.data;
                    var fallbackSet  =  data.fallbackSet;
                    if(Ls.isEmpty(fallbackSet) || 'null' == fallbackSet){
                        Ls.tipsErr('当前活动未设置回退类型');
                        return;
                    }
                    if('notAllow' == fallbackSet){
                        Ls.tipsErr('当前活动不允许退回');
                        return;
                    }else if('beforeStep' == fallbackSet){ //向前回退一步
                        CUR_PROCESS.fallbackOptions = data;
                        url = "/process/processFallBack?taskId="+CUR_PROCESS.params.taskId+"&recordId="+CUR_PROCESS.params.recordId+"&moduleCode="+CUR_PROCESS.params.moduleCode;
                        Ls.openWin(url, {
                            winType: 1,
                            title: '退回',
                            width: '440px',
                            height: '200px',
                        });
                    }else if('allHistoryStep' == fallbackSet){ //向前回退任何一步
                        CUR_PROCESS.fallbackOptions = data;
                        url = "/process/processFallBack?taskId="+CUR_PROCESS.params.taskId+"&recordId="+CUR_PROCESS.params.recordId+"&moduleCode="+CUR_PROCESS.params.moduleCode;
                        Ls.openWin(url, {
                            winType: 1,
                            title: '退回',
                            width: '440px',
                            height: '350px',
                        });
                    }
                }
            });

        },

        //打印
        print : function(){

        },
        //办理(检出)
        claim : function(){
            if(confirm("您是否确定办理该工作？")){
                Ls.ajax.post('/process/claimTask',{taskId:CUR_PROCESS.params.taskId},function(){
                    Ls.tip.success("办理成功",1,function(){
                        try{
                            window.opener.CUR_PROCESS.grid.reload();
                        }catch(e){}
                        window.location.reload();
                    })
                });
            }
        },
        //查看催办日志
        view_press_log : function(){
            var url = '/processPress/processPressList'.setUrlParam("procInstId",CUR_PROCESS.params.procInstId);
            Ls.openWin(url, {
                winType: 1,
                title: '催办日志',
                width: '1000px',
                height: '400px',
                max: false
            });
        },
        //催办
        press :  function(){
            //获取当前公文流程运行的任务
            var url = '/process/getRuExecutionTaskList';
            Ls.ajax.post(url,{procInstId:CUR_PROCESS.params.procInstId},function(json){
                var data = json.data;
                if(null == data || 'null' == data || Ls.isEmpty(data)){
                    Ls.tip.info('当前公文无可催办对象');
                    return;
                }
                if(data.length == 1){//如果当前只有一个任务,则直接弹出催办页面
                    var task = data[0];
                    var taskId = task.taskId;//任务ID
                    var pressedName; //办理人
                    if(Ls.isEmpty(task.assigneeId)){
                        pressedName = task.swimlaneName;
                    }else{
                        pressedName = task.assigneeName;
                    }
                    url = '/process/taskPress';
                    url = url.setUrlParam("taskId",taskId)
                        .setUrlParam("moduleCode",CUR_PROCESS.params.moduleCode)
                        .setUrlParam("pressedName",encodeURI(pressedName))
                        .setUrlParam("docTitle",encodeURI(CUR_PROCESS.params.docTitle));
                    return Ls.dialog.create({
                        self:false,
                        title:'公文催办',
                        width:500,
                        height:400,
                        content:'url:'+url,
                        max:false,
                        lock:true,
                        resize:false
                    });
                }else if(data.length > 1){//如果有多个任务,弹出选择移交任务列表
                    CUR_PROCESS.pressTaskList = data;
                    url = '/process/taskPressList';
                    url = url.setUrlParam("moduleCode",CUR_PROCESS.params.moduleCode)
                        .setUrlParam("docTitle",encodeURI(CUR_PROCESS.params.docTitle));
                    return Ls.dialog.create({
                        self:false,
                        title:'公文催办',
                        width:400,
                        height:320,
                        content:'url:'+url,
                        max:false,
                        lock:true,
                        resize:false
                    });
                }
            });
        },


        //保存
        save_form : function(){
            if(!CUR_PROCESS.fn.validate()) return;
            //保存手写签批
            Ls.iWebSign && Ls.iWebSign.CUR_PROCESS && Ls.iWebSign.CUR_PROCESS.CUR_PROCESSObject && (Ls.iWebSign.CUR_PROCESS.CUR_PROCESSObject.save());

            var data = Ls.getFromJSON($("#form1"));
            data.fileEditData = JSON.stringify(CUR_PROCESS.fileEditVOs);
            data.taskId = CUR_PROCESS.params.taskId;
            var url = "/process/saveFormData";
            $(".tbtn[data-code='save_form']",$("#btn_tools")).prop("disabled",true);
            Ls.ajax.post(url, data, function (json) {
                try{
                    window.opener.CUR_PROCESS.grid.reload();
                }catch(e){}
                try {
                    if (window.opener.CUR_PROCESS.fn.close){
                        window.opener.CUR_PROCESS.fn.close();
                    }
                } catch (e) {}
                Ls.tip.success("保存成功",1,function(){
                    Ls.closeWin();
                });
            });
        }
    }),






        //
        doc.extend({
                //按扭渲染
                render_button : function(element,data){
                    Ls.log.add("render_button>>>")
                    var arr = [];
                    for (var i = 0; i < data.length; i++) {
                        var el = data[i],icon = '';
                        //if(el.icon && el.icon!='default'){
//                    icon = '<i class="fonticon">'+el.icon+'</i>';
//                }
                        arr.push('<button class="btn22_gray ml15 h25 tbtn" type="' + el.type + '" data-code="' + el.code + '">' + icon + '' + el.name + '</button>');
                    }
                    $(element).html(arr.join(""));
                },

                //发布
                publish : function(){
                    Ls.ajaxGet({
                        data: {
                            id: CUR_PROCESS.params.dataId
                        },
                        url: "/articleNews/changePublish",
                        success: function (text) {
                            var isPublish = text.data;
                            if (isPublish == 1) {
                                Ls.tipsOk("取消发布成功");
                            } else {
                                Ls.tipsOk("发布成功");
                            }
                        }
                    });
                }
            }
        )

    //对外提供接口
    _win.doc = doc;

    //绑定对象到document
    $(document).ready(function () {
        $(document).on('click', '.tbtn', function () {
            var _this = $(this),code = _this.attr("data-code");
            if($.type(doc[code])=="function"){
                doc[code]();
            }else{
                Ls.tipsErr('['+_this.text()+'] 未绑定事件！',3);
            }
        })
    })

})(jQuery, window);