package com.olc.printchecker;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import hardware.print.BarcodeUtil;
import hardware.print.printer;

/************************************************************
 * Copyright 2000-2066 Olc Corp., Ltd.
 * All rights reserved.
 * Description     : The Main activity for  PrintChecker
 * History        :( ID, Date, Author, Description)
 * v1.0, 2017/3/2,  zhangyong, create
 ************************************************************/

public class ScanPrintFragment extends Fragment implements RadioGroup.OnCheckedChangeListener,View.OnClickListener{
    private static String TAG = "scan_print";
    private View mView;
    private ImageView mOneBarCodeImageView;
    private ImageView mTwoBarCodeImageView;
    private TextView mTextView;
    private EditText mContentEditText;
    private Button mClearBtn;
    private LinearLayout mAdvanceCommonLyt;
    private Spinner mLabelHeightSpn;
    private Spinner mLabelGaySpn;
    private EditText mLabelPagesEdt;
    private CheckBox mAdvanceCheckBox;
    private Button mPrintBtn;
    private Button mFeedBtn;
    private LinearLayout mAdvanceLayout;
    private LinearLayout mOneAdvanceLyt;

    private LinearLayout mTwoAdvanceLyt;
    private LinearLayout mTextAdvanceLyt;
    private EditText mOneWidthEditText;
    private EditText mOneHeithEditText;
    private Spinner mOneBarcodeSpn;
    private EditText mTwoWidthEditText;
    private EditText mTwoHeithEditText;
    private Spinner mTwoBarcodeSpn;
    private CheckBox mBlodCheckBox;
    private Spinner mTwoBarcodeSpn1;
    private Spinner mFontSizeSpn;
    private Spinner mTextAlignSpn;
    private RadioGroup mFunctionRG;

    printer mPrinter = new printer();
    private printer.PrintType mTitleType = printer.PrintType.Centering;
    private int mTitleTextSize = 40;
    int m1dBardCodeWidth = 40;
    int m1dBardCodeHeight = 20;
    int m2dBardCodeWidth = 40;
    int m2dBardCodeHeight = 20;
    private boolean titleBold;
    private int mLabelHeight = 50;
    private String mOneBarcodeType = "128";
    private String mTwoBarcodeType = "QR";
    private Button mCreateBtn;
    private CardView mCardView;
    private TextView mNoticeTwoView;
    private TextView mNoticeOneView;
    private Bitmap mOneBitmap;
    private Bitmap mTwoBitmap;
    private int printCount = 1;
    private Button mNextPageBtn;
    private CheckBox mPaperCheckBox;
    boolean isFirstScan = true;
    private String defaultCode;

    public ScanPrintFragment(){
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.activity_scan_print, container, false);
        mCardView = (CardView)mView.findViewById(R.id.cv_card);
        mOneBarCodeImageView = (ImageView)mView.findViewById(R.id.img_one_show);
        mTwoBarCodeImageView = (ImageView)mView.findViewById(R.id.img_two_show);
        mNoticeOneView = (TextView)mView.findViewById(R.id.tv_one_tips);
        mNoticeTwoView = (TextView)mView.findViewById(R.id.tv_two_tips);
        mTextView = (TextView)mView.findViewById(R.id.text_show);

        mContentEditText = (EditText)mView.findViewById(R.id.edt_content);
        mCreateBtn = (Button) mView.findViewById(R.id.btn_create);
        mClearBtn = (Button) mView.findViewById(R.id.btn_clear);

        mFunctionRG = (RadioGroup)mView.findViewById(R.id.rg_func);

        mAdvanceCheckBox = (CheckBox)mView.findViewById(R.id.cb_setting);
        mPaperCheckBox = (CheckBox)mView.findViewById(R.id.cb_paper);


        mAdvanceLayout = (LinearLayout)mView.findViewById(R.id.lyt_advance);
        mAdvanceCommonLyt = (LinearLayout)mView.findViewById(R.id.lyt_advance_common);
        mLabelHeightSpn = (Spinner)mView.findViewById(R.id.spinner_label_height);
        mLabelGaySpn = (Spinner)mView.findViewById(R.id.spinner_label_gray);
        mLabelPagesEdt = (EditText)mView.findViewById(R.id.edt_pages);

