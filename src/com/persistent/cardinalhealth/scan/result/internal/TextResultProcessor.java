package com.persistent.cardinalhealth.scan.result.internal;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.google.zxing.Result;
import com.google.zxing.client.result.ParsedResult;
import com.persistent.cardinalhealth.scan.api.CardPresenter;
import com.persistent.cardinalhealth.scan.result.ResultProcessor;

public class TextResultProcessor extends ResultProcessor<ParsedResult> {

    private static final String TAG = TextResultProcessor.class.getSimpleName();

    private static final String SEARCH_URL = "https://www.google.com/search?q=%s";

    public TextResultProcessor(Context context, ParsedResult parsedResult,
            Result result, Uri photoUri) {
        super(context, parsedResult, result, photoUri);
    }

    @Override
    public List<CardPresenter> getCardResults() {
        List<CardPresenter> cardPresenters = new ArrayList<CardPresenter>();

        ParsedResult parsedResult = getParsedResult();
        String codeValue = parsedResult.getDisplayResult();

        CardPresenter cardPresenter = new CardPresenter();
        cardPresenter.setText("Search Web").setFooter(codeValue);

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(String.format(SEARCH_URL, codeValue)));
        cardPresenter.setPendingIntent(createPendingIntent(getContext(), intent));

        cardPresenters.add(cardPresenter);

        return cardPresenters;
    }

}
