#include <WiFiClient.h>
#include <WiFiServer.h>
#include <ESP8266WiFi.h>
#include <WiFiUdp.h>
#include "FS.h"
#include <DNSServer.h>

#define sizePacket 255

const char server_private_key[] PROGMEM = R"EOF(
-----BEGIN PRIVATE KEY-----
MIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQCX7UBQ0rZv7GO3
pODJKveS5jlX/Bi1yil99IKAI4IsyO61QTscMFlcL+xZx66ig/7LD+DQoiaokdQX
ie1RKOTk16T+JdT65KTO65C68QjZU/i5I7gdDmCo0dVR0lFzWn+KZ7Cc/ppGmqqD
SQNYe0ujzIpvzvDq1HUTLCNw38qwHHF6NihSZNz1IOXNEck1xcubnQPG+OCZTOke
tntPCkzgq5Z457FQlHza2ZWFiqCovHD1rnAJnTP01RcWjkYqzy8LaIxyHvwzhHFs
+mSIlEYukIIjL6h8Jov6k7tzW2+SQ1dJRDnYgz0m2ePNT+X1sHGx7WxIHmOvlTRB
vuYWGH27AgMBAAECggEAE5ygzM4mltQhGzfBfLZ7Nw9ZH65/UhhCuuuxXPE4gKd/
iQ5enwjuwCO/kudf98KqRXRLRSNpciFBC7jWVb+9rHzZ46TmlWymib+G81riFYvt
c5jUz4tnluNfciyALy/jfu5bsih7qdmVYVUvhWCFJyPkSwKfvvCmHE96U7vP4mPW
pqsF6ePpqcNLuklRTVsRt7bqQHu6Au83rKFl254SQf22nm4NU4csn73z49UIn6UV
MmL9defMDG9KC4dIK6mv0UK1yt8WwcFMuRHizN2UdEiATm0nUPM1xfWg+Y5utxYt
kPbKPAzSaTPC7uxyTaB8WpNHLQYKSpLkrJMNluC2YQKBgQDHoyI7I1fbMy5Uc1uD
ikncleFXNU7c0rsVoSoirLR9lUJchM8FxUbEdH5llSSxGqgg3aAZ2jGZzKgv3o5B
OkPk45EuDFZrTioclkK4OVM1vDDw0+Gzw/FSru7bJlT/wib/7Bw7D4OqnzvgXg4G
s1AhZe64eMo2GthiI33+JF3v7QKBgQDC0dKMFPjOgcRoByGGtWXusjI75hceFPdr
w3odaiy4aoTtFrGNHYFHYY7C8ul+Hqu1a7BqAjv5aJa9AvG+46ha5Bp0IxBLJrZ9
Mcgjc28FAzrA6Tk0kKwfuHouZX5CIoMVqj+AVU/9WNR6Nv4NbxOiofHRspsB3e5Z
Vg+yv8pfRwKBgCkCuyp45ThzeCYDGJ6aDvhQld0LZ2r3o1UbKYO8BMvzmJFW+wxN
bW84GysVC1eSiU1PGe+VojDdGQPUbVa6+G2RziYuhKZAhWgf+g7MP+q/ATLH5ArA
ytdYdlrwse9kXAyg3V/InRy032EFEU6REXn67aJZtfiNZTwr3FsT73Q1AoGAd80p
qWTYH71i9wmzjzALj75a78StYJ1KDSW2+VGqUHmIZnd3Fa0YK7oNXUqdVSYtNzIS
2Q2ordWoJHEkEdvTUplEvzg0s9IJlHFBfrNkiPOUdL62o+F3R0ZdNBzLiIXKkE8S
H6vWtZejicg5cC9nJrJpjydC9OHxyVQH8UbJfs8CgYBqoTrZMtcEzoAmwAHJrxTO
amCFU1mW2h2Q1wpwQUqbbXUUxT3Ozb4HZpmox09iMhYhfvA5hU6u8zA1JY5N3Pb8
Hrl8TjrnDqAuLUntHd5BE/nzD6yknQXEYTuWEZiIrDUEjKzLIFcm33ngLXODwoCc
Bm9JuKuV4zPQDNCVL87sfQ==
-----END PRIVATE KEY-----
)EOF";

