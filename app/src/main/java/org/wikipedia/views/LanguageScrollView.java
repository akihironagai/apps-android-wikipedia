package org.wikipedia.views;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.material.tabs.TabLayout;

import org.wikipedia.R;
import org.wikipedia.WikipediaApp;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static org.wikipedia.search.SearchFragment.LANG_BUTTON_TEXT_SIZE_LARGER;
import static org.wikipedia.search.SearchFragment.LANG_BUTTON_TEXT_SIZE_SMALLER;
import static org.wikipedia.util.ResourceUtil.getThemedColor;

public class LanguageScrollView extends ConstraintLayout {
    public interface Callback {
        void onLanguageTabSelected(@NonNull String selectedLanguageCode, int position);
        void onLanguageButtonClicked();
    }

    @BindView(R.id.horizontal_scroll_languages) TabLayout horizontalLanguageScroll;
    private Callback callback;
    private List<String> languageCodes;

    public LanguageScrollView(Context context) {
        super(context);
        init(context);
    }

    public LanguageScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public LanguageScrollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        inflate(context, R.layout.view_language_scroll, this);
        ButterKnife.bind(this);
        horizontalLanguageScroll.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                updateTabView(true, tab);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                updateTabView(false, tab);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                updateTabView(true, tab);
            }
        });
    }

    private void updateTabView(boolean selected, TabLayout.Tab tab) {
        View view = tab.getCustomView();
        if (selected) {
            if (view != null) {
                @ColorInt int color = getThemedColor(getContext(), R.attr.colorAccent);
                @ColorInt int paperColor = getThemedColor(getContext(), R.attr.paper_color);
                Drawable drawable = AppCompatResources.getDrawable(getContext(), R.drawable.lang_button_shape);
                updateTabLanguageCode(view, null, paperColor, drawable, color);
                updateTabLanguageLabel(view, null, color);
            }
            if (callback != null) {
                callback.onLanguageTabSelected(languageCodes.get(tab.getPosition()), tab.getPosition());
            }
        } else {
            if (view != null) {
                @ColorInt int color = getThemedColor(getContext(), R.attr.material_theme_de_emphasised_color);
                updateTabLanguageLabel(view, null, color);
                updateTabLanguageCode(view, null, color, AppCompatResources.getDrawable(getContext(), R.drawable.lang_button_shape_border), color);
            }
        }
    }

    @SuppressWarnings("checkstyle:magicnumber")
    public void setUpLanguageScrollTabData(@NonNull List<String> languageCodes, int position, @Nullable Callback callback) {
        if (this.callback != null) {
            this.callback = null;
        }

        this.callback = callback;
        this.languageCodes = languageCodes;

        if (horizontalLanguageScroll.getChildCount() > 0) {
            horizontalLanguageScroll.removeAllTabs();
        }

        for (int i = 0; i < languageCodes.size(); i++) {
            TabLayout.Tab tab = horizontalLanguageScroll.newTab();
            tab.setCustomView(createLanguageTab(languageCodes.get(i)));
            horizontalLanguageScroll.addTab(tab);
            updateTabView(false, tab);
        }

        if (horizontalLanguageScroll != null && horizontalLanguageScroll.getTabAt(position) != null) {
            horizontalLanguageScroll.postDelayed(() -> {
                if (!isAttachedToWindow()) {
                    return;
                }
                horizontalLanguageScroll.getTabAt(position).select();
            }, 100);
        }
    }

    @NonNull
    private View createLanguageTab(@NonNull String languageCode) {
        View tab = LayoutInflater.from(getContext()).inflate(R.layout.view_custom_language_tab, this, false);
        updateTabLanguageCode(tab, languageCode, null, null, null);
        updateTabLanguageLabel(tab, languageCode, null);
        return tab;
    }

    private void updateTabLanguageLabel(@NonNull View customView, @Nullable String languageCode, @ColorInt Integer textColor) {
        TextView languageLabelTextView = customView.findViewById(R.id.language_label);
        if (languageCode != null) {
            languageLabelTextView.setText(WikipediaApp.getInstance().language().getAppLanguageLocalizedName(languageCode));
        }
        if (textColor != null) {
            languageLabelTextView.setTextColor(textColor);
        }
    }

    private void updateTabLanguageCode(@NonNull View customView, @Nullable String languageCode, @ColorInt Integer textColor, @Nullable Drawable background, @ColorInt Integer backgroundColorTint) {
        TextView languageCodeTextView = customView.findViewById(R.id.language_code);
        if (languageCode != null) {
            languageCodeTextView.setText(languageCode);
            ViewUtil.formatLangButton(languageCodeTextView, languageCode, LANG_BUTTON_TEXT_SIZE_SMALLER, LANG_BUTTON_TEXT_SIZE_LARGER);
        }
        if (textColor != null) {
            languageCodeTextView.setTextColor(textColor);
        }
        if (background != null) {
            languageCodeTextView.setBackground(background);
        }
        if (backgroundColorTint != null) {
            languageCodeTextView.getBackground().setColorFilter(backgroundColorTint, PorterDuff.Mode.SRC_IN);
        }
    }

    public int getSelectedPosition() {
        return horizontalLanguageScroll.getSelectedTabPosition();
    }

    @OnClick(R.id.more_languages)
    void onLangButtonClick() {
        if (callback != null) {
            callback.onLanguageButtonClicked();
        }
    }
}
