package cn.nobita.updatelibrary;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

public class NoticeAlertDialog extends Dialog {
    public interface Callback {
        void callbackSure();

        void callbackCancel();
    }

    Callback callback;
    boolean isForcedUpdate;
    private TextView content;
    private TextView sureBtn;
    private TextView cancelBtn;
    private View dividingLine;

    public NoticeAlertDialog(Context context, boolean isForcedUpdate, Callback callback) {
        super(context, R.style.CustomDialog);
        this.isForcedUpdate = isForcedUpdate;
        this.callback = callback;
        setCustomDialog();
    }

    private void setCustomDialog() {
        View mView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_notice, null);
        sureBtn = (TextView) mView.findViewById(R.id.dialog_confirm_sure);
        cancelBtn = (TextView) mView.findViewById(R.id.dialog_confirm_cancel);
        dividingLine = (View) mView.findViewById(R.id.dialog_dividing_line);
        content = (TextView) mView.findViewById(R.id.dialog_confirm_title);


        sureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.callbackSure();
                NoticeAlertDialog.this.cancel();
            }
        });
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.callbackCancel();
                NoticeAlertDialog.this.cancel();
            }
        });

        if (isForcedUpdate) {
            dividingLine.setVisibility(View.GONE);
            cancelBtn.setVisibility(View.GONE);
        } else {
            dividingLine.setVisibility(View.VISIBLE);
            cancelBtn.setVisibility(View.VISIBLE);
        }

        super.setContentView(mView);
    }


    public NoticeAlertDialog setContent(String s) {
        content.setText(s);
        return this;
    }

    public NoticeAlertDialog showView() {
        this.show();
        return this;
    }
}
