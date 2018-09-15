/*
*######################################
* eWebEditor V11.5 - Advanced online web based WYSIWYG HTML editor.
* Copyright (c) 2003-2018 eWebSoft.com
*
* For further information go to http://www.ewebeditor.net/
* This copyright notice MUST stay intact for use.
*######################################
*/


if (!window.EWEBEDITOR) {
    window.EWEBEDITOR = (function () {
        var EWEBEDITOR = {
            Version: '11.5',
            ReplaceByClassName: 'ewebeditor',
            ReplaceByClassEnabled: true,
            Config: {style: 'coolblue', width: '100%', height: '350'},

            BasePath: (function () {
                var s_Path = window.EWEBEDITOR_BASEPATH || '';

                if (!s_Path) {
                    var a_Scripts = document.getElementsByTagName('script');
                    for (var i = 0; i < a_Scripts.length; i++) {
                        var m = a_Scripts[i].src.match(/(^|.*[\\\/])ewebeditor.js$/i);
                        if (m) {
                            s_Path = m[1];
                            break;
                        }
                    }
                }

                if (s_Path.indexOf(':/') == -1) {
                    if (s_Path.indexOf('/') === 0) {
                        s_Path = location.href.match(/^.*?:\/\/[^\/]*/)[0] + s_Path;
                    } else {
                        s_Path = location.href.match(/^[^\?]*\/(?:)/)[0] + s_Path;
                    }
                }

                //if ( !s_Path ){
                //	alert('The EWEBEDITOR installation path could not be automatically detected. Please set the global variable "EWEBEDITOR_BASEPATH" before creating editor instances.');
                //}

                return s_Path;
            })(),

            Instances: {},

            Create: function (s_Id, o_Config, s_Html) {
                return this._create(s_Id, o_Config, 'create', s_Html);
            },

            Append: function (s_Id, o_Config, s_Html) {
                return this._create(s_Id, o_Config, 'append', s_Html);
            },

            Replace: function (s_Id, o_Config) {
                return this._create(s_Id, o_Config, 'replace');
            },

            ReplaceAll: function () {
                var o_Textareas = document.getElementsByTagName('textarea');

                for (var i = 0; i < o_Textareas.length; i++) {
                    var o_Config = null;
                    var o_Textarea = o_Textareas[i];
                    var s_Id = o_Textarea.name || o_Textarea.id;

                    if (!s_Id) {
                        continue;
                    }

                    if (typeof arguments[0] == 'string') {
                        var o_Regex = new RegExp('(?:^|\\s)' + arguments[0] + '(?:$|\\s)');
                        if (!o_Regex.test(o_Textarea.className)) {
                            continue;
                        }
                    } else if (typeof arguments[0] == 'function') {
                        o_Config = this._cloneObject(this.Config);
                        if (arguments[0](o_Textarea, o_Config) === false) {
                            continue;
                        }
                    }

                    if (!o_Config) {
                        o_Config = this._cloneObject(this.Config);
                        var s_AttrConf = o_Textarea.getAttribute('config');
                        if (s_AttrConf) {
                            var a1 = s_AttrConf.split(';');
                            for (var j = 0; j < a1.length; j++) {
                                var a2 = a1[j].split(':');
                                var v0 = this._trim(a2[0]);
                                if (v0) {
                                    o_Config[v0] = this._trim(a2[1]);
                                }
                            }
                        }
                    }

                    this.Replace(s_Id, o_Config);
                }
            },

            UpdateAll: function () {
                for (var s_Id in this.Instances) {
                    this.Instances[s_Id].Update();
                }
            },

            _add: function (editor) {
                this.Instances[editor.id] = editor;
            },

            _remove: function (editor) {
                delete this.Instances[editor.id];
            },

            _create: function (s_Id, o_Config, s_Mode, s_Html) {
                if (this.Instances[s_Id]) {
                    this.Instances[s_Id].Remove(true);
                }

                var editor = new this.editor(s_Id, o_Config, s_Mode, s_Html);
                this._add(editor);
                return editor;
            },

            _setTimeout: function (o_func, n_Milliseconds, o_ThisObject, a_ParamsArray, o_TimerWindow) {
                return (o_TimerWindow || window).setTimeout(
                    function () {
                        if (a_ParamsArray) {
                            o_func.apply(o_ThisObject, [].concat(a_ParamsArray));
                        } else {
                            o_func.apply(o_ThisObject);
                        }
                    },
                    n_Milliseconds);
            },

            SetHtmlAsync: function (s_Id, s_Html) {
                var editor = EWEBEDITOR.Instances[s_Id];
                if (editor) {
                    if (editor.Loaded) {
                        editor.setHTML(s_Html);
                        return true;
                    }
                }
                EWEBEDITOR._setTimeout(EWEBEDITOR.SetHtmlAsync, 200, null, [s_Id, s_Html]);
                return false;
            },

            EVENT: function (ev) {
                var editor = this.Instances[ev.id];
                if (!editor) {
                    editor = this._create(ev.id, null, 'exist');
                }
                switch (ev.flag) {
                    case 'LoadComplete':
                        editor.Loaded = true;
                        editor.win = ev.win;
                        editor.doc = ev.doc;
                        editor.getHTML = ev.win.getHTML;
                        editor.getText = ev.win.getText;
                        editor.setHTML = ev.win.setHTML;
                        editor.insertHTML = ev.win.insertHTML;
                        editor.appendHTML = ev.win.appendHTML;
                        editor.getCount = ev.win.getCount;
                        editor.setMode = ev.win.setMode;
                        editor.openUploadDialog = ev.win.openUploadDialog;
                        editor.focus = ev.win.focus;
                        editor.setReadOnly = ev.win.setReadOnly;
                        editor.localUpload = ev.win.localUpload;
                        editor.remoteUpload = ev.win.remoteUpload;
                        editor.exec = ev.win.exec;
                        editor.getSelectedHTML = ev.win.getSelectedHTML;
                        editor.getSelectedText = ev.win.getSelectedText;
                        editor.getSelectedElement = ev.win.getSelectedElement;
                        editor.getAllImageSrc = ev.win.getAllImageSrc;
                        editor.getMode = ev.win.getMode;
                        break;
                    default:

                }
            },

            _cloneObject: function (o_Original) {
                var o_New = new Object();
                for (s_Name in o_Original) {
                    o_New[s_Name] = o_Original[s_Name];
                }
                return o_New;
            },

            _trim: function (s) {
                return s.replace(/(^[ \t\n\r]*)|([ \t\n\r]*$)/g, '');
            }

        };


        return EWEBEDITOR;
    })();


    (function () {
        var _Onload = function () {
            if (EWEBEDITOR.ReplaceByClassEnabled) {
                EWEBEDITOR.ReplaceAll(EWEBEDITOR.ReplaceByClassName);
            }
        };

        if (window.addEventListener) {
            window.addEventListener('load', _Onload, false);
        } else if (window.attachEvent) {
            window.attachEvent('onload', _Onload);
        }
    })();

}


