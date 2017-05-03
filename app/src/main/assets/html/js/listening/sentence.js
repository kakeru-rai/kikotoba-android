$(function() {
    var Sentence = function(sentence, fromSec, toSec) {
        var _this = this;
        this.sentence = sentence;
        this.fromSec = fromSec;
        this.toSec = toSec;
    }
    Sentence.CLASS_SENTENCE = 'script';

    /**
     * @param $element Sentenceが生成した要素
     * @return Sentence
     */
    Sentence.newInstance = function newInstance($element) {
        console.log($element.get(0));
        return new Sentence(
                    $element.text().trim(),
                $element.data('from-sec'),
                $element.data('to-sec')
        );
    };

    Sentence.prototype.createElement = function createElement() {
        var $sentence = $('<span class="' + Sentence.CLASS_SENTENCE + '"'
                + ' data-from-sec="' + this.fromSec + '"'
                + ' data-to-sec="' + this.toSec + '"'
                + '></span>'
                )
        $sentence.text(this.sentence + ' ');
//            .data('track', trackIndex);
        return $sentence;
    };

    window.sri = window.sri || {};
    window.sri.Sentence = Sentence;
});
