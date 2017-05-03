package net.snow69it.listeningworkout.article;

import android.content.Context;

import net.snow69it.listeningworkout.util.IOUtil;

import java.io.IOException;
import java.util.List;

/**
 * Created by raix on 2017/01/24.
 */

public class ListeningHtml {

    public static ListeningHtml newInstance(Context context, Article targetArticle) throws IOException {
        Article translatedArticle = ArticleApi.getArticleEn(context);
        String htmlTmpl = IOUtil.newAssetsInstance().getText(context, "tmpl/listening.html");
        return new ListeningHtml(targetArticle, translatedArticle, htmlTmpl);
    }

    private final Article targetArticle;
    private final Article translatedArticle;
    private final String htmlTmpl;
    private final String photoTmpl = "<img src=\"%s\">";
    private final String titleTmpl = "<h1 class=\"chapter sentence\" data-track=\"0\">%s</h1>";
    private final String paragraphTmpl = "<p class=\"paragraph\">%s</p>";
    private final String sentenceTmpl = "<span class=\"script\" data-track=\"%d\">%s </span>";

    public ListeningHtml(Article targetArticle, Article translatedArticle, String htmlTmpl) {
        this.targetArticle = targetArticle;
        this.translatedArticle = translatedArticle;
        this.htmlTmpl = htmlTmpl;
    }

    public String createHtml() throws Exception {
        List<Sentence> targetSentences = targetArticle.getSentences();
        List<Sentence> translatedSentences = translatedArticle.getSentences();

        if (translatedSentences.size() != targetSentences.size()) {
            throw new Exception();
        }

        String paragraphBuffer = "";
        String sentenceBuffer = "";
        Sentence previouseTargetSentence = null;
        for (int i = 0; i < targetSentences.size(); i++) {
            Sentence targetSentence = targetSentences.get(i);
            Sentence translatedSentence = translatedSentences.get(i);
            int trackIndex = i + 1;
            // 文を結合
            sentenceBuffer += String.format(sentenceTmpl, trackIndex, targetSentence.getText());

            if (previouseTargetSentence == null) {
                previouseTargetSentence = targetSentence;
                continue;
            }

            if (previouseTargetSentence.getParagraph() != targetSentence.getParagraph()) {
                // 段落
                paragraphBuffer += String.format(paragraphTmpl, sentenceBuffer);
                sentenceBuffer = "";
            }
            previouseTargetSentence = targetSentence;
        }

        // 文書を結合
        String htmlBodyContents = "";
        if (targetArticle.getImage() != null) {
            htmlBodyContents += String.format(photoTmpl, targetArticle.getImage());
        }
        htmlBodyContents += String.format(titleTmpl, targetArticle.getTitle());
        htmlBodyContents += String.format(htmlTmpl, paragraphBuffer);

        return htmlBodyContents;
    }
}
