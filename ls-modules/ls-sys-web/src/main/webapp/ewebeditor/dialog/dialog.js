/*
*######################################
* eWebEditor V11.5 - Advanced online web based WYSIWYG HTML editor.
* Copyright (c) 2003-2018 eWebSoft.com
*
* For further information go to http://www.ewebeditor.net/
* This copyright notice MUST stay intact for use.
*######################################
*/


var URLParams = new Object();
var aParams = document.location.search.substr(1).split('&');
for (i = 0; i < aParams.length; i++) {
    var aParam = aParams[i].split('=');
    URLParams[aParam[0]] = aParam[1];
}


var EWIN = parent.EWIN;
var EWEB = EWIN.EWEB;
var EWEBDialog = EWIN.EWEBDialog;
var EWEBTools = EWIN.EWEBTools;
var EWEBBrowser = EWIN.EWEBBrowser;
var EWEBParam = EWIN.EWEBParam;
var lang = EWIN.lang;
var config = EWIN.config;
var EWEBSelection = EWIN.EWEBSelection;
var EWEBActiveX = EWIN.EWEBActiveX;

var ParentDialog = parent.ParentDialog();
if (ParentDialog) {
    ParentDialog = ParentDialog.contentWindow.InnerDialog;
}


EWEBTools.RegisterDollarFunction(window);


function GetParam(s_Name, s_Value) {
    return (URLParams[s_Name]) ? URLParams[s_Name] : s_Value;
}

function LoadScript(url) {
    document.write('<scr' + 'ipt type="text/javascript" src="' + url + '"><\/scr' + 'ipt>');
}

function BaseTrim(str) {
    lIdx = 0;
    rIdx = str.length;
    if (BaseTrim.arguments.length == 2) {
        act = BaseTrim.arguments[1].toLowerCase();
    } else {
        act = "all";
    }

    for (var i = 0; i < str.length; i++) {
        thelStr = str.substring(lIdx, lIdx + 1);
        therStr = str.substring(rIdx, rIdx - 1);
        if ((act == "all" || act == "left") && thelStr == " ") {
            lIdx++;
        }
        if ((act == "all" || act == "right") && therStr == " ") {
            rIdx--;
        }
    }
    str = str.slice(lIdx, rIdx);
    return str;
}

function BaseAlert(theText, notice) {
    alert(notice);
    theText.focus();
    theText.select();
    return false;
}

