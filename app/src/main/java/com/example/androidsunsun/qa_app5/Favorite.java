package com.example.androidsunsun.qa_app5;
import java.io.Serializable;

public class Favorite implements Serializable{
    private String mQuestionUid;
    private String mFavoriteUid;

    public Favorite(String  questionUid, String favoriteUid) {
        mQuestionUid = questionUid;
        mFavoriteUid = favoriteUid;
    }
    public String getQuestionUid() {
        return mQuestionUid;
    }
    public String getFavoriteUid() {
        return mFavoriteUid;
    }
}

