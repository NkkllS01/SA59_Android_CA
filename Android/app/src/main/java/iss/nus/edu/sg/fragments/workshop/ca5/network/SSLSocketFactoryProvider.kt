package iss.nus.edu.sg.fragments.workshop.ca5.network

import java.security.KeyStore
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.TrustManagerFactory
import javax.net.ssl.X509TrustManager
import android.content.Context
import iss.nus.edu.sg.fragments.workshop.ca5.R
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate

object SSLSocketFactoryProvider {
    fun provide(context: Context): SSLSocketFactory {
        try {
            // 从 res/raw 目录加载自定义证书
            val certificateFactory = CertificateFactory.getInstance("X.509")
            val inputStream = context.resources.openRawResource(R.raw.https) // 使用你的证书文件名
            val certificate = certificateFactory.generateCertificate(inputStream)
            inputStream.close()

            // 将证书加载到 KeyStore
            val keyStore = KeyStore.getInstance(KeyStore.getDefaultType())
            keyStore.load(null, null)
            keyStore.setCertificateEntry("ca", certificate)

            // 创建 TrustManagerFactory 并初始化
            val trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
            trustManagerFactory.init(keyStore)

            // 创建 SSLContext
            val sslContext = SSLContext.getInstance("TLS")
            sslContext.init(null, trustManagerFactory.trustManagers, null)

            return sslContext.socketFactory
        } catch (e: Exception) {
            throw RuntimeException("Failed to create SSLSocketFactory", e)
        }
    }

    fun provideTrustManager(context: Context): X509TrustManager {
        try {
            // 从 res/raw 目录加载自定义证书
            val certificateFactory = CertificateFactory.getInstance("X.509")
            val inputStream = context.resources.openRawResource(R.raw.https) // 使用你的证书文件名
            val certificate = certificateFactory.generateCertificate(inputStream)
            inputStream.close()

            // 将证书加载到 KeyStore
            val keyStore = KeyStore.getInstance(KeyStore.getDefaultType())
            keyStore.load(null, null)
            keyStore.setCertificateEntry("ca", certificate)

            // 创建 TrustManagerFactory 并初始化
            val trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
            trustManagerFactory.init(keyStore)

            return trustManagerFactory.trustManagers
                .filterIsInstance<X509TrustManager>()
                .firstOrNull()
                ?: throw IllegalStateException("No X509TrustManager found")
        } catch (e: Exception) {
            throw RuntimeException("Failed to create TrustManager", e)
        }
    }
}
