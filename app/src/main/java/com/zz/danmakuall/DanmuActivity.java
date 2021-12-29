package com.zz.danmakuall;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.BackgroundColorSpan;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import master.flame.danmaku.controller.IDanmakuView;
import master.flame.danmaku.danmaku.model.BaseDanmaku;
import master.flame.danmaku.danmaku.model.DanmakuTimer;
import master.flame.danmaku.danmaku.model.IDanmakus;
import master.flame.danmaku.danmaku.model.IDisplayer;
import master.flame.danmaku.danmaku.model.android.BaseCacheStuffer;
import master.flame.danmaku.danmaku.model.android.DanmakuContext;
import master.flame.danmaku.danmaku.model.android.Danmakus;
import master.flame.danmaku.danmaku.model.android.SpannedCacheStuffer;
import master.flame.danmaku.danmaku.parser.BaseDanmakuParser;
import master.flame.danmaku.danmaku.util.IOUtils;

/**
 * @author zhangzhao
 * @date 2021/7/14 5:18 下午
 * @describes
 */
public class DanmuActivity extends BaseActivity {
    @BindView(R.id.main_background)
    ImageView img;
    @BindView(R.id.main_emotion)
    RecyclerView emotionRv;
    @BindView(R.id.main_edit)
    EditText edit;
    @BindView(R.id.danmu_view)
    IDanmakuView mDanmakuView;
    @BindView(R.id.main_send)
    TextView mSend;
    @BindView(R.id.main_pause)
    TextView mPause;
    @BindView(R.id.main_hide)
    TextView mHide;
    @BindView(R.id.main_show)
    TextView mShow;
    @BindView(R.id.main_spannable)
    TextView mTextImg;
    @BindView(R.id.main_text)
    TextView mText;
    @BindView(R.id.main_resume)
    TextView mResume;

    private static final String TAG = "DanmuActivity";

    private EmotionAdapter emotionAdapter;
    private List<Integer> imgList;
    private DanmakuContext mContext;
    private BaseDanmakuParser mParser;

    private BaseDanmakuParser createParser() {
        return new BaseDanmakuParser() {
            @Override
            protected Danmakus parse() {
                return new Danmakus();
            }
        };
//        return new BiliDanmukuParser();
    }

    private BaseCacheStuffer.Proxy mCacheStufferAdapter = new BaseCacheStuffer.Proxy() {
        private Drawable mDrawable;

        @Override
        public void prepareDrawing(final BaseDanmaku danmaku, boolean fromWorkerThread) {
            if (danmaku.text instanceof Spanned) { // 根据你的条件检查是否需要需要更新弹幕
                // FIXME 这里只是简单启个线程来加载远程url图片，请使用你自己的异步线程池，最好加上你的缓存池
                new Thread() {

                    @Override
                    public void run() {
                        String url = "http://www.bilibili.com/favicon.ico";
                        InputStream inputStream = null;
                        Drawable drawable = mDrawable;
                        if (drawable == null) {
                            try {
                                URLConnection urlConnection = new URL(url).openConnection();
                                inputStream = urlConnection.getInputStream();
                                drawable = BitmapDrawable.createFromStream(inputStream, "bitmap");
                                mDrawable = drawable;
                            } catch (MalformedURLException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            } finally {
                                IOUtils.closeQuietly(inputStream);
                            }
                        }
                        if (drawable != null) {
                            drawable.setBounds(0, 0, 100, 100);
                            SpannableStringBuilder spannable = createSpannable(drawable);
                            danmaku.text = spannable;
                            if (mDanmakuView != null) {
                                mDanmakuView.invalidateDanmaku(danmaku, false);
                            }
                            return;
                        }
                    }
                }.start();
            }
        }

        @Override
        public void releaseResource(BaseDanmaku danmaku) {
            // TODO 重要:清理含有ImageSpan的text中的一些占用内存的资源 例如drawable
        }
    };

    @Override
    int getLayoutId() {
        return R.layout.activity_danmu;
    }

    @Override
    protected void doInitView() {
        super.doInitView();
        GridLayoutManager layoutManager = new GridLayoutManager(this, 10, LinearLayoutManager.VERTICAL, false);
        emotionRv.setLayoutManager(layoutManager);
        emotionAdapter = new EmotionAdapter();
        emotionRv.setAdapter(emotionAdapter);
        initDanmaku();
    }

