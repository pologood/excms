/*
*######################################
* eWebEditor V11.5 - Advanced online web based WYSIWYG HTML editor.
* Copyright (c) 2003-2018 eWebSoft.com
*
* For further information go to http://www.ewebeditor.net/
* This copyright notice MUST stay intact for use.
*######################################
*/

(function () {
    var fixSizes = window.DoResizeFixes = function () {
        var dlg = window.document.body;

        if (dlg.clientWidth == 0 || dlg.clientHeight == 0) {
            return false;
        }

        for (var i = 0; i < dlg.childNodes.length; i++) {
            var child = dlg.childNodes[i];
            switch (child.className) {
                case "tr" :
                    child.style.left = Math.max(0, dlg.clientWidth - tr.clientWidth);
                    break;

                case "tc" :
                    child.style.width = Math.max(0, dlg.clientWidth - tl.clientWidth - tr.clientWidth);
                    break;

                case "ml" :
                    child.style.height = Math.max(0, dlg.clientHeight - tl.clientHeight - bl.clientHeight);
                    break;

                case "mr" :
                    child.style.left = Math.max(0, dlg.clientWidth - mr.clientWidth);
                    child.style.height = Math.max(0, dlg.clientHeight - tr.clientHeight - br.clientHeight);
                    break;

                case "mc" :
                    child.style.width = Math.max(0, dlg.clientWidth - ml.clientWidth - mr.clientWidth);
                    child.style.height = Math.max(0, dlg.clientHeight - TitleArea.clientHeight - bc.clientHeight);
                    break;

                case "bl" :
                    child.style.top = Math.max(0, dlg.clientHeight - bl.clientHeight);
                    break;

                case "br" :
                    child.style.left = Math.max(0, dlg.clientWidth - br.clientWidth);
                    child.style.top = Math.max(0, dlg.clientHeight - br.clientHeight);
                    break;

                case "bc" :
                    child.style.width = Math.max(0, dlg.clientWidth - bl.clientWidth - br.clientWidth);
                    child.style.top = Math.max(0, dlg.clientHeight - bc.clientHeight);
                    break;
            }
        }
        return true;
    }

    window.DoResizeFixesAndCenter = function () {
        if (!window.DoResizeFixes()) {
            window.setTimeout(window.DoResizeFixesAndCenter, 1);
            return;
        }
        EWEBDialog.CenterDialog(window.frameElement);
    }

    var closeButtonOver = function () {
        this.className = "TitleCloseButtonHover";
    };

    var closeButtonOut = function () {
        this.className = "TitleCloseButton";
    };

    var fixCloseButton = function () {
        var closeButton = document.getElementById("TitleCloseButton");

        closeButton.onmouseover = closeButtonOver;
        closeButton.onmouseout = closeButtonOut;
    }

    var onLoad = function () {
        fixCloseButton();
        window.detachEvent("onload", onLoad);
    }

    window.attachEvent("onload", onLoad);

})();
