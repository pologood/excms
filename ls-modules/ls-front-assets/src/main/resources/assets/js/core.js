;
if (typeof jQuery === "undefined") {
    throw new Error("core requires jQuery")
}
var GLOBAL_CONTEXTPATH = "";
!(function ($, _win, _arg) {
    //'use strict';
    var //
        ME = this,
        _doc = _win.document,
        _storage = _win['localStorage'],
        _body = _doc.body,
        _docMode = _doc.documentMode,
        _isStrict = _doc.compatMode == "CSS1Compat",
        _userAgent = navigator.userAgent.toLowerCase(),
        _language = navigator.language || navigator.browserLanguage,
        _isSecure = /^https/i.test(_win.location.protocol),
        _prototype = Object.prototype,
        _toString = _prototype.toString,
        _hasOwn = _prototype.hasOwnProperty,
        _array = Array.prototype,
        _aslice = _array.slice,
        _push = _array.push,
        _location = _win.location.href,
        _isFocus = true;
    var //
        _class2type = {},
        _tmp = {},
        _cache = {};

    "Boolean Number String Function Array Date RegExp Object Error".replace(/[^, ]+/g, function (name) {
        _class2type["[object " + name + "]"] = name.toLowerCase()
    });

    //JSON2
    if (typeof JSON !== "object") {
        JSON = {};
    }

    (function () {
        "use strict";

        var rx_one = /^[\],:{}\s]*$/;
        var rx_two = /\\(?:["\\\/bfnrt]|u[0-9a-fA-F]{4})/g;
        var rx_three = /"[^"\\\n\r]*"|true|false|null|-?\d+(?:\.\d*)?(?:[eE][+\-]?\d+)?/g;
        var rx_four = /(?:^|:|,)(?:\s*\[)+/g;
        var rx_escapable = /[\\\"\u0000-\u001f\u007f-\u009f\u00ad\u0600-\u0604\u070f\u17b4\u17b5\u200c-\u200f\u2028-\u202f\u2060-\u206f\ufeff\ufff0-\uffff]/g;
        var rx_dangerous = /[\u0000\u00ad\u0600-\u0604\u070f\u17b4\u17b5\u200c-\u200f\u2028-\u202f\u2060-\u206f\ufeff\ufff0-\uffff]/g;

        function f(n) {
            // Format integers to have at least two digits.
            return n < 10
                ? "0" + n
                : n;
        }

        function this_value() {
            return this.valueOf();
        }

        if (typeof Date.prototype.toJSON !== "function") {

            Date.prototype.toJSON = function () {

                return isFinite(this.valueOf())
                    ? this.getUTCFullYear() + "-" +
                    f(this.getUTCMonth() + 1) + "-" +
                    f(this.getUTCDate()) + "T" +
                    f(this.getUTCHours()) + ":" +
                    f(this.getUTCMinutes()) + ":" +
                    f(this.getUTCSeconds()) + "Z"
                    : null;
            };

            Boolean.prototype.toJSON = this_value;
            Number.prototype.toJSON = this_value;
            String.prototype.toJSON = this_value;
        }

        var gap;
        var indent;
        var meta;
        var rep;


        function quote(string) {

            rx_escapable.lastIndex = 0;
            return rx_escapable.test(string)
                ? "\"" + string.replace(rx_escapable, function (a) {
                var c = meta[a];
                return typeof c === "string"
                    ? c
                    : "\\u" + ("0000" + a.charCodeAt(0).toString(16)).slice(-4);
            }) + "\""
                : "\"" + string + "\"";
        }


        function str(key, holder) {

            var i;          // The loop counter.
            var k;          // The member key.
            var v;          // The member value.
            var length;
            var mind = gap;
            var partial;
            var value = holder[key];

            if (value && typeof value === "object" &&
                typeof value.toJSON === "function") {
                value = value.toJSON(key);
            }

            if (typeof rep === "function") {
                value = rep.call(holder, key, value);
            }

            switch (typeof value) {
                case "string":
                    return quote(value);

                case "number":

                    return isFinite(value)
                        ? String(value)
                        : "null";

                case "boolean":
                case "null":

                    return String(value);
                case "object":

                    if (!value) {
                        return "null";
                    }

                    gap += indent;
                    partial = [];

                    if (Object.prototype.toString.apply(value) === "[object Array]") {

                        length = value.length;
                        for (i = 0; i < length; i += 1) {
                            partial[i] = str(i, value) || "null";
                        }

                        v = partial.length === 0
                            ? "[]"
                            : gap
                                ? "[\n" + gap + partial.join(",\n" + gap) + "\n" + mind + "]"
                                : "[" + partial.join(",") + "]";
                        gap = mind;
                        return v;
                    }

                    if (rep && typeof rep === "object") {
                        length = rep.length;
                        for (i = 0; i < length; i += 1) {
                            if (typeof rep[i] === "string") {
                                k = rep[i];
                                v = str(k, value);
                                if (v) {
                                    partial.push(quote(k) + (
                                        gap
                                            ? ": "
                                            : ":"
                                    ) + v);
                                }
                            }
                        }
                    } else {

                        for (k in value) {
                            if (Object.prototype.hasOwnProperty.call(value, k)) {
                                v = str(k, value);
                                if (v) {
                                    partial.push(quote(k) + (
                                        gap
                                            ? ": "
                                            : ":"
                                    ) + v);
                                }
                            }
                        }
                    }

                    v = partial.length === 0
                        ? "{}"
                        : gap
                            ? "{\n" + gap + partial.join(",\n" + gap) + "\n" + mind + "}"
                            : "{" + partial.join(",") + "}";
                    gap = mind;
                    return v;
            }
        }

        if (typeof JSON.stringify !== "function") {
            meta = {
                "\b": "\\b",
                "\t": "\\t",
                "\n": "\\n",
                "\f": "\\f",
                "\r": "\\r",
                "\"": "\\\"",
                "\\": "\\\\"
            };
            JSON.stringify = function (value, replacer, space) {

                var i;
                gap = "";
                indent = "";

                if (typeof space === "number") {
                    for (i = 0; i < space; i += 1) {
                        indent += " ";
                    }

                } else if (typeof space === "string") {
                    indent = space;
                }

                rep = replacer;
                if (replacer && typeof replacer !== "function" &&
                    (typeof replacer !== "object" ||
                        typeof replacer.length !== "number")) {
                    throw new Error("JSON.stringify");
                }

                return str("", {"": value});
            };
        }

        if (typeof JSON.parse !== "function") {
            JSON.parse = function (text, reviver) {

                var j;

                function walk(holder, key) {

                    var k;
                    var v;
                    var value = holder[key];
                    if (value && typeof value === "object") {
                        for (k in value) {
                            if (Object.prototype.hasOwnProperty.call(value, k)) {
                                v = walk(value, k);
                                if (v !== undefined) {
                                    value[k] = v;
                                } else {
                                    delete value[k];
                                }
                            }
                        }
                    }
                    return reviver.call(holder, key, value);
                }

                text = String(text);
                rx_dangerous.lastIndex = 0;
                if (rx_dangerous.test(text)) {
                    text = text.replace(rx_dangerous, function (a) {
                        return "\\u" +
                            ("0000" + a.charCodeAt(0).toString(16)).slice(-4);
                    });
                }

                if (
                    rx_one.test(
                        text
                            .replace(rx_two, "@")
                            .replace(rx_three, "]")
                            .replace(rx_four, "")
                    )
                ) {


                    j = eval("(" + text + ")");


                    return (typeof reviver === "function")
                        ? walk({"": j}, "")
                        : j;
                }

                throw new SyntaxError("JSON.parse");
            };
        }
    }());

    //log
    function log() {
        if (window.console) {
            Function.apply.call(console.log, console, arguments)
        } else {
            window.console = {}
            console.log = function (str) {
                $(_doc).ready(function () {
                    var div = _doc.createElement("pre");
                    div.className = "mass_sys_log";
                    div.innerHTML = str + ""; //确保为字符串
                    _doc.body.appendChild(div);
                });
            }
        }
    }

    //artTemplate
    (function (global) {

        /**
         * 模板引擎
         * @name    template
         * @param   {String}            模板名
         * @param   {Object, String}    数据。如果为字符串则编译并缓存编译结果
         * @return  {String, Function}  渲染好的HTML字符串或者渲染方法
         */
        var template = function (filename, content) {
            return typeof content === 'string'
                ? compile(content, {
                    filename: filename
                })
                : renderFile(filename, content);
        };

        template.version = '3.0.0';

        /**
         * 设置全局配置
         * @name    template.config
         * @param   {String}    名称
         * @param   {Any}       值
         */
        template.config = function (name, value) {
            defaults[name] = value;
        };

        var defaults = template.defaults = {
            openTag: '<?',    // 逻辑语法开始标签
            closeTag: '?>',   // 逻辑语法结束标签
            escape: true,     // 是否编码输出变量的 HTML 字符
            cache: true,      // 是否开启缓存（依赖 options 的 filename 字段）
            compress: false,  // 是否压缩输出
            parser: null      // 自定义语法格式器 @see: template-syntax.js
        };

        var cacheStore = template.cache = {};

        /**
         * 渲染模板
         * @name    template.render
         * @param   {String}    模板
         * @param   {Object}    数据
         * @return  {String}    渲染好的字符串
         */
        template.render = function (source, options) {
            return compile(source, options);
        };

        /**
         * 渲染模板(根据模板名)
         * @name    template.render
         * @param   {String}    模板名
         * @param   {Object}    数据
         * @return  {String}    渲染好的字符串
         */
        var renderFile = template.renderFile = function (filename, data) {
            var fn = template.get(filename) || showDebugInfo({
                filename: filename,
                name: 'Render Error',
                message: 'Template not found'
            });
            return data ? fn(data) : fn;
        };

        /**
         * 获取编译缓存（可由外部重写此方法）
         * @param   {String}    模板名
         * @param   {Function}  编译好的函数
         */
        template.get = function (filename) {
            var cache;
            if (cacheStore[filename]) {
                // 使用内存缓存
                cache = cacheStore[filename];
            } else if (typeof document === 'object') {
                // 加载模板并编译
                var elem = document.getElementById(filename);

                if (elem) {
                    var source = (elem.value || elem.innerHTML)
                        .replace(/^\s*|\s*$/g, '');
                    cache = compile(source, {
                        filename: filename
                    });
                }
            }
            return cache;
        };


        var toString = function (value, type) {
            if (typeof value !== 'string') {
                type = typeof value;
                if (type === 'number') {
                    value += '';
                } else if (type === 'function') {
                    value = toString(value.call(value));
                } else {
                    value = '';
                }
            }
            return value;
        };

        var escapeMap = {
            "<": "&#60;",
            ">": "&#62;",
            '"': "&#34;",
            "'": "&#39;",
            "&": "&#38;"
        };

        var escapeFn = function (s) {
            return escapeMap[s];
        };

        var escapeHTML = function (content) {
            return toString(content)
                .replace(/&(?![\w#]+;)|[<>"']/g, escapeFn);
        };

        var isArray = Array.isArray || function (obj) {
            return ({}).toString.call(obj) === '[object Array]';
        };

        var each = function (data, callback) {
            var i, len;
            if (isArray(data)) {
                for (i = 0, len = data.length; i < len; i++) {
                    callback.call(data, data[i], i, data);
                }
            } else {
                for (i in data) {
                    callback.call(data, data[i], i);
                }
            }
        };

        var utils = template.utils = {
            $helpers: {},
            $include: renderFile,
            $string: toString,
            $escape: escapeHTML,
            $each: each
        };

        /**
         * 添加模板辅助方法
         * @name    template.helper
         * @param   {String}    名称
         * @param   {Function}  方法
         */
        template.helper = function (name, helper) {
            helpers[name] = helper;
        };

        var helpers = template.helpers = utils.$helpers;

        /**
         * 模板错误事件（可由外部重写此方法）
         * @name    template.onerror
         * @event
         */
        template.onerror = function (e) {
            var message = 'Template Error\n\n';
            for (var name in e) {
                message += '<' + name + '>\n' + e[name] + '\n\n';
            }

            if (typeof console === 'object') {
                console.error(message);
            }
        };

        // 模板调试器
        var showDebugInfo = function (e) {
            template.onerror(e);
            return function () {
                return '{Template Error}';
            };
        };

        /**
         * 编译模板
         * 2012-6-6 @TooBug: define 方法名改为 compile，与 Node Express 保持一致
         * @name    template.compile
         * @param   {String}    模板字符串
         * @param   {Object}    编译选项
         *
         *      - openTag       {String}
         *      - closeTag      {String}
         *      - filename      {String}
         *      - escape        {Boolean}
         *      - compress      {Boolean}
         *      - debug         {Boolean}
         *      - cache         {Boolean}
         *      - parser        {Function}
         *
         * @return  {Function}  渲染方法
         */
        var compile = template.compile = function (source, options) {

            // 合并默认配置
            options = options || {};
            for (var name in defaults) {
                if (options[name] === undefined) {
                    options[name] = defaults[name];
                }
            }

            var filename = options.filename;

            try {
                var Render = compiler(source, options);
            } catch (e) {
                e.filename = filename || 'anonymous';
                e.name = 'Syntax Error';
                return showDebugInfo(e);
            }

            // 对编译结果进行一次包装
            function render(data) {
                try {
                    return new Render(data, filename) + '';
                } catch (e) {

                    // 运行时出错后自动开启调试模式重新编译
                    if (!options.debug) {
                        options.debug = true;
                        return compile(source, options)(data);
                    }
                    return showDebugInfo(e)();
                }
            }

            render.prototype = Render.prototype;
            render.toString = function () {
                return Render.toString();
            };

            if (filename && options.cache) {
                cacheStore[filename] = render;
            }

            return render;

        };

        // 数组迭代
        var forEach = utils.$each;

        // 静态分析模板变量
        var KEYWORDS =
            // 关键字
            'break,case,catch,continue,debugger,default,delete,do,else,false'
            + ',finally,for,function,if,in,instanceof,new,null,return,switch,this'
            + ',throw,true,try,typeof,var,void,while,with'

            // 保留字
            + ',abstract,boolean,byte,char,class,const,double,enum,export,extends'
            + ',final,float,goto,implements,import,int,interface,long,native'
            + ',package,private,protected,public,short,static,super,synchronized'
            + ',throws,transient,volatile'

            // ECMA 5 - use strict
            + ',arguments,let,yield'

            + ',undefined';

        var REMOVE_RE = /\/\*[\w\W]*?\*\/|\/\/[^\n]*\n|\/\/[^\n]*$|"(?:[^"\\]|\\[\w\W])*"|'(?:[^'\\]|\\[\w\W])*'|\s*\.\s*[$\w\.]+/g;
        var SPLIT_RE = /[^\w$]+/g;
        var KEYWORDS_RE = new RegExp(["\\b" + KEYWORDS.replace(/,/g, '\\b|\\b') + "\\b"].join('|'), 'g');
        var NUMBER_RE = /^\d[^,]*|,\d[^,]*/g;
        var BOUNDARY_RE = /^,+|,+$/g;
        var SPLIT2_RE = /^$|,+/;

        // 获取变量
        function getVariable(code) {
            return code
                .replace(REMOVE_RE, '')
                .replace(SPLIT_RE, ',')
                .replace(KEYWORDS_RE, '')
                .replace(NUMBER_RE, '')
                .replace(BOUNDARY_RE, '')
                .split(SPLIT2_RE);
        };

        // 字符串转义
        function stringify(code) {
            return "'" + code
            // 单引号与反斜杠转义
                .replace(/('|\\)/g, '\\$1')
                // 换行符转义(windows + linux)
                .replace(/\r/g, '\\r')
                .replace(/\n/g, '\\n') + "'";
        }

        function compiler(source, options) {

            var debug = options.debug;
            var openTag = options.openTag;
            var closeTag = options.closeTag;
            var parser = options.parser;
            var compress = options.compress;
            var escape = options.escape;

            var line = 1;
            var uniq = {$data: 1, $filename: 1, $utils: 1, $helpers: 1, $out: 1, $line: 1};

            var isNewEngine = ''.trim;// '__proto__' in {}
            var replaces = isNewEngine
                ? ["$out='';", "$out+=", ";", "$out"]
                : ["$out=[];", "$out.push(", ");", "$out.join('')"];

            var concat = isNewEngine
                ? "$out+=text;return $out;"
                : "$out.push(text);";

            var print = "function(){"
                + "var text=''.concat.apply('',arguments);"
                + concat
                + "}";

            var include = "function(filename,data){"
                + "data=data||$data;"
                + "var text=$utils.$include(filename,data,$filename);"
                + concat
                + "}";

            var headerCode = "'use strict';"
                + "var $utils=this,$helpers=$utils.$helpers,"
                + (debug ? "$line=0," : "");

            var mainCode = replaces[0];

            var footerCode = "return new String(" + replaces[3] + ");"

            // html与逻辑语法分离
            forEach(source.split(openTag), function (code) {
                code = code.split(closeTag);

                var $0 = code[0];
                var $1 = code[1];

                // code: [html]
                if (code.length === 1) {
                    mainCode += html($0);
                    // code: [logic, html]
                } else {
                    mainCode += logic($0);
                    if ($1) {
                        mainCode += html($1);
                    }
                }
            });

            var code = headerCode + mainCode + footerCode;

            // 调试语句
            if (debug) {
                code = "try{" + code + "}catch(e){"
                    + "throw {"
                    + "filename:$filename,"
                    + "name:'Render Error',"
                    + "message:e.message,"
                    + "line:$line,"
                    + "source:" + stringify(source)
                    + ".split(/\\n/)[$line-1].replace(/^\\s+/,'')"
                    + "};"
                    + "}";
            }

            try {
                var Render = new Function("$data", "$filename", code);
                Render.prototype = utils;
                return Render;
            } catch (e) {
                e.temp = "function anonymous($data,$filename) {" + code + "}";
                throw e;
            }

            // 处理 HTML 语句
            function html(code) {
                // 记录行号
                line += code.split(/\n/).length - 1;
                // 压缩多余空白与注释
                if (compress) {
                    code = code
                        .replace(/\s+/g, ' ')
                        .replace(/<!--[\w\W]*?-->/g, '');
                }
                if (code) {
                    code = replaces[1] + stringify(code) + replaces[2] + "\n";
                }
                return code;
            }

            // 处理逻辑语句
            function logic(code) {
                var thisLine = line;
                if (parser) {
                    // 语法转换插件钩子
                    code = parser(code, options);
                } else if (debug) {
                    // 记录行号
                    code = code.replace(/\n/g, function () {
                        line++;
                        return "$line=" + line + ";";
                    });
                }

                // 输出语句. 编码: <%=value%> 不编码:<%=#value%>
                // <%=#value%> 等同 v2.0.3 之前的 <%==value%>
                if (code.indexOf('=') === 0) {
                    var escapeSyntax = escape && !/^=[=#]/.test(code);
                    code = code.replace(/^=[=#]?|[\s;]*$/g, '');
                    // 对内容编码
                    if (escapeSyntax) {
                        var name = code.replace(/\s*\([^\)]+\)/, '');
                        // 排除 utils.* | include | print
                        if (!utils[name] && !/^(include|print)$/.test(name)) {
                            code = "$escape(" + code + ")";
                        }
                        // 不编码
                    } else {
                        code = "$string(" + code + ")";
                    }
                    code = replaces[1] + code + replaces[2];
                }

                if (debug) {
                    code = "$line=" + thisLine + ";" + code;
                }

                // 提取模板中的变量名
                forEach(getVariable(code), function (name) {
                    // name 值可能为空，在安卓低版本浏览器下
                    if (!name || uniq[name]) {
                        return;
                    }
                    var value;
                    // 声明模板变量
                    // 赋值优先级:
                    // [include, print] > utils > helpers > data
                    if (name === 'print') {
                        value = print;
                    } else if (name === 'include') {
                        value = include;
                    } else if (utils[name]) {
                        value = "$utils." + name;
                    } else if (helpers[name]) {
                        value = "$helpers." + name;
                    } else {
                        value = "$data." + name;
                    }

                    headerCode += name + "=" + value + ",";
                    uniq[name] = true;

                });

                return code + "\n";
            }

        };

        global.template = template;

    })(_win);

    //类型检测
    var type = function (obj) {//取得目标的类型
        if (obj == null) {
            return String(obj)
        }
        // 早期的webkit内核浏览器实现了已废弃的ecma262v4标准，可以将正则字面量当作函数使用，因此typeof在判定正则时会返回function
        return typeof obj === "object" || typeof obj === "function" ?
            _class2type[_toString.call(obj)] || "object" :
            typeof obj
    }

    //框架基类
    var base = new function () {

        var _base = function () {
            return this;
        }

        _base.prototype = {
            doTimeout: function () {
                function p_doTimeout(data_key) {
                    var that = this,
                        elem, data = {},
                        method_base = data_key ? $.fn : $,
                        args = arguments,
                        slice_args = 4,
                        id = args[1],
                        delay = args[2],
                        callback = args[3];
                    if (typeof id !== 'string') {
                        slice_args--;
                        id = data_key = 0;
                        delay = args[1];
                        callback = args[2]
                    }
                    if (data_key) {
                        elem = that.eq(0);
                        elem.data(data_key, data = elem.data(data_key) || {})
                    } else if (id) {
                        data = _cache[id] || (_cache[id] = {})
                    }
                    data.id && clearTimeout(data.id);
                    delete data.id;

                    function cleanup() {
                        if (data_key) {
                            elem.removeData(data_key)
                        } else if (id) {
                            delete _cache[id]
                        }
                    };

                    function actually_setTimeout() {
                        data.id = setTimeout(function () {
                            data.fn()
                        }, delay)
                    };
                    if (callback) {
                        data.fn = function (no_polling_loop) {
                            if (typeof callback === 'string') {
                                callback = method_base[callback]
                            }
                            callback.apply(that, _aslice.call(args, slice_args)) === true && !no_polling_loop ? actually_setTimeout() : cleanup()
                        };
                        actually_setTimeout()
                    } else if (data.fn) {
                        delay === undefined ? cleanup() : data.fn(delay === false);
                        return true
                    } else {
                        cleanup()
                    }
                };
                return p_doTimeout.apply(_win, [0].concat(_aslice.call(arguments)));
            },
            //元素居中
            center: function (speed) {
                var
                    _this = this.el,
                    _wh = $(_win).height(),
                    _ww = $(_win).width(),
                    _tw = _this.outerWidth(true),
                    _th = _this.outerHeight(true),
                    _fixed = _this.css("position") === "fixed",
                    _st = _fixed ? 0 : $(document).scrollTop(),
                    _sl = _fixed ? 0 : $(document).scrollLeft(),
                    _top = (_wh - _th) * 382 / 1000 + _st,
                    _left = (_ww - _tw) / 2 + _sl;

                /*_this.stop(false, true).animate({
                 top: Math.max(parseInt(_top), _st) + 'px',
                 left: Math.max(parseInt(_left), _sl) + 'px'
                 }, !TT.isEmpty(speed) ? speed : 200);*/

                _this.css({
                    top: Math.max(parseInt(_top), _st) + 'px',
                    left: Math.max(parseInt(_left), _sl) + 'px'
                });

                return _this;
            },
            clientXY: function (e) {
                var _left, _top, _st, _sl;

                _left = $(_win).width() - e.clientX < this.outerWidth() ? e.clientX - this.outerWidth() : e.clientX,
                    _top = $(_win).height() - e.clientY < this.outerHeight() ? e.clientY - this.outerHeight() : e.clientY,
                    _st = $(document).scrollTop(),
                    _sl = $(document).scrollLeft();

                this.css({
                    left: _left + _sl,
                    top: _top + _st
                }).show();

                return this;
            }
        }

        return _base;
    };

    //浏览器检测
    var browser = new function (userAgent, language) {
        var version, webkitVersion, isEdge, iOSAgent, iOSDevice, iOSMajorVersion, iOSMinorVersion, browser = {};
        userAgent = (userAgent || navigator.userAgent).toLowerCase();
        language = language || navigator.language || navigator.browserLanguage;
        isEdge = /\sedge\//.test(userAgent);
        if (isEdge) {
            version = (userAgent.match(/(?:edge\/)([\d\.]*)/) || [])[1];
        } else {
            version = (userAgent.match(/.*(?:rv|chrome|webkit|opera|ie)[\/: ](.+?)([ \);]|$)/) || [])[1];
        }
        browser.version = version;
        webkitVersion = (userAgent.match(/webkit\/(.+?) /) || [])[1];
        iOSAgent = userAgent.split(/\s*[;)(]\s*/) || [];
        iOSDevice = iOSAgent[1];
        iOSDeviceVersion = iOSAgent[2].match(/(\w{1,})_(\w{1,})/i);
        iOSMajorVersion = iOSDeviceVersion ? iOSDeviceVersion[1] : null;
        iOSMinorVersion = iOSDeviceVersion ? iOSDeviceVersion[2] : null;
        browser.windows = browser.isWindows = !!/windows/.test(userAgent);
        browser.mac = browser.isMac = !!/macintosh/.test(userAgent) || (/mac os x/.test(userAgent) && !/like mac os x/.test(userAgent));
        browser.lion = browser.isLion = !!(/mac os x 10[_\.][7-9]/.test(userAgent) && !/like mac os x 10[_\.][7-9]/.test(userAgent));
        browser.iPhone = browser.isiPhone = (iOSDevice === "iphone");
        browser.iPod = browser.isiPod = (iOSDevice.indexOf("ipod") > -1);
        browser.iPad = browser.isiPad = (iOSDevice === "ipad");
        browser.iOS = browser.isiOS = browser.iPhone || browser.iPod || browser.iPad;
        browser.iOSMajorVersion = browser.iOS ? iOSMajorVersion * 1 : undefined;
        browser.iOSMinorVersion = browser.iOS ? iOSMinorVersion * 1 : undefined;
        browser.android = browser.isAndroid = !!/android/.test(userAgent);
        browser.silk = browser.isSilk = !!/silk/.test(userAgent);
        browser.opera = /opera/.test(userAgent) ? version : 0;
        browser.isOpera = !!browser.opera;
        browser.msie = /msie \d+\.\d+|trident\/\d+\.\d.*; rv:\d+\.\d+[;\)]/.test(userAgent) && !browser.opera ? version : 0;
        browser.isIE = !!browser.msie;
        browser.isIE6 = !!(browser.msie && parseInt(browser.msie, 10) === 6);
        browser.isIE7OrLower = !!(browser.msie && parseInt(browser.msie, 10) <= 7);
        browser.isIE8OrLower = !!(browser.msie && parseInt(browser.msie, 10) <= 8);
        browser.isIE9OrLower = !!(browser.msie && parseInt(browser.msie, 10) <= 9);
        browser.isIE10OrLower = !!(browser.msie && parseInt(browser.msie, 10) <= 10);
        browser.isIE10 = !!(browser.msie && parseInt(browser.msie, 10) === 10);
        browser.isIE11 = !!(browser.msie && parseInt(browser.msie, 10) === 11);
        browser.edge = isEdge ? version : 0;
        browser.isEdge = isEdge;
        browser.mozilla = !isEdge && /mozilla/.test(userAgent) && !/(compatible|webkit|msie|trident)/.test(userAgent) ? version : 0;
        browser.isMozilla = !!browser.mozilla;
        browser.webkit = (!isEdge && /webkit/.test(userAgent)) ? webkitVersion : 0;
        browser.isWebkit = !!browser.webkit;
        browser.chrome = !isEdge && /chrome/.test(userAgent) ? version : 0;
        browser.isChrome = !!browser.chrome;
        browser.mobileSafari = /apple.*mobile/.test(userAgent) && browser.iOS ? webkitVersion : 0;
        browser.isMobileSafari = !!browser.mobileSafari;
        browser.iPadSafari = browser.iPad && browser.isMobileSafari ? webkitVersion : 0;
        browser.isiPadSafari = !!browser.iPadSafari;
        browser.iPhoneSafari = browser.iPhone && browser.isMobileSafari ? webkitVersion : 0;
        browser.isiPhoneSafari = !!browser.iphoneSafari;
        browser.iPodSafari = browser.iPod && browser.isMobileSafari ? webkitVersion : 0;
        browser.isiPodSafari = !!browser.iPodSafari;
        browser.isiOSHomeScreen = browser.isMobileSafari && !/apple.*mobile.*safari/.test(userAgent);
        browser.safari = browser.webkit && !browser.chrome && !browser.iOS && !browser.android ? webkitVersion : 0;
        browser.isSafari = !!browser.safari;
        browser.language = language.split("-", 1)[0];
        browser.current = browser.edge ? "edge" : browser.msie ? "msie" : browser.mozilla ? "mozilla" : browser.chrome ? "chrome" : browser.safari ? "safari" : browser.opera ? "opera" : browser.mobileSafari ? "mobile-safari" : browser.android ? "android" : "unknown";
        return browser
    };

    //JS缓存类
    var cache = new function () {

        function LRU(maxLength) {
            this.size = 0;
            this.limit = maxLength;
            this.head = this.tail = void 0;
            this._keymap = {};
            return this;
        }

        LRU.prototype = {
            put: function (key, value) {
                var entry = {
                    key: key,
                    value: value
                };
                this._keymap[key] = entry;
                if (this.tail) {
                    this.tail.newer = entry;
                    entry.older = this.tail
                } else {
                    this.head = entry
                }
                this.tail = entry;
                if (this.size === this.limit) {
                    this.shift()
                } else {
                    this.size++
                }
                return value
            },
            shift: function () {
                var entry = this.head;
                if (entry) {
                    this.head = this.head.newer;
                    this.head.older =
                        entry.newer =
                            entry.older =
                                this._keymap[entry.key] = void 0;
                }
            },
            get: function (key) {
                var entry = this._keymap[key];
                if (entry === void 0) {
                    return;
                }
                if (entry === this.tail) {
                    return entry.value
                }
                if (entry.newer) {
                    if (entry === this.head) {
                        this.head = entry.newer
                    }
                    entry.newer.older = entry.older;
                }
                if (entry.older) {
                    entry.older.newer = entry.newer
                }
                entry.newer = void 0;
                entry.older = this.tail;
                if (this.tail) {
                    this.tail.newer = entry
                }
                this.tail = entry;
                return entry.value
            },
            remove: function (key) {

                var entry = this._keymap[key];
                if (!entry) return;

                delete this._keymap[entry.key];

                if (entry.newer && entry.older) {
                    entry.older.newer = entry.newer;
                    entry.newer.older = entry.older;
                } else if (entry.newer) {
                    entry.newer.older = undefined;
                    this.head = entry.newer;
                } else if (entry.older) {
                    entry.older.newer = undefined;
                    this.tail = entry.older;
                } else {
                    this.head = this.tail = undefined;
                }

                this.size--;
                return entry.value;
            },
            removeAll: function () {
                this.head = this.tail = undefined;
                this.size = 0;
                this._keymap = {};
            }
        };

        return LRU
    };

    //TIPS 提示框
    var tips = new function () {

        var _tips = function () {
            return this.init.apply(this, arguments);
        };

        var prototype = {

            setOptions: function (options) {
                this.options = {
                    icons: "success",
                    times: 2,
                    autoClose: true,
                    callback: function () {
                    }
                };

                if (options) {
                    for (var property in options) {
                        this.options[property] = options[property];
                    }
                }
            },
            init: function (msg, options) {

                if (TT.isFunction(options)) {
                    options.callback = options;
                }

                var
                    _this = this,
                    _dfd = $.Deferred();
                _this.setOptions(options);

                var tipsHtml = '' +
                    '<div id="tips_box">' +
                    '   <div class="tips_body">' +
                    '     <div class="tips_icons tips_' + this.options.icons + '"></div>' +
                    '     <span>' + msg + '</span>' +
                    '   </div>' +
                    '</div>';

                $("#tips_box").remove();
                _this.el = $(tipsHtml).appendTo("body");

                //改变窗口大小重新定位
                $(window).on('resize.tips', function (e) {
                    _this.doTimeout('tips_resize', 250, function () {
                        _this.center();
                        //根据内容计算宽度
                        _this.el.width(_this.el.find(".tips_body").outerWidth() + 10);

                        setTimeout(function () {
                            _this.el.show();
                        }, 30)
                    })
                });

                _win.clearTimeout(_cache._timeout);
                if (_this.options.autoClose) {

                    _cache._timeout = setTimeout(function () {
                        if ($.isFunction(_this.options.callback)) {
                            _this.options.callback.call(_this);
                        }
                        _this.el.remove();
                        // _dfd.resolve(_this, _this.options);
                    }, parseInt(_this.options.times) * 1000);

                } else {
                    // _dfd.resolve(_this, _this.options);
                }

                setTimeout(function () {
                    $(window).trigger('resize.tips');
                }, 20);

                return _this;
                // return _dfd.promise();
            },
            hide: function () {
                _win.clearTimeout(_cache._timeout);
                this.el.remove();
            }
        };

        _tips.prototype = $.extend(prototype, new base());

        return _tips;
    };

    //URL操作相关
    var url = new function () {

        var _url = function () {
        };

        _url.prototype = {
            queryString: function (name, notDecoded) {
                name = (name + "").replace(/[\[]/, "\\\[").replace(/[\]]/, "\\\]");

                var regex = new RegExp("[\\?&]" + name + "=([^&#]*)");
                var results = regex.exec(location.search);
                var encoded = null;

                if (results === null) {
                    return "";
                } else {
                    encoded = results[1].replace(/\+/g, " ");
                    if (notDecoded) {
                        return encoded;
                    }
                    return decodeURIComponent(encoded);
                }
            },
            parseQuery: function (search) {
                var query = {};

                if (typeof search !== "string") {
                    search = window.location.search;
                }

                search = search.replace(/^\?/g, "");

                if (!search) {
                    return {};
                }

                var a = search.split("&");
                var iequ;
                for (var i = 0, l = a.length; i < l; ++i) {
                    iequ = a[i].indexOf("=");
                    if (iequ < 0) iequ = a[i].length;
                    query[decodeURIComponent(a[i].slice(0, iequ))] = decodeURIComponent(a[i].slice(iequ + 1));
                }

                return query;
            },
            stringify: function (queryObj) {

                if (!queryObj || queryObj.constructor !== Object) {
                    throw new Error("Query object should be an object.");
                }

                var stringified = "";
                for (var c in queryObj) {
                    stringified += c + "=" + encodeURIComponent(queryObj[c]) + "&";
                }
                /*Object.keys(queryObj).forEach(function (c) {
                 stringified += c + "=" + encodeURIComponent(queryObj[c]) + "&";
                 });*/

                stringified = stringified.replace(/\&$/g, "");
                return stringified;
            },
            updateSearchParam: function (param, value) {

                var searchParsed = this.parseQuery();

                // Delete the parameter
                if (value === undefined) {
                    delete searchParsed[param];
                } else {
                    // Update or add
                    value = encodeURIComponent(value);
                    if (searchParsed[param] === value) {
                        return {};
                    }
                    searchParsed[param] = value;
                }

                var newSearch = "?" + stringify(searchParsed);
                window.history.replaceState(null, "", newSearch + location.hash);

                return {};
            },
            get: function (key, url) {
                var params = this.parseQuery(url)[key];
                return !TT.isEmpty(params) ? params : "";
            }
        }

        return _url;
    };

    //打开窗口
    var openWin = new function () {

        var _openWin = function () {
            return this.open.apply(this, arguments)
        };

        _openWin.prototype = {
            setOptions: function (options) {
                this.options = {
                    id: "win",
                    winType: 1,
                    name: _win,
                    title: "",
                    width: 400,
                    height: 350,
                    resizable: "yes",
                    scroll: "yes",
                    status: "no",
                    menubar: "no",
                    toolbar: "no",
                    location: "yes",
                    menu: "no",
                    lock: true,
                    min: false,
                    max: false,
                    fixed: true,
                    data: null,
                    close: ""
                };

                if (options) {
                    for (var property in options) {
                        this.options[property] = options[property];
                    }
                }
            },
            open: function (url, width, height, options) {

                var _this = this;

                if (TT.isObject(url)) {
                    options = url;
                    url = "";
                    width = "";
                    height = "";
                }

                if (TT.isObject(width)) {
                    options = width;
                    width = "";
                }

                if (TT.isObject(height)) {
                    options = height;
                    height = "";
                }

                //如果URL不等于空，加上标识
                if (url != "") {
                    url = url + (/\?/.test(url) ? "&" : "?") + "isOpen=true";
                }

                _this.setOptions(options);
                _this.url = _this.options.content ? "" : url;
                _this.name = _this.options.name;
                _this.winType = _this.options.winType;
                _this.options.width = width || _this.options.width;
                _this.options.height = height || _this.options.height;
                _this.lock = _this.options.lock;
                _this.close = _this.options.close;

                if (_this.winType == 2 || _this.winType == 3) {

                    var availHeight = _win.screen.availHeight,
                        availWidth = _win.screen.availWidth,
                        winWidth = (_this.options.width + "").replace(/px/gi, ""),
                        winHeight = (_this.options.height + "").replace(/px/gi, "");

                    !_this.options.top && (_this.options.top = (availHeight - winHeight) / 2);
                    !_this.options.left && (_this.options.left = (availWidth - winWidth) / 2);

                }

                switch (_this.winType) {
                    case 1:
                        return _this.dialog(_this.url, _this.options);
                        break;
                    case 2:
                        return _this.winOpen(_this.url, _this.options);
                        break;
                    case 3:
                        return _this.modalDialog(_this.url, _this.options);
                        break;
                }
            },
            dialog: function (url, options) {

                var options = $.extend(true, {
                    url: url,
                    upload_settings: ""
                }, options);

                //初始化弹窗
                var d = dialog(options);

                //写入上传参数
                d.data("upload_settings", options.upload_settings);

                //关闭后全部删除
                d.addEventListener('close', function () {
                    d.remove();
                });

                return d;
            },
            winOpen: function (url, options) {

                var params = 'resizable=' + options.resizable +
                    ',scrollbars=' + options.scroll +
                    ',status=' + options.status +
                    ',toolbar=' + options.toolbar +
                    ',location=' + options.location +
                    ',menu=' + options.menu +
                    ',left=' + options.left +
                    ',top=' + options.top +
                    ',width=' + options.width +
                    ',height=' + options.height;

                var _window = _win.open(url, options.name, params);

                if (TT.isFunction(this.close)) {
                    this.close.call(this, _win);
                }

                return _window;
            },
            modalDialog: function (url, options) {

                var params = "scroll:" + options.scroll +
                    ";resizable:" + options.resizable +
                    ";help:" + options.help +
                    ";status:" + options.status +
                    ";center:" + options.center +
                    ";dialogHeight:" + options.height +
                    ";dialogWidth:" + options.width +
                    ";dialogTop:" + options.top +
                    ";dialogLeft:" + options.left;

                var vArguments = {
                    opener: _win,
                    data: options.data,
                    winType: 3
                };

                var _window;
                if (browser.isIE) {
                    if (this.lock) {
                        _window = showModalDialog(url, vArguments, params)
                    } else {
                        _window = showModelessDialog(url, vArguments, params)
                    }
                } else {
                    _window = showModalDialog(url, vArguments, params)
                }
                if (TT.isFunction(this.close)) {
                    this.close.call(this, _window);
                }
                return _window;
            }
        };

        return _openWin;
    };

    //拖动的示例
    var dragDrop = new function () {

        var _drag = function () {
            this.init.apply(this, arguments);
        };

        _drag.prototype = {
            setOptions: function (options) {
                this.options = {
                    limit: true,
                    handle: null,
                    container: "",
                    maxTop: 0,
                    maxLeft: 0,
                    maxRight: 9999,
                    maxBottom: 9999,
                    maxContainer: null,
                    lock: false,
                    lockX: false,
                    lockY: false,
                    onStart: null,
                    onMove: null,
                    onStop: null,
                    isMove: false,
                    getGhost: null,
                    getHolder: null,
                    selectArea: null
                }

                if (options) {
                    for (var property in options) {
                        this.options[property] = options[property];
                    }
                }
            },
            init: function (drag, options) {
                var
                    _this = this;
                _this.$drag = $(drag);
                _this.drag = _this.$drag[0];

                _this.clientX = 0;
                _this.clientY = 0;

                _this.mousemove = function (e) {
                    _this.move(e);
                };

                _this.mouseup = function (e) {
                    _this.stop(e);
                };

                _this.setOptions(options);

                _this.$drag = !_this.options.$drag && _this.$drag;

                _this.limit = _this.options.limit;
                _this.maxTop = _this.options.maxTop;
                _this.maxLeft = _this.options.maxLeft;
                _this.maxRight = _this.options.maxRight;
                _this.maxBottom = _this.options.maxBottom;
                _this.$maxContainer = _this.options.maxContainer && $(_this.options.maxContainer);

                _this.lock = _this.options.lock;
                _this.lockX = _this.options.lockX;
                _this.lockY = _this.options.lockY;

                _this.onStart = _this.options.onStart;
                _this.onMove = _this.options.onMove;
                _this.onStop = _this.options.onStop;
                _this.getGhost = _this.options.getGhost;
                _this.getHolder = _this.options.getHolder;
                _this.selectArea = _this.options.selectArea;

                _this.$handle = (_this.options.handle && $(_this.options.handle)) || _this.$drag;
                _this.handle = _this.$handle[0];

                if (!_this.$handle) {
                    return false;
                }

                _this._repair();
                _this.$handle.on("mousedown.drag", function (e) {
                    //阻止默认动作
                    e.preventDefault();
                    e.stopPropagation();
                    _this.start(e);
                })

            },
            start: function (e) {
                var _this, _e, _offset;

                //this 局部变量
                _this = this;

                //修复兼容性
                _e = $.event.fix(e);

                _this._repair();

                _offset = _this.$handle.offset();

                _this.offsetLeft = _offset.left;
                _this.offsetTop = _offset.top;

                _this.clientX = _e.clientX - _this.offsetLeft;
                _this.clientY = _e.clientY - _this.offsetTop;

                $(_doc)
                    .on("mousemove.drag", _this.mousemove)
                    .on("mouseup.drag", _this.mouseup);

                if (browser.isIE) {
                    _this.$handle.on("losecapture.drag", _this.mouseup);
                    _this.handle.setCapture();
                } else {
                    $.event.add(_win, "blur", _this.mouseup);
                    _e.preventDefault();
                }

                _this.onStart && _this.onStart.call(this, _e);
            },
            move: function (e) {

                var _this, _e;

                //this 局部变量
                _this = this;

                //清除选择
                _win.getSelection ? _win.getSelection().removeAllRanges() : _doc.selection.empty();

                //锁定状态禁止拖动
                if (_this.lock) {
                    _this.stop(e);
                    return false;
                }

                //修复兼容性
                _e = $.event.fix(e);

                _this.top = _e.clientY - _this.clientY;
                _this.left = _e.clientX - _this.clientX;

                //限定拖动范围
                if (_this.limit) {

                    //设置范围参数
                    var maxTop, maxLeft, maxRight, maxBottom;

                    maxLeft = _this.maxLeft;
                    maxRight = _this.maxRight;
                    maxTop = _this.maxTop;
                    maxBottom = _this.maxBottom;

                    //修正范围参数
                    if (_this.$maxContainer) {
                        maxTop = Math.max(maxTop, 0);
                        maxLeft = Math.max(maxLeft, 0);
                        maxRight = Math.min(maxRight, _this.$maxContainer.width());
                        maxBottom = Math.min(maxBottom, _this.$maxContainer.height());
                    }

                    //修正移动参数
                    _this.left = Math.max(Math.min(_this.left, maxRight - _this.$drag.outerWidth(true)), maxLeft);
                    _this.top = Math.max(Math.min(_this.top, maxBottom - _this.$drag.outerHeight(true)), maxTop);

                }

                var scrollTop = $(_doc).scrollTop();

                _this.$drag.css({
                    left: function (index, value) {
                        if (!_this.lockX) {
                            return _this.left
                        }
                    },
                    top: function (index, value) {
                        if (!_this.lockY) {
                            return scrollTop + _this.top;
                        }
                    }
                });

                _this.onMove && _this.onMove.call(this, _e);
            },
            stop: function (e) {

                var _this, _e;

                //this 局部变量
                _this = this;

                //修复兼容性
                _e = $.event.fix(e);

                $(_doc).off(".drag");

                if (browser.isIE) {
                    _this.$handle.off("losecapture.drag", _this.mouseup);
                    _this.handle.releaseCapture();
                } else {
                    $.event.remove(_win, "blur", _this.mouseup);
                }

                _this.onStop && _this.onStop.call(this, _e);
            },
            _repair: function (e) {
                var _this = this;
                var _position = !_this.$maxContainer || _this.$maxContainer.css("position");
                if (_this.limit) {
                    //修正错误范围参数
                    _this.maxRight = Math.max(_this.maxRight, _this.maxLeft + _this.$drag.outerWidth(true));
                    _this.maxBottom = Math.max(_this.maxBottom, _this.maxTop + _this.$drag.outerHeight(true));
                    //如果有容器必须设置position为relative或absolute来相对或绝对定位，并在获取offset之前设置
                    if (_position == "relative" || _position == "absolute") {
                        _this.$maxContainer.css("position", "relative");
                    }
                }
            }
        }

        return _drag;
    };

    //分页方法
    var pagination = new function () {
        var _pagination = function () {
            return this.init.apply(this, arguments);
        };
        _pagination.prototype = {
            setOptions: function (options) {
                this.options = {
                    total: 0,
                    currPage: 1,
                    pageCount: 10,
                    pageNumber: 5,
                    callback: $.noop,
                    isTotal: false,
                    isJump: true,
                    isNum: true,
                    isNext: true,
                    isPrev: true,
                    isFirst: true,
                    isLast: true,
                    isGoBtn: false,
                    tpl: {
                        first: "\u9996\u9875",
                        last: "\u5c3e\u9875",
                        next: "\u4e0b\u4e00\u9875",
                        prev: "\u4e0a\u4e00\u9875",
                        links: "javascript:void(0)",
                        inputInfo: '跳{input}{gobtn}/{sumPage}页',
                        goBtnText: "GO",
                        tips: '回车分页',
                        numText: '共 {total} 条'
                    }
                };
                this.options = $.extend(true, this.options, options);
            },
            init: function (el, options) {
                var _this = this;
                _this.$el = $(el);
                _this.setOptions(options);
                _this.render().bindFn();
                return _this;
            },
            render: function () {
                var _this = this;
                _this.$el.html(_this.createPageBar());
                return _this;
            },
            bindFn: function () {
                var _this = this;

                TT.nextTick(function () {
                    _this.$el
                        .off('.pagination')
                        .on('click.pagination.', 'a', function (e) {
                            e.preventDefault();
                            var $this = $(this),
                                page = parseInt($this.attr("paged")),
                                page = page > 0 ? page : 1;
                            _this.setPage(page).options.callback.call(this, page);
                        })
                        .on('keydown.pagination', 'input', function (e) {
                            var page = $.trim($(this).val());
                            if (e.which == 13) {
                                var reg = /^\+?[0-9][0-9]*$/;
                                if (reg.test(page) && page > 0) {
                                    page = (page > _this.options.pageCount) ? _this.options.pageCount : page;
                                    _this.setPage(page).options.callback.call(this, page);
                                }
                            }
                        })
                        .on('click.pagination', 'button', function (e) {
                            var ipt = $(this).prev('input'),
                                page = $.trim(ipt.val()),
                                reg = /^\+?[0-9][0-9]*$/;
                            if (reg.test(page) && page > 0) {
                                var e = jQuery.Event("keydown");
                                e.keyCode = 13;
                                ipt.tigger(e);
                            }
                        });
                });
                return _this
            },
            setPage: function (page) {
                var _this = this;
                _this.currPage = _this.options.currPage = page;
                return _this;
            },
            reload: function (pageIndex, pageCount) {
                var _this = this;
                _this.options.pageCount = pageCount || 0;
                _this.options.currPage = pageIndex || 0;
                _this.render().bindFn();
                return _this;
            },
            getPage: function () {
                return this.currPage
            },
            createPageBar: function () {
                var _this = this,
                    opts = _this.options,
                    links = opts.tpl.links;
                _this.currPage = opts.currPage = opts.currPage > opts.pageCount ? opts.pageCount : opts.currPage;
                var b = opts.currPage, c = parseInt(opts.pageCount), d = parseInt(opts.pageNumber / 2),
                    e = opts.pageNumber, f = "";

                if (1 == b) {
                    opts.isFirst && (f = '<span class="disabled" paged="1">' + opts.tpl.first + ' </span>');
                    opts.isPrev && (f += '<span class="disabled" paged="' + opts.tpl.prev + '">' + opts.tpl.prev + ' </span>');
                } else {
                    opts.isFirst && (f = '<a href="' + links + '" paged="1">' + opts.tpl.first + '</a>');
                    opts.isPrev && (f += '<a href="' + links + '" paged="' + (b - 1) + '">' + opts.tpl.prev + ' </a>');
                }
                if (opts.isNum) {
                    var g = lastPage = 1;
                    for (g = b - d > 0 ? g = b - d : 1, g + e > c ? (lastPage = c + 1, g = lastPage - e) : lastPage = g + e, 0 >= g && (g = 1), g; lastPage > g; g++) {
                        f += g == b ? '<span class="current" paged="' + g + '">' + g + "</span>" : "<a href='" + links + "' paged='" + g + "'>" + g + "</a>";
                    }
                }
                if (b == c) {
                    opts.isNext && (f += '<span class="disabled" paged="' + opts.tpl.next + '">' + opts.tpl.next + ' </span>');
                    opts.isLast && (f += '<span class="disabled" paged="' + opts.tpl.last + '">' + opts.tpl.last + ' </span>');
                } else {
                    opts.isNext && (f += '<a href="' + links + '" paged="' + (b + 1) + '">' + opts.tpl.next + '</a>');
                    opts.isLast && (f += "<a href='" + links + "' paged='" + c + "'>" + opts.tpl.last + "</a>");
                }
                opts.isJump && (f += this.createPageInputBar());
                opts.isTotal && (f += (' <span class="total">' + opts.tpl.numText + '</span>').replace('{total}', opts.total));
                return (_this.pageBar = f);
            },
            createPageInputBar: function () {
                var opts = this.options;
                var input = '<input type="text" value="' + opts.currPage + '" >',
                    button = '<button>' + opts.tpl.goBtnText + '</button>';
                var inputBar = $("<span/>").attr({"title": opts.tpl.tips}).addClass("inputBar").html(function () {
                    var str = opts.tpl.inputInfo.replace("{input}", input)
                        .replace("{gobtn}", opts.isGoBtn ? button : "")
                        .replace("{sumPage}", opts.pageCount);
                    return str;
                });
                return inputBar.prop("outerHTML")
            }
        };
        return _pagination;
    };

    var TT = function (selector, context) {
        return new TT.fn.init(selector, context);
    };

    TT.fn = TT.prototype = {
        version: "3.0.2",
        init: function (selector, context) {
            return $(selector, context);
        },
        args: {}
    };

    TT.fn.init.prototype = TT.fn;

    TT.extend = TT.fn.extend = $.extend;

    TT.extend({
        log: log,
        browser: browser,
        type: function (s) {
            return type(s)
        },
        nowDate: function () {
            return new Date();
        },
        now: function () {
            return (new Date()).getTime();
        },
        error: function (msg) {
            throw new Error(msg);
        },
        randomNum: function (Min, Max) {
            var Range = Max - Min;
            var Rand = Math.random();
            return (Min + Math.round(Rand * Range));
        },
        UUID: function (prefix) {
            prefix = prefix || "UUID";
            return String(Math.random() + Math.random()).replace(/\d\.\d{4}/, prefix)
        },
        isWindow: function (obj) {
            return obj && obj.document && obj.location && obj.alert && obj.setInterval;
        },
        isObject: function (s) {
            return type(s) == 'object';
        },
        isFunction: function (s) {
            return type(s) == 'function';
        },
        isDate: function (s) {
            return type(s) == 'date';
        },
        isNumber: function (value) {
            return type(value) == 'number';
        },
        isString: function (s) {
            return type(s) == 'string';
        },
        isDefined: function (s) {
            return type(s) != 'undefined';
        },
        isUndefined: function (s) {
            return type(s) == 'undefined';
        },
        isArray: function (s) {
            return type(s) == 'array';
        },
        isBoolean: function (s) {
            return type(s) == 'boolean';
        },
        isEmpty: function (s, allowBlank) {
            return s === null || s === undefined || ((this.isArray(s) && !s.length)) || (!allowBlank ? s === '' : false);
        },
        isElement: function (s) {
            return s ? !!s.tagName : false;
        },
        isFlash: function () {
            var flag = false;
            if (window.ActiveXObject) {
                try {
                    var swf = new ActiveXObject("ShockwaveFlash.ShockwaveFlash");
                    if (swf) {
                        flag = true;
                    }
                } catch (e) {
                }
            } else {
                try {
                    var swf = navigator.plugins['Shockwave Flash'];
                    if (swf) {
                        flag = true;
                    }
                } catch (e) {
                }
            }
            return flag
        },
        trim: function (s) {
            if (!" ".trim) {
                var rtrim = /^[\s\uFEFF\xA0]+|[\s\uFEFF\xA0]+$/g;
                String.prototype.trim = function () {
                    return this.replace(rtrim, "")
                }
            }
            //return TT.isString(value) ? value.replace(/^\s*/, '').replace(/\s*$/, '') : value;
            return s.trim()
        },
        on: function (elem, type, eventHandle) {
            if (elem.addEventListener) {
                elem.addEventListener(type, eventHandle, false);

            } else if (elem.attachEvent) {
                elem.attachEvent("on" + type, eventHandle);
            }
        },
        stopPropagation: function (event) {
            if (event.stopPropagation) {
                event.stopPropagation();
            } else {
                event.cancelBubble = true;
            }
        }
    });

    TT.extend({
        toCurrency: function (i) {
            i = parseFloat(i, 10).toFixed(2);
            return (i == 'NaN') ? '0.00' : i;
        },
        valid: function () {
            var _this = {
                require: /.+/,
                email: /^[\w\+\-]+(\.[\w\+\-]+)*@[a-z\d\-]+(\.[a-z\d\-]+)*\.([a-z]{2,4})$/,
                phone: /^((\(\d{2,3}\))|(\d{3}\-))?(\(0\d{2,3}\)|0\d{2,3}-)?[1-9]\d{6,7}(\-\d{1,4})?$/,
                mobile: /^13[0-9]{9}|15[012356789][0-9]{8}|18[0256789][0-9]{8}|147[0-9]{8}$/,
                url: /^(https?|s?ftp):\/\/\S+$/i,
                cy: /^\d+(\.\d+)?$/,
                number: /^\d+$/,
                zip: /^[1-9]\d{5}$/,
                qq: /^[1-9]\d{4,12}$/,
                "int": /^[-\+]?\d+$/,
                dbl: /^[-\+]?\d+(\.\d+)?$/,
                en: /^[A-Za-z]+$/,
                zh: /^[\u0391-\uFFE5]+$/,
                username: /^[a-z]\w{3,}$/i,
                // cnSafe: /[0-9a-zA-Z_.,#@!$%^&*()-+=|\?/<>]/g,
                safe: /^(([A-Z]*|[a-z]*|\d*|[-_\~!@#\$%\^&\*\.\(\)\[\]\{\}<>\?\\\/\'\"]*)|.{0,5})$|\s/,
                IsSafe: function (s) {
                    return !this.safe.test(s);
                },
                IdCard: function (s) {
                    return this.IsIdCard(s);
                },
                date: function (s) {
                    return !this.IsDate(s);
                },
                custom: function (s) {
                    return this.exec(value);
                },
                IsIdCard: function (number) {
                    var date, Ai;
                    var verify = "10x98765432";
                    var Wi = [7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2];
                    var area = ['', '', '', '', '', '', '', '', '', '', '', '\u5317\u4EAC', '\u5929\u6D25', '\u6CB3\u5317', '\u5C71\u897F', '\u5185\u8499\u53E4', '', '', '', '', '', '\u8FBD\u5B81', '\u5409\u6797', '\u9ED1\u9F99\u6C5F', '', '', '', '', '', '', '', '\u4E0A\u6D77', '\u6C5F\u82CF', '\u6D59\u6C5F', '\u5B89\u5FAE', '\u798F\u5EFA', '\u6C5F\u897F', '\u5C71\u4E1C', '', '', '', '\u6CB3\u5357', '\u6E56\u5317', '\u6E56\u5357', '\u5E7F\u4E1C', '\u5E7F\u897F', '\u6D77\u5357', '', '', '', '\u91CD\u5E86', '\u56DB\u5DDD', '\u8D35\u5DDE', '\u4E91\u5357', '\u4E91\u5357', '', '', '', '', '', '', '\u9655\u897F', '\u9655\u897F', '\u9655\u897F', '\u5B81\u590F', '\u65B0\u7586', '', '', '', '', '', '\u65B0\u7586', '', '', '', '', '', '', '', '', '', '\u65B0\u7586', '\u6FB3\u95E8', '', '', '', '', '', '', '', '', '\u56FD\u5916'];
                    var re = number.match(/^(\d{2})\d{4}(((\d{2})(\d{2})(\d{2})(\d{3}))|((\d{4})(\d{2})(\d{2})(\d{3}[x\d])))$/i);

                    if (re == null) {
                        return false;
                    }

                    if (re[1] >= area.length || area[re[1]] == "") {
                        return false;
                    }

                    if (re[2].length == 12) {
                        Ai = number.substr(0, 17);
                        date = [re[9], re[10], re[11]].join("-");
                    } else {
                        Ai = number.substr(0, 6) + "19" + number.substr(6);
                        date = ["19" + re[4], re[5], re[6]].join("-");
                    }

                    if (!this.IsDate(date, "ymd")) {
                        return false;
                    }

                    var sum = 0;
                    for (var i = 0; i <= 16; i++) {
                        sum += Ai.charAt(i) * Wi[i];
                    }
                    Ai += verify.charAt(sum % 11);

                    return (number.length == 15 || number.length == 18 && number == Ai);
                },
                IsDate: function (op, formatString) {
                    formatString = formatString || "ymd";
                    var m, year, month, day;

                    function GetFullYear(y) {
                        return ((y < 30 ? "20" : "19") + y) | 0;
                    }

                    switch (formatString) {
                        case "ymd" :
                            m = op.match(new RegExp("^((\\d{4})|(\\d{2}))([-./])(\\d{1,2})\\4(\\d{1,2})$"));
                            if (m == null) return false;
                            day = m[6];
                            month = m[5] * 1;
                            year = (m[2].length == 4) ? m[2] : GetFullYear(parseInt(m[3], 10));
                            break;
                        case "dmy" :
                            m = op.match(new RegExp("^(\\d{1,2})([-./])(\\d{1,2})\\2((\\d{4})|(\\d{2}))$"));
                            if (m == null) return false;
                            day = m[1];
                            month = m[3] * 1;
                            year = (m[5].length == 4) ? m[5] : GetFullYear(parseInt(m[6], 10));
                            break;
                        default :
                            break;
                    }

                    if (!parseInt(month)) {
                        return false;
                    }

                    month = month == 0 ? 12 : month;
                    var date = new Date(year, month - 1, day);
                    return (typeof(date) == "object" && year == date.getFullYear() && month == (date.getMonth() + 1) && day == date.getDate());
                },
                valid: function (value, vtype) {
                    if (type(this[vtype]) == 'object' || this[vtype] == 'undefined') {
                        return false;
                    }
                    switch (vtype) {
                        case "IdCard" :
                        case "date" :
                        case "IsSafe":
                        case "custom" :
                            return !eval(this[vtype]);
                            break;
                        default :
                            return !this[vtype].test(value);
                            break;
                    }
                },
                exec: function (op, reg) {
                    return new RegExp(reg, "g").test(op);
                }
            };
            return _this.valid(v, d);
        }
    });

    TT.extend({
        cache: new function () {
            return new cache(100);
        },
        data: function (a, b) {
            if (!TT.isEmpty(b)) {
                _storage.setItem(a, b);
            } else {
                return _storage.getItem(a);
            }
        },
        removeData: function (a) {
            !TT.isEmpty(a) ? _storage.removeItem(a) : _storage.clear();
        },
        clearData: function () {
            _storage.clear();
        },
        dialog: new function () {
            return new cache(100);
        },
        cookies: function (name, value, options) {
            if (typeof value != 'undefined') {
                options = options || {};
                if (value === null) {
                    value = '';
                    options.expires = -1;
                }
                var expires = '';
                if (options.expires && (typeof options.expires == 'number' || options.expires.toUTCString)) {
                    var date;
                    if (typeof options.expires == 'number') {
                        date = new Date();
                        date.setTime(date.getTime() + (options.expires * 24 * 60 * 60 * 1000));
                    } else {
                        date = options.expires;
                    }
                    expires = '; expires=' + date.toUTCString();
                }
                var path = options.path ? '; path=' + (options.path) : '/';
                var domain = options.domain ? '; domain=' + (options.domain) : '';
                var secure = options.secure ? '; secure' : '';
                document.cookie = [name, '=', encodeURIComponent(value), expires, path, domain, secure].join('');
            } else {
                var cookieValue = null;
                if (document.cookie && document.cookie != '') {
                    var cookies = document.cookie.split(';');
                    for (var i = 0; i < cookies.length; i++) {
                        var cookie = TT.trim(cookies[i]);
                        if (cookie.substring(0, name.length + 1) == (name + '=')) {
                            cookieValue = decodeURIComponent(cookie.substring(name.length + 1));
                            break;
                        }
                    }
                }
                return cookieValue;
            }
        },
        preloadImages: function () {
            var d = document;
            if (d.images) {
                if (!d.MM_p) d.MM_p = new Array();
                var i, j = d.MM_p.length,
                    a = preloadImages.arguments;
                for (i = 0; i < a.length; i++) if (a[i].indexOf("#") != 0) {
                    d.MM_p[j] = new Image;
                    d.MM_p[j++].src = a[i];
                }
            }
        },
        loadJs: function (url, callback, options) {
            var key = this.getFileName(url);
            var ret = cacheJS.get(key);
            if (!ret) {
                options = options || {async: true, charset: "utf-8"};
                var head = _doc.getElementsByTagName('head')[0] || _doc.documentElement,
                    script = _doc.createElement('script');
                script.type = "text/javascript";
                script.src = url;
                if (options.charset) {
                    script.charset = options.charset;
                }
                script.onload = script.onreadystatechange = function () {
                    var rs = script.readyState;
                    if ('undefined' === typeof rs || 'loaded' === rs || 'complete' === rs) {
                        try {
                            callback && callback();
                        } finally {
                            script.onload = script.onreadystatechange = null;
                            script = null;
                        }
                    }
                };
                script.src = url;
                head.appendChild(script);
                cacheJS.put(key, url);
            } else {
                callback && callback();
            }
        },
        nextTick: (function () {
            return function (fn) {
                setTimeout(fn, 5)
            }
        })(),
        compile: function (id, data) {
            return template.compile(id, data);
        },
        template: function (id, data) {
            return template(id, data);
        },
        strLen: function (str) {
            if (TT.isEmpty(str)) {
                return;
            }
            var str_length = 0;
            var str_len = (str + "").length;

            for (var i = 0; i < str_len; i++) {
                a = str.charAt(i);
                str_length++;
                if (escape(a).length > 4) {
                    //中文字符的长度经编码之后大于4
                    str_length++;
                }
            }
            return str_length;
        },
        /**
         * 截取字符串
         * @cutstr
         * @param   {String}        字符串
         * @param   {Integer}       截取字符串长度
         */
        cutstr: function (str, len, fill) {
            if (TT.isEmpty(str)) {
                return;
            }
            var str_length = 0;
            var fill = fill ? fill : "...";
            var str_cut = new String();
            var str_len = (str + "").length;
            len = len * 2;

            for (var i = 0; i < str_len; i++) {
                a = str.charAt(i);
                str_length++;
                if (escape(a).length > 4) {
                    //中文字符的长度经编码之后大于4
                    str_length++;
                }
                str_cut = str_cut.concat(a);
                if (str_length >= len) {
                    str_cut = str_cut.concat(fill);
                    return str_cut;
                }
            }
            //如果给定字符串小于指定长度，则返回源字符串；
            if (str_length < len) {
                return str;
            }
        },
        /**
         * 对日期进行格式化
         * @dateFormat
         * @param date 要格式化的日期
         * @param format 进行格式化的模式字符串
         *     支持的模式字母有：
         *     y:年,
         *     M:年中的月份(1-12),
         *     d:月份中的天(1-31),
         *     h:小时(0-23),
         *     m:分(0-59),
         *     s:秒(0-59),
         *     S:毫秒(0-999),
         *     q:季度(1-4)
         * @return String
         */
        dateFormat: function (date, format) {
            if (TT.isEmpty(date) && !TT.isString(date)) {
                format = "";
            } else {
                //兼容firefox 必须将日期 - 换成 /
                if (TT.isString(date)) {
                    date = new Date(date.replace(/-/g, "/"));
                }

                var map = {
                    "M": date.getMonth() + 1, //月份
                    "d": date.getDate(), //日
                    "h": date.getHours(), //小时
                    "m": date.getMinutes(), //分
                    "s": date.getSeconds(), //秒
                    "q": Math.floor((date.getMonth() + 3) / 3), //季度
                    "S": date.getMilliseconds() //毫秒
                };
                format = format.replace(/([yMdhmsqS])+/g, function (all, t) {
                    var v = map[t];
                    if (v !== undefined) {
                        if (all.length > 1) {
                            v = '0' + v;
                            v = v.substr(v.length - 2);
                        }
                        return v;
                    } else if (t === 'y') {
                        return (date.getFullYear() + '').substr(4 - all.length);
                    }
                    return all;
                });
            }
            return format;
        },
        /**
         * 对日期进行格式化
         * @dateDiff
         * @param date 要格式化的日期
         * @return String
         */
        dateDiff: function (date) {
            var d_minutes, d_hours, d_days;
            var timeNow = parseInt(new Date().getTime() / 1000);
            var d;
            d = timeNow - new Date(Date.parse(date.replace(/-/g, "/"))).getTime() / 1000;
            d_days = parseInt(d / 86400);
            d_hours = parseInt(d / 3600);
            d_minutes = parseInt(d / 60);
            if (d < 60) {
                return d_minutes + "秒前";
            } else if (d_days > 0 && d_days < 4) {
                return d_days + "天前";
            } else if (d_days <= 0 && d_hours > 0) {
                return d_hours + "小时前";
            } else if (d_hours <= 0 && d_minutes > 0) {
                return d_minutes + "分钟前";
            } else {
                return TT.dateFormat(date, "MM月dd日");
            }
        },
        /**
         * 秒换成时分秒
         * @formatSeconds
         * @param value 要转换的秒数
         * @return String
         */
        formatSeconds: function (value) {
            var millisecond = parseInt(value);// 毫秒
            var seconds = 0;//秒
            var minute = 0;// 分
            var hour = 0;// 小时
            if (millisecond > 1000) {
                seconds = parseInt(millisecond / 1000);
                if (seconds > 60) {
                    minute = parseInt(seconds / 60);
                    seconds = parseInt(seconds % 60);
                    if (minute > 60) {
                        hour = parseInt(minute / 60);
                        minute = parseInt(minute % 60);
                    }
                }
            }
            var result = "" + parseInt(millisecond) + " 毫秒";
            if (seconds > 0) {
                result = "" + parseInt(seconds) + " 秒";
            }
            if (minute > 0) {
                result = "" + parseInt(minute) + " 分" + result;
            }
            if (hour > 0) {
                result = "" + parseInt(hour) + " 小时" + result;
            }
            return result;
        },

        /**
         * JSON 内容转义为安全字符串
         * @str2Json
         * @return String
         */
        str2Json: function (s) {
            var newstr = "";
            for (var i = 0; i < s.length; i++) {
                c = s.charAt(i);
                switch (c) {
                    case '\"':
                        newstr += "\\\"";
                        break;
                    case '\\':
                        newstr += "\\\\";
                        break;
                    case '/':
                        newstr += "\\/";
                        break;
                    case '\b':
                        newstr += "\\b";
                        break;
                    case '\f':
                        newstr += "\\f";
                        break;
                    case '\n':
                        newstr += "\\n";
                        break;
                    case '\r':
                        newstr += "\\r";
                        break;
                    case '\t':
                        newstr += "\\t";
                        break;
                    default:
                        newstr += c;
                }
            }
            return newstr;
        },
        textAreaHtml: function (s) {
            if (s) {
                return s.replaceAll('\\n', '<br/>').replaceAll(' ', '&nbsp')
            }
            return ""
        },
        /**
         * asyncQueue ajax队列
         * @asyncQueue
         * @return String
         */
        asyncQueue: function () {
            var that = this,
                queue = [],
                completeFunc,
                failureFunc,
                paused = false,
                lastCallbackData,
                _run,
                _complete,
                inQueue = 0,
                defaultTimeOut = 10;

            _run = function () {
                var f = queue.shift();

                if (f) {
                    inQueue++;
                    setTimeout(function () {
                        f.fn.apply(that, [that]);

                        if (!f.isParallel)
                            if (paused === false) {
                                _run();
                            }
                        inQueue--;
                        if (inQueue == 0 && queue.length == 0)
                            _complete();
                    }, f.timeOut);

                    if (f.isParallel)
                        if (paused === false) {
                            _run();
                        }
                }
            };

            _complete = function () {
                if (completeFunc)
                    completeFunc.apply(that, [that]);
            };

            this.onComplete = function (func) {
                completeFunc = func;
            };

            this.onFailure = function (func) {
                failureFunc = func;
            };

            this.add = function (func) {
                // TODO: add callback for queue[i] complete

                var obj = arguments[0];
                if (obj && Object.prototype.toString.call(obj) === "[object Array]") {
                    var fn = arguments[1];
                    var timeOut = (typeof(arguments[2]) != "undefined") ? arguments[2] : defaultTimeOut;
                    if (typeof(fn) == "function") {
                        for (var i = 0; i < obj.length; i++) {
                            var f = function (objx) {
                                queue.push({
                                    isParallel: true, fn: function () {
                                        fn.apply(that, [that, objx]);
                                    }, timeOut: timeOut
                                });
                            }(obj[i])
                        }
                    }
                } else {
                    var fn = arguments[0];
                    var timeOut = (typeof(arguments[1]) != "undefined") ? arguments[2] : defaultTimeOut;
                    queue.push({isParallel: false, fn: func, timeOut: timeOut});
                }
                return this;
            };

            this.addParallel = function (func, timeOut) {
                // TODO: add callback for queue[i] complete

                queue.push({isParallel: true, fn: func, timeOut: timeOut});
                return this;
            };

            this.storeData = function (dataObject) {
                lastCallbackData = dataObject;
                return this;
            };

            this.lastCallbackData = function () {
                return lastCallbackData;
            };

            this.run = function () {
                paused = false;
                _run();
            };

            this.pause = function () {
                paused = true;
                return this;
            };

            this.failure = function () {
                paused = true;
                if (failureFunc) {
                    var args = [that];
                    for (i = 0; i < arguments.length; i++) {
                        args.push(arguments[i]);
                    }
                    failureFunc.apply(that, args);
                }
            };

            this.size = function () {
                return queue.length;
            };

            return this;
        },
        /**
         * base64 编码
         * @base64
         * @return String
         */
        base64: function () {
            // private property
            return {
                _keyStr: "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=",
                encode: function (input) {
                    var output = "";
                    var chr1, chr2, chr3, enc1, enc2, enc3, enc4;
                    var i = 0;
                    input = this._utf8_encode(input);
                    while (i < input.length) {
                        chr1 = input.charCodeAt(i++);
                        chr2 = input.charCodeAt(i++);
                        chr3 = input.charCodeAt(i++);
                        enc1 = chr1 >> 2;
                        enc2 = ((chr1 & 3) << 4) | (chr2 >> 4);
                        enc3 = ((chr2 & 15) << 2) | (chr3 >> 6);
                        enc4 = chr3 & 63;
                        if (isNaN(chr2)) {
                            enc3 = enc4 = 64;
                        } else if (isNaN(chr3)) {
                            enc4 = 64;
                        }
                        output = output + this._keyStr.charAt(enc1)
                            + this._keyStr.charAt(enc2) + this._keyStr.charAt(enc3)
                            + this._keyStr.charAt(enc4);
                    }
                    return output;
                },
                decode: function (input) {
                    var output = "";
                    var chr1, chr2, chr3;
                    var enc1, enc2, enc3, enc4;
                    var i = 0;
                    input = input.replace(/[^A-Za-z0-9\+\/\=]/g, "");
                    while (i < input.length) {
                        enc1 = this._keyStr.indexOf(input.charAt(i++));
                        enc2 = this._keyStr.indexOf(input.charAt(i++));
                        enc3 = this._keyStr.indexOf(input.charAt(i++));
                        enc4 = this._keyStr.indexOf(input.charAt(i++));
                        chr1 = (enc1 << 2) | (enc2 >> 4);
                        chr2 = ((enc2 & 15) << 4) | (enc3 >> 2);
                        chr3 = ((enc3 & 3) << 6) | enc4;
                        output = output + String.fromCharCode(chr1);
                        if (enc3 != 64) {
                            output = output + String.fromCharCode(chr2);
                        }
                        if (enc4 != 64) {
                            output = output + String.fromCharCode(chr3);
                        }
                    }
                    output = this._utf8_decode(output);
                    return output;
                },
                _utf8_encode: function (string) {
                    string = string.replace(/\r\n/g, "\n");
                    var utftext = "";
                    for (var n = 0; n < string.length; n++) {
                        var c = string.charCodeAt(n);
                        if (c < 128) {
                            utftext += String.fromCharCode(c);
                        } else if ((c > 127) && (c < 2048)) {
                            utftext += String.fromCharCode((c >> 6) | 192);
                            utftext += String.fromCharCode((c & 63) | 128);
                        } else {
                            utftext += String.fromCharCode((c >> 12) | 224);
                            utftext += String.fromCharCode(((c >> 6) & 63) | 128);
                            utftext += String.fromCharCode((c & 63) | 128);
                        }

                    }
                    return utftext;
                },
                // private method for UTF-8 decoding
                _utf8_decode: function (utftext) {
                    var string = "";
                    var i = 0;
                    var c = c1 = c2 = 0;
                    while (i < utftext.length) {
                        c = utftext.charCodeAt(i);
                        if (c < 128) {
                            string += String.fromCharCode(c);
                            i++;
                        } else if ((c > 191) && (c < 224)) {
                            c2 = utftext.charCodeAt(i + 1);
                            string += String.fromCharCode(((c & 31) << 6) | (c2 & 63));
                            i += 2;
                        } else {
                            c2 = utftext.charCodeAt(i + 1);
                            c3 = utftext.charCodeAt(i + 2);
                            string += String.fromCharCode(((c & 15) << 12)
                                | ((c2 & 63) << 6) | (c3 & 63));
                            i += 3;
                        }
                    }
                    return string;
                }
            }
        }(),
        /**
         * 返回路径中的文件名
         * @getFileName
         * @param   {String}        带路径的文件地址
         * @return  {String}        文件名带扩展名
         */
        getFileName: function (s) {
            var pos = s.lastIndexOf("/");
            return s.substring(pos + 1);
        },
        getDomain: function () {
            var domain = document.domain, host = location.protocol + "//" + window.location.host;
            return host
        }(),
        getLocation: function () {
            var location = window.location,
                hash = location.hash,
                host = location.host,
                hostname = location.hostname,
                pathname = location.pathname,
                port = location.port,
                protocol = location.protocol,
                url = protocol + "//" + hostname + (port ? ":" + port : "");
            var d = {
                fullUrl: url + pathname + hash,
                url: url
            }
            return d
        }(),
        parseOptions: function (s, f) {
            if (s) {
                if (s.substring(0, 1) != "{" && !f) {
                    s = "{" + s + "}";
                }
                try {
                    return (new Function("return " + $.trim(s)))();
                } catch (e) {
                    log("转换对象异常:" + s);
                }
            }
        },
        ping: function (option) {
            var ping, requestTime, responseTime;
            var getUrl = function (url) {    //保证url带http://
                var strReg = "^((https|http)?://){1}";
                var re = new RegExp(strReg);
                return re.test(url) ? url : "http://" + url;
            }
            TT.ajaxGet({
                url: getUrl(option.url) + '/' + (new Date()).getTime(),
                dataType: 'html',
                timeout: option.timeout || 4000,
                beforeSend: function () {
                    if (option.beforePing) option.beforePing.call(this);
                    requestTime = new Date().getTime();
                },
                complete: function () {
                    responseTime = new Date().getTime();
                    ping = Math.abs(requestTime - responseTime);
                    if (option.afterPing) option.afterPing.call(this, ping);
                }
            });

            /*if (option.interval && option.interval > 0) {
             var interval = option.interval * 1000;
             setTimeout(function () {
             Ls.ping(option)
             }, interval);
             }*/
        },
        /**
         * 子窗口获取父窗对象
         * @getWin
         * @return win 父窗口对象 ,api 弹窗对象, art弹窗的list对象， art 弹窗的 dialog 对象
         */
        getWin: function () {
            return 'dialog' in window ? dialog : null;
        }()
    });

    TT.extend({
        url: new function () {
            return new url()
        },
        tips: function (msg, options) {
            return new tips(msg, options);
        },
        tipsOk: function (msg, options) {
            return new tips(msg, $.extend(true, options, {icons: "success"}));
        },
        tipsErr: function (msg, options) {
            return new tips(msg, $.extend(true, options, {icons: "error"}));
        },
        tipsInfo: function (msg, options) {
            return new tips(msg, $.extend(true, options, {icons: "info"}));
        },
        tipsLoading: function (msg, options) {
            return new tips(msg, $.extend(true, options, {icons: "loading"}));
        },
        openWin: function (url, width, height, options) {
            return new openWin(url, width, height, options);
        },
        dragDrop: function (drag, options) {
            return new dragDrop(drag, options);
        }
    });

    TT.extend({
        /**
         * Jquery AJAX 方法
         * @ajax
         * @param   {Object}        参见jquery ajax options 配置
         * @return   {Integer}      返回 jquery ajax 对象
         */
        ajaxText: function (options) {
            var ops = $.extend(true, options, {
                type: "GET",
                dataType: "TEXT"
            });
            return TT.ajax(ops)
        },
        ajaxGet: function (options) {
            var ops = $.extend(true, options, {
                type: "GET"
            });
            return TT.ajax(ops)
        },
        ajax: function (options) {
            var defaults = {
                type: "POST",
                dataType: "JSON"
            };
            var ops = $.extend(true, defaults, options);

            if (ops.url == '') {
                TT.tipsErr('\u8bf7\u6c42\u5730\u5740\u4e0d\u80fd\u4e3a\u7a7a！');
                return false;
            }

            ops.url = GLOBAL_CONTEXTPATH + "" + ops.url + (/\?/.test(ops.url) ? "&" : "?") + "IsAjax=1&dataType=" + ops.dataType + "&_=" + Math.random();

            return $.ajax(ops).fail(function (d) {
                // TT.tips("请求出错！", {icons: "error"})
            }).always(function () {
                try {
                    Ls.www.isometryDiv();
                } catch (e) {
                }
                $("#ajax_tip").remove();
                //显示所有avalon的controller
                $("[ms-controller]").css("visibility", "visible");
            });
        },
        /**
         * 全选 checkbox 方法
         * @chkAll
         * @param   {Object}        当前全选 checkbox 对象的 this，目前此元素只能是 checkbox
         * @param   {Object}        选中的 checkbox 的名称，非ID
         * @param   {Integer}       type=1 实现择选功能，否则同全选按扭状态
         */
        chkAll: function (obj, el, type) {
            var input = {};

            if (type == undefined) {
                if (obj.checked) {
                    input = $("input[name='" + el + "']").each(function () {
                        $(this).prop("checked", true);
                    });
                } else {
                    input = $("input[name='" + el + "']").each(function () {
                        $(this).prop("checked", false);
                    });
                }
            } else if (type == 1) {
                input = $("input[name='" + el + "']").each(function () {
                    var _this = $(this);
                    _this.prop("checked", !this.checked);
                });
            }
            return input;
        }
    });

    var F_T = new String('萬與醜專業叢東絲丟兩嚴喪個爿豐臨為麗舉麼義烏樂喬習鄉書買亂爭於虧雲亙亞產畝親褻嚲億僅從侖倉儀們價眾優夥會傴傘偉傳傷倀倫傖偽佇體餘傭僉俠侶僥偵側僑儈儕儂俁儔儼倆儷儉債傾傯僂僨償儻儐儲儺兒兌兗黨蘭關興茲養獸囅內岡冊寫軍農塚馮衝決況凍淨淒涼淩減湊凜幾鳳鳧憑凱擊氹鑿芻劃劉則剛創刪別剗剄劊劌剴劑剮劍剝劇勸辦務勱動勵勁勞勢勳猛勩勻匭匱區醫華協單賣盧鹵臥衛卻巹廠廳曆厲壓厭厙廁廂厴廈廚廄廝縣參靉靆雙發變敘疊葉號歎嘰籲後嚇呂嗎唚噸聽啟吳嘸囈嘔嚦唄員咼嗆嗚詠哢嚨嚀噝吒噅鹹呱響啞噠嘵嗶噦嘩噲嚌噥喲嘜嗊嘮啢嗩唕喚呼嘖嗇囀齧囉嘽嘯噴嘍嚳囁嗬噯噓嚶囑嚕劈囂謔團園囪圍圇國圖圓聖壙場阪壞塊堅壇壢壩塢墳墜壟壟壚壘墾坰堊墊埡墶壋塏堖塒塤堝墊垵塹墮壪牆壯聲殼壺壼處備複夠頭誇夾奪奩奐奮獎奧妝婦媽嫵嫗媯姍薑婁婭嬈嬌孌娛媧嫻嫿嬰嬋嬸媼嬡嬪嬙嬤孫學孿寧寶實寵審憲宮寬賓寢對尋導壽將爾塵堯尷屍盡層屭屜屆屬屢屨嶼歲豈嶇崗峴嶴嵐島嶺嶽崠巋嶨嶧峽嶢嶠崢巒嶗崍嶮嶄嶸嶔崳嶁脊巔鞏巰幣帥師幃帳簾幟帶幀幫幬幘幗冪襆幹並廣莊慶廬廡庫應廟龐廢廎廩開異棄張彌弳彎彈強歸當錄彠彥徹徑徠禦憶懺憂愾懷態慫憮慪悵愴憐總懟懌戀懇惡慟懨愷惻惱惲悅愨懸慳憫驚懼慘懲憊愜慚憚慣湣慍憤憒願懾憖怵懣懶懍戇戔戲戧戰戩戶紮撲扡執擴捫掃揚擾撫拋摶摳掄搶護報擔擬攏揀擁攔擰撥擇掛摯攣掗撾撻挾撓擋撟掙擠揮撏撈損撿換搗據撚擄摑擲撣摻摜摣攬撳攙擱摟攪攜攝攄擺搖擯攤攖撐攆擷擼攛擻攢敵斂數齋斕鬥斬斷無舊時曠暘曇晝曨顯晉曬曉曄暈暉暫曖劄術樸機殺雜權條來楊榪傑極構樅樞棗櫪梘棖槍楓梟櫃檸檉梔柵標棧櫛櫳棟櫨櫟欄樹棲樣欒棬椏橈楨檔榿橋樺檜槳樁夢檮棶檢欞槨櫝槧欏橢樓欖櫬櫚櫸檟檻檳櫧橫檣櫻櫫櫥櫓櫞簷檁歡歟歐殲歿殤殘殞殮殫殯毆毀轂畢斃氈毿氌氣氫氬氳彙漢汙湯洶遝溝沒灃漚瀝淪滄渢溈滬濔濘淚澩瀧瀘濼瀉潑澤涇潔灑窪浹淺漿澆湞溮濁測澮濟瀏滻渾滸濃潯濜塗湧濤澇淶漣潿渦溳渙滌潤澗漲澀澱淵淥漬瀆漸澠漁瀋滲溫遊灣濕潰濺漵漊潷滾滯灩灄滿瀅濾濫灤濱灘澦濫瀠瀟瀲濰潛瀦瀾瀨瀕灝滅燈靈災燦煬爐燉煒熗點煉熾爍爛烴燭煙煩燒燁燴燙燼熱煥燜燾煆糊溜愛爺牘犛牽犧犢強狀獷獁猶狽麅獮獰獨狹獅獪猙獄猻獫獵獼玀豬貓蝟獻獺璣璵瑒瑪瑋環現瑲璽瑉玨琺瓏璫琿璡璉瑣瓊瑤璦璿瓔瓚甕甌電畫暢佘疇癤療瘧癘瘍鬁瘡瘋皰屙癰痙癢瘂癆瘓癇癡癉瘮瘞瘺癟癱癮癭癩癬癲臒皚皺皸盞鹽監蓋盜盤瞘眥矓著睜睞瞼瞞矚矯磯礬礦碭碼磚硨硯碸礪礱礫礎硜矽碩硤磽磑礄確鹼礙磧磣堿镟滾禮禕禰禎禱禍稟祿禪離禿稈種積稱穢穠穭稅穌穩穡窮竊竅窯竄窩窺竇窶豎競篤筍筆筧箋籠籩築篳篩簹箏籌簽簡籙簀篋籜籮簞簫簣簍籃籬籪籟糴類秈糶糲粵糞糧糝餱緊縶糸糾紆紅紂纖紇約級紈纊紀紉緯紜紘純紕紗綱納紝縱綸紛紙紋紡紵紖紐紓線紺絏紱練組紳細織終縐絆紼絀紹繹經紿綁絨結絝繞絰絎繪給絢絳絡絕絞統綆綃絹繡綌綏絛繼綈績緒綾緓續綺緋綽緔緄繩維綿綬繃綢綯綹綣綜綻綰綠綴緇緙緗緘緬纜緹緲緝縕繢緦綞緞緶線緱縋緩締縷編緡緣縉縛縟縝縫縗縞纏縭縊縑繽縹縵縲纓縮繆繅纈繚繕繒韁繾繰繯繳纘罌網羅罰罷羆羈羥羨翹翽翬耮耬聳恥聶聾職聹聯聵聰肅腸膚膁腎腫脹脅膽勝朧腖臚脛膠脈膾髒臍腦膿臠腳脫腡臉臘醃膕齶膩靦膃騰臏臢輿艤艦艙艫艱豔艸藝節羋薌蕪蘆蓯葦藶莧萇蒼苧蘇檾蘋莖蘢蔦塋煢繭荊薦薘莢蕘蓽蕎薈薺蕩榮葷滎犖熒蕁藎蓀蔭蕒葒葤藥蒞蓧萊蓮蒔萵薟獲蕕瑩鶯蓴蘀蘿螢營縈蕭薩蔥蕆蕢蔣蔞藍薊蘺蕷鎣驀薔蘞藺藹蘄蘊藪槁蘚虜慮虛蟲虯蟣雖蝦蠆蝕蟻螞蠶蠔蜆蠱蠣蟶蠻蟄蛺蟯螄蠐蛻蝸蠟蠅蟈蟬蠍螻蠑螿蟎蠨釁銜補襯袞襖嫋褘襪襲襏裝襠褌褳襝褲襇褸襤繈襴見觀覎規覓視覘覽覺覬覡覿覥覦覯覲覷觴觸觶讋譽謄訁計訂訃認譏訐訌討讓訕訖訓議訊記訒講諱謳詎訝訥許訛論訩訟諷設訪訣證詁訶評詛識詗詐訴診詆謅詞詘詔詖譯詒誆誄試詿詩詰詼誠誅詵話誕詬詮詭詢詣諍該詳詫諢詡譸誡誣語誚誤誥誘誨誑說誦誒請諸諏諾讀諑誹課諉諛誰諗調諂諒諄誶談誼謀諶諜謊諫諧謔謁謂諤諭諼讒諮諳諺諦謎諞諝謨讜謖謝謠謗諡謙謐謹謾謫譾謬譚譖譙讕譜譎讞譴譫讖穀豶貝貞負貟貢財責賢敗賬貨質販貪貧貶購貯貫貳賤賁貰貼貴貺貸貿費賀貽賊贄賈賄貲賃賂贓資賅贐賕賑賚賒賦賭齎贖賞賜贔賙賡賠賧賴賵贅賻賺賽賾贗讚贇贈贍贏贛赬趙趕趨趲躉躍蹌蹠躒踐躂蹺蹕躚躋踴躊蹤躓躑躡蹣躕躥躪躦軀車軋軌軒軑軔轉軛輪軟轟軲軻轤軸軹軼軤軫轢軺輕軾載輊轎輈輇輅較輒輔輛輦輩輝輥輞輬輟輜輳輻輯轀輸轡轅轄輾轆轍轔辭辯辮邊遼達遷過邁運還這進遠違連遲邇逕跡適選遜遞邐邏遺遙鄧鄺鄔郵鄒鄴鄰鬱郤郟鄶鄭鄆酈鄖鄲醞醱醬釅釃釀釋裏钜鑒鑾鏨釓釔針釘釗釙釕釷釺釧釤鈒釩釣鍆釹鍚釵鈃鈣鈈鈦鈍鈔鍾鈉鋇鋼鈑鈐鑰欽鈞鎢鉤鈧鈁鈥鈄鈕鈀鈺錢鉦鉗鈷缽鈳鉕鈽鈸鉞鑽鉬鉭鉀鈿鈾鐵鉑鈴鑠鉛鉚鈰鉉鉈鉍鈹鐸鉶銬銠鉺銪鋏鋣鐃銍鐺銅鋁銱銦鎧鍘銖銑鋌銩銛鏵銓鉿銚鉻銘錚銫鉸銥鏟銃鐋銨銀銣鑄鐒鋪鋙錸鋱鏈鏗銷鎖鋰鋥鋤鍋鋯鋨鏽銼鋝鋒鋅鋶鐦鐧銳銻鋃鋟鋦錒錆鍺錯錨錡錁錕錩錫錮鑼錘錐錦鍁錈錇錟錠鍵鋸錳錙鍥鍈鍇鏘鍶鍔鍤鍬鍾鍛鎪鍠鍰鎄鍍鎂鏤鎡鏌鎮鎛鎘鑷鐫鎳鎿鎦鎬鎊鎰鎔鏢鏜鏍鏰鏞鏡鏑鏃鏇鏐鐔钁鐐鏷鑥鐓鑭鐠鑹鏹鐙鑊鐳鐶鐲鐮鐿鑔鑣鑞鑲長門閂閃閆閈閉問闖閏闈閑閎間閔閌悶閘鬧閨聞闥閩閭闓閥閣閡閫鬮閱閬闍閾閹閶鬩閿閽閻閼闡闌闃闠闊闋闔闐闒闕闞闤隊陽陰陣階際陸隴陳陘陝隉隕險隨隱隸雋難雛讎靂霧霽黴靄靚靜靨韃鞽韉韝韋韌韍韓韙韞韜韻頁頂頃頇項順須頊頑顧頓頎頒頌頏預顱領頗頸頡頰頲頜潁熲頦頤頻頮頹頷頴穎顆題顒顎顓顏額顳顢顛顙顥纇顫顬顰顴風颺颭颮颯颶颸颼颻飀飄飆飆飛饗饜飣饑飥餳飩餼飪飫飭飯飲餞飾飽飼飿飴餌饒餉餄餎餃餏餅餑餖餓餘餒餕餜餛餡館餷饋餶餿饞饁饃餺餾饈饉饅饊饌饢馬馭馱馴馳驅馹駁驢駔駛駟駙駒騶駐駝駑駕驛駘驍罵駰驕驊駱駭駢驫驪騁驗騂駸駿騏騎騍騅騌驌驂騙騭騤騷騖驁騮騫騸驃騾驄驏驟驥驦驤髏髖髕鬢魘魎魚魛魢魷魨魯魴魺鮁鮃鯰鱸鮋鮓鮒鮊鮑鱟鮍鮐鮭鮚鮳鮪鮞鮦鰂鮜鱠鱭鮫鮮鮺鯗鱘鯁鱺鰱鰹鯉鰣鰷鯀鯊鯇鮶鯽鯒鯖鯪鯕鯫鯡鯤鯧鯝鯢鯰鯛鯨鯵鯴鯔鱝鰈鰏鱨鯷鰮鰃鰓鱷鰍鰒鰉鰁鱂鯿鰠鼇鰭鰨鰥鰩鰟鰜鰳鰾鱈鱉鰻鰵鱅鰼鱖鱔鱗鱒鱯鱤鱧鱣鳥鳩雞鳶鳴鳲鷗鴉鶬鴇鴆鴣鶇鸕鴨鴞鴦鴒鴟鴝鴛鴬鴕鷥鷙鴯鴰鵂鴴鵃鴿鸞鴻鵐鵓鸝鵑鵠鵝鵒鷳鵜鵡鵲鶓鵪鶤鵯鵬鵮鶉鶊鵷鷫鶘鶡鶚鶻鶿鶥鶩鷊鷂鶲鶹鶺鷁鶼鶴鷖鸚鷓鷚鷯鷦鷲鷸鷺鸇鷹鸌鸏鸛鸘鹺麥麩黃黌黶黷黲黽黿鼂鼉鞀鼴齇齊齏齒齔齕齗齟齡齙齠齜齦齬齪齲齷龍龔龕龜誌製谘隻裡係範鬆冇嚐嘗鬨麵準鐘彆閒乾儘臟拚');
    var F_S = new String('万与丑专业丛东丝丢两严丧个丬丰临为丽举么义乌乐乔习乡书买乱争于亏云亘亚产亩亲亵亸亿仅从仑仓仪们价众优伙会伛伞伟传伤伥伦伧伪伫体余佣佥侠侣侥侦侧侨侩侪侬俣俦俨俩俪俭债倾偬偻偾偿傥傧储傩儿兑兖党兰关兴兹养兽冁内冈册写军农冢冯冲决况冻净凄凉凌减凑凛几凤凫凭凯击凼凿刍划刘则刚创删别刬刭刽刿剀剂剐剑剥剧劝办务劢动励劲劳势勋勐勚匀匦匮区医华协单卖卢卤卧卫却卺厂厅历厉压厌厍厕厢厣厦厨厩厮县参叆叇双发变叙叠叶号叹叽吁后吓吕吗吣吨听启吴呒呓呕呖呗员呙呛呜咏咔咙咛咝咤咴咸哌响哑哒哓哔哕哗哙哜哝哟唛唝唠唡唢唣唤唿啧啬啭啮啰啴啸喷喽喾嗫呵嗳嘘嘤嘱噜噼嚣嚯团园囱围囵国图圆圣圹场坂坏块坚坛坜坝坞坟坠垄垅垆垒垦垧垩垫垭垯垱垲垴埘埙埚埝埯堑堕塆墙壮声壳壶壸处备复够头夸夹夺奁奂奋奖奥妆妇妈妩妪妫姗姜娄娅娆娇娈娱娲娴婳婴婵婶媪嫒嫔嫱嬷孙学孪宁宝实宠审宪宫宽宾寝对寻导寿将尔尘尧尴尸尽层屃屉届属屡屦屿岁岂岖岗岘岙岚岛岭岳岽岿峃峄峡峣峤峥峦崂崃崄崭嵘嵚嵛嵝嵴巅巩巯币帅师帏帐帘帜带帧帮帱帻帼幂幞干并广庄庆庐庑库应庙庞废庼廪开异弃张弥弪弯弹强归当录彟彦彻径徕御忆忏忧忾怀态怂怃怄怅怆怜总怼怿恋恳恶恸恹恺恻恼恽悦悫悬悭悯惊惧惨惩惫惬惭惮惯愍愠愤愦愿慑慭憷懑懒懔戆戋戏戗战戬户扎扑扦执扩扪扫扬扰抚抛抟抠抡抢护报担拟拢拣拥拦拧拨择挂挚挛挜挝挞挟挠挡挢挣挤挥挦捞损捡换捣据捻掳掴掷掸掺掼揸揽揿搀搁搂搅携摄摅摆摇摈摊撄撑撵撷撸撺擞攒敌敛数斋斓斗斩断无旧时旷旸昙昼昽显晋晒晓晔晕晖暂暧札术朴机杀杂权条来杨杩杰极构枞枢枣枥枧枨枪枫枭柜柠柽栀栅标栈栉栊栋栌栎栏树栖样栾桊桠桡桢档桤桥桦桧桨桩梦梼梾检棂椁椟椠椤椭楼榄榇榈榉槚槛槟槠横樯樱橥橱橹橼檐檩欢欤欧歼殁殇残殒殓殚殡殴毁毂毕毙毡毵氇气氢氩氲汇汉污汤汹沓沟没沣沤沥沦沧沨沩沪沵泞泪泶泷泸泺泻泼泽泾洁洒洼浃浅浆浇浈浉浊测浍济浏浐浑浒浓浔浕涂涌涛涝涞涟涠涡涢涣涤润涧涨涩淀渊渌渍渎渐渑渔渖渗温游湾湿溃溅溆溇滗滚滞滟滠满滢滤滥滦滨滩滪漤潆潇潋潍潜潴澜濑濒灏灭灯灵灾灿炀炉炖炜炝点炼炽烁烂烃烛烟烦烧烨烩烫烬热焕焖焘煅煳熘爱爷牍牦牵牺犊犟状犷犸犹狈狍狝狞独狭狮狯狰狱狲猃猎猕猡猪猫猬献獭玑玙玚玛玮环现玱玺珉珏珐珑珰珲琎琏琐琼瑶瑷璇璎瓒瓮瓯电画畅畲畴疖疗疟疠疡疬疮疯疱疴痈痉痒痖痨痪痫痴瘅瘆瘗瘘瘪瘫瘾瘿癞癣癫癯皑皱皲盏盐监盖盗盘眍眦眬着睁睐睑瞒瞩矫矶矾矿砀码砖砗砚砜砺砻砾础硁硅硕硖硗硙硚确硷碍碛碜碱碹磙礼祎祢祯祷祸禀禄禅离秃秆种积称秽秾稆税稣稳穑穷窃窍窑窜窝窥窦窭竖竞笃笋笔笕笺笼笾筑筚筛筜筝筹签简箓箦箧箨箩箪箫篑篓篮篱簖籁籴类籼粜粝粤粪粮糁糇紧絷纟纠纡红纣纤纥约级纨纩纪纫纬纭纮纯纰纱纲纳纴纵纶纷纸纹纺纻纼纽纾线绀绁绂练组绅细织终绉绊绋绌绍绎经绐绑绒结绔绕绖绗绘给绚绛络绝绞统绠绡绢绣绤绥绦继绨绩绪绫绬续绮绯绰绱绲绳维绵绶绷绸绹绺绻综绽绾绿缀缁缂缃缄缅缆缇缈缉缊缋缌缍缎缏缐缑缒缓缔缕编缗缘缙缚缛缜缝缞缟缠缡缢缣缤缥缦缧缨缩缪缫缬缭缮缯缰缱缲缳缴缵罂网罗罚罢罴羁羟羡翘翙翚耢耧耸耻聂聋职聍联聩聪肃肠肤肷肾肿胀胁胆胜胧胨胪胫胶脉脍脏脐脑脓脔脚脱脶脸腊腌腘腭腻腼腽腾膑臜舆舣舰舱舻艰艳艹艺节芈芗芜芦苁苇苈苋苌苍苎苏苘苹茎茏茑茔茕茧荆荐荙荚荛荜荞荟荠荡荣荤荥荦荧荨荩荪荫荬荭荮药莅莜莱莲莳莴莶获莸莹莺莼萚萝萤营萦萧萨葱蒇蒉蒋蒌蓝蓟蓠蓣蓥蓦蔷蔹蔺蔼蕲蕴薮藁藓虏虑虚虫虬虮虽虾虿蚀蚁蚂蚕蚝蚬蛊蛎蛏蛮蛰蛱蛲蛳蛴蜕蜗蜡蝇蝈蝉蝎蝼蝾螀螨蟏衅衔补衬衮袄袅袆袜袭袯装裆裈裢裣裤裥褛褴襁襕见观觃规觅视觇览觉觊觋觌觍觎觏觐觑觞触觯詟誉誊讠计订讣认讥讦讧讨让讪讫训议讯记讱讲讳讴讵讶讷许讹论讻讼讽设访诀证诂诃评诅识诇诈诉诊诋诌词诎诏诐译诒诓诔试诖诗诘诙诚诛诜话诞诟诠诡询诣诤该详诧诨诩诪诫诬语诮误诰诱诲诳说诵诶请诸诹诺读诼诽课诿谀谁谂调谄谅谆谇谈谊谋谌谍谎谏谐谑谒谓谔谕谖谗谘谙谚谛谜谝谞谟谠谡谢谣谤谥谦谧谨谩谪谫谬谭谮谯谰谱谲谳谴谵谶谷豮贝贞负贠贡财责贤败账货质贩贪贫贬购贮贯贰贱贲贳贴贵贶贷贸费贺贻贼贽贾贿赀赁赂赃资赅赆赇赈赉赊赋赌赍赎赏赐赑赒赓赔赕赖赗赘赙赚赛赜赝赞赟赠赡赢赣赪赵赶趋趱趸跃跄跖跞践跶跷跸跹跻踊踌踪踬踯蹑蹒蹰蹿躏躜躯车轧轨轩轪轫转轭轮软轰轱轲轳轴轵轶轷轸轹轺轻轼载轾轿辀辁辂较辄辅辆辇辈辉辊辋辌辍辎辏辐辑辒输辔辕辖辗辘辙辚辞辩辫边辽达迁过迈运还这进远违连迟迩迳迹适选逊递逦逻遗遥邓邝邬邮邹邺邻郁郄郏郐郑郓郦郧郸酝酦酱酽酾酿释里鉅鉴銮錾钆钇针钉钊钋钌钍钎钏钐钑钒钓钔钕钖钗钘钙钚钛钝钞钟钠钡钢钣钤钥钦钧钨钩钪钫钬钭钮钯钰钱钲钳钴钵钶钷钸钹钺钻钼钽钾钿铀铁铂铃铄铅铆铈铉铊铋铍铎铏铐铑铒铕铗铘铙铚铛铜铝铞铟铠铡铢铣铤铥铦铧铨铪铫铬铭铮铯铰铱铲铳铴铵银铷铸铹铺铻铼铽链铿销锁锂锃锄锅锆锇锈锉锊锋锌锍锎锏锐锑锒锓锔锕锖锗错锚锜锞锟锠锡锢锣锤锥锦锨锩锫锬锭键锯锰锱锲锳锴锵锶锷锸锹锺锻锼锽锾锿镀镁镂镃镆镇镈镉镊镌镍镎镏镐镑镒镕镖镗镙镚镛镜镝镞镟镠镡镢镣镤镥镦镧镨镩镪镫镬镭镮镯镰镱镲镳镴镶长门闩闪闫闬闭问闯闰闱闲闳间闵闶闷闸闹闺闻闼闽闾闿阀阁阂阃阄阅阆阇阈阉阊阋阌阍阎阏阐阑阒阓阔阕阖阗阘阙阚阛队阳阴阵阶际陆陇陈陉陕陧陨险随隐隶隽难雏雠雳雾霁霉霭靓静靥鞑鞒鞯鞴韦韧韨韩韪韫韬韵页顶顷顸项顺须顼顽顾顿颀颁颂颃预颅领颇颈颉颊颋颌颍颎颏颐频颒颓颔颕颖颗题颙颚颛颜额颞颟颠颡颢颣颤颥颦颧风飏飐飑飒飓飔飕飖飗飘飙飚飞飨餍饤饥饦饧饨饩饪饫饬饭饮饯饰饱饲饳饴饵饶饷饸饹饺饻饼饽饾饿馀馁馂馃馄馅馆馇馈馉馊馋馌馍馎馏馐馑馒馓馔馕马驭驮驯驰驱驲驳驴驵驶驷驸驹驺驻驼驽驾驿骀骁骂骃骄骅骆骇骈骉骊骋验骍骎骏骐骑骒骓骔骕骖骗骘骙骚骛骜骝骞骟骠骡骢骣骤骥骦骧髅髋髌鬓魇魉鱼鱽鱾鱿鲀鲁鲂鲄鲅鲆鲇鲈鲉鲊鲋鲌鲍鲎鲏鲐鲑鲒鲓鲔鲕鲖鲗鲘鲙鲚鲛鲜鲝鲞鲟鲠鲡鲢鲣鲤鲥鲦鲧鲨鲩鲪鲫鲬鲭鲮鲯鲰鲱鲲鲳鲴鲵鲶鲷鲸鲹鲺鲻鲼鲽鲾鲿鳀鳁鳂鳃鳄鳅鳆鳇鳈鳉鳊鳋鳌鳍鳎鳏鳐鳑鳒鳓鳔鳕鳖鳗鳘鳙鳛鳜鳝鳞鳟鳠鳡鳢鳣鸟鸠鸡鸢鸣鸤鸥鸦鸧鸨鸩鸪鸫鸬鸭鸮鸯鸰鸱鸲鸳鸴鸵鸶鸷鸸鸹鸺鸻鸼鸽鸾鸿鹀鹁鹂鹃鹄鹅鹆鹇鹈鹉鹊鹋鹌鹍鹎鹏鹐鹑鹒鹓鹔鹕鹖鹗鹘鹚鹛鹜鹝鹞鹟鹠鹡鹢鹣鹤鹥鹦鹧鹨鹩鹪鹫鹬鹭鹯鹰鹱鹲鹳鹴鹾麦麸黄黉黡黩黪黾鼋鼌鼍鼗鼹齄齐齑齿龀龁龂龃龄龅龆龇龈龉龊龋龌龙龚龛龟志制咨只里系范松没尝尝闹面准钟别闲干尽脏拼');

    /**
     * 转换文本
     * @param {String} str - 待转换的文本
     * @param {Boolean} toT - 是否转换成繁体
     * @returns {String} - 转换结果
     */
    function tranStr(str, toT) {
        var i;
        var letter;
        var code;
        var isChinese;
        var index;
        var src, des;
        var result = '';

        if (toT) {
            src = F_S;
            des = F_T;
        } else {
            src = F_T;
            des = F_S;
        }

        if (typeof str !== "string") {
            return str;
        }

        for (i = 0; i < str.length; i++) {
            letter = str.charAt(i);
            code = str.charCodeAt(i);

            // 根据字符的Unicode判断是否为汉字，以提高性能
            // 参考:
            // [1] http://www.unicode.org
            // [2] http://zh.wikipedia.org/wiki/Unicode%E5%AD%97%E7%AC%A6%E5%88%97%E8%A1%A8
            // [3] http://xylonwang.iteye.com/blog/519552
            isChinese = (code > 0x3400 && code < 0x9FC3) || (code > 0xF900 && code < 0xFA6A);

            if (!isChinese) {
                result += letter;
                continue;
            }

            index = src.indexOf(letter);

            if (index !== -1) {
                result += des.charAt(index);
            } else {
                result += letter;
            }
        }

        return result;
    }

    /**
     * 转换HTML Element节点
     * @param {Element} element - 待转换的HTML Element节点
     * @param {Boolean} toT - 是否转换成繁体
     */
    function tranElement(element, toT) {
        var i;
        var childNodes;

        if (element.nodeType !== 1) {
            return;
        }

        childNodes = element.childNodes;

        for (i = 0; i < childNodes.length; i++) {
            var childNode = childNodes.item(i);

            // 若为HTML Element节点
            if (childNode.nodeType === 1) {
                // 对以下标签不做处理
                if ("|BR|HR|TEXTAREA|SCRIPT|OBJECT|EMBED|".indexOf("|" + childNode.tagName + "|") !== -1) {
                    continue;
                }

                tranAttr(childNode, ['title', 'data-original-title', 'alt', 'placeholder'], toT);

                // input 标签
                // 对text类型的input输入框不做处理
                if (childNode.tagName === "INPUT"
                    && childNode.value !== ""
                    && childNode.type !== "text"
                    && childNode.type !== "hidden") {
                    childNode.value = tranStr(childNode.value, toT);
                }

                // 继续递归调用
                tranElement(childNode, toT);
            } else if (childNode.nodeType === 3) {  // 若为文本节点
                childNode.data = tranStr(childNode.data, toT);
            }
        }
    }

    /**
     * 转换HTML Element属性
     * @param {Element} element - 待转换的HTML Element节点
     * @param {String|Array} attr - 待转换的属性/属性列表
     * @param {Boolean} toT - 是否转换成繁体
     */
    function tranAttr(element, attr, toT) {
        var i, attrValue;

        if (attr instanceof Array) {
            for (i = 0; i < attr.length; i++) {
                tranAttr(element, attr[i], toT);
            }
        } else {
            attrValue = element.getAttribute(attr);

            if (attrValue !== "" && attrValue !== null) {
                element.setAttribute(attr, tranStr(attrValue, toT));
            }
        }
    }

    //JQuery插件
    $.fn.extend({
        /**
         * 全选 checkbox 并返回数组,此方法不支持反选功能
         * @getCheckBoxArr
         * @param    {string}        checkbox 的 name
         * @param    {boolean}       是否选中
         * @return   {array}         返回数组
         */
        getCheckBoxArr: function (flag) {
            var val = [];
            $(this).each(function (i, v) {
                v.checked = flag;
                flag && val.push(v.value);
            });
            return val;
        },
        getFromJSON: function () {
            var o = {};
            $.each(this.serializeArray(), function (index) {
                var _this = this;
                if (o[_this['name']]) {
                    o[_this['name']] = o[_this['name']] + "," + _this['value'];
                } else {
                    o[_this['name']] = _this['value'];
                }
            });
            return o;
        },
        //获取选中checkbox
        getChecked: function () {
            var _v = $(this).map(function () {
                return this.value;
            }).get().join(',');
            return _v;
        },
        serializeObject: function () {
            var o = {};
            var a = this.serializeArray();
            $.each(a, function () {
                if (o[this.name]) {
                    if (!o[this.name].push) {
                        o[this.name] = [o[this.name]];
                    }
                    o[this.name].push(this.value || '');
                } else {
                    o[this.name] = this.value || '';
                }
            });
            return o;
        },
        scrollLoading: function (options) {
            var defaults = {
                attr: "data-url",
                container: $(window),
                callback: $.noop
            };
            var params = $.extend({}, defaults, options || {});
            params.cache = [];
            $(this).each(function () {
                var node = this.nodeName.toLowerCase(),
                    url = $(this).attr(params["attr"]);
                //重组
                var data = {
                    obj: $(this),
                    tag: node,
                    url: url
                };
                params.cache.push(data);
            });

            var callback = function (call) {
                if ($.isFunction(params.callback)) {
                    params.callback.call(call.get(0));
                }
            };
            //动态显示数据
            var loading = function () {

                var contop, contHeight = params.container.height();
                if ($(window).get(0) === window) {
                    contop = $(window).scrollTop();
                } else {
                    contop = params.container.offset().top;
                }

                $.each(params.cache, function (i, data) {
                    var o = data.obj,
                        tag = data.tag,
                        url = data.url,
                        post, posb;

                    if (o) {
                        post = o.offset().top - contop,
                        post + o.height();

                        if ((post >= 0 && post < contHeight) || (posb > 0 && posb <= contHeight)) {
                            if (url) {
                                //在浏览器窗口内
                                if (tag === "img") {
                                    //图片，改变src
                                    callback(o.attr("src", url));
                                } else {
                                    o.load(url, {}, function () {
                                        callback(o);
                                    });
                                }
                            } else {
                                //无地址，直接触发回调
                                callback(o);
                            }
                            data.obj = null;
                        }
                    }
                });
            };
            //事件触发
            //加载完毕即执行
            loading();
            //滚动执行
            params.container.off("scroll.loading").on("scroll.loading", loading);
        },
        /**
         * jQuery Objects简转繁
         * @this {jQuery Objects} 待转换的jQuery Objects
         */
        s2t: function () {
            return this.each(function () {
                tranElement(this, true);
            });
        },

        /**
         * jQuery Objects繁转简
         * @this {jQuery Objects} 待转换的jQuery Objects
         */
        t2s: function () {
            return this.each(function () {
                tranElement(this, false);
            });
        }
    });

    //本项目扩展方法  ======
    TT.extend({
        /**
         * 分页涵数
         * @pagination
         * @param total 总页数
         * @param pageIndex 当前页面数
         * @param callBack 回调涵数
         * @return String
         */
        pagination: function (elem, callback, options) {
            // 创建分页
            var settings = $.extend(true, {
                total: 0,
                isTotal: false,
                isJump: true,
                pageCount: 20,
                currPage: 1,
                pageNumber: 5,
                callback: callback
            }, options);

            return new pagination(elem, {
                total: settings.total,
                pageCount: settings.pageCount,
                pageNumber: settings.pageNumber,
                currPage: settings.currPage,
                callback: settings.callback,
                isTotal: settings.isTotal,
                isJump: settings.isJump,
                isNext: settings.isNext,
                isPrev: settings.isPrev,
                isFirst: settings.isFirst,
                isLast: settings.isLast,
                tpl: settings.tpl
            });

        },
        /**
         * 更新 vm 模型中的值
         * @assignVM
         * @param   {Object}        vm 绑定的模型
         * @return  {Object}        新的对象，类似Jquery extend 方法
         */
        assignVM: function (vm, firstSource) {
            for (var i = 1; i < arguments.length; i++) {
                var nextSource = arguments[i];
                if (nextSource && typeof nextSource !== "object") continue;
                for (var i in vm) {
                    if (vm.hasOwnProperty(i) && nextSource.hasOwnProperty(i)) {
                        vm[i] = nextSource[i]
                    }
                }
            }
            return vm
        },
        /**
         * 转换 vm 模型为纯 JSON 对象
         * @toJSON
         * @param   {String}        表单 form ID
         * @return  {Object}        返回纯的 JSON 对象
         */
        toJSON: function (s) {
            var d = s || {}, seen = [];
            var json = JSON.stringify(d, function (key, value) {
                if (typeof value === 'object') {
                    if (!seen.indexOf(value)) {
                        return '__cycle__' + (typeof value) + '[' + key + ']';
                    }
                    seen.push(value);
                }
                return value;
            }, 4);
            return JSON.parse(json);
        },
        /**
         * 转换FORM表单元素为JSON对象
         * @initFORM
         * @param   {String}       表单 form ID
         * @param   {Object}       追加始化数据,如果同名将覆盖表单元素对象
         * @return  {Object}       返回纯的 JSON 对象
         */
        initFORM: function (elem, vmodels) {

            var elemJSON, elem = elem.replace(/(#|\.)/g, "");

            elemJSON = $("#" + elem).serializeObject() || {};
            elemJSON.$id = elem;

            return $.extend(true, elemJSON, vmodels);

        },
        /**
         * 转换当前时间为Unix时间缀
         * @toUnixTime
         * @return  {String}  返回Unix时间缀
         */
        toUnixTime: function () {
            var ts = arguments[0] || 0;
            var t, y, m, d, h, i, s;
            t = ts ? new Date(ts * 1000) : new Date();
            y = t.getFullYear();
            m = t.getMonth() + 1;
            d = t.getDate();
            h = t.getHours();
            i = t.getMinutes();
            s = t.getSeconds();
            // 可根据需要在这里定义时间格式
            return y + '-' + (m < 10 ? '0' + m : m) + '-' + (d < 10 ? '0' + d : d) + ' ' + (h < 10 ? '0' + h : h) + ':' + (i < 10 ? '0' + i : i) + ':' + (s < 10 ? '0' + s : s);
        },
        dropDown: function (ctrl) {
            var dfd = $.Deferred();
            dfd.reject(event, treeId, treeNode, msg);
            dfd.resolve(event, treeId, treeNode, msg);
            return dfd.promise();
        },
        /**
         * 公共上传 mimeTypes 字典
         * @param suffix  扩展名
         */
        mimeTypes: function (suffix) {
            if (Ls.isEmpty(suffix)) {
                return false;
            }
            var mime = [],
                ext = suffix.split(",");
            for (var i = 0, l = ext.length; i < l; i++) {
                mime.push(MIMETYPES[ext[i]]);
            }
            return mime.join(",");
        },
        /**
         * 公共上传方法
         * @uploadFile
         * @param callback 回调涵数 function(filepath) 返回数组
         * @param settings 参见 swfupload 配置
         */
        uploadFile: function (callback, settings) {

            //无回调，反转参数
            if (!this.isFunction(callback)) {
                settings = callback;
            }

            $.extend(true, {
                uploadType: 0
            }, settings);

            if (settings.uploadType == 1) {

                var resAll = [],
                    resFile = [],
                    fileIds = [],
                    fileNames = [],
                    loadSet = {
                        dialog: top.dialog,
                        // 文件接收服务端
                        server: settings.upload_url ? settings.upload_url : '',
                        // 渲染按扭ID
                        pick: {
                            id: '#spanButtonPlaceHolder'
                        },
                        // 附加参数
                        formData: settings.post_params ? settings.post_params : '',
                        // 文件总数量
                        fileNumLimit: settings.file_upload_limit ? settings.file_upload_limit : '',
                        // 文件大小
                        fileSingleSizeLimit: settings.file_size_limit ? settings.file_size_limit : '',
                        // 类型
                        accept: {
                            title: settings.file_types_description ? settings.file_types_description : '',
                            extensions: settings.file_types ? (settings.file_types === "*.*") ? "*.*" : settings.file_types.split(";*.").join().substr(2) : '',
                            mimeTypes: '*/*'
                        },
                        // 设置文件上传域的name
                        fileVal: 'Filedata',
                        // 错误判断
                        flag: true,
                        lock: settings.lock ? settings.lock : '',
                        events: {
                            beforeOpen: function () {
                                return true;
                            },
                            //删除前预判断
                            beforeRemove: function () {
                                return true;
                            },
                            //删除回调
                            removeFile: function () {

                            },
                            beforeFileQueued: function (file) {
                                this.options.flag = true;
                            },
                            fileQueued: function (file) {
                            },
                            filesQueued: function (file) {
                            },
                            fileDequeued: function (file) {
                            },
                            uploadStart: function (file) {
                            },
                            uploadSuccess: function (file, res) {
                                var options = this.options,
                                    doc = this.options.doc,
                                    $status = doc.find('.progressBarStatus'),
                                    $container = doc.find('.progressContainer');

                                if (res) {
                                    $container.addClass('blue');
                                    $status.text('上传成功');
                                    /*resAll.push(res);
                                    resFile.push(res);
                                    fileIds.push(res.mongoId);
                                    fileNames.push(res.fileName);
                                    resAll.push(JSON.stringify(res));*/
                                    resAll.push(res);
                                }

                                (!options.dialog && callback) && callback.call(this, resAll)
                            },
                            uploadError: function (file, res) {
                                var me = this,
                                    options = me.options,
                                    doc = me.options.doc,
                                    $container = doc.find('.progressContainer'),
                                    $status = doc.find('.progressBarStatus'),
                                    $progress = doc.find('.progressBarInProgress');

                                options.flag = false;
                                $container.addClass("red");
                                $status.text('上传失败');
                                $progress.css('width', '0%');

                                (!options.dialog && callback) && callback.call(this, res)
                            },
                            uploadProgress: function (file, percentage) {
                                var me = this,
                                    doc = me.options.doc;
                                var $wrapper = $('#' + file.id),
                                    $progress = doc.find('.progressBarInProgress');
                                $progress.css('width', percentage * 100 + '%');
                            },
                            uploadComplete: function (file) {
                                var me = this,
                                    doc = me.options.doc,
                                    $progress = doc.find('.progressBarInProgress');
                                $progress.remove();
                            },
                            uploadFinished: function () {
                                var me = this,
                                    options = me.options;
                                /*res = {
                                    res: resAll,
                                    list: resFile,
                                    html: parseFileList(resFile),
                                    fileIds: fileIds.join(","),
                                    fileNames: fileNames.join(",")
                                }*/
                                if (me.options.flag) {
                                    Ls.tips('文件上传成功，窗口将自动关闭！', {
                                        callback: function () {
                                            if (options.dialog) {
                                                options.dialog.close(resAll);
                                            }
                                        }
                                    })
                                } else {
                                    return false;
                                }

                            },
                            errorHandler: function (code) {
                                var me = this,
                                    options = me.options;

                                var showError = function (code) {
                                    var text = "";
                                    switch (code) {
                                        case 'Q_EXCEED_NUM_LIMIT':
                                            text = '超时上传数量';
                                            break;
                                        case 'Q_EXCEED_SIZE_LIMIT':
                                            text = '文件总大超出设定的值（' + options.fileSingleSizeLimit1 + options.mega + '）';
                                            break;
                                        case 'F_EXCEED_SIZE':
                                            text = '文件总大超出设定的值（' + options.fileSingleSizeLimit1 + options.mega + '）';
                                            break;
                                        case 'Q_TYPE_DENIED':
                                            text = '文件类型不匹配; \n\n 允许类型：' + options.accept[0].extensions;
                                            break;
                                        default:
                                            text = code;
                                    }
                                    return text;
                                }

                                alert(showError(code));
                                return false;
                            }
                        }
                    }

                return settings.fileType != 0 ? (function () {

                    using("dialog", function () {
                        // 重要，此处要置绑定的事件，否则打开窗口取到的是父页的对象
                        settings.events = {
                            beforeOpen: loadSet.events.beforeOpen,
                            beforeRemove: loadSet.events.beforeRemove,
                            removeFile: loadSet.events.removeFile
                        };

                        var pageUrl = settings.url ? settings.url : "/upload/webUploader?s=" + Math.random();
                        //弹出窗口
                        var uploadFile = TT.openWin(pageUrl, "530px", "360px", {
                            id: 'uploadFile_swf',
                            title: (settings && settings.title) || "上传",
                            upload_settings: loadSet
                        }).show();

                        //返回值
                        uploadFile.addEventListener('remove', function () {
                            var filePath = this.returnValue;
                            if (filePath && filePath.length > 0) {
                                callback.call(this, filePath);
                                /*try {
                                 filePath = new Function("return [" + filePath.join() + "]")();
                                 callback.call(this, filePath);
                                 } catch (e) {
                                 alert('返回数据类型无法解析！')
                                 }*/
                            }
                        });

                        //缓存弹窗对象备用
                        TT.dialog.put("uploadFile", uploadFile);
                    })
                }()) : (function () {
                    var $list = $('#fsUploadProgress');

                    if (settings.mega == 'KB') {
                        settings.fileSingleSizeLimit = settings.fileSingleSizeLimit1 * 1024;
                    } else {
                        settings.fileSingleSizeLimit = settings.fileSingleSizeLimit1 * 1024 * 1024;
                    }

                    var uploader = WebUploader.create(settings);

                    $(".max_info").html("&nbsp;[最多上传：" + settings.fileNumLimit + "个文件，最大上传：" + settings.fileSingleSizeLimit1 + settings.mega + " ]");

                    // 当有文件添加进来的时候
                    uploader.on('fileQueued', function (file) {
                        var $li = $(
                            '<div class="progressWrapper" id="' + file.id + '">' +
                            '    <div class="progressContainer">' +
                            '        <a class="progressCancel" href="javascript:void(0)" data-id="' + file.id + '" style="visibility: visible;"> </a>' +
                            '        <div class="progressBarInProgress"></div>' +
                            '        <div class="progressName">' + file.name + '</div>' +
                            '        <div class="progressBarStatus">&nbsp;</div>' +
                            '    </div>' +
                            '</div>'
                        );

                        // $list为容器jQuery实例
                        $list.append($li);

                    });

                    // 当文件被加入队列之前触发，此事件的handler返回值为false，则此文件不会被添加进入队列
                    uploader.on('beforeFileQueued', settings.events.beforeFileQueued);

                    // 当文件被加入队列以后触发
                    uploader.on('fileQueued', settings.events.fileQueued);

                    // 当一批文件添加进队列以后触发。
                    uploader.on('filesQueued', settings.events.filesQueued);

                    // 当一批文件添加进队列以后触发。
                    uploader.on('fileDequeued', settings.events.fileDequeued);

                    // 文件开始上传前触发，一个文件只会触发一次
                    uploader.on('uploadStart', settings.events.uploadStart);

                    // 文件上传过程中创建进度条实时显示
                    uploader.on('uploadProgress', settings.events.uploadProgress);

                    // 文件上传成功，给item添加成功class, 用样式标记上传成功
                    uploader.on('uploadSuccess', settings.events.uploadSuccess);

                    // 文件上传失败，显示上传出错
                    uploader.on('uploadError', settings.events.uploadError);

                    // 完成上传完了，成功或者失败，先删除进度条
                    uploader.on('uploadComplete', settings.events.uploadComplete);

                    //当所有文件上传结束时触发
                    uploader.on('uploadFinished', settings.events.uploadFinished);

                    //当validate不通过时
                    uploader.on('error', settings.events.errorHandler);

                    //删除队列
                    $(document).on('click', '.progressCancel', function () {
                        var id = $(this).data("id");
                        uploader.removeFile(id, true);
                        $("#" + id).remove();
                    });

                    return uploader;
                }());

            } else {

                using("dialog", function () {

                    var pageUrl = settings.url ? settings.url : "/upload/index?s=" + Math.random();

                    //弹出窗口
                    var uploadFile = TT.openWin(pageUrl, "530px", "360px", {
                        id: 'uploadFile_swf',
                        title: (settings && settings.title) || "上传",
                        upload_settings: settings
                    }).show();

                    //返回值
                    uploadFile.addEventListener('remove', function () {
                        var filePath = this.returnValue;
                        if (filePath && filePath.length > 0) {
                            callback.call(this, filePath);
                            /*try {
                             filePath = new Function("return [" + filePath.join() + "]")();
                             callback.call(this, filePath);
                             } catch (e) {
                             alert('返回数据类型无法解析！')
                             }*/
                        }
                    });

                    //缓存弹窗对象备用
                    TT.dialog.put("uploadFile", uploadFile);
                })

            }

        },
        /**
         * 公共上传方法
         * @uploadFile2
         * @param callback 回调涵数 function(filepath) 返回数组
         * @param settings 参见 swfupload 配置
         */
        uploadFile2: function (callback, settings) {
            using("dialog", function () {

                var pageUrl = settings.url ? settings.url : "/upload/index2?s=" + Math.random();

                //弹出窗口
                var uploadFile = TT.openWin(pageUrl, "530px", "360px", {
                    id: 'uploadFile_swf',
                    title: (settings && settings.title) || "上传",
                    upload_settings: settings
                }).show();

                //返回值
                uploadFile.addEventListener('remove', function () {
                    var filePath = this.returnValue;
                    if (filePath && filePath.length > 0) {
                        callback.call(this, filePath);
                        /*try {
                         filePath = new Function("return [" + filePath.join() + "]")();
                         callback.call(this, filePath);
                         } catch (e) {
                         alert('返回数据类型无法解析！')
                         }*/
                    }
                });

                //缓存弹窗对象备用
                TT.dialog.put("uploadFile", uploadFile);
            })
        }
    });

    //修复IE 对象不支持indexOf属性或方法
    if (!Array.prototype.indexOf) {
        Array.prototype.indexOf = function (obj) {
            for (var i = 0; i < this.length; i++) {
                if (this[i] == obj) {
                    return i;
                }
            }
            return -1;
        }
    }

    /**
     * 追加或更新当前获取URL参数
     * @replaceAll
     * @param   {String}       要替换的字符串
     * @param   {String}       替换后的字符串
     * @param   {String}       是否区分大小写
     */
    String.prototype.replaceAll = function (reallyDo, replaceWith, ignoreCase) {
        if (!RegExp.prototype.isPrototypeOf(reallyDo)) {
            return this.replace(new RegExp(reallyDo, (ignoreCase ? "gi" : "g")), replaceWith);
        } else {
            return this.replace(reallyDo, replaceWith);
        }
    };

    /**
     * 追加或更新当前获取URL参数
     * @setUrlParam
     * @param   {String}       key
     * @param   {String}       value
     */
    String.prototype.setUrlParam = function (param, value) {
        var searchParsed = TT.url.parseQuery(this);

        // Delete the parameter
        if (value === undefined) {
            delete searchParsed[param];
        } else {
            // Update or add
            searchParsed[param] = encodeURIComponent(value);
        }

        var newSearch = "?" + TT.url.stringify(searchParsed);

        return this.split("?")[0] + newSearch;
    };

    //注入CORE
    template.helper('Ls', TT);

    //本项目扩展方法  ====== 结束

    //缓存JS路径
    var cacheJS = new cache(30);

    //分析参数
    /*TT.args = (function (script, i, me) {
     for (var i = 0, l = script.length; i < l; i++) {
     me = !!document.querySelector ? script[i].src : script[i].getAttribute('src', 4);
     if (me.substr(me.lastIndexOf('/')).indexOf('core-min') !== -1) {
     break;
     }
     }
     var args = me.split('?')[1];
     return TT.url.parseQuery(args);
     })(document.getElementsByTagName('script'), 0);*/


    //copyright jeasyui
    (function () {

        var modules = {
            nice: {
                js: "nice-validator/validator-min.js",
                css: "nice-validator/validator-min.css"
            },
            validator: {
                js: "nice-validator/local/zh-CN.js",
                dependencies: [
                    "nice"
                ]
            },
            ztree: {
                js: "ztree/ztree-min.js",
                css: "ztree/ztree-min.css"
            },
            datePicker: {js: "datePicker/WdatePicker-min.js"},
            dialog: {js: "dialog-min.js"},
            artDialog: {js: "artdialog-min.js"},
            ckplayer: {js: "ckplayer/ckplayer-min.js"},
            myfocus: {
                js: "myfocus-min.js"
            },
            jt: {js: "jt-min.js"},
            png: {js: "png-min.js"},
            MSClass: {js: "MSClass-min.js"},
            hotkeys: {js: "hotkeys-min.js"},
            wza: {js: "wza-min.js"},
            swfobject: {js: "swfobject-min.js"},
            qrcode: {js: "qrcode.min.js"},
            moment_core: {js: "moment/moment.min.js"},
            moment: {
                js: "moment/zh-cn.js",
                dependencies: [
                    "moment_core"
                ]
            },
            marqueeV2: {js: "marquee_v2.min.js"},
            scroll: {js: "scroll.min.js"},
            html2canvas: {
                js: "html2canvas.min.js",
                dependencies: [
                    "printArea"
                ]
            },
            printArea: {js: 'printArea.min.js'},
            slimscroll: {js: 'slimscroll.min.js'}
        };

        var locales = {};
        var queues = {};

        function loadJs(url, callback) {
            var done = false;
            var key = TT.getFileName(url);
            var ret = cacheJS.get(key);
            if (!ret) {
                var script = document.createElement("script");
                script.type = "text/javascript";
                script.language = "javascript";
                script.src = url;
                script.onload = script.onreadystatechange = function () {
                    if (!done && (!script.readyState || script.readyState == "loaded" || script.readyState == "complete")) {
                        done = true;
                        script.onload = script.onreadystatechange = null;
                        if (callback) {
                            callback.call(script);
                        }
                    }
                };
                document.getElementsByTagName("head")[0].appendChild(script);
                cacheJS.put(key, url);
            } else {
                callback && callback();
            }
        };

        function runJs(url, callback) {
            loadJs(url, function () {
                document.getElementsByTagName("head")[0].removeChild(this);
                if (callback) {
                    callback();
                }
            });
        };

        function loadCss(url, callback) {
            var link = document.createElement("link");
            link.rel = "stylesheet";
            link.type = "text/css";
            link.media = "screen";
            link.href = url;
            document.getElementsByTagName("head")[0].appendChild(link);
            if (callback) {
                callback.call(link);
            }
        };

        function loadSingle(name, callback) {
            queues[name] = "loading";
            var module = modules[name];
            var jsStatus = "loading";
            var cssStatus = (loader.css && module["css"]) ? "loading" : "loaded";
            if (loader.css && module["css"]) {
                if (/^http/i.test(module["css"])) {
                    var url = module["css"];
                } else {
                    var url = loader.base + loader.theme + module["css"];
                }
                loadCss(url, function () {
                    cssStatus = "loaded";
                    if (jsStatus == "loaded" && cssStatus == "loaded") {
                        finish();
                    }
                });
            }
            if (/^http/i.test(module["js"])) {
                var url = module["js"];
            } else {
                var url = loader.base + module["js"];
            }
            loadJs(url, function () {
                jsStatus = "loaded";
                if (jsStatus == "loaded" && cssStatus == "loaded") {
                    finish();
                }
            });

            function finish() {
                queues[name] = "loaded";
                loader.onProgress(name);
                if (callback) {
                    callback();
                }
            };
        };

        function loadModule(name, callback) {
            var mm = [];
            var doLoad = false;
            if (typeof name == "string") {
                add(name);
            } else {
                for (var i = 0; i < name.length; i++) {
                    add(name[i]);
                }
            }

            function add(_1b) {
                if (!modules[_1b]) {
                    return;
                }
                var d = modules[_1b]["dependencies"];
                if (d) {
                    for (var i = 0; i < d.length; i++) {
                        add(d[i]);
                    }
                }
                mm.push(_1b);
            };

            function finish() {
                if (callback) {
                    callback();
                }
                loader.onLoad(name);
            };

            var time = 0;

            function loadMm() {
                if (mm.length) {
                    var m = mm[0];
                    if (!queues[m]) {
                        doLoad = true;
                        loadSingle(m, function () {
                            mm.shift();
                            loadMm();
                        });
                    } else {
                        if (queues[m] == "loaded") {
                            mm.shift();
                            loadMm();
                        } else {
                            if (time < loader.timeout) {
                                time += 10;
                                setTimeout(arguments.callee, 10);
                            }
                        }
                    }
                } else {
                    if (loader.locale && doLoad == true && locales[loader.locale]) {
                        var url = loader.base + "locale/" + locales[loader.locale];
                        runJs(url, function () {
                            finish();
                        });
                    } else {
                        finish();
                    }
                }
            };

            loadMm();
        };

        var loader = {
            modules: modules,
            locales: locales,
            base: "/assets/js/",
            theme: "",
            css: true,
            locale: null,
            timeout: 2000,
            load: function (name, callback) {
                if (/\.css$/i.test(name)) {
                    if (/^http/i.test(name)) {
                        loadCss(name, callback);
                    } else {
                        loadCss(loader.base + name, callback);
                    }
                } else {
                    if (/\.js$/i.test(name)) {
                        if (/^http/i.test(name)) {
                            loadJs(name, callback);
                        } else {
                            loadJs(loader.base + name, callback);
                        }
                    } else {
                        loadModule(name, callback);
                    }
                }
            },
            onProgress: function (name) {
            },
            onLoad: function (name) {
            }
        };

        var scripts = document.getElementsByTagName("script");
        for (var i = 0; i < scripts.length; i++) {
            var src = scripts[i].src;
            if (src && /core|common(?:(\-|\.)min)?.js/.test(src)) {
                var args = TT.url.parseQuery(src.split('?')[1]);
                if (args.lib) {
                    var libs = (args.lib).split(",");
                    for (var i = 0, l = libs.length; i < l; i++) {
                        loader.load(libs[i]);
                    }
                }
                TT.args = args;
                break;
            }
        }

        window.using = loader.load;

    })();

    /*
     * 项目前端定制
     */
    var os = {};
    os.www = (function () {
        return {
            /**
             * 初始化所有组件
             */
            init: function () {
                var me = this;

                //初始轮播事件
                me.myfocus();

                //初始领导之窗
                me.myLeadInfo();

                //初始领导之窗
                me.myLeadInfo2();

                //初始化标签信息调用
                me.getLabelList();

                //初始化图务院信息调用
                me.getRemoteInfo();

                //JSONP 调用本系统信息
                me.getJsonpInfo();

                //初始化会员登录状态
                me.memberLoginCheck();

                //初始化搜索条默认文字
                me.search();

                //初始化简繁体转换
                me.jft();

                //留言统计
                me.guestbookCount();

                //留言点击更新
                me.refreshGuestbookHit();

                //输入默认文字消除
                me.inputPlaceholder();

                //视频播放放
                me.swfPlayer();

                //初始化tabs
                me.tabs();

                //滚动到页面顶部代码
                me.gotoTop();

                //左右等高实现
                me.isometryDiv();

                //演示日期
                me.renderDate();

                //外网跳转链接提示
                me.linkDetection();

                // 文件点击数
                me.getInfoHit();

                //无缝滚动
                me.scroll();

                //初始化无缝滚动插件
                me.myMarquee();
            },
            init2: function () {
                var me = this;
                //飘浮插件
                me.myFloat();

                //飘浮对联
                me.myCouplet();

                //友情链接
                me.myLinks();

                //无障碍浏览插件初始化
                me.myWza();

                //字体大小
                me.fontSize();

                //查看图片大图
                me.pictureView();

                //IE6下加载PNG插件
                me.png();

                //绑定日期选择控件
                me.datePicker();

                //绑定表单验证事件
                me.formValidator();

                //二维码生成
                me.qrcode();

                //弹出页面
                me.winOpen();

                //页面纠错
                me.correction();

                // 党政机关
                me.footerIdentifier();

                // 站点统计
                me.getStatistics();

                // pdf 预览
                me.pdfView();

                // 打印
                me.printView();
            },
            /**
             * ajax 请求后调用方法
             */
            ajaxInit: function () {
                var me = this;
                //初始化简繁体转换
                me.jft();
            },
            /**
             * 全局搜索关键字填充
             * @search
             * @description   取URL上的keywords参数，自动填充样式名为“search-keywords”的元素。
             */
            search: function () {
                var keywords = TT.url.get("keywords");
                if (!TT.isEmpty(keywords)) {
                    $(".search-keywords").each(function (i, v) {
                        var $el = $(v);
                        var val = $el.is('input') ? 'val' : 'text';
                        $el[val](keywords.replace(/\+/g, " "));
                    });
                }
                var hash = window.location.hash;
                if (!TT.isEmpty(hash)) {
                    $("#search_tab").find('a[href="' + hash + '"]').trigger("click");
                }
                return this;
            },
            /**
             * 简繁体转换
             * @jft
             * @description   查找样式含有 ls-jt 的元素。
             */
            jft: function () {

                //如果人为禁用，则不加载
                var jt = TT.args.jt;
                if (!TT.isEmpty(jt) && jt == "false") {
                    return false;
                }

                var jtEle = $('.ls-jt,.j-jft'),
                    defaults = {
                        jtText: "",
                        ftText: "",
                        type: "",
                        current: "s"
                    };

                if (jtEle.length > 0) {
                    //绑定点击事件
                    $(document).off('click.jft').on('click.jft', '.ls-jt,.j-jft', function (e) {
                        var $this = $(this),
                            data = $this.data("setting"),
                            setting = $.extend(true, defaults, TT.parseOptions(data));

                        var type = "";

                        //简繁体切换按钮选中状态
                        $('.ls-jt,.j-ft').removeClass('active');
                        $this.addClass('active');

                        if (TT.isEmpty(setting.type) || setting.type == "jf") {
                            type = TT.cookies("ls-jt") == "t" ? "s" : "t";
                        } else if (setting.type == "s") {
                            type = "s";
                        } else if (setting.type == "t") {
                            type = "t";
                        }
                        setting.type = type == "t" ? "s" : "t";
                        innerDOM($this, setting);
                        if (type == "s") {
                            $('body').t2s();
                        } else {
                            $('body').s2t();
                        }
                        TT.cookies("ls-jt", type, {path: "/"});
                        return false;
                    });

                    //写入
                    var innerDOM = function (ele, setting) {
                        var type = setting.type;
                        if (type == "t") {
                            setting.ftText && ele.html('<span class="ft ' + (setting.current == 't' ? "active" : "") + '">' + setting.ftText + '</span>');
                        } else {
                            setting.jtText && ele.html('<span class="jt ' + (setting.current == 's' ? "active" : "") + '">' + setting.jtText + '</span>');
                        }
                    }

                    jtEle.each(function (i, v) {
                        var $this = $(v),
                            data = $this.data("setting"),
                            setting = $.extend(true, defaults, TT.parseOptions(data)),
                            type = setting.type;
                        setting.current = Ls.isEmpty(TT.cookies("ls-jt")) ? "s" : Ls.trim(TT.cookies("ls-jt"));

                        //读取COOKIES判断当前状态
                        if (type == 'jf') {
                            setting.type = setting.current == "t" ? "s" : "t";
                        } else {
                            if (setting.current == Ls.trim(type)) {
                                $this.addClass("active");
                            } else {
                                $this.removeClass("active");
                            }
                        }
                        innerDOM($this, setting)
                    });

                }

                //初始化
                if (Ls.cookies('ls-jt') == "t") {
                    $('body').s2t();
                }

                return this;
            },
            /**
             * PNG图片透明
             * @png
             * @description   IE6下系统自动装载此插件实现PNG图片透明
             */
            png: function () {
                //IE6下，加载png 插件
                if (TT.browser.isIE6) {
                    using("png", function () {
                        EvPNG.fix('*');
                    });
                }
                return this;
            },
            /**
             * 页面纠错公共弹窗
             * @correction
             * @description   页面纠错
             */
            correction: function () {
                $(document).off('.correction').on('click.correction', '.page-correction,.j-page-correction', function (e) {
                    var $this = $(this),
                        setting = TT.parseOptions($this.data("setting"));

                    var defaults = $.extend(true, {
                        id: "page_correction",
                        url: $this.data("url"),
                        title: $this.data("title") || "页面纠错",
                        width: $this.data("width") || "500px",
                        height: $this.data("height") || "400px",
                        lock: false
                    }, setting);

                    if (!defaults.url) {
                        return false;
                    }

                    using("dialog", function () {
                        var correction = TT.openWin(defaults.url, defaults);
                        if (defaults.lock) {
                            correction.showModal();
                        } else {
                            correction.show();
                        }
                        TT.dialog.put("correction", correction);
                    });
                    return false;
                });
                return this;
            },
            /**
             * 全局公共弹窗
             * @winOpen
             * @description   查找样式含有 winopen 的元素，绑定弹窗事件。
             */
            winOpen: function () {
                $(document).off('.winopen').on('click.winopen', '.winopen,.j-winopen', function (e) {
                    var $this = $(this),
                        openid = $this.data("openid"),
                        setting = TT.parseOptions($this.data("setting"));

                    var defaults = $.extend(true, {
                        id: openid || "win",
                        url: $this.data("url") || "",
                        title: $this.data("title") || "",
                        width: $this.data("width") || "500px",
                        height: $this.data("height") || "400px",
                        lock: false
                    }, setting);

                    using("dialog", function () {
                        var artDialog = TT.openWin(defaults.url, defaults);
                        if (defaults.lock) {
                            artDialog.showModal();
                        } else {
                            artDialog.show();
                        }
                        TT.dialog.put("dialog", artDialog);
                    });

                    return false;
                });

                return this;
            },
            /**
             * 全局公共上传弹窗
             * @winUpload
             * @description
             */
            winUpload: function () {
                return this;
            },
            /**
             * 元素等高
             * @isometryDiv
             * @description     简单实现左右元素等高
             */
            isometryDiv: function (refresh) {
                var $leftDiv = $("#leftDiv");
                var $rightDiv = $("#rightDiv");

                var setting = TT.parseOptions($leftDiv.data("setting")),
                    defaults = $.extend({
                        refresh: true
                    }, {
                        refresh: refresh ? refresh : setting ? setting.refresh : true
                    });

                if (defaults.refresh) {
                    if ($rightDiv.innerHeight() > $leftDiv.innerHeight()) {
                        $leftDiv.height($rightDiv.height());
                    } else {
                        $rightDiv.height($leftDiv.height());
                    }
                }
                return this;
            },
            /**
             * 留言统计
             * @guestbookCount
             * @description     查找样式含有 guestbookCountAjax 的元素，读取配置实现统计
             */
            guestbookCount: function () {
                var guestbookCount = $(".guestbookCountAjax,.messageBoardCountAjax,.j-messageBoard-count");
                var getFun = [];
                guestbookCount.each(function (i, v) {

                    var $this = $(v),
                        setting = TT.parseOptions($this.data("setting"));

                    var defaults = $.extend(true, {
                        siteId: $this.data("siteid"),
                        columnIds: $this.data("columnids"),
                        type: $this.data("type"),
                        flag: $this.data("flag") || 0
                    }, setting);

                    if (!TT.isEmpty(defaults.siteId) && !TT.isEmpty(defaults.columnIds)) {

                        var data = {
                            siteId: defaults.siteId,
                            columnIds: defaults.columnIds
                        };

                        if (!TT.isEmpty(defaults.type)) {
                            var types = defaults.type.split(",");
                            for (var i = 0, l = types.length; i < l; i++) {
                                data[types[i]] = true;
                            }
                        }

                        var url = "";
                        if ($this.hasClass("messageBoardCountAjax")) {
                            url = "/analyse/getMessageBoard";
                        } else {
                            url = "/analyse/getGuestBook";
                        }

                        TT.ajax({
                            type: "POST",
                            data: data,
                            url: url
                        }).done(function (d) {
                            if (d && d.status == 1) {
                                var data = d.data;
                                var param = TT.url.parseQuery(this.data);
                                for (var item in param) {
                                    if (item != 'siteId' && item != 'columnIds') {
                                        $("#" + item + "_" + defaults.flag).text(data[item]);
                                    }
                                }
                            }
                        }).done(function () {
                            //清除已配置属性
                            $this.removeAttr("data-setting");
                        });
                    } else {
                        $this.attr("data-msg", "站点ID或栏目ID为空！");
                    }

                });
                return this;
            },
            /**
             * 静态页留言点击数刷新方法
             * @refreshGuestbookHit
             * @description     查找样式含有 refresh-guestbook-hit 的元素，根据默认规则实现点击数刷新
             */
            refreshGuestbookHit: function () {

                var $hitList = $(".refresh-guestbook-hit,.j-guestbook-hit");
                if ($hitList.length > 0) {

                    //查找所有id
                    var ids = $hitList.map(function () {
                        var $this = $(this);
                        return $this.data("id");
                    }).get().join(',');

                    if (!TT.isEmpty(ids)) {

                        return Ls.ajax({
                            url: "/frontGuestBook/getHit",
                            data: {ids: ids}
                        }).done(function (d) {
                            if (d.status) {
                                var data = d.data;
                                for (var i = 0; i < data.length; i++) {
                                    var d = data[i];
                                    $hitList.find("[data-id='" + d.id + "']").text(d.count);
                                }
                            } else {
                                var idArr = ids.split(",");
                                for (var i = 0, l = idArr.length; i < l; i++) {
                                    var d = idArr[i];
                                    $hitList.find("[data-id='" + d + "']").text(0);
                                }
                            }
                        });

                    } else {

                        var dfd = $.Deferred();
                        // dfd.reject($member_info);
                        dfd.resolve($memberInfo);
                        return dfd.promise();

                    }
                }
                return this;
            },
            /**
             * 初始化日期选择控件
             * @datePicker
             * @description     查找样式含有 datePicker 的元素，自动绑定日期选择控件
             */
            datePicker: function () {
                var $datePicker = $(".datePicker,.j-datePicker");
                if ($datePicker.length > 0) {
                    using("datePicker", function () {
                        $datePicker.off('click.datePicker').on('click.datePicker', function (e) {
                            var $this = $(this),
                                setting = TT.parseOptions($this.data("setting"));
                            WdatePicker(setting);
                            //清除已配置属性
                            $this.removeAttr("data-setting");
                        });
                    });
                }
                return this;
            },
            /**
             * 表单控件仿 placeholder 默认提示
             * @inputPlaceholder
             * @description     底版本浏览器仿实现 placeholder 效果
             */
            inputPlaceholder: function () {

                if (!('placeholder' in document.createElement('input'))) {

                    $('input[placeholder],textarea[placeholder]').each(function () {
                        var that = $(this),
                            text = that.attr('placeholder');
                        if (that.val() === "") {
                            that.val(text).addClass('placeholder').removeClass('active');
                        }
                        that.on('focus', function () {
                            if (that.val() === text) {
                                that.val("").addClass("active").removeClass('placeholder');
                            }
                        }).on('blur', function () {
                            if (that.val() === "") {
                                that.val(text).addClass('placeholder').removeClass('active');
                            } else {
                                that.addClass('active').removeClass('placeholder');
                            }
                        });
                    });

                    /*$(document).on('submit.placeholder', 'form', function (e) {
                        $('input[placeholder],textarea[placeholder]').each(function () {
                            var that = $(this),
                                text = that.attr('placeholder');
                            if (that.val() === text) {
                                that.val('');
                            }
                        });
                    });*/

                } else {

                    $('input[placeholder],textarea[placeholder]').each(function () {
                        var that = $(this),
                            text = that.attr('placeholder');

                        that.on('focus', function () {
                            that.addClass("active");
                        }).on('blur', function () {
                            if (that.val() === "") {
                                that.removeClass('active');
                            }
                        });
                    });

                }

                $('select').each(function () {
                    var that = $(this);
                    that.on('focus', function () {
                        if (that.val() != "") {
                            that.addClass("active");
                        }
                    }).on('change', function () {
                        if (that.val() != "") {
                            that.addClass("active");
                        } else {
                            that.removeClass('active');
                        }
                    });
                });

                return this;
            },
            /**
             * 表单验证控件
             * @formValidator
             * @description     查找当前页面所有表单，读取规则绑定
             */
            formValidator: function () {
                //如果人为禁用，则不加载
                var vd = TT.args.vd;
                if (!TT.isEmpty(vd) && vd == "false") {
                    return false;
                }
                var $form = $(document, "form");
                $form.length > 0 && using("validator");
                return this;
            },
            /**
             * 返回顶部方法
             * @gotoTop
             * @description     查找样式含有 gotoTop 的元素，绑定返回顶部方法
             */
            gotoTop: function () {
                var $gotoTop = $('.gotoTop,.j-gotoTop');
                if ($gotoTop.length > 0) {
                    $gotoTop.each(function (i, v) {

                        var $this = $(v),
                            setting = TT.parseOptions($this.data("setting"));

                        var defaults = $.extend(true, {
                            duration: 500,
                            offset: $this.data("offset") ? $this.data("offset") : $(window).height()
                        }, setting);

                        $(window).on('scroll.gotoTop', function () {
                            if ($(window).scrollTop() > defaults.offset) {
                                $this.addClass('gotop-show');
                            } else {
                                $this.removeClass('gotop-show');
                            }
                        });

                        //当点击跳转链接后，回到页面顶部位置
                        $this.off('.gotoTop').on('click.gotoTop', function (e) {
                            e.preventDefault();
                            $('body,html').animate({
                                scrollTop: 0
                            }, defaults.duration);
                            return false;
                        });

                    })
                }
                return this;
            },
            /**
             * 初始化SWF播放器
             * @swfPlayer
             * @description     查找样式含有 video-player 的元素，绑定播放器实现播放
             */
            swfPlayer: function (selector) {
                var $videoPlayer = $(selector || ".video-player,.j-video-player");
                if ($videoPlayer.length > 0) {

                    using("ckplayer", function () {

                        $videoPlayer.each(function (i, v) {
                            var $this = $(v),
                                setting = TT.parseOptions($this.data("setting"));

                            var _width = "600px",
                                _height = "440px",
                                _isPc = true;

                            if (Ls.browser.isAndroid || Ls.browser.isiPhone || Ls.browser.isiPad) {
                                _width = $(".j-fontContent").width() || $(".video-player").parent().width();
                                _height = _width / 4 * 3;
                                _isPc = false;
                            } else {
                                _width = $this.data("width") || _width;
                                _height = $this.data("height") || _height;
                            }

                            var defaults = $.extend(true, {
                                f: $this.data("url"),
                                c: 0,
                                p: 1,
                                i: "",
                                h5: TT.args.h5 == "true" ? true : false,
                                type: "",
                                iframe: {
                                    id: "video_iframe",
                                    src: "",
                                    allowtransparency: "true",
                                    frameborder: "0",
                                    width: "300",
                                    height: "36",
                                    scrolling: "no"
                                },
                                width: _width,
                                height: _height
                            }, setting);

                            defaults.iframe.src = defaults.f = defaults.f ? defaults.f : defaults.url;

                            var UUID = TT.UUID();
                            $this.attr("id", UUID).css({
                                width: defaults.width,
                                height: defaults.height,
                                "marginLeft": 'auto',
                                "marginRight": 'auto'
                            });

                            var params = {
                                bgcolor: '#FFF',
                                allowFullScreen: true,
                                allowScriptAccess: 'always',
                                wmode: 'transparent'
                            };

                            var ext = (defaults.f + "").toLowerCase();

                            if (/\.wmv/.test(ext)) {
                                $this.html('<embed width="' + defaults.width + '" height="' + defaults.height + '" border="0" autostart="ture" loop="true" src="' + defaults.f + '"></embed>');
                            } else if (/\.swf/.test(ext)) {
                                var swf = '' +
                                    '<object classid="clsid:D27CDB6E-AE6D-11cf-96B8-444553540000" codebase="http://download.macromedia.com/pub/shockwave/cabs/flash/swflash.cab#version=9,0,16,0" width="' + defaults.width + ' height="' + defaults.height + '">' +
                                    '   <param name="movie" value="' + defaults.f + '">' +
                                    '   <param name="quality" value="high">' +
                                    '   <param name="play" value="true">' +
                                    '   <param name="loop" value="false">' +
                                    '   <embed src="' + defaults.f + '" width="' + defaults.width + '" height="' + defaults.height + '" play="true" loop="false" quality="high" pluginspage="http://www.macromedia.com/go/getflashplayer" type="application/x-shockwave-flash">' +
                                    '   </embed>' +
                                    '</object>'

                                $this.html(swf)
                            } else {
                                if (defaults.type == "iframe") {
                                    var html = $("<iframe>", defaults.iframe);
                                    $this.html(html)
                                } else {
                                    delete defaults.iframe;

                                    // IE9 下对视频编码严格，导致一些视频无法采用H5播放；
                                    if (TT.browser.isIE9OrLower) {
                                        defaults.h5 = false;
                                    }

                                    if (defaults.h5 && (/\.mp4/.test(ext))) {
                                        var video = [defaults.f + '->video/mp4'];
                                        CKobject.embed('/assets/js/ckplayer/ckplayer.swf', UUID, 'video_' + UUID, defaults.width, defaults.height, true, defaults, video, params);
                                    } else {
                                        CKobject.embedSWF('/assets/js/ckplayer/ckplayer.swf', UUID, 'video_' + UUID, defaults.width, defaults.height, defaults, params);
                                    }
                                }
                            }

                            //清除已配置属性
                            $this.removeAttr("data-setting");
                        });

                    })
                }
                return this;
            },
            /**
             * myfocus 初始化图片轮播插件
             * @myfocus
             * @description     查找样式含有 myFocus 的元素，绑定图片轮播插件
             */
            myfocus: function () {
                var $myFocus = $(".myFocus,.j-focus");
                if ($myFocus.length > 0) {
                    using("myfocus", function () {
                        $myFocus.each(function (i, v) {
                            var $this = $(v),
                                setting = TT.parseOptions($this.data("setting"));
                            myFocus.set(setting);
                            $this.removeAttr("data-setting").removeClass("myLoading");
                        })
                    })
                }
                return this;
            },
            /**
             * myMarquee    无缝滚动插件
             * @param   selector -   自定元素，必须符合JQ选择器
             * @param   refresh
             * @description 参考网址：http://www.popub.net/script/MSClass.html
             */
            myMarquee: function (selector, refresh) {
                var $myMarquee = $(selector || ".myMarquee:visible,.j-marquee:visible");
                if ($myMarquee.length > 0) {

                    var marqueeLeft = function ($this, $marquee1, $marquee2) {
                        if ($marquee2.offsetWidth - $this.scrollLeft <= 0) {
                            $this.scrollLeft -= $marquee1.offsetWidth;
                        } else {
                            $this.scrollLeft++;
                        }
                    }

                    var interval = {};

                    $myMarquee.each(function (i, v) {
                        var $this = $(v),
                            setting = Ls.parseOptions($this.data("setting"));

                        var defaults = $.extend(true, {
                            simple: false,
                            speed: 45,
                            limit: false,
                            ver: "v1"
                        }, setting);

                        if (defaults.ver == "v2") {

                            if (!$this.data("intervalMarquee") || refresh) {
                                using("marqueeV2", function () {
                                    $this.liMarquee(defaults);
                                });
                            }

                            //清除已配置的属性,设置初始属性
                            $this.removeAttr("data-setting").data("intervalMarquee", true);

                            return true;
                        }

                        if (defaults.simple && (!$this.data("intervalMarquee") || refresh)) {

                            var thisWidth = $this.parent().width(),
                                contentWidth = $this.find(".marquee1").children().outerWidth();

                            // 开启判断，如果内容宽度小于主宽度，不滚动
                            if (defaults.limit && contentWidth < thisWidth) {
                                return true;
                            }

                            //手工刷新清空所有定时器
                            var dataInterval = $this.data("intervalMarquee");
                            clearInterval(dataInterval);

                            var UUID = Ls.UUID(),
                                marquee1 = $this.find(".marquee1"),
                                marquee2 = $this.find(".marquee2");

                            marquee2[0].innerHTML = marquee1[0].innerHTML;

                            interval[UUID] = setInterval(function () {
                                marqueeLeft($this[0], marquee1[0], marquee2[0])
                            }, defaults.speed);

                            $this.off('.marquee').on('mouseenter.marquee', function (e) {
                                clearInterval(interval[UUID])
                            }).on('mouseleave.marquee', function (e) {
                                interval[UUID] = setInterval(function () {
                                    marqueeLeft($this[0], marquee1[0], marquee2[0])
                                }, defaults.speed);
                                $this.data("intervalMarquee", interval[UUID]);
                            }).data("intervalMarquee", interval[UUID]);

                        } else {
                            if (!$this.data("intervalMarquee") || refresh) {
                                var thisWidth = $this.width(),
                                    thisHeight = $this.height(),
                                    $li = $this.find("li");

                                if (defaults.limit) {

                                    // 上下滚动
                                    if (defaults.Direction == "top" || defaults.Direction == "bottom") {
                                        var thisContentHeight = $li.length * $li.eq(0).outerHeight(true);
                                        if (defaults.limit && thisContentHeight <= thisHeight) {
                                            return true;
                                        }
                                    }

                                    //左右滚动
                                    if (defaults.Direction == "left" || defaults.Direction == "right") {
                                        var thisContentWidth = $li.length * $li.eq(0).outerWidth(true);
                                        if (defaults.limit && thisContentWidth <= thisWidth) {
                                            return true;
                                        }
                                    }

                                }

                                using("MSClass", function () {
                                    new Marquee(defaults);
                                });

                                //清除已配置的属性,设置初始属性
                                $this.removeAttr("data-setting").data("intervalMarquee", true);
                            }
                        }
                    })

                }
                return this;
            },
            /**
             * tabs 页签方法
             * @description
             */
            tabs: function () {

                var cls = this,
                    Tab = function (element) {
                        this.element = $(element);
                    };

                Tab.prototype.show = function () {
                    var $this = this.element;
                    var $ul = $this.closest('ul');
                    var selector = $this.data('target');

                    if (!selector) {
                        selector = $this.data('id') || $this.attr('href');
                        selector = selector && selector.replace(/.*(?=#[^\s]*$)/, '');
                    }

                    //if ($this.parent('li').hasClass('active') && !$this.parent('li').hasClass('reload')) return;

                    var $previous = $ul.find('.active:last a');
                    var hideEvent = $.Event('hide.ls.tab', {
                        relatedTarget: $this[0]
                    });

                    var showEvent = $.Event('show.ls.tab', {
                        relatedTarget: $previous[0]
                    });

                    $previous.trigger(hideEvent);
                    $this.trigger(showEvent);

                    if (showEvent.isDefaultPrevented() || hideEvent.isDefaultPrevented()) return;

                    var $target = $(selector);

                    this.activate($this.closest('li'), $ul);
                    this.activate($target, $target.parent(), function () {
                        $previous.trigger({
                            type: 'hidden.ls.tab',
                            relatedTarget: $this[0]
                        });
                        $this.trigger({
                            type: 'shown.ls.tab',
                            relatedTarget: $previous[0]
                        });

                        var tabsInit = $this.data("tabsInit");
                        if (!tabsInit && $target.find(".marquee-move").length == 0) {
                            $this.data("tabsInit", true);
                            var $myMarquee = $target.find(".myMarquee,.j-marquee");
                            //触发滚动，解决tab页签下不能滚动的问题
                            $myMarquee.length > 0 && cls.myMarquee($myMarquee);
                        }
                    })
                };

                Tab.prototype.activate = function (element, container, callback) {
                    var $active = container.find('> .active');
                    $active.removeClass('active');

                    //更多链接按扭
                    var $a = element.find('[data-toggle]'),
                        url = $a.data("url"),
                        more = $a.data("more"),
                        href = $a.attr("href");

                    if (!Ls.isEmpty(more)) {
                        $(more).attr("href", url ? url : href.replace(/(#[^\s]*$)/, ''));
                    }

                    element.addClass('active');
                    callback && callback();
                };

                function Plugin(option) {
                    return this.each(function () {
                        var $this = $(this);
                        var data = $this.data('ls.tab');
                        if (!data) $this.data('ls.tab', (data = new Tab(this)));
                        if (typeof option == 'string') data[option]();
                    })
                };

                var clickHandler = function (e) {
                    e.preventDefault();
                    Plugin.call($(this), 'show');
                };

                $(document)
                    .on('click.ls.tab', '[data-toggle="tab"]', clickHandler)
                    .on('mouseover.ls.tab', '[data-toggle="mouse"]', {type: 'mouse'}, clickHandler);

                return this;
            },
            /**
             * 飘浮广告插件
             * @param   myFloat
             * @description
             */
            myFloat: function (selector) {
                var $myFloat = $(selector || ".myFloat,.j-float");
                if ($myFloat.length > 0) {

                    var floatConfig = {
                        IsInitialized: false,
                        scrollX: 0,
                        scrollY: 0,
                        moveWidth: 0,
                        moveHeight: 0,
                        interval: {},
                        getPageSize: function () {
                            var windowWidth = $(window).width(),
                                windowHeight = $(window).height(),
                                pageWidth = $(document).width(),
                                pageHeight = $(document).height();
                            return new Array(pageWidth, pageHeight, windowWidth, windowHeight);
                        },
                        getPageScroll: function () {
                            var scrollLeft = $(document).scrollLeft(),
                                scrollTop = $(document).scrollTop();
                            return new Array(scrollLeft, scrollTop);
                        },
                        Resize: function () {
                            var winsize = floatConfig.getPageSize();
                            floatConfig.moveWidth = winsize[2];
                            floatConfig.moveHeight = winsize[3];
                            floatConfig.Scroll();
                        },
                        Scroll: function () {
                            var winscroll = floatConfig.getPageScroll();
                            floatConfig.scrollX = winscroll[0];
                            floatConfig.scrollY = winscroll[1];
                        }
                    };

                    function floatMove(elem) {
                        if (!floatConfig.IsInitialized) {
                            floatConfig.Resize();
                            floatConfig.IsInitialized = true;
                        }

                        var outerWidth = elem.outerWidth(),
                            outerHeight = elem.outerHeight();

                        var _this = this,
                            W = outerWidth == 0 ? floatConfig.moveWidth * 0.5 : (floatConfig.moveWidth - outerWidth), //若漂浮广告元素尚未加载完，则调整元素出现的位置在屏幕可视区
                            H = floatConfig.moveHeight - outerHeight,
                            x = W * Math.random(),
                            y = H * Math.random(),
                            rad = (Math.random() + 1) * Math.PI / 6,
                            kx = Math.sin(rad),
                            ky = Math.cos(rad),
                            dirx = (Math.random() < 0.5 ? 1 : -1),
                            diry = (Math.random() < 0.5 ? 1 : -1),
                            step = 1;

                        _this.setLocation = function (vx, vy) {
                            x = vx;
                            y = vy;
                            return _this;
                        };

                        _this.setDirection = function (vx, vy) {
                            dirx = vx;
                            diry = vy;
                            return _this;
                        };

                        _this.method = function () {

                            elem.css({
                                position: "absolute",
                                top: (y + floatConfig.scrollY),
                                left: (x + floatConfig.scrollX)
                            }).show();

                            var elemWidth = elem.outerWidth(),  //重新获取加载完后的元素宽高
                                elemHeight = elem.outerHeight();

                            rad = (Math.random() + 1) * Math.PI / 6;
                            W = floatConfig.moveWidth - elemWidth;
                            H = floatConfig.moveHeight - elemHeight;
                            x = x + step * kx * dirx;
                            if (x < 0) {
                                dirx = 1;
                                x = 0;
                                kx = Math.sin(rad);
                                ky = Math.cos(rad);
                            }
                            if (x > W) {
                                dirx = -1;
                                x = W;
                                kx = Math.sin(rad);
                                ky = Math.cos(rad);
                            }
                            y = y + step * ky * diry;
                            if (y < 0) {
                                diry = 1;
                                y = 0;
                                kx = Math.sin(rad);
                                ky = Math.cos(rad);
                            }
                            if (y > H) {
                                diry = -1;
                                y = H;
                                kx = Math.sin(rad);
                                ky = Math.cos(rad);
                            }
                        };

                        _this.Run = function (options, uuid) {
                            var defaults = {
                                    delay: 25
                                },
                                setting = $.extend(defaults, options);
                            var uuid = TT.UUID();
                            floatConfig.interval[uuid] = setInterval(_this.method, setting.delay);
                            elem.off('.float').on('mouseenter.float', function () {
                                clearInterval(floatConfig.interval[uuid]);
                            }).on('mouseleave.float', function () {
                                floatConfig.interval[uuid] = setInterval(_this.method, setting.delay);
                            }).data("UUID", uuid);
                        }
                    }

                    $myFloat.each(function (i, v) {
                        var $this = $(v),
                            setting = TT.parseOptions($this.data("setting"));
                        if ($this.find("li").length > 0) {
                            (new floatMove($this)).Run(setting);
                        } else {
                            $this.remove();
                        }
                        //清除已配置的属性
                        $this.removeAttr("data-setting");
                    });

                    $(document).off('.float').on('resize.float', function () {
                        floatConfig.Resize();
                    }).on('scroll.float', function () {
                        floatConfig.Scroll();
                    }).on('click.float', '.float-close', function () {
                        var $this = $(this).parents(".myFloat,.j-float"),
                            uuid = $this.data("UUID");
                        $this.remove();
                        clearInterval(floatConfig.interval[uuid]);
                    });
                }
                return this;
            },
            /**
             * myCouplet    对联广告插件
             * @param   myCouplet
             * @description
             */
            myCouplet: function (selector) {
                var $myCouplet = $(selector || ".myCouplet,.j-couplet");
                if ($myCouplet.length > 0) {
                    $myCouplet.each(function (i, v) {
                        var $this = $(v);

                        if ($this.find("li").length > 0) {

                            var setting = TT.parseOptions($this.data("setting"));

                            var defaults = $.extend(true, {
                                isAnimate: false,
                                topDistance: 200,
                                screenWidth: 1024
                            }, setting);

                            var screen_w = screen.width;
                            if (screen_w > defaults.screenWidth) {
                                $this.show();
                            }

                            if (Ls.isIE6 || defaults.isAnimate) {
                                $(window).scroll(function () {
                                    var scrollTop = $(window).scrollTop();
                                    $this.stop().animate({
                                        top: scrollTop + defaults.topDistance
                                    });
                                });
                            }

                            $this.off('.couplet').on('click.couplet', '.couplet-close', function () {
                                $this.remove();
                                return false;
                            });
                        } else {
                            $this.remove();
                        }

                        //清除已配置的属性,设置初始属性
                        $this.removeAttr("data-setting").data("isInit", true);
                    })
                }
                return this;
            },
            /**
             * myLinks  友情链接
             * @param   myLinks
             * @description
             */
            myLinks: function () {
                var $myLinks = $(".myLinks,.j-links");
                if ($myLinks.length > 0) {

                    $myLinks.off('.links').on('click.links', 'dt', function (e) {
                        var $this = $(this),
                            $dd = $this.next(),
                            setting = Ls.parseOptions($this.data("setting")),
                            defaults = $.extend(true, {
                                showAnimate: "slideDown",
                                hideAnimate: "slideUp",
                                speed: 200,
                                show: function (ele) {
                                    ele && ele[this.showAnimate](this.speed);
                                },
                                hide: function (ele) {
                                    ele && ele[this.hideAnimate](this.speed);
                                }
                            }, setting);

                        if ($this.hasClass("active")) {
                            return false;
                        }

                        //上一次显示的元素
                        var $active = $myLinks.find(".active").removeClass("active").next();
                        defaults.hide($active);
                        //显示当前点前元素
                        defaults.show($dd);

                        //添加焦点事件
                        $("body").off('click.mylinks').on('click.mylinks', $myLinks, function (e) {
                            if ($(e.target).parents("dl").length == 0) {
                                //上一次显示的元素
                                var $active = $myLinks.find(".active").removeClass("active").next();
                                //隐藏上一次显示的元素
                                setTimeout(function () {
                                    defaults.hide($active);
                                }, 150)
                            }
                        });

                        /* //添加焦点事件
                         $dd.find("a:first").focus().one('blur.links', function () {
                         //上一次显示的元素
                         var $active = $myLinks.find(".active").removeClass("active").next();

                         //隐藏上一次显示的元素
                         setTimeout(function () {
                         defaults.hide($active);
                         }, 150)

                         });*/

                        //添加样式
                        $this.addClass("active");

                    })
                }
                return this;
            },
            /**
             * myWza    无障碍浏览插件
             * @param   myWza
             * @description
             */
            myWza: function () {
                var $myWza = $(".myWza,.j-wza");
                if ($myWza.length > 0) {
                    using("wza", function () {
                        $myWza.off('click.myWza').on('click.myWza', function (e) {
                            e.preventDefault();
                            wza.openNav();
                            $(window).scrollTop(0);
                        });
                        wza.onload();
                    });
                }
                return this;
            },
            /**
             * myLeadInfo   领导姓名相关的信息
             * @description
             */
            myLeadInfo: function () {
                var cls = Ls,
                    $myLeadName = $("#myLeadName"),
                    $myLeadInfoList = $("#myLeadInfoList");

                if ($myLeadName.length == 1) {

                    var setting = cls.parseOptions($myLeadInfoList.data("setting"));
                    var defaults = $.extend(true, {
                        tplId: "lead_info_tpl",
                        siteId: "",
                        columnIds: "",
                        pageSize: 16,
                        keywords: ($myLeadName.text() + "").replace(/[\s]+/gi, "").replace(/　/gi, ""),
                        length: 50,
                        dateFormat: "MM-dd",
                        target: "_blank",
                        islight: false,
                        isJson: true,
                        callback: ""
                    }, setting);

                    //如果页面上定义了模板，取页自定义的模板
                    var $lead_info_tpl = $("#" + defaults.tplId);
                    if ($lead_info_tpl.length == 0) {
                        var lead_list_tpl = cls.compile(
                            '<? if(data && data.length>0){?>' +
                            '<?' +
                            'for (var i=0,l=data.length; i<l; i++) {' +
                            'var el = data[i];' +
                            '?>' +
                            '    <li>' +
                            '        <span class="right"><?=Ls.dateFormat(el.createDate,dateFormat)?></span>' +
                            '        <a class="left" href="<?=el.link?>" target="<?=target?>" title="<?=el.title?>"><?=Ls.cutstr(el.title,length)?></a>' +
                            '    </li>' +
                            '<?}?>' +
                            '<?}?>'
                        );
                    }

                    cls.ajax({
                        dataType: "JSON",
                        data: Ls.toJSON(defaults),
                        url: "/site/label/481565"
                    }).done(function (d) {
                        var listHtml = "";
                        if (cls.isArray(d) && d.length > 0) {
                            var data = {
                                data: d,
                                length: defaults.length,
                                dateFormat: defaults.dateFormat
                            };
                            if ($lead_info_tpl.length == 0) {
                                listHtml = lead_list_tpl(data);
                            } else {
                                listHtml = cls.template(defaults.tplId, data);
                            }

                            listHtml && $myLeadInfoList.html(listHtml);
                        } else {
                            $myLeadInfoList.html("\u6682\u65e0\u76f8\u5173\u4fe1\u606f");
                        }
                        defaults.callback && defaults.callback.call(cls, d, listHtml);
                    }).done(function () {
                        //清除已配置属性
                        $myLeadName.removeAttr("data-setting");
                        $myLeadInfoList.removeAttr("data-setting");
                    })
                }
                return cls;
            },
            /**
             * myLeadInfo2   领导姓名相关的信息
             * @description
             */
            myLeadInfo2: function (selector) {
                var cls = Ls,
                    $myLeadName = $(selector || ".j-lead-name");

                if ($myLeadName.length > 0) {

                    $myLeadName.each(function (i, v) {
                        var $this = $(v),
                            setting = cls.parseOptions($this.data("setting")),
                            defaults = $.extend(true, {
                                infoId: "",
                                tplId: "lead_info_tpl",
                                siteId: "",
                                columnIds: "",
                                pageSize: 16,
                                keywords: ($this.text() + "").replace(/[\s]+/gi, "").replace(/　/gi, ""),
                                length: 50,
                                dateFormat: "MM-dd",
                                target: "_blank",
                                islight: false,
                                isJson: true,
                                callback: ""
                            }, setting);

                        var $infoList = "";
                        if (defaults.infoId) {
                            $infoList = $("#" + defaults.infoId);
                        }

                        //如果页面上定义了模板，取页自定义的模板
                        var $lead_info_tpl = $("#" + defaults.tplId);
                        if ($lead_info_tpl.length == 0) {
                            var lead_list_tpl = cls.compile(
                                '<? if(data && data.length>0){?>' +
                                '<?' +
                                'for (var i=0,l=data.length; i<l; i++) {' +
                                'var el = data[i];' +
                                '?>' +
                                '    <li>' +
                                '        <span class="right"><?=Ls.dateFormat(el.createDate,dateFormat)?></span>' +
                                '        <a class="left" href="<?=el.link?>" target="<?=target?>" title="<?=el.title?>"><?=Ls.cutstr(el.title,length)?></a>' +
                                '    </li>' +
                                '<?}?>' +
                                '<?}?>'
                            );
                        }

                        cls.ajax({
                            dataType: "JSON",
                            data: TT.toJSON(defaults),
                            url: "/site/label/481565"
                        }).done(function (d) {
                            var listHtml = "";
                            if (cls.isArray(d) && d.length > 0) {
                                var data = {
                                    data: d,
                                    length: defaults.length,
                                    dateFormat: defaults.dateFormat
                                };
                                if ($lead_info_tpl.length == 0) {
                                    listHtml = lead_list_tpl(data);
                                } else {
                                    listHtml = cls.template(defaults.tplId, data);
                                }
                                $infoList && $infoList.html(listHtml);
                            } else {
                                $infoList && $infoList.html("\u6682\u65e0\u76f8\u5173\u4fe1\u606f");
                            }
                            defaults.callback && defaults.callback(cls, d, listHtml);
                        }).done(function () {
                            //清除已配置属性
                            $myLeadName.removeAttr("data-setting");
                        })

                    })
                }
                return cls;
            },
            /**
             * 会员退出方法
             * @param   memberLogout
             * @description
             */
            memberLogout: function () {
                return TT.ajax({url: "/member/logout"}).done(function () {
                    window.location = window.location;
                });
            },
            /**
             * 检测会员是否登录
             * @param   memberLoginCheck
             * @description
             */
            memberLoginCheck: function (selector) {

                var $memberInfo = $(selector || "#member_info");
                if ($memberInfo.length == 1) {

                    var setting = TT.parseOptions($memberInfo.data("setting"));
                    var defaults = $.extend(true, {
                        siteId: ""
                    }, setting);

                    //如果页面上定义了模板，取页自定义的模板
                    var $member_Info_tpl = $("#member_Info_tpl");
                    if ($member_Info_tpl.length == 0) {
                        // 登录后模板
                        var member_Info_tpl = TT.compile(
                            '<a href="javascript:void(0)" class="member-logout">[\u9000\u51fa]</a>' +
                            '<a href="/member/center/<?=siteId?>?s=<?=random?>" class="user-name"><?=userName?></a>'
                        );
                    }

                    var $member_register_tpl = $("#member_register_tpl");
                    if ($member_register_tpl.length == 0) {
                        // 未登录后模板
                        var member_register_tpl = TT.compile(
                            '<a href="/member/login/<?=siteId?>" class="member-login">\u767b\u5f55</a>' +
                            '<a href="/member/register/<?=siteId?>" class="member-register">\u6ce8\u518c</a>'
                        );
                    }

                    return TT.ajax({url: "/member/isLogin"}).done(function (d) {
                        var data = d.data || {};
                        data.siteId = defaults.siteId;
                        data.random = Math.random();
                        if (d.status == 1) {
                            var html = "";

                            if ($member_Info_tpl.length == 0) {
                                html = member_Info_tpl(data);
                            } else {
                                html = TT.template("member_Info_tpl", data);
                            }

                            $("#member_info").append(html).on("click", ".member-logout", function () {
                                os.www.memberLogout();
                            });

                            var member = {
                                userId: data.userId,
                                userName: data.userName,
                                phone: data.phone
                            };

                            _win.member = member;
                        } else {

                            var html = "";
                            if ($member_register_tpl.length == 0) {
                                html = member_register_tpl(data);
                            } else {
                                html = TT.template("member_register_tpl", data);
                            }
                            $("#member_info").append(html);

                        }
                        return false;
                    }).done(function () {
                        //清除已配置属性
                        $memberInfo.removeAttr("data-setting");
                    });

                } else {

                    var dfd = $.Deferred();
                    // dfd.reject($member_info);
                    dfd.resolve($memberInfo);
                    return dfd.promise();

                }
                return this;
            },
            /**
             * 标签动态请求方法
             * @param   getLabelList
             * @description
             */
            getLabelList: function (selector) {
                var _this, $labelList = $(selector || ".mylabel-list,.j-label-list");
                if ($labelList.length > 0) {

                    var queue = new Ls.asyncQueue();
                    $labelList.each(function (i, v) {
                        var $this = $(v),
                            setting = TT.parseOptions($this.data("setting")),
                            defaults = $.extend(true, {
                                id: "",
                                siteId: "",
                                url: '/site/label/8888',
                                labelName: "",
                                typeCode: "",
                                length: 50,
                                dateFormat: "MM-dd",
                                target: "_blank",
                                callback: "",
                                dataType: "HTML"
                            }, setting);

                        //兼容老版本
                        if (!Ls.isEmpty(defaults.typeCode)) {
                            if (defaults.typeCode == "guestBook") {
                                defaults.labelName = 'guestBookList';
                            } else if (defaults.typeCode == "publicInfo") {
                                defaults.labelName = 'publicInfoList';
                            } else if (defaults.typeCode == "netWork") {
                                defaults.labelName = 'onlineNavItemByUnit';
                            }
                        }

                        if (defaults.labelName == 'keyWordsHeatRank') {
                            var wordsHeatRank = function ($el, d) {
                                var data = $.parseJSON(d);
                                if (data && data.list && data.list.length) {
                                    var arr = [];
                                    for (var i = 0, l = data.list.length; i < l; i++) {
                                        var item = data.list[i];
                                        arr.push('<li><a href="/site/search/' + item.siteId + '?keywords=' + encodeURIComponent(item.keyWords) + '" title="' + item.keyWords + '" target="_blank">' + item.keyWords + '</a></li>')
                                    }
                                    $el.html(arr.join(''));
                                }
                            }

                            defaults.callback = wordsHeatRank;
                        }

                        //添加队列
                        defaults.url && queue.add(function () {
                            TT.ajax({
                                dataType: defaults.dataType,
                                data: TT.toJSON(defaults),
                                url: defaults.url
                            }).done(function (d) {
                                if (defaults.callback) {
                                    defaults.callback.call(_this, $this, d);
                                } else {
                                    $this.html(d);
                                }
                                //清除已配置属性
                                $this.removeAttr("data-setting");
                            });
                        });

                    });
                    queue.run();
                }
            },
            /**
             * 获取国务院信息和安徽省政府信息
             * @getRemoteInfo
             * @description
             */
            getRemoteInfo: function (selector) {
                var $remoteInfo = $(selector || ".remote-info");
                if ($remoteInfo.length != 0) {
                    var queue = new Ls.asyncQueue();
                    $remoteInfo.each(function (i, v) {
                        var $this = $(v),
                            setting = Ls.parseOptions($this.data("setting"));
                        var defaults = $.extend(true, {
                            infoId: "remote_info_tpl",
                            url: "",
                            rows: 10,
                            length: 25,
                            dateFormat: "MM-dd",
                            target: "_blank",
                            type: 1,
                            callback: "",
                            data: "",
                            jsonp: "callback",
                            jsonpCallback: "",
                            renderer: true
                        }, setting);

                        var jsonpName = "";
                        var $remote_info_tpl = $("#" + defaults.infoId);
                        //1、国务院信息   2、省政府信息
                        if (defaults.type == 1) {
                            defaults.url = 'http://www.gov.cn/pushinfo/v150203/pushinfo.jsonp';

                            // json 回调名称
                            defaults.jsonpCallback = "pushInfoJsonpCallBack";

                            // 模板
                            var list_tpl = Ls.compile(
                                '<?' +
                                'var css = "";' +
                                'for (var i=0; i<rows; i++) {' +
                                '   var el = data[i];' +
                                '   css = "odd";' +
                                '   if (i % 2 == 1) {' +
                                '       css = "even";' +
                                '   }' +
                                '?>' +
                                '    <li class="<?=css?>">' +
                                '        <span class="date right"><?=Ls.dateFormat(el.pubDate,dateFormat)?></span>' +
                                '        <a href="<?=el.link?>" target="<?=target?>" title="<?=el.title?>"><?=Ls.cutstr(el.title,length)?></a>' +
                                '    </li>' +
                                '<?}?>'
                            );
                        } else if (defaults.type == 2) {
                            defaults.url = 'http://www.ah.gov.cn/pushinfo/pushinfojson.html';

                            // json 回调名称
                            defaults.jsonpCallback = "pushInfoCallBack";

                            // 模板
                            var list_tpl = Ls.compile(
                                '<?' +
                                'var css = "";' +
                                'for (var i=0; i<rows; i++) {' +
                                '   var el = data[i];' +
                                '   css = "odd";' +
                                '   if (i % 2 == 1) {' +
                                '       css = "even";' +
                                '   }' +
                                '?>' +
                                '    <li class="<?=css?>">' +
                                '        <span class="date right"><?=Ls.dateFormat(el.adddate,dateFormat)?></span>' +
                                '        <a href="<?=el.link?>" target="<?=target?>" title="<?=el.title?>"><?=Ls.cutstr(el.title,length)?></a>' +
                                '    </li>' +
                                '<?}?>'
                            );
                        }

                        //添加队列
                        queue.add(function () {
                            Ls.ajax({
                                url: defaults.url,
                                type: 'get',
                                cache: false,
                                dataType: "jsonp",
                                jsonp: defaults.jsonp,
                                jsonpCallback: defaults.jsonpCallback,
                                data: TT.toJSON(defaults)
                            }).done(function (data) {
                                if (!Ls.isEmpty(data)) {
                                    if (defaults.type == 2) {
                                        data = data.data;
                                    }
                                    var rows = data.length;
                                    if (rows && rows > defaults.rows) {
                                        rows = defaults.rows;
                                    }
                                    defaults.rows = rows || 0;
                                    defaults.data = data;
                                    var html = "";

                                    if (defaults.type == 1 || defaults.type == 2) {
                                        if ($remote_info_tpl.length == 0) {
                                            html = list_tpl(defaults);
                                        } else {
                                            html = TT.template(defaults.infoId, defaults);
                                        }
                                    }

                                    defaults.renderer && $this.html(html);

                                    if (defaults.callback) {
                                        defaults.callback.call(this, data, defaults, $this, html);
                                    }

                                    // 回调
                                    Ls.www.ajaxInit();
                                }
                            });
                        })
                    });
                    queue.run();
                }
                return this;
            },
            /**
             * 获取本系统远程信息公开信息
             * @getJsonpInfo
             * @description
             */
            getJsonpInfo: function (selector) {

                var $jsonpInfo = $(selector || ".jsonp-info");
                if ($jsonpInfo.length != 0) {

                    $jsonpInfo.each(function (i, v) {
                        var $this = $(v),
                            setting = TT.parseOptions($this.data("setting"));
                        var defaults = $.extend(true, {
                            url: "",
                            dateFormat: "MM-dd",
                            target: "_blank",
                            length: 25,
                            callback: "",
                            type: 1,
                            tplId: "",
                            jsonp: "jsonpCallback",
                            jsonpCallback: "jsonpCallback"
                        }, setting);

                        var list_tpl = TT.compile(
                            '<?' +
                            'var css = "";' +
                            'for (var i=0; i<data; i++) {' +
                            '   var el = data[i];' +
                            '   css = "odd";' +
                            '   if (i % 2 == 1) {' +
                            '       css = "even";' +
                            '   }' +
                            '?>' +
                            '    <li class="<?=css?>">' +
                            '        <a href="<?=el.link?>" target="<?=target?>" title="<?=el.title?>"><?=Ls.cutstr(el.title,length)?></a>' +
                            '        <span class="right"><?=Ls.dateFormat(el.publishDate,dateFormat)?></span>' +
                            '    </li>' +
                            '<?}?>'
                        );

                        defaults.url && Ls.ajax({
                            url: defaults.url,
                            dataType: "jsonp",
                            jsonp: defaults.jsonp,
                            jsonpCallback: defaults.jsonpCallback,
                            data: Ls.toJSON(defaults)
                        }).done(function (data) {
                            defaults.data = data;
                            if (defaults.callback) {
                                defaults.callback.call(this, defaults);
                            } else {
                                var html = "";
                                if (defaults.type == 1) {
                                    html = list_tpl(defaults);
                                } else {
                                    html = TT.template(defaults.tplId, defaults);
                                }
                                $this.html(html).removeAttr("data-setting");
                            }
                        });
                    })
                }
                return this;
            },
            /**
             * 二维码生成
             * @qrcode
             * @description   生成二维码图片
             */
            qrcode: function () {
                var $qrcode = $(".qrcode,.j-qrcode");
                if ($qrcode.length == 1) {

                    var setting = Ls.parseOptions($qrcode.data("setting")),
                        defaults = $.extend(true, {
                            render: "table", //table方式
                            width: 160, //宽度
                            height: 160, //高度
                            codeType: "m",
                            text: Ls.getLocation.fullUrl
                        }, setting);

                    if (defaults.codeType == "m") {
                        if (defaults.text.indexOf(".html") != -1) {
                            var url = defaults.text.substring(defaults.text.lastIndexOf("/") + 1);
                            defaults.text = Ls.getLocation.url + "/wap/content/article/" + url.substring(0, url.indexOf("."));
                        } else if (defaults.text.indexOf("/article/") != -1) {
                            defaults.text = Ls.getLocation.url + "/wap" + location.pathname;
                        }
                    }

                    // 小于等于IE8设置容器的大小
                    if (Ls.browser.isIE8OrLower) {
                        $qrcode.width(defaults.width).height(defaults.height);
                    }

                    //初始化
                    using("qrcode", function () {
                        var qrcode = new QRCode($qrcode.get(0), defaults);
                    });
                }
                return this;
            },
            /**
             * 模拟滚动条
             * @param selector
             * @description
             */
            scrollBar: function (selector) {
                var $scrollbar = $(selector || ".j-scrollbar");
                if ($scrollbar.length > 0) {
                    $scrollbar.each(function (i, v) {
                        using('slimscroll', function () {
                            // defaults
                        })
                    });
                }
                return this;
            },
            /**
             * 无缝滚动
             * @scroll
             * @description
             */
            scroll: function (selector) {
                var $scroll = $(selector || ".ls-scroll,.j-scroll");
                if ($scroll.length > 0) {
                    var interval = {};
                    $scroll.each(function (i, v) {

                        //解决按钮快速点击时导致图片选中高亮显示
                        $scroll.find('.ind_ztlist').before('<div style="height:1px;"></div>');

                        var $this = $(v),
                            UUID = Ls.UUID(),
                            setting = Ls.parseOptions($this.data("setting"));

                        var defaults = $.extend(true, {
                            autoScroll: true,
                            ver: 'v1',
                            customWid: false
                        }, setting);

                        if (defaults.ver == "v1") {

                            if (!defaults.speed) {
                                defaults.speed = 3;
                            }

                            var $ul = $this.find('.scroll-body'),
                                $li = $ul.find('li'),
                                parentWid = $ul.parent().width(),
                                liNum = $li.length;

                            var ulWid = 0;
                            $.each($li, function (i, v) {
                                var el = $(v);
                                ulWid += el.outerWidth(true);
                            });

                            $ul.width(ulWid);

                            var next = function (e) {
                                $ul.find('li').eq(0).appendTo($ul);
                                var liWidth = $ul.find('li').eq(1).outerWidth(true);
                                $ul.css({'margin-left': +liWidth}).stop(true, true).animate({'margin-left': 0});
                            }

                            var prev = function (e) {
                                $ul.find('li:last').prependTo($ul);
                                var liWidth = $ul.find('li:last').prev().outerWidth(true);
                                $ul.css({'margin-left': -liWidth}).stop(true, true).animate({'margin-left': 0});
                            }

                            //关闭事件
                            $this.off('.scroll');
                            //下移
                            $this.on('click.scroll.next', '.next', next);
                            ///上移
                            $this.on('click.scroll.prev', '.prev', prev);

                            //自动滚动
                            if (defaults.autoScroll) {

                                //手工刷新清空所有定时器
                                var dataInterval = $this.data("intervalScroll");
                                clearInterval(dataInterval);

                                interval[UUID] = setInterval(function () {
                                    next();
                                }, defaults.speed * 1000);

                                $this.on('mouseenter.scroll', function () {
                                    clearInterval(interval[UUID]);
                                }).on('mouseleave.scroll', function () {
                                    interval[UUID] = setInterval(function () {
                                        next();
                                    }, defaults.speed * 1000);
                                    $this.data("intervalScroll", interval[UUID]);
                                }).data("intervalScroll", interval[UUID]);
                            }
                        } else if (defaults.ver == "v2") {
                            using('scroll', function () {
                                $this.cxScroll(defaults);
                            })
                        }
                    });
                }
                return this;
            },
            /**
             * fontSize 字体大小
             * @description
             */
            fontSize: function () {
                $(document).off('.ls.fontsize').on('click.ls.fontsize', '.j-fontBig,.j-fontNormal,.j-fontSmall', function (e) {
                    var $this = $(this),
                        setting = TT.parseOptions($this.data("setting")) || {};

                    if ($this.hasClass("j-fontBig")) {
                        $('.j-fontContent, .j-fontContent *').css('fontSize', setting.fontSize ? setting.fontSize : '18px');
                    } else if ($this.hasClass("j-fontNormal")) {
                        $('.j-fontContent, .j-fontContent *').css('fontSize', setting.fontSize ? setting.fontSize : '16px');
                    } else if ($this.hasClass("j-fontSmall")) {
                        $('.j-fontContent, .j-fontContent *').css('fontSize', setting.fontSize ? setting.fontSize : '14px');
                    }
                    return false;
                });
                return this;
            },
            /**
             * 查看图片
             * @pictureView
             * @description 查看图片大图
             */
            pictureView: function () {
                var $content = $('.j-fontContent');
                if ($content.length > 0) {

                    var setting = TT.parseOptions($content.data("setting")),
                        defaults = $.extend(true, {
                            target: "_blank",
                            isImgLink: false
                        }, setting);

                    if (defaults.isImgLink) {
                        var picLists = $content.find('img[data-originalurl]');
                        picLists.wrap(function () {
                            var $this = $(this),
                                originalUrl = $this.data("originalurl");
                            return '<a href="' + originalUrl + '" target="' + defaults.target + '"></a>';
                        });
                    }
                }
            },
            /**
             * 日期渲染
             */
            renderDate: function () {
                var $datetime = $('.j-datetime');
                if ($datetime.length > 0) {
                    using('moment', function () {
                        $datetime.each(function (i, v) {
                            var $this = $(v),
                                setting = TT.parseOptions($this.data("setting")),
                                defaults = $.extend(true, {
                                    format: "YYYY年MM月DD日 dddd",
                                    isSimple: true,
                                    script: "",
                                    nl: false,
                                    prefix: "今日是 ",
                                    nl_prefix: " 农历 "
                                }, setting);

                            if (defaults.isSimple) {
                                $this.html(defaults.prefix + "" + moment().format(defaults.format) + "" + (defaults.nl ? defaults.nl_prefix + nlCalendar.getDate() : ""));
                            } else {
                                //defaults.script && $this.html(TT.parseOptions(defaults.script, true));
                            }

                        })
                    })
                }
            },
            /**
             * 外站链接检测
             */
            linkDetection: function () {
                var jump = (_arg && _arg.jump) || Ls.args.jump;

                if (Ls.isEmpty(jump)) {
                    return false;
                }
                $(document).off('.link').on('click.link', 'a:not(.btn-jump)', function () {
                    var $this = $(this),
                        href = $this.attr("href") || '';

                    var regExp = new RegExp(/\/\/(?:[^/]+\.)?([^./]+\.([^/]+))(?:$|\/)/g),
                        match = href.match(regExp),
                        siteTitle = $("meta[name='SiteName']").attr("content") || '',
                        siteDomain = $("meta[name='SiteDomain']").attr("content") || '';

                    if (match) {
                        // 白名单，代码有后台广告位生成固定的JS,要手动在页面公共部分引入
                        var isPass = typeof(whitelist) != 'undefined' && $.inArray(match[0].replace(/\//g, ''), whitelist) != -1;

                        if (("//" + location.host + "/") != match[0] && ("//" + location.host + "/") != siteDomain && !isPass) {
                            using("dialog", function () {

                                var html = '' +
                                    '<div class="jump-info" title="外部链接：' + href + '">' +
                                    '   <p>您访问的链接将离开"' + siteTitle + '网站"访问外部链接，是否继续？</p>' +
                                    '   <div class="btn-group">' +
                                    '       <a class="btn btn-yes btn-jump" href="' + href + '" target="_blank">确 定</a>' +
                                    '       <a class="btn btn-no btn-dialog-close" href="javascript:void(0)">取 消</a>' +
                                    '   </div>' +
                                    '</div>';

                                var artDialog = Ls.openWin({
                                    title: '链接跳转',
                                    height: 150,
                                    content: html
                                });

                                artDialog.showModal();

                                $(document).one('click.jump.close', '.btn-dialog-close', function () {
                                    artDialog.close().remove();
                                }).one('click.jump.yes', '.btn-jump', function () {
                                    artDialog.close();
                                    setTimeout(function () {
                                        artDialog.remove();
                                    }, 250)
                                });

                                Ls.dialog.put("dialog", artDialog);
                            });
                            return false;
                        }

                    }

                });
            },
            /**
             * 获取文章点击数
             */
            getInfoHit: function () {

                var $infoHit = $(".j-info-hit");
                if ($infoHit.length > 0) {
                    $infoHit.each(function (i, v) {
                        var $this = $(v);
                        var setting = Ls.parseOptions($this.data("setting")),
                            defaults = $.extend(true, {
                                infoId: $this.data("id") || ""
                            }, setting);

                        if (defaults.infoId) {
                            Ls.ajax({
                                url: '/content/hit/' + defaults.infoId,
                                dataType: 'html'
                            }).done(function (res) {
                                if (res) {
                                    var result,
                                        reg = /document\.write\(\"([\d]+)\"\)/gi;

                                    if ((result = reg.exec(res)) != null) {
                                        $this.text(result[1]).removeAttr("data-setting");
                                    }
                                }
                            })
                        }
                    })
                }

            },
            footerIdentifier: function () {
                var $jiucuo = $(".j-jiucuo"),
                    $gov = $(".j-gov");

                if ($jiucuo.length > 0) {

                    var getCurrUrl = function () {
                        var url = "";
                        if (parent !== window) {
                            try {
                                url = window.top.location.href;
                            } catch (e) {
                                url = window.top.document.referrer;
                            }
                        }
                        if (url.length == 0)
                            url = document.location.href;

                        return url;
                    }

                    var url = getCurrUrl(),
                        setting = TT.parseOptions($jiucuo.data("setting")),
                        defaults = $.extend(true, {
                            sitecode: ""
                        }, setting);

                    var $wrapj = $("<a>", {
                        id: '_jiucuo_',
                        href: 'http://121.43.68.40/exposure/jiucuo.html?site_code=' + defaults.sitecode + '&url=' + encodeURIComponent(url),
                        target: '_blank'
                    }).appendTo($jiucuo);

                    $("<img>", {
                        border: '0',
                        src: '/assets/images/jiucuo.png?v=' + defaults.sitecode
                    }).appendTo($wrapj);

                    /*$("<span>", {
                        id: "_span_jiucuo"
                    }).appendTo($jiucuo);

                    $("<script>", {
                        id: "_jiucuo_",
                        src: defaults.src
                    }).attr("sitecode", defaults.sitecode).appendTo($jiucuo)*/
                }

                if ($gov.length > 0) {

                    var setting = TT.parseOptions($gov.data("setting")),
                        defaults = $.extend(true, {
                            type: 0,
                            sitecode: ""
                        }, setting);

                    var $wrapa = $("<a>", {
                        href: 'http://bszs.conac.cn/sitename?method=show&id=' + defaults.sitecode,
                        target: '_blank'
                    }).appendTo($gov);

                    $("<img>", {
                        id: 'imgConac',
                        border: '0',
                        src: '/assets/images/' + (defaults.type == 0 ? 'gov_red' : 'gov_blue') + '.png'
                    }).attr({
                        vspace: '0',
                        hspace: '0',
                        "data-bd-imgshare-binded": "1"
                    }).appendTo($wrapa)
                }

            },
            pdfView: function () {
                var $pdfContent = $(".j-fontContent"),
                    $pdf = $pdfContent.find('a[data-file-ext]');
                if ($pdf.length == 1) {
                    var pdfurl = $pdf.attr('href'),
                        pdfwidth = 960,
                        pdfheight = pdfwidth * 4 / 3;
                    if (pdfurl.indexOf(".pdf") >= 0) {
                        //判断是否为PDF，是就显示，不是就不显示
                        var pdfObject = '<div> 如果内容不能正常显示：请安装pdf软件 <a href="/assets/soft/pdf_reader_install.rar" style="color: Red;">[点击下载]</a> 或下载本PDF文档 [<a href="' + pdfurl + '" target="_blank" style="color: Red;">点击下载</a>] </div>' +
                            '<div style="margin:auto; text-align: center;">' +
                            '<object classid="clsid:CA8A9780-280D-11CF-A24D-444553540000" width="' + pdfwidth + '" height="' + pdfheight + '" border="0" top="-10" name="pdf">' +
                            '   <param name="toolbar" value="true">' +
                            '   <param name="_Version" value="65539">' +
                            '   <param name="_ExtentX" value="20108">' +
                            '   <param name="_ExtentY" value="10866">' +
                            '   <param name="_StockProps" value="0">' +
                            '   <param name="SRC" value="' + pdfurl + '">' +
                            '   <embed name="plugin" src="' + pdfurl + '" type="application/pdf" width="' + pdfwidth + '" height="' + pdfheight + '">' +
                            '</object>' +
                            '</div>';
                        $pdfContent.find('img[data-file]').remove();
                        $pdfContent.html(pdfObject);
                    }
                }
            },
            getStatistics: function () {
                var $sitestat = $(".j-sitestats");

                if ($sitestat.length > 0) {
                    var data = {};
                    if (document) {
                        data.url = document.URL || '';
                        data.title = document.title || '';
                        data.referer = document.referrer || '';
                    }
                    if (window && window.screen) {
                        var sh = window.screen.height || 0;
                        var sw = window.screen.width || 0;
                        data.resolution = sw + ' x ' + sh;
                        data.colorDepth = window.screen.colorDepth || 0;
                    }

                    if (navigator) {
                        data.language = navigator.language || '';
                        var ua = navigator.userAgent;
                        var Agents = new Array("Android", "iPhone", "SymbianOS", "Windows Phone", "iPad", "iPod", "Windows", "Mac", "Unix", "Linux", "X11");
                        var f = "Other";
                        for (var v = 0; v < ua.length; v++) {
                            if (ua.indexOf(Agents[v]) > -1) {
                                f = Agents[v];
                            }
                        }
                        var isPc = "true";
                        if (f != "Windows" && f != "Mac" && f != "Unix" && f != "Linux") {
                            isPc = "false"
                        }
                        data.isPc = isPc;
                        if (f == "Windows") {
                            if (ua.indexOf("Windows NT 5.0") > -1 || ua.indexOf("Windows 2000") > -1) {
                                f = "Windows 2000"
                            } else if (ua.indexOf("Windows NT 5.1") > -1 || ua.indexOf("Windows XP") > -1) {
                                f = "Windows XP"
                            } else if (ua.indexOf("Windows NT 5.2") > -1 || ua.indexOf("Windows 2003") > -1) {
                                f = "Windows 2003"
                            } else if (ua.indexOf("Windows NT 6.0") > -1 || ua.indexOf("Windows Vista") > -1) {
                                f = "Windows Vista"
                            } else if (ua.indexOf("Windows NT 6.1") > -1 || ua.indexOf("Windows 7") > -1) {
                                f = "Windows 7"
                            } else if (ua.indexOf("Windows NT 6.2") > -1 || ua.indexOf("Windows 8") > -1) {
                                f = "Windows 8"
                            } else if (ua.indexOf("Windows NT 10") > -1 || ua.indexOf("Windows 10") > -1) {
                                f = "Windows 10"
                            }
                        }
                        data.os = f;
                    }

                    var setting = Ls.parseOptions($sitestat.data("setting")),
                        defaults = $.extend(true, {
                            el: '',
                            siteId: 0,
                            isTimes: true
                        }, setting);

                    data.siteId = defaults.siteId;
                    data.isTimes = defaults.isTimes;

                    Ls.ajax({
                        url: '/site/chart/webMst',
                        dataType: 'html',
                        data: data
                    }).done(function (res) {
                        if (res) {
                            var result,
                                reg = /website_times\.innerHTML=\'([\d]+)\'/gi;

                            if ((result = reg.exec(res)) != null) {
                                defaults.isTimes && $sitestat.text(result[1]).removeAttr("data-setting");
                            }
                        }
                    })

                }

            },
            thumbnailFocus: function () {

                var $thumb = $(".j-thumb-focus");
                if ($thumb.length > 0) {
                    $thumb.each(function (index, el) {
                        var $this = $(el),
                            setting = Ls.parseOptions($this.data("setting")),
                            defaults = $.extend(true, {
                                largeEl: "",
                                smallEl: "",
                                leftBtn: "",
                                rightBtn: "",
                                state: "on",
                                speed: "normal",
                                vis: 4
                            }, setting);

                        var $largeEl = $(defaults.largeEl),
                            $smallEl = $(defaults.smallEl);

                        var tIndex = 0,
                            meanNum = 0,
                            liNum = $smallEl.find("ul li").length;

                        $largeEl.find("ul li").eq(0).show();
                        $smallEl.find("ul li").eq(0).addClass(defaults.state);

                        if (liNum < defaults.vis) {
                            meanNum = 0;
                        } else {
                            meanNum = ((parseInt(liNum / defaults.vis) - 1) * defaults.vis) + (liNum % defaults.vis);
                        }

                        var liWid = $smallEl.find("ul li").outerWidth(true);

                        $smallEl.find("ul").css("width", liNum * liWid + "px");

                        $smallEl.find("ul li").click(function () {
                            var $this = $(this);
                            $this.addClass(defaults.state).siblings().removeClass(defaults.state);
                            tIndex = $this.index();
                            changeImg($this.index());
                        });

                        $(defaults.leftBtn).on('click.thumb', function () {
                            var i;
                            $smallEl.find("ul li").each(function (index) {
                                if ($(this).hasClass(defaults.state)) {
                                    i = index
                                }
                            });
                            i--;
                            if (i < 0) {
                                i = liNum - 1
                            }
                            tIndex = i;
                            changeImg(i)
                        });

                        $(defaults.rightBtn).on('click.thumb', function () {
                            var i;
                            $smallEl.find("ul li").each(function (index) {
                                if ($(this).hasClass(defaults.state)) {
                                    i = index
                                }
                            });
                            i++;
                            if (i > liNum - 1) {
                                i = 0
                            }
                            tIndex = i;
                            changeImg(i)
                        });

                        function changeImg(i) {
                            $largeEl.find("ul li").eq(i).fadeIn().siblings().hide();
                            $smallEl.find("ul li").eq(i).addClass(defaults.state).siblings().removeClass(defaults.state);
                            var ml = i * liWid;
                            if (ml <= meanNum * liWid) {
                                $smallEl.find("ul").stop().animate({
                                    marginLeft: -ml + "px"
                                }, defaults.speed);
                            } else {
                                $smallEl.find("ul").stop().animate({
                                    marginLeft: -(meanNum * liWid) + "px"
                                }, defaults.speed);
                            }
                        }

                        var timing = setInterval(function () {
                            tIndex++;
                            if (tIndex > liNum - 1) {
                                tIndex = 0;
                            }
                            changeImg(tIndex)
                        }, 3000);

                        $this.hover(function () {
                            clearInterval(timing)
                        }, function () {
                            timing = setInterval(function () {
                                tIndex++;
                                if (tIndex > liNum - 1) {
                                    tIndex = 0;
                                }
                                changeImg(tIndex)
                            }, 3000);
                        });


                    })
                }


            },
            printView: function () {
                /*var me = this,
                    $print = $(".f-print"),
                    setting = TT.parseOptions($print.data("setting")),
                    defaults = $.extend({
                        printContent: '.f-print-content'
                    }, setting);

                $print.on("click.print", function (e) {
                    e.preventDefault();
                    using("jqprint", function () {
                        $(defaults.printContent).jqprint();
                    });
                });*/

                var $print = $(".f-print"),
                    setting = TT.parseOptions($print.data("setting")),
                    defaults = $.extend({
                        printContent: '.f-print-content',
                        onrendered: function (canvas) {
                            var dataUrl = canvas.toDataURL();//获取canvas对象图形的外部url
                            var newImg = document.createElement("img");//创建img对象
                            newImg.src = dataUrl;//将canvas图形url赋给img对象
                            $printIframe.append(newImg).printArea();//打印img，注意不能直接打印img对象，需要包裹一层div
                            $printIframe.html(''); //打印完毕释放包裹层内容（图像）
                        }
                    }, setting);

                var $printIframe;
                $print.on("click.print", function (e) {
                    e.preventDefault();
                    if (Ls.browser.isIE8OrLower) {
                        window.print();
                    } else {

                        $printIframe = $('#print_iframe');
                        if ($printIframe.length == 0) {
                            $printIframe = $('<div>', {
                                id: 'print_iframe',
                            }).css({
                                position: 'absolute',
                                // top: '-500px',
                                // left: '-500px'
                            }).appendTo('body');
                        }

                        using("html2canvas", function () {
                            var $printArea = $(defaults.printContent); //待打印区域dom对象
                            html2canvas($printArea.get(0), defaults);
                        });
                    }
                });
            }
        }
    })();

    //初始化默认方法
    $(window).ready(function () {
        os.www.init();
    }).on('load', function () {
        os.www.init2();
    });

    //IE6
    if (TT.browser.isIE8OrLower) {
        try {
            document.execCommand("BackgroundImageCache", false, true)
        } catch (e) {
        }
    }

    //注入前端通用方法
    TT.www = os.www;

    //注入WINDOWS
    _win.Ls = TT;

    /*
    * myFocus v2.0.4
    */
    (function (isFocus) {
        if (!isFocus) {
            return false;
        }

        //DOM基础操作函数
        var $id = function (id) {
                return typeof id === 'string' ? document.getElementById(id) : id;
            },
            $tag = function (tag, parentNode) {
                return ($id(parentNode) || document).getElementsByTagName(tag);
            },
            $tag_ = function (tag, parentNode) {
                return $getChild(tag, parentNode, 'tag');
            },
            $class = function (className, parentNode) {
                var doms = $tag('*', parentNode), arr = [];
                for (var i = 0, l = doms.length; i < l; i++) {
                    if (hasClass(className, doms[i])) {
                        arr.push(doms[i]);
                    }
                }
                return arr;
            },
            $class_ = function (className, parentNode) {
                return $getChild(className, parentNode);
            },
            $getChild = function (selector, parentNode, type) {
                var arr = [], fn = type === 'tag' ? $tag : $class, doms = fn(selector, parentNode), len = doms.length;
                for (var i = 0; i < len; i++) {
                    if (doms[i].parentNode === parentNode) arr.push(doms[i]);
                    i += fn(selector, doms[i]).length;
                }
                return arr;
            },
            hasClass = function (className, node) {
                return eval('/(^|\\s)' + className + '(\\s|$)/').test(node.className);
            };
        //定义myFocus全局变量
        myFocus = function (settings) {
            return new myFocus.constr(settings);
        };
        //扩展
        myFocus.extend = function () {
            var arg = arguments, len = arg.length;
            if (this === myFocus) {//作为方法扩展，如果只有一个参数扩展本身
                if (len === 1) dest = myFocus, i = 0;//扩展myFocus类
                else dest = arg[0], i = 1;
            } else {//扩展引用对象本身
                dest = this, i = 0;
            }
            for (i; i < len; i++) {
                for (var p in arg[i]) {
                    dest[p] = arg[i][p];//dest属性最低
                }
            }
            return dest;
        };
        myFocus.extend({
            defConfig: {//全局默认设置
                pattern: 'mF_fancy',
                trigger: 'click',//触发切换模式['click'(鼠标点击)|'mouseover'(鼠标悬停)]
                txtHeight: 'default',//文字层高度设置[num(数字,单位像素,0表示隐藏文字层,省略设置即为默认高度)]
                wrap: true,//是否需要让程序添加焦点图的外围html(wrap)元素(目的是为了添加某些风格的边框)[true|false]
                auto: true,//是否自动播放(切换)[true|false]
                time: 4,//每次停留时间[num(数字,单位秒)]
                index: 0,//开始显示的图片序号(从0算起)[num(数字)]
                loadingShow: true,//是否显示Loading画面[true(显示，即等待图片加载完)|false(不显示，即不等待图片加载完)]
                delay: 100,//触发切换模式中'mouseover'模式下的切换延迟[num(数字,单位毫秒)]
                autoZoom: false,//是否允许图片自动缩放居中[true|false]
                onChange: null,//切换图片的时候执行的自定义函数，带有一个当前图索引的参数
                xmlFile: '',//myfocus图片的xml配置文件路径,留空即不读取xml文件
                __focusConstr__: true//程序构造参数
            },
            constr: function (settings) {//构造函数
                var e = settings, len = e && e.length;
                if (e instanceof myFocus.constr) return e;//myFocus::[]
                this.length = 0;
                if (!e || (e.sort && !len) || (e.item && !len)) {//null/array::[]/nodeList::[]
                    Array.prototype.push.call(this);
                } else if (e.__focusConstr__) {//new myFocus
                    e = $id(e.id);
                    Array.prototype.push.call(this, e);
                    this.settings = settings;
                    this.HTMLUList = $tag('li', $tag('ul', e)[0]);
                    this.HTMLUListLength = this.HTMLUList.length;
                } else if (len) {//nodeList/Array/字符串
                    for (var i = 0; i < len; i++) Array.prototype.push.call(this, e[i]);
                } else {//node
                    Array.prototype.push.call(this, e);
                }
                return this;
            },
            fn: {splice: [].splice},//原形
            pattern: {},//风格集
            config: {}//参数集
        });
        myFocus.constr.prototype = myFocus.fn;
        myFocus.fn.extend = myFocus.pattern.extend = myFocus.config.extend = myFocus.extend;
        myFocus.fn.extend({//DOM
            find: function (selector) {//选择器只应用基本查找,暂不考虑用querySelectorAll
                var parent = this, isChild = false, $ = myFocus;
                var arr = this.parseSelector(selector);
                if (this.length) for (var i = 0, len = arr.length; i < len; i++) {
                    var dom = [], s = arr[i];
                    switch (s.charAt(0)) {
                        case '>'://children
                            isChild = true;
                            break;
                        case '.'://class
                            var cls = s.slice(1);
                            var fn = isChild ? $class_ : $class;
                            $(parent).each(function () {
                                dom = dom.concat(fn(cls, this));
                            });
                            isChild = false;
                            break;
                        case '#'://id
                            var id = s.slice(1), e = $id(id);
                            if (e) dom.push($id(id));
                            isChild = false;
                            break;
                        default://tag(支持'tag.class'寻找,不支持也不建议用'tag#id'寻找,请用'#id')
                            var fn = isChild ? $tag_ : $tag, sArr = s.split('.');
                            var tag = sArr[0], cls = sArr[1];
                            $(parent).each(function () {
                                var arr = fn(tag, this);
                                for (var i = 0, len = arr.length; i < len; i++) {
                                    if (cls && !hasClass(cls, arr[i])) continue;
                                    dom.push(arr[i]);
                                }
                            });
                            isChild = false;
                    }
                    if (!isChild) parent = dom;//循环赋值父元素
                }
                return $(parent);
            },
            parent: function () {
                return myFocus(this[0].parentNode);
            },
            html: function (s) {
                if (typeof s !== 'undefined') {
                    this[0].innerHTML = s;
                    return this;
                }
                else return this[0].innerHTML;
            },
            each: function (fn) {
                var doms = this;
                for (var i = 0, len = doms.length; i < len; i++) {
                    var flag = fn.call(doms[i], i);
                    if (flag === false) break;
                    if (flag === true) continue;
                }
                return this;
            },
            eq: function (n) {
                return myFocus(this[n]);
            },
            parseSelector: function (selector) {
                var chunker = /(([^[\]'"]+)+\]|\\.|([^ >+~,(\[\\]+)+|[>+~])(\s*,\s*)?/g;
                var parts = [], m;
                while ((m = chunker.exec(selector)) !== null) {
                    parts.push(m[1]);//存储匹配的字符串信息
                }
                return parts;
            },
            wrap: function (html) {//每次只wrap一个元素,多个请用each
                var o = this[0], e = document.createElement('div');
                e.innerHTML = html;
                var wrap = e.firstChild;
                o.parentNode.replaceChild(wrap, o);
                wrap.appendChild(o);
                return this;
            },
            addHtml: function (html) {
                var parent = this[0];
                var e = document.createElement('div');
                e.innerHTML = html;
                var dom = e.childNodes[0];
                parent.appendChild(dom);
                return myFocus(dom);
            },
            addList: function (className, type) {
                var li = this.HTMLUList, n = this.HTMLUListLength;
                var strArr = ['<div class="' + className + '"><ul>'];
                for (var i = 0; i < n; i++) {
                    var img = $tag('img', li[i])[0], html;
                    switch (type) {
                        case 'num'  :
                            html = '<a>' + (i + 1) + '</a><b></b>';
                            break;//b标签主要是为了做透明背景,下同
                        case 'txt'  :
                            html = img ? li[i].innerHTML.replace(/\<img(.|\n|\r)*?\>/i, img.alt) + '<p>' + img.getAttribute("text") + '</p><b></b>' : '';
                            break;
                        case 'thumb':
                            html = img ? '<a><img src=' + (img.getAttribute("thumb") || img.src) + ' /></a><b></b>' : '';
                            break;
                        default     :
                            html = '<a></a><b></b>';
                    }
                    strArr.push('<li>' + html + '</li>');
                }
                strArr.push('</ul></div>');
                return this.addHtml(strArr.join(''));
            },
            addListNum: function (className) {
                return this.addList(className || 'num', 'num');//默认class=num
            },
            addListTxt: function (className) {
                return this.addList(className || 'txt', 'txt');//默认class=txt
            },
            addListThumb: function (className) {
                return this.addList(className || 'thumb', 'thumb');//默认class=thumb
            },
            remove: function () {
                var o = this[0];
                if (o) o.parentNode.removeChild(o);
            },
            repeat: function (n) {
                var n = n || 2, pNode = this[0].parentNode, html = pNode.innerHTML, s = [];
                for (var i = 0; i < n; i++) s.push(html);
                pNode.innerHTML = s.join('');
                return myFocus(pNode).find(this[0].nodeName);
            }
        });
        myFocus.fn.extend({//CSS
            css: function (css) {//可获值或设值
                var o = this[0], value, arr = [';'], isIE = myFocus.isIE;
                if (!o) return this;
                if (typeof css === 'string') {//获得css属性值,返回值不带单位
                    if (css === 'float') css = isIE ? 'styleFloat' : 'cssFloat';
                    if (!(value = o.style[css])) value = (isIE ? o.currentStyle : getComputedStyle(o, ''))[css];
                    if (css === 'opacity' && value === undefined) value = 1;//仅为在IE中得到默认值1
                    if (value === 'auto' && (css === 'width' || css === 'height')) value = o['offset' + css.replace(/\w/i, function (a) {
                        return a.toUpperCase()
                    })];
                    var numVal = parseFloat(value);
                    return isNaN(numVal) ? value : numVal;
                } else {//设置css属性值,不支持('height','300px')形式,请变成-->({height:'300px'}),可以不带单位px
                    for (var p in css) {
                        if (typeof css[p] === 'number' && !this.cssNumber[p]) css[p] += 'px';
                        arr.push(p.replace(/([A-Z])/g, '-$1') + ':' + css[p] + ';');
                        if (p === 'opacity') arr.push('filter:alpha(opacity=' + css[p] * 100 + ')');
                    }
                    o.style.cssText += arr.join('');
                    return this;
                }
            },
            setOpacity: function (value) {//仅用于animate要求高效的核心算法中,其它情况可用css()代替
                this[0].style.opacity = value, this[0].style.filter = 'alpha(opacity=' + value * 100 + ')';
            },
            setAnimateStyle: function (value, prop, m) {//仅用于animate要求高效的核心算法中,其它情况可用css()代替
                this[0].style[prop] = Math[m](value) + 'px';
            },
            addClass: function (className) {
                this[0].className += ' ' + className;
                return this;
            },
            removeClass: function (className) {
                var o = this[0], cls = className && o.className, reg = "/\\s*\\b" + className + "\\b/g";
                o.className = cls ? cls.replace(eval(reg), '') : '';
                return this;
            },
            cssNumber: {
                fillOpacity: true,
                fontWeight: true,
                lineHeight: true,
                opacity: true,
                orphans: true,
                widows: true,
                zIndex: true,
                zoom: true
            }//不加px的css,参考jQuery
        });
        myFocus.fn.extend({//Animate
            animate: function (attr, value, time, type, funcBefore, funcAfter) {//value支持相对增值'+=100',相对减值'-=100'形式
                var $o = this, o = $o[0], isOpacity = attr === 'opacity', diffValue = false;
                funcBefore && funcBefore.call(o);
                if (typeof value === 'string') {
                    if (/^[+-]=\d+/.test(value)) value = value.replace('=', ''), diffValue = true;
                    value = parseFloat(value);
                }
                var oriVal = $o.css(attr),//原始属性值
                    b = isNaN(oriVal) ? 0 : oriVal,//开始值,无值时为0
                    c = diffValue ? value : value - b,//差值
                    d = time,//总运行时间
                    e = this.easing[type],//缓动类型
                    m = c > 0 ? 'ceil' : 'floor',//取最大绝对值
                    timerId = '__myFocusTimer__' + attr,//计数器id
                    setProperty = $o[isOpacity ? 'setOpacity' : 'setAnimateStyle'],//属性设置方法
                    origTime = (new Date) * 1;//原始时间值
                o[timerId] && clearInterval(o[timerId]);
                o[timerId] = setInterval(function () {
                    var t = (new Date) - origTime;//已运行时间
                    if (t <= d) {
                        setProperty.call($o, e(t, b, c, d), attr, m);
                    } else {
                        setProperty.call($o, b + c, attr, m);//设置最终值
                        clearInterval(o[timerId]), o[timerId] = null;
                        funcAfter && funcAfter.call(o);
                    }
                }, 13);
                return this;
            },
            fadeIn: function (time, type, fn) {
                if (typeof time !== 'number') fn = time, time = 400;//默认400毫秒
                if (typeof type === 'function') fn = type, type = '';
                this.animate('opacity', 1, time, type || 'linear', function () {
                    myFocus(this).css({display: 'block', opacity: 0});
                }, fn);
                return this;
            },
            fadeOut: function (time, type, fn) {
                if (typeof time !== 'number') fn = time, time = 400;//默认400毫秒
                if (typeof type === 'function') fn = type, type = '';
                this.animate('opacity', 0, time, type || 'linear', null, function () {
                    this.style.display = 'none';
                    fn && fn.call(this);
                });
                return this;
            },
            slide: function (params, time, type, fn) {
                if (typeof time !== 'number') fn = time, time = 800;//默认800毫秒
                if (typeof type === 'function') fn = type, type = '';
                for (var p in params) this.animate(p, params[p], time, type || 'easeOut', null, fn);
                return this;
            },
            stop: function () {//停止所有运动
                var o = this[0];
                for (var p in o) if (p.indexOf('__myFocusTimer__') !== -1) o[p] && clearInterval(o[p]);
                return this;
            },
            easing: {
                linear: function (t, b, c, d) {
                    return c * t / d + b;
                },
                swing: function (t, b, c, d) {
                    return -c / 2 * (Math.cos(Math.PI * t / d) - 1) + b;
                },
                easeIn: function (t, b, c, d) {
                    return c * (t /= d) * t * t * t + b;
                },
                easeOut: function (t, b, c, d) {
                    return -c * ((t = t / d - 1) * t * t * t - 1) + b;
                },
                easeInOut: function (t, b, c, d) {
                    return ((t /= d / 2) < 1) ? (c / 2 * t * t * t * t + b) : (-c / 2 * ((t -= 2) * t * t * t - 2) + b);
                }
            }
        });
        myFocus.fn.extend({//Method(fn)
            bind: function (type, fn) {
                myFocus.addEvent(this[0], type, fn);
                return this;
            },
            play: function (funcLastFrame, funcCurrentFrame, seamless) {
                var this_ = this, p = this_.settings, n = this_.HTMLUListLength, t = p.time * 1000,
                    seamless = seamless || false,//是否无缝
                    float = myFocus(this_.HTMLUList).css('float'), isLevel = float === 'left',//仅支持'left'方向和'top'方向
                    direction = isLevel ? 'left' : 'top', distance = isLevel ? p.width : p.height,//运动距离
                    indexLast = 0, indexCurrent = p.index;//帧索引值,默认0
                this_.find('.loading').remove();//删除loading...
                this_.run = function (value) {//循环运动函数,支持+-=value
                    funcLastFrame && funcLastFrame(indexLast, n);//运行前一帧
                    indexCurrent = typeof value === 'string' ? indexLast + parseInt(value.replace('=', '')) : value;//fixed index
                    if (indexCurrent <= -1) {//prev run
                        indexCurrent = n - 1;//转到最后一帧
                        if (seamless) this_.HTMLUList[0].parentNode.style[direction] = -n * distance + 'px';//无缝的UL定位
                    }
                    if (indexCurrent >= n) {//next run
                        if (!seamless) indexCurrent = 0;//非无缝时转到第一帧
                        if (indexCurrent >= 2 * n) {//无缝
                            this_.HTMLUList[0].parentNode.style[direction] = -(n - 1) * distance + 'px';//无缝的UL定位
                            indexCurrent = n;
                        }
                    }
                    if (seamless && indexLast >= n && indexCurrent < n) indexCurrent += n;//无缝时的按钮点击(保持同一方向)
                    funcCurrentFrame && funcCurrentFrame(indexCurrent, n, indexLast);//运行当前帧
                    this_.runIndex = indexLast = indexCurrent;//保存已运行的帧索引
                    //增加自定义回调函数@10.27
                    p.onChange && p.onChange.call(this_, indexCurrent);
                };
                //运行...(try是为了兼容风格js中play比bindControl先执行)
                try {
                    this_.run(indexCurrent)
                } catch (e) {
                    setTimeout(function () {
                        this_.run(indexCurrent)
                    }, 0)
                }
                if (p.auto && n > 1) {//自动切换
                    this_.runTimer = setInterval(function () {
                        this_.run('+=1')
                    }, t);//默认递增运行每帧
                    this_.bind('mouseover', function () {//绑定事件
                        clearInterval(this_.runTimer);
                        this_.runTimer = 'pause';//标记以防止执行两次this_.runTimer
                    }).bind('mouseout', function () {
                        if (!this_.isStop && this_.runTimer === 'pause') this_.runTimer = setInterval(function () {
                            this_.run('+=1')
                        }, t);
                    });
                }
                this_.find('a').each(function () {//去除IE链接虚线
                    this.onfocus = function () {
                        this.blur();
                    }
                });
            },
            bindControl: function ($btnList, params) {//params={thumbShowNum(略缩图显示数目(如果有)):num,isRunning(运行中的标记(当需要判断时)):boolean}
                var this_ = this, p = this_.settings, type = p.trigger, delay = p.delay, par = params || {},
                    tsNum = par.thumbShowNum || p.thumbShowNum;
                var run = function () {
                    if (this.index !== this_.runIndex && !par.isRunning) {
                        this_.run(this.index);
                        return false;//阻止冒泡&默认事件
                    }
                };
                $btnList.each(function (i) {
                    this.index = i;//记录自身索引
                    var o = this, $o = myFocus(o);
                    if (type === 'click') {
                        $o.bind('mouseover', function () {
                            $o.addClass('hover');
                        }).bind('mouseout', function () {
                            $o.removeClass('hover');
                        }).bind('click', run);
                    } else if (type === 'mouseover') {
                        $o.bind('mouseover', function () {
                            if (delay === 0) run.call(o);
                            else $btnList.mouseoverTimer = setTimeout(function () {
                                run.call(o)
                            }, delay);
                        }).bind('mouseout', function () {
                            $btnList.mouseoverTimer && clearTimeout($btnList.mouseoverTimer);
                        });
                    } else {
                        alert('myFocus Error Setting(trigger) : \"' + type + '\"');
                        return false;
                    }
                });
                if (tsNum) {//thumb
                    var float = $btnList.css('float'), isLevel = float === 'left' || float === 'right';
                    $btnList.dir = isLevel ? 'left' : 'top';//方向
                    $btnList.n = this_.HTMLUListLength;//总数
                    $btnList.showNum = tsNum;//显示数目
                    $btnList.showStart = p.index;//显示的开始索引
                    $btnList.showEnd = $btnList.showStart + tsNum - 1;//显示的结尾索引
                    $btnList.distance = $btnList.css(isLevel ? 'width' : 'height');//运动距离
                    $btnList.slideBody = $btnList.parent();//运动对象(ul)
                }
            },
            scrollTo: function (i, time) {
                var n = this.n, dir = this.dir, $ul = this.slideBody, css = {};//总数/方向/滑动body(ul)/样式
                if (i >= this.showEnd) {//next
                    this.showEnd = i < n - 1 ? i + 1 : i;

                    this.showStart = this.showEnd - this.showNum + 1;
                } else if (i <= this.showStart) {//prev
                    this.showStart = i > 0 ? i - 1 : 0;
                    this.showEnd = this.showStart + this.showNum - 1;
                }
                css[dir] = -this.showStart * this.distance;
                $ul.slide(css, time || 500, 'easeOut');
                return this;
            }
        });
        myFocus.extend({//Init
            set: function (p, callback) {
                var F = this, id = p.id, oStyle = F.initBaseCSS(id);
                p.pattern = p.pattern || F.defConfig.pattern;
                p.__clsName = p.pattern + '_' + id;
                F.addEvent(window, 'load', function () {
                    F.onloadWindow = true
                });
                F.loadPattern(p, function () {
                    p = F.extend({}, F.defConfig, F.config[p.pattern], p);//收集完整参数
                    F.getBoxReady(p, function () {
                        var $o = F($id(id));
                        p.$o = $o;//保存node
                        //xml load
                        p.xmlFile && F.loadXML(p);
                        p.pic = $class('pic', $o[0])[0];//保存node for是否标准风格的判断及是否需要initcss
                        p.width = p.width || $o.css('width'), p.height = p.height || $o.css('height');//获得box高/宽
                        F.initCSS(p, oStyle);//css
                        $o.addClass(p.pattern + ' ' + p.__clsName);//+className
                        F.getIMGReady(p, function (arrSize) {
                            if (p.autoZoom) F.zoomIMG(p, arrSize);//缩放图片
                            F.pattern[p.pattern](p, F);//运行pattern代码
                            callback && callback();
                        });
                    });
                });
            },
            onloadWindow: false,
            loadPattern: function (p, callback) {
                if (this.pattern[p.pattern]) {
                    callback();
                    return;
                }//如果已加载pattern
                var path = this.getFilePath() + 'mf-pattern/' + p.pattern;
                var js = document.createElement("script"), css = document.createElement("link"), src = path + '.js',
                    href = path + '.css';
                js.type = "text/javascript", js.src = src;
                css.rel = "stylesheet", css.href = href;
                var head = $tag('head')[0], isSuccess = false, timeout = 10 * 1000;//超时10秒
                head.appendChild(css);
                head.appendChild(js);
                js.onload = js.onreadystatechange = function () {
                    if (isSuccess) return;//防止IE9+重复执行
                    if (!js.readyState || js.readyState == "loaded" || js.readyState == "complete") {
                        isSuccess = true;
                        callback();
                        js.onload = js.onreadystatechange = null;
                    }
                };
                setTimeout(function () {
                    if (!isSuccess) $id(p.id).innerHTML = '加载失败: ' + src;
                }, timeout);
            },
            getFilePath: function () {
                var path = '';
                var scripts = $tag("script");
                for (var i = 0, len = scripts.length; i < len; i++) {
                    var src = scripts[i].src;
                    if (src && /myfocus([\.-].*)?\.js/i.test(src)) {//兼容myfocus.js/myfocus.min.js/myfocus-2.x.js
                        path = src;
                        break;
                    }
                }
                return path.slice(0, path.lastIndexOf('/') + 1);
            },
            getBoxReady: function (p, fn) {
                var F = this;
                (function () {
                    try {
                        if (F.isIE) $id(p.id).doScroll();
                        else $id(p.id).innerHTML;
                        fn();
                    } catch (e) {
                        if (!F.onloadWindow) setTimeout(arguments.callee, 0);
                    }
                })();
            },
            getIMGReady: function (p, callback) {
                var isShow = p.loadingShow;
                var box = $id(p.id), img = $tag('img', p.pic), len = img.length,
                    count = 0, done = false, arrSize = new Array(len);
                if (!isShow || !len) {
                    callback();
                    return;
                }//无延迟
                for (var i = 0; i < len; i++) {
                    var IMG = new Image();
                    IMG.i = i;//标记前后顺序
                    IMG.onload = function () {
                        count += 1;
                        arrSize[this.i] = {w: this.width, h: this.height};//储存for zoomIMG
                        if (count == len && !done) {
                            done = true, callback(arrSize);
                        }
                    };
                    IMG.src = img[i].src;
                }
            },
            zoomIMG: function (p, arrSize) {
                var imgs = $tag('img', p.pic), len = imgs.length, boxWidth = p.width, boxHeight = p.height;
                for (var i = 0; i < len; i++) {
                    var w = arrSize[i].w, h = arrSize[i].h;
                    if (w == boxWidth && h == boxHeight) continue;
                    if (w < boxWidth && h < boxHeight) {
                        var width = w, height = h, top = (boxHeight - height) / 2;
                    } else if (w / h >= boxWidth / boxHeight) {
                        var width = boxWidth, height = boxWidth / w * h, top = (boxHeight - height) / 2;
                    } else {
                        var width = boxHeight / h * w, height = boxHeight, top = 0;
                    }
                    imgs[i].style.cssText = ';width:' + width + 'px;height:' + height + 'px;margin-top:' + top + 'px;';
                }
            },
            initCSS: function (p, oStyle) {
                var css = [], w = p.width || '', h = p.height || '';
                if (p.pic) {
                    css.push('.' + p.__clsName + ' *{margin:0;padding:0;border:0;list-style:none;}.' + p.__clsName + '{position:relative;width:' + w + 'px;height:' + h + 'px;overflow:hidden;font:12px/1.5 Verdana;text-align:left;background:#fff;visibility:visible!important;}.' + p.__clsName + ' .pic{position:relative;width:' + w + 'px;height:' + h + 'px;overflow:hidden;}.' + p.__clsName + ' .txt li{width:' + w + 'px;height:' + p.txtHeight + 'px!important;overflow:hidden;}');
                    if (p.wrap) p.$o.wrap('<div class="' + p.pattern + '_wrap"></div>');
                    if (p.autoZoom) css.push('.' + p.__clsName + ' .pic li{text-align:center;width:' + w + 'px;height:' + h + 'px;}');//缩放图片居中
                }
                try {
                    oStyle.styleSheet.cssText = css.join('')
                } catch (e) {
                    oStyle.innerHTML = css.join('')
                }
            },
            initBaseCSS: function (id) {
                var s = '#' + id + ' *{display:none}', oStyle = document.createElement('style');
                oStyle.type = 'text/css';
                try {
                    oStyle.styleSheet.cssText = s
                } catch (e) {
                    oStyle.innerHTML = s
                }
                var oHead = $tag('head', document)[0];
                oHead.insertBefore(oStyle, oHead.firstChild);
                return oStyle;
            }
        });
        myFocus.extend({//Method(myFocus)
            isIE: !!(document.all && navigator.userAgent.indexOf('Opera') === -1),//!(+[1,]) BUG IN IE9+
            addEvent: function (o, type, fn) {
                var ie = this.isIE, e = ie ? 'attachEvent' : 'addEventListener', t = (ie ? 'on' : '') + type;
                o[e](t, function (e) {
                    var e = e || window.event, flag = fn.call(o, e);
                    if (flag === false) {
                        if (ie) e.cancelBubble = true, e.returnValue = false;
                        else e.stopPropagation(), e.preventDefault();
                    }
                }, false);
            },
            loadXML: function (p) {
                var xmlhttp = window.XMLHttpRequest ? new XMLHttpRequest() : new ActiveXObject("Microsoft.XMLDOM");
                xmlhttp.open("GET", p.xmlFile + "?" + Math.random(), false);
                xmlhttp.send(null);
                this.appendXML(xmlhttp.responseXML, p);
            },
            appendXML: function (xml, p) {
                var items = xml.documentElement.getElementsByTagName("item"), len = items.length;
                var html = ['<div class="loading"></div><div class="pic"><ul>'];
                for (var i = 0; i < len; i++) {
                    html.push('<li><a href="' + items[i].getAttribute('href') + '"><img src="' + items[i].getAttribute('image') + '" thumb="' + items[i].getAttribute('thumb') + '" alt="' + items[i].getAttribute('title') + '" text="' + items[i].getAttribute('text') + '" /></a></li>');
                }
                html.push('</ul></div>');
                p.$o[0].innerHTML = html.join('');
            }
        });

        myFocus.pattern.extend({
            'mF_51xflash': function (settings, $) {
                var o = document.getElementById(settings.id), list = o.getElementsByTagName('li');
                for (var j = 0, len = list.length; j < len; j++) {
                    if (j >= 4) list[j].parentNode.removeChild(list[j]);
                }
                var $focus = $(settings);
                var $picBox = $focus.find('.pic');
                var $picUl = $picBox.find('ul');
                var $picList = $picUl.find('li');
                var $txtList = $focus.addListTxt().find('li');
                var $playBtn = $focus.addHtml('<div class="play"></div>');
                //CSS
                var pad = 4, w = (settings.width / 3), h = (settings.height - pad * 2) / 3, disX = w + pad,
                    disY = h + pad, txtH = settings.txtHeight;
                $focus[0].style.cssText = 'width:' + (settings.width + disX) + 'px;height:' + (settings.height + txtH + (txtH === 0 ? 0 : pad)) + 'px;';//焦点图盒子
                $picBox[0].style.cssText = 'width:' + (settings.width + disX) + 'px;height:' + settings.height + 'px;';//图片盒子
                for (var i = 0, n = $picList.length; i < n; i++) {
                    $txtList[i].style.display = 'none';
                    $picList[i].style.cssText = 'width:' + w + 'px;height:' + h + 'px;top:' + disY * (i - 1) + 'px;';
                }
                //PLAY
                $focus.play(function (prev, n) {
                    $txtList[prev].style.display = 'none';
                }, function (next, n, prev) {
                    $picList[prev].style.zIndex = 2, $picList[next].style.zIndex = 1;
                    $picList.eq(prev).slide({
                        right: 0,
                        top: parseInt($picList[next].style.top),
                        width: w,
                        height: h
                    }, 400, function () {
                        this.style.zIndex = ''
                    });
                    $picList.eq(next).slide({right: disX, top: 0, width: settings.width, height: settings.height}, 400);
                    $txtList[next].style.display = '';
                });
                //Control
                settings.trigger = 'click';//click only
                $focus.bindControl($picList);
                //Toggle Play
                $playBtn.bind('click', function () {
                    if ($focus.isStop) {//to play
                        $focus.isStop = false;
                        this.className = 'play';
                    } else {//to stop
                        $focus.isStop = true;
                        this.className = 'stop';
                    }
                });
            },
            'mF_classicHB': function (settings, $) {//*********************经典怀旧系列二--海报风格******************
                var $focus = $(settings);
                var $picList = $focus.find('.pic li');
                var $txtList = $focus.addListTxt().find('li');
                var $numList = $focus.addListNum().find('li');
                //CSS
                var w = settings.width, h = settings.height, txtH = settings.txtHeight;//设置默认的文字高度
                for (var i = 0, n = $picList.length; i < n; i++) {
                    $picList.eq(i).css({
                        display: 'none',
                        top: -0.1 * h,
                        left: -0.1 * w,
                        width: 1.2 * w,
                        height: 1.2 * h
                    });
                    $txtList.eq(i).css({top: -txtH});
                }
                //PLAY
                $focus.play(function (i) {
                    $picList.eq(i).stop().css({
                        display: 'none',
                        top: -0.1 * h,
                        left: -0.1 * w,
                        width: 1.2 * w,
                        height: 1.2 * h
                    });
                    $txtList.eq(i).stop().css({top: -txtH});
                    $numList.eq(i).slide({width: 16}, 200)[0].className = '';
                }, function (i) {
                    $picList.eq(i).fadeIn(300).slide({width: w, height: h, top: 0, left: 0}, 300);
                    $txtList.eq(i).slide({top: 0}, 300);
                    $numList.eq(i).slide({width: 26}, 200)[0].className = 'current';
                });
                //Control
                $focus.bindControl($numList);
            },
            'mF_classicHC': function (settings, $) {//*********************经典怀旧系列一--慧聪风格******************
                var $focus = $(settings);
                var $picList = $focus.find('.pic li');
                var $txtList = $focus.addListTxt().find('li');
                var $numBg = $focus.addHtml('<div class="num_bg"></div>');
                var $numBox = $focus.addListNum();
                var $numList = $numBox.find('li');
                //CSS
                var txtH = settings.txtHeight, w = settings.width, h = settings.height;
                $focus.css({width: w + 2, height: h + txtH + 2});
                $numBg[0].style.bottom = $numBox[0].style.bottom = txtH + 1 + 'px';
                for (var i = 0, n = $picList.length; i < n; i++) {
                    $picList[i].style.display = 'none';
                    $txtList[i].style.cssText = 'display:none;top:' + (h + 2) + 'px;width:' + (w + 2) + 'px';
                }
                //PLAY
                $focus.play(function (i) {
                    $picList[i].style.display = 'none';
                    $txtList[i].style.display = 'none';
                    $numList[i].className = '';
                }, function (i) {
                    $picList.eq(i).fadeIn();
                    $txtList[i].style.display = '';
                    $numList[i].className = 'current';
                });
                //Control
                $focus.bindControl($numList);
            },
            'mF_dleung': function (settings, $) {
                var $focus = $(settings);
                var $picList = $focus.find('.pic li');
                var $txtList = $focus.addListTxt().find('li');
                //CSS
                var n = $txtList.length, txtW = Math.ceil((settings.width - 2 * (n - 1)) / n);
                $txtList.each(function (i) {
                    this.style.width = txtW + 'px'
                });
                //PLAY
                $focus.play(function (i) {
                    $picList.eq(i).fadeOut(200, 'easeIn');
                    $txtList[i].className = '';
                }, function (i) {
                    $picList.eq(i).fadeIn(500, 'easeIn');
                    $txtList[i].className = 'current';
                });
                //Control
                $focus.bindControl($txtList);
            },
            'mF_kdui': function (settings, $) {
                var $focus = $(settings);
                var $picList = $focus.find('.pic li');
                var $txtList = $focus.addListTxt().find('li');
                var $dotList = $focus.addList('dot').find('li');
                var $prevBtn = $focus.addHtml('<div class="prev"></div>');
                var $nextBtn = $focus.addHtml('<div class="next"></div>');
                //PLAY
                var param = {isRunning: false};
                $focus.play(null, function (i, n, l) {
                    param.isRunning = true;
                    $txtList.eq(l).slide({left: -settings.width}, 800, 'easeInBack');
                    fx(i, l);//延迟执行部分
                });

                function fx(i, l) {
                    setTimeout(function () {
                        $txtList.eq(i).css({display: 'block', left: settings.width}).slide({left: 0}, 300);
                        $picList.eq(i).css({display: 'block', left: settings.width}).slide({left: 0}, function () {
                            param.isRunning = false
                        });
                        if (i !== l) $picList.eq(l).slide({left: -settings.width}, 700);
                        $dotList[l].className = '';
                        $dotList[i].className = 'current';
                    }, 600);
                }

                //Control
                $focus.bindControl($dotList, param);
                $prevBtn.bind('click', function () {
                    if (!param.isRunning) $focus.run('-=1')
                });
                $nextBtn.bind('click', function () {
                    if (!param.isRunning) $focus.run('+=1')
                });
                $focus.bind('mouseover', function () {
                    $prevBtn.addClass('arr-hover'), $nextBtn.addClass('arr-hover')
                });
                $focus.bind('mouseout', function () {
                    $prevBtn.removeClass('arr-hover'), $nextBtn.removeClass('arr-hover')
                });
            },
            'mF_expo2010': function (settings, $) {
                var $focus = $(settings);
                var $picList = $focus.find('.pic li');
                var $txtList = $focus.addListTxt().find('li');
                var $numList = $focus.addListNum().find('li');
                $focus.addHtml('<div class="txt_bg"></div>');
                //CSS
                var txtH = settings.txtHeight;
                $picList.each(function (i) {
                    this.style.display = 'none';
                    $txtList[i].style.bottom = -txtH + 'px';
                });
                //PLAY
                $focus.play(function (prev) {
                    $picList.eq(prev).fadeOut();
                    $numList[prev].className = '';
                }, function (next, n, prev) {
                    $picList.eq(next).fadeIn();
                    $txtList.eq(prev).slide({bottom: -txtH}, 200, 'swing', function () {
                        $txtList.eq(next).slide({bottom: 0}, 200);
                    });
                    $numList[next].className = 'current';
                });
                //Control
                settings.delay = 200;//固定其延迟
                $focus.bindControl($numList);
            },
            'mF_fancy': function (settings, $) {
                var $focus = $(settings);
                var $txtList = $focus.addListTxt().find('li');
                var $numList = $focus.addListNum().find('li'), n = $numList.length;
                var $thumbList = $focus.addListThumb().find('li');
                var $picUls = $focus.find('.pic ul').repeat(n);
                var $prevBtn = $focus.addHtml('<div class="prev"><a href="javascript:;">&#8249;</a></div>');
                var $nextBtn = $focus.addHtml('<div class="next"><a href="javascript:;">&#8250;</a></div>');
                var $picListArr = [];
                //CSS
                var w = Math.round(settings.width / n);
                $picUls.each(function (i) {
                    $(this).css({width: w * (i + 1), zIndex: n - i});
                    $picListArr.push($(this).find('li'));
                });
                var numW = Math.round((settings.width - n + 1) / n);
                $numList.each(function (i) {
                    this.style.width = numW + 'px';
                    if (i === (n - 1)) this.style.width = numW + 1 + 'px';//最后一个加1px
                    $thumbList.eq(i)[0].style.width = w + 'px';
                });
                $focus[0].style.height = settings.height + 12 + 'px';
                //PLAY
                var params = {isRunning: false}, done = 0;//记录运行状态
                var eff = {1: 'surf', 2: 'sliceUpDown', 3: 'fold', 4: 'sliceDown', 5: 'fade', 6: settings.effect};//过渡效果列表
                var effIndex = eff[6] === 'random' ? 0 : 6;
                $focus.play(function (i) {
                    params.isRunning = true;
                    $numList[i].className = '';
                    $txtList[i].className = '';
                }, function (i) {
                    $numList[i].className = 'current';
                    $txtList[i].className = 'current';
                    var r = effIndex || (1 + Math.round(Math.random() * 4));
                    for (var j = 0; j < n; j++) effect[eff[r]]($picListArr[j], j, i);
                });
                //过渡效果函数
                var effect = {
                    sliceDown: function ($picList, j, i) {
                        var css = {zIndex: 1, display: 'block', height: 0}, param = {height: settings.height},
                            animTime = (j + 1) * 100 + 400, t = 50;
                        this.fx($picList, j, i, css, param, animTime, t);
                    },
                    sliceUpDown: function ($picList, j, i) {
                        var isSin = j % 2;
                        var css = {
                            zIndex: 1,
                            display: 'block',
                            height: 0,
                            bottom: isSin ? 'auto' : 0,
                            top: isSin ? 0 : 'auto'
                        }, param = {height: settings.height}, animTime = 500, t = 50;
                        this.fx($picList, j, i, css, param, animTime, t);
                    },
                    fold: function ($picList, j, i) {
                        var css = {zIndex: 1, display: 'block', width: w * j}, param = {width: w * (j + 1)},
                            animTime = 600, t = 50;
                        this.fx($picList, j, i, css, param, animTime, t);
                    },
                    surf: function ($picList, j, i) {
                        var css = {zIndex: 1, display: 'block', left: -settings.width + j * w}, param = {left: 0},
                            animTime = (j + 1) * 50 + 300, t = 50;
                        this.fx($picList, j, i, css, param, animTime, t);
                    },
                    fx: function ($picList, j, i, css, param, animTime, t) {
                        setTimeout(function () {
                            $picList.eq(i).css(css).slide(param, animTime, function () {
                                $picList.each(function () {
                                    this.style.display = 'none'
                                });
                                this.style.cssText = 'z-index:"";display:block';
                                done += 1;
                                if (done === n) params.isRunning = false, done = 0;
                            });
                        }, j * t);
                    },
                    fade: function ($picList, j, i) {
                        setTimeout(function () {
                            $picList.eq(i).css({zIndex: 1, display: 'block'}).fadeIn(function () {
                                $picList.each(function () {
                                    this.style.display = 'none'
                                });
                                this.style.cssText = 'z-index:"";display:block';
                                done += 1;
                                if (done === n) params.isRunning = false, done = 0;
                            });
                        }, j * 100);
                    }
                }
                //Control
                settings.trigger = 'click';//固定为'click'触发模式
                $focus.bindControl($numList, params);
                $focus.bindControl($thumbList, params);
                $prevBtn.bind('click', function () {
                    if (!params.isRunning) $focus.run('-=1')
                });
                $nextBtn.bind('click', function () {
                    if (!params.isRunning) $focus.run('+=1')
                });
                $focus.bind('mouseover', function () {
                    $prevBtn.addClass('show'), $nextBtn.addClass('show')
                });
                $focus.bind('mouseout', function () {
                    $prevBtn.removeClass('show'), $nextBtn.removeClass('show')
                });
                //Thumb Show Event
                var onthumb = false;
                $numList.each(function (i) {
                    $(this).bind('mouseover', function () {
                        if (this.className.indexOf('current') !== -1) return;
                        $thumbList.eq(i).css({visibility: 'visible'}).slide({top: 28}, 400).fadeIn(400, 'easeOut');
                    });
                    $(this).bind('mouseout', function () {
                        setTimeout(function () {
                            if (!onthumb) $thumbList.eq(i).slide({top: 0}, 400).fadeOut(400, 'easeOut', function () {
                                this.style.visibility = 'hidden'
                            });
                        }, 0);
                    });
                });
                $thumbList.each(function (i) {
                    $(this).bind('mouseover', function () {
                        onthumb = true;
                    });
                    $(this).bind('mouseout', function () {
                        onthumb = false;
                        $thumbList.eq(i).slide({top: 0}, 400).fadeOut(400, 'easeOut', function () {
                            this.style.visibility = 'hidden'
                        });
                    });
                });
            },
            'mF_fscreen_tb': function (settings, $) {
                var $focus = $(settings);
                var $picList = $focus.find('.pic li');
                var $txtList = $focus.addListTxt().find('li');
                var $thumbBg = $focus.addHtml('<div class="thumb_bg"></div>');
                var $thumbBox = $focus.addListThumb();
                var $thumbUl = $thumbBox.find('ul');
                var $thumbList = $thumbUl.find('li');
                var $prevBtn = $focus.addHtml('<div class="prev"><a href="javascript:;">&#8249;</a></div>');
                var $nextBtn = $focus.addHtml('<div class="next"><a href="javascript:;">&#8250;</a></div>');
                //CSS
                var p = settings, showNum = p.thumbShowNum, thuBoxWidth = p.width - p.thumbBtnWidth * 2,
                    thuWidth = Math.round(thuBoxWidth / showNum), n = $picList.length;
                $thumbBg.css({height: p.thumbHeight});
                $thumbBox.css({width: thuBoxWidth, height: p.thumbHeight, left: p.thumbBtnWidth});
                $thumbUl.css({width: thuWidth * n});
                $thumbList.each(function () {
                    this.style.width = thuWidth + 'px'
                });
                $thumbBox.find('img').each(function () {
                    this.style.height = (p.thumbHeight - 9 * 2) + 'px';
                });//8px margin+1px border
                $txtList.each(function () {
                    this.style.left = -settings.width + 'px'
                });
                //PLAY
                $focus.play(function (i, n) {
                    $txtList.eq(i).stop().css({left: -settings.width});
                    $picList[i].style.display = 'none';
                    $thumbList[i].className = '';
                }, function (i, n) {
                    $picList.eq(i).fadeIn(300, function () {
                        $txtList.eq(i).slide({left: 0});
                    });
                    $thumbList[i].className = 'current';
                    $thumbList.scrollTo(i);
                });
                //Control
                p.trigger = 'click';//让其仅支持'click'点击
                $focus.bindControl($thumbList);
                //Prev & Next
                $prevBtn.bind('click', function () {
                    $focus.run('-=1')
                });
                $nextBtn.bind('click', function () {
                    $focus.run('+=1')
                });
            },
            'mF_games_tb': function (settings, $) {
                var $focus = $(settings);
                var $picBox = $focus.find('.pic');
                var $picList = $picBox.find('li');
                var $txtList = $focus.addListTxt().find('li');
                var $thumbBox = $focus.addListThumb();
                var $thumbUl = $thumbBox.find('ul');
                var $thumbList = $thumbUl.find('li');
                var $prevBtn = $focus.addHtml('<div class="prev"><a href="javascript:;">&#8249;</a></div>');
                var $nextBtn = $focus.addHtml('<div class="next"><a href="javascript:;">&#8250;</a></div>');
                //CSS
                var p = settings, showNum = p.thumbShowNum, thuBoxWidth = p.width - p.thumbBtnWidth * 2,
                    thuWidth = Math.round(thuBoxWidth / showNum), n = $picList.length;
                $focus[0].style.height = p.height + p.thumbHeight + 'px';
                $thumbBox.css({width: thuBoxWidth, height: p.thumbHeight, left: p.thumbBtnWidth});
                $thumbUl.css({width: thuWidth * n});
                $thumbList.each(function () {
                    this.style.width = thuWidth + 'px'
                });
                $thumbBox.find('img').each(function () {
                    this.style.height = (p.thumbHeight - 13 * 2) + 'px';
                });//10px margin+3px border
                $txtList.each(function () {
                    this.style.bottom = p.thumbHeight + 'px'
                });
                //PLAY
                $focus.play(function (i) {
                    $picList[i].style.display = 'none';
                    $txtList[i].style.display = 'none';
                    $thumbList[i].className = '';
                }, function (i) {
                    $picList.eq(i).fadeIn();
                    $txtList[i].style.display = 'block';
                    $thumbList.scrollTo(i)[i].className = 'current';
                });
                //Control
                p.trigger = 'click';//让其仅支持'click'点击
                $focus.bindControl($thumbList);
                //Prev & Next
                $prevBtn.bind('click', function () {
                    $focus.run('-=1')
                });
                $nextBtn.bind('click', function () {
                    $focus.run('+=1')
                });
            },
            'mF_kiki': function (settings, $) {
                var $focus = $(settings);
                var $picList = $focus.find('.pic li');
                var $txtList = $focus.addListTxt().find('li');
                var $numList = $focus.addListNum().find('li');
                var $navBox = $focus.addHtml('<div class="nav"><a class="prev">PREV</a><span>|</span><a class="next">NEXT</a></div>');
                var $prevBtn = $navBox.find('.prev');
                var $nextBtn = $navBox.find('.next');
                //CSS
                var d1 = settings.width / 2, d2 = settings.height / 2, txtH = settings.txtHeight;
                $focus[0].style.height = settings.height + txtH + 'px';
                //PLAY
                var s1, s2 = 1, first = true;
                switch (settings.turn) {
                    case 'left' :
                        s1 = 1, s2 = 1;
                        break;
                    case 'right':
                        s1 = 1, s2 = -1;
                        break;
                    case 'up'   :
                        s1 = 2, s2 = 1;
                        break;
                    case 'down' :
                        s1 = 2, s2 = -1;
                        break;
                }
                $focus.play(null, function (next, n, prev) {
                    $numList[prev].className = '', $numList[next].className = 'current';
                    var tt = s1 == 1 ? 1 : (s1 == 2 ? 2 : Math.round(1 + (Math.random() * (2 - 1)))), dis, d, p_s1 = {},
                        p_s2 = {}, p_e = {};
                    dis = tt == 1 ? d1 : d2, d = s2 * dis, p_s1[tt == 1 ? 'left' : 'top'] = d, p_s2[tt == 1 ? 'left' : 'top'] = -d, p_e[tt == 1 ? 'left' : 'top'] = 0;
                    if (!first) $picList.eq(prev).stop().css({left: 0, top: 0, zIndex: 3});
                    if (!first) $picList.eq(next).stop().css({left: 0, top: 0, zIndex: 2});
                    $picList.eq(prev).slide(p_s2, 300, 'linear', function () {
                        $txtList[prev].style.display = 'none', this.style.zIndex = 1;
                        $(this).slide(p_e, 800, function () {
                            this.style.zIndex = '';
                        });
                    });
                    $picList.eq(next).slide(p_s1, 300, 'linear', function () {
                        $txtList[next].style.display = 'block', this.style.zIndex = 3;
                        $(this).slide(p_e, 800);
                    });
                    first = false;
                });
                //Control
                $focus.bindControl($numList);
                //prev & next
                $prevBtn.bind('click', function () {
                    $focus.run('-=1')
                });
                $nextBtn.bind('click', function () {
                    $focus.run('+=1')
                });
            },
            'mF_ladyQ': function (settings, $) {
                var $focus = $(settings);
                var $picList = $focus.find('.pic li');
                var $txtList = $focus.addListTxt().find('li');
                var $numList = $focus.addListNum().find('li');
                var $bar = $focus.addHtml('<div class="time_bar"></div>');
                //CSS
                var n = $picList.length, numH = 28, barW = settings.width - 23 * n - 6;
                $focus[0].style.height = settings.height + numH + 'px';
                $bar[0].style.cssText = 'top:' + (settings.height + 4) + 'px;width:' + barW + 'px;' + (settings.timeBar ? '' : 'display:none');
                for (var i = 0; i < n; i++) $txtList[i].style.bottom = numH - 1 + 'px';
                //PLAY
                var over = false, start = true, t = settings.time * 1000, params = {isRunning: false};
                $focus.play(function (i) {
                    params.isRunning = true;
                    $txtList[i].className = '';
                    $numList[i].className = '';
                    if (settings.timeBar) $bar.stop()[0].style.width = barW + 'px';
                    if (settings.timeBar && !over) $bar.slide({width: 0}, t, 'linear');
                }, function (i) {
                    $picList.eq(i).css({zIndex: 1}).fadeIn(600, 'swing', function () {
                        $picList.each(function () {
                            this.style.display = 'none'
                        });
                        this.style.cssText = 'z-index:"";display:block';
                        params.isRunning = false;
                    });
                    $txtList[i].className = 'current';
                    $numList[i].className = 'current', start = false;
                });
                //Control
                $focus.bindControl($numList, params);
                //Stop time bar event
                if (settings.timeBar) {
                    $focus.bind('mouseover', function () {
                        $bar.stop()[0].style.width = barW + 'px', over = true;
                    });
                    $focus.bind('mouseout', function () {
                        $bar.slide({width: 0}, t, 'linear'), over = false;
                    });
                }
            },
            'mF_liquid': function (settings, $) {
                var $focus = $(settings);
                var $picBox = $focus.find('.pic');
                var $picList = $picBox.find('li');
                var $txtList = $focus.addListTxt().find('li');
                var $numList = $focus.addListNum().find('li');
                var $picModList = $focus.addHtml('<div class="pic_mod"></div>').html($picBox.html()).find('img');
                //CSS
                var w = settings.width, h = settings.height;
                $picModList.each(function (i) {
                    $picList[i].style.cssText = 'width:0px;z-index:1;';
                    this.style.cssText = 'width:' + w * 10 + 'px;height:' + h + 'px;left:' + w + 'px;';
                });
                //PLAY
                $focus.play(function (i) {
                    $picList.eq(i).stop()[0].style.width = 0 + 'px';
                    $picModList.eq(i).stop()[0].style.left = w + 'px';
                    $txtList[i].style.display = 'none';
                    $numList[i].className = '';
                }, function (i) {
                    $picModList.eq(i).slide({left: 0}, 100, 'linear', function () {
                        $picList.eq(i).slide({width: w}, 700);
                        $(this).slide({left: -9 * w}, 700);
                    });
                    $txtList[i].style.display = 'block';
                    $numList[i].className = 'current';
                });
                //Control
                settings.delay = 0;//mouseover模式时延迟固定为0以兼容IE6
                $focus.bindControl($numList);
            },
            'mF_liuzg': function (settings, $) {
                var $focus = $(settings);
                var $picBox = $focus.find('.pic');
                var $picList = $picBox.find('li');
                var $txtList = $focus.addListTxt().find('li');
                var $numList = $focus.addListNum().find('li');
                //HTML++
                var c = Math.floor(settings.height / settings.chipHeight), n = $txtList.length, html = ['<ul>'];
                for (var i = 0; i < c; i++) {
                    html.push('<li><div>');
                    for (var j = 0; j < n; j++) html.push($picList[j].innerHTML);
                    html.push('</div></li>');
                }
                html.push('</ul>');
                $picBox[0].innerHTML = html.join('');
                //CSS
                var w = settings.width, h = settings.height, cH = Math.round(h / c);
                var $picList = $picBox.find('li'), $picDivList = $picBox.find('div');
                $picList.each(function (i) {
                    $picList.eq(i).css({width: w, height: cH});
                    $picDivList.eq(i).css({height: h * n, top: -i * h});
                });
                $picBox.find('a').each(function () {
                    this.style.height = h + 'px'
                });
                //PLAY
                $focus.play(function (i) {
                    $txtList[i].style.display = 'none';
                    $numList[i].className = '';
                }, function (i) {
                    var tt = settings.type || Math.round(1 + Math.random() * 2);//效果选择
                    var dur = tt === 1 ? 1200 : 300;
                    for (var j = 0; j < c; j++) {
                        $picDivList.eq(j).slide({top: -i * h - j * cH}, tt === 3 ? Math.round(300 + (Math.random() * (1200 - 300))) : dur);
                        dur = tt === 1 ? dur - 150 : dur + 150;
                    }
                    $txtList[i].style.display = 'block';
                    $numList[i].className = 'current';
                });
                //Control
                $focus.bindControl($numList);
            },
            'mF_luluJQ': function (settings, $) {
                var $focus = $(settings);
                $focus.find('.pic a').each(function () {
                    var $o = $(this), txt = $o.find('img')[0].alt;
                    $o.addHtml('<span><b>' + txt + '</b><i></i></span>');
                });
                var $picBox = $focus.find('.pic');
                var $picList = $focus.find('li');
                var $imgList = $picBox.find('img');
                var $txtList = $focus.find('span');
                var $txtBgList = $focus.find('i');
                //CSS
                var n = $picList.length, tabW = settings.tabWidth, txtH = settings.txtHeight, o = settings.grayOpacity;
                $focus.css({width: settings.width + (n - 1) * tabW});
                for (var i = 0; i < n; i++) {
                    if (i > 0) $picList[i].style.left = settings.width + (i - 1) * tabW + 'px';
                    $txtList[i].style.cssText = $txtBgList[i].style.cssText = 'top:0;height:' + txtH + 'px;line-height:' + txtH + 'px;'
                }
                if (settings.grayChange) $imgList.each(function () {
                    $(this).setOpacity(o)
                });
                //PLAY
                $focus.play(function (i, n) {
                    $txtList.eq(i).slide({top: 0});
                    if (settings.grayChange) $imgList.eq(i).slide({opacity: o}, 400);
                }, function (i, n) {
                    for (var j = 0; j < n; j++) {
                        if (j <= i) $picList.eq(j).slide({left: j * tabW});
                        else $picList.eq(j).slide({left: settings.width + (j - 1) * tabW});
                    }
                    $txtList.eq(i).slide({top: -txtH});
                    if (settings.grayChange) $imgList.eq(i).slide({opacity: 1}, 400);
                });
                //Control
                $focus.bindControl($picList);
            },
            'mF_pconline': function (settings, $) {
                var $focus = $(settings);
                var $picList = $focus.find('.pic li');
                var $txtList = $focus.addListTxt().find('li');
                var $numList = $focus.addListNum().find('li');
                //CSS
                var txtH = settings.txtHeight;
                $focus[0].style.height = settings.height + txtH + 'px';
                //PLAY
                $focus.play(function (i) {
                    $picList[i].style.display = 'none';
                    $txtList[i].style.display = 'none';
                    $numList[i].className = '';
                }, function (i) {
                    $picList.eq(i).fadeIn(settings.duration);
                    $txtList[i].style.display = 'block';
                    $numList[i].className = 'current';
                });
                //Control
                $focus.bindControl($numList);
            },
            'mF_peijianmall': function (settings, $) {
                var $focus = $(settings);
                var $picUl = $focus.find('.pic ul');
                var $txtList = $focus.addListTxt().find('li');
                $picUl[0].innerHTML += $picUl[0].innerHTML;//无缝复制
                //CSS
                var n = $txtList.length, dir = settings.direction, prop = dir === 'left' ? 'width' : 'height',
                    dis = settings[prop];
                $picUl.addClass(dir)[0].style[prop] = dis * n * 2 + 'px';
                $picUl.find('li').each(function () {//消除上下li间的多余间隙
                    $(this).css({width: settings.width, height: settings.height});
                });
                //PLAY
                var param = {};
                $focus.play(function (i) {
                    $txtList[i >= n ? (i - n) : i].className = '';
                }, function (i) {
                    param[dir] = -dis * i;
                    $picUl.slide(param, settings.duration, settings.easing);
                    $txtList[i >= n ? (i - n) : i].className = 'current';
                }, settings.seamless);
                //Control
                $focus.bindControl($txtList);
            },
            'mF_pithy_tb': function (settings, $) {
                var $focus = $(settings);
                var $picUl = $focus.find('.pic ul');
                $picUl[0].innerHTML += $picUl[0].innerHTML;//无缝复制
                var $picList = $picUl.find('li');
                var $txtList = $focus.addListTxt().find('li');
                var $thumbBox = $focus.addListThumb();
                var $thumbUl = $thumbBox.find('ul');
                var $thumbList = $thumbUl.find('li');
                var $prevBtn = $focus.addHtml('<div class="prev"><a href="javascript:;">&and;</a></div>');
                var $nextBtn = $focus.addHtml('<div class="next"><a href="javascript:;">&or;</a></div>');
                var $prevBtn2 = $focus.addHtml('<div class="prev-2"></div>');
                var $nextBtn2 = $focus.addHtml('<div class="next-2"></div>');
                //CSS
                var p = settings,
                    showNum = p.thumbShowNum,
                    thuBoxHeight = p.height - p.thumbBtnHeight * 2,
                    thuHeight = Math.round(thuBoxHeight / showNum),
                    n = $txtList.length;
                $focus[0].style.width = p.width + p.thumbWidth + 'px';
                $picList.each(function () {
                    this.style.height = p.height + 'px'
                });
                $thumbBox.css({width: p.thumbWidth, height: thuBoxHeight, top: p.thumbBtnHeight});
                $thumbList.each(function () {
                    this.style.height = thuHeight + 'px'
                });
                $thumbBox.find('img').each(function () {
                    $(this).css({height: thuHeight - 12, width: p.thumbWidth - 21})
                });//减去padding+margin+border
                $prevBtn[0].style.height = $nextBtn[0].style.height = p.thumbBtnHeight + 'px';//暂时不用
                $prevBtn2.css({left: 0.86 * p.width});
                $nextBtn2.css({left: 0.86 * p.width + 23});
                //PLAY
                $focus.play(function (i) {
                    var index = i >= n ? (i - n) : i;
                    $txtList[index].style.display = 'none';
                    $thumbList[index].className = '';
                }, function (i) {
                    var index = i >= n ? (i - n) : i;
                    $picUl.slide({top: -settings.height * i}, 600);
                    $txtList[index].style.display = 'block';
                    $thumbList.scrollTo(index)[index].className = 'current';
                }, p.seamless);
                //Control
                p.trigger = 'click';//trigger限定为click
                $focus.bindControl($thumbList);
                //Prev & Next
                $prevBtn2.bind('click', function () {
                    $focus.run('-=1')
                });
                $nextBtn2.bind('click', function () {
                    $focus.run('+=1')
                });
            },
            'mF_qiyi': function (settings, $) {
                var $focus = $(settings);
                var $slider = $focus.addHtml('<div class="slider"></div>');
                $slider[0].appendChild($focus.find('.pic')[0]);
                $slider[0].appendChild($focus.addListTxt()[0]);
                var $picList = $focus.find('.pic li');
                var $txtList = $focus.find('.txt li');
                var $numList = $focus.addListNum().find('li');
                //CSS
                var w = settings.width, txtH = settings.txtHeight, n = $picList.length;
                $slider[0].style.width = w * n + 'px';
                for (var i = 0; i < n; i++) $picList[i].style.width = w + 'px';
                //PLAY
                $focus.play(function (i) {
                    $numList[i].className = '';
                }, function (i) {
                    $txtList[i].style.top = 0 + 'px';//复位
                    $slider.slide({left: -w * i}, 800, function () {
                        $txtList.eq(i).slide({top: -txtH});
                    });
                    $numList[i].className = 'current';
                });
                //Control
                $focus.bindControl($numList);
            },
            'mF_quwan': function (settings, $) {
                var $focus = $(settings);
                var $picList = $focus.find('.pic li');
                var $txtList = $focus.addListTxt().find('li');
                var $numList = $focus.addListNum().find('li');
                //CSS
                var numH = $numList.css('height');
                $txtList.each(function () {
                    $(this).css({bottom: numH + 1});
                });
                $focus.css({height: settings.height + numH + 1});
                //PLAY
                $focus.play(function (i) {
                    $picList[i].style.display = 'none';
                    $txtList[i].style.display = 'none';
                    $numList[i].className = '';
                }, function (i) {
                    $picList.eq(i).fadeIn();
                    $txtList[i].style.display = 'block';
                    $numList[i].className = 'current';
                });
                //Control
                $focus.bindControl($numList);
            },
            'mF_rapoo': function (settings, $) {
                var $focus = $(settings);
                var $picList = $focus.find('.pic li');
                var $txtBox = $focus.addListTxt();
                var $txtList = $txtBox.find('li');
                var $numBox = $focus.addListNum();
                var $numList = $numBox.find('li');
                var $prevBtn = $focus.addHtml('<div class="prev"><a href="javascript:;">&#8249;</a></div>');
                var $nextBtn = $focus.addHtml('<div class="next"><a href="javascript:;">&#8250;</a></div>');
                //CSS
                var txtW = settings.txtWidth, n = $picList.length;
                $txtBox[0].style.width = (n - 1) * 24 + txtW + 'px';
                $numBox[0].style.width = n * 24 + 6 + txtW + 'px';
                $prevBtn[0].style.right = parseInt($numBox[0].style.width) + 26 + 'px';
                for (var i = 0; i < n; i++) {
                    $txtList[i].style.left = i * 24 + 'px';
                    $picList[i].style.left = settings.width + 'px';
                }
                //PLAY
                $focus.play(function (i) {
                    $txtList[i].style.width = 0 + 'px';
                    $numList[i].className = '';
                    $numList.each(function () {
                        this.style.marginLeft = 6 + 'px'
                    });
                    $nextBtn[0].style.right = 88 + 'px';
                }, function (next, n, prev) {
                    $picList[next].style.zIndex = 1;
                    $picList.eq(next).slide({left: 0}, 400, 'easeInOut', function () {
                        this.style.zIndex = '';
                        if (prev !== next) $picList[prev].style.left = settings.width + 'px';
                    });
                    $txtList.eq(next).slide({width: txtW}, 400, 'easeInOut');
                    $nextBtn.slide({right: 14}, 400, 'easeInOut');
                    $numList[next].className = 'current';
                    if (next < n - 1) $numList.eq(next + 1).slide({marginLeft: txtW + 12}, 400, 'easeInOut');
                });
                //Control
                settings.trigger = 'click';//固定trigger类型为‘click’
                $focus.bindControl($numList);
                //Prev & Next
                $prevBtn.bind('click', function () {
                    $focus.run('-=1')
                });
                $nextBtn.bind('click', function () {
                    $focus.run('+=1')
                });
            },
            'mF_shutters': function (settings, $) {
                var c = Math.floor(settings.width / 50);
                var $focus = $(settings);
                var $txtList = $focus.addListTxt().find('li');
                var $picUls = $focus.find('.pic ul').repeat(c);
                var $prevBtn = $focus.addHtml('<div class="prev"><a href="javascript:;">PREV</a></div>');
                var $nextBtn = $focus.addHtml('<div class="next"><a href="javascript:;">NEXT</a></div>');
                var $picListArr = [];
                //CSS
                var w = settings.width / c;
                $picUls.each(function (i) {
                    $(this).css({width: w * (i + 1), zIndex: c - i});
                    $picListArr.push($(this).find('li'));
                });
                //PLAY
                var running = false, done = 0;//记录运行状态
                $focus.play(function (i) {
                    running = true;
                    $txtList[i].className = '';
                }, function (i) {
                    $txtList[i].className = 'current';
                    for (var j = 0; j < c; j++) timeoutFx($picListArr[j], i, (j + 1) * 100);//每切片延时100毫秒
                });

                function timeoutFx($picList, i, t) {
                    setTimeout(function () {
                        $picList.eq(i).css({zIndex: 1}).fadeIn(function () {
                            $picList.each(function () {
                                this.style.display = 'none'
                            });
                            this.style.cssText = 'z-index:"";display:block';
                            done += 1;
                            if (done === c) running = false, done = 0;
                        });
                    }, t);
                }

                //Control
                $prevBtn.bind('click', function () {
                    if (!running) $focus.run('-=1')
                });
                $nextBtn.bind('click', function () {
                    if (!running) $focus.run('+=1')
                });
                $focus.bind('mouseover', function () {
                    $prevBtn.addClass('hover'), $nextBtn.addClass('hover')
                });
                $focus.bind('mouseout', function () {
                    $prevBtn.removeClass('hover'), $nextBtn.removeClass('hover')
                });
            },
            'mF_slide3D': function (settings, $) {
                var $focus = $(settings);
                var $picUl = $focus.find('.pic ul');
                $picUl[0].innerHTML += $picUl[0].innerHTML;
                var $numList = $focus.addListNum().find('li');
                var $picList = $picUl.find('li');
                var $m11 = $focus.addHtml('<div class="mask11"></div>');
                var $m12 = $focus.addHtml('<div class="mask12"></div>');
                var $m21 = $focus.addHtml('<div class="mask21"></div>');
                var $m22 = $focus.addHtml('<div class="mask22"></div>');
                var $nextBtn = $focus.addHtml('<div class="next"><a href="javascript:;">NEXT</a></div>');
                //PLAY
                var w = settings.width,
                    h = settings.height,
                    d = Math.ceil(settings.height / 6),//立体深度
                    halfW = w / 2;
                $focus.play(function (i) {
                    $m11[0].style.cssText = $m12[0].style.cssText = 'border-width:0px ' + halfW + 'px;';
                    $m21[0].style.cssText = $m22[0].style.cssText = 'border-width:' + d + 'px 0px;';
                    $numList[i].className = '';
                }, function (next, n, prev) {
                    $picList[n].innerHTML = $picList[prev].innerHTML;
                    $picList[n + 1].innerHTML = $picList[next].innerHTML;
                    $picList.eq(n).find('img').css({width: w, height: h}).slide({width: 0});
                    $picList.eq(n + 1).find('img').css({width: 0, height: h}).slide({width: w});
                    $m11.slide({borderLeftWidth: 0, borderRightWidth: 0, borderTopWidth: d, borderBottomWidth: d});
                    $m12.slide({borderLeftWidth: 0, borderRightWidth: 0, borderTopWidth: d, borderBottomWidth: d});
                    $m21.slide({
                        borderLeftWidth: halfW,
                        borderRightWidth: halfW,
                        borderTopWidth: 0,
                        borderBottomWidth: 0
                    });
                    $m22.slide({
                        borderLeftWidth: halfW,
                        borderRightWidth: halfW,
                        borderTopWidth: 0,
                        borderBottomWidth: 0
                    });
                    $numList[next].className = 'current';
                });
                //Control
                $focus.bindControl($numList);
                //Next
                $nextBtn.bind('click', function () {
                    $focus.run('+=1')
                });
            },
            'mF_sohusports': function (settings, $) {
                var $focus = $(settings);
                var $picList = $focus.find('.pic li');
                var $txtList = $focus.addListTxt().find('li');
                var $numList = $focus.addListNum().find('li');
                //PLAY
                $focus.play(function (i) {
                    $picList[i].style.display = 'none';
                    $txtList[i].style.display = 'none';
                    $numList[i].className = '';
                }, function (i) {
                    $picList.eq(i).fadeIn();
                    $txtList[i].style.display = 'block';
                    $numList[i].className = 'current';
                });
                //Control
                $focus.bindControl($numList);
            },
            'mF_taobao2010': function (settings, $) {
                var $focus = $(settings);
                var $picUl = $focus.find('.pic ul');
                var $txtList = $focus.addListTxt().find('li');
                var $numList = $focus.addListNum().find('li');
                $picUl[0].innerHTML += $picUl[0].innerHTML;//无缝复制
                //CSS
                var n = $txtList.length, dir = settings.direction, prop = dir === 'left' ? 'width' : 'height',
                    dis = settings[prop];
                $picUl.addClass(dir)[0].style[prop] = dis * n * 2 + 'px';
                $picUl.find('li').each(function () {//消除上下li间的多余间隙
                    $(this).css({width: settings.width, height: settings.height});
                });
                //PLAY
                var param = {};
                $focus.play(function (i) {
                    var index = i >= n ? (i - n) : i;
                    $numList[index].className = '';
                    $txtList[index].style.display = 'none';
                }, function (i) {
                    var index = i >= n ? (i - n) : i;
                    param[dir] = -dis * i;
                    $picUl.slide(param, settings.duration, settings.easing);
                    $numList[index].className = 'current';
                    $txtList[index].style.display = 'block';
                }, settings.seamless);
                //Control
                $focus.bindControl($numList);
            },
            'mF_taobaomall': function (settings, $) {
                var $focus = $(settings);
                var $picBox = $focus.find('.pic');
                var $picUl = $picBox.find('ul');
                var $txtList = $focus.addListTxt().find('li');
                $picUl[0].innerHTML += $picUl[0].innerHTML;//无缝复制
                //CSS
                var n = $txtList.length, dir = settings.direction, prop = dir === 'left' ? 'width' : 'height',
                    dis = settings[prop];
                $picUl.addClass(dir)[0].style[prop] = dis * n * 2 + 'px';
                $picUl.find('li').each(function () {//消除上下li间的多余间隙
                    $(this).css({width: settings.width, height: settings.height});
                });
                var txtH = settings.txtHeight;
                $focus.css({height: settings.height + txtH + 1});
                $picBox.css({width: settings.width, height: settings.height});
                $txtList.each(function () {
                    this.style.width = (settings.width - n - 1) / n + 1 + 'px'
                });
                $txtList[n - 1].style.border = 0;
                //PLAY
                var param = {};
                $focus.play(function (i) {
                    $txtList[i >= n ? (i - n) : i].className = '';
                }, function (i) {
                    param[dir] = -dis * i;
                    $picUl.slide(param, settings.duration, settings.easing);
                    $txtList[i >= n ? (i - n) : i].className = 'current';
                }, settings.seamless);
                //Control
                $focus.bindControl($txtList);
            },
            'mF_tbhuabao': function (settings, $) {
                var $focus = $(settings);
                var $picBox = $focus.find('.pic');
                var $picUl = $picBox.find('ul');
                $picUl[0].innerHTML += $picUl[0].innerHTML;//无缝复制
                var $txtList = $focus.addListTxt().find('li');
                var $dotList = $focus.addList('dot').find('li');
                $dotList.each(function () {
                    this.innerHTML = '<a href="javascript:;">&bull;</a>'
                });//小点
                var $prevBtn = $focus.addHtml('<div class="prev"><a href="javascript:;">&#8249;</a></div>');
                var $nextBtn = $focus.addHtml('<div class="next"><a href="javascript:;">&#8250;</a></div>');
                //CSS
                var w = settings.width, h = settings.height, dotH = 32, arrTop = h / 2 - 32, n = $txtList.length;
                $focus[0].style.height = h + dotH + 'px';
                $picBox[0].style.cssText = 'width:' + w + 'px;height:' + h + 'px;';
                $picUl[0].style.width = w * 2 * n + 'px';
                $txtList.each(function () {
                    this.style.bottom = dotH + 'px'
                });
                $picUl.find('li').each(function () {
                    this.style.cssText = 'width:' + w + 'px;height:' + h + 'px;'
                });//滑动固定其大小
                $prevBtn[0].style.cssText = $nextBtn[0].style.cssText = 'top:' + arrTop + 'px;';
                //PLAY
                $focus.play(function (i) {
                    var index = i >= n ? (i - n) : i;
                    $txtList[index].style.display = 'none';
                    $dotList[index].className = '';
                }, function (i) {
                    var index = i >= n ? (i - n) : i;
                    $picUl.slide({left: -w * i});
                    $txtList[index].style.display = 'block';
                    $dotList[index].className = 'current';
                }, settings.seamless);
                //Control
                $focus.bindControl($dotList);
                //Prev & Next
                $prevBtn.bind('click', function () {
                    $focus.run('-=1')
                });
                $nextBtn.bind('click', function () {
                    $focus.run('+=1')
                });
            },
            'mF_YSlider': function (settings, $) {
                var $focus = $(settings);
                var $picList = $focus.find('.pic li');
                var $rePicList = $focus.addHtml('<div class="rePic">' + $focus.find('.pic').html() + '</div>').find('li');
                var $txtList = $focus.addListTxt().find('li');
                var $numList = $focus.addListNum().find('li');
                //PLAY
                var s = settings.direction === 'single' ? true : false, d1 = settings.width, d2 = settings.height;
                $focus.play(function (i, n) {
                    var r = s ? 1 : Math.round(1 + (Math.random() * (2 - 1))), dis, d, p = {};
                    dis = r === 1 ? d1 : d2, d = Math.round(Math.random() + s) ? dis : -dis, p[r === 1 ? 'left' : 'top'] = d;
                    $picList[i].style.display = $txtList[i].style.display = 'none';
                    $numList[i].className = '';
                    $rePicList.eq(i).css({
                        left: 0,
                        top: 0,
                        display: 'block',
                        opacity: 1
                    }).slide(p, 500, 'swing').fadeOut(500);
                }, function (i, n) {
                    $picList[i].style.display = $txtList[i].style.display = 'block';
                    $numList[i].className = 'current';
                });
                //Control
                $focus.bindControl($numList);
            }
        });

        myFocus.config.extend({
            //默认文字层高度
            'mF_51xflash': {
                txtHeight: 34
            },
            //默认文字层高度
            'mF_classicHB': {
                txtHeight: 20
            },
            'mF_classicHC': {
                txtHeight: 26
            },
            'mF_expo2010': {
                txtHeight: 36
            },
            'mF_fancy': {
                effect: 'random'
            },
            'mF_fscreen_tb': {//可选个性参数
                thumbShowNum: 4,//略缩图显示数目
                thumbBtnWidth: 28,//略缩图导航(prev/next)按钮的宽度
                thumbHeight: 72//略缩图总高度
            },
            'mF_games_tb': {//可选个性参数
                thumbShowNum: 5,//略缩图显示数目
                thumbBtnWidth: 16,//略缩图导航(prev/next)按钮的宽度
                thumbHeight: 86//略缩图总高度
            },
            'mF_kiki': {//可选个性参数
                turn: 'random',//翻牌方向,可选：'left'(左)|'right'(右)|'up'(上)|'down'(下)|'random'(单向随机)
                txtHeight: 30//标题默认高度
            },
            'mF_ladyQ': {//可选个性参数
                txtHeight: 58,//默认标题高度
                timeBar: false//是否有时间条[true(有)|false(无)]
            },
            'mF_liuzg': {//可选个性参数
                chipHeight: 36,//图片切片高度(像素)，越大切片密度越小
                type: 0////切片效果，可选：1(甩头) | 2(甩尾) | 3(凌乱) | 0(随机)
            },
            'mF_luluJQ': {//可选个性参数
                tabWidth: 68,//图片tab留边宽度(像素)
                txtHeight: 34,//文字层高度(像素)
                grayChange: true,//非当前图片是否变暗,可选：true(是) | false(否)
                grayOpacity: 0.5//非当前图片的透明度
            },
            'mF_pconline': {
                duration: 200,//图片淡出时间
                txtHeight: 28//标题高度
            },
            'mF_peijianmall': {//可选个性参数
                txtHeight: 40,//标题高度
                seamless: true,//是否无缝，可选：true(是) | false(否)
                duration: 800,//过渡时间(毫秒)，时间越大速度越小
                direction: 'left',//运动方向，可选：'top'(向上) | 'left'(向左)
                easing: 'easeOut'//运动形式，可选：'easeOut'(快出慢入) | 'easeIn'(慢出快入) | 'easeInOut'(慢出慢入) | 'swing'(摇摆运动) | 'linear'(匀速运动)
            },
            'mF_pithy_tb': {//可选个性参数
                seamless: true,//是否无缝，可选：true(是) | false(否)
                txtHeight: 38,//标题高度
                thumbShowNum: 4,//略缩图显示数目
                thumbBtnHeight: 0,//略缩图导航(prev/next)按钮的高度
                thumbWidth: 112//略缩图总宽度
            },
            'mF_qiyi': {
                txtHeight: 34
            },
            'mF_rapoo': {//可选个性参数
                txtWidth: 68,//文字段宽度(像素)
                txtHeight: 18//文字段高度(像素)
            },
            'mF_taobao2010': {//可选个性参数
                seamless: true,//是否无缝，可选：true(是)/false(否)
                duration: 600,//过渡时间(毫秒)，时间越大速度越小
                direction: 'left',//运动方向，可选：'top'(向上) | 'left'(向左)
                easing: 'easeOut'//运动形式，可选：'easeOut'(快出慢入) | 'easeIn'(慢出快入) | 'easeInOut'(慢出慢入) | 'swing'(摇摆运动) | 'linear'(匀速运动)
            },
            'mF_taobaomall': {//可选个性参数
                txtHeight: 28,//默认标题按钮高度
                seamless: true,//是否无缝：true(是)| false(否)
                duration: 600,//过渡时间(毫秒)，时间越大速度越小
                direction: 'top',//运动方向，可选：'top'(向上) | 'bottom'(向下) | 'left'(向左) | 'right'(向右)
                easing: 'easeOut'//运动形式，可选：'easeOut'(快出慢入) | 'easeIn'(慢出快入) | 'easeInOut'(慢出慢入) | 'swing'(摇摆运动) | 'linear'(匀速运动)
            },
            'mF_tbhuabao': {
                seamless: true
            },
            'mF_YSlider': {
                direction: 'random'
            }
        });

        myFocus.extend(myFocus.fn.easing, {//扩展缓动方法
            easeInBack: function (t, b, c, d, s) {
                if (s == undefined) s = 1.70158;
                return c * (t /= d) * t * ((s + 1) * t - s) + b;
            }
        });

        if (typeof jQuery !== 'undefined') {
            $.fn.extend({
                myFocus: function (p, a) {
                    if (!p) p = {};
                    p.id = this[0].id;
                    if (!p.id) p.id = this[0].id = 'mF__ID__';
                    myFocus.set(p, a)
                }
            });
        }

    })(_isFocus);

})(jQuery, window);
