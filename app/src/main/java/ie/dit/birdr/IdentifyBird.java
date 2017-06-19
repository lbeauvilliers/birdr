package ie.dit.birdr;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;

public class IdentifyBird extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WebView webview = new WebView(this);
        setContentView(webview);
        webview.loadUrl("http://www.birdwatchireland.ie/IrelandsBirds/tabid/541/Default.aspx");
        //setContentView(R.layout.activity_identify_bird);
    }
}
