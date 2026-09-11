package app.morphe.extension.jhc;

import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.app.Application;
import android.app.Fragment;
import android.app.FragmentManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Icon;
import android.content.ComponentName;
import android.content.pm.ShortcutManager;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JhcUpdateCheckPatch {
    private static final String TAG = "MANCrimSon_update";
    private static final String PREFS_NAME = "mancrimson_update_prefs";
    private static final String KEY_SNOOZE_UNTIL = "snooze_until";
    private static final String KEY_SNOOZED_TAG = "snoozed_tag";
    private static final String KEY_SKIPPED_TAG = "skipped_tag";
    private static final String KEY_LAST_CHECK_TIME = "last_check_time";
    private static final String KEY_LAST_REMOTE_TAG = "last_remote_tag";

    // Target repository
    private static final String REPO_OWNER_NAME = "MANCrimSon/YouTube-ReVanced-Extended";
    private static final String REPO_RELEASES_API = "https://api.github.com/repos/" + REPO_OWNER_NAME + "/releases?per_page=10";

    private static String getObtainiumDeepLink(Context context) {
        String pkg = (context != null) ? context.getPackageName().toLowerCase(Locale.ROOT) : "";
        boolean isMusic = pkg.contains("music");
        boolean isAnddea = pkg.contains("anddea") || pkg.contains("rvx");

        if (isMusic) {
            if (isAnddea) {
                return "obtainium://app/%7B%22id%22%3A%22anddea.youtube.music%22%2C%22url%22%3A%22https%3A%2F%2Fgithub.com%2FMANCrimSon%2FYouTube-ReVanced-Extended%22%2C%22author%22%3A%22MANCrimSon%22%2C%22name%22%3A%22YT%20Music%20RVX%20%28anddea%29%22%2C%22additionalSettings%22%3A%22%7B%5C%22includePrereleases%5C%22%3Atrue%2C%5C%22fallbackToOlderReleases%5C%22%3Atrue%2C%5C%22versionDetection%5C%22%3Afalse%2C%5C%22apkFilterRegEx%5C%22%3A%5C%22%5Eyoutube-music-revanced-extended%5C%22%2C%5C%22autoApkFilterByArch%5C%22%3Atrue%2C%5C%22appName%5C%22%3A%5C%22YT%20Music%20RVX%20%28anddea%29%5C%22%7D%22%7D";
            } else {
                return "obtainium://app/%7B%22id%22%3A%22app.morphe.android.apps.youtube.music%22%2C%22url%22%3A%22https%3A%2F%2Fgithub.com%2FMANCrimSon%2FYouTube-ReVanced-Extended%22%2C%22author%22%3A%22MANCrimSon%22%2C%22name%22%3A%22YT%20Music%20Morphe%22%2C%22additionalSettings%22%3A%22%7B%5C%22includePrereleases%5C%22%3Atrue%2C%5C%22fallbackToOlderReleases%5C%22%3Atrue%2C%5C%22versionDetection%5C%22%3Afalse%2C%5C%22apkFilterRegEx%5C%22%3A%5C%22%5Eyoutube-music-morphe%5C%22%2C%5C%22autoApkFilterByArch%5C%22%3Atrue%7D%22%7D";
            }
        } else {
            if (isAnddea) {
                return "obtainium://app/%7B%22id%22%3A%22anddea.youtube%22%2C%22url%22%3A%22https%3A%2F%2Fgithub.com%2FMANCrimSon%2FYouTube-ReVanced-Extended%22%2C%22author%22%3A%22MANCrimSon%22%2C%22name%22%3A%22YouTube%20RVX%20%28anddea%29%22%2C%22additionalSettings%22%3A%22%7B%5C%22includePrereleases%5C%22%3Atrue%2C%5C%22fallbackToOlderReleases%5C%22%3Atrue%2C%5C%22versionDetection%5C%22%3Afalse%2C%5C%22apkFilterRegEx%5C%22%3A%5C%22%5Eyoutube-revanced-extended%5C%22%2C%5C%22autoApkFilterByArch%5C%22%3Afalse%2C%5C%22appName%5C%22%3A%5C%22YouTube%20RVX%20%28anddea%29%5C%22%7D%22%7D";
            } else {
                return "obtainium://app/%7B%22id%22%3A%22app.morphe.android.youtube%22%2C%22url%22%3A%22https%3A%2F%2Fgithub.com%2FMANCrimSon%2FYouTube-ReVanced-Extended%22%2C%22author%22%3A%22MANCrimSon%22%2C%22name%22%3A%22YouTube%20Morphe%22%2C%22additionalSettings%22%3A%22%7B%5C%22includePrereleases%5C%22%3Atrue%2C%5C%22fallbackToOlderReleases%5C%22%3Atrue%2C%5C%22versionDetection%5C%22%3Afalse%2C%5C%22apkFilterRegEx%5C%22%3A%5C%22%5Eyoutube-morphe%5C%22%2C%5C%22autoApkFilterByArch%5C%22%3Afalse%7D%22%7D";
            }
        }
    }
    private static String getAppDisplayName(Context context) {
        String pkg = (context != null) ? context.getPackageName().toLowerCase(Locale.ROOT) : "";
        boolean isMusic = pkg.contains("music");
        boolean isAnddea = pkg.contains("anddea") || pkg.contains("rvx");
        if (isMusic) {
            return isAnddea ? "YT Music RVX" : "YT Music Morphe";
        } else {
            return isAnddea ? "YouTube RVX" : "YouTube Morphe";
        }
    }
    private static String getSettingsBrandName(Context context) {
        String pkg = (context != null) ? context.getPackageName().toLowerCase(Locale.ROOT) : "";
        boolean isAnddea = pkg.contains("anddea") || pkg.contains("rvx");
        return isAnddea ? "RVX" : "Morphe";
    }
    private static final String OBTAINIUM_DOWNLOAD_URL = "https://github.com/ImranR98/Obtainium/releases/latest";

    private static final String ACTION_MANUAL_CHECK = "app.morphe.action.CHECK_UPDATES";

    // 10 seconds delay on normal startup
    private static final long STARTUP_DELAY_MS = 10000L;
    // 24 hours cooldown between automatic background checks
    private static final long API_COOLDOWN_MS = 86_400_000L;
    // Current latest release in MANCrimSon/YouTube-ReVanced-Extended is 411.
    // In CI build, NEXT_VER_CODE will dynamically overwrite this with the next tag (e.g. 412).
    private static final int EMBEDDED_BUILD_CODE = 411;
    // FALSE: dialog only appears if new update is available (and cooldown/snooze respected)
    private static final boolean FORCE_TEST_ALWAYS_SHOW = false;

    public static void checkUpdate(Context context) {
        if (context == null) return;
        try {
            String pkg = context.getPackageName().toLowerCase(Locale.ROOT);
            if (pkg.startsWith("com.google.android")) {
                // Root installation: in-app updater disabled (updates handled via Magisk/KernelSU)
                return;
            }
        } catch (Throwable ignored) {}

        registerLifecycleIfNeeded(context);
        cleanupShortcuts(context);

        boolean isManual = false;
        if (context instanceof Activity) {
            Intent intent = ((Activity) context).getIntent();
            if (intent != null && ACTION_MANUAL_CHECK.equals(intent.getAction())) {
                isManual = true;
                intent.setAction(Intent.ACTION_MAIN);
            }
        }

        final boolean manualCheck = isManual;
        long delay = manualCheck ? 300L : STARTUP_DELAY_MS;

        if (manualCheck && context instanceof Activity) {
            showToast(context, getString("toast_checking_updates"));
        }

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            try {
                Context appContext = context.getApplicationContext();
                SharedPreferences prefs = appContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

                long now = System.currentTimeMillis();
                long lastCheck = prefs.getLong(KEY_LAST_CHECK_TIME, 0L);
                long snoozeUntil = prefs.getLong(KEY_SNOOZE_UNTIL, 0L);
                if (!manualCheck && !FORCE_TEST_ALWAYS_SHOW && (now < snoozeUntil)) {
                    Log.d(TAG, "Update notifications paused until " + snoozeUntil);
                    return;
                }
                if (!manualCheck && !FORCE_TEST_ALWAYS_SHOW && API_COOLDOWN_MS > 0 && (now - lastCheck < API_COOLDOWN_MS)) {
                    Log.d(TAG, "Cooldown active, skipping check");
                    return;
                }

                new Thread(() -> performCheck(context, manualCheck)).start();
            } catch (Throwable t) {
                Log.e(TAG, "Error in checkUpdate scheduler", t);
            }
        }, delay);
    }

    private static boolean lifecycleRegistered = false;
    private static final String PREF_KEY_UPDATE = "jhc_morphe_update_check_action_sort_by_unsorted";
    private static WeakReference<Activity> currentActivityRef = new WeakReference<>(null);

    private static void registerLifecycleIfNeeded(Context context) {
        if (lifecycleRegistered || context == null) return;
        try {
            Application app = null;
            if (context instanceof Activity) {
                app = ((Activity) context).getApplication();
            } else if (context instanceof Application) {
                app = (Application) context;
            } else if (context.getApplicationContext() instanceof Application) {
                app = (Application) context.getApplicationContext();
            }
            if (app != null) {
                lifecycleRegistered = true;
                app.registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {
                    @Override
                    public void onActivityResumed(Activity activity) {
                        currentActivityRef = new WeakReference<>(activity);
                        Intent intent = activity.getIntent();
                        if (intent != null && ACTION_MANUAL_CHECK.equals(intent.getAction())) {
                            intent.setAction(Intent.ACTION_MAIN);
                            showToast(activity, getString("toast_checking_updates"));
                            new Thread(() -> performCheck(activity, true)).start();
                        }
                        scheduleInjection(activity);
                    }

                    @Override
                    public void onActivityStarted(Activity activity) {
                        scheduleInjection(activity);
                    }

                    @Override
                    public void onActivityCreated(Activity a, Bundle b) {
                        try {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                a.getFragmentManager().registerFragmentLifecycleCallbacks(
                                    new FragmentManager.FragmentLifecycleCallbacks() {
                                        @Override
                                        public void onFragmentResumed(FragmentManager fm, Fragment f) {
                                            tryInjectIntoFragment(f, a);
                                        }
                                        @Override
                                        public void onFragmentStarted(FragmentManager fm, Fragment f) {
                                            tryInjectIntoFragment(f, a);
                                        }
                                    }, true);
                            }
                        } catch (Throwable ignored) {}
                    }

                    @Override public void onActivityPaused(Activity a) {}
                    @Override public void onActivityStopped(Activity a) {}
                    @Override public void onActivitySaveInstanceState(Activity a, Bundle b) {}
                    @Override public void onActivityDestroyed(Activity a) {}
                });
            }
        } catch (Throwable t) {
            Log.e(TAG, "Failed to register lifecycle callbacks", t);
        }
    }

    private static void scheduleInjection(Activity activity) {
        if (activity == null) return;
        currentActivityRef = new WeakReference<>(activity);
        Handler handler = new Handler(Looper.getMainLooper());
        runInjectionPass(activity);
        handler.postDelayed(() -> runInjectionPass(activity), 150L);
        handler.postDelayed(() -> runInjectionPass(activity), 450L);
        handler.postDelayed(() -> runInjectionPass(activity), 1000L);
    }

    private static void runInjectionPass(Activity activity) {
        if (activity == null || activity.isFinishing()) return;
        if (Build.VERSION.SDK_INT >= 17 && activity.isDestroyed()) return;

        try {
            // 1. Attempt via static instances of Morphe / RVX
            tryInjectFromStaticInstances(activity);

            // 2. Attempt via Activity FragmentManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                FragmentManager fm = activity.getFragmentManager();
                if (fm != null) {
                    List<Fragment> fragments = fm.getFragments();
                    if (fragments != null) {
                        for (Fragment f : fragments) {
                            if (f != null && tryInjectIntoFragment(f, activity)) {
                                return;
                            }
                        }
                    }
                }
            }

            // 3. Attempt via SupportFragmentManager (AndroidX FragmentActivity)
            try {
                Method getSupportFm = activity.getClass().getMethod("getSupportFragmentManager");
                Object sfm = getSupportFm.invoke(activity);
                if (sfm != null) {
                    Method getFragments = sfm.getClass().getMethod("getFragments");
                    Object listObj = getFragments.invoke(sfm);
                    if (listObj instanceof List) {
                        for (Object f : (List<?>) listObj) {
                            if (f != null && tryInjectIntoFragment(f, activity)) {
                                return;
                            }
                        }
                    }
                }
            } catch (Throwable ignored) {}

            // 4. Attempt if Activity itself is PreferenceActivity
            if (activity instanceof PreferenceActivity) {
                PreferenceScreen screen = ((PreferenceActivity) activity).getPreferenceScreen();
                if (screen != null && isRootMorpheOrRvxScreen(screen, null)) {
                    tryInjectIntoScreen(screen, activity);
                }
            }
        } catch (Throwable t) {
            Log.d(TAG, "runInjectionPass error: " + t.getMessage());
        }
    }

    private static void tryInjectFromStaticInstances(Activity activity) {
        if (activity == null) return;
        String[] knownFragmentClasses = new String[] {
            "app.morphe.extension.shared.settings.preference.AbstractPreferenceFragment",
            "app.morphe.extension.shared.settings.preference.ToolbarPreferenceFragment",
            "app.morphe.extension.music.settings.preference.YouTubeMusicPreferenceFragment",
            "app.revanced.extension.shared.settings.preference.AbstractPreferenceFragment",
            "anddea.extension.shared.settings.preference.AbstractPreferenceFragment"
        };

        for (String clsName : knownFragmentClasses) {
            try {
                Class<?> clazz = Class.forName(clsName);
                Field instanceField = clazz.getField("instance");
                Object weakRefObj = instanceField.get(null);
                if (weakRefObj instanceof WeakReference) {
                    Object fragment = ((WeakReference<?>) weakRefObj).get();
                    if (fragment != null) {
                        if (tryInjectIntoFragment(fragment, activity)) {
                            return;
                        }
                    }
                }
            } catch (Throwable ignored) {}
        }
    }

    private static boolean tryInjectIntoFragment(Object fragment, Activity activity) {
        if (fragment == null || activity == null) return false;
        try {
            Class<?> cls = fragment.getClass();
            Method getScreenMethod = null;
            try {
                getScreenMethod = cls.getMethod("getPreferenceScreen");
            } catch (NoSuchMethodException ignored) {}

            if (getScreenMethod == null) return false;

            Object screenObj = getScreenMethod.invoke(fragment);
            if (screenObj instanceof PreferenceScreen) {
                PreferenceScreen screen = (PreferenceScreen) screenObj;
                if (isRootMorpheOrRvxScreen(screen, fragment)) {
                    return tryInjectIntoScreen(screen, activity);
                }
            }
        } catch (Throwable t) {
            Log.d(TAG, "tryInjectIntoFragment: " + t.getMessage());
        }
        return false;
    }

    private static boolean isRootMorpheOrRvxScreen(PreferenceScreen screen, Object fragment) {
        if (screen == null) return false;

        // 1. Check fragment class if provided
        if (fragment != null) {
            String clsName = fragment.getClass().getName().toLowerCase(Locale.ROOT);
            boolean isMorpheOrRvx = clsName.contains("morphe") 
                                 || clsName.contains("revanced") 
                                 || clsName.contains("anddea") 
                                 || clsName.contains("preference");
            if (!isMorpheOrRvx) {
                return false;
            }
        }

        // 2. Check screen key for nested sub-screens
        String key = screen.getKey();
        if (key != null) {
            String keyLower = key.toLowerCase(Locale.ROOT);
            if (keyLower.contains("player") || keyLower.contains("sponsorblock")
                    || keyLower.contains("video") || keyLower.contains("audio")
                    || keyLower.contains("overlay") || keyLower.contains("layout")
                    || keyLower.contains("ads") || keyLower.contains("sub_")
                    || keyLower.contains("flyout") || keyLower.contains("general")) {
                return false;
            }
        }

        return true;
    }

    private static boolean tryInjectIntoScreen(PreferenceScreen screen, Activity activity) {
        if (screen == null || activity == null) return false;
        try {
            if (screen.findPreference(PREF_KEY_UPDATE) != null) {
                return true;
            }

            Preference pref = new Preference(activity) {
                @Override
                public View getView(View convertView, ViewGroup parent) {
                    View view = super.getView(convertView, parent);
                    bindIconView(activity, view);
                    return view;
                }

                @Override
                protected void onBindView(View view) {
                    super.onBindView(view);
                    bindIconView(activity, view);
                }
            };
            pref.setKey(PREF_KEY_UPDATE);
            pref.setTitle(getString("update_settings_title"));
            pref.setPersistent(false);
            pref.setOrder(99999);

            String pkg = activity.getPackageName();
            boolean isMorphe = pkg != null && pkg.contains("morphe");

            // Copy layoutResource ONLY for non-Morphe (RVX), because Morphe already uses its own custom layout without icon_frame
            if (!isMorphe) {
                int layoutRes = 0;
                if (screen.getPreferenceCount() > 0) {
                    for (int i = 0; i < screen.getPreferenceCount(); i++) {
                        Preference p = screen.getPreference(i);
                        if (p != null && p.getLayoutResource() != 0) {
                            layoutRes = p.getLayoutResource();
                            break;
                        }
                    }
                }
                if (layoutRes != 0) {
                    pref.setLayoutResource(layoutRes);
                } else {
                    String[] layoutCandidates = new String[] {
                        "preference_with_icon"
                    };
                    for (String lName : layoutCandidates) {
                        try {
                            int id = activity.getResources().getIdentifier(lName, "layout", activity.getPackageName());
                            if (id != 0) {
                                pref.setLayoutResource(id);
                                break;
                            }
                        } catch (Throwable ignored) {}
                    }
                }
            }

            Drawable icon = createSettingsIcon(activity);
            if (icon != null) {
                pref.setIcon(icon);
            }

            try {
                pref.setIconSpaceReserved(true);
            } catch (Throwable ignored) {}

            final Activity actRef = activity;
            pref.setOnPreferenceClickListener(p -> {
                try {
                    Activity act = (!actRef.isFinishing()) ? actRef : currentActivityRef.get();
                    if (act != null) {
                        showToast(act, getString("toast_checking_updates"));
                        new Thread(() -> performCheck(act, true)).start();
                    }
                } catch (Throwable t) {
                    Log.e(TAG, "Failed to perform manual update check from settings", t);
                }
                return true;
            });

            screen.addPreference(pref);
            Log.i(TAG, "Successfully injected update preference into root settings screen!");
            return true;
        } catch (Throwable t) {
            Log.e(TAG, "Error injecting preference into screen", t);
            return false;
        }
    }

    private static void bindIconView(Activity activity, View view) {
        if (activity == null || view == null) return;
        try {
            // Force hide summary line, keeping title only
            View summaryView = view.findViewById(android.R.id.summary);
            if (summaryView != null) {
                summaryView.setVisibility(View.GONE);
            }

            Drawable icon = createSettingsIcon(activity);
            if (icon == null) return;

            // 1. Standard Android icon ID (used in Morphe YouTube and Morphe Music)
            View iv = view.findViewById(android.R.id.icon);

            // 2. Custom RVX YouTube icon ID (revanced_custom_icon)
            if (iv == null) {
                try {
                    int rvxIconId = activity.getResources().getIdentifier("revanced_custom_icon", "id", activity.getPackageName());
                    if (rvxIconId != 0) {
                        iv = view.findViewById(rvxIconId);
                    }
                } catch (Throwable ignored) {}
            }

            // 3. If ImageView found in layout - center icon without distortion
            if (iv instanceof ImageView) {
                ImageView img = (ImageView) iv;
                img.setImageDrawable(icon);
                img.setScaleType(ImageView.ScaleType.CENTER);
                img.setVisibility(View.VISIBLE);
                View iconFrame = view.findViewById(android.R.id.icon_frame);
                if (iconFrame != null) {
                    iconFrame.setVisibility(View.VISIBLE);
                }
                return;
            }

            // 4. If layout lacks icon container (RVX Music), dynamically insert 48dp slot
            if (view instanceof ViewGroup) {
                ViewGroup vg = (ViewGroup) view;
                View custom = vg.findViewWithTag("jhc_update_icon");
                ImageView img;
                if (custom instanceof ImageView) {
                    img = (ImageView) custom;
                } else {
                    img = new ImageView(activity);
                    img.setTag("jhc_update_icon");
                    img.setFocusable(false);
                    img.setClickable(false);

                    float density = activity.getResources().getDisplayMetrics().density;
                    int slotSize = Math.round(48f * density);
                    if (slotSize <= 0) slotSize = 96;

                    // 16dp margin and 48dp slot match standard grid of other RVX Music items (center 40dp, 16dp margin to text)
                    int marginStart = Math.max(0, Math.round(16f * density) - vg.getPaddingStart());
                    int marginEnd = Math.round(16f * density);

                    if (vg instanceof LinearLayout) {
                        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(slotSize, slotSize);
                        lp.gravity = Gravity.CENTER_VERTICAL;
                        lp.setMarginStart(marginStart);
                        lp.setMarginEnd(marginEnd);
                        lp.leftMargin = marginStart;
                        lp.rightMargin = marginEnd;
                        img.setLayoutParams(lp);
                    } else {
                        ViewGroup.MarginLayoutParams mlp = new ViewGroup.MarginLayoutParams(slotSize, slotSize);
                        mlp.setMarginStart(marginStart);
                        mlp.setMarginEnd(marginEnd);
                        mlp.leftMargin = marginStart;
                        mlp.rightMargin = marginEnd;
                        img.setLayoutParams(mlp);
                    }
                    img.setScaleType(ImageView.ScaleType.CENTER);
                    vg.addView(img, 0);
                }
                img.setImageDrawable(icon);
                img.setVisibility(View.VISIBLE);
            }
        } catch (Throwable t) {
            Log.e(TAG, "bindIconView error: " + t.getMessage(), t);
        }
    }

    private static void cleanupShortcuts(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1 && context != null) {
            try {
                Context appContext = (context.getApplicationContext() != null) ? context.getApplicationContext() : context;
                ShortcutManager sm = (ShortcutManager) appContext.getSystemService(Context.SHORTCUT_SERVICE);
                if (sm != null) {
                    sm.removeDynamicShortcuts(Collections.singletonList("morphe_check_updates"));
                    sm.removeAllDynamicShortcuts();
                    Log.d(TAG, "Removed dynamic shortcuts from system");
                }
            } catch (Throwable t) {
                Log.w(TAG, "Failed to remove dynamic shortcuts", t);
            }
        }
    }

    private static Drawable createSettingsIcon(Context context) {
        try {
            float density = context.getResources().getDisplayMetrics().density;
            String pkg = context.getPackageName();
            boolean isMorphe = pkg != null && pkg.contains("morphe");
            boolean isYtmRvx = pkg != null && pkg.contains("music") && !isMorphe;

            // In Morphe, the ImageView is fixed to 24dp in its custom layout.
            // In RVX, the icon slot is standard 48dp (with icon_frame).
            int slotSize = Math.round((isMorphe ? 24f : 48f) * density);
            if (slotSize <= 0) slotSize = isMorphe ? 48 : 96;

            // Glyph size (circular arrow) is 24dp
            int iconSize = Math.round(24f * density);
            if (iconSize <= 0) iconSize = 48;

            Bitmap bitmap = Bitmap.createBitmap(slotSize, slotSize, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);

            int color = Color.WHITE;
            try {
                TypedValue tv = new TypedValue();
                if (context.getTheme().resolveAttribute(android.R.attr.textColorPrimary, tv, true)) {
                    if (tv.type >= TypedValue.TYPE_FIRST_COLOR_INT && tv.type <= TypedValue.TYPE_LAST_COLOR_INT) {
                        color = tv.data;
                    } else if (tv.resourceId != 0) {
                        try {
                            if (Build.VERSION.SDK_INT >= 23) {
                                color = context.getColor(tv.resourceId);
                            } else {
                                color = context.getResources().getColor(tv.resourceId);
                            }
                        } catch (Throwable ignored) {
                            if (tv.data != 0) color = tv.data;
                        }
                    } else if (tv.data != 0) {
                        color = tv.data;
                    }
                }
            } catch (Throwable ignored) {}

            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            paint.setColor(color);

            Path path = null;
            // 1. Attempt to create exact Material Design 'refresh' vector via PathParser
            try {
                Class<?> ppClass = Class.forName("androidx.core.graphics.PathParser");
                Method m = ppClass.getMethod("createPathFromPathData", String.class);
                path = (Path) m.invoke(null, "M17.65,6.35C16.2,4.9 14.21,4 12,4c-4.42,0 -7.99,3.58 -7.99,8s3.57,8 7.99,8c3.73,0 6.84,-2.55 7.73,-6h-2.08c-.82,2.33 -3.04,4 -5.65,4-3.31,0 -6,-2.69 -6,-6s2.69,-6 6,-6c1.66,0 3.14,0.69 4.22,1.78L13,11h7V4l-2.35,2.35z");
            } catch (Throwable t) {
                try {
                    Class<?> ppClass2 = Class.forName("android.util.PathParser");
                    Method m2 = ppClass2.getMethod("createPathFromPathData", String.class);
                    path = (Path) m2.invoke(null, "M17.65,6.35C16.2,4.9 14.21,4 12,4c-4.42,0 -7.99,3.58 -7.99,8s3.57,8 7.99,8c3.73,0 6.84,-2.55 7.73,-6h-2.08c-.82,2.33 -3.04,4 -5.65,4-3.31,0 -6,-2.69 -6,-6s2.69,-6 6,-6c1.66,0 3.14,0.69 4.22,1.78L13,11h7V4l-2.35,2.35z");
                } catch (Throwable ignored) {}
            }

            float scale = (float) iconSize / 24f;
            float offsetX = (slotSize - iconSize) / 2f;
            float offsetY = (slotSize - iconSize) / 2f;

            if (isYtmRvx) {
                // In ReVanced Extended for YouTube Music, vector preference icons (revanced_preference_screen_*)
                // have an internal offset (pivotX=300, scale=0.57 in 960 viewport -> center at 20.13dp instead of 24dp).
                // Shift glyph left by 3.87dp for exact alignment with the other 13 menu items.
                offsetX -= 3.87f * density;
            }

            if (path != null) {
                paint.setStyle(Paint.Style.FILL);
                Matrix matrix = new Matrix();
                matrix.setScale(scale, scale);
                matrix.postTranslate(offsetX, offsetY);
                path.transform(matrix);
                canvas.drawPath(path, paint);
            } else {
                // 2. Fallback direct drawing of refresh circular arrow via Canvas
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth(2.3f * density);
                paint.setStrokeCap(Paint.Cap.ROUND);

                float centerX = slotSize / 2f;
                if (isYtmRvx) {
                    centerX -= 3.87f * density;
                }
                float centerY = slotSize / 2f;
                float r = 7.5f * density;
                RectF arcBounds = new RectF(centerX - r, centerY - r, centerX + r, centerY + r);
                canvas.drawArc(arcBounds, 45, 275, false, paint);

                Paint arrowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
                arrowPaint.setColor(color);
                arrowPaint.setStyle(Paint.Style.FILL);

                float arrowSize = 3.2f * density;
                float tipX = centerX + (float) (r * Math.cos(Math.toRadians(45)));
                float tipY = centerY + (float) (r * Math.sin(Math.toRadians(45)));
                Path arrow = new Path();
                arrow.moveTo(tipX + arrowSize * 0.2f, tipY - arrowSize * 1.3f);
                arrow.lineTo(tipX + arrowSize * 1.5f, tipY + arrowSize * 0.5f);
                arrow.lineTo(tipX - arrowSize * 1.1f, tipY + arrowSize * 0.5f);
                arrow.close();
                canvas.drawPath(arrow, arrowPaint);
            }

            return new BitmapDrawable(context.getResources(), bitmap);
        } catch (Throwable t) {
            Log.w(TAG, "Failed to create settings icon", t);
            return null;
        }
    }

    private static void performCheck(Context context, boolean manualCheck) {
        try {
            Context appContext = context.getApplicationContext();
            SharedPreferences prefs = appContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

            String targetTag = null;
            String downloadUrl = null;
            String appVersion = "";
            String patchVersion = "";
            String changelogUrl = null;

            // Channel 1: GitHub JSON API
            try {
                HttpURLConnection conn = (HttpURLConnection) new URL(REPO_RELEASES_API).openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Accept", "application/vnd.github+json");
                conn.setRequestProperty("User-Agent", "MANCrimSon-Update-Checker");
                conn.setConnectTimeout(6000);
                conn.setReadTimeout(6000);

                int code = conn.getResponseCode();
                if (code == 200) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line);
                    }
                    reader.close();
                    conn.disconnect();

                    JSONArray releases = new JSONArray(sb.toString());
                    for (int i = 0; i < releases.length(); i++) {
                        JSONObject rel = releases.getJSONObject(i);
                        String tag = rel.optString("tag_name", "").trim();
                        if (tag.isEmpty()) continue;

                        JSONArray assets = rel.optJSONArray("assets");
                        if (assets == null || assets.length() == 0) continue;

                        String matchedUrl = findMatchingAsset(context, assets);
                        if (matchedUrl == null) continue;

                        String body = rel.optString("body", "");
                        PatchInfo pInfo = extractPatchInfo(context, body);

                        targetTag = tag;
                        downloadUrl = matchedUrl;
                        appVersion = extractVersionFromUrl(matchedUrl);
                        patchVersion = pInfo.version;

                        if (pInfo.changelogUrl != null && !pInfo.changelogUrl.isEmpty()) {
                            changelogUrl = pInfo.changelogUrl;
                        } else {
                            changelogUrl = rel.optString("html_url", "https://github.com/" + REPO_OWNER_NAME + "/releases/tag/" + tag);
                        }
                        break;
                    }
                } else {
                    Log.w(TAG, "GitHub API returned " + code + ", falling back to releases.atom");
                    conn.disconnect();
                }
            } catch (Throwable t) {
                Log.w(TAG, "GitHub API check failed, falling back to releases.atom", t);
            }

            // Channel 2 (Backup without rate limits): releases.atom + expanded_assets
            if (targetTag == null || downloadUrl == null) {
                try {
                    String atomUrl = "https://github.com/" + REPO_OWNER_NAME + "/releases.atom";
                    HttpURLConnection atomConn = (HttpURLConnection) new URL(atomUrl).openConnection();
                    atomConn.setRequestMethod("GET");
                    atomConn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64)");
                    atomConn.setConnectTimeout(6000);
                    atomConn.setReadTimeout(6000);

                    if (atomConn.getResponseCode() == 200) {
                        BufferedReader reader = new BufferedReader(new InputStreamReader(atomConn.getInputStream()));
                        StringBuilder sb = new StringBuilder();
                        String line;
                        while ((line = reader.readLine()) != null) {
                            sb.append(line);
                        }
                        reader.close();
                        atomConn.disconnect();

                        String atom = sb.toString();
                        Matcher tagMatcher = Pattern.compile("/releases/tag/([^\"'\\s]+)").matcher(atom);
                        if (tagMatcher.find()) {
                            targetTag = tagMatcher.group(1);
                        }
                        Matcher verMatcher = Pattern.compile("YouTube-Morphe:\\s*([0-9.]+)").matcher(atom);
                        if (verMatcher.find()) {
                            appVersion = verMatcher.group(1);
                        } else {
                            appVersion = "21.13.164";
                        }
                        PatchInfo atomPatchInfo = extractPatchInfo(context, atom);
                        patchVersion = atomPatchInfo.version;
                        if (targetTag != null) {
                            // Try expanded_assets for direct APK link
                            try {
                                String assetsUrl = "https://github.com/" + REPO_OWNER_NAME + "/releases/expanded_assets/" + targetTag;
                                HttpURLConnection expConn = (HttpURLConnection) new URL(assetsUrl).openConnection();
                                expConn.setRequestMethod("GET");
                                expConn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64)");
                                expConn.setConnectTimeout(5000);
                                expConn.setReadTimeout(5000);
                                if (expConn.getResponseCode() == 200) {
                                    BufferedReader expReader = new BufferedReader(new InputStreamReader(expConn.getInputStream()));
                                    StringBuilder expSb = new StringBuilder();
                                    String expLine;
                                    while ((expLine = expReader.readLine()) != null) expSb.append(expLine);
                                    expReader.close();
                                    expConn.disconnect();
                                    Matcher linkMatcher = Pattern.compile("href=\"([^\"]*releases/download/[^\"]+)\"").matcher(expSb.toString());
                                    java.util.List<String> expUrls = new java.util.ArrayList<>();
                                    while (linkMatcher.find()) {
                                        String foundHref = linkMatcher.group(1);
                                        expUrls.add(foundHref.startsWith("/") ? ("https://github.com" + foundHref) : foundHref);
                                    }
                                    downloadUrl = findMatchingUrl(context, expUrls);
                                } else {
                                    expConn.disconnect();
                                }
                            } catch (Throwable ignored) {}

                            if (downloadUrl == null) {
                                String fbPkg = (context != null) ? context.getPackageName().toLowerCase(Locale.ROOT) : "";
                                boolean fbMusic = fbPkg.contains("music");
                                boolean fbRoot = fbPkg.startsWith("com.google.android");
                                String appPrefix = fbMusic ? "youtube-music-morphe" : "youtube-morphe";
                                String ext = fbRoot ? "-module-v" + appVersion + "-all.zip" : "-v" + appVersion + "-all.apk";
                                downloadUrl = "https://github.com/" + REPO_OWNER_NAME + "/releases/download/" + targetTag + "/" + appPrefix + ext;
                            }

                            if (atomPatchInfo.changelogUrl != null && !atomPatchInfo.changelogUrl.isEmpty()) {
                                changelogUrl = atomPatchInfo.changelogUrl;
                            } else {
                                changelogUrl = "https://github.com/" + REPO_OWNER_NAME + "/releases/tag/" + targetTag;
                            }
                        }
                        Log.d(TAG, "Extracted release from releases.atom successfully! Tag=" + targetTag);
                    } else {
                        atomConn.disconnect();
                    }
                } catch (Throwable t) {
                    Log.e(TAG, "releases.atom fallback failed", t);
                }
            }

            prefs.edit().putLong(KEY_LAST_CHECK_TIME, System.currentTimeMillis()).apply();

            if (targetTag == null || downloadUrl == null) {
                Log.d(TAG, "No matching APK found in releases or network unreachable");
                if (manualCheck && context instanceof Activity) {
                    ((Activity) context).runOnUiThread(() -> 
                        showToast(context, getString("toast_check_failed")));
                }
                return;
            }

            boolean isLatest = false;
            if (!FORCE_TEST_ALWAYS_SHOW && !manualCheck) {
                long snoozeUntil = prefs.getLong(KEY_SNOOZE_UNTIL, 0L);
                long now = System.currentTimeMillis();
                if (now < snoozeUntil) {
                    Log.d(TAG, "Update notifications are paused until " + snoozeUntil);
                    return;
                }

                String skippedTag = prefs.getString(KEY_SKIPPED_TAG, "");
                if (targetTag.equals(skippedTag)) {
                    Log.d(TAG, "Build " + targetTag + " was skipped by user");
                    return;
                }

                if (EMBEDDED_BUILD_CODE > 0) {
                    int remoteBuildCode = parseNumericTag(targetTag);
                    if (remoteBuildCode > 0 && remoteBuildCode <= EMBEDDED_BUILD_CODE) {
                        Log.d(TAG, "App is up to date (remote: " + remoteBuildCode + ", installed: " + EMBEDDED_BUILD_CODE + ")");
                        return;
                    }
                }
            } else if (manualCheck && EMBEDDED_BUILD_CODE > 0 && !FORCE_TEST_ALWAYS_SHOW) {
                int remoteBuildCode = parseNumericTag(targetTag);
                if (remoteBuildCode > 0 && remoteBuildCode <= EMBEDDED_BUILD_CODE) {
                    isLatest = true;
                }
            }

            final String finalTag = targetTag;
            final String finalUrl = downloadUrl;
            final String finalVer = appVersion;
            final String finalPatchVer = patchVersion;
            final String finalChangelog = changelogUrl;
            final boolean finalIsLatest = isLatest;

            if (context instanceof Activity) {
                ((Activity) context).runOnUiThread(() -> 
                    showDialog((Activity) context, finalTag, finalVer, finalPatchVer, finalUrl, finalChangelog, finalIsLatest));
            }
        } catch (Throwable t) {
            Log.e(TAG, "Error checking updates", t);
        }
    }

    private static class PatchInfo {
        final String version;
        final String changelogUrl;

        PatchInfo(String version, String changelogUrl) {
            this.version = (version != null) ? version : "";
            this.changelogUrl = (changelogUrl != null) ? changelogUrl : "";
        }
    }

    private static PatchInfo extractPatchInfo(Context context, String body) {
        if (body == null || body.isEmpty()) {
            return new PatchInfo("", "");
        }

        String pkg = (context != null) ? context.getPackageName().toLowerCase(Locale.ROOT) : "";
        boolean isMusic = pkg.contains("music");
        boolean isAnddea = pkg.contains("anddea") || pkg.contains("rvx");

        String[] targetRepos;
        if (isAnddea) {
            targetRepos = new String[] { "anddea/revanced-patches" };
        } else if (isMusic) {
            targetRepos = new String[] { "MorpheApp/morphe-patches", "sashade8-ship-it/dual-vot-patches" };
        } else {
            targetRepos = new String[] { "sashade8-ship-it/dual-vot-patches", "MorpheApp/morphe-patches" };
        }

        for (String repo : targetRepos) {
            try {
                Pattern pVer = Pattern.compile(Pattern.quote(repo) + "/patches-(?:v)?([0-9a-zA-Z._-]+)\\.mpp");
                Matcher mVer = pVer.matcher(body);
                if (mVer.find()) {
                    String ver = mVer.group(1);
                    String changelog = "";

                    // If dual-vot patches are used, changelog points to official upstream MorpheApp/morphe-patches
                    String chRepo = repo;
                    if (repo.contains("dual-vot") || ver.toLowerCase(Locale.ROOT).contains("dualvot")) {
                        chRepo = "MorpheApp/morphe-patches";
                    }

                    Pattern pCh = Pattern.compile("https://github\\.com/" + Pattern.quote(chRepo) + "/releases/tag/[^\\s)\"<>]+");
                    Matcher mCh = pCh.matcher(body);
                    if (mCh.find()) {
                        changelog = mCh.group(0);
                    } else if (ver.toLowerCase(Locale.ROOT).contains("dualvot")) {
                        String baseVer = ver.replaceAll("-dualvot\\.[0-9a-zA-Z._-]+", "").replaceAll("^[vV]", "");
                        changelog = "https://github.com/MorpheApp/morphe-patches/releases/tag/v" + baseVer;
                    } else {
                        changelog = "https://github.com/" + chRepo + "/releases";
                    }
                    return new PatchInfo(ver, changelog);
                }
            } catch (Throwable ignored) {}
        }

        // Fallback search for any patches and changelog in release body
        String fallbackVer = "";
        String fallbackChangelog = "";
        try {
            Pattern pAnyVer = Pattern.compile("patches-(?:v)?([0-9a-zA-Z._-]+)\\.mpp");
            Matcher mAnyVer = pAnyVer.matcher(body);
            if (mAnyVer.find()) {
                fallbackVer = mAnyVer.group(1);
            }
            Pattern pAnyCh = Pattern.compile("https://github\\.com/[^\\s)\"<>]+/releases/tag/[^\\s)\"<>]+");
            Matcher mAnyCh = pAnyCh.matcher(body);
            if (mAnyCh.find()) {
                fallbackChangelog = mAnyCh.group(0);
            }
        } catch (Throwable ignored) {}

        return new PatchInfo(fallbackVer, fallbackChangelog);
    }

    private static String findMatchingUrl(Context context, java.util.List<String> urls) {
        if (urls == null || urls.isEmpty()) return null;

        boolean is64Bit = false;
        if (Build.SUPPORTED_ABIS != null) {
            for (String abi : Build.SUPPORTED_ABIS) {
                if (abi.contains("arm64")) {
                    is64Bit = true;
                    break;
                }
            }
        }

        String pkg = (context != null) ? context.getPackageName().toLowerCase(Locale.ROOT) : "";
        boolean isMusic = pkg.contains("music");
        boolean isRoot = pkg.startsWith("com.google.android");

        String allApkUrl = null;
        String archApkUrl = null;

        for (String url : urls) {
            if (url == null || url.isEmpty()) continue;
            String name = url.toLowerCase(Locale.ROOT);
            int lastSlash = name.lastIndexOf('/');
            if (lastSlash != -1) name = name.substring(lastSlash + 1);

            // If root: look for Magisk/KernelSU module .zip
            if (isRoot) {
                if (!name.endsWith(".zip") || !name.contains("module")) continue;
                if (isMusic && !name.contains("music")) continue;
                if (!isMusic && name.contains("music")) continue;
                return url;
            }

            // If non-root: look for .apk (excluding modules)
            if (!name.endsWith(".apk") || name.contains("module")) {
                continue;
            }

            if (isMusic && !name.contains("music")) continue;
            if (!isMusic && name.contains("music")) continue;

            if (name.contains("-all.apk")) {
                allApkUrl = url;
            }
            if (is64Bit && name.contains("arm64")) {
                archApkUrl = url;
            } else if (!is64Bit && (name.contains("arm-v7a") || name.contains("armeabi-v7a"))) {
                archApkUrl = url;
            }
        }

        return archApkUrl != null ? archApkUrl : allApkUrl;
    }

    private static String findMatchingAsset(Context context, JSONArray assets) {
        if (assets == null) return null;
        java.util.List<String> urls = new java.util.ArrayList<>();
        for (int i = 0; i < assets.length(); i++) {
            JSONObject asset = assets.optJSONObject(i);
            if (asset != null) {
                String u = asset.optString("browser_download_url", "");
                if (!u.isEmpty()) urls.add(u);
            }
        }
        return findMatchingUrl(context, urls);
    }

    private static String extractVersionFromUrl(String url) {
        try {
            int vIdx = url.indexOf("-v");
            if (vIdx != -1) {
                int endIdx = url.indexOf("-", vIdx + 2);
                if (endIdx != -1) {
                    return url.substring(vIdx + 2, endIdx);
                }
            }
        } catch (Exception ignored) {}
        return "";
    }

    private static int parseNumericTag(String tag) {
        try {
            String clean = tag.replaceAll("[^0-9]", "");
            return Integer.parseInt(clean);
        } catch (Exception e) {
            return 0;
        }
    }

    // --- ACCURATE THEME DETECTION (Morphe Utils, ThemeUtils, DecorView, Attributes, System) ---
    private static boolean isDarkTheme(Activity activity) {
        if (activity == null) return true;

        // 1. Morphe / ReVanced internal API via reflection
        try {
            Class<?> utilsClass = Class.forName("app.morphe.extension.shared.Utils");
            Method isDarkMethod = utilsClass.getMethod("isDarkModeEnabled");
            Object result = isDarkMethod.invoke(null);
            if (result instanceof Boolean) {
                return (Boolean) result;
            }
        } catch (Throwable ignored) {}

        // 2. ThemeUtils getDialogBackgroundColor via reflection
        try {
            Class<?> themeUtilsClass = Class.forName("app.morphe.extension.shared.theme.ThemeUtils");
            Method getBgMethod = themeUtilsClass.getMethod("getDialogBackgroundColor");
            Object result = getBgMethod.invoke(null);
            if (result instanceof Integer) {
                return isColorDark((Integer) result);
            }
        } catch (Throwable ignored) {}

        // 3. SharedPreferences of Morphe
        try {
            SharedPreferences sp = activity.getSharedPreferences(activity.getPackageName() + "_preferences", Context.MODE_PRIVATE);
            if (sp.contains("morphe_theme_last_used_dark_mode")) {
                return sp.getBoolean("morphe_theme_last_used_dark_mode", true);
            }
        } catch (Throwable ignored) {}

        // 4. Activity DecorView background color
        try {
            if (activity.getWindow() != null && activity.getWindow().getDecorView() != null) {
                Drawable decorBg = activity.getWindow().getDecorView().getBackground();
                if (decorBg instanceof ColorDrawable) {
                    int c = ((ColorDrawable) decorBg).getColor();
                    if (c != 0) return isColorDark(c);
                }
            }
        } catch (Throwable ignored) {}

        // 5. Activity Theme attributes
        try {
            TypedValue tv = new TypedValue();
            if (activity.getTheme().resolveAttribute(android.R.attr.colorBackground, tv, true)) {
                if (tv.type >= TypedValue.TYPE_FIRST_COLOR_INT && tv.type <= TypedValue.TYPE_LAST_COLOR_INT) {
                    return isColorDark(tv.data);
                }
            }
            if (activity.getTheme().resolveAttribute(android.R.attr.windowBackground, tv, true)) {
                if (tv.type >= TypedValue.TYPE_FIRST_COLOR_INT && tv.type <= TypedValue.TYPE_LAST_COLOR_INT) {
                    return isColorDark(tv.data);
                }
            }
            if (activity.getTheme().resolveAttribute(android.R.attr.isLightTheme, tv, true)) {
                return (tv.data == 0);
            }
        } catch (Throwable ignored) {}

        // 6. System Configuration uiMode
        try {
            int uiMode = activity.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
            if (uiMode == Configuration.UI_MODE_NIGHT_YES) return true;
            if (uiMode == Configuration.UI_MODE_NIGHT_NO) return false;
        } catch (Throwable ignored) {}

        return true;
    }

    private static boolean isColorDark(int color) {
        double lum = (0.299 * Color.red(color) + 0.587 * Color.green(color) + 0.114 * Color.blue(color)) / 255.0;
        return lum < 0.5;
    }

    // --- UI DIALOG (Style 1: Material 3 / Telegram Layout with Changelog & Return Labels) ---
    private static void showDialog(Activity activity, String tag, String version, String patchVersion, String downloadUrl, String changelogUrl, boolean isLatest) {
        if (activity == null || activity.isFinishing() || activity.isDestroyed()) {
            return;
        }

        try {
            Dialog dialog = new Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

            DisplayMetrics dm = activity.getResources().getDisplayMetrics();
            float density = dm.density;
            boolean dark = isDarkTheme(activity);

            // Palette Definition (Material 3 / YouTube Clean)
            int darkCardBg = Color.parseColor("#121214");
            try {
                Class<?> themeUtilsClass = Class.forName("app.morphe.extension.shared.theme.ThemeUtils");
                Method getBgMethod = themeUtilsClass.getMethod("getDialogBackgroundColor");
                int morpheBg = (Integer) getBgMethod.invoke(null);
                if (morpheBg != 0) darkCardBg = morpheBg;
            } catch (Throwable ignored) {}

            final int colCardBg = dark ? darkCardBg : Color.parseColor("#FFFFFF");
            final int colCardBorder = dark ? Color.parseColor("#242428") : Color.parseColor("#E8E8EE");
            final int colBackdrop = dark ? Color.parseColor("#90000000") : Color.parseColor("#50000000");
            final int colTitle = dark ? Color.WHITE : Color.parseColor("#0F0F12");
            final int colSubtitle = dark ? Color.parseColor("#9E9EA6") : Color.parseColor("#62626C");
            final int colHandle = dark ? Color.parseColor("#38383E") : Color.parseColor("#D4D4DC");
            
            // Container Surface
            final int colSurface = dark ? Color.parseColor("#1A1A1E") : Color.parseColor("#F6F6FA");
            final int colSurfaceBorder = dark ? Color.parseColor("#28282E") : Color.parseColor("#E6E6EE");
            final int colButtonSurface = dark ? Color.parseColor("#1E1E22") : Color.parseColor("#F4F4F8");
            final int colButtonBorder = dark ? Color.parseColor("#2E2E34") : Color.parseColor("#E4E4EC");
            final int colButtonText = dark ? Color.parseColor("#F0F0F4") : Color.parseColor("#121216");
            
            // Accents
            final int colPrimaryBtnBg = dark ? Color.parseColor("#3EA6FF") : Color.parseColor("#065FD4");
            final int colPrimaryBtnText = dark ? Color.BLACK : Color.WHITE;
            final int colAccentText = dark ? Color.parseColor("#3EA6FF") : Color.parseColor("#065FD4");
            final int colIconBg = dark ? Color.parseColor("#1C2430") : Color.parseColor("#EBF3FF");

            // Fullscreen backdrop container
            FrameLayout rootFrame = new FrameLayout(activity);
            rootFrame.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            rootFrame.setBackgroundColor(colBackdrop);
            rootFrame.setFitsSystemWindows(true);
            rootFrame.setOnClickListener(v -> dialog.dismiss());

            // Bottom sheet card layout
            LinearLayout sheet = new LinearLayout(activity);
            sheet.setOrientation(LinearLayout.VERTICAL);
            sheet.setClickable(true);

            GradientDrawable sheetBg = new GradientDrawable();
            sheetBg.setColor(colCardBg);
            sheet.setBackground(sheetBg);

            // ScrollWrapper with prevention of auto-scrolling
            MaxHeightScrollView scrollWrapper = new MaxHeightScrollView(activity);
            scrollWrapper.setDescendantFocusability(ViewGroup.FOCUS_BEFORE_DESCENDANTS);
            scrollWrapper.setFocusable(true);
            scrollWrapper.setFocusableInTouchMode(true);

            boolean isInitLandscape = dm.widthPixels > dm.heightPixels;
            int initMaxH = isInitLandscape ? Math.min(dm.heightPixels - dp(32, density), dp(310, density)) : (int) (dm.heightPixels * 0.9f);
            scrollWrapper.setMaxHeight(initMaxH);

            FrameLayout.LayoutParams initialWrapLp = new FrameLayout.LayoutParams(
                isInitLandscape ? Math.min(dm.widthPixels - dp(32, density), dp(660, density)) : ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                isInitLandscape ? Gravity.CENTER : Gravity.BOTTOM
            );
            if (isInitLandscape) {
                initialWrapLp.topMargin = dp(16, density);
                initialWrapLp.bottomMargin = dp(16, density);
            }
            scrollWrapper.setLayoutParams(initialWrapLp);
            scrollWrapper.setVerticalScrollBarEnabled(false);

            // Drag to dismiss touch listener
            View.OnTouchListener dragListener = new View.OnTouchListener() {
                private float startY;
                private float lastY;
                private boolean dragging = false;

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getActionMasked()) {
                        case MotionEvent.ACTION_DOWN:
                            startY = event.getRawY();
                            lastY = startY;
                            dragging = false;
                            return true;
                        case MotionEvent.ACTION_MOVE:
                            float rawY = event.getRawY();
                            float dy = rawY - startY;
                            if (dy > dp(6, density)) {
                                dragging = true;
                                scrollWrapper.setTranslationY(Math.max(0, dy));
                            }
                            lastY = rawY;
                            return true;
                        case MotionEvent.ACTION_UP:
                        case MotionEvent.ACTION_CANCEL:
                            if (dragging) {
                                float totalDy = lastY - startY;
                                if (totalDy > dp(80, density)) {
                                    scrollWrapper.animate()
                                        .translationY(scrollWrapper.getHeight() + dp(50, density))
                                        .setDuration(180)
                                        .withEndAction(dialog::dismiss)
                                        .start();
                                } else {
                                    scrollWrapper.animate()
                                        .translationY(0)
                                        .setDuration(180)
                                        .start();
                                }
                                dragging = false;
                                return true;
                            }
                            return false;
                    }
                    return false;
                }
            };

            // Header Container (Drag handle only for portrait bottom sheet)
            LinearLayout headerLayout = new LinearLayout(activity);
            headerLayout.setOrientation(LinearLayout.VERTICAL);
            headerLayout.setOnTouchListener(dragListener);

            View handle = new View(activity);
            LinearLayout.LayoutParams handleLp = new LinearLayout.LayoutParams(dp(36, density), dp(4, density));
            handleLp.gravity = Gravity.CENTER_HORIZONTAL;
            handle.setLayoutParams(handleLp);
            GradientDrawable handleBg = new GradientDrawable();
            handleBg.setColor(colHandle);
            handleBg.setCornerRadius(dp(2, density));
            handle.setBackground(handleBg);
            headerLayout.addView(handle);
            sheet.addView(headerLayout);

            // Columns Container (Vertical in portrait, 2-column Horizontal in landscape)
            LinearLayout colsContainer = new LinearLayout(activity);
            LinearLayout leftCol = new LinearLayout(activity);
            leftCol.setOrientation(LinearLayout.VERTICAL);
            LinearLayout rightCol = new LinearLayout(activity);
            rightCol.setOrientation(LinearLayout.VERTICAL);

            // ==================== LEFT COLUMN CONTENT ====================

            // 1. App Identity Row (Icon + Title + Subtitle)
            LinearLayout identityRow = new LinearLayout(activity);
            identityRow.setOrientation(LinearLayout.HORIZONTAL);
            identityRow.setGravity(Gravity.CENTER_VERTICAL);
            LinearLayout.LayoutParams idRowLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            idRowLp.topMargin = dp(12, density);
            identityRow.setLayoutParams(idRowLp);

            TextView iconBox = new TextView(activity);
            if (isLatest) {
                iconBox.setText("✅");
            } else {
                iconBox.setText(emoji(0x1F680));
            }
            iconBox.setTextSize(22);
            iconBox.setGravity(Gravity.CENTER);
            LinearLayout.LayoutParams iconLp = new LinearLayout.LayoutParams(dp(44, density), dp(44, density));
            iconLp.rightMargin = dp(12, density);
            iconBox.setLayoutParams(iconLp);

            final int colGreenIconBg = dark ? Color.parseColor("#152B1E") : Color.parseColor("#E6F4EA");
            GradientDrawable iconBg = new GradientDrawable();
            iconBg.setColor(isLatest ? colGreenIconBg : colIconBg);
            iconBg.setCornerRadius(dp(14, density));
            iconBox.setBackground(iconBg);
            identityRow.addView(iconBox);

            LinearLayout textBlock = new LinearLayout(activity);
            textBlock.setOrientation(LinearLayout.VERTICAL);

            TextView titleView = new TextView(activity);
            titleView.setText(isLatest ? getString("title_latest") : getString("title"));
            titleView.setTextColor(colTitle);
            titleView.setTypeface(Typeface.DEFAULT_BOLD);
            titleView.setTextSize(18);
            textBlock.addView(titleView);

            TextView subView = new TextView(activity);
            String verText = version.isEmpty() ? "v" + tag : "v" + version;
            subView.setText(getAppDisplayName(activity) + " • " + verText);
            subView.setTextColor(colSubtitle);
            subView.setTextSize(12);
            textBlock.addView(subView);

            identityRow.addView(textBlock);
            leftCol.addView(identityRow);

            // 2. Info Container Card (Patch version, Build number, Changelog link)
            LinearLayout infoCard = new LinearLayout(activity);
            infoCard.setOrientation(LinearLayout.VERTICAL);
            infoCard.setPadding(dp(14, density), dp(10, density), dp(14, density), dp(10, density));

            GradientDrawable infoCardBg = new GradientDrawable();
            infoCardBg.setColor(colSurface);
            infoCardBg.setCornerRadius(dp(16, density));
            infoCardBg.setStroke(1, colSurfaceBorder);
            infoCard.setBackground(infoCardBg);

            LinearLayout.LayoutParams infoCardLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            infoCardLp.topMargin = dp(10, density);
            infoCard.setLayoutParams(infoCardLp);

            if (!patchVersion.isEmpty()) {
                LinearLayout patchRow = new LinearLayout(activity);
                patchRow.setOrientation(LinearLayout.HORIZONTAL);

                TextView patchLbl = new TextView(activity);
                patchLbl.setText(getString("info_patch_label"));
                patchLbl.setTextColor(colSubtitle);
                patchLbl.setTextSize(12);
                patchLbl.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f));
                patchRow.addView(patchLbl);

                TextView patchVal = new TextView(activity);
                patchVal.setText(patchVersion);
                patchVal.setTextColor(colTitle);
                patchVal.setTypeface(Typeface.DEFAULT_BOLD);
                patchVal.setTextSize(12);
                patchRow.addView(patchVal);

                infoCard.addView(patchRow);
            }

            LinearLayout buildRow = new LinearLayout(activity);
            buildRow.setOrientation(LinearLayout.HORIZONTAL);
            LinearLayout.LayoutParams buildRowLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            buildRowLp.topMargin = dp(4, density);
            buildRow.setLayoutParams(buildRowLp);

            TextView buildLbl = new TextView(activity);
            buildLbl.setText(getString("info_build_label"));
            buildLbl.setTextColor(colSubtitle);
            buildLbl.setTextSize(12);
            buildLbl.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f));
            buildRow.addView(buildLbl);

            TextView buildVal = new TextView(activity);
            buildVal.setText(String.format(getString("subtitle_fmt"), tag));
            buildVal.setTextColor(colTitle);
            buildVal.setTypeface(Typeface.DEFAULT_BOLD);
            buildVal.setTextSize(12);
            buildRow.addView(buildVal);

            infoCard.addView(buildRow);

            View cardDivider = new View(activity);
            cardDivider.setBackgroundColor(colSurfaceBorder);
            LinearLayout.LayoutParams cDivLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp(1, density));
            cDivLp.topMargin = dp(6, density);
            cDivLp.bottomMargin = dp(6, density);
            cardDivider.setLayoutParams(cDivLp);
            infoCard.addView(cardDivider);

            TextView changelogBtn = new TextView(activity);
            changelogBtn.setText(getString("info_changelog_btn"));
            changelogBtn.setTextColor(colAccentText);
            changelogBtn.setTextSize(12);
            changelogBtn.setTypeface(Typeface.DEFAULT_BOLD);
            changelogBtn.setOnClickListener(v -> {
                openUrl(activity, changelogUrl);
            });
            infoCard.addView(changelogBtn);
            leftCol.addView(infoCard);

            // 3. Primary Download Button (APK for NonRoot, Magisk Module for Root)
            String pkgName = activity.getPackageName().toLowerCase(Locale.ROOT);
            boolean isRootApp = pkgName.startsWith("com.google.android");
            String dlBtnText = isRootApp ? getString("download_btn_root") : getString("download_btn");
            TextView downloadBtn = createButton(activity, dlBtnText, colPrimaryBtnBg, colPrimaryBtnText, density, 14);
            LinearLayout.LayoutParams dlLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp(42, density));
            dlLp.topMargin = dp(10, density);
            downloadBtn.setLayoutParams(dlLp);
            downloadBtn.setOnClickListener(v -> {
                dialog.dismiss();
                openUrl(activity, downloadUrl);
            });
            leftCol.addView(downloadBtn);

            // ==================== RIGHT COLUMN CONTENT ====================

            // 4. Obtainium Section
            TextView obtainiumLabel = new TextView(activity);
            obtainiumLabel.setText(getString("obtainium_label"));
            obtainiumLabel.setTextColor(colSubtitle);
            obtainiumLabel.setTextSize(11);
            obtainiumLabel.setTypeface(Typeface.DEFAULT_BOLD);
            LinearLayout.LayoutParams obLblLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            obLblLp.topMargin = dp(12, density);
            obtainiumLabel.setLayoutParams(obLblLp);
            rightCol.addView(obtainiumLabel);

            LinearLayout obtainiumRow = new LinearLayout(activity);
            obtainiumRow.setOrientation(LinearLayout.HORIZONTAL);
            LinearLayout.LayoutParams obRowLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            obRowLp.topMargin = dp(6, density);
            obtainiumRow.setLayoutParams(obRowLp);

            TextView openObtainiumBtn = createSubButton(activity, getString("obtainium_open"), colButtonSurface, colButtonBorder, colButtonText, density);
            LinearLayout.LayoutParams openLp = new LinearLayout.LayoutParams(0, dp(38, density), 1.0f);
            openLp.rightMargin = dp(6, density);
            openObtainiumBtn.setLayoutParams(openLp);
            openObtainiumBtn.setOnClickListener(v -> {
                launchObtainium(activity);
            });
            obtainiumRow.addView(openObtainiumBtn);

            TextView importBtn = createSubButton(activity, getString("obtainium_import"), colButtonSurface, colButtonBorder, colAccentText, density);
            LinearLayout.LayoutParams importLp = new LinearLayout.LayoutParams(0, dp(38, density), 1.0f);
            importBtn.setLayoutParams(importLp);
            importBtn.setOnClickListener(v -> {
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(getObtainiumDeepLink(activity)));
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    activity.startActivity(intent);
                } catch (ActivityNotFoundException ex) {
                    openUrl(activity, OBTAINIUM_DOWNLOAD_URL);
                    showToast(activity, getString("toast_install_obtainium"));
                }
            });
            obtainiumRow.addView(importBtn);
            rightCol.addView(obtainiumRow);

            // 5. Snooze Section Header with Reset Button
            LinearLayout snoozeHeader = new LinearLayout(activity);
            snoozeHeader.setOrientation(LinearLayout.HORIZONTAL);
            snoozeHeader.setGravity(Gravity.CENTER_VERTICAL);
            LinearLayout.LayoutParams snoozeHeaderLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            snoozeHeaderLp.topMargin = dp(12, density);
            snoozeHeader.setLayoutParams(snoozeHeaderLp);

            SharedPreferences prefs = activity.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            long curSnooze = prefs.getLong(KEY_SNOOZE_UNTIL, 0L);
            String curSkip = prefs.getString(KEY_SKIPPED_TAG, "");
            long now = System.currentTimeMillis();
            boolean isSnoozed = (now < curSnooze);
            boolean isSkipped = !curSkip.isEmpty();
            boolean hasActivePause = isSnoozed || isSkipped;

            String headerText;
            if (curSnooze == Long.MAX_VALUE) {
                headerText = getString("status_snoozed_forever");
            } else if (isSnoozed) {
                long diff = curSnooze - now;
                int leftDays = (int) (diff / (24 * 60 * 60 * 1000L));
                int leftHours = (int) ((diff % (24 * 60 * 60 * 1000L)) / (60 * 60 * 1000L));
                if (leftDays > 0) {
                    headerText = String.format(getString("status_snoozed_days_fmt"), leftDays);
                } else {
                    headerText = String.format(getString("status_snoozed_hours_fmt"), Math.max(1, leftHours));
                }
            } else if (isSkipped) {
                headerText = String.format(getString("status_skipped_fmt"), curSkip);
            } else {
                headerText = getString("snooze_label");
            }

            TextView snoozeLabel = new TextView(activity);
            snoozeLabel.setText(headerText);
            snoozeLabel.setTextColor(hasActivePause ? colAccentText : colSubtitle);
            snoozeLabel.setTextSize(11);
            snoozeLabel.setTypeface(Typeface.DEFAULT_BOLD);
            snoozeLabel.setSingleLine(true);
            LinearLayout.LayoutParams sLblLp = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f);
            snoozeLabel.setLayoutParams(sLblLp);
            snoozeHeader.addView(snoozeLabel);

            TextView resetBtn = new TextView(activity);
            resetBtn.setText(getString("btn_reset_snooze"));
            resetBtn.setTextColor(colAccentText);
            resetBtn.setTextSize(11);
            resetBtn.setTypeface(Typeface.DEFAULT_BOLD);
            resetBtn.setPadding(dp(4, density), 0, dp(4, density), 0);
            resetBtn.setVisibility(hasActivePause ? View.VISIBLE : View.GONE);
            resetBtn.setOnClickListener(v -> {
                prefs.edit()
                    .remove(KEY_SNOOZE_UNTIL)
                    .remove(KEY_SNOOZED_TAG)
                    .remove(KEY_SKIPPED_TAG)
                    .apply();
                snoozeLabel.setText(getString("snooze_label"));
                snoozeLabel.setTextColor(colSubtitle);
                resetBtn.setVisibility(View.GONE);
                showToast(activity, getString("toast_snooze_reset"));
            });
            snoozeHeader.addView(resetBtn);
            rightCol.addView(snoozeHeader);

            LinearLayout chipsRow = new LinearLayout(activity);
            chipsRow.setOrientation(LinearLayout.HORIZONTAL);
            LinearLayout.LayoutParams scrollLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp(34, density));
            scrollLp.topMargin = dp(6, density);
            chipsRow.setLayoutParams(scrollLp);

            int[] days = {1, 3, 7, 14, 30, -1};
            for (int i = 0; i < days.length; i++) {
                int d = days[i];
                String label;
                if (d == -1) {
                    label = getString("chip_forever");
                } else if (d == 30) {
                    label = getString("chip_1mo");
                } else {
                    label = d + " " + getString("chip_day");
                }
                TextView chip = createChip(activity, label, colSurface, colSurfaceBorder, colSubtitle, density);
                chip.setGravity(Gravity.CENTER);
                chip.setPadding(0, 0, 0, 0);
                chip.setSingleLine(true);
                chip.setTextSize(d == -1 ? 9.5f : 11f);

                LinearLayout.LayoutParams chipLp = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1.0f);
                if (i > 0) {
                    chipLp.leftMargin = dp(4, density);
                }
                chip.setLayoutParams(chipLp);

                chip.setOnClickListener(v -> {
                    snooze(activity, tag, d);
                    dialog.dismiss();
                    if (d == -1) {
                        showToast(activity, getString("toast_snoozed_forever"));
                    } else {
                        showToast(activity, String.format(getString("toast_snoozed"), label));
                    }
                });
                chipsRow.addView(chip);
            }
            rightCol.addView(chipsRow);

            // 6. Dismiss / Skip Button
            TextView skipBtn = new TextView(activity);
            skipBtn.setText(isLatest ? getString("btn_close") : getString("skip_btn"));
            skipBtn.setTextColor(colSubtitle);
            skipBtn.setTextSize(12);
            skipBtn.setGravity(Gravity.CENTER);
            LinearLayout.LayoutParams skipLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            skipLp.topMargin = dp(10, density);
            skipBtn.setLayoutParams(skipLp);
            skipBtn.setPadding(0, dp(4, density), 0, dp(4, density));
            skipBtn.setOnClickListener(v -> {
                if (isLatest) {
                    dialog.dismiss();
                } else {
                    prefs.edit().putString(KEY_SKIPPED_TAG, tag).apply();
                    dialog.dismiss();
                    showToast(activity, String.format(getString("toast_skipped"), tag));
                }
            });
            rightCol.addView(skipBtn);

            // 7. Shortcut Hint Badge
            TextView hintView = new TextView(activity);
            hintView.setText(String.format(getString("hint_shortcut_fmt"), getSettingsBrandName(activity)));
            hintView.setTextColor(dark ? Color.parseColor("#C8C8D0") : Color.parseColor("#2E2E36"));
            hintView.setTextSize(11f);
            hintView.setGravity(Gravity.CENTER);

            GradientDrawable hintBg = new GradientDrawable();
            hintBg.setColor(colSurface);
            hintBg.setCornerRadius(dp(12, density));
            hintBg.setStroke(1, colSurfaceBorder);
            hintView.setBackground(hintBg);
            hintView.setPadding(dp(10, density), dp(6, density), dp(10, density), dp(6, density));

            LinearLayout.LayoutParams hintLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            hintLp.topMargin = dp(8, density);
            hintView.setLayoutParams(hintLp);
            rightCol.addView(hintView);

            colsContainer.addView(leftCol);
            colsContainer.addView(rightCol);
            sheet.addView(colsContainer);

            // Initial Layout Configuration (Portrait vs Landscape)
            // isInitLandscape already defined above
            if (isInitLandscape) {
                handle.setVisibility(View.GONE);
                sheetBg.setCornerRadius(dp(20, density));
                sheet.setPadding(dp(20, density), dp(16, density), dp(20, density), dp(16, density));

                colsContainer.setOrientation(LinearLayout.HORIZONTAL);
                LinearLayout.LayoutParams leftLp = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f);
                leftCol.setLayoutParams(leftLp);
                LinearLayout.LayoutParams rightLp = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f);
                rightLp.leftMargin = dp(16, density);
                rightCol.setLayoutParams(rightLp);

                idRowLp.topMargin = 0;
                obLblLp.topMargin = 0;
            } else {
                handle.setVisibility(View.VISIBLE);
                float r = dp(24, density);
                sheetBg.setCornerRadii(new float[]{r, r, r, r, 0, 0, 0, 0});
                sheet.setPadding(dp(20, density), dp(10, density), dp(20, density), dp(24, density));

                colsContainer.setOrientation(LinearLayout.VERTICAL);
                leftCol.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                rightCol.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            }

            scrollWrapper.addView(sheet);
            rootFrame.addView(scrollWrapper);
            dialog.setContentView(rootFrame);

            // Dynamic Layout Updater for Orientation Changes
            rootFrame.addOnLayoutChangeListener((v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom) -> {
                int totalWidth = right - left;
                int totalHeight = bottom - top;
                if (totalWidth <= 0 || totalHeight <= 0) return;

                boolean isLandscape = totalWidth > totalHeight;

                if (isLandscape) {
                    int cardWidth = Math.min(totalWidth - dp(32, density), dp(660, density));
                    int maxCardHeight = Math.min(totalHeight - dp(32, density), dp(310, density));
                    scrollWrapper.setMaxHeight(maxCardHeight);

                    FrameLayout.LayoutParams wrapLp = new FrameLayout.LayoutParams(
                        cardWidth,
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        Gravity.CENTER
                    );
                    wrapLp.topMargin = dp(16, density);
                    wrapLp.bottomMargin = dp(16, density);
                    scrollWrapper.setLayoutParams(wrapLp);

                    handle.setVisibility(View.GONE);
                    sheetBg.setCornerRadius(dp(20, density));
                    sheet.setPadding(dp(20, density), dp(16, density), dp(20, density), dp(16, density));

                    colsContainer.setOrientation(LinearLayout.HORIZONTAL);
                    LinearLayout.LayoutParams leftLp = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f);
                    leftCol.setLayoutParams(leftLp);
                    LinearLayout.LayoutParams rightLp = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f);
                    rightLp.leftMargin = dp(16, density);
                    rightCol.setLayoutParams(rightLp);

                    idRowLp.topMargin = 0;
                    infoCardLp.topMargin = dp(8, density);
                    dlLp.topMargin = dp(8, density);
                    obLblLp.topMargin = 0;
                    obRowLp.topMargin = dp(4, density);
                    snoozeHeaderLp.topMargin = dp(8, density);
                    scrollLp.topMargin = dp(4, density);
                    skipLp.topMargin = dp(8, density);
                    hintLp.topMargin = dp(4, density);

                    scrollWrapper.post(() -> scrollWrapper.scrollTo(0, 0));
                } else {
                    int maxCardHeight = (int) (totalHeight * 0.9f);
                    scrollWrapper.setMaxHeight(maxCardHeight);

                    FrameLayout.LayoutParams wrapLp = new FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        Gravity.BOTTOM
                    );
                    scrollWrapper.setLayoutParams(wrapLp);

                    handle.setVisibility(View.VISIBLE);
                    float r = dp(24, density);
                    sheetBg.setCornerRadii(new float[]{r, r, r, r, 0, 0, 0, 0});
                    sheet.setPadding(dp(20, density), dp(10, density), dp(20, density), dp(24, density));
                    handleLp.bottomMargin = dp(10, density);

                    colsContainer.setOrientation(LinearLayout.VERTICAL);
                    LinearLayout.LayoutParams leftLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    leftCol.setLayoutParams(leftLp);
                    LinearLayout.LayoutParams rightLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    rightLp.leftMargin = 0;
                    rightCol.setLayoutParams(rightLp);

                    idRowLp.topMargin = dp(12, density);
                    infoCardLp.topMargin = dp(12, density);
                    dlLp.topMargin = dp(12, density);
                    obLblLp.topMargin = dp(12, density);
                    obRowLp.topMargin = dp(6, density);
                    snoozeHeaderLp.topMargin = dp(12, density);
                    scrollLp.topMargin = dp(6, density);
                    skipLp.topMargin = dp(10, density);
                    hintLp.topMargin = dp(6, density);
                }
            });

            Window window = dialog.getWindow();
            if (window != null) {
                window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            }
            dialog.show();
        } catch (Throwable t) {
            Log.e(TAG, "Error displaying update dialog", t);
        }
    }

    private static void launchObtainium(Context context) {
        try {
            Intent launchIntent = context.getPackageManager().getLaunchIntentForPackage("dev.imranr.obtainium");
            if (launchIntent == null) {
                launchIntent = context.getPackageManager().getLaunchIntentForPackage("dev.imranr.obtainium.fdroid");
            }
            if (launchIntent != null) {
                launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(launchIntent);
            } else {
                openUrl(context, OBTAINIUM_DOWNLOAD_URL);
                showToast(context, getString("toast_install_obtainium"));
            }
        } catch (Exception e) {
            Log.e(TAG, "Failed to launch Obtainium", e);
            openUrl(context, OBTAINIUM_DOWNLOAD_URL);
        }
    }

    private static void snooze(Context context, String tag, int days) {
        long snoozeTime = (days == -1) ? Long.MAX_VALUE : (System.currentTimeMillis() + (days * 86_400_000L));
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit()
            .putLong(KEY_SNOOZE_UNTIL, snoozeTime)
            .putString(KEY_SNOOZED_TAG, tag)
            .apply();
    }

    private static void openUrl(Context context, String url) {
        try {
            Uri uri = Uri.parse(url);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            intent.addCategory(Intent.CATEGORY_BROWSABLE);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://"));
            PackageManager pm = context.getPackageManager();
            ResolveInfo resolveInfo = pm.resolveActivity(browserIntent, PackageManager.MATCH_DEFAULT_ONLY);

            if (resolveInfo != null && resolveInfo.activityInfo != null) {
                String browserPkg = resolveInfo.activityInfo.packageName;
                if (browserPkg != null && !browserPkg.equals(context.getPackageName()) && !browserPkg.contains("youtube")) {
                    intent.setPackage(browserPkg);
                }
            }

            context.startActivity(intent);
        } catch (ActivityNotFoundException ex) {
            try {
                Intent fallback = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                fallback.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(fallback);
            } catch (Exception ignored) {}
        } catch (Exception e) {
            Log.e(TAG, "Failed to open url: " + url, e);
        }
    }

    private static void showToast(Context context, String msg) {
        try {
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
        } catch (Exception ignored) {}
    }

    private static int dp(float dp, float density) {
        return (int) (dp * density + 0.5f);
    }

    private static TextView createButton(Context context, String text, int bgColor, int textColor, float density, int textSize) {
        TextView tv = new TextView(context);
        tv.setText(text);
        tv.setTextColor(textColor);
        tv.setTextSize(textSize);
        tv.setTypeface(Typeface.DEFAULT_BOLD);
        tv.setGravity(Gravity.CENTER);

        GradientDrawable gd = new GradientDrawable();
        gd.setColor(bgColor);
        gd.setCornerRadius(dp(14, density));
        tv.setBackground(gd);
        return tv;
    }

    private static TextView createChip(Context context, String text, int bgColor, int borderColor, int textColor, float density) {
        TextView tv = new TextView(context);
        tv.setText(text);
        tv.setTextColor(textColor);
        tv.setTextSize(12);
        tv.setGravity(Gravity.CENTER);
        tv.setPadding(dp(12, density), dp(6, density), dp(12, density), dp(6, density));

        GradientDrawable gd = new GradientDrawable();
        gd.setColor(bgColor);
        gd.setCornerRadius(dp(10, density));
        gd.setStroke(1, borderColor);
        tv.setBackground(gd);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.rightMargin = dp(6, density);
        tv.setLayoutParams(lp);
        return tv;
    }

    private static TextView createSubButton(Context context, String text, int bgColor, int borderColor, int textColor, float density) {
        TextView tv = new TextView(context);
        tv.setText(text);
        tv.setTextColor(textColor);
        tv.setTextSize(11);
        tv.setTypeface(Typeface.DEFAULT_BOLD);
        tv.setGravity(Gravity.CENTER);
        tv.setSingleLine(true);
        tv.setPadding(dp(4, density), dp(6, density), dp(4, density), dp(6, density));

        GradientDrawable gd = new GradientDrawable();
        gd.setColor(bgColor);
        gd.setCornerRadius(dp(12, density));
        gd.setStroke(1, borderColor);
        tv.setBackground(gd);
        return tv;
    }

    private static String emoji(int codePoint) {
        return new String(Character.toChars(codePoint));
    }

    // --- MULTILINGUAL DICTIONARY ---
    private static String getString(String key) {
        String lang = Locale.getDefault().getLanguage().toLowerCase(Locale.ROOT);

        // Ukrainian
        if (lang.equals("uk") || lang.equals("ua")) {
            switch (key) {
                case "title": return "Доступне оновлення";
                case "title_latest": return "У вас остання версія";
                case "subtitle_fmt": return "Збірка %s";
                case "info_patch_label": return "Версія патчів:";
                case "info_build_label": return "Номер збірки:";
                case "info_changelog_btn": return "🐙  Список змін на GitHub  ↗";
                case "download_btn": return "⬇️  Завантажити APK";
                case "download_btn_root": return "⬇️  Завантажити модуль (Root)";
                case "obtainium_label": return "ОНОВЛЕННЯ ЧЕРЕЗ OBTAINIUM:";
                case "obtainium_open": return emoji(0x1F4F1) + "  Відкрити Obtainium";
                case "obtainium_import": return "➕  Імпорт профілю";
                case "snooze_label": return "🔕  ВИМКНУТИ СПОВІЩЕННЯ НА:";
                case "chip_day": return "дн";
                case "chip_1mo": return "1 міс";
                case "chip_forever": return "Назавжди";
                case "btn_reset_snooze": return "↺  Скинути";
                case "btn_close": return "Закрити";
                case "status_snoozed_forever": return "🔕  Вимкнено назавжди";
                case "status_snoozed_days_fmt": return "🔕  Пауза: ще %d дн.";
                case "status_snoozed_hours_fmt": return "🔕  Пауза: ще %d год.";
                case "status_skipped_fmt": return "⚠️  Збірку %s пропущено";
                case "skip_btn": return "Пропустити цю версію";
                case "toast_snoozed": return "Сповіщення вимкнено на %s";
                case "toast_snoozed_forever": return "Сповіщення вимкнено назавжди";
                case "toast_snooze_reset": return "Паузу скинуто. Сповіщення увімкнено";
                case "toast_skipped": return "Збірку %s пропущено";
                case "toast_install_obtainium": return "Встановіть Obtainium для автооновлень";
                case "update_settings_title": return "Оновлення патчів";
                case "update_settings_summary": return "Перевірити наявність нових збірок";
                case "shortcut_label": return "Оновити патчі";
                case "shortcut_long_label": return "🔄  Оновити патчі";
                case "toast_checking_updates": return "Перевірка оновлень патчів...";
                case "toast_already_latest": return "У вас встановлені найновіші патчі";
                case "toast_check_failed": return "Не вдалося перевірити оновлення. Перевірте мережу";
                case "hint_shortcut_fmt": return "💡 Перевірка: Налаштування -> %s -> Оновлення патчів";
            }
        }
        // Russian, Belarusian, Kazakh
        else if (lang.equals("ru") || lang.equals("be") || lang.equals("kk")) {
            switch (key) {
                case "title": return "Доступно обновление";
                case "title_latest": return "У вас последняя версия";
                case "subtitle_fmt": return "Сборка %s";
                case "info_patch_label": return "Версия патчей:";
                case "info_build_label": return "Номер сборки:";
                case "info_changelog_btn": return "🐙  Список изменений на GitHub  ↗";
                case "download_btn": return "⬇️  Скачать APK";
                case "download_btn_root": return "⬇️  Скачать модуль (Root)";
                case "obtainium_label": return "ОБНОВЛЕНИЕ ЧЕРЕЗ OBTAINIUM:";
                case "obtainium_open": return emoji(0x1F4F1) + "  Открыть Obtainium";
                case "obtainium_import": return "➕  Импорт профиля";
                case "snooze_label": return "🔕  ОТКЛЮЧИТЬ УВЕДОМЛЕНИЯ НА:";
                case "chip_day": return "дн";
                case "chip_1mo": return "1 мес";
                case "chip_forever": return "Навсегда";
                case "btn_reset_snooze": return "↺  Сбросить";
                case "btn_close": return "Закрыть";
                case "status_snoozed_forever": return "🔕  Отключено навсегда";
                case "status_snoozed_days_fmt": return "🔕  Пауза: ещё %d дн.";
                case "status_snoozed_hours_fmt": return "🔕  Пауза: ещё %d ч.";
                case "status_skipped_fmt": return "⚠️  Сборка %s пропущена";
                case "skip_btn": return "Пропустить эту версию";
                case "toast_snoozed": return "Уведомления отключены на %s";
                case "toast_snoozed_forever": return "Уведомления отключены навсегда";
                case "toast_snooze_reset": return "Пауза сброшена. Уведомления включены";
                case "toast_skipped": return "Билд %s пропущен";
                case "toast_install_obtainium": return "Установите Obtainium для автообновлений";
                case "update_settings_title": return "Обновление патчей";
                case "update_settings_summary": return "Проверить наличие новой версии сборок";
                case "shortcut_label": return "Обновить патчи";
                case "shortcut_long_label": return "🔄  Обновить патчи";
                case "toast_checking_updates": return "Проверка обновлений патчей...";
                case "toast_already_latest": return "У вас установлены актуальные патчи";
                case "toast_check_failed": return "Не удалось проверить обновления. Проверьте сеть";
                case "hint_shortcut_fmt": return "💡 Проверка: Настройки -> %s -> Обновление патчей";
            }
        } 
        // Spanish
        else if (lang.equals("es")) {
            switch (key) {
                case "title": return "Actualización disponible";
                case "title_latest": return "Tienes la última versión";
                case "subtitle_fmt": return "Versión %s";
                case "info_patch_label": return "Versión de parches:";
                case "info_build_label": return "Número de build:";
                case "info_changelog_btn": return "🐙  Registro de cambios en GitHub  ↗";
                case "download_btn": return "⬇️  Descargar APK";
                case "download_btn_root": return "⬇️  Descargar módulo (Root)";
                case "obtainium_label": return "ACTUALIZACIÓN VÍA OBTAINIUM:";
                case "obtainium_open": return emoji(0x1F4F1) + "  Abrir Obtainium";
                case "obtainium_import": return "➕  Importar perfil";
                case "snooze_label": return "🔕  PAUSAR NOTIFICACIONES:";
                case "chip_day": return "d";
                case "chip_1mo": return "1 mes";
                case "chip_forever": return "Siempre";
                case "btn_reset_snooze": return "↺  Restablecer";
                case "btn_close": return "Cerrar";
                case "status_snoozed_forever": return "🔕  Desactivado permanentemente";
                case "status_snoozed_days_fmt": return "🔕  Pausa: quedan %d d";
                case "status_snoozed_hours_fmt": return "🔕  Pausa: quedan %d h";
                case "status_skipped_fmt": return "⚠️  Versión %s omitida";
                case "skip_btn": return "Omitir esta versión";
                case "toast_snoozed": return "Notificaciones pausadas por %s";
                case "toast_snoozed_forever": return "Notificaciones desactivadas permanentemente";
                case "toast_snooze_reset": return "Pausa restablecida. Notificaciones activadas";
                case "toast_skipped": return "Versión %s omitida";
                case "toast_install_obtainium": return "Instala Obtainium para actualizaciones";
                case "update_settings_title": return "Actualización de parches";
                case "update_settings_summary": return "Buscar nuevas compilaciones";
                case "shortcut_label": return "Actualizar parches";
                case "shortcut_long_label": return "🔄  Actualizar parches";
                case "toast_checking_updates": return "Buscando actualizaciones de parches...";
                case "toast_already_latest": return "Tienes instalados los parches más recientes";
                case "toast_check_failed": return "Error al buscar actualizaciones. Comprueba la red";
                case "hint_shortcut_fmt": return "💡 Comprobar: Ajustes -> %s -> Actualización de parches";
            }
        } 
        // German
        else if (lang.equals("de")) {
            switch (key) {
                case "title": return "Update verfügbar";
                case "title_latest": return "Sie haben die neueste Version";
                case "subtitle_fmt": return "Build %s";
                case "info_patch_label": return "Patch-Version:";
                case "info_build_label": return "Build-Nummer:";
                case "info_changelog_btn": return "🐙  Changelog auf GitHub  ↗";
                case "download_btn": return "⬇️  APK herunterladen";
                case "download_btn_root": return "⬇️  Modul herunterladen (Root)";
                case "obtainium_label": return "AKTUALISIERUNG ÜBER OBTAINIUM:";
                case "obtainium_open": return emoji(0x1F4F1) + "  Obtainium öffnen";
                case "obtainium_import": return "➕  Profil importieren";
                case "snooze_label": return "🔕  BENACHRICHTIGUNGEN PAUSIEREN:";
                case "chip_day": return "T";
                case "chip_1mo": return "1 Monat";
                case "chip_forever": return "Immer";
                case "btn_reset_snooze": return "↺  Zurücksetzen";
                case "btn_close": return "Schließen";
                case "status_snoozed_forever": return "🔕  Dauerhaft deaktiviert";
                case "status_snoozed_days_fmt": return "🔕  Pausiert: noch %d T";
                case "status_snoozed_hours_fmt": return "🔕  Pausiert: noch %d Std";
                case "status_skipped_fmt": return "⚠️  Build %s übersprungen";
                case "skip_btn": return "Diese Version überspringen";
                case "toast_snoozed": return "Benachrichtigungen pausiert für %s";
                case "toast_snoozed_forever": return "Benachrichtigungen dauerhaft deaktiviert";
                case "toast_snooze_reset": return "Pause zurückgesetzt. Benachrichtigungen aktiviert";
                case "toast_skipped": return "Build %s übersprungen";
                case "toast_install_obtainium": return "Installiere Obtainium für Updates";
                case "update_settings_title": return "Patch-Updates";
                case "update_settings_summary": return "Nach neuen Builds suchen";
                case "shortcut_label": return "Patches aktualisieren";
                case "shortcut_long_label": return "🔄  Patches aktualisieren";
                case "toast_checking_updates": return "Suche nach Patch-Updates...";
                case "toast_already_latest": return "Sie haben die neuesten Patches installiert";
                case "toast_check_failed": return "Fehler bei der Update-Suche. Netzwerk prüfen";
                case "hint_shortcut_fmt": return "💡 Prüfung: Einstellungen -> %s -> Patch-Updates";
            }
        }

        // English default
        switch (key) {
            case "title": return "Update Available";
            case "title_latest": return "You have the latest version";
            case "subtitle_fmt": return "Build %s";
            case "info_patch_label": return "Patches version:";
            case "info_build_label": return "Build number:";
            case "info_changelog_btn": return "🐙  Changelog on GitHub  ↗";
            case "download_btn": return "⬇️  Download APK";
            case "download_btn_root": return "⬇️  Download Module (Root)";
            case "obtainium_label": return "UPDATE VIA OBTAINIUM:";
            case "obtainium_open": return emoji(0x1F4F1) + "  Open Obtainium";
            case "obtainium_import": return "➕  Import Profile";
            case "snooze_label": return "🔕  PAUSE NOTIFICATIONS FOR:";
            case "chip_day": return "d";
            case "chip_1mo": return "1 mo";
            case "chip_forever": return "Forever";
            case "btn_reset_snooze": return "↺  Reset";
            case "btn_close": return "Close";
            case "status_snoozed_forever": return "🔕  Disabled permanently";
            case "status_snoozed_days_fmt": return "🔕  Paused: %d d left";
            case "status_snoozed_hours_fmt": return "🔕  Paused: %d h left";
            case "status_skipped_fmt": return "⚠️  Build %s skipped";
            case "skip_btn": return "Skip this version";
            case "toast_snoozed": return "Notifications paused for %s";
            case "toast_snoozed_forever": return "Notifications disabled permanently";
            case "toast_snooze_reset": return "Pause reset. Notifications enabled";
            case "toast_skipped": return "Build %s skipped";
            case "toast_install_obtainium": return "Install Obtainium for auto-updates";
            case "update_settings_title": return "Patch updates";
            case "update_settings_summary": return "Check for new builds";
            case "shortcut_label": return "Update Patches";
            case "shortcut_long_label": return "🔄  Update Patches";
            case "toast_checking_updates": return "Checking for patch updates...";
            case "toast_already_latest": return "You have the latest patches installed";
            case "toast_check_failed": return "Failed to check for updates. Check your network";
            case "hint_shortcut_fmt": return "💡 Check: Settings -> %s -> Patch updates";
            default: return key;
        }
     }

    private static class MaxHeightScrollView extends ScrollView {
        private int maxHeight = -1;

        public MaxHeightScrollView(Context context) {
            super(context);
        }

        public void setMaxHeight(int max) {
            this.maxHeight = max;
            requestLayout();
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            if (maxHeight > 0) {
                int size = MeasureSpec.getSize(heightMeasureSpec);
                int mode = MeasureSpec.getMode(heightMeasureSpec);
                int target = (mode == MeasureSpec.UNSPECIFIED) ? maxHeight : Math.min(size, maxHeight);
                heightMeasureSpec = MeasureSpec.makeMeasureSpec(target, MeasureSpec.AT_MOST);
            }
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }
}