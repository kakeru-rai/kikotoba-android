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
    private int audioVersion;
    private List<String> translations;

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

    public int getAudioVersion() {
        return audioVersion;
    }

    public void setAudioVersion(int audioVersion) {
        this.audioVersion = audioVersion;
    }

    public List<String> getTranslations() {
        return translations;
    }

    public void setTranslations(List<String> translations) {
        this.translations = translations;
    }

}
