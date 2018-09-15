//敏感词检测
function senWordsCheck() {
    //var content = editor.html();
    var content = eweb.getHtml();
    Ls.ajax({
        data: {
            content: content,
            flag: "SENSITIVE"
        },
        url: "/content/errorWordsCheck",
        success: function (text) {
            if (text.status == 1) {
                Ls.tipsOk("敏感词检测成功");
            } else {
                Ls.tipsErr(text.desc);
            }
        }
    });
}

function getTypes() {
    var types = [];
    if (cur.contentModel.isEasyWord == 1) {
        types.push(1);
    }
    if (cur.contentModel.isSensitiveWord == 1) {
        types.push(2);
    }
    if (cur.contentModel.isHotWord) {
        types.push(3);
    }
    return types;
}

function contentCheck(flag) {
    Ls.openWin("/content/contentCheck?flag=" + flag, '600px', '460px', {
        id: 'contentCheck',
        title: '内容检测'
    });
}

function checkAll() {
    Ls.openWin("/content/checkPage", '600px', '460px', {
        id: 'checkAll',
        title: '内容检测'
    });
}

// 易错词检测
function errorWordsCheck() {
    //var content = editor.html();
    var content = eweb.getHtml();
    Ls.ajax({
        data: {
            content: content,
            flag: "EASYERR"
        },
        url: "/content/errorWordsCheck",
        success: function (text) {
            if (text.status == 1) {
                Ls.tipsOk("易错词检测成功");
            } else {
                Ls.tipsErr(text.desc);
            }
        }
    });
}

// 热词检测
function hotWordsCheck() {
    //var content = editor.html();
    var content = eweb.getHtml();
    Ls.ajax({
        data: {
            content: content,
            flag: "HOT"
        },
        url: "/content/errorWordsCheck",
        success: function (text) {
            if (text.status == 1) {
                Ls.tipsOk("热词检测成功");
            } else {
                Ls.tipsErr(text.desc);
            }
        }
    });
}