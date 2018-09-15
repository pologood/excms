var cur = {
    editorOther: ""
};

function kindEditorOther(editor,siteId,columnId) {
    cur.editorOther = editor;
    cur.editorOther.clickToolbar('mtxx', function() {
        Ls.openWin('/articleNews/mtxxUpload?siteId='+siteId+'&columnId='+columnId, '885px', '500px', {
            lock: true,
            title: '美图秀秀'
        });
    });
    cur.editorOther.clickToolbar('flvUpload', function() {
        Ls.openWin('/articleNews/videoUpload?siteId='+siteId+'&columnId='+columnId, '500px', '340px', {
            lock: true,
            title: '视频上传'
        });
    });
}

//插入编辑器
function insertHtml(path){
    if(cur.editorOther != ''){
        cur.editorOther.insertHtml(path)
    }
}