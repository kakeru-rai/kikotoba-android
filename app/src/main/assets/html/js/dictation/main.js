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

        this.inputTextIndex = 0;
    }

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

        var inputTextIndex = this.inputTextIndex;
        ++this.inputTextIndex;
        $input.data('text', text)
                .data('input-text-index', inputTextIndex)
                .css('width', '' + this._measureWidth(text) + 'px')
                .on('keyup', function(event) {
                console.log(event.keyCode);
                    if (event.keyCode === 13) {
                        var nextIndex = Number($(this).data('input-text-index')) + 1;
                console.log($(this).data('input-text-index'));
                console.log(nextIndex);
                console.log($('[data-input-text-index=' + nextIndex + ']'));
                        $('[data-input-text-index=' + nextIndex + ']').focus();
                    }
                });

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

    AppInterface.prototype.showAnswer = function() {
        $inputs = this.$container.find('input');
        $inputs.each(function() {
            var $this = $(this);
            $this
                .addClass('red')
                .val($this.data('text'));
        });

    }

    window.web = new AppInterface();

    Android.onReady();
});