if (!EWEBEDITOR.editor) {

    (function () {

        var _GetSpecialLinkField = function (s_Tag, s_Id) {
            var els = document.getElementsByTagName(s_Tag);
            for (var i = 0; i < els.length; i++) {
                var s_Name = els[i].getAttribute('name');
                if (!s_Name) {
                    continue;
                }

                var n = s_Name.lastIndexOf('$');
                if (n < 0) {
                    n = s_Name.lastIndexOf(':');
                }

                if (n >= 0) {
                    s_Name = s_Name.substr(n + 1);
                    if (s_Name == s_Id) {
                        return els[i];
                    }
                }
            }
            return null;
        };

        var _nNameCounter = 0;
        var _GetNewName = function (s_Flag) {
            var s_Name = 'ewebeditor_auto_' + s_Flag + '_' + (++_nNameCounter);
            return (EWEBEDITOR.Instances[s_Name] || document.getElementById(s_Name) || document.getElementsByName(s_Name).length > 0) ? _GetNewName(s_Flag) : s_Name;
        };

        var _HTMLEncode = function (s_Html) {
            s_Html = s_Html.replace(
                /&/g, '&amp;').replace(
                /\"/g, '&quot;').replace(
                /</g, '&lt;').replace(
                />/g, '&gt;');

            return s_Html;
        };

        var _InsertHtmlBefore = function (s_Html, o_Element) {
            if (o_Element.insertAdjacentHTML) {
                o_Element.insertAdjacentHTML('beforeBegin', s_Html);
            } else {
                var oRange = document.createRange();
                oRange.setStartBefore(o_Element);
                var oFragment = oRange.createContextualFragment(s_Html);
                o_Element.parentNode.insertBefore(oFragment, o_Element);
            }
        };

        var _GetLinkElementFromId = function (s_Id) {
            var o_Element = document.getElementById(s_Id);
            if (!o_Element) {
                o_Element = document.getElementsByName(s_Id)[0];
            }
            if (!o_Element) {
                o_Element = _GetSpecialLinkField('input', s_Id);
            }
            if (!o_Element) {
                o_Element = _GetSpecialLinkField('textarea', s_Id);
            }
            if (!o_Element) {
                alert('[EWEBEDITOR.editor] The element with id or name "' + s_Id + '" was not found.');
            }
            return o_Element;
        };

        var _GetIframeIdFromLinkId = function (s_LinkId, o_Config) {
            var s_IframeId;
            var o_Iframes = document.getElementsByTagName('iframe');
            for (var i = 0; i < o_Iframes.length; i++) {
                var o_Iframe = o_Iframes[i];
                var s_Src = _GetAttribute(o_Iframe, 'src');
                var o_Regex = new RegExp('/ewebeditor\.[a-z]{3}\?.*?id=' + s_LinkId.replace('$', '\$') + '(?:$|\\s|&)', 'gi');
                if (o_Regex.test(s_Src)) {
                    s_IframeId = _GetAttribute(o_Iframe, 'id');
                    if (!s_IframeId) {
                        s_IframeId = _GetNewName('iframe');
                        _SetAttribute(o_Iframe, 'id', s_IframeId);
                    }

                    s_Src = s_Src.substring(s_Src.indexOf('?') + 1);
                    s_Src = s_Src.replace('&amp;', '&');
                    var a_Params = s_Src.split('&');
                    for (var i = 0; i < a_Params.length; i++) {
                        var a_Param = a_Params[i].split('=');
                        o_Config[a_Param[0]] = a_Param[1];
                    }
                    o_Config.width = _GetAttribute(o_Iframe, 'width');
                    o_Config.height = _GetAttribute(o_Iframe, 'height');
                    o_Config.display = o_Iframe.style.display;
                    break;
                }
            }
            return s_IframeId;
        };


        var _GetAttribute = function (o_Element, s_AttrName) {
            var s_Att = o_Element.attributes[s_AttrName];
            if (s_Att == null || !s_Att.specified) {
                return "";
            }
            var s_Value = o_Element.getAttribute(s_AttrName, 2);
            if (s_Value == null) {
                s_Value = s_Att.nodeValue;
            }
            return (s_Value == null ? "" : s_Value);
        };

        var _SetAttribute = function (o_Element, s_AttrName, s_AttrValue) {
            if (s_AttrValue == null || s_AttrValue.length == 0) {
                o_Element.removeAttribute(s_AttrName, 0);
            } else {
                o_Element.setAttribute(s_AttrName, s_AttrValue, 0);
            }
        };


        EWEBEDITOR.editor = function (s_Id, o_Config, s_Mode, s_Html) {
            this.Loaded = false;
            this.id = s_Id;
            var o_InstanceElement;
            var s_LinkType = 'input';
            var s_LinkId;
            var o_LinkElement;
            s_Html = s_Html || '';
            o_Config = o_Config || EWEBEDITOR._cloneObject(EWEBEDITOR.Config);
            o_Config.style = o_Config.style || EWEBEDITOR.Config.style;
            o_Config.width = o_Config.width || EWEBEDITOR.Config.width;
            o_Config.height = o_Config.height || EWEBEDITOR.Config.height;

            if (s_Mode == 'replace') {
                o_InstanceElement = _GetLinkElementFromId(s_Id);
                if (o_InstanceElement.tagName != 'TEXTAREA' && o_InstanceElement.tagName.substring(0, 5) != 'INPUT') {
                    s_LinkType = 'other';
                }

                this.OriginalDisplay = o_InstanceElement.style.display;
                o_InstanceElement.style.display = 'none';

                s_LinkId = s_Id;
                o_LinkElement = o_InstanceElement;
            } else if (s_Mode == 'append') {
                o_InstanceElement = document.getElementById(s_Id);
                if (!o_InstanceElement) {
                    alert('[EWEBEDITOR.editor] The element with id or name "' + s_Id + '" was not found.');
                }

                var o_Tmp;
                if (o_Config.linkid) {
                    s_LinkId = o_Config.linkid;
                    o_Tmp = document.getElementById(s_LinkId) || document.getElementsByName(s_LinkId)[0];
                } else {
                    s_LinkId = _GetNewName('text');
                }

                if (o_Tmp) {
                    o_LinkElement = o_Tmp;
                } else {
                    var o_Text = document.createElement('textarea');
                    o_Text.setAttribute('id', s_LinkId);
                    o_Text.setAttribute('name', s_LinkId);
                    o_Text.style.display = 'none';
                    o_Text.value = s_Html;

                    o_InstanceElement.appendChild(o_Text);
                    o_LinkElement = o_Text;
                }
            } else if (s_Mode == 'create') {
                s_LinkId = s_Id;
                var o_Tmp = document.getElementById(s_LinkId) || document.getElementsByName(s_LinkId)[0];
                if (o_Tmp) {
                    o_LinkElement = o_Tmp;
                } else {
                    var s_OutText = '<textarea id="' + s_LinkId + '" name="' + s_LinkId + '" style="display:none">' + _HTMLEncode(s_Html) + '</textarea>';
                    document.write(s_OutText);
                    o_LinkElement = document.getElementById(s_LinkId);
                }
            }


            var s_IframeId;
            if (s_Mode == 'exist') {
                s_LinkId = s_Id;
                o_LinkElement = _GetLinkElementFromId(s_LinkId);
                s_IframeId = _GetIframeIdFromLinkId(s_LinkId, o_Config);

            } else {
                s_IframeId = _GetNewName('iframe');
                var s_IframeHtml = '<iframe id="' + s_IframeId + '"'
                    + ' src="' + EWEBEDITOR.BasePath + 'ewebeditor.htm'
                    + '?id=' + s_LinkId
                    + '&instanceid=' + s_Id
                    + '&style=' + o_Config.style;

                for (var s_Name in o_Config) {
                    if (!(s_Name in {width: 1, height: 1, style: 1, linkid: 1, display: 1})) {
                        s_IframeHtml += '&' + s_Name + '=' + o_Config[s_Name];
                    }
                }

                if (o_Config.display) {
                    if (o_Config.display.toLowerCase() == 'none') {
                        s_IframeHtml += '" style="position:absolute;left:-90000px';
                    }
                }

                s_IframeHtml += '" width="' + o_Config.width
                    + '" height="' + o_Config.height
                    + '" frameborder="0" scrolling="no"></iframe>';


                if (s_Mode == 'create') {
                    document.write(s_IframeHtml);
                } else {
                    _InsertHtmlBefore(s_IframeHtml, o_LinkElement);
                }
            }


            this.Mode = s_Mode;
            this.LinkType = s_LinkType;
            this.LinkId = s_LinkId;
            this.LinkElement = o_LinkElement;
            this.InstanceElement = o_InstanceElement;
            this.IframeId = s_IframeId;
            this.Config = o_Config;

        };


        EWEBEDITOR.editor.prototype.Remove = function (b_NoUpdate) {
            if (!this.Loaded) {
                return;
            }

            if (!b_NoUpdate) {
                this.Update();
            }

            try {
                this.win.Remove();
            } catch (e) {
            }
            EWEBEDITOR._remove(this);

            var o_Iframe = document.getElementById(this.IframeId);
            if (!o_Iframe) {
                return;
            }
            o_Iframe.parentNode.removeChild(o_Iframe);

            if (this.Mode == 'replace') {
                this.LinkElement.style.display = this.OriginalDisplay;
            } else {
                var o_Text = this.LinkElement;
                o_Text.parentNode.removeChild(o_Text);
            }

            try {
                EWEBEDITOR_EVENT({flag: 'remove', id: this.id});
            } catch (e) {
            }
        };


        EWEBEDITOR.editor.prototype.Hide = function (b_Hide) {
            if (!this.Loaded) {
                return;
            }

            var o_Iframe = document.getElementById(this.IframeId);
            if (b_Hide) {
                o_Iframe.style.display = 'none';
            } else {
                o_Iframe.style.display = '';
            }
        };


        EWEBEDITOR.editor.prototype.Update = function () {
            if (!this.Loaded) {
                return;
            }

            var s_Html = this.getHTML();
            if (this.LinkType == 'input') {
                this.LinkElement.value = s_Html;
            } else {
                this.LinkElement.innerHTML = s_Html;
            }
        };


    })();

}

