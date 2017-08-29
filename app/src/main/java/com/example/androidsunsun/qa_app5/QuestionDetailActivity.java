package com.example.androidsunsun.qa_app5;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class QuestionDetailActivity extends AppCompatActivity {

    private ListView mListView;
    private Question mQuestion;
    private QuestionDetailListAdapter mAdapter;

    private DatabaseReference mFavoriteRef;
    private boolean favorite = false;
    private DatabaseReference mDatabaseReference;
    private FloatingActionButton mFavoriteFab;

    private String favoriteQid;

    public ChildEventListener mEventListener = new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            HashMap map = (HashMap) dataSnapshot.getValue();

            String answerUid = dataSnapshot.getKey();
            favoriteQid = (String) dataSnapshot.getValue();

            for (Answer answer : mQuestion.getAnswers()) {
                // 同じAnswerUidのものが存在しているときは何もしない
                if (answerUid.equals(answer.getAnswerUid())) {
                    return;
                }
            }
            String body = (String) map.get("body");
            String name = (String) map.get("name");
            String uid = (String) map.get("uid");

            Answer answer = new Answer(body, name, uid, answerUid);
            mQuestion.getAnswers().add(answer);
            mAdapter.notifyDataSetChanged();
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {
        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {
        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String s) {
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_detail);

        // 渡ってきたQuestionのオブジェクトを保持する
        Bundle extras = getIntent().getExtras();
        mQuestion = (Question) extras.get("question");
        setTitle(mQuestion.getTitle());

        // ListViewの準備
        mListView = (ListView) findViewById(R.id.listView);
        mAdapter = new QuestionDetailListAdapter(this, mQuestion);
        mListView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();

        mFavoriteFab = (FloatingActionButton) findViewById(R.id.favoriteFab);
        mFavoriteFab.setImageResource(R.drawable.illust2148);
        mFavoriteFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mFavoriteRef = mDatabaseReference.child(Const.FavoritesPATH).child(mQuestion.getQuestionUid());

                if (favoriteQid != null) {
                    //登録済みの場合
                    mFavoriteRef.removeValue();
                    favorite = false;
                    mFavoriteFab.setImageResource(R.drawable.illust2148);
                } else {
                    //favoriteボタンの切り替え
                    favorite = true;
                    mFavoriteFab.setImageResource(R.drawable.illust2147);

                    //favoritesPATHへの登録
                    DatabaseReference dataBaseReference = FirebaseDatabase.getInstance().getReference();
                    DatabaseReference favoriteRef = dataBaseReference.child(Const.FavoritesPATH).child(mQuestion.getQuestionUid());
                    Map<String, String> data = new HashMap<String, String>();

                    data.put("favoriteQid", mQuestion.getQuestionUid());
                    favoriteRef.push().setValue(data, this);

                    //snackbarの表示
                    Snackbar.make(findViewById(android.R.id.content), "お気に入りに登録しました。", Snackbar.LENGTH_LONG).show();
                }
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // ログイン済みのユーザーを取得する
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                if (user == null) {
                    // ログインしていなければログイン画面に遷移させる
                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(intent);
                } else {
                    // Questionを渡して回答作成画面を起動する
                    Intent intent = new Intent(getApplicationContext(), AnswerSendActivity.class);
                    intent.putExtra("question", mQuestion);
                    startActivity(intent);
                }
            }
        });
    }
}


/*
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class QuestionDetailActivity extends AppCompatActivity {

    private ListView mListView;
    private Question mQuestion;
    private QuestionDetailListAdapter mAdapter;

    private DatabaseReference mAnswerRef;

    private FloatingActionButton mFavoriteButton;

    private DatabaseReference mFavoriteRef;
    public DatabaseReference mDatabaseReference;
    OnCompleteListener<AuthResult> mCreateFavoriteListener;
    private Object FavoritesPATH;
    private View view;
    OnCompleteListener<AuthResult> mLoginListener;


    private HashMap map;
    private String body = (String) map.get("body");
    private String name = (String) map.get("name");
    private String uid = (String) map.get("uid");


    private ChildEventListener mEventListener = new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {

            map = (HashMap) dataSnapshot.getValue();

            String answerUid = dataSnapshot.getKey();

            for (Answer answer : mQuestion.getAnswers()) {
                //同じAnswerUidのものが存在しているときは何もしない
                if (answerUid.equals(answer.getAnswerUid())) {
                    return;
                }
            }

            Answer answer = new Answer(body, name, uid, answerUid);
            mQuestion.getAnswers().add(answer);
            mAdapter.notifyDataSetChanged();
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {
        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {
        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String s) {
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
        }
    };
    private boolean favorite = false;

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {
        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {
        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String s) {
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
        }
    };
    FirebaseAuth mAuth;
    boolean mIsCreateAccount = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_detail);

        // 渡ってきたQuestionのオブジェクトを保持する
        Bundle extras = getIntent().getExtras();
        mQuestion = (Question) extras.get("question");


        setTitle(mQuestion.getTitle());

        // ListViewの準備
        mListView = (ListView) findViewById(R.id.listView);
        mAdapter = new QuestionDetailListAdapter(this, mQuestion);
        mListView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();


        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();


        mFavoriteRef = mDatabaseReference.child(Const.FavoritesPATH).child(user.getUid()).child(mQuestion.getQuestionUid());
        mFavoriteRef.addChildEventListener(mFavoriteEventListener);


        //Buttonの準備
        mFavoriteButton = (FloatingActionButton) findViewById(R.id.favoriteButton);
        if (user == null) {
            //ログインしていないときはお気に入りfabを非表示
            mFavoriteButton.setVisibility(View.GONE);
        } else {
            //ログインしているときはお気に入りfabを表示
            mFavoriteButton.setVisibility(View.VISIBLE);
            if (favorite == true) {
                //お気に入りに登録していないときの画像
                mFavoriteButton.setImageResource(R.drawable.illust2148);
            } else {
                //お気に入りに登録しているときの画像
                mFavoriteButton.setImageResource(R.drawable.illust2147);

            }
        }


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // ログイン済みのユーザーを取得する
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                // ログインしていなければログイン画面に遷移させる
                if (user == null) {
                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(intent);
                }
            }
        });
        FloatingActionButton favoriteButton = (FloatingActionButton) findViewById(R.id.favoriteButton);
        favoriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (favorite == true) {
                    //登録済みの場合
                    mFavoriteRef.removeValue();
                    favorite = false;
                    mFavoriteButton.setImageResource(R.drawable.illust2148);
                } else {
                    //favoriteの登録
                    Map<String, String> data = new HashMap<String, String>();
                    data.put("favoriteQid", mQuestion.getQuestionUid());
                    mFavoriteRef.setValue(data);
                    Snackbar.make(findViewById(android.R.id.content), "お気に入りに登録しました。", Snackbar.LENGTH_LONG).show();
                    favorite = true;
                    mFavoriteButton.setImageResource(R.drawable.illust2147);
                }
            }
        });
        DatabaseReference dataBaseReference = FirebaseDatabase.getInstance().getReference();
        mAnswerRef = dataBaseReference.child(Const.ContentsPATH).child(String.valueOf(mQuestion.getGenre())).child(mQuestion.getQuestionUid()).child(Const.AnswersPATH);
        mAnswerRef.addChildEventListener(mEventListener);
    }
}
*/