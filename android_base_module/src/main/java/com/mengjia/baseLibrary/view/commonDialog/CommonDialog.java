package com.mengjia.baseLibrary.view.commonDialog;

/**
 * Created by Android Studio.
 * User: SnapeYang
 * Date: 2020/10/15
 * Time: 17:41
 */

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.mengjia.baseLibrary.R;
import com.mengjia.baseLibrary.utils.AppUtils;


/**
 * description:自定义dialog
 */

public class CommonDialog extends Dialog {
    private Activity context;
    /**
     * 显示的图片
     */
    protected ImageView imageIv;

    /**
     * 显示的标题
     */
    protected TextView titleTv;

    /**
     * 显示的消息
     */
    protected TextView messageTv;

    /**
     * 副文
     */
    protected TextView tvSubtextMessage;

    /**
     * 确认和取消按钮
     */
    protected TextView negativeBn, positiveBn;

    /**
     * 关闭按钮
     */
    protected Button closeBtn;

    /**
     * 按钮之间的分割线
     */
    protected View columnLineView;
    /**
     * 自定义内容区域
     */
    protected ViewGroup contentLayout;
    /**
     * 自定义view
     */
    protected View customizeView;

    public CommonDialog(Activity context) {
        super(context, R.style.CustomDialog);
        this.context = context;
        setOwnerActivity(context);
    }

    /**
     * 都是内容数据
     */
    protected CharSequence message;
    protected String subtextMessage;
    protected String title;
    protected String positive, negative;
    protected int imageResId = -1;

