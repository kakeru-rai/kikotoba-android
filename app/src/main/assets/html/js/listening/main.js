if (!window.Android) {
    // デバッグ用にモックを用意する
    var Android = {};
    Android.onReady = function() {console.log('onReady');};
    Android.onSentenceSelected = function(index) {console.log(['onSentenceSelected', index]);};
    Android.onTrackEnded = function() {console.log('onTrackEnded')};
    window.Android = Android;
}

for (var p in Android) {
    console.log([p, Android[p]]);
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
        console.log([sentence.fromSec, sentence.toSec]);
        this.audioPlayer.playDuration(sentence.fromSec, sentence.toSec);
        this.article.scrollUpToSentence();
    };
//    AppInterface.prototype.play = function play() {
//        this.audioPlayer.play();
//        this.article.scrollUpToSentence();
//    };

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

//    AppInterface.prototype.setAudioSrcAndPlay = function setAudioSrc(src) {
//        this.audioPlayer.setAudioSrc(src);
//        this.audioPlayer.play();
//    };

    // ############ 記事
    /**
     * @param {string} text
     */
    AppInterface.prototype.addSentence = function addSentence(sentence, fromSec, toSec) {
        this.article.addSentence(sentence, fromSec, toSec);
    };
//    AppInterface.prototype.addSentence = function addSentence(sentence, trackIndex) {
//        this.article.addSentence(sentence, trackIndex);
//    };

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

    AppInterface.prototype.setCurrentSentenceIndex = function setCurrentSentenceIndex(currentSentenceIndex) {
        this.article.setCurrentSentenceIndex(currentSentenceIndex);
    };

    AppInterface.prototype.popup = function popup(text) {
        this.article.popup(text);
    };
//    AppInterface.prototype.next = function next() {
//        this.article.next();
//    };
//
//    AppInterface.prototype.prev = function prev() {
//        this.article.prev();
//    };

$('audio').on('abort', function(){console.log('abort')});
$('audio').on('canplay', function(){console.log('canplay')});
$('audio').on('canplaythrough', function(){console.log('canplaythrough')});
$('audio').on('durationchange', function(){console.log('durationchange:' + this.duration)});
$('audio').on('emptied', function(){console.log('emptied')});
$('audio').on('encrypted ', function(){console.log('encrypted ')});
$('audio').on('ended', function(){console.log('ended')});
$('audio').on('error', function(){console.log('error')});
$('audio').on('interruptbegin', function(){console.log('interruptbegin')});
$('audio').on('interruptend', function(){console.log('interruptend')});
$('audio').on('loadeddata', function(){console.log('loadeddata')});
$('audio').on('loadedmetadata', function(){console.log('loadedmetadata')});
$('audio').on('loadstart', function(){console.log('loadstart')});
$('audio').on('mozaudioavailable', function(){console.log('mozaudioavailable')});
$('audio').on('pause', function(){console.log('pause')});
$('audio').on('play', function(){console.log('play')});
$('audio').on('playing', function(){console.log('playing')});
$('audio').on('progress', function(){console.log('progress')});
$('audio').on('ratechange', function(){console.log('ratechange')});
$('audio').on('seeked', function(){console.log('seeked:' + this.currentTime)});
$('audio').on('seeking', function(){console.log('seeking:' + this.currentTime)});
$('audio').on('stalled', function(){console.log('stalled')});
$('audio').on('suspend', function(){console.log('suspend')});
$('audio').on('timeupdate', function(){console.log('timeupdate:' + this.currentTime)});
$('audio').on('volumechange', function(){console.log('volumechange')});
$('audio').on('waiting', function(){console.log('waiting')});

try {
    window.web = new AppInterface();
    } catch (e) {
        console.log(e);
    }
    Android.onReady();
});
