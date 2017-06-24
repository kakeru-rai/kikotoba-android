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

package com.kikotoba.android;

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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.kikotoba.android.model.WorkingDirectory;
import com.kikotoba.android.model.audio.AudioDownloadTask;
import com.kikotoba.android.model.dictation.Level;
import com.kikotoba.android.model.entity.ArticlePair;
import com.kikotoba.android.model.entity.ArticlePairDummy;
import com.kikotoba.android.model.entity.UserLogByArticle;
import com.kikotoba.android.repository.ArticleRepository;
import com.kikotoba.android.repository.BaseRepository;
import com.kikotoba.android.repository.UserLogRepository;
import com.kikotoba.android.view.StarList;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.kikotoba.android.ArticleListFragment.SimpleStringRecyclerViewAdapter.ViewHolder.ArticleStatus.UNDER_CONSTRUCTION;

public class ArticleListFragment extends Fragment {

    @BindView(R.id.recyclerview) RecyclerView mRecyclerView;
    @BindView(R.id.retryButton) Button mRetryButton;
    @BindView(R.id.progressBar) ProgressBar mProgressBar;

    private List<ArticlePair> articlePairs = new ArrayList();

    private SimpleStringRecyclerViewAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_article_list, container, false);
        ButterKnife.bind(this, rootView);

        mRetryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                queryArticle();
            }
        });

        queryArticle();
        return rootView;
    }

    private void queryArticle() {
        moveListLoadStateProgreess();

        final UserLogRepository userLogRepo = new UserLogRepository();
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        ArticleRepository repo = new ArticleRepository();
        repo.queryArticles(new BaseRepository.EntityListEventListener<ArticlePair>() {
            @Override
            public void onSuccess(List<ArticlePair> entities) {
                if (getActivity() == null) {
                    return;
                }
                moveListLoadStateComplete();

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

                setupRecyclerView(mRecyclerView);
            }

            @Override
            public void onError(DatabaseError error) {
                if (getActivity() == null) {
                    return;
                }
                error.toException().printStackTrace();
                moveListLoadStateRetry();
            }
        });
    }

    private void moveListLoadStateRetry() {
        mRetryButton.setVisibility(View.VISIBLE);
        mProgressBar.setVisibility(View.GONE);
    }

    private void moveListLoadStateProgreess() {
        mRetryButton.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.VISIBLE);
    }

    private void moveListLoadStateComplete() {
        mRetryButton.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.GONE);
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

            enum ArticleStatus {
                READY,
                AUDIO_NOT_DOWNLOADED,
                UNDER_CONSTRUCTION,
            }

            // data
            public ArticlePair mArticlePair;
            private ArticleStatus mArticleStatus;

            // view
            public final View mView;
            public final CardView mCardView;
            public final ImageView mImageView;
            public final TextView mTextView;
//            public final TextView mTextViewDictationScore;
            public final ImageView mImageViewDictationIcon;
            public final TextView mTextViewSpeakingScore;
            public final ImageView mImageViewSpeakingIcon;
            public final TextView mTextViewPlaybackTime;
            public final ImageView mImageViewPlaybackTimeIcon;

            @BindView(R.id.cardButtonListening) View buttonListening;
            @BindView(R.id.cardButtonLevel1) View buttonLevel1;
            @BindView(R.id.cardButtonLevel2) View buttonLevel2;
            @BindView(R.id.cardButtonLevel3) View buttonLevel3;

            @BindView(R.id.cardStarList1) StarList starList1;
            @BindView(R.id.cardStarList2) StarList starList2;
            @BindView(R.id.cardStarList3) StarList starList3;

            @BindView(R.id.cardDownloadLayout) View downloadLayout;
            @BindView(R.id.cardPreparingLayout) View preparingLayout;
            @BindView(R.id.cardNormalLayout) View normalLayout;



            public ViewHolder(View view) {
                super(view);
                ButterKnife.bind(this, view);
                mView = view;

                mCardView = (CardView) view.findViewById(R.id.cardView);
                mImageView = (ImageView) view.findViewById(R.id.avatar);
                mTextView = (TextView) view.findViewById(android.R.id.text1);
                mImageViewDictationIcon = (ImageView) view.findViewById(R.id.dictationIcon);
//                mTextViewDictationScore = (TextView) view.findViewById(R.id.dictationScore);
                mImageViewSpeakingIcon = (ImageView) view.findViewById(R.id.speakingIcon);
                mTextViewSpeakingScore = (TextView) view.findViewById(R.id.speakingScore);
                mImageViewPlaybackTimeIcon = (ImageView) view.findViewById(R.id.playbackTimeIcon);
                mTextViewPlaybackTime = (TextView) view.findViewById(R.id.playbackTime);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + mTextView.getText();
            }

