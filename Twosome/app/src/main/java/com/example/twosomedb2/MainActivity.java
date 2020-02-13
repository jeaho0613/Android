package com.example.twosomedb2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import static com.example.twosomedb2.R.id.Lay_filed;
import static com.example.twosomedb2.R.id.tab1;
import static com.example.twosomedb2.R.id.ed_changeEdit;
import static com.example.twosomedb2.R.id.tab2;
import static com.example.twosomedb2.R.id.tx_namber1;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    static int counter = 0;

    // floting bar 변수들
    private Context mContext;
    private FloatingActionButton fab_main, fab_sub1, fab_sub2;
    private Animation fab_open, fab_close;
    private boolean isFabOpen = false;

    // 수량 변경 변수들
    TextView txNamber1;
    EditText changeEdit;
    TextView Number;

    // 대화상자 변수들
    View dialogView, addlistView, toastView;

    // 새로고침 로직
    TextView all_count, butReserve;
    Integer[] all_Id = new Integer[22];
    TextView[] all_Text = new TextView[22];
    String[] all_str = new String[22];
    static int addCount;
    LinearLayout Layfiled;

    // 예약 List 변수들
    EditText editDate, editServername, editNumber, editUniq;
    TextView txDate, txServername, txNumber, txName, txUniq;
    AutoCompleteTextView autoText;

    // 자동완성 변수
    String[] arWords = new String[]{
            "모어 댄 쿠키 앤 크림", "스트로베리 초콜릿 생크림", "딸기 생크림", "로즈베리 생크림"
            , "벨지안 멜팅 가나슈", "퀸즈 캐롯", "그뤼에르 치즈 무스", "티라미슈", "뉴욕 치즈 케익"
            , "비 마이 스트로 베리", "마스카포네 생크림", "화이트 포레스트", "레드 벨벳", "더치 솔티드 카라멜"
            , "블랙 벨벳", "트리플 초콜릿 무스", "블랙 포레스트", "클래식 가토", "바닐라 크램브륄레", "헤이즐넛 마스카포네 치즈"
            , "카푸치노 생크림"
    };

    // 파이어 베이스 변수
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = firebaseDatabase.getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        /*----------------------------새로 고침 관련 로직-------------------------------------------*/
        Layfiled = (LinearLayout) findViewById(Lay_filed);
        all_count = findViewById(R.id.tx_allCount);
        butReserve = findViewById(R.id.but_reserve);

        TimerTask tt = new TimerTask() {
            @Override
            public void run() {
                for (int i = 1; i < all_Id.length; i++) {
                    all_Id[i] = getResources().getIdentifier("tx_namber" + i, "id", getPackageName());
                    all_Text[i] = findViewById(all_Id[i]);
                    all_str[i] = all_Text[i].getText().toString();
                }
                for (int i = 1; i < all_str.length; i++) {

                    addCount += Integer.parseInt(all_str[i]);
                    /*System.out.println(i + "/" + all_str[i]);*/
                }
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                all_count.setText(Integer.toString(addCount));
                                butReserve.setText(String.valueOf(Layfiled.getChildCount()));

                                addCount = 0;
                            }
                        });
                    }
                }).start();

                /*System.out.println(addCount);*/

            }
        };

        Timer timer = new Timer();
        timer.schedule(tt, 0, 1000);
        /*----------------------------------------------------------------------------------------*/



        /*----------------------전체 탭 Host 관련 로직-------------------------------------*/
        TabHost tabHost = (TabHost) findViewById(R.id.Tabhost);
        tabHost.setup();

        TabHost.TabSpec tabSpecTab1 = tabHost.newTabSpec("TAB1").setIndicator("홈");
        tabSpecTab1.setContent(tab1);
        tabHost.addTab(tabSpecTab1);

        TabHost.TabSpec tabSpecTab2 = tabHost.newTabSpec("TAB2").setIndicator("제품");
        tabSpecTab2.setContent(tab2);
        tabHost.addTab(tabSpecTab2);

        tabHost.setCurrentTab(0);
        /*----------------------------------------------------------------------------*/



        /*---------------------floting bar 설정 로직-----------------------------------*/
        mContext = getApplicationContext();
        fab_open = AnimationUtils.loadAnimation(mContext, R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(mContext, R.anim.fab_close);

        fab_main = (FloatingActionButton) findViewById(R.id.fab_main);
        fab_sub1 = (FloatingActionButton) findViewById(R.id.fab_sub1);
        fab_sub2 = (FloatingActionButton) findViewById(R.id.fab_sub2);

        fab_main.setOnClickListener(this);
        fab_sub1.setOnClickListener(this);
        fab_sub2.setOnClickListener(this);

        fab_main.setImageResource(R.drawable.add);
        fab_sub1.setImageResource(R.drawable.list);
        fab_sub2.setImageResource(R.drawable.cake);

        /*----------------------------------------------------------------------------*/



        /*--------------------------수량 변경 로직---------------------------------------*/

        txNamber1 = (TextView) findViewById(tx_namber1);

        /*----------------------------------------------------------------------------*/


        /*---------------------미리 생성한 예약 List 로직----------------------------------------*/
        for (int i = 0; i < 2; i++) {
            LinearLayout linearLayout = (LinearLayout) findViewById(R.id.Lay_filed);
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            inflater.inflate(R.layout.add_reservation, linearLayout);
        }
        /*----------------------------------------------------------------------------*/


    }


    // floating button 선택 로직
    @Override
    public void onClick(View v) {

        int id = v.getId();

        switch (id) {
            /*----------------------floting But 로직----------------------------------------------*/
            case R.id.fab_main: // 메인 floating 버튼
                toggleFab();
                break;

            case R.id.fab_sub1: // 맨위에 floating 버튼
                toggleFab();
                addlistView = (View) View.inflate(MainActivity.this, R.layout.add_list, null);
                final AlertDialog.Builder listDlg = new AlertDialog.Builder(MainActivity.this);
                listDlg.setView(addlistView);
                //자동완성 로직
                ArrayAdapter<String> adWord = new ArrayAdapter<String>(MainActivity.this,
                        android.R.layout.simple_dropdown_item_1line, arWords);

                autoText = (AutoCompleteTextView) addlistView.findViewById(R.id.autoEdit);
                autoText.setAdapter(adWord);

                listDlg.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.Lay_filed);
                        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        inflater.inflate(R.layout.add_reservation, linearLayout);

                        editNumber = addlistView.findViewById(R.id.edit_number);
                        editDate = addlistView.findViewById(R.id.edit_date);
                        editServername = addlistView.findViewById(R.id.edit_servername);
                        editUniq = addlistView.findViewById(R.id.edit_uniq);

                        txNumber = linearLayout.findViewById(R.id.tx_number);
                        txServername = linearLayout.findViewById(R.id.tx_servername);
                        txDate = linearLayout.findViewById(R.id.tx_date);
                        txName = linearLayout.findViewById(R.id.tx_name);
                        txUniq = linearLayout.findViewById(R.id.tx_uniq);

                        txNumber.setText(editNumber.getText().toString());
                        txServername.setText(editServername.getText().toString());
                        txDate.setText(editDate.getText().toString());
                        txName.setText(autoText.getText().toString());
                        txUniq.setText(editUniq.getText().toString());

                        databaseReference.child(editDate.getText().toString()).child("예약자").setValue(txServername.getText().toString());
                        databaseReference.child(editDate.getText().toString()).child("상품명").setValue(txName.getText().toString());
                        databaseReference.child(editDate.getText().toString()).child("수량").setValue(editNumber.getText().toString() +"개");
                        databaseReference.child(editDate.getText().toString()).child("날짜").setValue(txDate.getText().toString());

                        Number = findViewById(tx_namber1);
                        Integer numA = Integer.parseInt(Number.getText().toString());
                        Integer numB = Integer.parseInt(editNumber.getText().toString());

                        // 수량 빼주기 로직
                        Integer numC = numA - numB;
                        String addA = String.valueOf(numC);
                        Number.setText(addA);


                        addToast("예약됬습니다...");
                    }
                });
                listDlg.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        subToast("취소했습니다..");
                    }
                });
                listDlg.show();
                break;

            case R.id.fab_sub2: // 아래 floatring 버튼
                /*databaseReference.child("message").push().setValue("2");*/
                toggleFab();
                addToast("floating But을 눌렀습니다.");
                break;
            /*-----------------------------------------------------------------------------------*/

            /*----------------------------수량 클릭 onClick---------------------------------------*/
            case R.id.tx_namber1:
                dialogView = (View) View.inflate(MainActivity.this, R.layout.change_stock, null);
                AlertDialog.Builder dlg1 = new AlertDialog.Builder(MainActivity.this);
                dlg1.setView(dialogView);
                dlg1.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        changeEdit = (EditText) dialogView.findViewById(ed_changeEdit);
                        all_count.setText(Integer.toString(addCount));
                        txNamber1.setText(changeEdit.getText().toString());
                        addToast("수량변경됬습니다..");

                    }
                });
                dlg1.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        subToast("취소했습니다..");
                    }
                });
                dlg1.show();
                break;
            case R.id.tx_namber2:
                Toast.makeText(this, "스트로베리초콜릿생크림 입니다.", Toast.LENGTH_SHORT).show();
                break;
            case R.id.tx_namber3:
                Toast.makeText(this, "딸기생크림 입니다..", Toast.LENGTH_SHORT).show();
                break;
            case R.id.tx_namber4:
                Toast.makeText(this, "로즈베리생크림 입니다..", Toast.LENGTH_SHORT).show();
                break;
            case R.id.tx_namber5:
                Toast.makeText(this, "벨지안 멜팅 가나슈 입니다..", Toast.LENGTH_SHORT).show();
                break;
            case R.id.tx_namber6:
                Toast.makeText(this, "퀸즈캐롯 입니다..", Toast.LENGTH_SHORT).show();
                break;
            case R.id.tx_namber7:
                Toast.makeText(this, "그뤼에르 치즈 무스 입니다.", Toast.LENGTH_SHORT).show();
                break;
            case R.id.tx_namber8:
                Toast.makeText(this, "티라미슈 입니다.", Toast.LENGTH_SHORT).show();
                break;
            case R.id.tx_namber9:
                Toast.makeText(this, "뉴욕 치즈 케익 입니다.", Toast.LENGTH_SHORT).show();
                break;
            case R.id.tx_namber10:
                Toast.makeText(this, "비 마이 스트로베리 입니다.", Toast.LENGTH_SHORT).show();
                break;
            case R.id.tx_namber11:
                Toast.makeText(this, "마스카포네생크림 입니다.", Toast.LENGTH_SHORT).show();
                break;
            case R.id.tx_namber12:
                Toast.makeText(this, "화이트 포레스트 입니다.", Toast.LENGTH_SHORT).show();
                break;
            case R.id.tx_namber13:
                Toast.makeText(this, "레드벨벳 입니다.", Toast.LENGTH_SHORT).show();
                break;
            case R.id.tx_namber14:
                Toast.makeText(this, "더치 솔티드 카라멜 입니다.", Toast.LENGTH_SHORT).show();
                break;
            case R.id.tx_namber15:
                Toast.makeText(this, "블랙 벨벳 입니다.", Toast.LENGTH_SHORT).show();
                break;
            case R.id.tx_namber16:
                Toast.makeText(this, "트리플 초콜릿 무스 입니다.", Toast.LENGTH_SHORT).show();
                break;
            case R.id.tx_namber17:
                Toast.makeText(this, "블랙포레스트 입니다.", Toast.LENGTH_SHORT).show();
                break;
            case R.id.tx_namber18:
                Toast.makeText(this, "클래식가토 입니다.", Toast.LENGTH_SHORT).show();
                break;
            case R.id.tx_namber19:
                Toast.makeText(this, "바닐라크램브륄레 입니다.", Toast.LENGTH_SHORT).show();
                break;
            case R.id.tx_namber20:
                Toast.makeText(this, "헤이즐넛마스카포네치즈 입니다.", Toast.LENGTH_SHORT).show();
                break;
            case R.id.tx_namber21:
                Toast.makeText(this, "카푸치노생크림 입니다.", Toast.LENGTH_SHORT).show();
                break;
            /*----------------------------------------------------------------------------------------*/

            default:
        }
    }


    // floating button 애니메이션 출력정의 부분
    private void toggleFab() {
        if (isFabOpen) {
            fab_sub1.startAnimation(fab_close);
            fab_sub2.startAnimation(fab_close);
            fab_sub1.setClickable(false);
            fab_sub2.setClickable(false);
            isFabOpen = false;
        } else {
            fab_sub1.startAnimation(fab_open);
            fab_sub2.startAnimation(fab_open);
            fab_sub1.setClickable(true);
            fab_sub2.setClickable(true);
            isFabOpen = true;

        }
    }

    private void addToast(String text){
        Toast toast = new Toast(MainActivity.this);
        toastView = View.inflate(MainActivity.this,R.layout.toast_custom,null);
        TextView textToast = toastView.findViewById(R.id.textToast);
        textToast.setText(text);
        toast.setGravity(Gravity.TOP|Gravity.LEFT,100,800);
        toast.setView(toastView);
        toast.show();
    }
    private void subToast(String text){
        Toast toast = new Toast(MainActivity.this);
        toastView = View.inflate(MainActivity.this,R.layout.toast_custom,null);
        TextView textToast = toastView.findViewById(R.id.textToast);
        ImageView imageToast = toastView.findViewById(R.id.imageToast);
        textToast.setText(text);
        imageToast.setImageResource(R.drawable.woringui);
        toast.setGravity(Gravity.TOP|Gravity.LEFT,200,800);
        toastView.setBackgroundColor(Color.BLUE);
        toast.setView(toastView);
        toast.show();
    }

}

