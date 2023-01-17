package com.portalp.utils;

import static android.graphics.Color.BLACK;
import static android.graphics.Color.WHITE;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Looper;
import android.os.PowerManager;
import android.provider.Settings;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.crashlytics.buildtools.reloc.javax.annotation.Nonnull;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * Helper class offering static util methods.
 */
public class AndroidUtils {

    private static final String TAG = AndroidUtils.class.getSimpleName();

    //region Android
    public static Activity getActivity(Context context) {
        while (context instanceof ContextWrapper) {
            if (context instanceof Activity) {
                return (Activity) context;
            }
            context = ((ContextWrapper) context).getBaseContext();
        }
        return null;
    }

    /**
     * Indicates whether the user has granted the given permissions to the application.
     *
     * @param context     the {@link Context}
     * @param permissions the {@link String}[] permissions
     * @return <b>true</b> if the user has granted all the required permissions<br/>
     * <b>false</b> if at least one permission is missing
     */
    public static boolean hasPermissions(Context context, String[] permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Returns true if the device is in Doze/Idle mode. Should be called before checking the network connection because
     * the ConnectionManager may report the device is connected when it isn't during Idle mode.
     */
    @TargetApi(23)
    public static boolean isDozing(Context context) {
        if (Build.VERSION.SDK_INT >= 23) {
            PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            return powerManager.isDeviceIdleMode() &&
                    !powerManager.isIgnoringBatteryOptimizations(context.getPackageName());
        } else {
            return false;
        }
    }

    public static boolean isUiThread() {
        return Looper.getMainLooper().getThread() == Thread.currentThread();
    }


    @SuppressLint("HardwareIds")
    public static String getSmartphoneIdentifier(Context context) {
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    public static void longLogD(String tag, String log) {
        int maxLogSize = 1000;
        for (int i = 0; i <= log.length() / maxLogSize; i++) {
            int start = i * maxLogSize;
            int end = (i + 1) * maxLogSize;
            end = end > log.length() ? log.length() : end;
            Log.d(tag, log.substring(start, end));
        }
    }

    /**
     * Ignore SSL errors when using Web Services. Dangerous, only use for debug.
     */
    public static void trustAllSSLCerts() {
        try {
            TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        public X509Certificate[] getAcceptedIssuers() {
                            X509Certificate[] myTrustedAnchors = new X509Certificate[0];
                            return myTrustedAnchors;
                        }

                        @Override
                        public void checkClientTrusted(X509Certificate[] certs, String authType) {
                        }

                        @Override
                        public void checkServerTrusted(X509Certificate[] certs, String authType) {
                        }
                    }
            };

            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier((arg0, arg1) -> true);
        } catch (Exception e) {
        }
    }
    //endregion

    //region Android UI

    /**
     * @return "[package]:id/[xml-id]"
     * where [package] is your package and [xml-id] is id of view
     * or "no-id" if there is no id
     */
    public static String getId(View view) {
        if (view.getId() == View.NO_ID) return "no-id";
        else return view.getResources().getResourceName(view.getId());
    }