//            public void setAudioDownloaded(boolean downloaded) {
//                isAudioDownloaded = downloaded;
//                if (downloaded) {
//                    mImageViewDownload.setImageResource(R.drawable.ic_cloud_done_black_24dp);
//                } else {
//                    mImageViewDownload.setImageResource(R.drawable.ic_cloud_download_black_24dp);
//                }
//            }
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
            holder.mArticleStatus = getStatus(articlePair);

            view(holder);
        }

        private void view(final ViewHolder holder) {
            switch (holder.mArticleStatus) {
                case READY:
                    holder.normalLayout.setVisibility(View.VISIBLE);
                    holder.preparingLayout.setVisibility(View.GONE);
                    holder.downloadLayout.setVisibility(View.GONE);
//                    holder.mCardView.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View view) {
//                            showMenuDialog(mContext, holder);
//                        }
//                    });
                    break;
                case AUDIO_NOT_DOWNLOADED:
                    holder.normalLayout.setVisibility(View.GONE);
                    holder.preparingLayout.setVisibility(View.GONE);
                    holder.downloadLayout.setVisibility(View.VISIBLE);
                    holder.downloadLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            showStartDownloadDialog(mContext, holder);
                        }
                    });
                    break;
                case UNDER_CONSTRUCTION:
                    holder.normalLayout.setVisibility(View.GONE);
                    holder.preparingLayout.setVisibility(View.VISIBLE);
                    holder.downloadLayout.setVisibility(View.GONE);
                    holder.mCardView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            showUnderConstructionDialog(mContext, holder);
                        }
                    });
                    break;
            }

            if (holder.mArticleStatus == ViewHolder.ArticleStatus.UNDER_CONSTRUCTION) {
                return;
            }


            holder.buttonListening.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    moveToListening(holder);
                }
            });
            holder.buttonLevel1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    moveToDictation(holder, Level.EASY);
                }
            });
            holder.buttonLevel2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    moveToDictation(holder, Level.NORMAL);
                }
            });
            holder.buttonLevel3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    moveToDictation(holder, Level.HARD);
                }
            });

            holder.mTextView.setText(holder.mArticlePair.getTranslated().getTitle());

            int sentenceSize = holder.mArticlePair.getTarget().getSentences().size();

            // 利用ログの有無
            UserLogByArticle userLog = holder.mArticlePair.getUserLogByArticle();
            if (userLog == null) {
                holder.mTextViewPlaybackTime.setText(String.format("--:--:--", sentenceSize));
                holder.mTextViewSpeakingScore.setText(String.format("--/%d", sentenceSize));
            } else {
                holder.mTextViewPlaybackTime.setText(userLog.calcListeningPlaybackTime());

                holder.mTextViewSpeakingScore.setText(String.format("%d/%d",
                        userLog.calcSpeakingTotal(),
                        sentenceSize));
            }

            // dictation
