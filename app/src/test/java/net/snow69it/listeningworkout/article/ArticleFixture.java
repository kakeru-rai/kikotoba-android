package net.snow69it.listeningworkout.article;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by raix on 2017/01/31.
 */

public class ArticleFixture {

    public static final String defaultTitle = "title";
    public static final String defaultImage = "http://image";
    public static final String defaultLanguage = "ja";
    public static final String defaultOrigin = "http://origin";
    public static final String sentenceFormat = "sentence%d";
    public static final int sentenceSize = 3;

    public static Article newArticle() {
        Article article = new Article();
        List<Sentence> sentences = new ArrayList<>();
        int paragraph = 1;
        for (int i = 0; i < sentenceSize; i++) {
            if (i % 2 == 0) {
                paragraph += 1;
            }
            sentences.add(newSentence(paragraph, i, i+1, String.format(sentenceFormat, i)));
        }
        article.setSentences(sentences)
                .setTitle(defaultTitle)
                .setImage(defaultImage)
                .setLanguage(defaultLanguage)
                .setOrigin(defaultOrigin);
        return article;
    }

    public static Sentence newSentence(int paragraph, float fromSec, float toSec, String text) {
//    public static Sentence newSentence(int paragraph, int track, String text) {
        Sentence sentence = new Sentence();
        sentence.setParagraph(paragraph);
        sentence.setFromSec(fromSec);
        sentence.setToSec(toSec);
//        sentence.setTrack(track);
        sentence.setText(text);
        return sentence;
    }
}
