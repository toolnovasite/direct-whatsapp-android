package com.personal.directwhatsapp;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public final class MainActivity extends Activity {
    private static final int INK = Color.rgb(24, 35, 30);
    private static final int MUTED = Color.rgb(101, 113, 105);
    private static final int GREEN = Color.rgb(22, 136, 91);
    private static final int LINE = Color.rgb(224, 230, 225);
    private EditText phone, message;
    private Spinner country;
    private TextView error;
    private final String[] codes = {"+92 Pakistan", "+1 United States / Canada", "+44 United Kingdom", "+91 India", "+971 UAE", "+966 Saudi Arabia", "+61 Australia", "Other / full number"};
    private final String[] prefixes = {"92", "1", "44", "91", "971", "966", "61", ""};

    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        getWindow().setStatusBarColor(Color.rgb(245,247,242));
        getWindow().setNavigationBarColor(Color.rgb(245,247,242));
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR | View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
        buildScreen();
    }

    private void buildScreen() {
        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(true);
        LinearLayout page = new LinearLayout(this);
        page.setOrientation(LinearLayout.VERTICAL);
        page.setPadding(dp(24), dp(22), dp(24), dp(28));
        page.setBackgroundColor(Color.rgb(245,247,242));
        scroll.addView(page);

        LinearLayout header = row();
        TextView logo = text("◉", 23, Color.WHITE, true);
        logo.setGravity(Gravity.CENTER);
        logo.setBackground(round(GREEN, 15));
        header.addView(logo, new LinearLayout.LayoutParams(dp(42), dp(42)));
        TextView brand = text("  direct", 20, INK, true);
        header.addView(brand, new LinearLayout.LayoutParams(0, -2, 1));
        TextView privacy = text("●  Private on this device", 12, MUTED, false);
        privacy.setGravity(Gravity.CENTER_VERTICAL);
        header.addView(privacy);
        page.addView(header);

        TextView eyebrow = text("A QUICKER WAY TO REACH SOMEONE", 12, GREEN, true);
        eyebrow.setLetterSpacing(.08f);
        LinearLayout.LayoutParams ep = new LinearLayout.LayoutParams(-1, -2);
        ep.topMargin = dp(52); page.addView(eyebrow, ep);
        TextView title = text("Message without\nsaving the number.", 36, INK, true);
        title.setLetterSpacing(-.035f);
        LinearLayout.LayoutParams tp = new LinearLayout.LayoutParams(-1, -2); tp.topMargin = dp(13); page.addView(title, tp);
        TextView intro = text("Enter a phone number and jump straight into a WhatsApp chat.", 16, MUTED, false);
        intro.setLineSpacing(dp(4), 1f);
        LinearLayout.LayoutParams ip = new LinearLayout.LayoutParams(-1, -2); ip.topMargin = dp(12); ip.bottomMargin = dp(26); page.addView(intro, ip);

        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL); card.setPadding(dp(20), dp(22), dp(20), dp(22)); card.setBackground(round(Color.WHITE, 20));
        page.addView(card, new LinearLayout.LayoutParams(-1, -2));
        card.addView(text("Start a conversation", 19, INK, true));
        TextView sub = text("Use the recipient’s country code for best results.", 13, MUTED, false);
        LinearLayout.LayoutParams sp = new LinearLayout.LayoutParams(-1, -2); sp.topMargin = dp(6); sp.bottomMargin = dp(23); card.addView(sub, sp);

        card.addView(text("PHONE NUMBER", 12, INK, true));
        country = new Spinner(this);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, codes);
        country.setAdapter(adapter);
        country.setSelection(0);
        country.setBackground(round(Color.rgb(250,252,249), 11));
        LinearLayout.LayoutParams cp = new LinearLayout.LayoutParams(-1, dp(50)); cp.topMargin = dp(8); card.addView(country, cp);
        phone = input("e.g. 300 1234567", InputType.TYPE_CLASS_PHONE);
        LinearLayout.LayoutParams ph = new LinearLayout.LayoutParams(-1, dp(52)); ph.topMargin = dp(9); card.addView(phone, ph);
        error = text("", 12, Color.rgb(181,71,56), false);
        LinearLayout.LayoutParams er = new LinearLayout.LayoutParams(-1, -2); er.topMargin = dp(5); card.addView(error, er);

        TextView messageLabel = text("MESSAGE  ·  OPTIONAL", 12, INK, true);
        LinearLayout.LayoutParams ml = new LinearLayout.LayoutParams(-1, -2); ml.topMargin = dp(17); card.addView(messageLabel, ml);
        message = input("Write a message…", InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        message.setGravity(Gravity.TOP | Gravity.START); message.setMinLines(4); message.setPadding(dp(13), dp(13), dp(13), dp(13)); message.setSingleLine(false);
        LinearLayout.LayoutParams mp = new LinearLayout.LayoutParams(-1, dp(120)); mp.topMargin = dp(8); card.addView(message, mp);

        Button open = new Button(this);
        open.setText("Choose WhatsApp account  →"); open.setTextColor(Color.WHITE); open.setTextSize(15); open.setTypeface(Typeface.DEFAULT, Typeface.BOLD); open.setAllCaps(false); open.setBackground(round(GREEN, 12));
        LinearLayout.LayoutParams bp = new LinearLayout.LayoutParams(-1, dp(54)); bp.topMargin = dp(20); card.addView(open, bp);
        open.setOnClickListener(v -> openChat());

        TextView note = text("Choose WhatsApp, WhatsApp Business, or the Vivo clone if it appears. You send inside WhatsApp.", 12, MUTED, false);
        note.setGravity(Gravity.CENTER); note.setLineSpacing(dp(3), 1f);
        LinearLayout.LayoutParams np = new LinearLayout.LayoutParams(-1, -2); np.topMargin = dp(12); card.addView(note, np);

        TextView privacyNote = text("🔒  Your number and draft are not saved by this app. WhatsApp handles the chat after it opens.", 12, MUTED, false);
        privacyNote.setLineSpacing(dp(3), 1f);
        LinearLayout.LayoutParams pp = new LinearLayout.LayoutParams(-1, -2); pp.topMargin = dp(20); page.addView(privacyNote, pp);
        setContentView(scroll);
    }

    private void openChat() {
        String raw = phone.getText().toString().trim();
        String digits = raw.replaceAll("\\D", "");
        int selected = country.getSelectedItemPosition();
        if (selected < prefixes.length && !prefixes[selected].isEmpty()) digits = prefixes[selected] + digits.replaceFirst("^0+", "");
        if (digits.length() < 7 || digits.length() > 15) { error.setText("Enter a valid phone number (7–15 digits), including its country code."); phone.requestFocus(); return; }
        error.setText("");
        ((InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(message.getWindowToken(), 0);
        String draft = message.getText().toString().trim();
        String url = "https://wa.me/" + digits;
        if (!draft.isEmpty()) url += "?text=" + Uri.encode(draft);
        try {
            Intent chatIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(Intent.createChooser(chatIntent, "Choose WhatsApp account"));
        }
        catch (ActivityNotFoundException ex) { Toast.makeText(this, "No browser or WhatsApp app is available to open this chat.", Toast.LENGTH_LONG).show(); }
    }

    private EditText input(String hint, int type) {
        EditText field = new EditText(this); field.setSingleLine(type == InputType.TYPE_CLASS_PHONE); field.setHint(hint); field.setTextSize(15); field.setTextColor(INK); field.setHintTextColor(Color.rgb(149,159,151)); field.setInputType(type); field.setPadding(dp(13), dp(10), dp(13), dp(10)); field.setBackground(round(Color.rgb(250,252,249), 11)); return field;
    }
    private LinearLayout row() { LinearLayout l = new LinearLayout(this); l.setOrientation(LinearLayout.HORIZONTAL); l.setGravity(Gravity.CENTER_VERTICAL); return l; }
    private TextView text(String value, int size, int color, boolean bold) { TextView t = new TextView(this); t.setText(value); t.setTextSize(size); t.setTextColor(color); if (bold) t.setTypeface(Typeface.DEFAULT, Typeface.BOLD); return t; }
    private GradientDrawable round(int color, int radius) { GradientDrawable d = new GradientDrawable(); d.setColor(color); d.setCornerRadius(dp(radius)); return d; }
    private int dp(int value) { return (int)(value * getResources().getDisplayMetrics().density + .5f); }
}