//            if (userLog == null || userLog.getScore() == 0) {
//                holder.dictationScoreEmpty.setVisibility(View.VISIBLE);
//                holder.dictationScore1.setVisibility(View.GONE);
//                holder.dictationScore2.setVisibility(View.GONE);
//                holder.dictationScore3.setVisibility(View.GONE);
//            } else {
//                holder.dictationScoreEmpty.setVisibility(View.GONE);
//                holder.dictationScore1.setVisibility(View.VISIBLE);
//                holder.dictationScore2.setVisibility(View.VISIBLE);
//                holder.dictationScore3.setVisibility(View.VISIBLE);
//                holder.dictationScore1.setImageResource(
//                        userLog.getScore() >= 1 ? R.drawable.ic_star_black_48dp : R.drawable.ic_star_border_black_48dp);
//                holder.dictationScore2.setImageResource(
//                        userLog.getScore() >= 2 ? R.drawable.ic_star_black_48dp : R.drawable.ic_star_border_black_48dp);
//                holder.dictationScore3.setImageResource(
//                        userLog.getScore() >= 3 ? R.drawable.ic_star_black_48dp : R.drawable.ic_star_border_black_48dp);
//            }

            if (userLog == null) {
                holder.starList1.setStarCount(0);
                holder.starList2.setStarCount(0);
                holder.starList3.setStarCount(0);
            } else {
                holder.starList1.setStarCount(userLog._getScore(Level.EASY));
                holder.starList2.setStarCount(userLog._getScore(Level.NORMAL));
                holder.starList3.setStarCount(userLog._getScore(Level.HARD));
            }

            holder.mImageView.setImageResource(R.mipmap.ic_launcher);
            Glide.with(holder.mImageView.getContext())
                    .load(holder.mArticlePair.getImage())
                    .fitCenter()
                    .into(holder.mImageView);
        }

        private ViewHolder.ArticleStatus getStatus(ArticlePair articlePair) {
            if (articlePair instanceof ArticlePairDummy) {
                return UNDER_CONSTRUCTION;
            } else if (WorkingDirectory.getInstance().hasAudioDownloaded(mContext, articlePair.getTarget())) {
                return ViewHolder.ArticleStatus.READY;
            } else {
                return ViewHolder.ArticleStatus.AUDIO_NOT_DOWNLOADED;
            }
        }

        private void showMenuDialog(final Context context, final ViewHolder holder) {
            final String[] items = context.getResources().getStringArray(R.array.article_function_names);
            new AlertDialog.Builder(context)
                    .setTitle(holder.mArticlePair.getTranslated().getTitle())
                    .setItems(items, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
                                case 0: // リスニング
                                    context.startActivity(
                                            ListeningActivity.newIntent(
                                                    context,
                                                    holder.mArticlePair.getId(),
                                                    holder.mTextView.getText().toString(),
                                                    holder.mArticlePair,
                                                    holder.mArticlePair.getUserLogByArticle() != null ? holder.mArticlePair.getUserLogByArticle().getCurrentReadingIndex() : 0
                                            )
                                    );
                                    break;
//                                case 1: // スピーキング
//                                    context.startActivity(
//                                            SpeakingActivity.newIntent(
//                                                    context,
//                                                    holder.mArticlePair.getId(),
//                                                    holder.mTextView.getText().toString(),
//                                                    holder.mArticlePair
//                                            )
//                                    );
//                                    break;
//                                case 2:
//                                case 1: // ディクテーション
//                                    break;
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
                                            holder.mArticleStatus = ViewHolder.ArticleStatus.READY;
                                            view(holder);
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

        private void showUnderConstructionDialog(final Context context, final ViewHolder holder) {
            new AlertDialog.Builder(context)
                    .setMessage(R.string.article_under_construction)
                    .setPositiveButton(R.string.tmpl_close, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    })
                    .setCancelable(true)
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

        private void moveToListening(ViewHolder holder) {
            mContext.startActivity(
                    ListeningActivity.newIntent(
                            mContext,
                            holder.mArticlePair.getId(),
                            holder.mTextView.getText().toString(),
                            holder.mArticlePair,
                            holder.mArticlePair.getUserLogByArticle() != null
                                    ? holder.mArticlePair.getUserLogByArticle().getCurrentReadingIndex()
                                    : 0
                    )
            );
        }

        private void moveToDictation(ViewHolder holder, Level level) {
            mContext.startActivity(
                    DictationActivity.newIntent(
                            mContext,
                            holder.mArticlePair.getId(),
                            holder.mTextView.getText().toString(),
                            holder.mArticlePair,
                            level
                    )
            );
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }
    }
}
