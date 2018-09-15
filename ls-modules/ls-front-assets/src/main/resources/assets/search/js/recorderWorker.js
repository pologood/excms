(function (window) {

    var recordWorkerCode = function () {
        return window.URL.createObjectURL(new Blob(['var outputSampleRate,outputSampleBits,inputSampleRate,recBuffers=[],outputBufferLength=0,inputSampleBits=16;function reset(){recBuffers=[]}function init(t){inputSampleRate=t.inputSampleRate,outputSampleRate=t.outputSampleRate||7350,outputSampleBits=t.outputSampleBits||16}function record(t){recBuffers.push(new Float32Array(t)),outputBufferLength+=t.length;for(var e=0,a=Math.floor(t.length/10),n=0;n<a;n++)e+=Math.abs(t[10*n]);this.postMessage({buffer:t,volume:e})}function getRecord(){var t=encodeWAV();this.postMessage({command:"end",audio:t,bufferLen:outputBufferLength})}function compress(){for(var t=new Float32Array(outputBufferLength),e=0,a=0;a<recBuffers.length;a++)t.set(recBuffers[a],e),e+=recBuffers[a].length;for(var n=parseInt(inputSampleRate/outputSampleRate),r=t.length/n,u=new Float32Array(r),o=0,s=0;o<r;)u[o]=t[s],s+=n,o++;return u}function encodeWAV(){var t=Math.min(inputSampleRate,outputSampleRate),e=Math.min(inputSampleBits,outputSampleBits),a=compress(),n=a.length*(e/8),r=new ArrayBuffer(44+n),u=new DataView(r),o=0,s=function(t){for(var e=0;e<t.length;e++)u.setUint8(o+e,t.charCodeAt(e))};if(s("RIFF"),o+=4,u.setUint32(o,36+n,!0),o+=4,s("WAVE"),o+=4,s("fmt "),o+=4,u.setUint32(o,16,!0),o+=4,u.setUint16(o,1,!0),o+=2,u.setUint16(o,1,!0),o+=2,u.setUint32(o,t,!0),o+=4,u.setUint32(o,1*t*(e/8),!0),o+=4,u.setUint16(o,e/8*1,!0),o+=2,u.setUint16(o,e,!0),o+=2,s("data"),o+=4,u.setUint32(o,n,!0),o+=4,8===e)for(var i=0;i<a.length;i++,o++){var f=(p=Math.max(-1,Math.min(1,a[i])))<0?32768*p:32767*p;f=parseInt(255/(65535/(f+32768))),u.setInt8(o,f,!0)}else for(i=0;i<a.length;i++,o+=2){var p=Math.max(-1,Math.min(1,a[i]));u.setInt16(o,p<0?32768*p:32767*p,!0)}return new Blob([u],{type:"audio/wav"})}onmessage=function(t){var e=t.data;switch(e.command){case"init":init(e.config);break;case"record":record(e.buffer);break;case"getRecord":getRecord();break;case"reset":reset()}};'], {type: "text/javascript"}));
    };

    var audioCtx = null;
    var audioStream = null;
    var rec_state = null;
    var recording = false;
    var recorderWorker = null;
    var encodeWAV = null;
    var audioNode = {
        source: null,
        scriptNode: null
    }

    var recorderStatus = {
        idle: "idle",//空闲
        sessionBegin: "ssb",//session begin 会话开始
        audioWrite: "auw",//audio write 写入音频
        getResult: "grs",//get result 获取结果
        sessionEnd: "sse"//session end 会话结束
    };

    var env = {
        bufferSize: null,
        isSupport: true
    };

    var settings = {
        serverUrl: "/search/xfyy/getData",
        recordWorkerPath: null
    };

    var callback = {
        init: function () {
        },
        onResult: function (code, re) {
        },
        onVolume: function () {
        },
        onError: function () {
        },
        onProcess: function (status) {
        }
    };

    var utils = {
        base64encode: function (str) {
            var base64EncodeChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";
            var out, i, len;
            var c1, c2, c3;
            len = str.length;
            i = 0;
            out = "";
            while (i < len) {
                c1 = str.charCodeAt(i++) & 0xff;
                if (i == len) {
                    out += base64EncodeChars.charAt(c1 >> 2);
                    out += base64EncodeChars.charAt((c1 & 0x3) << 4);
                    out += "==";
                    break;
                }
                c2 = str.charCodeAt(i++);
                if (i == len) {
                    out += base64EncodeChars.charAt(c1 >> 2);
                    out += base64EncodeChars.charAt(((c1 & 0x3) << 4) | ((c2 & 0xF0) >> 4));
                    out += base64EncodeChars.charAt((c2 & 0xF) << 2);
                    out += "=";
                    break;
                }
                c3 = str.charCodeAt(i++);
                out += base64EncodeChars.charAt(c1 >> 2);
                out += base64EncodeChars.charAt(((c1 & 0x3) << 4) | ((c2 & 0xF0) >> 4));
                out += base64EncodeChars.charAt(((c2 & 0xF) << 2) | ((c3 & 0xC0) >> 6));
                out += base64EncodeChars.charAt(c3 & 0x3F);
            }
            return out;
        },
        getBufferSize: function () {
            var ua = navigator.userAgent;
            return 4096;
            /*if (/(Win(dows )?NT 6\.2)/.test(ua)) {
                return 1024;  //Windows 8
            } else if (/(Win(dows )?NT 6\.1)/.test(ua)) {
                return 1024;  //Windows 7
            } else if (/(Win(dows )?NT 6\.0)/.test(ua)) {
                return 2048;  //Windows Vista
            } else if (/Win(dows )?(NT 5\.1|XP)/.test(ua)) {
                return 4096;  //Windows XP
            } else if (/Mac|PPC/.test(ua)) {
                return 1024;  //Mac OS X
            } else if (/Linux/.test(ua)) {
                return 8192;  //Linux
            } else if (/iPhone|iPad|iPod/.test(ua)) {
                return 2048;  //iOS
            } else {
                return 16384;  //Otherwise
            }*/
        },
        checkIsSupport: function () {
            if (!navigator.getUserMedia) {
                return false;
            }
            if (!window.AudioContext) {
                return false
            }
            if (!window.Worker) {
                return false
            }
            if (!window.URL) {
                return false;
            }
            return true
        }
    };

    var volumeCheck = (function () {
        var lowVolumeLimit = 20;//音量过小
        var interval = 500;//音量判定间隔
        var maxTooLow = 5;//录音开始多少判定点提示音量过小
        var maxVolume = 0;
        var checkEventId = 0;

        var isTooLow = false;
        var tooLowCount = 0;

        var init = function () {
            maxVolume = 0;
            isTooLow = false;
            tooLowCount = 0;
        };

        var fire = function () {
            var me = this;
            if (!isTooLow && maxVolume < lowVolumeLimit) {
                tooLowCount++;
                if (tooLowCount >= maxTooLow) {
                    isTooLow = true;
                    callback.onProcess("lowVolume");//音量太小
                }
                return
            }
            if (isTooLow && maxVolume >= lowVolumeLimit) {
                callback.onProcess("normalVolume");//正常音量
            }
            if (maxVolume >= lowVolumeLimit) {
                clearInterval(checkEventId);//一旦恢复正常，结束
                start();
                tooLowCount = 0;
            }
        };

        var start = function () {
            init();
            checkEventId = setInterval(fire, interval);
        };

        var stop = function () {
            clearInterval(checkEventId);
        };

        var listen = function (volume) {
            maxVolume = Math.max(maxVolume, volume);
        };

        return {
            start: start,
            stop: stop,
            listen: listen
        }
    })();

    var gotStream = function (stream) {
        audioStream = stream;
        audioNode.source = audioCtx.createMediaStreamSource(stream);
        audioNode.scriptNode = audioCtx.createScriptProcessor(4096, 1, 1);
        recorderWorker.init(audioCtx.sampleRate);

        audioNode.scriptNode.onaudioprocess = function (e) {
            if (!recording) return;
            var data = e.inputBuffer.getChannelData(0);
            recorderWorker.sendData(data);
        };

        audioNode.source.connect(audioNode.scriptNode);
        audioNode.scriptNode.connect(audioCtx.destination);
        iatEvent.startRecord();
    };

    var initMedia = function () {
        if (navigator.getUserMedia) {
            navigator.getUserMedia({audio: true}, gotStream, function (error) {
                var code = error.code || error.name;
                switch (code) {
                    case 'PERMISSION_DENIED':
                    case 'PermissionDeniedError':
                        callback.onError.call(this, code, '用户拒绝提供信息。');
                        break;
                    case 'NOT_SUPPORTED_ERROR':
                    case 'NotSupportedError':
                        callback.onError.call(this, code, '浏览器不支持硬件设备。');
                        break;
                    case 'MANDATORY_UNSATISFIED_ERROR':
                    case 'MandatoryUnsatisfiedError':
                        callback.onError.call(this, code, '无法发现指定的硬件设备。');
                        break;
                    default:
                        callback.onError.call(this, code, '无法打开麦克风。异常信息:' + (error.code || error.name));
                        break;
                }
            });

            if (audioCtx == null) {
                audioCtx = new window.AudioContext();
            }

        } else {
            callback.onError.call(this, code, '当前浏览器不支持录音功能。');
            return false;
        }

    };

    var iatEvent = (function () {

        var startRecord = function () {
            /* 写音频接口 或 非首次初始化 则不进行多媒体初始化*/
            if (audioStream == null) {
                initMedia();
                recorderWorker.init();
                return;
            }
            callback.onProcess('onStart');//开始启动录音
            rec_state = recorderStatus.sessionBegin;
            recording = true;
            volumeCheck.start();
            recorderWorker.reset();
        };

        var getResult = function () {
            stopRecord();//停止录音
            if (rec_state = recorderStatus.sessionBegin) {
                callback.onProcess("onStop");
            }
            rec_state = recorderStatus.getResult;
        };

        var stopRecord = function () {
            recording = false;
            volumeCheck.stop();
        };

        var abortSession = function () {
            stopRecord();
            rec_state = recorderStatus.sessionEnd;
        };

        return {
            startRecord: startRecord,
            getResult: getResult,
            stopRecord: stopRecord,
            abortSession: abortSession
        };
    })();

    var updateRecorder = function (data) {
        if (!data) {
            return false;
        }
        //data 对象为录音后的音频数据
        var fd = new FormData();
        fd.append("audio", data);
        fd.append("engineType", 'sms8k');
        fd.append("aue", 'raw');
        return $.ajax({
            url: settings.serverUrl,
            type: 'POST',
            data: fd,
            processData: false,
            contentType: false
        });
    }

    var newRecorderWorker = function (path) {
        var recorderWorker = new Worker(path);
        recorderWorker.onmessage = function (e) {
            var data = e.data;
            if (data.command == "end") {
                callback.onResult.call(this, updateRecorder(data.audio));
                rec_state = recorderStatus.sessionEnd;
                return false;
            }
            volumeCheck.listen(e.data.volume);
            callback.onVolume.call(this, e.data.volume, data.buffer);
        };

        var init = function (inputSampleRate) {
            recorderWorker.postMessage({
                command: 'init',
                config: {
                    inputSampleRate: inputSampleRate
                }
            });
        };

        var reset = function () {
            recorderWorker.postMessage({command: 'reset'});
        };

        var sendData = function (data) {
            recorderWorker.postMessage({
                command: 'record',
                buffer: data
            });
        };

        var getRecord = function () {
            recorderWorker.postMessage({command: 'getRecord'});
        }

        return {
            init: init,
            reset: reset,
            sendData: sendData,
            getRecord: getRecord
        }
    };

    var voiceWorker = function (setting) {
        var me = this;

        //init
        (function () {
            //api 统一
            navigator.getUserMedia = navigator.getUserMedia ||
                navigator.webkitGetUserMedia ||
                navigator.mozGetUserMedia ||
                navigator.msGetUserMedia;

            window.AudioContext = window.webkitAudioContext ||
                window.AudioContext ||
                window.mozAudioContext ||
                window.msAudioContext;

            window.URL = window.URL || window.webkitURL;

            env.isSupport = utils.checkIsSupport();

        })();

        // 检测环境
        me.isSupport = function () {
            return env.isSupport;
        };

        if (!env.isSupport) {
            return;
        }

        callback = $.extend(callback, setting.callback);
        settings = $.extend(settings, setting.params);

        settings.recordWorkerPath = recordWorkerCode();
        me.recorderWorker = recorderWorker = newRecorderWorker(settings.recordWorkerPath);

        // 开始录音
        me.start = function () {
            iatEvent.startRecord();
        }

        // 停止
        me.stop = function () {
            iatEvent.getResult();
        }

        me.cancel = function () {
            iatEvent.abortSession();
        };

        me.kill = function () {
            if (audioStream != null) {
                var tracks = audioStream.getAudioTracks();
                for (var i = 0; i < tracks.length; i++) {
                    tracks[i].stop();
                }
                audioStream = null;
            }
            if (audioNode.source != null) {
                // 由于部分旧浏览器不支持audioCtx.close
                audioNode.source.disconnect();
                audioNode.scriptNode.disconnect();
                audioNode.source = null;
                audioNode.scriptNode = null;
            }
        }
    };

    window.voiceWorker = voiceWorker;

})(window);