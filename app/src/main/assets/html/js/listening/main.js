if (!window.Android) {
    // デバッグ用にモックを用意する
    var Android = {};
    Android.onReady = function() {console.log('onReady');};
    Android.onSentenceSelected = function(index) {console.log(['onSentenceSelected', index]);};
    Android.onTrackEnded = function() {console.log('onTrackEnded')};
    window.Android = Android;
}

var web = {};
$(function() {
    var AppInterface = function() {
        this.$container = $('#container');
        this.$photo = $('#photo');
        this.$title = $('#title');
        this.audioPlayer = new sri.AudioPlayer($('#audioPlayer'));
        this.audioPlayer.setOnTrackEndedListener(function() {
            Android.onTrackEnded();
        });
        this.article = new sri.Article($('#container'));
        this.article.setOnSentenceSelectedListener(function(selectedIndex) {
            Android.onSentenceSelected(selectedIndex);
        });
    }

    // ############ オーディオ
    AppInterface.prototype.play = function play() {
        var sentence = this.article.getCurrentSentence();
        this.audioPlayer.playDuration(sentence.fromSec, sentence.toSec);
    };

    AppInterface.prototype.pause = function pause() {
        this.audioPlayer.pause();
    };

    AppInterface.prototype.setCurrentTimeSec = function setCurrentTimeSec(sec) {
        this.audioPlayer.setCurrentTimeSec(sec);
    };

    /**
     * @param {Number} speed 0.5 - 2.0
     */
    AppInterface.prototype.setSpeed = function setSpeed(speed) {
        this.audioPlayer.setSpeed(speed);
    }

    /**
     * @param Number volume 0-1.0
     */
    AppInterface.prototype.setVolume = function setVolume(volume) {
        this.audioPlayer.setVolume(volume);
    }

    /**
     * @param {string} src
     */
    AppInterface.prototype.setAudioSrc = function setAudioSrc(src) {
        this.audioPlayer.setAudioSrc(src);
    };

    // ############ 記事
    /**
     * @param {string} text
     * @param {number} fromSec
     * @param {number} toSec
     * @param {number} translationIndex
     */
    AppInterface.prototype.addSentence = function addSentence(sentence, fromSec, toSec, translationIndex) {
        this.article.addSentence(sentence, fromSec, toSec, translationIndex);
    };

    AppInterface.prototype.flushParagraph = function flushParagraph() {
        this.article.flushParagraph();
    };

    AppInterface.prototype.setPhotoSrc = function setPhotoSrc(src) {
        this.$photo.attr('src', src);
    };

    AppInterface.prototype.setTitle = function setTitle(title) {
        this.$title.text(title);
    };

    AppInterface.prototype.refreshUI = function refreshUI() {
        this.article.refreshUI();
    };

    AppInterface.prototype.scrollUpToSentence = function() {
        this.article.scrollUpToSentence();
    };

    AppInterface.prototype.setCurrentSentenceIndex = function setCurrentSentenceIndex(currentSentenceIndex) {
        this.article.setCurrentSentenceIndex(currentSentenceIndex);
    };

    AppInterface.prototype.popup = function popup(text) {
        this.article.popup(text);
        this.article.scrollUpToSentence();
    };

    AppInterface.prototype.isBlindMode = function(isBlindMode) {
        this.article.isBlindMode = isBlindMode;
        this.article.refreshUI();
    };

    window.web = new AppInterface();

    Android.onReady();
});
