$(function() {
    /**
     * @param {string} sentence 文章
     * @param {number} fromSec 音声の開始位置
     * @param {number} toSec 音声の終了位置
     * @param {number} translationIndex 翻訳インデックス
     */
    var Sentence = function(sentence, fromSec, toSec, translationIndex) {
        var _this = this;
        this.sentence = sentence;
        this.fromSec = fromSec;
        this.toSec = toSec;
        this.translationIndex = translationIndex;
    }

    Sentence.CLASS_SENTENCE = 'script';

    /**
     * @param $element Sentenceが生成した要素
     * @returns {Sentence}
     */
    Sentence.newInstance = function newInstance($element) {
        console.log($element.get(0));
        return new Sentence(
                    $element.text().trim(),
                    $element.data('from-sec'),
                    $element.data('to-sec'),
                    $element.data('translation-index')
        );
    };

    /**
     * @return {jQuery}
     */
    Sentence.prototype.createElement = function createElement() {
        var $sentence = $('<span class="' + Sentence.CLASS_SENTENCE + '"'
                + ' data-from-sec="' + this.fromSec + '"'
                + ' data-to-sec="' + this.toSec + '"'
                + ' data-translation-index="' + this.translationIndex + '"'
                + '></span>'
                )
        $sentence.text(this.sentence + ' ');
//            .data('track', trackIndex);
        return $sentence;
    };

    window.sri = window.sri || {};
    window.sri.Sentence = Sentence;
});
