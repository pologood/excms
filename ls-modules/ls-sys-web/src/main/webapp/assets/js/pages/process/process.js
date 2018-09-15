/**
 * Created by zhusy on 2016-8-2.
 */
/**
 * 流程启动
 * @param columnId
 *        栏目
 * @param dataId
 *        数据ID
 * @param processBusinessType
 *        流程业务类型
 */
var CUR_PROCESS = {};
function startProcess(moduleCode,columnId,processBusinessType,dataId,processId,processName){
    Ls.ajax({
        type: "POST",
        data: {columnId: columnId,processId:processId,processName:processName},
        url: '/process/getProcessStartData',
        success: function (json) {
            var data = json.data;
            var processStartData = data.processStartData;
            var processId = processStartData.processId;//流程ID
            var processName = processStartData.processName;//流程名称
            var firstActivity = processStartData.firstActivity;
            var nextActivityData = processStartData.nextActivity;
            var nextActivity = nextActivityData.nextActivity;//下一个活动
            var receiver = nextActivityData.receiver;//下一个活动办理人
            if (!Ls.isEmpty(receiver) && !Ls.isEmpty(nextActivity)) {//存在默认办理人则直接提交流程,不需要弹出下一步页面
                var confirmInfo = "确定提交给" + receiver[0].personName + "进行" + nextActivity.name + "吗?";
                if (!confirm(confirmInfo)) {
                    return;
                }
                var processPostData = {};//流程相关提交参数
                processPostData.startFlow = 1;//流程启动标识
                processPostData.moduleCode = moduleCode;//流程引擎编码
                processPostData.processId = processId;//流程ID
                processPostData.processName = processName;//流程名称
                processPostData.activityId = firstActivity.elementId;//当前活动ID
                processPostData.activityName = firstActivity.name;//当前活动名称
                processPostData.elementId = nextActivity.elementId;//下一个活动ID
                processPostData.nextActivityName = nextActivity.name;//下一个活动的名称
                processPostData.participation = nextActivity.participation;//参与者类型
                processPostData.canPeriodicAgent = nextActivity.canPeriodicAgent;//是否允许发起人代理
                processPostData.transactType = 'single';//办理方式(单人办理)
                processPostData.receivers = JSON.stringify(receiver);//办理人
                processPostData.dataId = dataId;
                processPostData.processBusinessType = processBusinessType;
                var url = '/process/processStart';
                Ls.ajax({
                    type: "POST",
                    data: processPostData,
                    url: url,
                    success: function (json) {
                        Ls.tipsOk("提交成功");
                        try {
                            reloadData();
                        }catch (e){}
                    }
                });
            } else {//弹出下一步页面
                var formData = {};
                formData.moduleCode = moduleCode;
                formData.processId = processId;
                formData.activityId = firstActivity.elementId;
                formData.activityName = firstActivity.name;
                formData.processBusinessType = processBusinessType;
                formData.dataId = dataId;
                CUR_PROCESS.formData = formData;
                var url = "/process/processNextStep";
                url += "?elementId=" + firstActivity.elementId;
                url += "&processId=" + processId;
                url += "&startFlow=1";
                Ls.openWin(url, 600, 250, {title: '下一步骤'});
            }
        }
    });
}

/**
 * 新闻编辑页面提交审核
 * @param moduleCode
 * @param columnId
 * @param processBusinessType
 * @param dataId
 * @param processId
 * @param processName
 */
function startProcess1(moduleCode,columnId,processBusinessType,dataId,processId,processName){
    Ls.ajax({
        type: "POST",
        data: {columnId: columnId,processId:processId,processName:processName},
        url: '/process/getProcessStartData',
        success: function (json) {
            var data = json.data;
            var processStartData = data.processStartData;
            var processId = processStartData.processId;//流程ID
            var processName = processStartData.processName;//流程名称
            var firstActivity = processStartData.firstActivity;
            var nextActivityData = processStartData.nextActivity;
            var nextActivity = nextActivityData.nextActivity;//下一个活动
            var receiver = nextActivityData.receiver;//下一个活动办理人
            if (!Ls.isEmpty(receiver) && !Ls.isEmpty(nextActivity)) {//存在默认办理人则直接提交流程,不需要弹出下一步页面
                var confirmInfo = "确定提交给" + receiver[0].personName + "进行" + nextActivity.name + "吗?";
                if (!confirm(confirmInfo)) {
                    return;
                }
                var processPostData = {};//流程相关提交参数
                processPostData.startFlow = 1;//流程启动标识
                processPostData.moduleCode = moduleCode;//流程引擎编码
                processPostData.processId = processId;//流程ID
                processPostData.processName = processName;//流程名称
                processPostData.activityId = firstActivity.elementId;//当前活动ID
                processPostData.activityName = firstActivity.name;//当前活动名称
                processPostData.elementId = nextActivity.elementId;//下一个活动ID
                processPostData.nextActivityName = nextActivity.name;//下一个活动的名称
                processPostData.participation = nextActivity.participation;//参与者类型
                processPostData.canPeriodicAgent = nextActivity.canPeriodicAgent;//是否允许发起人代理
                processPostData.transactType = 'single';//办理方式(单人办理)
                processPostData.receivers = JSON.stringify(receiver);//办理人
                processPostData.dataId = dataId;
                processPostData.processBusinessType = processBusinessType;
                var url = '/process/processStart';
                Ls.ajax({
                    type: "POST",
                    data: processPostData,
                    url: url,
                    success: function (json) {
                        goBack();
                        Ls.tipsOk("报审成功");
                    }
                });
            } else {//弹出下一步页面
                var formData = {};
                formData.moduleCode = moduleCode;
                formData.processId = processId;
                formData.activityId = firstActivity.elementId;
                formData.activityName = firstActivity.name;
                formData.processBusinessType = processBusinessType;
                formData.dataId = dataId;
                CUR_PROCESS.formData = formData;
                var url = "/process/processNextStep";
                url += "?elementId=" + firstActivity.elementId;
                url += "&processId=" + processId;
                url += "&startFlow=1";
                Ls.openWin(url, 600, 250, {
                    title: '下一步骤',
                    close: function () {
                        goBack();
                    }
                });
                

            }
        }
    });
}

/**
 * 获取栏目关联的流程
 * @param columnId
 */
function getProcessConfig(contentModelCode) {
    var data = {};
    if(!Ls.isEmpty(contentModelCode)){
        Ls.ajax({
            type: "POST",
            data: {contentModelCode:contentModelCode},
            url: '/process/getProcessConfig',
            async:false,
            success: function (json) {
                data = json.data;
            }
        });
    }
    return data;
}