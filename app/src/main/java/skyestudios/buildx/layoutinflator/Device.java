package skyestudios.buildx.layoutinflator;

import android.os.Build;


import static skyestudios.buildx.layoutinflator.Device.Key.BRAND;
import static skyestudios.buildx.layoutinflator.Device.Key.MODEL;
import static skyestudios.buildx.layoutinflator.Device.Key.SDK;
import static skyestudios.buildx.layoutinflator.Device.Matcher.ABOVE;
import static skyestudios.buildx.layoutinflator.Device.Matcher.BELOW;

class Device {

    public enum Key {
        BRAND,
        MODEL,
        SDK
    }

    public enum Matcher {
        EQUAL,
        BELOW,
        ABOVE
    }

    private static String getInfo(Key key) {
        switch(key) {
            case BRAND:
                return Build.MANUFACTURER;
            case MODEL:
                return Build.MODEL;
            case SDK:
                return String.valueOf(Build.VERSION.SDK_INT);
            default:
                return "/";
        }
    }

    public static String getAllInfo() {
        StringBuilder builder = new StringBuilder();
        builder.append(getInfo(BRAND));
        builder.append(" ");
        builder.append(getInfo(MODEL));
        builder.append(" ");
        builder.append(getInfo(SDK));

        return builder.toString();
    }

    public static boolean matches(String key, String value, String matcherString) throws NumberFormatException {
        Matcher matcher;

        try {
            matcher = Matcher.valueOf(matcherString);
        }catch(IllegalArgumentException e) {
            return value.equalsIgnoreCase(getInfo(Key.valueOf(key)));
        }

        int requested = Integer.parseInt(value);
        int info = Integer.parseInt(getInfo(Key.valueOf(key)));

        return (matcher == BELOW && info < requested) || (matcher == ABOVE && info > requested);
    }
}