if (!window.Android) {
    // デバッグ用にモックを用意する
    window.Android = {};
    window.Android.onParagraphClicked = function onParagraphClicked(){};
    window.Android.onReady = function onReady(){};
    window.Android.onTextSelected = function onTextSelected(){};
    window.Android.onGetCurrentTranscript = function onGetCurrentTranscript(transcript) {
        console.log(transcript);
    };
}

var web = {};
$(function() {
    var AppInterface = function() {
        this.$container = $('#container');

        // 要素の横幅を測定する
        this.$ruler = $('#ruler');
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
        console.log(src);
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
    AppInterface.prototype.addText = function(text) {
        var $span = $('<span>');
        if (text === ' ') {
            $span.html('&nbsp;');
         } else {
            $span.text(text);
        }
        this.$container.append($span);
    };

    /**
     * @param {int} textLength
     */
    AppInterface.prototype.addInput = function(text) {
        var $input = $('<input type="text">');
        $input.attr('data-text', text);
        $input.css('width', '' + this._measureWidth(text) + 'px');

        var $spanAnswer = $('<span class="answer jsAnswerIncorrect">');
        $spanAnswer
            .hide()
            .text('×');

        var $spanCorrect = $('<span class="answer jsAnswerCorrect">');
        $spanCorrect
            .hide()
            .text('◎');

        var $spanContainer = $('<span>');
        $spanContainer
            .append($spanAnswer)
            .append($spanCorrect)
            .append($input);
        this.$container.append($spanContainer);
    };

    /**
     * @param {string} text
     */
    AppInterface.prototype._measureWidth = function _measureWidth(text) {
        var $span = $('<span>');
        $span
            .css('visibility', 'hidden')
            .text(text);
        this.$ruler.append($span);
        var width = $span.get(0).offsetWidth + 10;
        $span.remove();
        return width;
    }

    AppInterface.prototype.submit = function() {
        var isCleared = true;

        // 正誤判定
        $inputs = this.$container.find('input');
        $inputs.each(function() {
            var $this = $(this);
            if ($this.val().toLowerCase() === $this.data('text').toLowerCase()) {
                $this.parent().find('.jsAnswerCorrect').show();
                $this.parent().find('.jsAnswerIncorrect').hide();
            } else {
                $this.parent().find('.jsAnswerCorrect').hide();
                $this.parent().find('.jsAnswerIncorrect').show();
                isCleared = false;
            }
        });

        Android.submitCallback(isCleared);
    };
$('audio').on('abort', function(){console.log('abort')});
$('audio').on('canplay', function(){console.log('canplay')});
$('audio').on('canplaythrough', function(){console.log('canplaythrough')});
$('audio').on('durationchange', function(){console.log('durationchange:' + this.duration)});
$('audio').on('emptied', function(){console.log('emptied')});
$('audio').on('encrypted ', function(){console.log('encrypted ')});
$('audio').on('ended', function(){console.log('ended')});
$('audio').on('error', function(event){
    var el = event.target;
    console.log('error:' + el.error.code + ":" + el.error.message);
});
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

    window.web = new AppInterface();

    Android.onReady();
});
