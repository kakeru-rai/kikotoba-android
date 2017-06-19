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

var audioPlayer = {};
$(function() {
    window.audioPlayer = new window.sri.AudioPlayer($('#audioPlayer'));
    console.log(1111);
    Android.onReady();
    console.log(2222);
});
