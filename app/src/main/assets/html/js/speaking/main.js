if (!window.Android) {
    // デバッグ用にモックを用意する
    window.Android = {};
}

var web = {};
$(function() {
    var AppInterface = function() {
        this.$container = $('#container');
        this.audioPlayer = new sri.AudioPlayer($('#audioPlayer'));
    }

    /**
     * @param {Number} fromSec 1.23
     * @param {Number} toSec 2.34
     */
    AppInterface.prototype.play = function(fromSec, toSec) {
        this.audioPlayer.playDuration(fromSec, toSec);
    };

    AppInterface.prototype.pause = function pause() {
        this.audioPlayer.pause();
    };

    /**
     * @param {string} src
     */
    AppInterface.prototype.setAudioSrc = function setAudioSrc(src) {
        this.audioPlayer.setAudioSrc(src);
    };
//    /**
//     * @param {string} src
//     */
//    AppInterface.prototype.setAudioSrc = function(articleId, trackIndex) {
//        this.audioPlayer.setSrc(articleId, trackIndex);
//    };

    /**
     * @param {string} text
     */
    AppInterface.prototype.setText = function(text) {
        this.$container.text(text);
    };

    window.web = new AppInterface();
    Android.onReady();
});
