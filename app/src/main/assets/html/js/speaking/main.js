if (!window.Android) {
    // デバッグ用にモックを用意する
    window.Android = {};
}

var web = {};
$(function() {
    var AppInterface = function() {
        this.$container = $('#container');
    }

    /**
     * @param {string} text
     */
    AppInterface.prototype.setText = function(text) {
        this.$container.text(text);
    };

    window.web = new AppInterface();
    Android.onReady();
});