function IsColor(color) {
    var temp = color;
    if (temp == "") return true;
    if (temp.length != 7) return false;
    return (temp.search(/\#[a-fA-F0-9]{6}/) != -1);
}

function IsEmptyOrInt(str) {
    return (str.search(/[^0-9]+/) == -1);
}

function IsEmptyOr1Plus(str) {
    return (IsEmptyOrInt(str) && parseInt(str) > 0);
}


function OnDigitFieldKeyDown(e) {
    if (!e) {
        e = window.event;
    }
    if ((e.keyCode >= 48 && e.keyCode <= 57) || (e.keyCode >= 96 && e.keyCode <= 105) || (e.keyCode >= 35 && e.keyCode <= 40) || e.keyCode == 46 || e.keyCode == 8) {
        return true;
    }
    return EWEBTools.CancelEvent(e);
}

function SelectColor(s_FieldFlag) {
    var s_Url = "selcolor.htm?returnfieldflag=" + s_FieldFlag;
    EWEBDialog.OpenDialog(s_Url);
}

function SelectImage() {
    EWEBDialog.OpenDialog("backimage.htm?action=other");
}

function SelectBrowse(type, s_FieldFlag) {
    EWEBDialog.OpenDialog('browse.htm?returnfieldflag=' + s_FieldFlag + '&type=' + type);
}

function SearchSelectValue(o_Select, s_Value) {
    for (var i = 0; i < o_Select.length; i++) {
        if (o_Select.options[i].value == s_Value) {
            o_Select.selectedIndex = i;
            return true;
        }
    }
    return false;
}

function ToInt(str) {
    str = BaseTrim(str);
    if (str != "") {
        var sTemp = parseFloat(str);
        if (isNaN(sTemp)) {
            str = "";
        } else {
            str = sTemp;
        }
    }
    return str;
}

function IsURL(url) {
    var sTemp;
    var b = true;
    sTemp = url.substring(0, 7);
    sTemp = sTemp.toUpperCase();
    if ((sTemp != "HTTP://") || (url.length < 10)) {
        b = false;
    }
    return b;
}

function IsExt(url, opt) {
    var sTemp;
    var b = false;
    var s = opt.toUpperCase().split("|");
    for (var i = 0; i < s.length; i++) {
        sTemp = url.substr(url.length - s[i].length - 1);
        sTemp = sTemp.toUpperCase();
        s[i] = "." + s[i];
        if (s[i] == sTemp) {
            b = true;
            break;
        }
    }
    return b;
}

function CheckSizeAsync(oFile, sSizeKB, _Callback) {
    var nSize = parseInt(sSizeKB) * 1024;
    if (oFile.files) {
        if (oFile.files[0].size > nSize) {
            _Callback("no");
        } else {
            _Callback("yes");
        }
        return;
    }

    var s_File = oFile.value;
    if (!s_File) {
        _Callback("unknow");
        return;
    }
    s_File = s_File.replace(/\\/gi, "/");
    if (s_File.indexOf("/") <= 0) {
        _Callback("unknow");
        return;
    }

    EWEBActiveX.AsynCallBack("isinstalled", [false],
        function (o_Data) {
            if (!o_Data["Ret"]) {
                _Callback("unknow");
                return;
            }

            EWEBActiveX.AsynCallBack("getinfo", ["filesize", s_File, ""],
                function (o_Data) {
                    if (!o_Data["Ret"]) {
                        _Callback("unknow");
                        return;
                    }

                    if (o_Data["Ret"] == "-1") {
                        _Callback("unknow");
                        return;
                    } else {
                        if (parseInt(o_Data["Ret"]) > nSize) {
                            _Callback("no");
                            return;
                        } else {
                            _Callback("yes");
                            return;
                        }
                    }
                }
            );
        }
    );
}

function RelPath2FSPath(s_Url) {
    if (config.FSPath == "") {
        return s_Url;
    }
    if (s_Url.indexOf("://") >= 0) {
        return s_Url;
    }

    var s_FSPath = config.FSPath;
    if (s_Url.substring(0, 1) == "/") {
        var s_SitePath = s_FSPath.replace(/^([^\/]+?:\/\/[^\/]+?)\/.*?$/gi, "$1");
        return s_SitePath + s_Url;
    } else {
        var s_RootPath = s_FSPath;
        var n = s_RootPath.length;
        if (s_RootPath.substr(n - 1, 1) == "/") {
            s_RootPath = s_RootPath.substr(0, n - 1);
        }
        while (s_Url.substr(0, 3) == "../") {
            s_Url = s_Url.substr(3);
            s_RootPath = s_RootPath.substring(0, s_RootPath.lastIndexOf("/"));
        }
        return s_RootPath + "/" + s_Url;
    }
}

function relativePath2rootPath(s_Url) {
    if (s_Url.substring(0, 1) == "/") {
        return s_Url;
    }
    if (s_Url.indexOf("://") >= 0) {
        return s_Url;
    }

    var s_RootPath = EWEB.RootPath;
    while (s_Url.substr(0, 3) == "../") {
        s_Url = s_Url.substr(3);
        s_RootPath = s_RootPath.substring(0, s_RootPath.lastIndexOf("/"));
    }
    return s_RootPath + "/" + s_Url;
}

function Rel2RootByBase(s_Url) {
    if (config.BaseHref == "") {
        return s_Url;
    }
    if (s_Url.substring(0, 1) == "/") {
        return s_Url;
    }
    if (s_Url.indexOf("://") >= 0) {
        return s_Url;
    }

    var s_Base = config.BaseHref;
    s_Base = s_Base.substring(0, s_Base.length - 1);
    while (s_Url.substring(0, 3) == "../") {
        s_Url = s_Url.substr(3);
        s_Base = s_Base.substring(0, s_Base.lastIndexOf("/"));
    }
    return s_Base + "/" + s_Url
}

function Root2PlugRel(s_Url) {
    var s_PlugPath = EWEB.RootPath + "/plugin/";
    while (true) {
        var n1 = s_Url.indexOf("/");
        var n2 = s_PlugPath.indexOf("/");
        if (n1 >= 0 && n1 == n2) {
            if (s_Url.substring(0, n1 + 1) == s_PlugPath.substring(0, n2 + 1)) {
                s_Url = s_Url.substr(n1 + 1);
                s_PlugPath = s_PlugPath.substr(n2 + 1);
            } else {
                break;
            }
        } else {
            break;
        }
    }

    var s = s_PlugPath.replace(/[^\/]+/gi, '');
    var n = s.length;
    for (var i = 0; i < n; i++) {
        s_Url = "../" + s_Url;
    }
    return s_Url;
}

function relativePath2setPath(url, b_IsResMovePath) {
    if (b_IsResMovePath) {
        if (config.ResMovePath) {
            url = config.ResMovePath + url;
            return url;
        }
    }

    switch (config.BaseUrl) {
        case "0":
            url = relativePath2rootPath(url);
            return EraseBaseHref(url);
            break;
        case "1":
            return relativePath2rootPath(url);
            break;
        case "2":
        case "3":
            return EWEB.SitePath + relativePath2rootPath(url);
            break;
    }
}

function EraseBaseHref(url) {
    var baseHref = config.BaseHref;

    var b = true;
    while (b) {
        var n1 = url.indexOf("/");
        var n2 = baseHref.indexOf("/");
        if ((n1 >= 0) && (n2 >= 0)) {
            var u1 = url.substring(0, n1 + 1);
            var u2 = baseHref.substring(0, n2 + 1);
            if (u1 == u2) {
                url = url.substr(n1 + 1);
                baseHref = baseHref.substr(n2 + 1);
            } else {
                b = false;
            }
        } else {
            b = false;
        }
    }

    if (baseHref != "") {
        var a = baseHref.split("/");
        for (var i = 1; i < a.length; i++) {
            url = "../" + url;
        }
    }

    return url;
}

function imgButtonOver(el) {
    if (!el["imageinitliazed"]) {
        el["oncontextmenu"] = new Function("event.returnValue=false");
        el["onmouseout"] = new Function("imgButtonOut(this)");
        el["onmousedown"] = new Function("imgButtonDown(this)");
        el["unselectable"] = "on";
        el["imageinitliazed"] = true;
    }
    el.className = "imgButtonOver";
}

function imgButtonOut(el) {
    el.className = "imgButtonOut";
}

function imgButtonDown(el) {
    el.className = "imgButtonDown";
}

function getUploadForm(s_Type) {
    var s_MaxSize;
    switch (s_Type) {
        case "image":
            s_MaxSize = config.AllowImageSize;
            break;
        case "flash":
            s_MaxSize = config.AllowFlashSize;
            break;
        case "media":
            s_MaxSize = config.AllowMediaSize;
            break;
        case "file":
            s_MaxSize = config.AllowFileSize;
            break;
        default:
            return "";
    }

    var n_MaxSize = parseFloat(s_MaxSize) * 1024;
    var html = "<iframe name='myuploadformtarget' style='display:none;position:absolute;width:0px;height:0px' src='blank.htm'></iframe>"
        + "<form action=\"" + EWEB.UploadAct + "&action=save&type=" + s_Type + "\" method=post name=myuploadform enctype='multipart/form-data' style='margin:0px;padding:0px;width:100%;border:0px;' target='myuploadformtarget'>"
        + "<input type=hidden name='MAX_FILE_SIZE' value='" + n_MaxSize + "'>"
        + "<input type=file name='uploadfile' id='uploadfile' size=28 style='width:100%'  onchange=\"this.form.originalfile.value=this.value;try{doPreview();} catch(e){}\">"
        + "<input type=hidden name='originalfile' value=''>"
        + "</form>";

    return html;
}

function getUploadErrDesc(s_Flag, s_Ext, s_Size) {
    var s_ErrDesc = "";
    switch (s_Flag) {
        case "ext":
            s_ErrDesc = lang["ErrUploadInvalidExt"] + ":" + s_Ext;
            break;
        case "size":
            s_ErrDesc = lang["ErrUploadSizeLimit"] + ":" + s_Size + "KB";
            break;
        case "file":
            s_ErrDesc = lang["ErrUploadInvalidFile"];
            break;
        case "style":
            s_ErrDesc = lang["ErrInvalidStyle"];
            break;
        case "space":
            s_ErrDesc = lang["ErrUploadSpaceLimit"] + ":" + config.SpaceSize + "MB";
            break;
        default:
            s_ErrDesc = s_Flag;
            break;
    }

    return s_ErrDesc;
}

function readCookie(s_Name) {
    var s_CookieValue = "";
    var s_Search = s_Name + "=";
    if (document.cookie.length > 0) {
        n_Offset = document.cookie.indexOf(s_Search);
        if (n_Offset != -1) {
            n_Offset += s_Search.length;
            var n_End = document.cookie.indexOf(";", n_Offset);
            if (n_End == -1) {
                n_End = document.cookie.length;
            }
            s_CookieValue = unescape(document.cookie.substring(n_Offset, n_End));
        }
    }
    return s_CookieValue;
}

function writeCookie(s_Name, s_Value) {
    var s_Expire = "";
    s_Expire = new Date((new Date()).getTime() + 24 * 365 * 3600000);
    s_Expire = ";expires=" + s_Expire.toGMTString();
    document.cookie = s_Name + "=" + escape(s_Value) + s_Expire;
}

function SetAttribute(o_Element, s_AttrName, s_AttrValue) {
    if (s_AttrValue == null || s_AttrValue.length == 0) {
        o_Element.removeAttribute(s_AttrName, 0);
    } else {
        o_Element.setAttribute(s_AttrName, s_AttrValue, 0);	// 0 : Case Insensitive
    }
}

function SetProtectAttribute(o_Element, s_AttName, s_AttValue) {
    SetAttribute(o_Element, "_ewebeditor_pa_" + s_AttName, encodeURIComponent(s_AttValue));
}

function GetAttribute(o_Element, s_AttrName) {
    var s_Att = o_Element.attributes[s_AttrName];
    if (s_Att == null || !s_Att.specified) {
        return "";
    }
    var s_Value = o_Element.getAttribute(s_AttrName, 2);
    if (s_Value == null) {
        s_Value = s_Att.nodeValue;
    }
    return (s_Value == null ? "" : s_Value);
}

function GetStyleAttribute(el, s_AttrName) {
    s_AttrName = s_AttrName.replace(/\-(\w)/g, function (s_Match, p1) {
        return p1.toUpperCase();
    });

    var s_Value = el.style[s_AttrName];
    if (s_Value && (!EWEBBrowser.IsIE) && s_AttrName.indexOf("Color") >= 0) {
        s_Value = RGBtoHEX(s_Value);
    }
    if (!s_Value) {
        switch (s_AttrName) {
            case "backgroundColor":
                s_AttrName = "bgColor";
                break;
            case "textAlign":
                s_AttrName = "align";
                break;
            case "verticalAlign":
                s_AttrName = "valign";
                break;
            default:
        }
        s_Value = GetAttribute(el, s_AttrName);
    }

    return s_Value;
}

function RemovePX(s_Value) {
    var n = parseInt(s_Value);
    if (isNaN(n)) {
        return '';
    }
    var l = s_Value.length;
    if (l > 2) {
        if (s_Value.toLowerCase().substr(l - 2) == "px") {
            s_Value = n + "";
        }
    }
    return s_Value;
}

function GetProtectAttribute(el, s_AttName) {
    var s_ProtectAttName = "_ewebeditor_pa_" + s_AttName;
    var o_Att = el.attributes[s_ProtectAttName];

    if (o_Att == null || !o_Att.specified) {
        return GetAttribute(el, s_AttName);
    } else {
        return decodeURIComponent(el.getAttribute(s_ProtectAttName, 2));
    }
}

function GetFakeTag(el) {
    return GetAttribute(el, "_ewebeditor_fake_tag");
}

function GetFakeValue(el) {
    return decodeURIComponent(GetAttribute(el, "_ewebeditor_fake_value"));
}

function SetFakeValue(el, s_Html) {
    SetAttribute(el, '_ewebeditor_fake_value', encodeURIComponent(s_Html));
}

function toHex(N) {
    if (N == null) return "00";
    N = parseInt(N);
    if (N == 0 || isNaN(N)) return "00";
    N = Math.max(0, N);
    N = Math.min(N, 255);
    N = Math.round(N);
    return "0123456789ABCDEF".charAt((N - N % 16) / 16) + "0123456789ABCDEF".charAt(N % 16);
}

function RGBtoHEX(str) {
    if (str.substring(0, 3) == 'rgb') {
        var arr = str.split(",");
        var r = arr[0].replace('rgb(', '').trim()
        var g = arr[1].trim()
        var b = arr[2].replace(')', '').trim();
        var hex = [
            toHex(r),
            toHex(g),
            toHex(b)
        ];
        return "#" + hex.join('');
    } else {
        return str;
    }
}

var HtmlParamParser = {

    Init: function (s_Html) {
        this.Params = new Object();
        this.Html = s_Html;
        var re = new RegExp("<(video|audio)(?=[\\s>])", "gi");
        if (re.test(s_Html)) {
            s_Html = s_Html.replace(/<object[\s\S]*?<\/object>/gi, "");
        }

        re = new RegExp("<object(?=[\\s>])", "gi");
        if (re.test(s_Html)) {
            this.Tag = "object";
            re = /<param\s+name\s*?=\s*?([\'\"]?)(\w+)\1[\s]+value\s*?=\s*?(\w+|\'[^\'>]+\'|\"[^\">]+\")[^>]*?>/gi;
            while ((arr = re.exec(s_Html)) != null) {
                var s_V = RegExp.$3;
                if (s_V.substring(0, 1) == '\'' || s_V.substring(0, 1) == '"') {
                    s_V = s_V.substring(1, s_V.length - 1);
                }
                this.Params[RegExp.$2.toLowerCase()] = s_V;
            }
        } else {
            this.Tag = "common";
            re = /\s(\w+)\s*?=\s*?(\w+|\'[^\'>]+\'|\"[^\">]+\")/gi;
            while ((arr = re.exec(s_Html)) != null) {
                var s_V = RegExp.$2;
                if (s_V.substring(0, 1) == '\'' || s_V.substring(0, 1) == '"') {
                    s_V = s_V.substring(1, s_V.length - 1);
                }
                this.Params[RegExp.$1.toLowerCase()] = s_V;
            }
        }
    },

    GetValue: function (s_Key) {
        return (this.Params[s_Key]) ? this.Params[s_Key] : "";
    },


    GetHtml: function () {
        return this.Html;
    },

    SetValue: function (s_Key, s_Value) {
        var s_Html = this.Html;

        function _Replace1(m, m1) {
            if (s_Value == '') {
                return '';
            } else {
                return '<param name="' + s_Key + '" value="' + s_Value + '">';
            }
        }

        function _Replace2(m) {
            return m + '<param name="' + s_Key + '" value="' + s_Value + '">';
        }

        if (this.Tag == 'object') {
            var re = new RegExp('<param(?=\\s)[^>]*?\\sname\\s*?=\\s*?(\w+|\'[^\'>]+\'|\"[^\">]+\")[^>]*?>', 'gi');
            if (re.test(s_Html)) {
                s_Html = s_Html.replace(re, _Replace1);
            } else {
                if (s_Value != '') {
                    s_Html = s_Html.replace(/<object[^>]*?>/, _Replace2);
                }
            }

            s_Html = this._GetSetValueByTag(s_Html, 'embed', s_Key, s_Value);
        } else {
            s_Html = this._GetSetValueByTag(s_Html, '\\w+', s_Key, s_Value);
        }


        this.Html = s_Html;
    },

    _GetSetValueByTag: function (s_Html, s_Tag, s_Key, s_Value) {

        function _Replace1(m, m1, m2, m3) {
            if (s_Value == '') {
                return m1 + m3;
            } else {
                return m1 + ' ' + s_Key + '="' + s_Value + '"' + m3;
            }
        }

        function _Replace2(m) {
            return m.substring(0, m.length - 1) + ' ' + s_Key + '="' + s_Value + '">';
        }

        var re = new RegExp('(<' + s_Tag + '(?=[\\s>])[^>]*?)\\s' + s_Key + '\\s*?=\\s*?(\w+|\'[^\'>]+\'|\"[^\">]+\")([^>]*>)', 'gi');
        if (re.test(s_Html)) {
            s_Html = s_Html.replace(re, _Replace1);
        } else {
            if (s_Value != '') {
                re = new RegExp('<' + s_Tag + '(?=[\\s>])[^>]*>', 'gi');
                s_Html = s_Html.replace(re, _Replace2);
            }
        }
        return s_Html;
    }

};


var DLGTab = {

    Click: function (n_Index, n_Count) {
        if ($("tab_nav_" + n_Index).className == "tab_on") {
            return;
        }
        var s_NavID, s_TabID, s_CurrTabID;
        for (var i = 1; i <= n_Count; i++) {
            s_NavID = "tab_nav_" + i;
            s_TabID = $(s_NavID).getAttribute("_content_id", 2);
            if ($(s_NavID).className == "tab_on") {
                if (!DLGTab.FrameSize) {
                    DLGTab.FrameSize = new Array();
                }
                if (!DLGTab.FrameSize[i]) {
                    DLGTab.FrameSize[i] = {Width: $("tabDialogSize").offsetWidth, Height: $("tabDialogSize").offsetHeight};
                }

                if (!DLGTab.TabSize) {
                    DLGTab.TabSize = new Array();
                }
                if (!DLGTab.TabSize[i]) {
                    DLGTab.TabSize[i] = {Width: $(s_TabID).offsetWidth, Height: $(s_TabID).offsetHeight};
                }

                $(s_NavID).className = "tab_off";
                $(s_TabID).style.display = "none";
            }

            if (n_Index == i) {
                s_CurrTabID = s_TabID;
            }
        }
        $("tab_nav_" + n_Index).className = "tab_on";
        $(s_CurrTabID).style.display = "";
        try {
            TabOnClick(n_Index, n_Count, s_CurrTabID);
        } catch (e) {
        }
    },

    Create: function (a_Tab) {
        var s_Html = '<table class=tab_layout1 border=0 cellpadding=0 cellspacing=0 width="100%"><tr><td>'
            + '<table class=tab_layout2 border=0 cellpadding=0 cellspacing=0><tr>'
            + '<td class=tab_begin></td>';
        for (var i = 1; i <= a_Tab.length; i++) {
            var s_Class = 'tab_on';
            if (i > 1) {
                s_Html += '<td class=tab_sep></td>';
                s_Class = 'tab_off';
            }
            s_Html += '<td><table id="tab_nav_' + i + '" class="' + s_Class + '" _content_id="' + a_Tab[i - 1][1] + '" border=0 cellpadding=0 cellspacing=0><tr>'
                + '<td class=tab_left></td>'
                + '<td class=tab_center onclick="DLGTab.Click(' + i + ',' + a_Tab.length + ')">' + a_Tab[i - 1][0] + '</td>'
                + '<td class=tab_right></td>'
                + '</tr></table></td>';
        }

        s_Html += '</tr></table></td></tr></table>';
        document.write(s_Html);
    }

};


var DLGMFU = {

    Load: function (s_Type, o_Container, s_Width, s_Height, s_MultiFile) {
        if (this._Loaded) {
            return;
        }

        this._Type = s_Type;
        this._Container = o_Container;
        this._MultiFile = s_MultiFile || '1';

        this._Container.style.width = s_Width;
        this._Container.style.height = s_Height;

        EWEBActiveX.AsynCallBack("isinstalled", [true],
            function (o_Data) {
                if (!o_Data["Ret"]) {
                    DLGMFU.ShowMsg(lang["DlgComNotice"] + "<br>" + lang["DlgComMFUMsgNotInstall"]);
                    DLGMFU.CheckInstalled();
                } else {
                    DLGMFU.ShowControl();
                }
            }
        );
    },

    CheckInstalled: function () {
        EWEBActiveX.AsynCallBack("isinstalled", [false],
            function (o_Data) {
                if (!o_Data["Ret"]) {
                    if (!EWEBBrowser.IsUseLS) {
                        window.setTimeout("DLGMFU.CheckInstalled()", 1000);
                    } else {
                        var s_Html = "<input type=button class='DlgBtn' value='" + Lang["DlgActivexChkInsBtn"] + "' onclick='DLGMFU.CheckInstalled()'>";
                        DLGMFU.ShowMsg(s_Html);
                    }
                } else {
                    var s_Html = "<span class=red><b>" + lang["DlgComMFUMsgInstallOk"] + "</b></span><br><br><input type=button class='dlgBtn' value='" + lang["DlgComMFUMsgBtnOk"] + "' onclick='DLGMFU.ShowControl()'>";
                    DLGMFU.ShowMsg(s_Html);
                }
            }
        );
    },

    ShowMsg: function (s_Html) {
        this._Container.innerHTML = '<table border=0 cellpadding=0 cellspacing=5 width="100%" height="100%"><tr><td align=center valign=middle>'
            + '<table border=0 cellpadding=0 cellspacing=5>'
            + '<tr valign=top>'
            + '<td><img border=0 src="images/info.gif" align=absmiddle></td>'
            + '<td>' + s_Html + '</td>'
            + '</tr>'
            + '</table>'
            + '</td></tr></table>';
    },

    ShowControl: function () {
        switch (this._Type) {
            case 'image':
                this._AllowSize = config.AllowImageSize;
                this._AllowExt = config.AllowImageExt;
                this._FileTypeDesc = lang['DlgMFUFYDImage'];
                break;
            case 'media':
                this._AllowSize = config.AllowMediaSize;
                this._AllowExt = config.AllowMediaExt;
                this._FileTypeDesc = lang['DlgMFUFYDMedia'];
                break;
            case 'flash':
                this._AllowSize = config.AllowFlashSize;
                this._AllowExt = config.AllowFlashExt;
                this._FileTypeDesc = lang['DlgMFUFYDFlash'];
                break;
            case 'file':
                this._AllowSize = config.AllowFileSize;
                this._AllowExt = config.AllowFileExt;
                this._FileTypeDesc = lang['DlgMFUFYDFile'];
                break;
        }

        var s_Html = '<table id="mfu_maintable" border=0 cellpadding=0 cellspacing=5 width="100%" height="100%">'
            + '<tr>'
            + '<td id="mfu_msgtd">'
            + lang['DlgComMFUMsgAllow'].replace('<ext>', this._AllowExt).replace('<size>', FormatKB(this._AllowSize))
            + '</td>'
            + '</tr>'
            + '<tr>'
            + '<td>'
            + '<table width="100%" cellspacing=0 cellpadding=0><tr>'
            + '<td><input id="mfu_btn_browse" type=button class="dlgBtnCommon dlgBtn" value="' + lang['DlgMFUBrowse'] + '" onclick="DLGMFU.Browse()">&nbsp;'
            + '<input id="mfu_btn_del" type=button class="dlgBtnCommon dlgBtn" value="' + lang['DlgMFUDel'] + '" onclick="DLGMFU.Del()">&nbsp;'
            + '<input id="mfu_btn_empty" type=button class="dlgBtnCommon dlgBtn" value="' + lang['DlgMFUEmpty'] + '" onclick="DLGMFU.Empty()"></td>'
            + '<td align=right><input id="mfu_btn_up" type=button class="dlgBtnCommon dlgBtn" value="' + lang['DlgMFUUp'] + '" onclick="DLGMFU.Up()">&nbsp;'
            + '<input id="mfu_btn_down" type=button class="dlgBtnCommon dlgBtn" value="' + lang['DlgMFUDown'] + '" onclick="DLGMFU.Down()"></td>'
            + '</tr></table>'
            + '</td>'
            + '</tr>'
            + '<tr>'
            + '<td id="mfu_list_ptd" height="100%">'
            + '<div id="mfu_list_div" style="width:100%;height:100%;overflow:auto">'
            + '<table id="mfu_list_table" class="mfu_list_table" width="100%" cellspacing=1 cellpadding=2 border=0>'
            + '<tr class="mfu_list_headtr">'
            + '<td width="8%">' + lang["DlgMFUSeq"] + '</td>'
            + '<td width="50%">' + lang["DlgMFUFileName"] + '</td>'
            + '<td width="14%">' + lang["DlgMFUFileSize"] + '</td>'
            + '<td width="14%">' + lang["DlgMFUProgress"] + '</td>'
            + '<td width="14%">' + lang["DlgMFUStatus"] + '</td>'
            + '</tr>'
            + '</table>'
            + '</div>'
            + '</td>'
            + '</tr>'
            + '<tr>'
            + '<td>'
            + '<table width="100%" cellspacing=0 cellpadding=0><tr>'
            + '<td id="mfu_tpb_td">'
            + '<table border=0 cellpadding=0 cellspacing=0>'
            + '<tr>'
            + '<td>' + this._GetTotalPropress(0, 0) + '</td>'
            + '<td width="10px"></td>'
            + '<td width="100px">' + this._GetPBHtml('t', '0%') + '</td>'
            + '</tr>'
            + '</table>'
            + '</td>'
            + '<td align=right><input id="mfu_btn_upload" type=button class="dlgBtnCommon dlgBtn" value="' + lang['DlgMFUUpload'] + '" onclick="DLGMFU.Upload()">&nbsp;'
            + '<input id="mfu_btn_cancel" type=button class="dlgBtnCommon dlgBtn" value="' + lang['DlgMFUCancel'] + '" onclick="DLGMFU.Cancel()"></td>'
            + '</tr></table>'
            + '</td>'
            + '</tr>'
            + '</table>';

        this._Container.innerHTML = s_Html;

        //if (EWEBBrowser.IsIE9Compat || EWEBBrowser.IsIE10P){
        //	var h = parseInt(this._Container.style.height)-$("mfu_msgtd").offsetHeight-15;
        //	MFU.style.height = h + "px";
        //}

        if (EWEBBrowser.IsUseLS) {
            var s_Prop = config.MFUBlockSize + "\r" + this._Type + "\r" + this._AllowSize + "\r" + this._AllowExt + "\r" + this._MultiFile;
            EWEBActiveX.AsynCallBack("mfu_init", [s_Prop]);
        } else {
            EWEBActiveX.ActiveX.MFUEmpty();
            EWEBActiveX.ActiveX.BlockSize = config.MFUBlockSize;
            EWEBActiveX.ActiveX.FileType = this._Type;	//image,media,flash,file
            EWEBActiveX.ActiveX.AllowSize = this._AllowSize;
            EWEBActiveX.ActiveX.AllowExt = this._AllowExt;
            if (this._MultiFile == '0') {
                EWEBActiveX.ActiveX.MultiFile = this._MultiFile;
            }
        }

        $('mfu_list_div').style.height = $('mfu_list_div').offsetHeight + 'px';

        this._listTable = $("mfu_list_table");
        this._SelectedIndex = 0;
        this._RefreshBtnState();
        this._Loaded = true;
        EWEBTools.AddEventListener(this._listTable, 'click', this._ClickHandler);
        EWEBTools.AddEventListener(this._listTable, 'mouseover', this._MouseOverHandler);
        EWEBTools.AddEventListener(this._listTable, 'mouseout', this._MouseOutHandler);
    },

    Browse: function () {
        var b_MultiFile = (this._MultiFile == '0') ? 0 : 1;
        var s_Filter = this._FileTypeDesc + "(*." + this._AllowExt.replace(/\|/gi, ",*.") + ")|*." + this._AllowExt.replace(/\|/gi, ";*.");

        EWEBActiveX.AsynCallBack("dialogopen", [1, b_MultiFile, s_Filter, 1],
            function (o_Data) {
                var s_Files = o_Data["Ret"];
                if (s_Files == "") {
                    return;
                }

                var a_Files = s_Files.split("|");
                if (EWEBBrowser.IsUseLS) {
                    EWEBActiveX.AsynCallBack("mfu_getinfos", [s_Files],
                        function (o_Data) {
                            var a_Infos = o_Data["Ret"].split("\r");
                            DLGMFU._Browse2(a_Files, a_Infos, b_MultiFile);
                        }
                    );
                } else {
                    var a_Infos = [];
                    for (var i = 0; i < a_Files.length; i++) {
                        a_Infos[i] = EWEBActiveX.ActiveX.MFUAdd(a_Files[i]);
                    }
                    DLGMFU._Browse2(a_Files, a_Infos, b_MultiFile);
                }
            }
        );
    },

    _Browse2: function (a_Files, a_Infos, b_MultiFile) {
        var n_ErrCount = 0;
        for (var i = 0; i < a_Files.length; i++) {
            var s_Info = a_Infos[i];
            if (s_Info) {
                if (!b_MultiFile) {
                    this.Empty(true);
                }
                this._AddFileToList(s_Info);
            } else {
                n_ErrCount++;
            }
        }

        if (n_ErrCount > 0) {
            alert(lang["DlgMFUMsgFilter"].replace("{count}", n_ErrCount));
        }
        if (this._SelectedIndex <= 0) {
            this._SelectRow(1);
        }
        this._RefreshBtnState();
    },

    Empty: function (b_OnlyUI) {
        var n_Count = this._listTable.rows.length - 1;
        for (var i = n_Count; i > 0; i--) {
            this._listTable.deleteRow(-1);
        }
        this._SelectedIndex = 0;
        this._RefreshBtnState();
        if (!b_OnlyUI) {
            if (EWEBBrowser.IsUseLS) {
                EWEBActiveX.AsynCallBack("mfuempty", []);
            } else {
                EWEBActiveX.ActiveX.MFUEmpty();
            }
        }
    },

    Del: function () {
        var n_Index = this._SelectedIndex;
        if (n_Index <= 0) {
            return;
        }

        var o_Table = this._listTable;
        o_Table.deleteRow(n_Index);
        var n_Count = o_Table.rows.length - 1;
        if (n_Count <= 0) {
            this._SelectedIndex = 0;
        } else if (n_Count < n_Index) {
            this._SelectedIndex = 0;
            this._SelectRow(n_Count);
        } else {
            this._SelectedIndex = 0;
            this._SelectRow(n_Index);
            for (var i = n_Index; i <= n_Count; i++) {
                o_Table.rows[i].cells[0].innerHTML = i + "";
            }
        }
        DLGMFU._RefreshBtnState();

        if (EWEBBrowser.IsUseLS) {
            EWEBActiveX.AsynCallBack("mfudel", [n_Index]);
        } else {
            EWEBActiveX.ActiveX.MFUDel(n_Index);
        }
    },

    Up: function () {
        var n_Index = this._SelectedIndex;
        var n_Count = this._listTable.rows.length - 1;
        if (n_Index <= 1 || n_Index > n_Count) {
            return;
        }

        if (EWEBBrowser.IsUseLS) {
            EWEBActiveX.AsynCallBack("mfu_up", [n_Index],
                function (o_Data) {
                    var a_Info = o_Data["Ret"].split("\r");
                    DLGMFU._EditFileInList(n_Index, a_Info[0]);
                    DLGMFU._EditFileInList(n_Index - 1, a_Info[1]);
                    DLGMFU._SelectRow(n_Index - 1);
                    DLGMFU._RefreshBtnState();
                }
            );
        } else {
            EWEBActiveX.ActiveX.MFUUp(n_Index);
            DLGMFU._EditFileInList(n_Index, EWEBActiveX.ActiveX.MFUFileInfo(n_Index));
            DLGMFU._EditFileInList(n_Index - 1, EWEBActiveX.ActiveX.MFUFileInfo(n_Index - 1));
            DLGMFU._SelectRow(n_Index - 1);
            DLGMFU._RefreshBtnState();
        }
    },

    Down: function () {
        var n_Index = this._SelectedIndex;
        var n_Count = this._listTable.rows.length - 1;
        if (n_Index <= 0 || n_Index >= n_Count) {
            return;
        }

        if (EWEBBrowser.IsUseLS) {
            EWEBActiveX.AsynCallBack("mfu_down", [n_Index],
                function (o_Data) {
                    var a_Info = o_Data["Ret"].split("\r");
                    DLGMFU._EditFileInList(n_Index, a_Info[0]);
                    DLGMFU._EditFileInList(n_Index + 1, a_Info[1]);
                    DLGMFU._SelectRow(n_Index + 1);
                    DLGMFU._RefreshBtnState();
                }
            );
        } else {
            EWEBActiveX.ActiveX.MFUDown(n_Index);
            DLGMFU._EditFileInList(n_Index, EWEBActiveX.ActiveX.MFUFileInfo(n_Index));
            DLGMFU._EditFileInList(n_Index + 1, EWEBActiveX.ActiveX.MFUFileInfo(n_Index + 1));
            DLGMFU._SelectRow(n_Index + 1);
            DLGMFU._RefreshBtnState();
        }
    },

    Upload: function () {
        if (this._listTable.rows.length < 2) {
            return;
        }
        if (EWEBActiveX.CheckIsRun()) {
            return;
        }
        this._ProgressIndex = 1;
        this._UploadBtnState(true);
        this._IsUploading = true;

        EWEBActiveX.AsynCallBack("mfuupload", [],
            function (o_Data) {
                if (o_Data["Error"] != "ok") {
                    EWEBActiveX.SetIsRun(false);
                    DLGMFU._UploadBtnState(false);
                    DLGMFU._IsUploading = false;
                    EWEBActiveX.IsError(o_Data["Error"]);
                    return;
                }

                DLGMFU._TimerUpload();
            }
        );
    },

    Cancel: function () {
        if (this._IsUploading) {
            if (EWEBBrowser.IsUseLS) {
                EWEBActiveX.AsynCallBack("mfucancel", []);
            } else {
                EWEBActiveX.ActiveX.MFUCancel();
            }
            EWEBActiveX.SetIsRun(false);
        } else {
            parent.Cancel();
        }
    },

    _TimerUpload: function () {
        var s_State;
        if (EWEBBrowser.IsUseLS) {
            EWEBActiveX.AsynCallBack("mfustate", [],
                function (o_Data) {
                    s_State = o_Data["Ret"];
                    DLGMFU._TimerUpload2(s_State);
                }
            );
        } else {
            s_State = EWEBActiveX.ActiveX.MFUState();
            DLGMFU._TimerUpload2(s_State);
        }
    },

    _TimerUpload2: function (s_State) {
        if (!s_State) {
            EWEBActiveX.SetIsRun(false);
            return;
        }

        var b_StopTimer = false;
        var a_Ret = s_State.split('|');
        var s_StateFlag = a_Ret[0];
        var n_Index = parseInt(a_Ret[1]);
        var n_Count = parseInt(a_Ret[2]);
        var n_Percent = parseInt(a_Ret[3]);
        var s_ErrCount = a_Ret[4];

        var n1 = DLGMFU._ProgressIndex;

        EWEBActiveX.AsynCallBack("mfu_fileinfo_fromto", [n1, n_Index],
            function (o_Data) {
                var a_Infos = o_Data["Ret"].split("\r");

                for (var n = n1; n <= n_Index; n++) {
                    var s_FileInfo = a_Infos[n - n1];
                    var a_FileInfo = s_FileInfo.split("|");
                    var s_InfoPercent = a_FileInfo[3];
                    var s_InfoState = a_FileInfo[4];
                    DLGMFU._SetPBValue(n, s_InfoPercent);
                    DLGMFU._listTable.rows[n].cells[4].innerHTML = DLGMFU._GetStateLang(s_InfoState);
                    if (s_InfoState == "ok" || s_InfoState == "errorupload") {
                        DLGMFU._ProgressIndex = n + 1;
                    }
                }
                DLGMFU._SetTotalProgress(n_Index, n_Count, n_Percent);

                switch (s_StateFlag) {
                    case 'cancel':
                        b_StopTimer = true;
                        EWEBActiveX.SetIsRun(false);
                        DLGMFU._UploadBtnState(false);
                        DLGMFU._IsUploading = false;
                        break;
                    case 'endall':
                        b_StopTimer = true;
                        EWEBActiveX.SetIsRun(false);
                        DLGMFU._UploadBtnState(false);
                        DLGMFU._Return();
                        break;
                    case 'endapart':
                        b_StopTimer = true;
                        EWEBActiveX.SetIsRun(false);

                        if (confirm(lang["DlgMFUErrRetry"].replace("{count}", s_ErrCount))) {
                            DLGMFU.Upload();
                        } else {
                            DLGMFU._UploadBtnState(false);
                            DLGMFU._Return();
                        }
                        break;
                    case 'uploading':

                        break;
                }

                if (!b_StopTimer) {
                    window.setTimeout(DLGMFU._TimerUpload, 300);
                }

            }
        );
    },

    _Return: function () {
        try {
            if (EWEBBrowser.IsUseLS) {
                EWEBActiveX.AsynCallBack("mfuokurl", [],
                    function (o_Data) {
                        MFUReturn(o_Data["Ret"]);
                    }
                );
            } else {
                MFUReturn(EWEBActiveX.ActiveX.MFUOkUrl());
            }
        } catch (e) {
        }
    },

    _ClickHandler: function (ev) {
        if (!ev) {
            ev = window.event;
        }
        var o_Target = ev.srcElement || ev.target;
        var o_TR = EWEBTools.GetParentNodeByTag(o_Target, "TR");
        if (!DLGMFU._IsInListTable(o_TR)) {
            return;
        }
        if (o_TR.rowIndex > 0) {
            DLGMFU._SelectRow(o_TR.rowIndex);
            DLGMFU._RefreshBtnState();
        }
    },

    _MouseOverHandler: function (ev) {
        DLGMFU._Highlight(ev, true);
    },

    _MouseOutHandler: function (ev) {
        DLGMFU._Highlight(ev, false);
    },

    _Highlight: function (ev, b_Hover) {
        if (!ev) {
            ev = window.event;
        }
        var o_Target = ev.srcElement || ev.target;
        var o_TR = EWEBTools.GetParentNodeByTag(o_Target, "TR");
        if (!DLGMFU._IsInListTable(o_TR)) {
            return;
        }
        if (o_TR.rowIndex > 0 && o_TR.rowIndex != DLGMFU._SelectedIndex) {
            var s_ClassName = b_Hover ? "mfu_list_tr_hover" : "";
            DLGMFU._listTable.rows[o_TR.rowIndex].className = s_ClassName;
        }
    },

    _IsInListTable: function (o_TR) {
        if (o_TR) {
            var o_Table = EWEBTools.GetParentNodeByTag(o_TR, "TABLE");
            if (o_Table == this._listTable) {
                return true;
            }
        }
        return false;

    },

    _SelectRow: function (n_Index) {
        if (this._SelectedIndex == n_Index || n_Index < 1 || this._listTable.rows.length <= n_Index) {
            return;
        }

        if (this._SelectedIndex > 0) {
            this._listTable.rows[this._SelectedIndex].className = "";
        }
        this._listTable.rows[n_Index].className = "mfu_list_tr_selected";
        this._SelectedIndex = n_Index;
    },

    _GetPBHtml: function (s_Key, s_Percent) {
        var s_id_c = '';
        var s_id_p = '';
        var s_id_t = '';
        if (isNaN(parseInt(s_Key))) {
            s_id_c = 'id="mfu_pb_c_' + s_Key + '"';
            s_id_p = 'id="mfu_pb_p_' + s_Key + '"';
            s_id_t = 'id="mfu_pb_t_' + s_Key + '"';
        }

        var s_Html = '<div ' + s_id_c + ' class="mfu_pb_c">'
            + '<div ' + s_id_p + ' class="mfu_pb_p" style="width:' + s_Percent + '"></div>'
            + '<div ' + s_id_t + ' class="mfu_pb_t">' + s_Percent + '</div>'
            + '</div>';
        return s_Html;
    },

    _SetPBValue: function (s_Key, s_Percent) {
        var o_p;
        var o_t;
        if (isNaN(parseInt(s_Key))) {
            o_p = $('mfu_pb_p_' + s_Key);
            o_t = $('mfu_pb_t_' + s_Key);
        } else {
            var o_col = this._listTable.rows[parseInt(s_Key)].cells[3];
            var o_divs = o_col.getElementsByTagName("div");
            for (var i = 0; i < o_divs.length; i++) {
                if (o_divs[i].className == 'mfu_pb_p') {
                    o_p = o_divs[i];
                }
                if (o_divs[i].className == 'mfu_pb_t') {
                    o_t = o_divs[i];
                }
            }
        }
        o_p.style.width = s_Percent;
        o_t.innerHTML = s_Percent;
    },

    _AddFileToList: function (s_FileInfo) {
        var a_FileInfo = s_FileInfo.split("|");
        var o_Row = this._listTable.insertRow(-1);
        var o_Cell = o_Row.insertCell(-1);
        o_Cell.innerHTML = a_FileInfo[0];
        o_Cell = o_Row.insertCell(-1);
        o_Cell.style.textAlign = "left";
        o_Cell.innerHTML = this._EncodeFileName(a_FileInfo[1]);
        o_Cell = o_Row.insertCell(-1);
        o_Cell.innerHTML = a_FileInfo[2];
        o_Cell = o_Row.insertCell(-1);
        o_Cell.innerHTML = this._GetPBHtml(a_FileInfo[0], a_FileInfo[3]);
        o_Cell = o_Row.insertCell(-1);
        o_Cell.innerHTML = this._GetStateLang(a_FileInfo[4]);
    },

    _EditFileInList: function (n_Index, s_FileInfo) {
        var a_FileInfo = s_FileInfo.split("|");
        var o_Row = this._listTable.rows[n_Index];
        o_Row.cells[0].innerHTML = a_FileInfo[0];
        o_Row.cells[1].innerHTML = this._EncodeFileName(a_FileInfo[1]);
        o_Row.cells[2].innerHTML = a_FileInfo[2];
        o_Row.cells[3].innerHTML = this._GetPBHtml(a_FileInfo[0], a_FileInfo[3]);
        o_Row.cells[4].innerHTML = this._GetStateLang(a_FileInfo[4]);
    },

    _EncodeFileName: function (s_Name) {
        var s_Ret = s_Name;
        s_Ret = s_Ret.replace(/&/gi, "&amp;");
        s_Ret = s_Ret.replace(/\"/gi, "&quot;");
        s_Ret = s_Ret.replace(/</gi, "&lt;");
        s_Ret = s_Ret.replace(/>/gi, "&gt;");
        return s_Ret;
    },

    _GetStateLang: function (s_Flag) {
        var s_Lang;
        switch (s_Flag) {
            case 'ok':
                s_Lang = lang["DlgMFUStateOk"];
                break;
            case 'uploading':
                s_Lang = lang["DlgMFUStateUploading"];
                break;
            case 'errorupload':
                s_Lang = lang["DlgMFUStateErrUpload"];
                break;
            case 'cancel':
                s_Lang = lang["DlgMFUStateCancel"];
                break;
            case 'wait':
                s_Lang = lang["DlgMFUStateWait"];
                break;
        }
        return s_Lang;
    },

    _RefreshBtnState: function () {
        var n_Count = this._listTable.rows.length - 1;
        var n_Index = this._SelectedIndex;

        var b = (n_Count <= 0) ? true : false;
        this._DisableBtn("mfu_btn_upload", b);
        this._DisableBtn("mfu_btn_del", b);
        this._DisableBtn("mfu_btn_empty", b);

        b = (n_Index <= 1) ? true : false;
        this._DisableBtn("mfu_btn_up", b);

        b = (n_Index >= n_Count) ? true : false;
        this._DisableBtn("mfu_btn_down", b);
    },

    _UploadBtnState: function (b) {
        this._DisableBtn("mfu_btn_browse", b);
        this._DisableBtn("mfu_btn_del", b);
        this._DisableBtn("mfu_btn_empty", b);
        this._DisableBtn("mfu_btn_up", b);
        this._DisableBtn("mfu_btn_down", b);
        this._DisableBtn("mfu_btn_upload", b);
    },

    _DisableBtn: function (s_Name, b) {
        var s_ClassName = b ? "dlgBtnCommon dlgBtn dlgBtnDisabled" : "dlgBtnCommon dlgBtn";
        $(s_Name).disabled = b;
        $(s_Name).className = s_ClassName;
    },

    _GetTotalPropress: function (n_Index, n_Count, n_Percent) {
        var s_Html = lang['DlgMFUTotalProgress'].replace('{index}', '<span id="mfu_tpb_index">' + n_Index + '</span>').replace('{count}', '<span id="mfu_tpb_count">' + n_Count + '</span>');
        return s_Html;
    },

    _SetTotalProgress: function (n_Index, n_Count, n_Percent) {
        $('mfu_tpb_index').innerHTML = n_Index + '';
        $('mfu_tpb_count').innerHTML = n_Count + '';
        this._SetPBValue('t', n_Percent + '%');
    }


};


function FormatKB(s_KB) {
    var n = parseFloat(s_KB);
    var s = ""
    if (n >= 1048576) {
        n = n / 1048576;
        s = Math.round(n * 100) / 100 + "GB";
    } else if (n >= 1024) {
        n = n / 1024;
        s = Math.round(n * 100) / 100 + "MB";
    } else {
        s = s_KB + "KB";
    }
    return s;
}

function AddUnitPX(s_Value) {
    if (!s_Value) {
        return "";
    }

    if (s_Value.search(/[^0-9]+/) != -1) {
        return s_Value;
    } else {
        return s_Value + "px";
    }
}


var DLGRunOne = (function () {
    var _aStatus = new Object();

    return {

        IsRun: function (s_Flag) {
            return (_aStatus[s_Flag] ? true : false);
        },

        DisableBtn: function (s_BtnID, b_Disabled) {
            var o_Btn = $(s_BtnID);
            var s_ClassName = o_Btn.className;
            o_Btn.disabled = b_Disabled;
            if (s_ClassName.indexOf("dlgBtnDisabled") >= 0) {
                if (!b_Disabled) {
                    o_Btn.className = s_ClassName.replace(/ *dlgBtnDisabled/gi, "");
                }
            } else {
                if (b_Disabled) {
                    o_Btn.className = s_ClassName + " dlgBtnDisabled";
                }
            }
            _aStatus[s_BtnID] = b_Disabled;
        }

    };

})();


var DLGDoingMsg = (function () {
    var _IsShow = false;
    var _OriMsg = "";

    return {
        OutHtml: function (s_Msg) {
            _OriMsg = s_Msg;
            document.write('<div id=divProcessing style="width:200px;height:30px;position:absolute;left:-10000px;top:-10000px;">'
                + '<table border=0 cellpadding=0 cellspacing=1 bgcolor="#000000" width="100%" height="100%"><tr><td bgcolor=#3A6EA5><marquee align="middle" behavior="alternate" scrollamount="5" id="marq_msg"><span style="color:#ffffff">' + s_Msg + '</span></marquee></td></tr></table>'
                + '</div>');
        },

        ChangeMsg: function (s_Msg, s_ColorFlag) {
            var s_Color = "#ffffff";
            switch (s_ColorFlag) {
                case "1":
                    s_Color = "#ffff00";
                    break;
                case "2":
                    s_Color = "#ff0000";
                    break;
            }
            $("marq_msg").innerHTML = "<span style='color:" + s_Color + "'>" + s_Msg + "</span>";
        },

        ChangeCancelMsg: function (b_Cancel) {
            if (b_Cancel) {
                DLGDoingMsg.ChangeMsg(lang["MsgCancel"], "1");
            } else {
                DLGDoingMsg.ChangeMsg(_OriMsg);
            }
        },

        IsShow: function () {
            return _IsShow;
        },

        Show: function () {
            _IsShow = true;
            var s_Left = parseInt(($("tabDialogSize").offsetWidth - 200) / 2) + "px";
            var s_Top = parseInt(($("tabDialogSize").offsetHeight - 20 - 30) / 2) + "px";
            $("divProcessing").style.left = s_Left;
            $("divProcessing").style.top = s_Top;
        },

        Hide: function () {
            _IsShow = false;
            $("divProcessing").style.left = "-10000px";
            $("divProcessing").style.top = "-10000px";
        }

    };
})();
