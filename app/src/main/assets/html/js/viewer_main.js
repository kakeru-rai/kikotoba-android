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

// アプリからの呼び出し関数
var play = function(){};
var pause = function(){};
var next = function(){};
var prev = function(){};
var setSpeed = function(){};
var setVolume = function(){};
var getTranscript = function(){};

$(function() {
    AudioPlayer = function AudioPlayer() {
        this.gapMsec = 1000;
        this.$audio = $('#audioPlayer');
        console.log(this.$audio);
    }
    AudioPlayer.prototype.setTrack = function setGapMsec(gapMsec) {
        this.gapMsec = gapMsec;
    }
    AudioPlayer.prototype.setTrack = function setTrack(index) {
//        this.$audio.attr('src', '/audio/en/voa_00' + (index + 1) + '.mp3');
        this.$audio.attr('src', '../audio/en/voa_00' + (index + 1) + '.mp3');
    }
    AudioPlayer.prototype.setOnEndedListener = function setOnEndedListener(listener) {
        console.log(10);
        var _this = this;
        this.$audio.on('ended', function() {
        console.log(11);
            setTimeout(function() {
        console.log(12);
                listener();
            }, _this.gapMsec);
        });
    }

    AudioPlayer.prototype.play = function play() {
        console.log(this);
        console.log(this.$audio);
        console.log(this.$audio.get(0));
        console.log(this.$audio.get(0).play);
        this.$audio.get(0).play();
    }
    AudioPlayer.prototype.pause = function pause() {this.$audio.get(0).pause();}
    /**
     * @param Number speed 0-1.0
     */
    AudioPlayer.prototype.setSpeed = function setSpeed(speed) {this.$audio.get(0).playbackRate = speed;}
    /**
     * @param Number volume 0-1.0
     */
    AudioPlayer.prototype.setVolume = function setVolume(volume) {this.$audio.get(0).volume = volume;}
});

$(function() {
    var CLASS_CURRENT_PLAYING = 'current-playing';
    Book = function Book() {
        this.currentIndex = 0;
        this.$scripts = $('.script');
        this.$transcript = $('.transcript');

        var _this = this;
        this.$scripts.on('touchend click', function() {
            var audio = new Audio('/android_asset/audio/en/voa_001.mp3');
            audio.play();

//            _this.currentIndex = Number($(this).closest('[data-track]').attr('data-track')) - 1;
//            _this.refreshView();
//            play();
        });
    };
    Book.prototype.refreshView = function refreshView() {
        this.$scripts.removeClass(CLASS_CURRENT_PLAYING);
        this.$scripts.eq(this.currentIndex).addClass(CLASS_CURRENT_PLAYING);
    };
    Book.prototype.getTranscript = function getTranscript() {
        return this.$transcript.eq(this.currentIndex).text();
    };
    Book.prototype.hasNext = function hasNext() {
        return this.currentIndex < this.$transcript.length - 1
    };
    Book.prototype.hasPrev = function hasPrev() {
        return this.currentIndex > 0
    };
    Book.prototype.increment = function increment() {
        if (this.hasNext()) {
            this.currentIndex += 1;
        }
    };
    Book.prototype.decrement = function increment() {
        if (this.hasPrev()) {
            this.currentIndex -= 1;
        }
    };
});

$(function() {
    var book = new Book();
    var audioPlayer = new AudioPlayer();
    audioPlayer.setOnEndedListener(function() {
        console.log(1);
        next();
    });

    // アプリからの呼び出し関数
    play = function play() {
        book.refreshView();
        audioPlayer.setTrack(book.currentIndex);
    console.log(211);
        audioPlayer.play();
    console.log(212);
    }
    pause = function stop() {
        audioPlayer.pause();
    }
    next = function next() {
        console.log(2);
        if (!book.hasNext()) {
            return;
        }
        console.log(3);
        book.increment();
        book.refreshView();
        audioPlayer.setTrack(book.currentIndex);
        audioPlayer.play();
    }
    prev = function prev() {
        if (!book.hasPrev()) {
            return;
        }
        book.decrement();
        book.refreshView();
        audioPlayer.setTrack(book.currentIndex);
        audioPlayer.play();
    }
    setSpeed = function setSpeed(speed) {
        audioPlayer.setSpeed(speed);
    }
    setVolume = function setVolume(volume) {
        audioPlayer.setVolume(volume);
    }
    getTranscript = function getTranscript() {
        return book.getTranscript();
    }

//    this.$scripts.
    console.log(111);
//    play();
    console.log(112);
});
