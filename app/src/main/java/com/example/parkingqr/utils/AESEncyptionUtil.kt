package com.example.parkingqr.utils

import android.util.Base64
import android.util.Log
import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec

object AESEncyptionUtil {

    private const val TAG = "AESEncryption"
    private const val secretKey = "tK5UTui+DPh8lIlBxya5XVsmeDCoUl6vHhdIESMB6sQ="
    private const val salt = "QWlGNHNhMTJTQWZ2bGhpV3U="
    private const val iv = "bVQzNFNhRkQ1Njc4UUFaWA=="

    fun encrypt(strToEncrypt: String) : String? {
        try {
            val secretKey = getSecretKey()

            val ivParameterSpec = IvParameterSpec(Base64.decode(iv, Base64.DEFAULT))
            val cipher = Cipher.getInstance("AES/CBC/PKCS7Padding")
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivParameterSpec)

            return Base64.encodeToString(cipher.doFinal(strToEncrypt.toByteArray(Charsets.UTF_8)), Base64.DEFAULT)
        } catch (e: Exception) {
            Log.e(TAG, "Error while encrypting", e)
        }
        return null
    }

    fun decrypt(strToDecrypt: String) : String? {
        try {
            val secretKey = getSecretKey()

            val ivParameterSpec = IvParameterSpec(Base64.decode(iv, Base64.DEFAULT))
            val cipher = Cipher.getInstance("AES/CBC/PKCS7Padding")
            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivParameterSpec)

            return String(cipher.doFinal(Base64.decode(strToDecrypt, Base64.DEFAULT)))
        } catch (e: Exception) {
            Log.e(TAG, "Error while decrypting", e)
        }
        return null
    }

    private fun getSecretKey(): SecretKeySpec {
        val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1")
        val spec = PBEKeySpec(secretKey.toCharArray(), Base64.decode(salt, Base64.DEFAULT), 10000, 256)
        val tmp = factory.generateSecret(spec)
        return SecretKeySpec(tmp.encoded, "AES")
    }
}