const char server_cert[] PROGMEM = R"EOF(
-----BEGIN CERTIFICATE-----
MIIDmDCCAoACCQD5+FAvbo2IKzANBgkqhkiG9w0BAQsFADCBjTELMAkGA1UEBhMC
ZGUxEDAOBgNVBAgMB2JhdmFyaWExETAPBgNVBAcMCHd1cnpidXJnMRUwEwYDVQQK
DAxsaWdodGNvbnRyb2wxGTAXBgNVBAMMEGxpZ2h0Y29udHJvbENlcnQxJzAlBgkq
hkiG9w0BCQEWGGxpZ2h0Y29udHJvbEB0ZXN0Q2VydC5kZTAeFw0xOTAxMjYxOTMx
MjNaFw0zMDA0MTQxOTMxMjNaMIGNMQswCQYDVQQGEwJkZTEQMA4GA1UECAwHYmF2
YXJpYTERMA8GA1UEBwwId3VyemJ1cmcxFTATBgNVBAoMDGxpZ2h0Y29udHJvbDEZ
MBcGA1UEAwwQbGlnaHRjb250cm9sQ2VydDEnMCUGCSqGSIb3DQEJARYYbGlnaHRj
b250cm9sQHRlc3RDZXJ0LmRlMIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKC
AQEAl+1AUNK2b+xjt6TgySr3kuY5V/wYtcopffSCgCOCLMjutUE7HDBZXC/sWceu
ooP+yw/g0KImqJHUF4ntUSjk5Nek/iXU+uSkzuuQuvEI2VP4uSO4HQ5gqNHVUdJR
c1p/imewnP6aRpqqg0kDWHtLo8yKb87w6tR1EywjcN/KsBxxejYoUmTc9SDlzRHJ
NcXLm50DxvjgmUzpHrZ7TwpM4KuWeOexUJR82tmVhYqgqLxw9a5wCZ0z9NUXFo5G
Ks8vC2iMch78M4RxbPpkiJRGLpCCIy+ofCaL+pO7c1tvkkNXSUQ52IM9JtnjzU/l
9bBxse1sSB5jr5U0Qb7mFhh9uwIDAQABMA0GCSqGSIb3DQEBCwUAA4IBAQB3HZON
527Cpzls/iJas4i5zh1NE7g70kahSzTTETn10bCiFutvBEFJqCXkNIvArqBd0pmo
gxv/xEd3PRUfQl1pG8E0AuIO2Dq8Rrgoeb9Hn9YR2lCRw8QI5abriWl6nTmwPVP3
c74VUS6DTpkHrj+LE0P5CP16OKSio/NgGXuqu6ZFitt2AH8Vo7sUCnMWsVTAUx0B
u6bCSK/qkjQJSjvo9cK+w6DUBFGxP/F8HPUG4GgXliVCkBsGYvjIOPVLrCOSTHRY
97gB8mm3UQ6/HtAwB+XkPgiekRFOyYdbBrw9bXXuzS+JSMYJFDn3F3uRJHMSR2oE
fKX4nP557Cl38i7T
-----END CERTIFICATE-----
)EOF";

const String accDir = "/accFile.txt";
const int timeout = 5000;

unsigned int localTcpPort = 1234;
unsigned int localUdpPort = 4210;
const byte DNS_PORT = 53;
WiFiUDP Udp;
WiFiServer Tcp(localTcpPort);
DNSServer dnsServer;
BearSSL::WiFiServerSecure sslServer(443);

IPAddress local_IP(192,168,4,22);
IPAddress gateway(192,168,4,22);
IPAddress subnet(255,255,255,0);

String connectionMode;

void setup()
{
  delay(100);

  Serial.begin(115200);
  Serial.println();
  WiFi.softAPdisconnect();
  WiFi.disconnect();

  SPIFFS.begin();
  
  if (!SPIFFS.exists(accDir)) {
    Serial.println("No SPIFF initialized. Formatting...");
    SPIFFS.format();
    Serial.println("Formatting done.");
    WiFi.mode(WIFI_AP);
    WiFi.softAPConfig(local_IP, local_IP, subnet);
    WiFi.softAP("Lamp");
    dnsServer.start(DNS_PORT, "*", local_IP);
    delay(500);
    
    BearSSL::X509List *serverCertList = new BearSSL::X509List(server_cert);
    BearSSL::PrivateKey *serverPrivKey = new BearSSL::PrivateKey(server_private_key);
    sslServer.setRSACert(serverCertList, serverPrivKey);
    sslServer.begin();
    
    Tcp.begin();
    connectionMode = "SoftAP";
    
  } else {
    Serial.println("File found");
    Serial.println("Connecting, using:");
    File accF = SPIFFS.open(accDir, "r");
    String mySSID = accF.readStringUntil('\n');
    String myPW = accF.readStringUntil('\n');
    mySSID.trim();
    myPW.trim();
    accF.close();
    SPIFFS.end();
    Serial.println(mySSID);
    Serial.println(myPW);
    Serial.println("Connecting to: " + mySSID);
    int timeCounter = 0;
    WiFi.begin(mySSID, myPW);
    while (WiFi.status() != WL_CONNECTED)
    {
      if (timeCounter > timeout) {
        Serial.println("Could not connect to the network.");
        Serial.println("Resetting ESP");
        SPIFFS.format();
        ESP.reset();
      }
      delay(500);
      Serial.print(".");
      timeCounter += 500;
    }
    Serial.println(" connected");

    BearSSL::X509List *serverCertList = new BearSSL::X509List(server_cert);
    BearSSL::PrivateKey *serverPrivKey = new BearSSL::PrivateKey(server_private_key);
    sslServer.setRSACert(serverCertList, serverPrivKey);
    sslServer.begin();
    sslServer.setNoDelay(false);
    
    Tcp.begin();
    Tcp.setNoDelay(true);
    Udp.begin(localUdpPort);
    connectionMode = "Station";
    Serial.printf("Now listening at IP %s, UDP port %d\n", WiFi.localIP().toString().c_str(), localUdpPort);
  }
  Serial.println("Free HEAP: " + String(ESP.getFreeHeap()));
}

