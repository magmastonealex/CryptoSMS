package net.magmastone.cryptosms;

/**
 * Created by Alex on 14-07-21.
 */
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Base64;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Security;
import java.security.cert.X509Certificate;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.KeyAgreement;
import javax.crypto.spec.DHParameterSpec;


public class CryptoClass {
    private SharedPreferences sharedPref;
    private PublicKey pubKey;
    private PrivateKey priv;

    public CryptoClass(Context c){
        sharedPref = PreferenceManager.getDefaultSharedPreferences(c);
        Security.addProvider(new BouncyCastleProvider());
        String pub=sharedPref.getString("publicKey", "xxx");
        if(pub.equals("xxx")){
            try {
                KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA", "BC");
                keyGen.initialize(2048, new SecureRandom());
                KeyPair pair = keyGen.generateKeyPair();
                pubKey = pair.getPublic();
                priv = pair.getPrivate();
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString("publicKey",savePublicKey(pubKey));
                editor.putString("privateKey",savePrivateKey(priv));
                editor.commit();
                System.out.println("Created new private key!");
            } catch (Exception e) {
                System.err.println(e.getLocalizedMessage());
            }
        }else{
            try {
                pubKey = loadPublicKey(pub);
                priv = loadPrivateKey(sharedPref.getString("privateKey", "xxx"));
             }catch (Exception e){
                System.err.println(e.getLocalizedMessage());
            }
        }
    }

    public String getPublicKey(){
        try {
            return  savePublicKey(pubKey);
        }catch (Exception e){
            return null;
        }

    }
    public String decryptMessage(String message){
        try {
            Cipher cipher = Cipher.getInstance("RSA", "BC");
            cipher.init(Cipher.DECRYPT_MODE, priv);
            return new String(cipher.doFinal(Base64.decode(message, Base64.DEFAULT)), "UTF-8");
        }catch (Exception e){
            return null;
        }
    }
    public static String encryptMessage(String pubKey, String message){
        try {
            Cipher cipher = Cipher.getInstance("RSA", "BC");
            PublicKey ourPub = loadPublicKey(pubKey);
            cipher.init(Cipher.ENCRYPT_MODE, ourPub);
            return Base64.encodeToString(cipher.doFinal(message.getBytes("UTF-8")), Base64.DEFAULT);
        }catch (Exception e){
            return e.getLocalizedMessage();
        }
    }

    public static String savePublicKey(PublicKey publ) throws GeneralSecurityException {
        KeyFactory fact = KeyFactory.getInstance("RSA", "BC");
        X509EncodedKeySpec spec = fact.getKeySpec(publ,
                X509EncodedKeySpec.class);
        return Base64.encodeToString(spec.getEncoded(), Base64.DEFAULT);
    }
    public static String savePrivateKey(PrivateKey priv) throws GeneralSecurityException {
        KeyFactory fact = KeyFactory.getInstance("RSA","BC");
        PKCS8EncodedKeySpec spec = fact.getKeySpec(priv,
                PKCS8EncodedKeySpec.class);
        byte[] packed = spec.getEncoded();
        String key64 = Base64.encodeToString(packed, Base64.DEFAULT);

        Arrays.fill(packed, (byte) 0);
        return key64;
    }
    public static PrivateKey loadPrivateKey(String key64) throws GeneralSecurityException {
        byte[] clear = Base64.decode(key64, Base64.DEFAULT);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(clear);
        KeyFactory fact = KeyFactory.getInstance("RSA", "BC");
        PrivateKey priv = fact.generatePrivate(keySpec);
        Arrays.fill(clear, (byte) 0);
        return priv;
    }

    public static PublicKey loadPublicKey(String stored) throws GeneralSecurityException {
        byte[] data = Base64.decode(stored, Base64.DEFAULT);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(data);
        KeyFactory fact = KeyFactory.getInstance("RSA","BC");
        return fact.generatePublic(spec);
    }

}
