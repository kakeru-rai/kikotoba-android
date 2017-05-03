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

var $currentParagraph;
function positionCallback(hour, min, sec, msec, trackIndex) {
    var positionDate = new Date(0, 0, 0, 0, 0, 0, 0);
    positionDate.setHours(hour);
    positionDate.setMinutes(min);
    positionDate.setSeconds(sec);

    positionDate.setMilliseconds(msec);
    var paragraphDate = new Date(0, 0, 0, 0, 0, 0, 0);

    console.log([hour, min, sec, msec, trackIndex, $currentParagraph, positionDate]);
    selectSentence(trackIndex);
}

function toggleTranscript() {
    // TODO: トグル前の表示中英文の位置までスクロール
    $('.transcript').toggle(200);
}

function getCurrentTranscript() {
    console.log($currentParagraph.find('.transcript').text());
    Android.onGetCurrentTranscript(
            $currentParagraph.find('.script').text(),
            $currentParagraph.find('.transcript').text()
    );
}

function selectSentence(trackIndex) {
    var selector = '[data-track=' + trackIndex + ']';

    if (window.$currentParagraph) {
        window.$currentParagraph.removeClass('current-playing');
    }
    var $currentParagraph = $(selector);
    $currentParagraph.addClass('current-playing');

    window.$currentParagraph = $currentParagraph;
}

$(function() {
    console.log(2);

    $(document).on('selectionchange', function(){
        Android.onTextSelected(document.getSelection().toString());
    });

    window.$pList = $('[data-track]');
//    window.$pList = $('.wrapper_chapter .sentence');
    window.$pList.on('click', function onParagraphClickListener() {
        console.log(123);
        var $this = $(this);
        selectSentence($this.data('track'));
//        window.$pList.removeClass('current-playing');
//        $this.addClass('current-playing');

        Android.onParagraphClicked(
            Number($this.data('hour')),
            Number($this.data('min')),
            Number($this.data('sec')),
            Number($this.data('msec')),
            Number($this.data('track'))
        );
    });
    Android.onReady();


});
