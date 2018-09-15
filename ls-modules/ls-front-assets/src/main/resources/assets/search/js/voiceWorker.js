$(document).ready(function () {

    var mic_pressed = false,
        speakTimer = null,
        time = 0,
        $round1 = $('.round1'),
        $round2 = $('.round2'),
        $round3 = $('.round3'),
        $boxInner = $('.box-inner'),
        $voiceMsg = $('.voice-msg'),
        $speakBtn = $(".speak-btn");

    var soundWave = function (flag) {
        $round1[flag == false ? 'hide' : 'show']();
        $round2[flag == false ? 'hide' : 'show']();
        if (flag == false) {
            $round3.removeAttr('style');
            $boxInner.removeAttr('style');
        }
    }

    var clearBtnInterval = function () {
        var speakTimer = $speakBtn.data('speakTimer');
        clearInterval(speakTimer);
        $speakBtn.removeClass('speaking');
        mic_pressed = false;
    }

    var session = new voiceWorker({
        callback: {
            onResult: function (ajax) {
                ajax.done(function (res) {
                    var obj = $.parseJSON(res),
                        data = obj.data;

                    if (obj.status) {
                        if (data.code == 0 && !Ls.isEmpty(data.data)) {
                            $voiceMsg.html('正在搜索结果，请稍候！');
                            var searchStr = (data.data).replace(/，|。|！/ig, " ");
                            sendKeyWords(searchStr);
                        } else {
                            $voiceMsg.html('识别失败，请点击按钮后再说一次。');
                        }
                    }
                    soundWave(false);
                    $speakBtn.removeClass('speaking');
                    mic_pressed = false;
                });
            },
            onProcess: function (code) {
                // onStop onStart
                Ls.log(code)
                if (code == 'lowVolume') {
                    session.stop();
                    soundWave(false);
                } else if (code == 'onStart') {
                    soundWave();
                    $voiceMsg.html('语音识别中...');
                } else if (code == 'onStop') {
                    soundWave(false);
                    clearBtnInterval();
                    session.recorderWorker.getRecord();
                }
            },
            onVolume: function (volume, buffer) {
                $speakBtn.addClass('speaking');

                var channelData = Math.max.apply(Math, buffer),
                    boxHeight = (1 - channelData) * (1 - channelData) * 36;

                $boxInner.height(boxHeight);

                var css = {
                    width: volume + 61 + "px",
                    height: volume + 61 + "px",
                    margin: 11 - volume / 2 + "px 0 0 -" + (volume / 2) + "px"
                }
                $(".round3").css(css);
            },
            onError: function (code, txt) {
                clearBtnInterval();
                alert(txt);
            }
        }
    });

    //页面不可见，断开麦克风调用
    document.addEventListener && document.addEventListener("visibilitychange", function () {
        if (document.hidden == true) {
            session.kill();
        }
    });

    $speakBtn.click(function () {
        var _this = $(this),
            speakTimer = _this.data('speakTimer');

        time = 0;
        clearInterval(speakTimer);
        speakTimer = setInterval(function () {
            time++;
            if (time > 30) {
                mic_pressed = false;
                clearInterval(speakTimer);
                _this.removeClass('speaking');
                session.stop();
                return;
            }
        }, 1000);

        _this.data('speakTimer', speakTimer);

        if (_this.hasClass('speaking')) {
            clearInterval(speakTimer);
            _this.removeClass('speaking');
            mic_pressed = false;
            session.stop();
            return false;
        }

        if (!mic_pressed) {
            mic_pressed = true;
            session.start();
        } else {
            clearInterval(speakTimer);
            session.stop();
        }

    });

    if (!session.isSupport()) {
        $('.voice-worker').hide();
    } else {
        $('.voice-worker').show();
    }

});