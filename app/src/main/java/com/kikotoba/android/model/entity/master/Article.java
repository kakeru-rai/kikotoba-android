package com.kikotoba.android.model.entity.master;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by raix on 2017/01/24.
 */

public class Article {

    private String title;
    private List<Sentence> sentences;
    private int audioVersion;
    private List<String> translations;

    private String id;
    private String image;
    private String origin;
    private String language;

    public String getTitle() {
        return title;
    }

    public Article setTitle(String title) {
        this.title = title;
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

    // setter/getter>

    public String _getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String _getImage() {
        return image;
    }

    public Article setImage(String image) {
        this.image = image;
        return this;
    }

    public String _getOrigin() {
        return origin;
    }

    public Article setOrigin(String origin) {
        this.origin = origin;
        return this;
    }

    public String _getLanguage() {
        return language;
    }

    public Article setLanguage(String language) {
        this.language = language;
        return this;
    }


    public List<String> makeSenteceStringList() {
        List<String> scripts = new ArrayList<>();
        for (Sentence sentence : sentences) {
            scripts.add(sentence.getText());
        }
        return scripts;
    }

}
