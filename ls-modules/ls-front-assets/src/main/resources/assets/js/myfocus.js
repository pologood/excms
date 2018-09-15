/*!
 * myFocus JavaScript Library v2.0.4
 * https://github.com/koen301/myfocus
 *
 * Copyright 2012, Koen Lee
 * Released under the MIT license
 *
 * Date: 2012/10/28
 */
(function () {

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
            var pad = 4, w = (settings.width / 3), h = (settings.height - pad * 2) / 3, disX = w + pad, disY = h + pad, txtH = settings.txtHeight;
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
                $picList.eq(prev).slide({right: 0, top: parseInt($picList[next].style.top), width: w, height: h}, 400, function () {
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
                $picList.eq(i).css({display: 'none', top: -0.1 * h, left: -0.1 * w, width: 1.2 * w, height: 1.2 * h});
                $txtList.eq(i).css({top: -txtH});
            }
            //PLAY
            $focus.play(function (i) {
                $picList.eq(i).stop().css({display: 'none', top: -0.1 * h, left: -0.1 * w, width: 1.2 * w, height: 1.2 * h});
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
                    var css = {zIndex: 1, display: 'block', height: 0}, param = {height: settings.height}, animTime = (j + 1) * 100 + 400, t = 50;
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
                    var css = {zIndex: 1, display: 'block', width: w * j}, param = {width: w * (j + 1)}, animTime = 600, t = 50;
                    this.fx($picList, j, i, css, param, animTime, t);
                },
                surf: function ($picList, j, i) {
                    var css = {zIndex: 1, display: 'block', left: -settings.width + j * w}, param = {left: 0}, animTime = (j + 1) * 50 + 300, t = 50;
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
            var p = settings, showNum = p.thumbShowNum, thuBoxWidth = p.width - p.thumbBtnWidth * 2, thuWidth = Math.round(thuBoxWidth / showNum), n = $picList.length;
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
            var p = settings, showNum = p.thumbShowNum, thuBoxWidth = p.width - p.thumbBtnWidth * 2, thuWidth = Math.round(thuBoxWidth / showNum), n = $picList.length;
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
                var tt = s1 == 1 ? 1 : (s1 == 2 ? 2 : Math.round(1 + (Math.random() * (2 - 1)))), dis, d, p_s1 = {}, p_s2 = {}, p_e = {};
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
            var n = $txtList.length, dir = settings.direction, prop = dir === 'left' ? 'width' : 'height', dis = settings[prop];
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
                $m21.slide({borderLeftWidth: halfW, borderRightWidth: halfW, borderTopWidth: 0, borderBottomWidth: 0});
                $m22.slide({borderLeftWidth: halfW, borderRightWidth: halfW, borderTopWidth: 0, borderBottomWidth: 0});
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
            var n = $txtList.length, dir = settings.direction, prop = dir === 'left' ? 'width' : 'height', dis = settings[prop];
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
            var n = $txtList.length, dir = settings.direction, prop = dir === 'left' ? 'width' : 'height', dis = settings[prop];
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
                $rePicList.eq(i).css({left: 0, top: 0, display: 'block', opacity: 1}).slide(p, 500, 'swing').fadeOut(500);
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

})();