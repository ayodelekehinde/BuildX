package skyestudios.buildx.layoutinflator.style;

import android.view.View;
import org.json.JSONObject;

interface Styler {

    View style(View view, JSONObject attributes) throws Exception;
}