String ssidMsg = "";
String pwMsg = "";
void handleTcpClients(bool secure) {
  BearSSL::WiFiClientSecure sslClient = sslServer.available();
  WiFiClient client = Tcp.available();

  if (secure ? sslClient : client) {
    while (secure ? sslClient.connected() : client.connected()) {
      String incommingMsg = "";
      if (secure ? sslClient.available() > 0 : client.available() > 0) {
        String incommingMsg = secure ? sslClient.readStringUntil('\n') : client.readStringUntil('\n');
        Serial.print("Raw message: ");
        Serial.println(incommingMsg);
        Serial.println();
        String key = split(incommingMsg, '=', 0);
        String value = split(incommingMsg, '=', 1);
        if (key.equals("SSID")) {
          ssidMsg = value;
          Serial.println("SSID set");
          Serial.println("SSID: " + ssidMsg);
          Serial.println("PW: " + pwMsg);
        } else if (key.equals("PW")) {
          pwMsg = value;
          Serial.println("SSID set");
          Serial.println("SSID: " + ssidMsg);
          Serial.println("PW: " + pwMsg);
        }
        if (!pwMsg.equals("") && !ssidMsg.equals("")) {
          File f = SPIFFS.open(accDir, "w");
          if (!f) {
            Serial.println("File open for writing failed.");
          }
          Serial.println("Free HEAP before file write" + String(ESP.getFreeHeap()));
          Serial.println("SSID: " + ssidMsg);
          Serial.println("PW: " + pwMsg);
          f.println(ssidMsg);
          f.println(pwMsg);
          f.close();
          if (secure) {
            sslClient.println("OK");
          } else {
            client.println("OK");
          }
          if (secure) {
            sslClient.flush();
          } else {
            client.flush();
          }
          delay(100);
          connectionMode = "Reset";
          return;
        }
      }
    }
  }
}

void runSoftApMode() {
  dnsServer.processNextRequest();
  handleTcpClients(false);
  handleTcpClients(true);
}

void runTCPinteractions() {
  WiFiClient client = Tcp.available();
  if (client) {
    while(client.connected()) {
      client.println("LAMPI");
      client.flush();
      delay(10);
    }
  }
  BearSSL::WiFiClientSecure sslClient = sslServer.available();
  if (sslClient) {
    while(sslClient.connected()) {
      sslClient.println("LAMPI");
      sslClient.flush();
      delay(10);
    }
  }
}

void runUDPinteractions() {
  int packetSize = Udp.parsePacket();
  if (packetSize)
  {
    char udpMsg[packetSize];
    Udp.read(udpMsg, sizePacket);
    String udpString = String(udpMsg);
    
    String key = split(udpString, '=', 0);
    String value = split(udpString, '=', 1);
    int color = value.toInt();
    if (key.equals("rgb")) {
      int red = color & 255;
      int green = (color>>8) & 255;
      int blue = (color>>16) & 255;
      Serial.printf("Red: %d, Green: %d, Blue: %d \n", red, green, blue);
    }
  }
}

void runStationMode() {
  runTCPinteractions();
  
  runUDPinteractions();
}

void loop()
{
  if (connectionMode.equals("Reset")) {
    Serial.println("RESTARTING");
    ESP.restart();
  }
  
  else if (connectionMode.equals("SoftAP")) {
    runSoftApMode();
  }

  else if (connectionMode.equals("Station")) {
    runStationMode();
  }
}

String split(String data, char separator, int index)
{
  int found = 0;
  int strIndex[] = {0, -1};
  int maxIndex = data.length()-1;

  for(int i=0; i<=maxIndex && found<=index; i++){
    if(data.charAt(i)==separator || i==maxIndex){
        found++;
        strIndex[0] = strIndex[1]+1;
        strIndex[1] = (i == maxIndex) ? i+1 : i;
    }
  }

  return found>index ? data.substring(strIndex[0], strIndex[1]) : "";
}
