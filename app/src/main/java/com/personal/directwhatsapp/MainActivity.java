package com.personal.directwhatsapp;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.CallLog;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class MainActivity extends Activity {
    private static final int CALL_LOG_PERMISSION_REQUEST = 41;
    private static final int INK = Color.rgb(24, 37, 32);
    private static final int MUTED = Color.rgb(105, 119, 111);
    private static final int GREEN = Color.rgb(23, 132, 88);
    private static final int DARK_GREEN = Color.rgb(16, 52, 40);
    private static final int LIME = Color.rgb(206, 239, 132);
    private static final int PAGE = Color.rgb(246, 248, 245);
    private static final int LINE = Color.rgb(225, 233, 227);
    private EditText phone;
    private Spinner country;
    private TextView error;
    private String pendingChatUrl;
    private final String[] countries = {"🇵🇰   Pakistan  +92", "🇺🇸   United States  +1", "🇬🇧   United Kingdom  +44", "🇮🇳   India  +91", "🇦🇪   United Arab Emirates  +971", "🇸🇦   Saudi Arabia  +966", "🇦🇺   Australia  +61", "Other / full number"};
    private final String[] prefixes = {"92", "1", "44", "91", "971", "966", "61", ""};

    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        getWindow().setStatusBarColor(PAGE);
        getWindow().setNavigationBarColor(PAGE);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR | View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
        buildScreen();
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
        LinearLayout.LayoutParams ep = new LinearLayout.LayoutParams(-1, -2); ep.topMargin = dp(30); page.addView(eyebrow, ep);
        TextView title = text("Message without\nsaving a number.", 33, INK, true);
        title.setLetterSpacing(-.035f);
        LinearLayout.LayoutParams hp = new LinearLayout.LayoutParams(-1, -2); hp.topMargin = dp(12); page.addView(title, hp);
        TextView intro = text("Enter a number, choose an account, then write your message in WhatsApp.", 14, MUTED, false);
        intro.setLineSpacing(dp(4), 1f);
        LinearLayout.LayoutParams ip = new LinearLayout.LayoutParams(-1, -2); ip.topMargin = dp(11); ip.bottomMargin = dp(22); page.addView(intro, ip);

        LinearLayout card = column();
        card.setPadding(dp(19), dp(20), dp(19), dp(19));
        card.setBackground(panel());
        card.setElevation(dp(3));
        page.addView(card, new LinearLayout.LayoutParams(-1, -2));
        card.addView(text("New conversation", 19, INK, true));
        TextView cardSub = text("Start a chat without adding a contact.", 12, MUTED, false);
        LinearLayout.LayoutParams csp = new LinearLayout.LayoutParams(-1, -2); csp.topMargin = dp(5); csp.bottomMargin = dp(20); card.addView(cardSub, csp);

        LinearLayout phoneHeader = row();
        phoneHeader.addView(text("PHONE NUMBER", 11, INK, true), new LinearLayout.LayoutParams(0, -2, 1));
        card.addView(phoneHeader);
        LinearLayout numberTools = row();
        LinearLayout.LayoutParams toolsParams = new LinearLayout.LayoutParams(-1, dp(44));
        toolsParams.topMargin = dp(9);
        card.addView(numberTools, toolsParams);
        TextView recentCalls = text("◷   Recent calls", 12, DARK_GREEN, true);
        recentCalls.setGravity(Gravity.CENTER);
        recentCalls.setBackground(pill(Color.rgb(232, 243, 235), 13));
        numberTools.addView(recentCalls, new LinearLayout.LayoutParams(0, -1, 1));
        recentCalls.setOnClickListener(v -> openRecentCalls());
        TextView paste = text("▣   Paste", 12, GREEN, true);
        paste.setGravity(Gravity.CENTER);
        paste.setBackground(pill(Color.rgb(242, 246, 242), 13));
        LinearLayout.LayoutParams pasteParams = new LinearLayout.LayoutParams(0, -1, 1);
        pasteParams.leftMargin = dp(8);
        numberTools.addView(paste, pasteParams);
        paste.setOnClickListener(v -> pasteNumber());
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
        TextView shareHint = text("Call history is read only when you tap Recent calls.", 11, MUTED, false);
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
        TextView privacyText = text("No contacts are saved. Call history is read only when you open Recent calls.", 11, MUTED, false);
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
        if (service instanceof InputMethodManager) ((InputMethodManager)service).hideSoftInputFromWindow(phone.getWindowToken(), 0);
        pendingChatUrl = "https://wa.me/" + digits;
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

    private void openRecentCalls() {
        if (Build.VERSION.SDK_INT < 23 || checkSelfPermission(Manifest.permission.READ_CALL_LOG) == PackageManager.PERMISSION_GRANTED) {
            showRecentCalls(readRecentCalls());
            return;
        }
        new AlertDialog.Builder(this)
                .setTitle("Allow Recent Calls access?")
                .setMessage("Direct reads your recent call entries only when you open this picker, so you can choose a number. Direct does not save your call history.")
                .setNegativeButton("Not now", (dialog, which) -> dialog.dismiss())
                .setPositiveButton("Continue", (dialog, which) -> requestPermissions(
                        new String[]{Manifest.permission.READ_CALL_LOG}, CALL_LOG_PERMISSION_REQUEST))
                .show();
    }

    @Override public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode != CALL_LOG_PERMISSION_REQUEST) return;
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            showRecentCalls(readRecentCalls());
        } else {
            Toast.makeText(this, "Call history access was not allowed. You can still share or paste a number.", Toast.LENGTH_LONG).show();
        }
    }

    private List<RecentCall> readRecentCalls() {
        List<RecentCall> calls = new ArrayList<>();
        String[] columns = {CallLog.Calls.NUMBER, CallLog.Calls.CACHED_NAME, CallLog.Calls.DATE};
        Uri uri = CallLog.Calls.CONTENT_URI.buildUpon().appendQueryParameter("limit", "50").build();
        try (Cursor cursor = getContentResolver().query(uri, columns, null, null, CallLog.Calls.DATE + " DESC")) {
            if (cursor == null) return calls;
            int numberIndex = cursor.getColumnIndex(CallLog.Calls.NUMBER);
            int nameIndex = cursor.getColumnIndex(CallLog.Calls.CACHED_NAME);
            int dateIndex = cursor.getColumnIndex(CallLog.Calls.DATE);
            SimpleDateFormat formatter = new SimpleDateFormat("MMM d · h:mm a", Locale.getDefault());
            while (cursor.moveToNext() && calls.size() < 50) {
                String number = numberIndex >= 0 ? cursor.getString(numberIndex) : null;
                if (number == null || number.trim().isEmpty()) continue;
                String name = nameIndex >= 0 ? cursor.getString(nameIndex) : null;
                long timestamp = dateIndex >= 0 ? cursor.getLong(dateIndex) : 0L;
                calls.add(new RecentCall(number.trim(), name == null || name.trim().isEmpty() ? "Unknown caller" : name.trim(),
                        timestamp > 0 ? formatter.format(new Date(timestamp)) : ""));
            }
        } catch (SecurityException ex) {
            Toast.makeText(this, "Android or Vivo blocked call history access. Use Share or Paste instead.", Toast.LENGTH_LONG).show();
        } catch (RuntimeException ex) {
            Toast.makeText(this, "Could not read recent calls on this phone.", Toast.LENGTH_LONG).show();
        }
        return calls;
    }

    private void showRecentCalls(List<RecentCall> calls) {
        Dialog dialog = new Dialog(this);
        LinearLayout sheet = column();
        sheet.setPadding(dp(22), dp(18), dp(22), dp(18));
        sheet.setBackground(round(Color.WHITE, 24));
        TextView title = text("Choose a recent number", 21, INK, true);
        sheet.addView(title);
        TextView subtitle = text("Select a call to fill the phone number.", 12, MUTED, false);
        LinearLayout.LayoutParams subtitleParams = new LinearLayout.LayoutParams(-1, -2);
        subtitleParams.topMargin = dp(5);
        subtitleParams.bottomMargin = dp(12);
        sheet.addView(subtitle, subtitleParams);

        if (calls.isEmpty()) {
            TextView empty = text("No recent numbers are available.", 14, MUTED, false);
            empty.setGravity(Gravity.CENTER);
            empty.setPadding(0, dp(24), 0, dp(24));
            sheet.addView(empty);
        } else {
            ScrollView listScroll = new ScrollView(this);
            listScroll.setFillViewport(false);
            LinearLayout list = column();
            for (RecentCall call : calls) {
                LinearLayout item = column();
                item.setPadding(dp(13), dp(11), dp(13), dp(11));
                item.setBackground(fieldBackground());
                item.addView(text(call.name, 14, INK, true));
                TextView detail = text(call.number + (call.time.isEmpty() ? "" : "   ·   " + call.time), 12, MUTED, false);
                LinearLayout.LayoutParams detailParams = new LinearLayout.LayoutParams(-1, -2);
                detailParams.topMargin = dp(3);
                item.addView(detail, detailParams);
                LinearLayout.LayoutParams itemParams = new LinearLayout.LayoutParams(-1, -2);
                itemParams.bottomMargin = dp(7);
                list.addView(item, itemParams);
                item.setOnClickListener(v -> {
                    phone.setText(call.number);
                    if (call.number.startsWith("+")) country.setSelection(prefixes.length - 1);
                    phone.setSelection(phone.length());
                    error.setText("");
                    dialog.dismiss();
                });
            }
            listScroll.addView(list);
            sheet.addView(listScroll, new LinearLayout.LayoutParams(-1, Math.min(dp(440), Math.max(dp(120), calls.size() * dp(66)))));
        }
        TextView close = text("Close", 14, GREEN, true);
        close.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams closeParams = new LinearLayout.LayoutParams(-1, dp(46));
        closeParams.topMargin = dp(8);
        sheet.addView(close, closeParams);
        close.setOnClickListener(v -> dialog.dismiss());
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

    private static final class RecentCall {
        final String number;
        final String name;
        final String time;
        RecentCall(String number, String name, String time) {
            this.number = number;
            this.name = name;
            this.time = time;
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
        sheet.setBackground(round(Color.WHITE, 26));
        View handle = new View(this);
        handle.setBackground(round(Color.rgb(218, 226, 220), 4));
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
    private TextView text(String value, int size, int color, boolean bold) { TextView t = new TextView(this); t.setText(value); t.setTextSize(size); t.setTextColor(color); if (bold) t.setTypeface(Typeface.DEFAULT, Typeface.BOLD); return t; }
    private GradientDrawable round(int color, int radius) { GradientDrawable d = new GradientDrawable(); d.setColor(color); d.setCornerRadius(dp(radius)); return d; }
    private GradientDrawable pill(int color, int radius) { return round(color, radius); }
    private GradientDrawable fieldBackground() { GradientDrawable d = round(Color.rgb(248, 250, 248), 13); d.setStroke(dp(1), LINE); return d; }
    private GradientDrawable panel() { GradientDrawable d = round(Color.WHITE, 22); d.setStroke(dp(1), Color.rgb(234, 239, 235)); return d; }
    private int dp(int value) { return (int)(value * getResources().getDisplayMetrics().density + .5f); }
}


