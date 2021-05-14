package {PACKAGE};

import android.app.IntentService;
import android.content.Intent;

import androidx.annotation.Nullable;

public class {CLASS_NAME} extends IntentService {

private static final String SERVICE_NAME = {PACKAGE}.{CLASS_NAME};


  public {CLASS_NAME}() {
    super(SERVICE_NAME);
  }

  @Override
  protected void onHandleIntent(@Nullable Intent intent) {

    //Your Background code here
  }
}
