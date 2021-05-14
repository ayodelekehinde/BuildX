package skyestudios.buildx.keystorecreator.keystoreviews;

import skyestudios.buildx.keystorecreator.DistinguishedNameValues;

public class Params {
    int requestCode;
    String storePath;
    String storePass;
    String keyName;
    String keyAlgorithm;
    int keySize;
    String keyPass;
    int certValidityYears;
    String certSignatureAlgorithm;
    DistinguishedNameValues distinguishedNameValues = new DistinguishedNameValues();
}
