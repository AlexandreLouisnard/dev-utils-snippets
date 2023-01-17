package com.portalp.technician.ui.utils;

import static android.content.DialogInterface.BUTTON_NEGATIVE;
import static android.content.DialogInterface.BUTTON_NEUTRAL;
import static android.content.DialogInterface.BUTTON_POSITIVE;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.portalp.technician.R;
import com.portalp.utils.AndroidUtils;
import com.portalp.utils.JavaUtils;

/**
 * A custom {@link DialogFragment} to show alert, popups, etc..
 */
public class AlertDialogFragment extends DialogFragment implements DialogInterface.OnClickListener {

    public static final String TAG = AlertDialogFragment.class.getSimpleName();


    //region (dec) Constants
    // Dialog creation arguments
    private static final String ARG_TITLE_RES_ID = "ARG_TITLE_RES_ID";
    private static final String ARG_TITLE = "ARG_TITLE";
    private static final String ARG_MESSAGE_RES_ID = "ARG_MESSAGE_RES_ID";
    private static final String ARG_MESSAGE = "ARG_MESSAGE";
    private static final String ARG_MESSAGE_IS_HTML = "ARG_MESSAGE_IS_HTML";
    private static final String ARG_BUTTON_1_POSITIVE_RES_ID = "ARG_BUTTON_1_POSITIVE_RES_ID";
    private static final String ARG_BUTTON_2_NEGATIVE_RES_ID = "ARG_BUTTON_2_NEGATIVE_RES_ID";
    private static final String ARG_BUTTON_3_NEUTRAL_RES_ID = "ARG_BUTTON_3_NEUTRAL_RES_ID";
    private static final String ARG_QRCODE_CONTENT = "ARG_QRCODE_CONTENT";

    // Dialog result
    public enum Button {
        BUTTON_ANDROID_BACK,
        BUTTON_1_OK,
        BUTTON_2_CANCEL,
        BUTTON_3_NEUTRAL
    }

    // Other constants
    private static final int QRCODE_SIZE = 512;
    //endregion

    private AlertDialogFragmentListener listener;
    private int requestCode;

    public interface AlertDialogFragmentListener {
        /**
         * Callback method invoked when the user has clicked a button on the dialog, therefore closing it.
         *
         * @param requestCode   the request code specified through {@link #setListener(AlertDialogFragmentListener, int)}
         * @param clickedButton the clicked {@link Button}
         * @param data          extra data
         */
        void onAlertDialogResult(int requestCode, Button clickedButton, Bundle data);
    }

    /**
     * Returns an {@link AlertDialogFragment}.
     *
     * @param titleResId          title res id of the dialog, or 0
     * @param title               title of the dialog, or null
     * @param messageResId        message res id of the dialog, or 0
     * @param message             message of the dialog, or null
     * @param messageIsHtml       <b>true</b> if the message is HTML
     * @param button1okResId      button 1 (OK / positive) res id
     * @param button2cancelResId  button 2 (Cancel / negative) res id
     * @param button3neutralResId button 3 (neutral) res id
     * @param qrCodeContent       the QRCode content to display in an {@link android.widget.ImageView}.
     * @return
     */
    private static AlertDialogFragment newInstance(int titleResId, String title, int messageResId, String message, boolean messageIsHtml, int button1okResId, int button2cancelResId, int button3neutralResId, String qrCodeContent) {
        AlertDialogFragment alertDialogFragment = new AlertDialogFragment();

        // Set arguments
        Bundle args = new Bundle();
        if (titleResId != 0) {
            args.putInt(ARG_TITLE_RES_ID, titleResId);
        }
        if (title != null) {
            args.putString(ARG_TITLE, title);
        }
        if (messageResId != 0) {
            args.putInt(ARG_MESSAGE_RES_ID, messageResId);
        }
        if (message != null) {
            args.putString(ARG_MESSAGE, message);
        }
        args.putBoolean(ARG_MESSAGE_IS_HTML, messageIsHtml);
        if (button1okResId != 0) {
            args.putInt(ARG_BUTTON_1_POSITIVE_RES_ID, button1okResId);
        }
        if (button2cancelResId != 0) {
            args.putInt(ARG_BUTTON_2_NEGATIVE_RES_ID, button2cancelResId);
        }
        if (button3neutralResId != 0) {
            args.putInt(ARG_BUTTON_3_NEUTRAL_RES_ID, button3neutralResId);
        }
        if (qrCodeContent != null) {
            args.putString(ARG_QRCODE_CONTENT, qrCodeContent);
        }
        alertDialogFragment.setArguments(args);

        return alertDialogFragment;
    }