    /**
     * 底部是否只有一个按钮
     */
    protected boolean isSingle = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //按空白处不能取消动画
        setCanceledOnTouchOutside(false);
        //初始化界面控件
        initView();
        //全屏展示Dialog
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        //初始化界面数据
        refreshView();
        //初始化界面控件的事件
        initEvent();
    }

    /**
     * 初始化界面的确定和取消监听器
     */
    protected void initEvent() {
        //设置确定按钮被点击后，向外界提供监听
        positiveBn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onClickBottomListener != null) {
                    onClickBottomListener.onPositiveClick(CommonDialog.this);
                }
                if (positiveClickListener != null) {
                    positiveClickListener.onClick(CommonDialog.this);
                }
            }
        });
        //设置取消按钮被点击后，向外界提供监听
        negativeBn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onClickBottomListener != null) {
                    onClickBottomListener.onNegativeClick(CommonDialog.this);
                }
                if (negativeClickListener != null) {
                    negativeClickListener.onClick(CommonDialog.this);
                }
            }
        });
    }

    /**
     * 初始化界面控件的显示数据
     */
    protected void refreshView() {
        //如果用户自定了title和message
        if (!TextUtils.isEmpty(title)) {
            titleTv.setText(title);
            titleTv.setVisibility(View.VISIBLE);
        } else {
            titleTv.setVisibility(View.GONE);
        }
        if (!TextUtils.isEmpty(message)) {
            messageTv.setText(message);
        }
        if (!TextUtils.isEmpty(subtextMessage)) {
            tvSubtextMessage.setText(subtextMessage);
        }
        //如果设置按钮的文字
        if (!TextUtils.isEmpty(positive)) {
            positiveBn.setText(positive);
        } else {
            positiveBn.setText("确定");
        }
        if (!TextUtils.isEmpty(negative)) {
            negativeBn.setText(negative);
        } else {
            negativeBn.setText("取消");
        }

        if (imageResId != -1) {
            imageIv.setImageResource(imageResId);
            imageIv.setVisibility(View.VISIBLE);
        } else {
            imageIv.setVisibility(View.GONE);
        }
        /**
         * 只显示一个按钮的时候隐藏取消按钮，回掉只执行确定的事件
         */
        if (isSingle) {
            columnLineView.setVisibility(View.GONE);
            negativeBn.setVisibility(View.GONE);
        } else {
            negativeBn.setVisibility(View.VISIBLE);
            columnLineView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void show() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
        super.show();
        AppUtils.fullScreenImmersive(getWindow());
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
        refreshView();
    }

    @Override
    public void dismiss() {
        super.dismiss();
    }

    /**
     * 初始化界面控件
     */
    protected void initView() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.common_dialog_layout);
        negativeBn = (Button) findViewById(R.id.negative);
        if (TextUtils.isEmpty(negative)) {
            negativeBn.setVisibility(View.GONE);
        } else {
            negativeBn.setVisibility(View.VISIBLE);
        }
        positiveBn = (Button) findViewById(R.id.positive);
        if (TextUtils.isEmpty(positive)) {
            positiveBn.setVisibility(View.GONE);
        } else {
            positiveBn.setVisibility(View.VISIBLE);
        }
        titleTv = (TextView) findViewById(R.id.title);
        if (TextUtils.isEmpty(title)) {
            titleTv.setVisibility(View.GONE);
        } else {
            titleTv.setVisibility(View.VISIBLE);
        }
        messageTv = (TextView) findViewById(R.id.message);
        if (TextUtils.isEmpty(message)) {
            messageTv.setVisibility(View.GONE);
        } else {
            messageTv.setVisibility(View.VISIBLE);
        }
        imageIv = (ImageView) findViewById(R.id.image);
        if (imageResId == 0) {
            messageTv.setVisibility(View.GONE);
        } else {
            messageTv.setVisibility(View.VISIBLE);
        }

        columnLineView = findViewById(R.id.column_line);
        contentLayout = findViewById(R.id.content_layout);
        if (customizeView != null) {
            contentLayout.addView(customizeView);
        }
    }

    /**
     * 设置确定取消按钮的回调
     */
    public OnClickBottomListener onClickBottomListener;

    protected OnCommonClickListener positiveClickListener;
    protected OnCommonClickListener negativeClickListener;

    public OnCommonClickListener getPositiveClickListener() {
        return positiveClickListener;
    }

    public CommonDialog setPositiveClickListener(OnCommonClickListener positiveClickListener) {
        this.positiveClickListener = positiveClickListener;
        return this;
    }

    public OnCommonClickListener getNegativeClickListener() {
        return negativeClickListener;
    }

    public CommonDialog setNegativeClickListener(OnCommonClickListener negativeClickListener) {
        this.negativeClickListener = negativeClickListener;
        return this;
    }

    public CommonDialog setOnClickBottomListener(OnClickBottomListener onClickBottomListener) {
        this.onClickBottomListener = onClickBottomListener;
        return this;
    }

    public interface OnCommonClickListener {
        /**
         * 点击事件
         *
         * @param commonDialog
         */
        void onClick(CommonDialog commonDialog);
    }

    public interface OnClickBottomListener {

        /**
         * 点击取消按钮事件
         */
        void onNegativeClick(CommonDialog commonDialog);

        /**
         * 点击确定按钮事件
         */
        void onPositiveClick(CommonDialog commonDialog);
    }

    public CharSequence getMessage() {
        return message;
    }

    public CommonDialog setMessage(CharSequence message) {
        this.message = message;
        return this;
    }

    public String getSubtextMessage() {
        return subtextMessage;
    }

    public CommonDialog setSubtextMessage(String subtextMessage) {
        this.subtextMessage = subtextMessage;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public CommonDialog setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getPositive() {
        return positive;
    }

    public CommonDialog setPositive(String positive) {
        this.positive = positive;
        return this;
    }

    public View getCustomizeView() {
        return customizeView;
    }

    public CommonDialog setCustomizeView(View customizeView) {
        this.customizeView = customizeView;
        return this;
    }

    public String getNegative() {
        return negative;
    }

    public CommonDialog setNegative(String negative) {
        this.negative = negative;
        return this;
    }

    public int getImageResId() {
        return imageResId;
    }

    public boolean isSingle() {
        return isSingle;
    }

    public CommonDialog setSingle(boolean single) {
        isSingle = single;
        return this;
    }

    public CommonDialog setImageResId(int imageResId) {
        this.imageResId = imageResId;
        return this;
    }

}

