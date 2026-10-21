package com.personal.directwhatsapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class MainActivity extends Activity {
    private static final String PREFS_NAME = "direct_preferences";
    private static final String PREF_SAVE_HISTORY = "save_recent_numbers";
    private static final String PREF_RECENT_NUMBERS = "recent_numbers";
    private static final String PREF_DARK_MODE = "dark_mode";
    private int INK;
    private int MUTED;
    private int GREEN;
    private int DARK_GREEN;
    private int LIME;
    private int PAGE;
    private int LINE;
    private int PANEL_COLOR;
    private int FIELD_COLOR;
    private int SOFT_GREEN;
    private int PRIVACY_COLOR;
    private EditText phone;
    private Spinner country;
    private TextView error;
    private String pendingChatUrl;
    private final PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
    private List<CountryOption> countryOptions = new ArrayList<>();
    private String[] countries;
    private String[] prefixes;
    private Switch saveHistorySwitch;
    private FrameLayout bannerAdContainer;
    private AdView bannerAdView;
    private FrameLayout pageFrame;
    private ScrollView homeScroll;
    private LinearLayout navigationBar;
    private String activeTab = "home";
    private final List<LinearLayout> navigationItems = new ArrayList<>();
    private final List<TextView> navigationIcons = new ArrayList<>();
    private final List<TextView> navigationLabels = new ArrayList<>();
    private final List<String> navigationKeys = new ArrayList<>();

    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        applyThemeColors();
        getWindow().setStatusBarColor(PAGE);
        getWindow().setNavigationBarColor(PAGE);
        updateSystemBarIcons();
        prepareCountries();
        buildScreen();
        MobileAds.initialize(this, initializationStatus -> loadBannerAd());
        handleIncomingNumber(getIntent());
    }

    @Override protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        handleIncomingNumber(intent);
    }

    private void buildScreen() {
        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(true);
        scroll.setClipToPadding(false);
        LinearLayout page = new LinearLayout(this);
        page.setOrientation(LinearLayout.VERTICAL);
        page.setPadding(dp(20), dp(12), dp(20), dp(18));
        page.setBackgroundColor(PAGE);
        scroll.addView(page);

        LinearLayout header = row();
        TextView mark = text("↗", 22, LIME, true);
        mark.setGravity(Gravity.CENTER);
        mark.setBackground(round(DARK_GREEN, 15));
        header.addView(mark, new LinearLayout.LayoutParams(dp(44), dp(44)));
        LinearLayout brand = column();
        brand.setPadding(dp(11), 0, 0, 0);
        LinearLayout brandTitleRow = row();
        brandTitleRow.addView(text("Direct Chat", 19, INK, true));
        TextView privatePill = text("●  PRIVATE", 9, GREEN, true);
        privatePill.setGravity(Gravity.CENTER);
        privatePill.setPadding(dp(8), dp(5), dp(8), dp(5));
        privatePill.setBackground(pill(SOFT_GREEN, 30));
        LinearLayout.LayoutParams privateParams = new LinearLayout.LayoutParams(-2, -2);
        privateParams.leftMargin = dp(8);
        brandTitleRow.addView(privatePill, privateParams);
        brand.addView(brandTitleRow);
        TextView tagline = text("WHATSAPP, WITHOUT THE CONTACT", 9, MUTED, true);
        tagline.setLetterSpacing(.08f);
        LinearLayout.LayoutParams tg = new LinearLayout.LayoutParams(-2, -2); tg.topMargin = dp(2); brand.addView(tagline, tg);
        header.addView(brand, new LinearLayout.LayoutParams(0, -2, 1));
        page.addView(header);

        TextView eyebrow = text("QUICK WHATSAPP CHAT", 11, GREEN, true);
        eyebrow.setLetterSpacing(.13f);
        LinearLayout.LayoutParams ep = new LinearLayout.LayoutParams(-1, -2); ep.topMargin = dp(22); page.addView(eyebrow, ep);
        TextView title = text("Message without\nsaving a number.", 30, INK, true);
        title.setLetterSpacing(-.035f);
        LinearLayout.LayoutParams hp = new LinearLayout.LayoutParams(-1, -2); hp.topMargin = dp(12); page.addView(title, hp);
        TextView intro = text("Enter a number, choose an account, then write your message in WhatsApp.", 14, MUTED, false);
        intro.setLineSpacing(dp(4), 1f);
        LinearLayout.LayoutParams ip = new LinearLayout.LayoutParams(-1, -2); ip.topMargin = dp(9); ip.bottomMargin = dp(18); page.addView(intro, ip);

        LinearLayout card = column();
        card.setPadding(dp(17), dp(17), dp(17), dp(16));
        card.setBackground(panel());
        card.setElevation(dp(3));
        page.addView(card, new LinearLayout.LayoutParams(-1, -2));
        card.addView(text("New conversation", 19, INK, true));
        TextView cardSub = text("Start a chat without adding a contact.", 12, MUTED, false);
        LinearLayout.LayoutParams csp = new LinearLayout.LayoutParams(-1, -2); csp.topMargin = dp(5); csp.bottomMargin = dp(20); card.addView(cardSub, csp);

        LinearLayout phoneHeader = row();
        phoneHeader.addView(text("PHONE NUMBER", 11, INK, true), new LinearLayout.LayoutParams(0, -2, 1));
        TextView autoCountry = text("AUTO", 10, GREEN, true);
        autoCountry.setPadding(dp(10), dp(6), dp(10), dp(6));
        autoCountry.setBackground(pill(SOFT_GREEN, 20));
        phoneHeader.addView(autoCountry);
        autoCountry.setOnClickListener(v -> {
            int detected = getDetectedCountryIndex();
            country.setSelection(detected);
            Toast.makeText(this, "Country: " + countryOptions.get(detected).label, Toast.LENGTH_SHORT).show();
        });
        card.addView(phoneHeader);
        LinearLayout numberTools = row();
        LinearLayout.LayoutParams toolsParams = new LinearLayout.LayoutParams(-1, dp(44));
        toolsParams.topMargin = dp(9);
        card.addView(numberTools, toolsParams);
        TextView recentCalls = text("◷   Saved numbers", 12, GREEN, true);
        recentCalls.setGravity(Gravity.CENTER);
        recentCalls.setContentDescription("Open saved numbers");
        recentCalls.setBackground(pill(SOFT_GREEN, 13));
        numberTools.addView(recentCalls, new LinearLayout.LayoutParams(0, -1, 1));
        recentCalls.setOnClickListener(v -> switchTab("recent"));
        TextView paste = text("▣   Paste", 12, GREEN, true);
        paste.setGravity(Gravity.CENTER);
        paste.setBackground(pill(FIELD_COLOR, 13));
        LinearLayout.LayoutParams pasteParams = new LinearLayout.LayoutParams(0, -1, 1);
        pasteParams.leftMargin = dp(8);
        numberTools.addView(paste, pasteParams);
        paste.setOnClickListener(v -> pasteNumber());
        country = new Spinner(this);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, countries) {
            private TextView styledCountryView(String value, boolean dropdown) {
                TextView view = new TextView(MainActivity.this);
                view.setText(value);
                view.setTextColor(INK);
                view.setTextSize(14);
                view.setGravity(Gravity.CENTER_VERTICAL);
                view.setPadding(dp(12), dp(dropdown ? 12 : 8), dp(12), dp(dropdown ? 12 : 8));
                if (dropdown) view.setBackgroundColor(PANEL_COLOR);
                return view;
            }

            @Override public View getView(int position, View convertView, android.view.ViewGroup parent) {
                return styledCountryView(getItem(position), false);
            }

            @Override public View getDropDownView(int position, View convertView, android.view.ViewGroup parent) {
                return styledCountryView(getItem(position), true);
            }
        };
        country.setAdapter(adapter);
        country.setSelection(getDetectedCountryIndex());
        country.setPadding(dp(10), 0, dp(10), 0);
        country.setBackground(fieldBackground());
        LinearLayout.LayoutParams cp = new LinearLayout.LayoutParams(-1, dp(54)); cp.topMargin = dp(8); card.addView(country, cp);

        phone = input("e.g. 300 1234567", InputType.TYPE_CLASS_PHONE);
        phone.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                String value = s == null ? "" : s.toString().trim();
                if (!value.startsWith("+") || value.length() < 3) return;
                try {
                    Phonenumber.PhoneNumber parsed = phoneUtil.parse(value, null);
                    selectCountryRegion(phoneUtil.getRegionCodeForNumber(parsed));
                } catch (NumberParseException ignored) { }
            }
            @Override public void afterTextChanged(Editable s) { }
        });
        LinearLayout.LayoutParams pp = new LinearLayout.LayoutParams(-1, dp(54)); pp.topMargin = dp(10); card.addView(phone, pp);
        TextView shareHint = text("Country auto-detected from your SIM or device region. Type + to detect from a number.", 11, MUTED, false);
        LinearLayout.LayoutParams shareHintParams = new LinearLayout.LayoutParams(-1, -2);
        shareHintParams.topMargin = dp(8);
        card.addView(shareHint, shareHintParams);
        error = text("", 12, Color.rgb(181, 66, 53), false);
        LinearLayout.LayoutParams er = new LinearLayout.LayoutParams(-1, -2); er.topMargin = dp(5); card.addView(error, er);

        TextView open = text("Continue to WhatsApp", 15, Color.WHITE, true);
        open.setGravity(Gravity.CENTER);
        open.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
        open.setBackground(round(DARK_GREEN, 16));
        open.setElevation(dp(2));
        LinearLayout.LayoutParams bp = new LinearLayout.LayoutParams(-1, dp(56)); bp.topMargin = dp(18); card.addView(open, bp);
        open.setOnClickListener(v -> openChat());
        TextView hint = text("Choose WhatsApp or WhatsApp Business", 11, MUTED, false);
        hint.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams hn = new LinearLayout.LayoutParams(-1, -2); hn.topMargin = dp(10); card.addView(hint, hn);

        bannerAdContainer = new FrameLayout(this);
        LinearLayout.LayoutParams adContainerParams = new LinearLayout.LayoutParams(-1, -2);
        adContainerParams.topMargin = dp(12);
        adContainerParams.gravity = Gravity.CENTER_HORIZONTAL;
        card.addView(bannerAdContainer, adContainerParams);

        homeScroll = scroll;
        pageFrame = new FrameLayout(this);
        pageFrame.addView(homeScroll, new FrameLayout.LayoutParams(-1, -1));

        LinearLayout appRoot = column();
        appRoot.setBackgroundColor(PAGE);
        appRoot.addView(pageFrame, new LinearLayout.LayoutParams(-1, 0, 1f));
        navigationBar = buildNavigationBar();
        appRoot.addView(navigationBar, new LinearLayout.LayoutParams(-1, dp(64)));
        setContentView(appRoot);
        updateNavigationAppearance();
    }


    private LinearLayout buildNavigationBar() {
        navigationItems.clear();
        navigationIcons.clear();
        navigationLabels.clear();
        navigationKeys.clear();
        LinearLayout bar = row();
        bar.setPadding(dp(7), dp(5), dp(7), dp(5));
        bar.setBackgroundColor(PANEL_COLOR);
        bar.setElevation(dp(5));
        String[][] destinations = {
                {"home", "⌂", "Home"},
                {"recent", "◷", "Recent"},
                {"guide", "ⓘ", "Guide"},
                {"settings", "⚙", "Settings"}
        };
        for (String[] destination : destinations) {
            final String tab = destination[0];
            LinearLayout item = column();
            item.setGravity(Gravity.CENTER);
            item.setPadding(dp(2), dp(3), dp(2), dp(3));
            TextView icon = text(destination[1], 19, MUTED, false);
            icon.setGravity(Gravity.CENTER);
            TextView label = text(destination[2], 10, MUTED, false);
            label.setGravity(Gravity.CENTER);
            item.addView(icon, new LinearLayout.LayoutParams(-1, -2));
            LinearLayout.LayoutParams labelParams = new LinearLayout.LayoutParams(-1, -2);
            labelParams.topMargin = dp(1);
            item.addView(label, labelParams);
            item.setContentDescription(destination[2] + " tab");
            item.setOnClickListener(v -> switchTab(tab));
            bar.addView(item, new LinearLayout.LayoutParams(0, -1, 1f));
            navigationItems.add(item);
            navigationIcons.add(icon);
            navigationLabels.add(label);
            navigationKeys.add(tab);
        }
        return bar;
    }

    private void updateNavigationAppearance() {
        for (int i = 0; i < navigationItems.size(); i++) {
            boolean selected = activeTab.equals(navigationKeys.get(i));
            navigationItems.get(i).setBackground(round(selected ? SOFT_GREEN : PANEL_COLOR, 14));
            navigationIcons.get(i).setTextColor(selected ? GREEN : MUTED);
            navigationLabels.get(i).setTextColor(selected ? GREEN : MUTED);
            navigationLabels.get(i).setTypeface(Typeface.DEFAULT, selected ? Typeface.BOLD : Typeface.NORMAL);
        }
    }

    private void switchTab(String tab) {
        if (pageFrame == null || homeScroll == null) return;
        activeTab = tab;
        for (int i = pageFrame.getChildCount() - 1; i >= 1; i--) {
            pageFrame.removeViewAt(i);
        }
        if ("home".equals(tab)) {
            homeScroll.setVisibility(View.VISIBLE);
        } else {
            homeScroll.setVisibility(View.GONE);
            pageFrame.addView(buildDestinationPage(tab), new FrameLayout.LayoutParams(-1, -1));
        }
        updateNavigationAppearance();
    }

    private View buildDestinationPage(String tab) {
        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(true);
        scroll.setClipToPadding(false);
        LinearLayout content = column();
        content.setPadding(dp(20), dp(20), dp(20), dp(26));
        content.setBackgroundColor(PAGE);
        scroll.addView(content);
        if ("recent".equals(tab)) buildRecentPage(content);
        else if ("guide".equals(tab)) buildGuidePage(content);
        else buildSettingsPage(content);
        return scroll;
    }

    private void addPageHeader(LinearLayout content, String eyebrow, String title, String subtitle) {
        TextView kicker = text(eyebrow, 10, GREEN, true);
        kicker.setLetterSpacing(.10f);
        content.addView(kicker);
        TextView heading = text(title, 27, INK, true);
        LinearLayout.LayoutParams headingParams = new LinearLayout.LayoutParams(-1, -2);
        headingParams.topMargin = dp(7);
        content.addView(heading, headingParams);
        TextView detail = text(subtitle, 13, MUTED, false);
        detail.setLineSpacing(dp(3), 1f);
        LinearLayout.LayoutParams detailParams = new LinearLayout.LayoutParams(-1, -2);
        detailParams.topMargin = dp(6);
        detailParams.bottomMargin = dp(14);
        content.addView(detail, detailParams);
    }

    private LinearLayout sectionCard(LinearLayout parent, String title, String subtitle) {
        LinearLayout card = column();
        card.setPadding(dp(16), dp(15), dp(16), dp(15));
        card.setBackground(panel());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(-1, -2);
        params.topMargin = dp(10);
        parent.addView(card, params);
        card.addView(text(title, 16, INK, true));
        if (subtitle != null && !subtitle.isEmpty()) {
            TextView detail = text(subtitle, 12, MUTED, false);
            detail.setLineSpacing(dp(2), 1f);
            LinearLayout.LayoutParams detailParams = new LinearLayout.LayoutParams(-1, -2);
            detailParams.topMargin = dp(5);
            card.addView(detail, detailParams);
        }
        return card;
    }

    private TextView primaryAction(String caption) {
        TextView action = text(caption, 14, Color.WHITE, true);
        action.setGravity(Gravity.CENTER);
        action.setPadding(dp(12), dp(12), dp(12), dp(12));
        action.setBackground(round(DARK_GREEN, 14));
        action.setMinHeight(dp(48));
        return action;
    }

    private void buildRecentPage(LinearLayout content) {
        addPageHeader(content, "YOUR ACTIVITY", "Recent numbers",
                "Reuse numbers you chose to save locally. You can also enter, paste, or share a number into Direct Chat.");
        boolean historyEnabled = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).getBoolean(PREF_SAVE_HISTORY, false);
        List<String> numbers = getSavedNumberList();
        LinearLayout card = sectionCard(content, "Saved numbers",
                historyEnabled ? "Saved privately on this device · " + numbers.size() + " of 30"
                        : "Local history is off. Turn it on in Settings to save numbers you open.");
        if (!historyEnabled) {
            TextView enable = primaryAction("Manage local history in Settings");
            LinearLayout.LayoutParams enableParams = new LinearLayout.LayoutParams(-1, -2);
            enableParams.topMargin = dp(12);
            card.addView(enable, enableParams);
            enable.setOnClickListener(v -> switchTab("settings"));
        } else if (numbers.isEmpty()) {
            TextView empty = text("No saved numbers yet. When local history is enabled, numbers you open will appear here.", 13, MUTED, false);
            empty.setLineSpacing(dp(3), 1f);
            LinearLayout.LayoutParams emptyParams = new LinearLayout.LayoutParams(-1, -2);
            emptyParams.topMargin = dp(12);
            card.addView(empty, emptyParams);
        } else {
            TextView clear = text("Clear all", 12, GREEN, true);
            clear.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
            LinearLayout.LayoutParams clearParams = new LinearLayout.LayoutParams(-1, dp(36));
            clearParams.topMargin = dp(4);
            card.addView(clear, clearParams);
            clear.setOnClickListener(v -> new AlertDialog.Builder(this)
                    .setTitle("Clear saved numbers?")
                    .setMessage("This removes the saved numbers stored by Direct Chat on this phone.")
                    .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                    .setPositiveButton("Clear", (dialog, which) -> {
                        getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit().remove(PREF_RECENT_NUMBERS).apply();
                        switchTab("recent");
                    }).show());

            for (String number : numbers) {
                LinearLayout item = row();
                item.setPadding(dp(12), dp(11), dp(9), dp(11));
                item.setBackground(fieldBackground());
                LinearLayout.LayoutParams itemParams = new LinearLayout.LayoutParams(-1, -2);
                itemParams.topMargin = dp(7);
                card.addView(item, itemParams);
                LinearLayout copy = column();
                copy.addView(text(formatDisplayNumber(number), 13, INK, true));
                TextView hint = text("Tap to use this number", 11, MUTED, false);
                LinearLayout.LayoutParams hintParams = new LinearLayout.LayoutParams(-1, -2);
                hintParams.topMargin = dp(3);
                copy.addView(hint, hintParams);
                item.addView(copy, new LinearLayout.LayoutParams(0, -2, 1f));
                TextView remove = text("×", 21, MUTED, false);
                remove.setGravity(Gravity.CENTER);
                remove.setPadding(dp(10), 0, dp(7), 0);
                remove.setContentDescription("Remove saved number");
                item.addView(remove, new LinearLayout.LayoutParams(-2, dp(42)));
                item.setOnClickListener(v -> useSavedNumber(number));
                remove.setOnClickListener(v -> {
                    List<String> remaining = getSavedNumberList();
                    remaining.remove(number);
                    persistSavedNumberList(remaining);
                    switchTab("recent");
                });
            }
        }
    }

    private String formatDisplayNumber(String number) {
        try {
            return phoneUtil.format(phoneUtil.parse(number, null), PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL);
        } catch (NumberParseException ignored) {
            return number;
        }
    }

    private void useSavedNumber(String number) {
        switchTab("home");
        if (phone == null) return;
        phone.setText(number);
        phone.setSelection(phone.length());
        error.setText("");
    }

    private void persistSavedNumberList(List<String> numbers) {
        StringBuilder stored = new StringBuilder();
        for (String number : numbers) {
            if (number == null || number.trim().isEmpty()) continue;
            if (stored.length() > 0) stored.append('\n');
            stored.append(number.trim());
        }
        android.content.SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();
        if (stored.length() == 0) editor.remove(PREF_RECENT_NUMBERS);
        else editor.putString(PREF_RECENT_NUMBERS, stored.toString());
        editor.apply();
    }

    private void buildGuidePage(LinearLayout content) {
        addPageHeader(content, "QUICK START", "How Direct Chat works",
                "Start a conversation without first creating a contact. Direct Chat opens WhatsApp; you stay in control of the message.");
        addGuideStep(content, "01", "Enter the phone number",
                "Choose the country code or paste a number. For reliable international formatting, enter the full number starting with + and the country code.");
        addGuideStep(content, "02", "Choose your WhatsApp account",
                "Tap Continue, then choose WhatsApp or WhatsApp Business. The selected app must be installed on your phone.");
        addGuideStep(content, "03", "Review the chat in WhatsApp",
                "Check the number and type your message in WhatsApp. Direct Chat does not send messages for you.");

        sectionCard(content, "Country selection",
                "We prefer your SIM country, then your mobile network country, then your device region. If those sources are unavailable, the app falls back to Pakistan. A number that starts with + can be used to detect its country code. You can still choose a country manually.");
        sectionCard(content, "Your privacy",
                "Saving recent numbers is optional and off by default. If enabled, numbers are stored locally on this phone; turning the setting off clears that list. Direct Chat does not read your call log. You can type a number, paste it, or share a number into Direct Chat from another app.");

        LinearLayout helpCard = sectionCard(content, "Troubleshooting", "");
        addHelpRow(helpCard, "Country looks wrong", "Open the country field on Home and select the correct country code.");
        addHelpRow(helpCard, "A number is rejected", "Check that the number is complete and includes the correct international prefix when needed.");
        addHelpRow(helpCard, "WhatsApp does not open", "Check that WhatsApp or WhatsApp Business is installed and available in your phone profile.");
    }

    private void addGuideStep(LinearLayout content, String number, String title, String description) {
        LinearLayout card = sectionCard(content, title, description);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) card.getLayoutParams();
        params.topMargin = dp(7);
        card.setLayoutParams(params);
        TextView badge = text(number, 11, GREEN, true);
        badge.setGravity(Gravity.CENTER);
        badge.setBackground(pill(SOFT_GREEN, 20));
        badge.setPadding(dp(10), dp(6), dp(10), dp(6));
        card.addView(badge, 0);
        LinearLayout.LayoutParams badgeParams = (LinearLayout.LayoutParams) badge.getLayoutParams();
        badgeParams.bottomMargin = dp(9);
        badge.setLayoutParams(badgeParams);
    }

    private void addHelpRow(LinearLayout parent, String title, String detail) {
        TextView heading = text(title, 13, INK, true);
        LinearLayout.LayoutParams headingParams = new LinearLayout.LayoutParams(-1, -2);
        headingParams.topMargin = dp(10);
        parent.addView(heading, headingParams);
        TextView copy = text(detail, 12, MUTED, false);
        copy.setLineSpacing(dp(2), 1f);
        LinearLayout.LayoutParams copyParams = new LinearLayout.LayoutParams(-1, -2);
        copyParams.topMargin = dp(3);
        parent.addView(copy, copyParams);
    }

    private void buildSettingsPage(LinearLayout content) {
        addPageHeader(content, "PREFERENCES", "Settings",
                "Control local history, appearance, and legal information.");

        LinearLayout appearance = sectionCard(content, "Appearance", "Choose the look that feels comfortable on your phone.");
        LinearLayout themeRow = row();
        LinearLayout themeCopy = column();
        themeCopy.addView(text("Dark mode", 14, INK, true));
        TextView themeHint = text("Use a darker background throughout the app.", 12, MUTED, false);
        LinearLayout.LayoutParams themeHintParams = new LinearLayout.LayoutParams(-1, -2);
        themeHintParams.topMargin = dp(3);
        themeCopy.addView(themeHint, themeHintParams);
        themeRow.addView(themeCopy, new LinearLayout.LayoutParams(0, -2, 1f));
        Switch darkSwitch = new Switch(this);
        darkSwitch.setChecked(getSharedPreferences(PREFS_NAME, MODE_PRIVATE).getBoolean(PREF_DARK_MODE, false));
        themeRow.addView(darkSwitch);
        LinearLayout.LayoutParams themeRowParams = new LinearLayout.LayoutParams(-1, -2);
        themeRowParams.topMargin = dp(12);
        appearance.addView(themeRow, themeRowParams);
        darkSwitch.setOnCheckedChangeListener((button, enabled) -> {
            getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit().putBoolean(PREF_DARK_MODE, enabled).apply();
            recreate();
        });

        LinearLayout history = sectionCard(content, "Local history",
                "Off by default. When enabled, Direct Chat keeps up to 30 numbers you open on this device only.");
        LinearLayout historyRow = row();
        LinearLayout historyCopy = column();
        historyCopy.addView(text("Save recent numbers", 14, INK, true));
        TextView historyHint = text("Turning this off also clears saved numbers.", 12, MUTED, false);
        LinearLayout.LayoutParams historyHintParams = new LinearLayout.LayoutParams(-1, -2);
        historyHintParams.topMargin = dp(3);
        historyCopy.addView(historyHint, historyHintParams);
        historyRow.addView(historyCopy, new LinearLayout.LayoutParams(0, -2, 1f));
        saveHistorySwitch = new Switch(this);
        saveHistorySwitch.setChecked(getSharedPreferences(PREFS_NAME, MODE_PRIVATE).getBoolean(PREF_SAVE_HISTORY, false));
        historyRow.addView(saveHistorySwitch);
        LinearLayout.LayoutParams historyRowParams = new LinearLayout.LayoutParams(-1, -2);
        historyRowParams.topMargin = dp(12);
        history.addView(historyRow, historyRowParams);
        saveHistorySwitch.setOnCheckedChangeListener((button, enabled) -> {
            android.content.SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();
            editor.putBoolean(PREF_SAVE_HISTORY, enabled);
            if (!enabled) editor.remove(PREF_RECENT_NUMBERS);
            editor.apply();
            historyHint.setText(enabled ? "Numbers you open will be stored only on this phone."
                    : "History is off; any previously saved numbers have been cleared.");
        });

        LinearLayout legal = sectionCard(content, "Privacy and terms",
                "Read how the app handles data and what to expect when using it.");
        addSettingsLink(legal, "Privacy Policy", () -> openLegalPage("https://toolnovasite.github.io/direct-whatsapp-android/privacy-policy.html"));
        addSettingsLink(legal, "Terms of Use", () -> openLegalPage("https://toolnovasite.github.io/direct-whatsapp-android/terms.html"));

        sectionCard(content, "About Direct Chat",
                "Direct Chat opens a chat link in WhatsApp or WhatsApp Business. You review and send messages inside that app; Direct Chat does not send them on your behalf.");
    }

    private void addSettingsLink(LinearLayout parent, String caption, Runnable action) {
        TextView link = text(caption + "   ›", 14, GREEN, true);
        link.setPadding(0, dp(13), 0, dp(9));
        parent.addView(link);
        link.setOnClickListener(v -> action.run());
    }

    private void openLegalPage(String url) {
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
        } catch (ActivityNotFoundException ex) {
            Toast.makeText(this, "Could not open the policy page.", Toast.LENGTH_LONG).show();
        }
    }

    private void loadBannerAd() {
        if (bannerAdContainer == null || isFinishing()) return;
        bannerAdContainer.post(() -> {
            if (bannerAdContainer == null || isFinishing() || bannerAdContainer.getWidth() <= 0) return;
            if (bannerAdView != null) {
                bannerAdView.destroy();
                bannerAdView = null;
            }
            float density = getResources().getDisplayMetrics().density;
            int availableWidthDp = (int) (bannerAdContainer.getWidth() / density);
            bannerAdView = new AdView(this);
            bannerAdView.setAdUnitId(AdMobConfig.BANNER_AD_UNIT_ID);
            bannerAdView.setAdSize(AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(
                    this, Math.max(1, availableWidthDp)));
            bannerAdContainer.removeAllViews();
            bannerAdContainer.addView(bannerAdView, new FrameLayout.LayoutParams(-1, -2, Gravity.CENTER));
            bannerAdView.loadAd(new AdRequest.Builder().build());
        });
    }

    @Override protected void onDestroy() {
        if (bannerAdView != null) {
            bannerAdView.destroy();
            bannerAdView = null;
        }
        super.onDestroy();
    }

    private void openChat() {
        String raw = phone.getText().toString().trim();
        String region = getSelectedRegion();
        Phonenumber.PhoneNumber parsed;
        try {
            parsed = phoneUtil.parse(raw, "ZZ".equals(region) ? null : region);
        } catch (NumberParseException ex) {
            error.setText("Enter a valid number and choose its country.");
            phone.requestFocus();
            return;
        }
        if (!phoneUtil.isPossibleNumber(parsed)) {
            error.setText("This number does not look complete for the selected country.");
            phone.requestFocus();
            return;
        }
        String e164 = phoneUtil.format(parsed, PhoneNumberUtil.PhoneNumberFormat.E164);
        if (e164.length() < 8 || e164.length() > 16) {
            error.setText("Enter a valid number with its country code.");
            phone.requestFocus();
            return;
        }
        String numberRegion = phoneUtil.getRegionCodeForNumber(parsed);
        selectCountryRegion(numberRegion);
        phone.setText(phoneUtil.format(parsed, PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL));
        phone.setSelection(phone.length());
        error.setText("");
        rememberNumber(e164);
        Object service = getSystemService(Context.INPUT_METHOD_SERVICE);
        if (service instanceof InputMethodManager) ((InputMethodManager)service).hideSoftInputFromWindow(phone.getWindowToken(), 0);
        pendingChatUrl = "https://wa.me/" + e164.substring(1);
        showAccountPicker();
    }

    private void handleIncomingNumber(Intent intent) {
        if (intent == null) return;
        String value = null;
        if (Intent.ACTION_VIEW.equals(intent.getAction()) && intent.getData() != null
                && "tel".equalsIgnoreCase(intent.getData().getScheme())) {
            value = intent.getData().getSchemeSpecificPart();
        } else if (Intent.ACTION_SEND.equals(intent.getAction())) {
            value = intent.getStringExtra(Intent.EXTRA_TEXT);
        }
        if (value != null) fillPhoneFromSharedText(value);
    }

    private void pasteNumber() {
        Object service = getSystemService(Context.CLIPBOARD_SERVICE);
        if (!(service instanceof ClipboardManager)) return;
        ClipboardManager clipboard = (ClipboardManager) service;
        if (!clipboard.hasPrimaryClip() || clipboard.getPrimaryClip() == null
                || clipboard.getPrimaryClip().getItemCount() == 0) {
            Toast.makeText(this, "Copy a phone number first.", Toast.LENGTH_SHORT).show();
            return;
        }
        CharSequence copied = clipboard.getPrimaryClip().getItemAt(0).coerceToText(this);
        if (copied == null || !fillPhoneFromSharedText(copied.toString())) {
            Toast.makeText(this, "No phone number found in the copied text.", Toast.LENGTH_SHORT).show();
        }
    }

    private void prepareCountries() {
        List<String> regions = new ArrayList<>(phoneUtil.getSupportedRegions());
        Collections.sort(regions, (first, second) ->
                new Locale("", first).getDisplayCountry().compareToIgnoreCase(new Locale("", second).getDisplayCountry()));
        countryOptions.clear();
        for (String region : regions) {
            int callingCode = phoneUtil.getCountryCodeForRegion(region);
            if (callingCode <= 0) continue;
            String countryName = new Locale("", region).getDisplayCountry();
            countryOptions.add(new CountryOption(region, String.valueOf(callingCode),
                    flagFor(region) + "   " + countryName + "  +" + callingCode));
        }
        countryOptions.add(new CountryOption("ZZ", "", "Other / full number"));
        countries = new String[countryOptions.size()];
        prefixes = new String[countryOptions.size()];
        for (int i = 0; i < countryOptions.size(); i++) {
            countries[i] = countryOptions.get(i).label;
            prefixes[i] = countryOptions.get(i).callingCode;
        }
    }

    private String flagFor(String region) {
        if (region == null || region.length() != 2) return "";
        int first = Character.toUpperCase(region.charAt(0)) - 'A' + 0x1F1E6;
        int second = Character.toUpperCase(region.charAt(1)) - 'A' + 0x1F1E6;
        return new String(new int[]{first, second}, 0, 2);
    }

    private String detectCountryRegion() {
        TelephonyManager manager = null;
        // Some devices (including OEM variants) do not report the subscription/radio
        // feature flags consistently. Asking for the service is safe without extra
        // permissions; the ISO getters below are guarded for devices without telephony.
        try {
            manager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        } catch (RuntimeException ignored) { }

        String simRegion = null;
        String networkRegion = null;
        if (manager != null) {
            try {
                simRegion = manager.getSimCountryIso();
            } catch (RuntimeException ignored) { }
            try {
                networkRegion = manager.getNetworkCountryIso();
            } catch (RuntimeException ignored) { }
        }

        return CountryDetectionPolicy.chooseRegion(
                simRegion,
                networkRegion,
                Locale.getDefault().getCountry(),
                this::isSupportedRegion);
    }

    private boolean isSupportedRegion(String region) {
        return region != null && region.length() == 2 && phoneUtil.getCountryCodeForRegion(region.toUpperCase(Locale.ROOT)) > 0;
    }

    private int getDetectedCountryIndex() {
        String detected = detectCountryRegion();
        for (int i = 0; i < countryOptions.size(); i++) {
            if (countryOptions.get(i).region.equals(detected)) return i;
        }
        return 0;
    }

    private String getSelectedRegion() {
        int selected = country == null ? getDetectedCountryIndex() : country.getSelectedItemPosition();
        return selected >= 0 && selected < countryOptions.size() ? countryOptions.get(selected).region : detectCountryRegion();
    }

    private void selectCountryRegion(String region) {
        if (region == null || region.isEmpty() || "ZZ".equals(region)) return;
        for (int i = 0; i < countryOptions.size(); i++) {
            if (countryOptions.get(i).region.equalsIgnoreCase(region)) {
                country.setSelection(i);
                return;
            }
        }
    }

    private void rememberNumber(String e164) {
        if (!getSharedPreferences(PREFS_NAME, MODE_PRIVATE).getBoolean(PREF_SAVE_HISTORY, false)) return;
        List<String> numbers = getSavedNumberList();
        numbers.remove(e164);
        numbers.add(0, e164);
        while (numbers.size() > 30) numbers.remove(numbers.size() - 1);
        StringBuilder stored = new StringBuilder();
        for (String number : numbers) {
            if (stored.length() > 0) stored.append('\n');
            stored.append(number);
        }
        getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit().putString(PREF_RECENT_NUMBERS, stored.toString()).apply();
    }

    private List<String> getSavedNumberList() {
        String saved = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).getString(PREF_RECENT_NUMBERS, "");
        List<String> numbers = new ArrayList<>();
        if (saved == null || saved.isEmpty()) return numbers;
        for (String number : saved.split("\\n")) {
            if (!number.trim().isEmpty()) numbers.add(number.trim());
        }
        return numbers;
    }

    private static final class CountryOption {
        final String region;
        final String callingCode;
        final String label;
        CountryOption(String region, String callingCode, String label) {
            this.region = region;
            this.callingCode = callingCode;
            this.label = label;
        }
    }

    private boolean fillPhoneFromSharedText(String value) {
        Matcher matcher = Pattern.compile("(?<!\\d)\\+?\\d[\\d\\s().-]{5,}\\d(?!\\d)").matcher(value);
        String candidate = null;
        while (matcher.find()) {
            String found = matcher.group().trim();
            int digitCount = found.replaceAll("\\D", "").length();
            if (digitCount >= 7 && digitCount <= 15) { candidate = found; break; }
        }
        if (candidate == null) return false;
        if (candidate.startsWith("00")) candidate = "+" + candidate.substring(2);
        phone.setText(candidate);
        if (candidate.startsWith("+")) country.setSelection(prefixes.length - 1);
        phone.setSelection(phone.length());
        phone.requestFocus();
        return true;
    }

    private void showAccountPicker() {
        Dialog dialog = new Dialog(this);
        LinearLayout sheet = column();
        sheet.setPadding(dp(22), dp(17), dp(22), dp(20));
        sheet.setBackground(round(PANEL_COLOR, 26));
        View handle = new View(this);
        handle.setBackground(round(LINE, 4));
        LinearLayout.LayoutParams handleParams = new LinearLayout.LayoutParams(dp(38), dp(4));
        handleParams.gravity = Gravity.CENTER_HORIZONTAL;
        handleParams.bottomMargin = dp(20);
        sheet.addView(handle, handleParams);
        TextView eyebrow = text("OPEN CHAT WITH", 10, GREEN, true);
        eyebrow.setLetterSpacing(.12f);
        sheet.addView(eyebrow);
        TextView title = text("Choose an account", 23, INK, true);
        LinearLayout.LayoutParams titleParams = new LinearLayout.LayoutParams(-1, -2);
        titleParams.topMargin = dp(5);
        sheet.addView(title, titleParams);
        TextView subtitle = text("Your number opens directly in your chosen app.", 13, MUTED, false);
        LinearLayout.LayoutParams subtitleParams = new LinearLayout.LayoutParams(-1, -2);
        subtitleParams.topMargin = dp(4);
        subtitleParams.bottomMargin = dp(17);
        sheet.addView(subtitle, subtitleParams);
        sheet.addView(accountChoice(dialog, "W", "WhatsApp", "Personal account", DARK_GREEN, "com.whatsapp"));
        LinearLayout.LayoutParams businessParams = new LinearLayout.LayoutParams(-1, -2);
        businessParams.topMargin = dp(10);
        sheet.addView(accountChoice(dialog, "B", "WhatsApp Business", "Business account", GREEN, "com.whatsapp.w4b"), businessParams);
        TextView cancel = text("Cancel", 14, MUTED, true);
        cancel.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams cancelParams = new LinearLayout.LayoutParams(-1, dp(48));
        cancelParams.topMargin = dp(8);
        sheet.addView(cancel, cancelParams);
        cancel.setOnClickListener(v -> dialog.dismiss());
        dialog.setContentView(sheet);
        Window window = dialog.getWindow();
        if (window != null) {
            window.setBackgroundDrawableResource(android.R.color.transparent);
            window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            WindowManager.LayoutParams params = window.getAttributes();
            params.width = getResources().getDisplayMetrics().widthPixels - dp(28);
            params.height = WindowManager.LayoutParams.WRAP_CONTENT;
            params.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
            params.dimAmount = .38f;
            window.setAttributes(params);
        }
        dialog.show();
        if (window != null) window.setLayout(getResources().getDisplayMetrics().widthPixels - dp(28), WindowManager.LayoutParams.WRAP_CONTENT);
    }

    private LinearLayout accountChoice(Dialog dialog, String mark, String title, String subtitle, int accent, String packageName) {
        LinearLayout option = row();
        option.setPadding(dp(13), dp(12), dp(13), dp(12));
        option.setBackground(fieldBackground());
        TextView badge = text(mark, 17, Color.WHITE, true);
        badge.setGravity(Gravity.CENTER);
        badge.setBackground(round(accent, 16));
        LinearLayout.LayoutParams badgeParams = new LinearLayout.LayoutParams(dp(44), dp(44));
        badgeParams.rightMargin = dp(12);
        option.addView(badge, badgeParams);
        LinearLayout copy = column();
        copy.addView(text(title, 15, INK, true));
        TextView detail = text(subtitle, 12, MUTED, false);
        LinearLayout.LayoutParams detailParams = new LinearLayout.LayoutParams(-1, -2);
        detailParams.topMargin = dp(3);
        copy.addView(detail, detailParams);
        option.addView(copy, new LinearLayout.LayoutParams(0, -2, 1));
        TextView arrow = text("›", 26, MUTED, false);
        option.addView(arrow);
        option.setOnClickListener(v -> {
            dialog.dismiss();
            Intent chatIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(pendingChatUrl));
            chatIntent.setPackage(packageName);
            try {
                startActivity(chatIntent);
            } catch (ActivityNotFoundException ex) {
                String appName = "com.whatsapp".equals(packageName) ? "WhatsApp" : "WhatsApp Business";
                Toast.makeText(this, appName + " is not installed or available on this profile.", Toast.LENGTH_LONG).show();
            }
        });
        return option;
    }

    private LinearLayout step(String number, String caption) {
        LinearLayout box = column();
        box.setGravity(Gravity.CENTER_VERTICAL);
        box.setPadding(dp(11), dp(10), dp(8), dp(10));
        box.setBackground(panel());
        TextView n = text(number, 10, GREEN, true);
        box.addView(n);
        TextView c = text(caption, 11, INK, true);
        c.setLineSpacing(dp(1), 1f);
        LinearLayout.LayoutParams cp = new LinearLayout.LayoutParams(-1, -2); cp.topMargin = dp(4); box.addView(c, cp);
        return box;
    }
    private LinearLayout label(String left, String right) {
        LinearLayout wrap = row();
        TextView l = text(left, 11, INK, true);
        TextView r = text(right, 9, MUTED, true);
        wrap.addView(l, new LinearLayout.LayoutParams(0, -2, 1));
        wrap.addView(r);
        return wrap;
    }
    private EditText input(String hint, int type) {
        EditText field = new EditText(this);
        field.setHint(hint); field.setTextSize(14); field.setTextColor(INK); field.setHintTextColor(Color.rgb(151, 162, 154));
        field.setInputType(type); field.setPadding(dp(14), dp(10), dp(14), dp(10)); field.setBackground(fieldBackground());
        if ((type & InputType.TYPE_TEXT_FLAG_MULTI_LINE) == 0) field.setSingleLine(true);
        return field;
    }
    private LinearLayout row() { LinearLayout l = new LinearLayout(this); l.setOrientation(LinearLayout.HORIZONTAL); l.setGravity(Gravity.CENTER_VERTICAL); return l; }
    private LinearLayout column() { LinearLayout l = new LinearLayout(this); l.setOrientation(LinearLayout.VERTICAL); return l; }
    private TextView text(String value, int size, int color, boolean bold) { TextView t = new TextView(this); t.setText(value); t.setTextSize(Math.max(11, size)); t.setTextColor(color); if (bold) t.setTypeface(Typeface.DEFAULT, Typeface.BOLD); return t; }
    private GradientDrawable round(int color, int radius) { GradientDrawable d = new GradientDrawable(); d.setColor(color); d.setCornerRadius(dp(radius)); return d; }
    private GradientDrawable pill(int color, int radius) { return round(color, radius); }
    private GradientDrawable fieldBackground() { GradientDrawable d = round(FIELD_COLOR, 13); d.setStroke(dp(1), LINE); return d; }
    private GradientDrawable panel() { GradientDrawable d = round(PANEL_COLOR, 22); d.setStroke(dp(1), LINE); return d; }

    private void applyThemeColors() {
        boolean dark = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).getBoolean(PREF_DARK_MODE, false);
        if (dark) {
            INK = Color.rgb(235, 242, 237);
            MUTED = Color.rgb(174, 190, 180);
            GREEN = Color.rgb(111, 211, 160);
            DARK_GREEN = Color.rgb(19, 70, 53);
            LIME = Color.rgb(206, 239, 132);
            PAGE = Color.rgb(18, 27, 23);
            LINE = Color.rgb(55, 72, 62);
            PANEL_COLOR = Color.rgb(29, 41, 34);
            FIELD_COLOR = Color.rgb(35, 49, 41);
            SOFT_GREEN = Color.rgb(35, 61, 47);
            PRIVACY_COLOR = Color.rgb(34, 48, 39);
        } else {
            INK = Color.rgb(24, 37, 32);
            MUTED = Color.rgb(105, 119, 111);
            GREEN = Color.rgb(23, 132, 88);
            DARK_GREEN = Color.rgb(16, 52, 40);
            LIME = Color.rgb(206, 239, 132);
            PAGE = Color.rgb(246, 248, 245);
            LINE = Color.rgb(225, 233, 227);
            PANEL_COLOR = Color.WHITE;
            FIELD_COLOR = Color.rgb(248, 250, 248);
            SOFT_GREEN = Color.rgb(232, 243, 235);
            PRIVACY_COLOR = Color.rgb(235, 241, 236);
        }
    }

    private void updateSystemBarIcons() {
        boolean dark = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).getBoolean(PREF_DARK_MODE, false);
        getWindow().getDecorView().setSystemUiVisibility(dark ? 0 :
                View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR | View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
    }

    private int dp(int value) { return (int)(value * getResources().getDisplayMetrics().density + .5f); }
}