    public static AlertDialogFragment newInstance(int titleResId, String title, int messageResId, String message, boolean messageIsHtml, int button1okResId, int button2cancelResId, int button3neutralResId) {
        return newInstance(titleResId, title, messageResId, message, messageIsHtml, button1okResId, button2cancelResId, button3neutralResId, null);
    }

    public static AlertDialogFragment newInstance(int titleResId, int messageResId, boolean messageIsHtml, int button1okResId, int button2cancelResId, int button3neutralResId) {
        return newInstance(titleResId, null, messageResId, null, messageIsHtml, button1okResId, button2cancelResId, button3neutralResId);
    }

    public static AlertDialogFragment newInstance(String title, String message, boolean messageIsHtml, int button1okResId, int button2cancelResId) {
        return newInstance(0, title, 0, message, messageIsHtml, button1okResId, button2cancelResId, 0);
    }

    public static AlertDialogFragment newInstance(int titleResId, int messageResId, int button1okResId, int button2cancelResId) {
        return newInstance(titleResId, messageResId, false, button1okResId, button2cancelResId, 0);
    }

    public static AlertDialogFragment newInstance(String title, String message, boolean messageIsHtml) {
        return newInstance(title, message, messageIsHtml, android.R.string.ok, 0);
    }

    public static AlertDialogFragment newInstance(int titleResId, int messageResId) {
        return newInstance(titleResId, messageResId, android.R.string.ok, 0);
    }

