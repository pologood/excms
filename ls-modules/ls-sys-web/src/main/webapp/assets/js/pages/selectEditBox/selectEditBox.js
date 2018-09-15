
function getSource() {
    Ls.ajaxGet({
        url: "/content/newsSource",
        success: function (text) {
            if (text.status == 1) {
                var ns = text.data;
                var ns_t = "";
                for (var i = 0; i < ns.length; i++) {
                    ns_t += '<tr style="margin:5px;cursor:pointer;font-size:14px;" onclick="getKey(\'' + ns[i].key + '\')"><td>' + ns[i].key + '</td></tr>';
                    if (ns[i]['default'] == true) $("#resources").val(ns[i].key);
                }
                $("#ns_list").html(ns_t);
            }
        }
    });
}

function getKey(key) {
    $("#resources").val(key);
    $("#res").hide();
    $("#select_icon").addClass("fa-caret-down");
    $("#select_icon").removeClass("fa-caret-up");
    k = 1;
}