    public static int getNavBarHeight(Context context) {
        int height = 0;
        Resources resources = context.getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) {
            height = resources.getDimensionPixelSize(resourceId);
        }
        return height;
    }

    public static int getStatusBarHeight(Context context) {
        Resources resources = context.getResources();
        int resourceId = resources.getIdentifier("status_bar_height", "dimen", "android");
        return (resourceId > 0) ? resources.getDimensionPixelSize(resourceId) : 0;
    }

    public static int getNavigationBarHeight(Context context) {
        Resources resources = context.getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        return (resourceId > 0) ? resources.getDimensionPixelSize(resourceId) : 0;
    }

    /**
     * Hides the Android soft keyboard.
     *
     * @param activity the current {@link Activity}
     */
    public static void hideKeyboard(Activity activity) {
        if (activity == null) {
            return;
        }
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        // Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        // If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public static void releaseFocus(View view) {
        ViewParent parent = view.getParent();
        ViewGroup group = null;
        View child = null;
        while (parent != null) {
            if (parent instanceof ViewGroup) {
                group = (ViewGroup) parent;
                for (int i = 0; i < group.getChildCount(); i++) {
                    child = group.getChildAt(i);
                    if (child != view && child.isFocusable())
                        child.requestFocus();
                }
            }
            parent = parent.getParent();
        }
    }

    @NonNull
    public static Point getDisplayDimensions(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();

        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        int screenWidth = metrics.widthPixels;
        int screenHeight = metrics.heightPixels;

        // find out if status bar has already been subtracted from screenHeight
        display.getRealMetrics(metrics);
        int physicalHeight = metrics.heightPixels;
        int statusBarHeight = getStatusBarHeight(context);
        int navigationBarHeight = getNavigationBarHeight(context);
        int heightDelta = physicalHeight - screenHeight;
        if (heightDelta == 0 || heightDelta == navigationBarHeight) {
            screenHeight -= statusBarHeight;
        }

        return new Point(screenWidth, screenHeight);
    }

    /**
     * Converts dp to px dimensions.
     *
     * @param dp int DensityPixel
     * @return int Pixel
     */
    public static int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    /**
     * Converts px to dp dimensions.
     *
     * @param px int Pixel
     * @return int DensityPixel
     */
    public static int pxToDp(int px) {
        return (int) (px / Resources.getSystem().getDisplayMetrics().density);
    }

    public static Drawable getDrawable(Context context, int drawableResId) {
        return context.getDrawable(drawableResId);
    }

    /**
     * Mutates and applies a filter that converts the given drawable to a Gray
     * image. This method may be used to simulate the color of disable icons in
     * Honeycomb's ActionBar.
     *
     * @return a mutated version of the given drawable with a color filter applied.
     */
    public static Drawable convertDrawableToGrayScale(Drawable drawable) {
        if (drawable == null)
            return null;

        Drawable res = drawable.mutate();
        res.setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_IN);
        return res;
    }

    public static void setTextViewDrawableColor(TextView textView, int color) {
        for (Drawable drawable : textView.getCompoundDrawables()) {
            if (drawable != null) {
                drawable.setColorFilter(new PorterDuffColorFilter(ContextCompat.getColor(textView.getContext(), color), PorterDuff.Mode.SRC_IN));
            }
        }
    }

    public static void clearTextViewDrawableColor(TextView textView) {
        for (Drawable drawable : textView.getCompoundDrawables()) {
            if (drawable != null) {
                drawable.clearColorFilter();
            }
        }
    }
    //endregion

    //region Android Files & storage
    public static final String DIRECTORY_TEMP = "/temp"; // must be the same in res/xml/provider_paths.xml
    public static final String DIRECTORY_LOGS = "/logs"; // must be the same in res/xml/provider_paths.xml
    public static final String FORBIDDEN_CHARS_IN_FILENAME = "?:\"*|/\\<>";

    public static InputFilter filterFileNameForbiddenChars = new InputFilter() {
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            if (source.length() < 1) {
                return null;
            }
            char last = source.charAt(source.length() - 1);
            if (FORBIDDEN_CHARS_IN_FILENAME.indexOf(last) > -1) {
                return source.subSequence(0, source.length() - 1);
            }
            return null;
        }
    };

    // Checks if a volume containing external storage is available
    // for read and write.
    private static boolean isExternalStorageWritable() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    // Checks if a volume containing external storage is available to at least read.
    private static boolean isExternalStorageReadable() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED) ||
                Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED_READ_ONLY);
    }

    public static String computeFileNameWithExt(String fileName, @Nullable String extension) {
        String fileNameWithExt = fileName;
        if (extension != null && !extension.isEmpty()) {
            if (!extension.startsWith(".")) {
                extension = "." + extension;
            }
            if (!fileName.toLowerCase(Locale.ROOT).endsWith(extension.toLowerCase())) {
                fileNameWithExt += extension;
            }
        }
        return fileNameWithExt;
    }

    /**
     * Writes a file into the app-specific directory.
     *
     * @param context
     * @param isExternal <b>true</b> if other apps with Manifest.permission.READ_EXTERNAL_STORAGE or Manifest.permission.WRITE_EXTERNAL_STORAGE may read/write it.<br />
     *                   <b>false</b> if the file must be 100% private to this app
     * @param dirName
     * @param fileName   with or without the extension
     * @param extension  with or without the dot, example "admp" or ".admp"
     * @param append
     * @param data
     * @return
     */
    private static File writeFile(Context context, boolean isExternal, String dirName, String fileName, @Nullable String extension, boolean append, String data) {
        final String fileNameWithExt = computeFileNameWithExt(fileName, extension);
        try {
            final File rootAppDir = isExternal ? context.getExternalFilesDir(null) : context.getFilesDir();
            final File dir = new File(rootAppDir, dirName);
            dir.mkdirs();
            final File file = new File(dir, fileNameWithExt);
            final FileWriter fileWriter = new FileWriter(file, append);
            fileWriter.write(data);
            fileWriter.close();
            return file;
        } catch (NullPointerException | IOException e) {
            Log.e(TAG, "writeFile() failed (isExternal=" + isExternal + "): " + e.toString());
            return null;
        }
    }

    public static File writeExternalFile(Context context, String dirName, String fileName, @Nullable String extension, boolean append, String data) {
        return writeFile(context, true, dirName, fileName, extension, append, data);
    }

    public static void logToExtFile(Context context, String filename, String tag, String message) {
        final Date date = Calendar.getInstance().getTime();
        final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        final String strDate = dateFormat.format(date);

        Log.d(TAG, "logToExtFile(): " + tag + ": " + message);
        writeExternalFile(context, DIRECTORY_LOGS, filename, ".log", true, strDate + " " + tag + ": " + message + "\n");
    }

    public static File writeInternalFile(Context context, String dirName, String fileName, @Nullable String extension, boolean append, String data) {
        return writeFile(context, false, dirName, fileName, extension, append, data);
    }

    public static String readFile(Context context, boolean isExternal, String dirName, String fileName, @Nullable String extension) {
        final File rootAppDir = isExternal ? context.getExternalFilesDir(null) : context.getFilesDir();
        final String fileNameWithExt = computeFileNameWithExt(fileName, extension);
        final File dir = new File(rootAppDir, dirName);
        final File file = new File(dir, fileNameWithExt);
        return readFile(file);
    }

    public static String readFile(File file) {
        StringBuilder text = new StringBuilder();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = reader.readLine()) != null) {
                text.append(line + '\n');
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return text.toString();
    }

    public static String readFile(Context context, Uri uri) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        try (InputStream inputStream = context.getContentResolver().openInputStream(uri);
             BufferedReader reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(inputStream)))) {
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }
        }
        return stringBuilder.toString();
    }

    public static File[] getFilesList(Context context, boolean isExternal, String directory) {
        final File rootAppDir = isExternal ? context.getExternalFilesDir(null) : context.getFilesDir();
//        File dir0 = Environment.getExternalStorageDirectory(); // => /storage/sdcard0
//        File dir1 = Environment.getDataDirectory(); // => /data
//        File dir2 = Environment.getRootDirectory(); // => /system
//        File dir3 = Environment.getStorageDirectory(); // =>
//        File dir4 = context.getExternalFilesDir(null); // => /storage/sdcard0/Android/data/package/files : browsable
//        File dir5 = context.getFilesDir(); // => /data/data/package/files : not accessible with anything else than the app itself
        final File dir = new File(rootAppDir, directory);
        dir.mkdirs();
        File[] files = dir.listFiles();
        Arrays.sort(files);
        Log.d(TAG, "getFilesList(): " + files.length + " files");
        return files;
    }

    public static void deleteFile(Context context, File file) {

    }
    //endregion

    //region Android Tests
    private static AtomicBoolean mIsRunningTest;

    /**
     * Indicates whether the current RUN is an Espresso test.
     *
     * @return <b>true</b> if it is an Espresso test
     */
    public static synchronized boolean isRunningEspressoTest() {
        if (null == mIsRunningTest) {
            boolean istest;

            try {
                Class.forName("android.support.test.espresso.Espresso");
                istest = true;
            } catch (ClassNotFoundException e) {
                istest = false;
            }

            mIsRunningTest = new AtomicBoolean(istest);
        }

        return mIsRunningTest.get();
    }
    //endregion
}