    /**
     * Returns an {@link AlertDialogFragment} with a QRCode in an {@link android.widget.ImageView}.
     *
     * @param title              the string of the title
     * @param message            the string of the message
     * @param button1okResId     resId of the button 1 (ok / positive) text in string.xml
     * @param button2cancelResId resId of the button 2 (cancel / negative) text in string.xml
     * @param qrCodeContent      the QRCode content to display in an {@link android.widget.ImageView}.
     * @return AlertDialogFragment to display
     */
    public static AlertDialogFragment newQrCodeInstance(String title, String message, int button1okResId, int button2cancelResId, String qrCodeContent) {
        return newInstance(0, title, 0, message, false, button1okResId, button2cancelResId, 0, qrCodeContent);
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        if (getContext() == null) {
            throw new NullPointerException("OnCreateDialog() failed because of null Context");
        }
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        setStyle(DialogFragment.STYLE_NORMAL, 0);

        final Bundle args = getArguments();

        if (args != null && args.containsKey(ARG_QRCODE_CONTENT)) {
            String qrCodeContent = args.getString(ARG_QRCODE_CONTENT);
            LayoutInflater factory = LayoutInflater.from(getContext());
            @SuppressLint("InflateParams") final View view = factory.inflate(R.layout.dialog_qrcode, null);
            ImageView imageView = view.findViewById(R.id.image_view_info_fr);
            imageView.setImageBitmap(JavaUtils.encodeAsQRCode(qrCodeContent, QRCODE_SIZE));
            builder.setView(view);
        }

        if (args != null) {
            if (args.containsKey(ARG_TITLE_RES_ID)) {
                builder.setTitle(args.getInt(ARG_TITLE_RES_ID));
            } else if (args.containsKey(ARG_TITLE)) {
                builder.setTitle(args.getString(ARG_TITLE));
            }

            String message = null;
            if (args.containsKey(ARG_MESSAGE_RES_ID)) {
                message = getResources().getText(args.getInt(ARG_MESSAGE_RES_ID)).toString();
            } else if (args.containsKey(ARG_MESSAGE)) {
                message = args.getString(ARG_MESSAGE);
            }
            if (message != null) {
                if (args.containsKey((ARG_MESSAGE_IS_HTML))) {
                    builder.setMessage(Html.fromHtml(message));
                } else {
                    builder.setMessage(message);
                }
            }

            if (args.containsKey(ARG_BUTTON_1_POSITIVE_RES_ID)) {
                builder.setPositiveButton(args.getInt(ARG_BUTTON_1_POSITIVE_RES_ID), this);
            }

            if (args.containsKey(ARG_BUTTON_2_NEGATIVE_RES_ID)) {
                builder.setNegativeButton(args.getInt(ARG_BUTTON_2_NEGATIVE_RES_ID), this);
            }

            if (args.containsKey(ARG_BUTTON_3_NEUTRAL_RES_ID)) {
                builder.setNeutralButton(args.getInt(ARG_BUTTON_3_NEUTRAL_RES_ID), this);
            }
        }

        AlertDialog dialog = builder.create();

        // Custom dialog buttons color (otherwise they are the same as the normal buttons, though not visible with this theme)
        dialog.setOnShowListener(arg0 -> {
            int color = getContext() != null ? getContext().getResources().getColor(R.color.colorAccent) : 0;
            if (dialog.getButton(BUTTON_POSITIVE) != null) {
                dialog.getButton(BUTTON_POSITIVE).setTextColor(color);
            }
            if (dialog.getButton(BUTTON_NEGATIVE) != null) {
                dialog.getButton(BUTTON_NEGATIVE).setTextColor(color);
            }
            if (dialog.getButton(BUTTON_NEUTRAL) != null) {
                dialog.getButton(BUTTON_NEUTRAL).setTextColor(color);
            }
        });

        // Prevent back key
        dialog.setOnKeyListener((dial, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
                if (listener != null) {
                    listener.onAlertDialogResult(requestCode, Button.BUTTON_ANDROID_BACK, null);
                }
                return true;
            }
            return false;
        });

        dialog.setCanceledOnTouchOutside(false);

        return dialog;
    }

    /**
     * Sets the {@link AlertDialogFragmentListener} for this {@link AlertDialogFragment}.
     *
     * @param listener    listener of the dialog return
     * @param requestCode code requested
     */
    public void setListener(AlertDialogFragmentListener listener, int requestCode) {
        this.listener = listener;
        this.requestCode = requestCode;
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        if (listener != null) {
            switch (which) {
                case BUTTON_POSITIVE:
                    listener.onAlertDialogResult(requestCode, Button.BUTTON_1_OK, null);
                    break;
                case BUTTON_NEGATIVE:
                    listener.onAlertDialogResult(requestCode, Button.BUTTON_2_CANCEL, null);
                    break;
                case BUTTON_NEUTRAL:
                    listener.onAlertDialogResult(requestCode, Button.BUTTON_3_NEUTRAL, null);
                    break;
            }
        }
    }

    @Override
    public void show(@NonNull FragmentManager manager, String tag) {
        // Do not show AlertDialogs during Espresso TEST execution as they get stuck until the user clicks OK
        if (AndroidUtils.isRunningEspressoTest()) {
            if (listener != null) {
                listener.onAlertDialogResult(requestCode, Button.BUTTON_1_OK, null);
            }
            return;
        }

        try {
            super.show(manager, tag);
        } catch (IllegalStateException | NullPointerException e) {
            Log.w(TAG, "show(): error:");
            e.printStackTrace();
        }
    }
}
