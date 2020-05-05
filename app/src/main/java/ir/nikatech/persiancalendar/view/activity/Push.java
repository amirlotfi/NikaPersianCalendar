package ir.nikatech.persiancalendar.view.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

public class Push extends Activity {

    private String link, mode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        mode = getIntent().getStringExtra("mode");

        if (mode.equals("1")) {
            link = getIntent().getStringExtra("link");
            startActivity(new Intent("android.intent.action.VIEW", Uri.parse(link)));
            finish();
        }
    }
}
