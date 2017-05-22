package com.kikotoba.android.model.entity;

import java.util.List;

/**
 * Created by raix on 2017/01/24.
 */

public class Article {

    private String id;
    private String title;
    private String image;
    private String origin;
    private String language;
    private List<Sentence> sentences;

    public String createListeningHtml() {
        return "";
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public Article setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getImage() {
        return image;
    }

    public Article setImage(String image) {
        this.image = image;
        return this;
    }

    public String getOrigin() {
        return origin;
    }

    public Article setOrigin(String origin) {
        this.origin = origin;
        return this;
    }

    public String getLanguage() {
        return language;
    }

    public Article setLanguage(String language) {
        this.language = language;
        return this;
    }

    public List<Sentence> getSentences() {
        return sentences;
    }

    public Article setSentences(List<Sentence> sentences) {
        this.sentences = sentences;
        return this;
    }

    public String getAudio() {
        return String.format("../../audio/%s/%s.mp3", id, language);
    }

    private String makeAudioPath() {
        return String.format("../../audio/%s/%s.mp3", id, language);
    }

}
