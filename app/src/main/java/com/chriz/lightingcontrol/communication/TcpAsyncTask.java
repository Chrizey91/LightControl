package com.chriz.lightingcontrol.communication;

import android.content.Context;
import android.util.Log;

import com.chriz.lightingcontrol.R;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Socket;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

class TcpAsyncTask extends BaseIpAsyncTask {
    private static final String TAG = TcpAsyncTask.class.getSimpleName();
    private static final int TIMEOUT = 5000;
    private boolean mEncrypted;

    TcpAsyncTask(Context context, InetAddress serverIP, int port, boolean encrypted) {
        super(context, serverIP, port);
        this.mEncrypted = encrypted;
    }

    private Socket createEncryptedSocket() {
        try {
            // This all needs to be done as the certificate is custom generated.
            // Hence, it is not obtained by a CA and thus Android would not allow to connect
            // to the receiver as it is untrusted. Change the R.raw.cert to your desired public key
            // of the receiver
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            InputStream caInput = mContext.getResources().openRawResource(R.raw.cert);
            Certificate ca;
            ca = cf.generateCertificate(caInput);
            caInput.close();
            // Create a KeyStore containing our trusted CAs
            String keyStoreType = KeyStore.getDefaultType();
            KeyStore keyStore = KeyStore.getInstance(keyStoreType);
            keyStore.load(null, null);
            keyStore.setCertificateEntry("ca", ca);
            // Create a TrustManager that trusts the CAs in our KeyStore
            String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
            tmf.init(keyStore);
            // Create an SSLContext that uses our TrustManager
            SSLContext context = SSLContext.getInstance("TLS");
            context.init(null, tmf.getTrustManagers(), null);
            return context.getSocketFactory().createSocket(this.mServerIp, this.mPort);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Socket createUnencryptedSocket() {
        try {
            return new Socket(this.mServerIp, this.mPort);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void run() {
        try {
            Log.d(TAG, "Connecting to: " + mServerIp);
            Socket tcpSocket = this.mEncrypted ? createEncryptedSocket() : createUnencryptedSocket();

            if (tcpSocket == null) {
                Log.e(TAG, "Could not create socket.");
                return;
            }

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(tcpSocket.getInputStream()));
            DataOutputStream outToClient = new DataOutputStream(tcpSocket.getOutputStream());
            // For each DataPacket in the stack that was added we act accordingly
            for (DataPackage dataPackage : this.mDataPackages) {
                // If we expect to receive a packet...
                if (dataPackage instanceof DataPackageReceiver) {
                    DataPackageReceiver dataPackageReceiver = (DataPackageReceiver) dataPackage;
                    String line = bufferedReader.readLine();
                    long time = System.currentTimeMillis();
                    // We wait TIMEOUT milliseconds for the answer. This has to be done because
                    // due to the possible encryption sending/receiving takes longer
                    while (System.currentTimeMillis() - time < TIMEOUT && (line == null || line.isEmpty())) {
                        Thread.sleep(20);
                        line = bufferedReader.readLine();
                        if (line != null && !line.isEmpty()) {
                            break;
                        }
                    }
                    if (line == null || line.isEmpty()) {
                        Log.e(TAG, "No answer received");
                        tcpSocket.close();
                        return;
                    }
                    Log.e(TAG, "Received answer: " + line);
                    dataPackageReceiver.getAnswerListener().onReceiveAnswer(line);
                // If we want to send a single message...
                } else if (dataPackage instanceof DataPackageSender) {
                    DataPackageSender dataPackageSender = (DataPackageSender) dataPackage;
                    Log.d(TAG, "Data sent: " + dataPackageSender.getData());
                    outToClient.writeBytes(dataPackageSender.getData());
                    outToClient.flush();
                // If we want to continuously send a message...
                } else if (dataPackage instanceof DataPackageSenderContinuously) {
                    DataPackageSenderContinuously dataPackageSenderContinuously = (DataPackageSenderContinuously) dataPackage;
                    while (dataPackageSenderContinuously.isActive()) {
                        outToClient.writeBytes(dataPackageSenderContinuously.getData());
                        Thread.sleep(20);
                    }
                }
            }
            bufferedReader.close();
            outToClient.close();
            tcpSocket.close();
        } catch (IOException | InterruptedException e) {
            Log.e(TAG, "Could not send.");
            e.printStackTrace();
        }
    }
}
