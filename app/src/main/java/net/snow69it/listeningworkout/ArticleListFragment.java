/*
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.snow69it.listeningworkout;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;

import net.snow69it.listeningworkout.article.ArticlePair;
import net.snow69it.listeningworkout.audio.AudioDownloadTask;
import net.snow69it.listeningworkout.common.WorkingDirectory;
import net.snow69it.listeningworkout.entity.UserLogByArticle;
import net.snow69it.listeningworkout.repository.ArticleRepository;
import net.snow69it.listeningworkout.repository.BaseRepository;
import net.snow69it.listeningworkout.repository.UserLogRepository;

import java.util.ArrayList;
import java.util.List;

public class ArticleListFragment extends Fragment {

    private List<ArticlePair> articlePairs = new ArrayList();

    private SimpleStringRecyclerViewAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final RecyclerView rv = (RecyclerView) inflater.inflate(
                R.layout.fragment_article_list, container, false);

        ArticleRepository repo = new ArticleRepository();
        final UserLogRepository userLogRepo = new UserLogRepository();
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        repo.getArticles(new BaseRepository.EntityListEventListener<ArticlePair>() {
            @Override
            public void onSuccess(List<ArticlePair> entities) {
                articlePairs = entities;

                for (final ArticlePair articlePair : articlePairs) {
                    userLogRepo.bindUserLogByArticle(user.getUid(), articlePair.getId(), new BaseRepository.EntityEventListener<UserLogByArticle>() {
                        @Override
                        public void onSuccess(UserLogByArticle entity) {
                            articlePair.setUserLogByArticle(entity);
                            adapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onError(DatabaseError error) {

                        }
                    });
                }

                setupRecyclerView(rv);
            }

            @Override
            public void onError(DatabaseError error) {
                error.toException().printStackTrace();
            }
        });
        return rv;
    }

    private void setupRecyclerView(RecyclerView recyclerView) {
        adapter = new SimpleStringRecyclerViewAdapter(
                getActivity(),
                articlePairs
        );
        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
        recyclerView.setAdapter(adapter);
    }

    public static class SimpleStringRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleStringRecyclerViewAdapter.ViewHolder> {

        private Context mContext;
        private final TypedValue mTypedValue = new TypedValue();
        private int mBackground;
        private List<ArticlePair> mValues;

        public static class ViewHolder extends RecyclerView.ViewHolder {

            public String mArticleId;
            public ArticlePair mArticlePair;
            private boolean isAudioDownloaded;
            public final View mView;
            public final CardView mCardView;
            public final ImageView mImageView;
            public final TextView mTextView;
            public final ImageView mImageViewDownload;
            public final TextView mTextViewDictationScore;
            public final ImageView mImageViewDictationIcon;
            public final TextView mTextViewSpeakingScore;
            public final ImageView mImageViewSpeakingIcon;
            public final TextView mTextViewPlaybackTime;
            public final ImageView mImageViewPlaybackTimeIcon;

            public ViewHolder(View view) {
                super(view);
                mView = view;

                mCardView = (CardView) view.findViewById(R.id.cardView);
                mImageView = (ImageView) view.findViewById(R.id.avatar);
                mTextView = (TextView) view.findViewById(android.R.id.text1);
                mImageViewDownload = (ImageView) view.findViewById(R.id.audioDownload);
                mImageViewDictationIcon = (ImageView) view.findViewById(R.id.dictationIcon);
                mTextViewDictationScore = (TextView) view.findViewById(R.id.dictationScore);
                mImageViewSpeakingIcon = (ImageView) view.findViewById(R.id.speakingIcon);
                mTextViewSpeakingScore = (TextView) view.findViewById(R.id.speakingScore);
                mImageViewPlaybackTimeIcon = (ImageView) view.findViewById(R.id.playbackTimeIcon);
                mTextViewPlaybackTime = (TextView) view.findViewById(R.id.playbackTime);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + mTextView.getText();
            }

            public void setAudioDownloaded(boolean downloaded) {
                isAudioDownloaded = downloaded;
                if (downloaded) {
                    mImageViewDownload.setImageResource(R.drawable.ic_cloud_done_black_24dp);
                } else {
                    mImageViewDownload.setImageResource(R.drawable.ic_cloud_download_black_24dp);
                }
            }
        }

        public String getValueAt(int position) {
            return mValues.get(position).getTranslated().getTitle();
        }

        public SimpleStringRecyclerViewAdapter(Context context, List<ArticlePair> items) {
            context.getTheme().resolveAttribute(R.attr.selectableItemBackground, mTypedValue, true);
            mContext = context;
            mBackground = mTypedValue.resourceId;
            mValues = items;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_item, parent, false);
            view.setBackgroundResource(mBackground);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            ArticlePair articlePair = mValues.get(position);
            holder.mArticlePair = articlePair;
            holder.mArticleId = articlePair.getId();

            holder.setAudioDownloaded(WorkingDirectory.getInstance().hasAudioDownloaded(mContext, holder.mArticlePair.getTarget()));

//            holder.mArticleId = articlePair.getTranslated().getId();
            holder.mTextView.setText(articlePair.getTranslated().getTitle());

            int sentenceSize = articlePair.getTarget().getSentences().size();
            if (articlePair.getUserLogByArticle() == null) {
                holder.mTextViewDictationScore.setText(String.format("--/%d", sentenceSize));
                holder.mTextViewSpeakingScore.setText(String.format("--/%d", sentenceSize));
                holder.mTextViewPlaybackTime.setText(String.format("--:--:--", sentenceSize));
            } else {
                UserLogByArticle log = articlePair.getUserLogByArticle();
                holder.mTextViewSpeakingScore.setText(String.format("%d/%d",
                        log.calcSpeakingTotal(),
                        sentenceSize));
                holder.mTextViewDictationScore.setText(String.format("%d/%d",
                        log.calcDictationTotal(),
                        sentenceSize));
                holder.mTextViewPlaybackTime.setText(log.calcListeningPlaybackTime());
            }

            holder.mCardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 音声データをダウンロードしてから各機能画面への導線を表示する
                    if (holder.isAudioDownloaded) {
                        showMenuDialog(mContext, holder);
                    } else {
                        showStartDownloadDialog(mContext, holder);
                    }
                }
            });

            Glide.with(holder.mImageView.getContext())
                    .load(articlePair.getImage())
                    .fitCenter()
                    .into(holder.mImageView);
        }

        private void showMenuDialog(final Context context, final ViewHolder holder) {
            final String[] items = context.getResources().getStringArray(R.array.article_function_names);
            new AlertDialog.Builder(context)
                    .setTitle(holder.mArticlePair.getTranslated().getTitle())
                    .setItems(items, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
                                case 0:
                                    context.startActivity(
                                            ListeningActivity.newIntent(
                                                    context,
                                                    holder.mArticleId,
                                                    holder.mTextView.getText().toString(),
                                                    holder.mArticlePair
                                            )
                                    );
                                    break;
                                case 1:
                                    context.startActivity(
                                            SpeakingActivity.newIntent(
                                                    context,
                                                    holder.mArticleId,
                                                    holder.mTextView.getText().toString(),
                                                    holder.mArticlePair
                                            )
                                    );
                                    break;
                                case 2:
                                    context.startActivity(
                                            DictationActivity.newIntent(
                                                    context,
                                                    holder.mArticleId,
                                                    holder.mTextView.getText().toString(),
                                                    holder.mArticlePair
                                            )
                                    );
                                    break;
                            }
                        }
                    })
                    .show();
        }

        private void showStartDownloadDialog(final Context context, final ViewHolder holder) {
            new AlertDialog.Builder(context)
                    .setTitle(holder.mArticlePair.getTranslated().getTitle())
                    .setMessage(R.string.audio_download_message)
                    .setPositiveButton(R.string.tmpl_download, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            final ProgressDialog progressDialog = createDownloadProgressDialog(context, holder.mArticlePair.getTranslated().getTitle());
                            progressDialog.show();
                            AudioDownloadTask task = new AudioDownloadTask(
                                    context,
                                    holder.mArticlePair.getTarget(),
                                    new AudioDownloadTask.AudioDownloadTaskListener() {
                                        @Override
                                        public void onSuccess() {
                                            progressDialog.dismiss();
                                            holder.setAudioDownloaded(true);
                                            showMenuDialog(context, holder);
                                        }

                                        @Override
                                        public void onFailure(Exception exception) {
                                            progressDialog.dismiss();
                                        }

                                        @Override
                                        public void onProgress(long max, long progress) {
                                            progressDialog.setProgress((int) (100 * progress / max));
                                        }
                                    });
                            task.exec();
                        }
                    })
                    .setCancelable(true)
                    .setNegativeButton(R.string.tmpl_cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    })
                .show();
        }

        private ProgressDialog createDownloadProgressDialog(Context context, String title) {
            ProgressDialog progressDialog =  new ProgressDialog(context);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setCancelable(false);
            progressDialog.setTitle(title);
            progressDialog.setMax(100);
            progressDialog.setProgress(0);
            return progressDialog;
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }
    }
}