    @Override
    protected void doInitData() {
        super.doInitData();
        imgList = new ArrayList<>(20);
        imgList.add(R.drawable.pet_1);
        imgList.add(R.drawable.pet_2);
        imgList.add(R.drawable.pet_3);
        imgList.add(R.drawable.pet_4);
        imgList.add(R.drawable.pet_5);
        imgList.add(R.drawable.pet_6);
        imgList.add(R.drawable.pet_7);
        imgList.add(R.drawable.pet_8);
        imgList.add(R.drawable.pet_9);
        imgList.add(R.drawable.pet_10);
        imgList.add(R.drawable.pet_11);
        imgList.add(R.drawable.pet_12);
        imgList.add(R.drawable.pet_13);
        imgList.add(R.drawable.pet_14);
        imgList.add(R.drawable.pet_15);
        imgList.add(R.drawable.pet_16);
        imgList.add(R.drawable.pet_17);
        imgList.add(R.drawable.pet_18);
        imgList.add(R.drawable.pet_19);
        imgList.add(R.drawable.pet_20);
        emotionAdapter.setImgList(imgList);
    }

    @Override
    protected void doInitListener() {
        super.doInitListener();
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emotionRv.setVisibility(View.GONE);
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
                edit.requestFocus();
                showKeyword(edit);
            }
        });
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (emotionRv.getVisibility() == View.GONE) {
                    edit.requestFocus();
                    getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
                    hideKeyword(getWindow().getDecorView());
                    emotionRv.setVisibility(View.VISIBLE);
                } else {
                    emotionRv.setVisibility(View.GONE);
                    getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
                    edit.requestFocus();
                    showKeyword(edit);
                }
            }
        });
        mSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edit.getText() != null && TextUtils.isEmpty(edit.getText().toString())) {
                    addDanmaku(edit.getText().toString());
                } else {
                    Toast.makeText(DanmuActivity.this, "输入内容不能为空", Toast.LENGTH_SHORT).show();
                }
            }
        });
        mShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDanmakuView.show();
            }
        });
        mHide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDanmakuView.hide();
            }
        });
        mPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDanmakuView.pause();
            }
        });
        mResume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDanmakuView.resume();
            }
        });
        mText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addDanmaku(null);
            }
        });
        mTextImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addDanmaKuShowTextAndImage();
            }
        });
    }

    private void initDanmaku() {
        // 设置最大显示行数
        HashMap<Integer, Integer> maxLinesPair = new HashMap<>();
        // 滚动弹幕最大显示5行
        maxLinesPair.put(BaseDanmaku.TYPE_SCROLL_RL, 4);
        // 设置是否禁止重叠
        HashMap<Integer, Boolean> overlappingEnablePair = new HashMap<>();
        overlappingEnablePair.put(BaseDanmaku.TYPE_SCROLL_RL, true);
        overlappingEnablePair.put(BaseDanmaku.TYPE_FIX_TOP, true);

        mContext = DanmakuContext.create();
        mContext.setDanmakuStyle(IDisplayer.DANMAKU_STYLE_STROKEN, 3)
                .setDuplicateMergingEnabled(false)
                .setScrollSpeedFactor(1.2f)
                .setScaleTextSize(1.2f)
                .setCacheStuffer(new SpannedCacheStuffer(), mCacheStufferAdapter) // 图文混排使用SpannedCacheStuffer
//        .setCacheStuffer(new BackgroundCacheStuffer())  // 绘制背景使用BackgroundCacheStuffer
                .setMaximumLines(maxLinesPair)
                .preventOverlapping(overlappingEnablePair)
                .setDanmakuMargin(40);

        if (mDanmakuView != null) {
            mParser = createParser();
            mDanmakuView.prepare(mParser, mContext);
            mDanmakuView.showFPS(true);
            mDanmakuView.enableDanmakuDrawingCache(true);
            mDanmakuView.setCallback(new master.flame.danmaku.controller.DrawHandler.Callback() {
                @Override
                public void updateTimer(DanmakuTimer timer) {
                }

                @Override
                public void drawingFinished() {

                }

                @Override
                public void danmakuShown(BaseDanmaku danmaku) {
                    Log.d(TAG, "弹幕文本 text=" + danmaku.text);
                }

                @Override
                public void prepared() {
                    mDanmakuView.start();
                }
            });
            mDanmakuView.setOnDanmakuClickListener(new IDanmakuView.OnDanmakuClickListener() {

                @Override
                public boolean onDanmakuClick(IDanmakus danmakus) {
                    Log.d(TAG, "onDanmakuClick: danmakus size:" + danmakus.size());
                    BaseDanmaku latest = danmakus.last();
                    if (null != latest) {
                        Log.d(TAG, "onDanmakuClick: text of latest danmaku:" + latest.text);
                        return true;
                    }
                    return false;
                }

                @Override
                public boolean onDanmakuLongClick(IDanmakus danmakus) {
                    return false;
                }

                @Override
                public boolean onViewClick(IDanmakuView view) {
//                    mMediaController.setVisibility(View.VISIBLE);
                    return false;
                }
            });
        }
    }

    private void addDanmaku(String content) {
        BaseDanmaku danmaku = mContext.mDanmakuFactory.createDanmaku(BaseDanmaku.TYPE_SCROLL_RL);
        if (danmaku == null || mDanmakuView == null) {
            return;
        }
        if (TextUtils.isEmpty(content)) {
            danmaku.text = "这是一条弹幕" + System.nanoTime();
        } else {
            danmaku.text = content;
        }
        danmaku.padding = 5;
        // 可能会被各种过滤器过滤并隐藏显示
        danmaku.priority = 0;
        danmaku.isLive = false;
        danmaku.setTime(mDanmakuView.getCurrentTime() + 1200);
        danmaku.textSize = 25f * (mParser.getDisplayer().getDensity() - 0.6f);
        danmaku.textColor = Color.RED;
        danmaku.textShadowColor = Color.WHITE;
        // danmaku.underlineColor = Color.GREEN;
        danmaku.borderColor = Color.GREEN;
        mDanmakuView.addDanmaku(danmaku);
    }

    /**
     * 图文弹幕
     */
    private void addDanmaKuShowTextAndImage() {
        BaseDanmaku danmaku = mContext.mDanmakuFactory.createDanmaku(BaseDanmaku.TYPE_SCROLL_RL);
        Drawable drawable = getResources().getDrawable(R.drawable.emotion);
        drawable.setBounds(0, 0, 100, 100);
        SpannableStringBuilder spannable = createSpannable(drawable);
        danmaku.text = spannable;
        danmaku.padding = 5;
        // 一定会显示, 一般用于本机发送的弹幕
        danmaku.priority = 1;
        danmaku.isLive = false;
        danmaku.setTime(mDanmakuView.getCurrentTime() + 1200);
        danmaku.textSize = 25f * (mParser.getDisplayer().getDensity() - 0.6f);
        danmaku.textColor = Color.RED;
        // 重要：如果有图文混排，最好不要设置描边(设textShadowColor=0)，否则会进行两次复杂的绘制导致运行效率降低
        danmaku.textShadowColor = 0;
        danmaku.underlineColor = Color.GREEN;
        mDanmakuView.addDanmaku(danmaku);
    }

    /**
     * 用来创建spannable
     *
     * @param drawable
     * @return
     */
    private SpannableStringBuilder createSpannable(Drawable drawable) {
        String text = "bitmap";
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(text);
        //ImageSpan.ALIGN_BOTTOM);
        ImageSpan span = new ImageSpan(drawable);
        spannableStringBuilder.setSpan(span, 0, text.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        spannableStringBuilder.append("图文混排");
        spannableStringBuilder.setSpan(new BackgroundColorSpan(Color.parseColor("#8A2233B1")), 0, spannableStringBuilder.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        return spannableStringBuilder;
    }

    private void hideKeyword(View view) {
        InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void showKeyword(View view) {
        InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.showSoftInput(view, 0);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mDanmakuView != null && mDanmakuView.isPrepared()) {
            mDanmakuView.pause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mDanmakuView != null && mDanmakuView.isPrepared() && mDanmakuView.isPaused()) {
            mDanmakuView.resume();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mDanmakuView != null) {
            // dont forget release!
            mDanmakuView.release();
            mDanmakuView = null;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (mDanmakuView != null) {
            // dont forget release!
            mDanmakuView.release();
            mDanmakuView = null;
        }
    }
}
