package com.personal.directwhatsapp;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public final class MainActivity extends Activity {
    private static final int INK = Color.rgb(24, 37, 32);
    private static final int MUTED = Color.rgb(105, 119, 111);
    private static final int GREEN = Color.rgb(23, 132, 88);
    private static final int DARK_GREEN = Color.rgb(16, 52, 40);
    private static final int LIME = Color.rgb(206, 239, 132);
    private static final int PAGE = Color.rgb(246, 248, 245);
    private static final int LINE = Color.rgb(225, 233, 227);
    private EditText phone, message;
    private Spinner country;
    private TextView error;
    private final String[] countries = {"🇵🇰   Pakistan  +92", "🇺🇸   United States  +1", "🇬🇧   United Kingdom  +44", "🇮🇳   India  +91", "🇦🇪   United Arab Emirates  +971", "🇸🇦   Saudi Arabia  +966", "🇦🇺   Australia  +61", "Other / full number"};
    private final String[] prefixes = {"92", "1", "44", "91", "971", "966", "61", ""};

    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        getWindow().setStatusBarColor(PAGE);
        getWindow().setNavigationBarColor(PAGE);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR | View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
        buildScreen();
    }

    private void buildScreen() {
        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(true);
        scroll.setClipToPadding(false);
        LinearLayout page = new LinearLayout(this);
        page.setOrientation(LinearLayout.VERTICAL);
        page.setPadding(dp(22), dp(15), dp(22), dp(30));
        page.setBackgroundColor(PAGE);
        scroll.addView(page);

        LinearLayout header = row();
        TextView mark = text("↗", 22, LIME, true);
        mark.setGravity(Gravity.CENTER);
        mark.setBackground(round(DARK_GREEN, 15));
        header.addView(mark, new LinearLayout.LayoutParams(dp(44), dp(44)));
        LinearLayout brand = column();
        brand.setPadding(dp(11), 0, 0, 0);
        brand.addView(text("direct", 19, INK, true));
        TextView tagline = text("WHATSAPP, WITHOUT THE CONTACT", 9, MUTED, true);
        tagline.setLetterSpacing(.08f);
        LinearLayout.LayoutParams tg = new LinearLayout.LayoutParams(-2, -2); tg.topMargin = dp(2); brand.addView(tagline, tg);
        header.addView(brand, new LinearLayout.LayoutParams(0, -2, 1));
        TextView privatePill = text("●  PRIVATE", 10, GREEN, true);
        privatePill.setGravity(Gravity.CENTER);
        privatePill.setPadding(dp(11), dp(8), dp(11), dp(8));
        privatePill.setBackground(pill(Color.rgb(232, 243, 235), 30));
        header.addView(privatePill);
        page.addView(header);

        TextView eyebrow = text("QUICK WHATSAPP CHAT", 11, GREEN, true);
        eyebrow.setLetterSpacing(.13f);
        LinearLayout.LayoutParams ep = new LinearLayout.LayoutParams(-1, -2); ep.topMargin = dp(40); page.addView(eyebrow, ep);
        TextView title = text("Start a chat,\nwithout saving.", 35, INK, true);
        title.setLetterSpacing(-.045f);
        LinearLayout.LayoutParams hp = new LinearLayout.LayoutParams(-1, -2); hp.topMargin = dp(12); page.addView(title, hp);
        TextView intro = text("Enter a phone number and open a conversation in the WhatsApp account you choose.", 15, MUTED, false);
        intro.setLineSpacing(dp(4), 1f);
        LinearLayout.LayoutParams ip = new LinearLayout.LayoutParams(-1, -2); ip.topMargin = dp(11); ip.bottomMargin = dp(22); page.addView(intro, ip);

        LinearLayout card = column();
        card.setPadding(dp(19), dp(20), dp(19), dp(19));
        card.setBackground(panel());
        card.setElevation(dp(3));
        page.addView(card, new LinearLayout.LayoutParams(-1, -2));
        card.addView(text("New conversation", 19, INK, true));
        TextView cardSub = text("The number stays out of your contacts.", 12, MUTED, false);
        LinearLayout.LayoutParams csp = new LinearLayout.LayoutParams(-1, -2); csp.topMargin = dp(5); csp.bottomMargin = dp(20); card.addView(cardSub, csp);

        card.addView(label("PHONE NUMBER", "COUNTRY CODE INCLUDED"));
        country = new Spinner(this);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, countries);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        country.setAdapter(adapter);
        country.setSelection(0);
        country.setPadding(dp(10), 0, dp(10), 0);
        country.setBackground(fieldBackground());
        LinearLayout.LayoutParams cp = new LinearLayout.LayoutParams(-1, dp(54)); cp.topMargin = dp(8); card.addView(country, cp);

        phone = input("e.g. 300 1234567", InputType.TYPE_CLASS_PHONE);
        LinearLayout.LayoutParams pp = new LinearLayout.LayoutParams(-1, dp(54)); pp.topMargin = dp(10); card.addView(phone, pp);
        error = text("", 12, Color.rgb(181, 66, 53), false);
        LinearLayout.LayoutParams er = new LinearLayout.LayoutParams(-1, -2); er.topMargin = dp(5); card.addView(error, er);

        LinearLayout msgLabel = row();
        LinearLayout.LayoutParams ml = new LinearLayout.LayoutParams(-1, -2); ml.topMargin = dp(18); card.addView(msgLabel, ml);
        msgLabel.addView(text("MESSAGE", 11, INK, true), new LinearLayout.LayoutParams(0, -2, 1));
        msgLabel.addView(text("OPTIONAL", 10, MUTED, true));
        message = input("Write a message…", InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        message.setGravity(Gravity.TOP | Gravity.START);
        message.setMinLines(3);
        message.setSingleLine(false);
        message.setPadding(dp(14), dp(13), dp(14), dp(13));
        LinearLayout.LayoutParams mp = new LinearLayout.LayoutParams(-1, dp(104)); mp.topMargin = dp(8); card.addView(message, mp);

        TextView open = text("Choose WhatsApp account", 15, Color.WHITE, true);
        open.setGravity(Gravity.CENTER);
        open.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
        open.setBackground(round(DARK_GREEN, 16));
        open.setElevation(dp(2));
        LinearLayout.LayoutParams bp = new LinearLayout.LayoutParams(-1, dp(56)); bp.topMargin = dp(18); card.addView(open, bp);
        open.setOnClickListener(v -> openChat());
        TextView hint = text("WhatsApp · Business · Vivo clone, if available", 11, MUTED, false);
        hint.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams hn = new LinearLayout.LayoutParams(-1, -2); hn.topMargin = dp(10); card.addView(hint, hn);

        LinearLayout stepsTitle = row();
        LinearLayout.LayoutParams stp = new LinearLayout.LayoutParams(-1, -2); stp.topMargin = dp(25); stp.bottomMargin = dp(11); page.addView(stepsTitle, stp);
        stepsTitle.addView(text("Simple by design", 14, INK, true), new LinearLayout.LayoutParams(0, -2, 1));
        stepsTitle.addView(text("3 STEPS", 9, MUTED, true));
        LinearLayout steps = row();
        page.addView(steps);
        steps.addView(step("01", "Enter\nnumber"), new LinearLayout.LayoutParams(0, dp(70), 1));
        LinearLayout.LayoutParams middle = new LinearLayout.LayoutParams(0, dp(70), 1); middle.leftMargin = dp(8); middle.rightMargin = dp(8);
        steps.addView(step("02", "Choose\naccount"), middle);
        steps.addView(step("03", "Review &\nsend"), new LinearLayout.LayoutParams(0, dp(70), 1));

        LinearLayout privacy = row();
        privacy.setGravity(Gravity.CENTER_VERTICAL);
        privacy.setPadding(dp(13), dp(12), dp(13), dp(12));
        privacy.setBackground(pill(Color.rgb(235, 241, 236), 14));
        LinearLayout.LayoutParams prv = new LinearLayout.LayoutParams(-1, -2); prv.topMargin = dp(16); page.addView(privacy, prv);
        TextView lock = text("✓", 14, GREEN, true); lock.setGravity(Gravity.CENTER);
        lock.setBackground(pill(Color.WHITE, 20));
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(dp(26), dp(26)); lp.rightMargin = dp(10); privacy.addView(lock, lp);
        TextView privacyText = text("No account, contact access, or saved message history. Your draft is handed to WhatsApp only when you open the chat.", 11, MUTED, false);
        privacyText.setLineSpacing(dp(2), 1f);
        privacy.addView(privacyText, new LinearLayout.LayoutParams(0, -2, 1));
        setContentView(scroll);
    }

    private void openChat() {
        String raw = phone.getText().toString().trim();
        String digits = raw.replaceAll("\\D", "");
        int selected = country.getSelectedItemPosition();
        if (selected < prefixes.length && !prefixes[selected].isEmpty()) {
            String prefix = prefixes[selected];
            String local = digits.replaceFirst("^0+", "");
            digits = local.startsWith(prefix) ? local : prefix + local;
        }
        if (digits.length() < 7 || digits.length() > 15) {
            error.setText("Enter a valid number with its country code (7–15 digits).");
            phone.requestFocus();
            return;
        }
        error.setText("");
        Object service = getSystemService(Context.INPUT_METHOD_SERVICE);
        if (service instanceof InputMethodManager) ((InputMethodManager)service).hideSoftInputFromWindow(message.getWindowToken(), 0);
        String draft = message.getText().toString().trim();
        String url = "https://wa.me/" + digits;
        if (!draft.isEmpty()) url += "?text=" + Uri.encode(draft);
        try {
            Intent chatIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(Intent.createChooser(chatIntent, "Choose WhatsApp account"));
        } catch (ActivityNotFoundException ex) {
            Toast.makeText(this, "No app is available to open this chat.", Toast.LENGTH_LONG).show();
        }
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
    private TextView text(String value, int size, int color, boolean bold) { TextView t = new TextView(this); t.setText(value); t.setTextSize(size); t.setTextColor(color); if (bold) t.setTypeface(Typeface.DEFAULT, Typeface.BOLD); return t; }
    private GradientDrawable round(int color, int radius) { GradientDrawable d = new GradientDrawable(); d.setColor(color); d.setCornerRadius(dp(radius)); return d; }
    private GradientDrawable pill(int color, int radius) { return round(color, radius); }
    private GradientDrawable fieldBackground() { GradientDrawable d = round(Color.rgb(248, 250, 248), 13); d.setStroke(dp(1), LINE); return d; }
    private GradientDrawable panel() { GradientDrawable d = round(Color.WHITE, 22); d.setStroke(dp(1), Color.rgb(234, 239, 235)); return d; }
    private int dp(int value) { return (int)(value * getResources().getDisplayMetrics().density + .5f); }
}


