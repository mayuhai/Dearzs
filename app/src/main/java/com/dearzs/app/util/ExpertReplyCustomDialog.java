package com.dearzs.app.util;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dearzs.app.R;
import com.dearzs.app.entity.EntityOrderInfo;

public class ExpertReplyCustomDialog extends Dialog {

    public ExpertReplyCustomDialog(Context context) {
        super(context);
    }

    public ExpertReplyCustomDialog(Context context, int theme) {
        super(context, theme);
    }

    public static class Builder {
        private Context context;
        private String title;
        private String message;
        private String positiveButtonText;
        private String negativeButtonText;
        private View contentView;
        private EditText editText;
        private NumberPicker hourPicker;
        private NumberPicker minutePicker;
        private OnClickListener positiveButtonClickListener;
        private OnClickListener negativeButtonClickListener;

        public Builder(Context context) {
            this.context = context;
        }

        public Builder setMessage(String message) {
            this.message = message;
            return this;
        }

        /**
         * Set the Dialog message from resource
         *
         * @param message
         * @return
         */
        public Builder setMessage(int message) {
            this.message = (String) context.getText(message);
            return this;
        }

        /**
         * Set the Dialog title from resource
         *
         * @param title
         * @return
         */
        public Builder setTitle(int title) {
            this.title = (String) context.getText(title);
            return this;
        }

        /**
         * Set the Dialog title from String
         *
         * @param title
         * @return
         */

        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        public Builder setContentView(View v) {
            this.contentView = v;
            return this;
        }

        public String getMessage() {
            return message;
        }

        public EditText getEditText() {
            return editText;
        }

        public NumberPicker getHourPicker() {
            return hourPicker;
        }

        public NumberPicker getMinutePicker() {
            return minutePicker;
        }

        public void setPickerTimeShow(int time) {
            if (time == EntityOrderInfo.AM) {
                hourPicker.setMaxValue(12);
                hourPicker.setMinValue(8);
                hourPicker.setValue(8);
            }else if (time == EntityOrderInfo.PM){
                hourPicker.setMaxValue(19);
                hourPicker.setMinValue(13);
                hourPicker.setValue(13);
            }else if (time == EntityOrderInfo.NIGHT){
                hourPicker.setMaxValue(23);
                hourPicker.setMinValue(20);
                hourPicker.setValue(20);
            }else if (time == EntityOrderInfo.ALLDAY){
                hourPicker.setMaxValue(23);
                hourPicker.setMinValue(0);
                hourPicker.setValue(8);
            }
        }

        /**
         * Set the positive button resource and it's listener
         *
         * @param positiveButtonText
         * @return
         */
        public Builder setPositiveButton(int positiveButtonText,
                                         OnClickListener listener) {
            this.positiveButtonText = (String) context.getText(positiveButtonText);
            this.positiveButtonClickListener = listener;
            return this;
        }

        public Builder setPositiveButton(String positiveButtonText, OnClickListener listener) {
            this.positiveButtonText = positiveButtonText;
            this.positiveButtonClickListener = listener;
            return this;
        }

        public Builder setNegativeButton(int negativeButtonText,
                                         OnClickListener listener) {
            this.negativeButtonText = (String) context.getText(negativeButtonText);
            this.negativeButtonClickListener = listener;
            return this;
        }

        public Builder setNegativeButton(String negativeButtonText,
                                         OnClickListener listener) {
            this.negativeButtonText = negativeButtonText;
            this.negativeButtonClickListener = listener;
            return this;
        }

        @SuppressLint("WrongViewCast")
        public ExpertReplyCustomDialog create() {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            // instantiate the dialog with the custom Theme
            final ExpertReplyCustomDialog dialog = new ExpertReplyCustomDialog(context, R.style.Dialog);
            View layout = inflater.inflate(R.layout.dialog_custom_layout, null);
            dialog.addContentView(layout, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
            // set the dialog title
            ((TextView) layout.findViewById(R.id.title)).setText(title);
            editText = (EditText) layout.findViewById(R.id.edt_message);
            hourPicker = (NumberPicker) layout.findViewById(R.id.hour);
            minutePicker = (NumberPicker) layout.findViewById(R.id.minute);

//            hourPicker.setMaxValue(23);
//            hourPicker.setMinValue(0);
//            hourPicker.setValue(12);
            hourPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                @Override
                public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
//			hourPicker = hourPicker.getValue();
                    updateDateTimeLinkStartTime();

                }
            });

            minutePicker.setMaxValue(59);
            minutePicker.setMinValue(0);
            minutePicker.setValue(0);
            minutePicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                @Override
                public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
//			minuteSpinner = minuteSpinner.getValue();
                    updateDateTimeLinkStartTime();
                }
            });

            // set the confirm button
            if (positiveButtonText != null) {
                ((Button) layout.findViewById(R.id.positiveButton)).setText(positiveButtonText);
                if (positiveButtonClickListener != null) {
                    ((Button) layout.findViewById(R.id.positiveButton)).setOnClickListener(new View.OnClickListener() {
                                public void onClick(View v) {
                                    positiveButtonClickListener.onClick(dialog,DialogInterface.BUTTON_POSITIVE);
                                }
                            });
                }
            } else {
                // if no confirm button just set the visibility to GONE
                layout.findViewById(R.id.positiveButton).setVisibility(
                        View.GONE);
            }
            // set the cancel button
            if (negativeButtonText != null) {
                ((Button) layout.findViewById(R.id.negativeButton)).setText(negativeButtonText);
                if (negativeButtonClickListener != null) {
                    ((Button) layout.findViewById(R.id.negativeButton))
                            .setOnClickListener(new View.OnClickListener() {
                                public void onClick(View v) {negativeButtonClickListener.onClick(dialog,DialogInterface.BUTTON_NEGATIVE);
                                }
                            });
                }
            } else {
                // if no confirm button just set the visibility to GONE
                layout.findViewById(R.id.negativeButton).setVisibility(View.GONE);
            }
            // set the content message
            if (message != null) {
                ((TextView) layout.findViewById(R.id.message)).setText(message);
            } else if (contentView != null) {
                // if no message set
                // add the contentView to the dialog body
                ((RelativeLayout) layout.findViewById(R.id.content)).removeAllViews();
                ((RelativeLayout) layout.findViewById(R.id.content)).addView(
                        contentView, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
            }
            dialog.setContentView(layout);
            return dialog;
        }

    }

    public static void updateDateTimeLinkStartTime() {
//		mLinkStartTime.setText(mHourLinkStart + ":" + mMinuteLinkStart);

    }
}