        mOneAdvanceLyt = (LinearLayout)mView.findViewById(R.id.lyt_one_advance);
        mOneWidthEditText = (EditText)mView.findViewById(R.id.edt_one_width);
        mOneHeithEditText = (EditText)mView.findViewById(R.id.edt_one_height);
        mOneBarcodeSpn = (Spinner)mView.findViewById(R.id.spinner_one_barcode);

        mTwoAdvanceLyt = (LinearLayout)mView.findViewById(R.id.lyt_two_advance);
        mTwoWidthEditText = (EditText)mView.findViewById(R.id.edt_two_width);
        mTwoHeithEditText = (EditText)mView.findViewById(R.id.edt_two_height);
        mTwoBarcodeSpn = (Spinner)mView.findViewById(R.id.spinner_two_barcode);

        mTextAdvanceLyt = (LinearLayout)mView.findViewById(R.id.lyt_text_advance);
        mBlodCheckBox = (CheckBox)mView.findViewById(R.id.cb_text_blod);
        mFontSizeSpn = (Spinner)mView.findViewById(R.id.spinner_fontsize);
        mTextAlignSpn = (Spinner)mView.findViewById(R.id.spinner_text_align);

        mFeedBtn = (Button)mView.findViewById(R.id.btn_feed);
        mPrintBtn = (Button)mView.findViewById(R.id.btn_print);
        mNextPageBtn = (Button)mView.findViewById(R.id.btn_next_page);

        return mView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mFunctionRG.setOnCheckedChangeListener(this);

        mAdvanceCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    mAdvanceLayout.setVisibility(View.VISIBLE);
                    mAdvanceCommonLyt.setVisibility(View.VISIBLE);
                } else {
                    mAdvanceLayout.setVisibility(View.GONE);
                    mAdvanceCommonLyt.setVisibility(View.GONE);
                }
            }
        });
        String [] strAct={"0","1","2","3","4","5","6","7","8","9"};
        ArrayAdapter mAdapter=new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item,strAct);
        mAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        mLabelGaySpn.setAdapter(mAdapter);
        mPrinter.SetGrayLevel((byte)0x05);
        mLabelGaySpn.setSelection(5);
        mLabelGaySpn.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                byte btGrayLevel=(byte)mLabelGaySpn.getSelectedItemPosition();
                mPrinter.SetGrayLevel(btGrayLevel);
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });

        String[] alignArray = {"Center","Left","Right"};
        ArrayAdapter mAlignAdapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item,alignArray);
        mAlignAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        mTextAlignSpn.setAdapter(mAlignAdapter);
        mTextAlignSpn.setSelection(0);
        mTextAlignSpn.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                int position = mTextAlignSpn.getSelectedItemPosition();
                if (position == 0) {
                    mTitleType = printer.PrintType.Centering;
                } else if (position == 1) {
                    mTitleType = printer.PrintType.Left;
                } else if (position == 2) {
                    mTitleType = printer.PrintType.Right;
                }
                updateTextAlign();
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });

        String[] sizeArray = {"18","20","22","24","40"};
        ArrayAdapter mSizeAdapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item,sizeArray);
        mSizeAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        mFontSizeSpn.setAdapter(mSizeAdapter);
        mFontSizeSpn.setSelection(4);
        mFontSizeSpn.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,int arg2, long arg3) {
                int titleSize = Integer.valueOf((String)(mFontSizeSpn.getSelectedItem()));
                mTitleTextSize = titleSize;
                mTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX,mTitleTextSize);

            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });

        final String[] labelHeightArray = {"50","60"};
        ArrayAdapter mLabelHeightAdapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item,labelHeightArray);
        mLabelHeightAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        mLabelHeightSpn.setAdapter(mLabelHeightAdapter);
        mLabelHeightSpn.setSelection(0);
        mLabelHeightSpn.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,int arg2, long arg3) {
                mLabelHeight = Integer.valueOf((String)(mLabelHeightSpn.getSelectedItem()));
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)mCardView.getLayoutParams();
                layoutParams.height = mLabelHeight * 8;
                mCardView.setLayoutParams(layoutParams);
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });


        final String[] oneBarcodeArray = {"128","EAN13"};
        ArrayAdapter mOneBarcodeAdapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item,oneBarcodeArray);
        mOneBarcodeAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        mOneBarcodeSpn.setAdapter(mOneBarcodeAdapter);
        mOneBarcodeSpn.setSelection(0);
        mOneBarcodeSpn.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,int arg2, long arg3) {
                int position = mOneBarcodeSpn.getSelectedItemPosition();
                mOneBarcodeType = oneBarcodeArray[position];
                refreshBy1dBarcode();

            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });

        final String[] twoBarcodeArray = {"QR","PDF417"};
        ArrayAdapter mTwoBarcodeAdapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item,twoBarcodeArray);
        mTwoBarcodeAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        mTwoBarcodeSpn.setAdapter(mTwoBarcodeAdapter);
        mTwoBarcodeSpn.setSelection(0);
        mTwoBarcodeSpn.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,int arg2, long arg3) {
                int position = mTwoBarcodeSpn.getSelectedItemPosition();
                mTwoBarcodeType = twoBarcodeArray[position];
                refreshBy2dBarcode();

            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });

        mBlodCheckBox.setChecked(true);
        mBlodCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    titleBold = true;
                } else {
                    titleBold = false;
                }
                if (mBlodCheckBox.isChecked()){
                    mTextView.setTypeface(Typeface.DEFAULT_BOLD);
                } else {
                    mTextView.setTypeface(Typeface.DEFAULT);
                }
            }
        });
        defaultCode = "9787122078285";
        mContentEditText.setText(defaultCode);
        mContentEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                android.util.Log.d(TAG," content beforeTextChanged s="+s);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                android.util.Log.d(TAG," content afterTextChanged s="+s);
                String needDelete = defaultCode;
                if (!TextUtils.isEmpty(s)) {
                    String data = s.toString();
                    if ((data.startsWith(needDelete) || data.endsWith(needDelete)) && isFirstScan) {
                        s.replace(0,defaultCode.length(),"");
                        isFirstScan = false;
                    }
                }
                generateBarcode();
            }
        });

        refreshBy1dBarcode();
        mCreateBtn.setOnClickListener(this);
        mClearBtn.setOnClickListener(this);
        mOneWidthEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s != null && !s.equals("")){
                    int markVal = 0;
                    try{
                        markVal = Integer.parseInt(s.toString());
                    }catch (NumberFormatException e){
                        markVal = 0;
                    }
                    if (markVal > 50){
                        Toast.makeText(getActivity(), getString(R.string.size_invalid_tips), Toast.LENGTH_SHORT).show();
                        mOneWidthEditText.setText(String.valueOf(50));
                    }
                    return;
                }
            }
        });
        mOneHeithEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s != null && !s.equals(""))
                {
                    int markVal = 0;
                    try{
                        markVal = Integer.parseInt(s.toString());
                    }catch (NumberFormatException e){
                        markVal = 0;
                    }
                    if (markVal > 50){
                        Toast.makeText(getActivity(), getString(R.string.size_invalid_tips), Toast.LENGTH_SHORT).show();
                        mOneHeithEditText.setText(String.valueOf(50));
                    }
                    return;
                }
            }
        });
        mTwoWidthEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s != null && !s.equals(""))
                {
                    int markVal = 0;
                    try{
                        markVal = Integer.parseInt(s.toString());
                    }catch (NumberFormatException e){
                        markVal = 0;
                    }
                    if (markVal > 50){
                        Toast.makeText(getActivity(),getString(R.string.size_invalid_tips), Toast.LENGTH_SHORT).show();
                        mTwoWidthEditText.setText(String.valueOf(50));
                    }
                    return;
                }
            }
        });
        mTwoHeithEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s != null && !s.equals(""))
                {
                    int markVal = 0;
                    try{
                        markVal = Integer.parseInt(s.toString());
                    }catch (NumberFormatException e){
                        markVal = 0;
                    }
                    if (markVal > 50){
                        Toast.makeText(getActivity(), getString(R.string.size_invalid_tips), Toast.LENGTH_SHORT).show();
                        mTwoHeithEditText.setText(String.valueOf(50));
                    }
                    return;
                }
            }
        });

        try {
            mPrinter.Open();
        }catch (Exception e){
            e.printStackTrace();
        }
        mPrintBtn.setOnClickListener(this);
        mFeedBtn.setOnClickListener(this);
        mNextPageBtn.setOnClickListener(this);
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId){
            case R.id.rb_one:
                mOneBarCodeImageView.setVisibility(View.VISIBLE);
                mTwoBarCodeImageView.setVisibility(View.GONE);
                mTextView.setVisibility(View.GONE);
                mNoticeOneView.setVisibility(View.VISIBLE);
                mNoticeTwoView.setVisibility(View.GONE);
                mOneAdvanceLyt.setVisibility(View.VISIBLE);
                mTwoAdvanceLyt.setVisibility(View.GONE);
                mTextAdvanceLyt.setVisibility(View.GONE);
                refreshBy1dBarcode();
                break;
            case R.id.rb_two:
                mOneBarCodeImageView.setVisibility(View.GONE);
                mTwoBarCodeImageView.setVisibility(View.VISIBLE);
                mTextView.setVisibility(View.GONE);
                mNoticeOneView.setVisibility(View.GONE);
                mNoticeTwoView.setVisibility(View.VISIBLE);
                mTwoAdvanceLyt.setVisibility(View.VISIBLE);
                mOneAdvanceLyt.setVisibility(View.GONE);
                mTextAdvanceLyt.setVisibility(View.GONE);
                refreshBy2dBarcode();
                break;
            case R.id.rb_text:
                mOneBarCodeImageView.setVisibility(View.GONE);
                mTwoBarCodeImageView.setVisibility(View.GONE);
                mTextView.setVisibility(View.VISIBLE);
                mNoticeOneView.setVisibility(View.GONE);
                mNoticeTwoView.setVisibility(View.GONE);
                mTextAdvanceLyt.setVisibility(View.VISIBLE);
                mOneAdvanceLyt.setVisibility(View.GONE);
                mTwoAdvanceLyt.setVisibility(View.GONE);
                refreshByText();
                break;
        }
    }

    private Bitmap create1dBarcode(String type,String content,int width,int height){
        Bitmap bitmap = null;
        BarcodeFormat barcodeFormat = null;
        if ("128".equals(type)){
            if (content.length() <= 128) {
                barcodeFormat = BarcodeFormat.CODE_128;
            }
        } else if ("EAN13".equals(type)){
            if (content.length() == 13) {
                barcodeFormat = BarcodeFormat.EAN_13;
            }
        }
        if (barcodeFormat == null){
            return bitmap;
        }
        try {
            android.util.Log.d(TAG,"content="+content);
            bitmap = BarcodeUtil.create1dBarcode(content,barcodeFormat,width,height);
        } catch (WriterException e) {
            e.printStackTrace();
        } catch (Exception e){
            e.printStackTrace();
        }
        return bitmap;
    }
    private Bitmap create2dBarcode(String type,String content,int width,int height){
        Bitmap bitmap = null;
        BarcodeFormat barcodeFormat = null;
        if ("QR".equals(type)){
            barcodeFormat = BarcodeFormat.QR_CODE;
            try {
                bitmap = BarcodeUtil.create2dBarcode(content,barcodeFormat,width,width);
            } catch (WriterException e) {
                e.printStackTrace();
            } catch (Exception e){
                e.printStackTrace();
            }
        } else if ("PDF417".equals(type)){
            barcodeFormat = BarcodeFormat.PDF_417;
            try {
                bitmap = BarcodeUtil.create1dBarcode(content,barcodeFormat,width,height);
            } catch (WriterException e) {
                e.printStackTrace();
            } catch (Exception e){
                e.printStackTrace();
            }
        }
        if (barcodeFormat == null){
            return bitmap;
        }

        return bitmap;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_create:
                if (mOneAdvanceLyt.getVisibility() == View.VISIBLE) {
                    Editable widthEditable = mOneWidthEditText.getEditableText();
                    if (widthEditable != null && !TextUtils.isEmpty(widthEditable.toString())){
                        m1dBardCodeWidth = Integer.valueOf(widthEditable.toString());
                    }
                    Editable heightEditable = mOneHeithEditText.getEditableText();
                    if (heightEditable != null && !TextUtils.isEmpty(heightEditable.toString())){
                        m1dBardCodeHeight = Integer.valueOf(heightEditable.toString());
                    }
                } else if (mTwoAdvanceLyt.getVisibility() == View.VISIBLE) {
                    Editable widthEditable = mTwoWidthEditText.getEditableText();
                    if (widthEditable != null && !TextUtils.isEmpty(widthEditable.toString())){
                        m2dBardCodeWidth = Integer.valueOf(widthEditable.toString());
                    }
                    Editable heightEditable = mTwoHeithEditText.getEditableText();
                    if (heightEditable != null && !TextUtils.isEmpty(heightEditable.toString())){
                        m2dBardCodeHeight = Integer.valueOf(heightEditable.toString());
                    }
                } else if (mTextAdvanceLyt.getVisibility() == View.VISIBLE) {
                    refreshByText();;
                }
                break;
            case R.id.btn_clear:
                mContentEditText.setText("");
                break;
            case R.id.btn_print:
                if (!mPrinter.IsOutOfPaper() && !mPrinter.IsOverTemperature()){
                    if (mOneAdvanceLyt.getVisibility() == View.VISIBLE) {
                        printOneBarcode();
                    } else if (mTwoAdvanceLyt.getVisibility() == View.VISIBLE) {
                        printTwoBarcode();
                    } else if (mTextAdvanceLyt.getVisibility() == View.VISIBLE) {
                        printText();
                    }
                }
                break;
            case R.id.btn_feed:
                int result = mPrinter.Step((byte)0x5f);
                break;
            case R.id.btn_next_page:
                mPrinter.GoToNextPage();
                break;
        }
    }

    private void printText() {

        Editable editable = mContentEditText.getEditableText();
        if (editable != null && !TextUtils.isEmpty(editable.toString())){
            boolean bold = false;
            if (mBlodCheckBox.isChecked()){
                bold = true;
            } else {
                bold = false;
            }
            int count = 1;
            Editable countEditable = mLabelPagesEdt.getEditableText();
            if (countEditable != null && !TextUtils.isEmpty(countEditable.toString())){
                count = Integer.valueOf(countEditable.toString());
            }
            if (count > 9) {
                count = 9;
            }
            for (int i=0; i<count; i++){
                if (mPaperCheckBox.isChecked()) {
                    mPrinter.GoToNextPage();
                }
                mPrinter.PrintLineInit(mTitleTextSize);
//                mPrinter.PrintLineStringByType(editable.toString(), mTitleTextSize, mTitleType, bold);
                mPrinter.PrintStringEx(editable.toString(),mTitleTextSize,false,bold,mTitleType);
                mPrinter.PrintLineEnd();
            }

        }
    }

    private void printTwoBarcode() {
        try{
//            Bitmap bitmap = Bitmap.createBitmap(50*8, mLabelHeight*8,Bitmap.Config.ARGB_8888);
//            Canvas canvas = new Canvas(bitmap);
//            canvas.drawBitmap(mTwoBitmap,(bitmap.getWidth()-mTwoBitmap.getWidth())/2,(bitmap.getHeight()-mTwoBitmap.getHeight())/2,new Paint());

            int count = 1;
            Editable countEditable = mLabelPagesEdt.getEditableText();
            if (countEditable != null && !TextUtils.isEmpty(countEditable.toString())){
                count = Integer.valueOf(countEditable.toString());
            }
            if (count > 9) {
                count = 9;
            }
            for (int i=0; i<count; i++){
                if (mPaperCheckBox.isChecked()) {
                    mPrinter.GoToNextPage();
                }
                mPrinter.PrintBitmapAtCenter(mTwoBitmap,mLabelHeight*8);
//                Utils.stroageBitmap(bitmap);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private void printOneBarcode() {
        try{
//            Bitmap bitmap = Bitmap.createBitmap(50*8, mLabelHeight*8,Bitmap.Config.ARGB_8888);
//            Canvas canvas = new Canvas(bitmap);
//            canvas.drawBitmap(mOneBitmap,(bitmap.getWidth()-mOneBitmap.getWidth())/2,(bitmap.getHeight()-mOneBitmap.getHeight())/2,new Paint());

            int count = 1;
            Editable countEditable = mLabelPagesEdt.getEditableText();
            if (countEditable != null && !TextUtils.isEmpty(countEditable.toString())){
                count = Integer.valueOf(countEditable.toString());
            }
            if (count > 9) {
                count = 9;
            }
            for (int i=0; i<count; i++){
                if (mPaperCheckBox.isChecked()) {
                    mPrinter.GoToNextPage();
                }
                mPrinter.PrintBitmapAtCenter(mOneBitmap,mLabelHeight*8);
//                Utils.stroageBitmap(bitmap);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    private void generateBarcode(){
        if (mOneAdvanceLyt.getVisibility() == View.VISIBLE) {
            refreshBy1dBarcode();
        } else if (mTwoAdvanceLyt.getVisibility() == View.VISIBLE) {
            refreshBy2dBarcode();
        } else if (mTextAdvanceLyt.getVisibility() == View.VISIBLE) {
            refreshByText();;
        }
    }


    private void refreshBy1dBarcode(){
        mOneWidthEditText.setText(m1dBardCodeWidth+"");
        mOneHeithEditText.setText(m1dBardCodeHeight+"");
        Editable editable = mContentEditText.getEditableText();
        if (editable != null && !TextUtils.isEmpty(editable.toString())){
            String content = editable.toString().trim();
            Pattern pattern = Pattern.compile("[\u4e00-\u9fa5]");
            Matcher matcher = pattern.matcher(content);
            if(matcher.find()){
                Toast.makeText(getActivity(),getString(R.string.zh_create_1d_tips), Toast.LENGTH_SHORT).show();
                return;
            }

            mOneBitmap = create1dBarcode(mOneBarcodeType,content,m1dBardCodeWidth*8,m1dBardCodeHeight*8);
            if (mOneBitmap != null){
                mOneBarCodeImageView.setVisibility(View.VISIBLE);
                mOneBarCodeImageView.setImageBitmap(mOneBitmap);
            } else {
                mOneBarCodeImageView.setVisibility(View.INVISIBLE);
            }
        } else {
            mOneBarCodeImageView.setVisibility(View.INVISIBLE);
        }
    }
    private void refreshBy2dBarcode(){
        mTwoWidthEditText.setText(m2dBardCodeWidth+"");
        mTwoHeithEditText.setText(m2dBardCodeHeight+"");
        Editable editable = mContentEditText.getEditableText();
        if (editable != null && !TextUtils.isEmpty(editable.toString())){
            String content = editable.toString().trim();

            mTwoBitmap = create2dBarcode(mTwoBarcodeType,content,m2dBardCodeWidth*8,m2dBardCodeHeight*8);
            if (mTwoBitmap != null){
                mTwoBarCodeImageView.setVisibility(View.VISIBLE);
                mTwoBarCodeImageView.setImageBitmap(mTwoBitmap);
            } else {
                mTwoBarCodeImageView.setVisibility(View.INVISIBLE);
            }
        } else {
            mTwoBarCodeImageView.setVisibility(View.INVISIBLE);
        }
    }

    private void refreshByText(){
        Editable editable = mContentEditText.getEditableText();
        if (editable != null && !TextUtils.isEmpty(editable.toString())){
            mTextView.setText(editable.toString().trim());
            mTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX,mTitleTextSize);
            if (mBlodCheckBox.isChecked()){
                mTextView.setTypeface(Typeface.DEFAULT_BOLD);
            } else {
                mTextView.setTypeface(Typeface.DEFAULT);
            }
            updateTextAlign();
            mTextView.postInvalidate();
        }
    }
    private void updateTextAlign(){
        if (mTitleType == printer.PrintType.Centering) {
            mTextView.setGravity(Gravity.CENTER);
        } else if (mTitleType == printer.PrintType.Left) {
            mTextView.setGravity(Gravity.LEFT);
        } else if (mTitleType == printer.PrintType.Right) {
            mTextView.setGravity(Gravity.RIGHT);
        }
    }
}
